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
 * This enumeration describes all available constant for ExprEnd as defined by FIPA for Bit-Efficient encoding, 
 * and their setter (used for decoding process - java reflection tips)
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

public enum ExprEnd {

	LEVEL_UP((byte) 0x40),
	WORD_BEGIN((byte) 0x50),
	WORD_END((byte) 0x00),
	INDEX_WORD_CODE_BEGIN((byte) 0x51),
	NUMBER_BEGIN((byte) 0x52),
	HEXA_NUMBER_BEGIN((byte) 0x53),
	STRING_BEGIN((byte) 0x54),
	STRING_END((byte) 0x00),
	INDEX_STRING_BEGIN((byte) 0x55),
	LEN8_STRING_BEGIN((byte) 0x56),
	LEN16_STRING_BEGIN((byte) 0x57),
	LEN32_STRING_BEGIN((byte) 0x58),
	INDEX_BYTE_STRING_BEGIN((byte) 0x59);
	
	private final byte code;
	
	ExprEnd(byte code){
		this.code = code;
	}
	
	public byte getCode(){
		return this.code;
	}
	

}
