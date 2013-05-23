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
package org.janusproject.acl.encoding.bitefficient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.ACLMessageContent;
import org.janusproject.acl.Performative;
import org.janusproject.acl.encoding.ACLMessageContentEncodingService;
import org.janusproject.acl.encoding.bitefficient.constant.EndOfCollection;
import org.janusproject.acl.encoding.bitefficient.constant.MessageID;
import org.janusproject.acl.encoding.bitefficient.constant.PredefinedMsgParam;
import org.janusproject.acl.encoding.bitefficient.constant.Version;

/**
 * This class encodes an ACLMessageContent in bit efficient or decodes an ACLMessageContent encoded in bytes from bit efficient
 * 
 * @see <a href="http://fipa.org/specs/fipa00069/SC00069G.html">FIPA ACL Message Representation in Bit Efficient Specification</a>
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BitEfficientACLCodec implements ACLMessageContentEncodingService {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see #toBitEfficient(ACLMessage)
	 */
	@Override
	public byte[] encode(ACLMessage aMsg) {
		
		return ArrayUtils.toPrimitive(toBitEfficient(aMsg).toArray(new Byte[0]));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ACLMessageContent decode(byte[] byteMsg, Object... parameters) {
		ACLMessage.Content content = new ACLMessage.Content();
		
		List<Byte> buffer = new ArrayList<Byte>(Arrays.asList(ArrayUtils.toObject(byteMsg)));
		
		// MessageId
		BitEfficientACLCodecHelperDecode.readByte(buffer);
		
		// Version
		BitEfficientACLCodecHelperDecode.readByte(buffer);
		
		Performative per = BitEfficientACLCodecHelperDecode.decodePerformative(buffer);
		content.setPerformative(per.ordinal());
		
		Byte b;
		while ((b = BitEfficientACLCodecHelperDecode.readByte(buffer)) != EndOfCollection.END_OF_COLLECTION.getCode()) {
			
			if (b == PredefinedMsgParam.PARAM_SENDER.getCode()) {
				
				content.setSender(BitEfficientACLCodecHelperDecode.decodeAgent(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_RECEIVER.getCode()) {
				
				content.setReceiver(BitEfficientACLCodecHelperDecode.decodeAgents(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_REPLY_TO.getCode()) {
				
				content.setReceiver(BitEfficientACLCodecHelperDecode.decodeAgents(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_REPLY_BY.getCode()) {
				
				content.setReplyBy(BitEfficientACLCodecHelperDecode.decodeDate(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_REPLY_WITH.getCode()) {
				
				content.setReplyWith(BitEfficientACLCodecHelperDecode.decodeParam(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_LANGUAGE.getCode()) {
				
				content.setLanguage(BitEfficientACLCodecHelperDecode.decodeParam(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_ENCODING.getCode()) {
				
				content.setEncoding(BitEfficientACLCodecHelperDecode.decodeParam(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_ONTOLOGY.getCode()) {
				
				content.setOntology(BitEfficientACLCodecHelperDecode.decodeParam(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_PROTOCOL.getCode()) {
				
				content.setProtocol(BitEfficientACLCodecHelperDecode.decodeParam(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_CONVERSATION_ID.getCode()) {
				
				content.setConversationId(BitEfficientACLCodecHelperDecode.decodeUUID(buffer));
				
			} else if (b == PredefinedMsgParam.PARAM_CONTENT.getCode()) {
				
				content.setContent(BitEfficientACLCodecHelperDecode.decodeMsgContent(buffer));
				
			} else {
				break;
			}
		}
		
		return content;
	}
	
	/**
	 * Compute the Bit Efficient representation of an ACLMessage
	 * 
	 * @param msg the ACLMessage to encode to bit efficient
	 * @return list of Byte representing the given ACLMessage
	 * 
	 * @see ACLMessage#toXML()
	 */
	public static List<Byte> toBitEfficient(ACLMessage msg) {
		List<Byte> buffer = new ArrayList<Byte>();

		buffer.add(MessageID.BITEFFICIENT.getCode()); // we don't use code table for now
		buffer.add(Version.VERSION.getCode()); // 0x10
		
		// add the corresponding byte of the performative
		// we don't deal with user defined performative as done in jade
		BitEfficientACLCodecHelperEncode.dumpMsgType(buffer, msg.getPerformative());

		BitEfficientACLCodecHelperEncode.dumpAgent(buffer, PredefinedMsgParam.PARAM_SENDER.getCode(), msg.getSender());
		
		BitEfficientACLCodecHelperEncode.dumpAgents(buffer, PredefinedMsgParam.PARAM_RECEIVER.getCode(), msg.getReceiver());
		
		BitEfficientACLCodecHelperEncode.dumpAgents(buffer, PredefinedMsgParam.PARAM_REPLY_TO.getCode(), msg.getContent().getReplyTo());
		
		BitEfficientACLCodecHelperEncode.dumpReplyBy(buffer, PredefinedMsgParam.PARAM_REPLY_BY.getCode(), msg.getContent().getReplyBy());
		
		BitEfficientACLCodecHelperEncode.dumpParam(buffer, PredefinedMsgParam.PARAM_REPLY_WITH.getCode(), msg.getContent().getReplyWith());
		
		BitEfficientACLCodecHelperEncode.dumpParam(buffer, PredefinedMsgParam.PARAM_IN_REPLY_TO.getCode(), msg.getContent().getInReplyTo());

		BitEfficientACLCodecHelperEncode.dumpParam(buffer, PredefinedMsgParam.PARAM_LANGUAGE.getCode(), msg.getContent().getLanguage());
		
		BitEfficientACLCodecHelperEncode.dumpParam(buffer, PredefinedMsgParam.PARAM_ENCODING.getCode(), msg.getContent().getEncoding());
		
		BitEfficientACLCodecHelperEncode.dumpParam(buffer, PredefinedMsgParam.PARAM_ONTOLOGY.getCode(), msg.getContent().getOntology());
		
		BitEfficientACLCodecHelperEncode.dumpWordParam(buffer, PredefinedMsgParam.PARAM_PROTOCOL.getCode(), msg.getContent().getProtocol());
		
		BitEfficientACLCodecHelperEncode.dumpParam(buffer, PredefinedMsgParam.PARAM_CONVERSATION_ID.getCode(), msg.getContent().getOntology());
		
		BitEfficientACLCodecHelperEncode.dumpMsgContent(buffer, PredefinedMsgParam.PARAM_CONTENT.getCode(), msg.getContent().getContent().toString());
	
		buffer.add(EndOfCollection.END_OF_COLLECTION.getCode());
		
		return buffer;
	}
	
}
