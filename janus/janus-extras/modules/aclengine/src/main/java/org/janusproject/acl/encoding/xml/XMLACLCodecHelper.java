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

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.Performative;
import org.janusproject.acl.encoding.ACLDateUtil;
import org.janusproject.acl.encoding.PayloadEncoding;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.AddressUtil;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

/**
 * Helper used in XML encoding (encode and decode). Make the XMLACLCodec class simplier and easier to read.
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
public class XMLACLCodecHelper {

	/**
	 * Builds the fipa-message begin tag
	 * ex: <fipa-message act="inform">
	 * 
	 * @param sb string buffer which will contains the tag built
	 * @param performative the Performative of the ACLMessage
	 * 
	 * @see #encodeFipaMessageEnd(StringBuffer)
	 * @see Performative
	 */
	public static void encodeFipaMessageStart(StringBuffer sb, Performative performative) {
		sb.append(Locale.getString(XMLACLCodec.class, "STARTTAGATTR",  //$NON-NLS-1$
				Locale.getString(XMLACLCodec.class, "FIPAMESSAGE"), //$NON-NLS-1$
				Locale.getString(XMLACLCodec.class, "FIPAMESSAGEATTR"), //$NON-NLS-1$
				XMLACLCodecHelper.ifNotNull(performative.getName())
			)
		);
	}
	
	/**
	 * Builds the fipa-message end tag
	 * ex: </fipa-message>
	 * 
	 * @param sb string buffer which will contains the fipa-message end tag
	 * 
	 * @see #encodeFipaMessageStart(StringBuffer, Performative)
	 */
	public static void encodeFipaMessageEnd(StringBuffer sb) {
		sb.append(Locale.getString(XMLACLCodec.class, "ENDTAG", //$NON-NLS-1$
				Locale.getString(XMLACLCodec.class, "FIPAMESSAGE") //$NON-NLS-1$
			)
		);
	}

	/**
	 * Builds a agent tag (sender, receiver, reply-to)
	 * ex: <sender><agent-identifier><name id=".."/></agent-identifier></sender>
	 * 
	 * @param sb string buffer which will contains the built tag
	 * @param tag the name of the tag (sender, receiver, reply-to)
	 * @param agentAddress the concerned agent
	 * 
	 * @see #encodeAgents(StringBuffer, String, Collection)
	 * @see AgentAddress
	 */
	public static void encodeAgent(StringBuffer sb, String tag, AgentAddress agentAddress) {
		sb.append(Locale.getString(XMLACLCodec.class, "STARTTAG",  //$NON-NLS-1$
				tag
			)
		);
		sb.append(Locale.getString(XMLACLCodec.class, "STARTTAG", //$NON-NLS-1$
				Locale.getString(XMLACLCodec.class, "AGENTIDENTIFIER") //$NON-NLS-1$
			)
		);
		sb.append(Locale.getString(XMLACLCodec.class, "SHORTELT",  //$NON-NLS-1$
				Locale.getString(XMLACLCodec.class, "NAME"), //$NON-NLS-1$
				Locale.getString(XMLACLCodec.class, "NAMEATTR"), //$NON-NLS-1$
				ifNotNull(agentAddress.getUUID())
			)
		);
		//TODO Addresses ?
		sb.append(Locale.getString(XMLACLCodec.class, "ENDTAG", //$NON-NLS-1$
				Locale.getString(XMLACLCodec.class, "AGENTIDENTIFIER") //$NON-NLS-1$
			)
		);
		sb.append(Locale.getString(XMLACLCodec.class, "ENDTAG",  //$NON-NLS-1$
				tag
			)
		);
	}
	
	/**
	 * Builds several agent tags for only one type of agent (receiver, reply-to)
	 * ex: <receiver><agent-identifier><name id=".."/></agent-identifier></receiver><receiver><agent-identifier><name id=".."/></agent-identifier></receiver>
	 * 
	 * @param sb string buffer which will contains the built tags
	 * @param tag the name of the tags (receiver, reply-to)
	 * @param agentAddresses concerned agents
	 * 
	 * @see #encodeAgent(StringBuffer, String, AgentAddress)
	 */
	public static void encodeAgents(StringBuffer sb, String tag, Collection<AgentAddress> agentAddresses) {
		if (agentAddresses != null && agentAddresses.size() > 0) {
			for (AgentAddress agentAddress : agentAddresses) {
				encodeAgent(sb, tag, agentAddress);
			}
		} else {
			sb.append(Locale.getString(XMLACLCodec.class, "STARTTAG",  //$NON-NLS-1$
					tag
				)
			);
			sb.append(Locale.getString(XMLACLCodec.class, "ENDTAG",  //$NON-NLS-1$
					tag
				)
			);
		}
	}
	
	/**
	 * Builds a simple tag. Check if the value contains invalid xml characters (such as < for exameple).
	 * If it does, a CDATA section is added.
	 * ex: <tag>value</value> or <tag><![CDATA[value]]></tag>
	 * 
	 * @param sb string buffer which will contains the built tag
	 * @param tag the name of the tag
	 * @param value value of the tag
	 */
	public static void encodeBasicElement(StringBuffer sb, String tag, String value) {
		Pattern p = Pattern.compile(Locale.getString(XMLACLCodec.class, "INVALIDCHARREGEX")); //$NON-NLS-1$
		
		String effectiveValue = ifNotNull(value).toString();
		
		if (p.matcher(effectiveValue).find()) {
			effectiveValue = Locale.getString(XMLACLCodec.class, "CDATABEGIN") //$NON-NLS-1$
							+ effectiveValue
							+ Locale.getString(XMLACLCodec.class, "CDATAEND"); //$NON-NLS-1$
		}
		
		sb.append(Locale.getString(XMLACLCodec.class, "ELEMENT",  //$NON-NLS-1$
				tag, 
				effectiveValue,
				tag
			)
		);
	}
	
	/**
	 * Builds the reply-by tag
	 * ex: <reply-by time=""/>
	 * 
	 * @param sb string buffer wich will contains the reply-by tag
	 * @param tag the name of the tag (should always be 'reply-by')
	 * @param value the reply by date value
	 */
	public static void encodeReplyByDate(StringBuffer sb, String tag, Date value) {
		sb.append(Locale.getString(XMLACLCodec.class, "SHORTELT",  //$NON-NLS-1$
				tag, 
				Locale.getString(XMLACLCodec.class, "REPLYBYATTR"), //$NON-NLS-1$
				ifNotNull(ACLDateUtil.toDateTimeToken(value))
			)
		);
	}

	/**
	 * 
	 * @param byteMsg the ACLMessage encoded in byte array (payload)
	 * @param parameters may contains the PayloadEncoding (not mandatory)
	 * @return a JDOM document from the byteMsg param
	 * 
	 * @see XMLACLCodec#decode(byte[], Object...)
	 */
	public static Document getXMLDocument(byte[] byteMsg, Object... parameters) {
		String charset = PayloadEncoding.UTF8.getValue();
		for (Object parameter : parameters) {
			if (parameter instanceof PayloadEncoding) {
				charset = ((PayloadEncoding)parameter).getValue();
			}
		}
		
		String message;
		try {
			message = new String(byteMsg, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		try {
			doc = sb.build(new StringReader(message));
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return doc;
	}
	
	/**
	 * Retrieves the Performative
	 * 
	 * @param element a JDOM Element which represents the root tag (<fipa-message>)
	 * @return the decoded Performative
	 * 
	 * @see Performative
	 */
	public static Performative decodePerformative(Element element) {
		if (element == null || element.getAttribute(Locale.getString(XMLACLCodec.class, "FIPAMESSAGEATTR")) == null) { //$NON-NLS-1$
			return Performative.NONE;
		}
			
		return Performative.valueOfByName(element.getAttributeValue(Locale.getString(XMLACLCodec.class, "FIPAMESSAGEATTR"))); //$NON-NLS-1$
	}
	
	/**
	 * Retrieves all the agents of one type (receiver, reply-to)
	 * 
	 * @param agentEltList a list of JDOM Element which represents (receiver|reply-to) tags
	 * @return collection of decoded AgentAddress
	 * 
	 * @see #decodeAgent(Element)
	 * @see AgentAddress
	 */
	public static Collection<AgentAddress> decodeAgents(List<?> agentEltList) {
		if (agentEltList == null || agentEltList.size() < 1) {
			return null;
		}
		
		Collection<AgentAddress> agents = null;
		AgentAddress agt;
		
		for (Object o : agentEltList) {
			if (o instanceof Element) {
				agt = decodeAgent((Element)o);
				if (agt != null) {
					if (agents == null) {
						agents = new ArrayList<AgentAddress>();
					}
					agents.add(agt);
				}
			}
		}
		
		return agents;
	}
	
	/**
	 * Retrieves a AgentAddress (sender, receiver, reply-to)
	 * 
	 * @param element a JDOM Element which represents a (sender|receiver|reply-to) tag
	 * @return the decoded AgentAddress
	 * 
	 * @see #decodeAgentAddress(Element)
	 * @see AgentAddress
	 */
	public static AgentAddress decodeAgent(Element element) {	
		String agentIdentifierTag = Locale.getString(XMLACLCodec.class, "AGENTIDENTIFIER"); //$NON-NLS-1$
		String nameTag = Locale.getString(XMLACLCodec.class, "NAME"); //$NON-NLS-1$
		String nameAttr = Locale.getString(XMLACLCodec.class, "NAMEATTR"); //$NON-NLS-1$
		
		if (element == null 
				|| element.getChild(agentIdentifierTag) == null
				|| element.getChild(agentIdentifierTag).getChild(nameTag) == null
				|| element.getChild(agentIdentifierTag).getChild(nameTag).getAttribute(nameAttr) == null
		) {
			return null;
		}
		
		return decodeAgentAddress(element);
	}
	
	/**
	 * Retrieves a AgentAddress (sender, receiver, reply-to)
	 * 
	 * @param element a JDOM Element which represents a (sender|receiver|reply-to) tag
	 * @return the decoded AgentAddress
	 * 
	 * @see #decodeAgent(Element)
	 * @see AddressUtil#createAgentAddress(UUID)
	 * @see AgentAddress
	 */
	private static AgentAddress decodeAgentAddress(Element element) {
		UUID uuid;
		try {
			uuid = UUID.fromString(
					element	.getChild(Locale.getString(XMLACLCodec.class, "AGENTIDENTIFIER")) //$NON-NLS-1$
							.getChild(Locale.getString(XMLACLCodec.class, "NAME")) //$NON-NLS-1$
							.getAttributeValue(Locale.getString(XMLACLCodec.class, "NAMEATTR")) //$NON-NLS-1$
			);
		} catch (IllegalArgumentException e) {
			return null;
		}
		
		return AddressUtil.createAgentAddress(uuid);
	
	}
	
	/**
	 * Retrieves the String value of a basic tag
	 * 
	 * @param element a JDOM Element which represents a basic tag
	 * @return the value of the tag
	 */
	public static String decodeStringMsgParam(Element element) {
		if (element == null || element.getValue() == null) {
			return null;
		}
		
		return element.getValue();
	}
	
	/**
	 * Retrieves the content of the content
	 * 
	 * @param element a JDOM Element wich represents the content tag
	 * @return the content of the content in a StringBuffer
	 */
	public static StringBuffer decodeContent(Element element) {
		if (element == null || element.getValue() == null) {
			return null;
		}
		
		return new StringBuffer(element.getValue());
	}
	
	/**
	 * Retrieves the reply by date
	 * @param element a JDOM Element which represents a reply-by tag
	 * @return the reply by data String value
	 */
	public static Date decodeReplyByDate(Element element) {
		if (element == null || element.getAttribute(Locale.getString(XMLACLCodec.class, "REPLYBYATTR")) == null) { //$NON-NLS-1$
			return null;
		}
		
		return ACLDateUtil.toDate(element.getAttributeValue(Locale.getString(XMLACLCodec.class, "REPLYBYATTR"))); //$NON-NLS-1$
	}
	
	/**
	 * Retrieves the conversation ID
	 * @param element a JDOM Element which represents a conversation-id tag
	 * @return the UUID of the conversation
	 */
	public static UUID decodeConversationId(Element element) {
		if (element == null || element.getValue() == null) {
			return null;
		}
		
		try {
			return UUID.fromString(element.getValue());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	/**
	 * In an ACLMessage, a lot of attributes are not mandatory so we often have to deal with null value.
	 * This silly method return an empty String if the given object is null or the object itself otherwise
	 * 
	 * @param object
	 * @return an empty String if the given object is null or the object itself otherwise
	 */
	public static Object ifNotNull(Object object) {
		if (object == null) {
			return ""; //$NON-NLS-1$
		}
		return object;
	}
	
	/**
	 * Computes a pretty xml String (ident, ..)
	 * 
	 * @param xml a not pretty xml String
	 * @return a prtty xml String
	 */
	public static String format(String xml) {
        try {
            final InputSource src = new InputSource(new StringReader(xml));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();

            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation(Locale.getString(XMLACLCodec.class, "formatxml.ls")); //$NON-NLS-1$
            final LSSerializer writer = impl.createLSSerializer();

            writer.getDomConfig().setParameter(Locale.getString(XMLACLCodec.class, "formatxml.prettyprint"), true); //$NON-NLS-1$
            writer.getDomConfig().setParameter(Locale.getString(XMLACLCodec.class, "formatxml.xmldeclaration"), false); //$NON-NLS-1$

            return writer.writeToString(document);
        } catch (Exception e) {
            return xml;
        }
    }
}
