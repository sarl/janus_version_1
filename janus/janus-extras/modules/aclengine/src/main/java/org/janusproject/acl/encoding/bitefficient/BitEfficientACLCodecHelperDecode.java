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
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.janusproject.acl.Performative;
import org.janusproject.acl.encoding.ACLDateUtil;
import org.janusproject.acl.encoding.bitefficient.constant.AgentIdentifier;
import org.janusproject.acl.encoding.bitefficient.constant.BinDateTimeToken;
import org.janusproject.acl.encoding.bitefficient.constant.BinString;
import org.janusproject.acl.encoding.bitefficient.constant.BinWord;
import org.janusproject.acl.encoding.bitefficient.constant.EndOfCollection;
import org.janusproject.acl.encoding.bitefficient.constant.PredefinedMsgType;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.AddressUtil;

/**
 * Helper used in bit efficient decoding. Make the BitEfficientACLCodec class simplier and easier to read.
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
public class BitEfficientACLCodecHelperDecode {
	
	/**
	 * Read the performative-byte from the buffer.
	 * 
	 * @param buffer buffer from which bytes are read
	 * @return the corresponding Performative
	 */
	public static Performative decodePerformative(List<Byte> buffer) {
		return PredefinedMsgType.getPerformative(readByte(buffer));
	}
	
	/**
	 * Read a agent address from the buffer
	 * 
	 * @param buffer buffer from which bytes are read
	 * @return the corresponding Agent Address
	 */
	public static AgentAddress decodeAgent(List<Byte> buffer) {
		AgentAddress agent = null;
		
		Byte b = readByte(buffer); // b == AgentIdentifier.AGENT_NAME_BEGIN == 0x02
		
		if (b == AgentIdentifier.AGENT_NAME_BEGIN.getCode()) {
			String s = getString(buffer);
			UUID uuid = UUID.fromString(s);
			agent = AddressUtil.createAgentAddress(uuid);
		}
		
		b = readByte(buffer); // b == EndOfCollection.END_OF_COLLECTION == 0x00
		
		return agent;
	}
	
	/**
	 * Read a collection of agent address from the buffer
	 * 
	 * @param buffer buffer from which bytes are read
	 * @return the corresponding collection of Agent Address
	 */
	public static Collection<AgentAddress> decodeAgents(List<Byte> buffer) {
		Collection<AgentAddress> agents = null;
		AgentAddress agt;
		
		while (buffer.get(0) != EndOfCollection.END_OF_COLLECTION.getCode()) {
			agt = decodeAgent(buffer);
			if (agt != null) {
				if (agents == null) {
					agents = new ArrayList<AgentAddress>();
				}
				agents.add(agt);
			}
		}
		
		readByte(buffer); // EndOfCollection.END_OF_COLLECTION
		
		return agents;
	}
	
	/**
	 * Read a date from the buffer 
	 * 
	 * @param buffer buffer from which bytes are read
	 * @return the corresponding date
	 */
	public static Date decodeDate(List<Byte> buffer) {
		byte type = readByte(buffer);
		
		List<Byte> bytesRead = readBytes(buffer, 9);

        String s = BinDate.toString(bytesRead);
        
        if (type == BinDateTimeToken.ABS_TIME_TYPE_DESIGNATOR.getCode() 
        		|| type == BinDateTimeToken.REL_TIME_POS_TYPE_DESIGNATOR.getCode()
                || type == BinDateTimeToken.REL_TIME_NEG_TYPE_DESIGNATOR.getCode()) {
			s += (char)readByte(buffer).byteValue();
        }
        
        if (type == BinDateTimeToken.REL_TIME_POS.getCode() || type == BinDateTimeToken.REL_TIME_POS_TYPE_DESIGNATOR.getCode()) {
        	s = '+' + s;
        } else if (type == BinDateTimeToken.REL_TIME_NEG.getCode() || type == BinDateTimeToken.REL_TIME_NEG_TYPE_DESIGNATOR.getCode()) {
        	s = '-' + s;
        }
        
        return ACLDateUtil.toDate(s);
	}
	
	/**
	 * Read a string parameter from the buffer.
	 * 
	 * @param buffer buffer from which bytes are read
	 * @return the corresponding String parameter
	 */
	public static String decodeParam(List<Byte> buffer) {
		return getString(buffer);
	}
	
	/**
	 * Read an UUID from the buffer
	 * 
	 * @param buffer buffer from which bytes are read
	 * @return the corresponding UUID
	 */
	public static UUID decodeUUID(List<Byte> buffer) {
		return UUID.fromString(decodeParam(buffer));
	}
	
	/**
	 * Read a content of an ACLMessage from the buffer
	 * 
	 * @param buffer buffer from which bytes are read
	 * @return the string buffer content
	 */
	public static StringBuffer decodeMsgContent(List<Byte> buffer) {
		return new StringBuffer(decodeParam(buffer));
	}
	
	/**
	 * Read a String (Word or String) from the buffer
	 * 
	 * @param buffer buffer from which bytes are read
	 * @return the corresponding String
	 */
	private static String getString(List<Byte> buffer) {
		byte type = readByte(buffer);
		return getRealString(type, buffer);
    }
	
	/**
	 * Read a Word or a String (depending on type byte) from the buffer
	 * 
	 * @param type of String (String, Word, ByteLengthEncoded, ..)
	 * @param buffer buffer from which bytes are read
	 * @return the corresponding String
	 */
    private static String getRealString(byte type, List<Byte> buffer) {
    	String decodedString = null;
    	List<Byte> bytesRead = new ArrayList<Byte>();
    	
		if (type == BinWord.WORD_BEGIN.getCode() || type == BinString.STRING_BEGIN.getCode()) {
			byte until = (type == BinWord.WORD_BEGIN.getCode()) ? BinString.STRING_END.getCode() : BinWord.WORD_END.getCode();
			
			bytesRead = readBytes(buffer, until);
			decodedString = new String(toPrimitive(bytesRead));
			
		} else if (type == BinString.LEN8_BYTE_SEQ_BEGIN.getCode()
					|| type == BinString.LEN16_BYTE_SEQ_BEGIN.getCode()
					|| type == BinString.LEN32_BYTE_SEQ_BEGIN.getCode()) {
			
			
			int length = getBLEHeader(buffer, type);
			decodedString = "#"; //$NON-NLS-1$
			decodedString += length;
			decodedString += "\""; //$NON-NLS-1$
			
			bytesRead = readBytes(buffer, length);			
			decodedString += new String(toPrimitive(bytesRead));
		}
        
        return decodedString;
    }
    
    /**
     * Read the BLE header from the buffer
     * 
     * @param buffer buffer from which bytes are read
     * @param type the type of BLE (8, 16 or 32 bits)
     * @return the corresponding length
     */
    private static int getBLEHeader(List<Byte> buffer, byte type) {
    	int length = 0;
    	
    	if (type == BinString.LEN8_BYTE_SEQ_BEGIN.getCode()) {
    		length = readByte(buffer);
        } else if (type == BinString.LEN16_BYTE_SEQ_BEGIN.getCode()) {
        	length = ((readByte(buffer)&0xff)<<8) + (readByte(buffer)&0xff);
        } else if (type == BinString.LEN32_BYTE_SEQ_BEGIN.getCode()) {
        	length = ((readByte(buffer)&0xff)<<24) + ((readByte(buffer)&0xff)<<16) + ((readByte(buffer)&0xff)<<8) + (readByte(buffer)&0xff);
        }
    	
    	return length;
    }
    
    /**
     * Read bytes from the buffer until it reach the given byte.
     * 
     * @param buffer buffer from which bytes are read
     * @param until byte limit
     * @return list of Byte read
     */
    public static List<Byte> readBytes(List<Byte> buffer, byte until) {
    	List<Byte> bytesRead = new ArrayList<Byte>();
		
    	Byte b;
    	while (((b = readByte(buffer)) != until) && (!buffer.isEmpty())) {
    		bytesRead.add(b);
    	}
    	
    	return bytesRead;
    }
    
    /**
     * Read bytes (according to the given number) from the buffer
     * 
     * @param buffer buffer from which bytes are read
     * @param nbBytes number of bytes to read
     * @return list of Byte read
     */
    public static List<Byte> readBytes(List<Byte> buffer, int nbBytes) {
    	List<Byte> bytesRead = new ArrayList<Byte>();
    	
    	int byteNUmber = nbBytes;
    	while (byteNUmber > 0 && !buffer.isEmpty()) {
    		bytesRead.add(buffer.remove(0));
    		--byteNUmber;
    	}
    	
    	return bytesRead;
    }
    
    /**
     * Read one byte from the buffer 
     * @param buffer buffer from which bytes are read
     * @return byte read
     */
    public static Byte readByte(List<Byte> buffer) {
    	return buffer.remove(0);
    }
    
    /**
     * List of Byte to array of byte
     * 
     * @param buffer
     * @return array of byte from buffer
     */
    private static byte[] toPrimitive(List<Byte> buffer) {
    	return ArrayUtils.toPrimitive(buffer.toArray(new Byte[0]));
    }
}
