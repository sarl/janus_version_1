/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.acl.encoding.xml;

import java.io.UnsupportedEncodingException;

import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.ACLMessageContent;
import org.janusproject.acl.encoding.ACLMessageContentEncodingService;
import org.jdom2.Document;
import org.jdom2.Element;


/**
 * This class encodes an ACLMessageContent in XML or decodes an ACLMessageContent encoded in bytes from XML
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00071/SC00071E.html">FIPA ACL Message Representation in XML Specification</a>
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class XMLACLCodec implements ACLMessageContentEncodingService {
	/**
	 * {@inheritDoc}
	 * 
	 * @see #toXML(ACLMessage)
	 */
	@Override
	public byte[] encode(ACLMessage aMsg) {
		byte[] payload;
		
		try {
			payload = aMsg.toXML().getBytes(aMsg.getEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new byte[0];
		}
		
		return payload;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ACLMessageContent decode(byte[] byteMsg, Object... parameters) {
		ACLMessage.Content content = new ACLMessage.Content();
		
		// get back a JDOM document from the encoded bytes
		Document doc = XMLACLCodecHelper.getXMLDocument(byteMsg, parameters);
		
		// <fipa-message act="..">
		Element root = doc.getRootElement();
		
		// the act attribut of the root contains the performative
		content.setPerformative(XMLACLCodecHelper.decodePerformative(root).ordinal());
		
		// handle collections of agents (receiver, reply-to)
		content.setReceiver(XMLACLCodecHelper.decodeAgents(root.getChildren(XMLACLCodecXMLElement.RECEIVER.getTag())));
		content.setReplyTo(XMLACLCodecHelper.decodeAgents(root.getChildren(XMLACLCodecXMLElement.REPLY_TO.getTag())));
		
		// handle other (easier) param/tags
		Element currentElt;
		for (Object childObj : root.getChildren()) {
			if (childObj instanceof Element) {
				currentElt = (Element) childObj;
				
				if (currentElt.getName().equals(XMLACLCodecXMLElement.SENDER.getTag())) {	
					
					content.setSender(XMLACLCodecHelper.decodeAgent(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.CONTENT.getTag())) {
					
					content.setContent(XMLACLCodecHelper.decodeContent(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.LANGUAGE.getTag())) {
					
					content.setLanguage(XMLACLCodecHelper.decodeStringMsgParam(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.ENCODING.getTag())) {
					
					content.setEncoding(XMLACLCodecHelper.decodeStringMsgParam(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.ONTOLOGY.getTag())) {
					
					content.setOntology(XMLACLCodecHelper.decodeStringMsgParam(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.PROTOCOL.getTag())) {
					
					content.setProtocol(XMLACLCodecHelper.decodeStringMsgParam(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.CONVERSATION_ID.getTag())) {
					
					content.setConversationId(XMLACLCodecHelper.decodeConversationId(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.REPLY_WITH.getTag())) {
					
					content.setReplyWith(XMLACLCodecHelper.decodeStringMsgParam(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.IN_REPLY_TO.getTag())) {
					
					content.setInReplyTo(XMLACLCodecHelper.decodeStringMsgParam(currentElt));
					
				} else if (currentElt.getName().equals(XMLACLCodecXMLElement.REPLY_BY.getTag())) {
					
					content.setReplyBy(XMLACLCodecHelper.decodeReplyByDate(currentElt));
					
				} 
			}
		}
		
		return content;
	}
	
	/**
	 * Compute the XML representation of an ACLMessage
	 * 
	 * @param msg the ACLMessage to builds in xml format
	 * @return the xml representation of the given ACLMessage
	 * 
	 * @see ACLMessage#toXML()
	 */
    public static String toXML(ACLMessage msg) {
        StringBuffer sb = new StringBuffer();
		
        /**
         * builds the main tag
         * ex: <fipa-message act="inform">
         */
        XMLACLCodecHelper.encodeFipaMessageStart(sb, msg.getPerformative());
		
        /**
         * builds the sender tag
         * ex: <sender><agent-identifier><name id=".."/></agent-identifier></sender>
         */
		XMLACLCodecHelper.encodeAgent(sb, XMLACLCodecXMLElement.SENDER.getTag(), msg.getContent().getSender());
		
		 /**
         * builds the receiver tag(s)
         * ex: <receiver><agent-identifier><name id=".."/></agent-identifier></receiver>
         */
		XMLACLCodecHelper.encodeAgents(sb, XMLACLCodecXMLElement.RECEIVER.getTag(), msg.getContent().getReceiver());
		
		/**
         * builds the reply-to tag(s)
         * ex: <reply-to><agent-identifier><name id=".."/></agent-identifier></reply-to>
         */
		XMLACLCodecHelper.encodeAgents(sb, XMLACLCodecXMLElement.REPLY_TO.getTag(), msg.getContent().getReplyTo());
		
		/**
		 * builds the content tag
		 * ex: <content>this is my content</content>
		 */
		XMLACLCodecHelper.encodeBasicElement(sb, XMLACLCodecXMLElement.CONTENT.getTag(), msg.getContent().getContent().toString());
		
		/**
		 * builds the language tag
		 * ex: <language>French</language>
		 */
		XMLACLCodecHelper.encodeBasicElement(sb, XMLACLCodecXMLElement.LANGUAGE.getTag(), msg.getContent().getLanguage());
		
		/**
		 * builds the encoding tag
		 * ex: <encoding>UTF-8</encoding>
		 */
		XMLACLCodecHelper.encodeBasicElement(sb, XMLACLCodecXMLElement.ENCODING.getTag(), msg.getContent().getEncoding());
		
		/**
		 * builds the ontology tag
		 * ex: <ontology>UsefullOntology.owl</ontology>
		 */
		XMLACLCodecHelper.encodeBasicElement(sb, XMLACLCodecXMLElement.ONTOLOGY.getTag(), msg.getContent().getOntology());
		
		/**
		 * builds the protocol tag
		 * ex: <protocol>fipa-query</protocol>
		 */
		XMLACLCodecHelper.encodeBasicElement(sb, XMLACLCodecXMLElement.PROTOCOL.getTag(), msg.getContent().getProtocol());
		
		/**
		 * builds the conversation-id tag
		 * ex: <conversation-id>my-unique-id</conversation-id>
		 */
		XMLACLCodecHelper.encodeBasicElement(sb, XMLACLCodecXMLElement.CONVERSATION_ID.getTag(), XMLACLCodecHelper.ifNotNull(msg.getContent().getConversationId()).toString());		
		
		/**
		 * builds the reply-with tag
		 * ex: <reply-with>..</reply-with>
		 */
		XMLACLCodecHelper.encodeBasicElement(sb, XMLACLCodecXMLElement.REPLY_WITH.getTag(), msg.getContent().getReplyWith());
		
		/**
		 * builds the in-reply-to tag
		 * ex: <in-reply-to>..</in-relpy-to>
		 */
		XMLACLCodecHelper.encodeBasicElement(sb, XMLACLCodecXMLElement.IN_REPLY_TO.getTag(), msg.getContent().getInReplyTo());	
		
		/**
		 * builds the reply-by tag
		 * ex: <reply-by time=".."s/>
		 */
		XMLACLCodecHelper.encodeReplyByDate(sb, XMLACLCodecXMLElement.REPLY_BY.getTag(), msg.getContent().getReplyBy());


		/**
		 * close the main tag
		 * ex: <fipa-message> 
		 */
		XMLACLCodecHelper.encodeFipaMessageEnd(sb);
    		
		return sb.toString();
	}
}
