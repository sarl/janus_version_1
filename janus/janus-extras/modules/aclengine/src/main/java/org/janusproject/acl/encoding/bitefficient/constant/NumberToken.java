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

package org.janusproject.acl.encoding.bitefficient.constant;


/**
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00069/SC00069G.html">FIPA ACL Message Representation in Bit-Efficient Specification</a> 
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth-01$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */

public enum NumberToken {

	PADDING("", (byte) 0x00), //$NON-NLS-1$
	ZERO("0", (byte) 0x01), //$NON-NLS-1$
	ONE("1", (byte) 0x02), //$NON-NLS-1$
	TWO("2", (byte) 0x03), //$NON-NLS-1$
	THREE("3", (byte) 0x04), //$NON-NLS-1$
	FOUR("4", (byte) 0x05), //$NON-NLS-1$
	FIVE("5", (byte) 0x06), //$NON-NLS-1$
	SIX("6", (byte) 0x07), //$NON-NLS-1$
	SEVEN("7", (byte) 0x08), //$NON-NLS-1$
	EIGHT("8", (byte) 0x09), //$NON-NLS-1$
	NINE("9", (byte) 0x0a), //$NON-NLS-1$
	PLUS("+", (byte) 0x0b), //$NON-NLS-1$
	EXPONENT("E", (byte) 0x0c), //$NON-NLS-1$
	MINUS("-", (byte) 0x0d), //$NON-NLS-1$
	DOT(".", (byte) 0x0e); //$NON-NLS-1$
	
	private final String token;
	private final byte code;
	
	NumberToken(String token, byte code){
		this.token = token;
		this.code = code;
	}
	
	public byte getCode() {
		return this.code;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public static byte getCode(char token) {
		for (NumberToken value : values()) { 
			if (value.getToken().equalsIgnoreCase(Character.toString(token))) { 
				return value.getCode();
			} 
		}
		return NumberToken.PADDING.getCode(); 
	}
	
	public static String getToken(byte code) {
		for (NumberToken value : values()) { 
			if (value.getCode() == code) { 
				return value.getToken();
			} 
		}
		return NumberToken.PADDING.getToken();
	}
}
