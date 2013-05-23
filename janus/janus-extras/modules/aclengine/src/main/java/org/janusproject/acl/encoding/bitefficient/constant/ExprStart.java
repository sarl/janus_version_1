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
 * This enumeration describes all available constant for ExprStart as 
 * defined by FIPA for Bit-Efficient encoding, 
 * and their setter (used for decoding process - java reflection tips)
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00069/SC00069G.html">FIPA ACL Message Representation in Bit-Efficient Specification</a> 
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */

public enum ExprStart {

	/** Level down.
	 * <p>
	 * Code: {@code 0x60}.
	 */
	LEVEL_DOWN((byte) 0x60),
	/** Begin a word.
	 * <p>
	 * Code: {@code 0x70}.
	 */
	WORD_BEGIN((byte) 0x70),
	/** End a word.
	 * <p>
	 * Code: {@code 0x00}.
	 */
	WORD_END((byte) 0x00),
	/** Begin the code of an index word.
	 * <p>
	 * Code: {@code 0x71}.
	 */
	INDEX_WORD_CODE_BEGIN((byte) 0x71),
	/** Begin a number.
	 * <p>
	 * Code: {@code 0x72}.
	 */
	NUMBER_BEGIN((byte) 0x72),
	/** Begin an hexadecimal number.
	 * <p>
	 * Code: {@code 0x73}.
	 */
	HEXA_NUMBER_BEGIN((byte) 0x73),
	/** Begin a string.
	 * <p>
	 * Code: {@code 0x74}.
	 */
	STRING_BEGIN((byte) 0x74),
	/** End a string.
	 * <p>
	 * Code: {@code 0x00}.
	 */
	STRING_END((byte) 0x00),
	/** Begin an index string.
	 * <p>
	 * Code: {@code 0x75}.
	 */
	INDEX_STRING_BEGIN((byte) 0x75),
	/** Begin an len8 string.
	 * <p>
	 * Code: {@code 0x76}.
	 */
	LEN8_STRING_BEGIN((byte) 0x76),
	/** Begin an len16 string.
	 * <p>
	 * Code: {@code 0x77}.
	 */
	LEN16_STRING_BEGIN((byte) 0x77),
	/** Begin a len32 string.
	 * <p>
	 * Code: {@code 0x78}.
	 */
	LEN32_STRING_BEGIN((byte) 0x78),
	/** Begin a string of index byte.
	 * <p>
	 * Code: {@code 0x79}.
	 */
	INDEX_BYTE_STRING_BEGIN((byte) 0x79);
	
	private final byte code;
	
	private ExprStart(byte code) {
		this.code = code;
	}
	
	/** Replies the code from the FIPA specification.
	 * 
	 * @return the code.
	 */
	public byte getCode() {
		return this.code;
	}
	

}
