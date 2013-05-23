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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.janusproject.acl.Performative;
import org.janusproject.acl.encoding.ACLDateUtil;
import org.janusproject.acl.encoding.bitefficient.constant.AgentIdentifier;
import org.janusproject.acl.encoding.bitefficient.constant.BinNumber;
import org.janusproject.acl.encoding.bitefficient.constant.BinString;
import org.janusproject.acl.encoding.bitefficient.constant.BinWord;
import org.janusproject.acl.encoding.bitefficient.constant.EndOfCollection;
import org.janusproject.acl.encoding.bitefficient.constant.NumberToken;
import org.janusproject.acl.encoding.bitefficient.constant.PredefinedMsgType;
import org.janusproject.kernel.address.AgentAddress;

/**
 * Helper used in bit efficient encoding. Make the BitEfficientACLCodec class simplier and easier to read.
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
public class BitEfficientACLCodecHelperEncode {
	
	/**
	 * Add byte of the given performative
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param performative a performative of an ACLMessage
	 */
	public static void dumpMsgType(List<Byte> buffer, Performative performative) {
		buffer.add(PredefinedMsgType.getCode(performative));
	}
	
	/**
	 * Add bytes of the given agent. Add the message parameter (sender, receiver) and then add the agent identifier
	 * representation that encode the information of the agent address.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param msgParam byte representation of the role of the agent to encode (sender = 0x02, receiver = 0x03, ..)
	 * @param agent agent address to dump
	 */
	public static void dumpAgent(List<Byte> buffer, byte msgParam, AgentAddress agent) {
		if (agent != null) {
			buffer.add(msgParam);
			dumpAgentIdentifier(buffer, agent);
		}
	}
	
	/**
	 * Add bytes of the given agents. Add the message parameter (sender, receiver) and then add an agent identifier
	 * representation that encode the information of a agent address for the given collection.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param msgParam byte representation of the role of the agent to encode (sender = 0x02, receiver = 0x03, ..)
	 * @param agents list of agent addresses to dump
	 */
	public static void dumpAgents(List<Byte> buffer, byte msgParam, Collection<AgentAddress> agents) {
		if (agents != null && !agents.isEmpty()) {
			buffer.add(msgParam);
			if (agents.size() > 0) {
				for (AgentAddress agent : agents) {
					dumpAgentIdentifier(buffer, agent);
				}
			}
			buffer.add(EndOfCollection.END_OF_COLLECTION.getCode());
		}
	}
	
	/**
	 * Dump an agent address
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param agent agent address to dump
	 */
	private static void dumpAgentIdentifier(List<Byte> buffer, AgentAddress agent) {
		if (agent != null) {
			buffer.add(AgentIdentifier.AGENT_NAME_BEGIN.getCode());
			dumpWord(buffer,agent.getUUID().toString());
			buffer.add(EndOfCollection.END_OF_COLLECTION.getCode());
		}
	}
	
	/**
	 * Add bytes for the given parameter and its type.
	 * Deal with String and Word cases.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param msgParam byte representation of predefined parameter
	 * @param string information to dump
	 */
	public static void dumpParam(List<Byte> buffer, byte msgParam, String string) {
		if (string == null || string.length() < 1) {
			return;
		}
        
		String newString = ""; //$NON-NLS-1$
        if (string.contains(" ")) { //$NON-NLS-1$
            if (string.charAt(0) != '"') {
            	newString = '"' + escape(string) + '"';
            }
        }
		
        buffer.add(msgParam);
        
        if (isExpression(newString)) {
        	dumpString(buffer, newString);
        } else if (isString(newString)) {
        	dumpString(buffer, newString);
        } else {
        	dumpWord(buffer, newString);
        }
	}
	
	/**
	 * Add bytes for the given parameter wich is a simple word and will be encoded as simple word.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param msgParam byte representation of predefined parameter
	 * @param word the word to dump
	 */
	public static void dumpWordParam(List<Byte> buffer, byte msgParam, String word) {
		if (word != null) {
			buffer.add(msgParam);
			dumpWord(buffer, word);
		}
	}
	
	/** 
	 * Add bytes for the content parameter. The content is always encoded as a String.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param msgParam byte representation of predefined parameter
	 * @param content content of an ACLMessage
	 */
	public static void dumpMsgContent(List<Byte> buffer, byte msgParam, String content) {
		if (content != null) {
			buffer.add(msgParam);
			dumpString(buffer, content);
		}
	}
	
	/**
	 * Add bytes for the reply by parameter. The reply by parameter is always encoded as a date
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param msgParam byte representation of predefined parameter
	 * @param date the date to dump
	 */
	public static void dumpReplyBy(List<Byte> buffer, byte msgParam, Date date) {
		if (date != null) {
			buffer.add(msgParam);
			dumpDate(buffer, date);
		}
	}
	
	/**
	 * Add bytes for the given date.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param date the date to dump
	 */
	private static void dumpDate(List<Byte> buffer, Date date) {
		String s = ACLDateUtil.toDateTimeToken(date);
        
		buffer.add(BinDate.getDateTimeType(s));
		buffer.addAll(BinDate.toBin(s));
		
		if (ACLDateUtil.containsTypeDesignator(s)) {
			buffer.add((byte) s.charAt(s.length()-1));
		}
	}

	/**
	 * Add bytes for the given word
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param word word to dump
	 */
	private static void dumpWord(List<Byte> buffer, String word) {
		buffer.add(BinWord.WORD_BEGIN.getCode());
		buffer.addAll(getBytesFromString(word));
		buffer.add(BinWord.WORD_END.getCode());
	}
	
	/**
	 * Add bytes for the given string.
	 * Deal with bytes length encoding case.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param string string to dump
	 */
	private static void dumpString(List<Byte> buffer, String string) {
		if (string == null || string.length() < 1) {
			return;
		}
		
		boolean isBLE = (string.charAt(0) == '#' ? true : false);
		
		BinString id = getBinStringId(string);
		buffer.add(id.getCode());
		
		String newString = ""; //$NON-NLS-1$
		if (isBLE) {
			newString = string.substring(string.indexOf('"')+1);
			dumpBLEHeader(buffer, newString.length());
		}
		
		buffer.addAll(getBytesFromString(newString));
		
		if (id == BinString.STRING_BEGIN) {
			buffer.add(BinString.STRING_END.getCode());
		}
	}
	
	/**
	 * Get the bin String ID for a given String
	 * @param s string
	 * @return the bin strnig id of s
	 */
	private static BinString getBinStringId(String s) {
		if (s.charAt(0) != '#') { 
			return BinString.STRING_BEGIN;
		} else if (s.length() < 256)  {
			return BinString.LEN8_BYTE_SEQ_BEGIN;
		} else if (s.length() < 65536) {
			return BinString.LEN16_BYTE_SEQ_BEGIN;
		} else {
			return BinString.LEN32_BYTE_SEQ_BEGIN;
		}
	}
	
	/**
	 * Add bytes corresponding to the length of the byte length encoded string.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param length the length of the string which is BLE
	 */
	private static void dumpBLEHeader(List<Byte> buffer, int length) {
        if (length < 256) {
            buffer.add((byte) (length & 0xff));
        } else if (length < 65536) {
            buffer.add((byte) ((length >> 8) & 0xff));
            buffer.add((byte) (length & 0xff));
        } else {
        	buffer.add((byte) ((length >> 24) & 0xff));
            buffer.add((byte) ((length >> 16) & 0xff));
            buffer.add((byte) ((length >> 8) & 0xff));
            buffer.add((byte) (length & 0xff));
        }
    }
	
	/**
	 * Add bytes for the given number.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param number string representing a number
	 */
	@SuppressWarnings("unused")
	private static void dumpBinNumber(List<Byte> buffer, String number) {
		buffer.add(BinNumber.DECIMAL_NUMBER_BEGIN.getCode());
		dumpDigits(buffer, number);
	}
	
	/**
	 * Add bytes for the given number.
	 * 
	 * @param buffer buffer in which bytes will be added
	 * @param number string representing a number
	 */
	private static void dumpDigits(List<Byte> buffer, String number) {
		int length = number.length();
		byte d;
		
		for (int i = 0 ; i < length ; i+=2) {
			d = NumberToken.getCode(number.charAt(i));
			if ((i+1) < length) {
				d |= (NumberToken.getCode(number.charAt(i))&0x0f);
			} else {
				d |= 0x00;
			}
			
			buffer.add(d);
		}
		
		if ((length % 2) == 0) {
			buffer.add((byte) 0x00);
		}
	}
	
	/**
	 * Get the Bytes of a given String
	 * 
	 * @param s string
	 * @return a collection of Bytes
	 */
	private static Collection<Byte> getBytesFromString(String s) {
		ArrayList<Byte> bytes = null;
		
		if (s != null) {
			bytes = new ArrayList<Byte>();
			for (byte b : s.getBytes()) {
				bytes.add(b);
			}
		}
		
		return bytes;
	}


	/**
	 * Escape all " characters in a String. Used in dumpParam when dealing with string with at least one space char.
	 * 
	 * @param s string
	 * @return s with " characters escaped
	 */
	private static String escape(String s) {
        StringBuffer result = new StringBuffer(s.length());
        
        for (int i=0 ; i < s.length() ; i++) {
            if (s.charAt(i) == '"' ) {
                result.append("\\\""); //$NON-NLS-1$
            } else {
                result.append(s.charAt(i));
            }
        }
        
        return result.toString();
    }
	
	/**
	 * Check if the give string is an expression
	 * 
	 * @param s string
	 * @return boolean
	 */
	private static boolean isExpression(String s) {
		return s.charAt(0) == '(' ? true : false;
	}
	
	/**
	 * Check if the give string is a string (according to bit efficient representation)
	 * 
	 * @param s string
	 * @return boolean
	 */
	private static boolean isString(String s) {
		return s.charAt(0) == '"' || s.charAt(0) == '#' ? true : false;
	}
}
