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
 * This enumeration describes all available constant for BinString as defined by FIPA for Bit-Efficient encoding, 
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

public enum BinString {

	/** Begin a string.
	 * <p>
	 * Code: {@code 0x14}.
	 */
	STRING_BEGIN((byte) 0x14),
	/** End a string.
	 * <p>
	 * Code: {@code 0x00}.
	 */
	STRING_END((byte) 0x00),
	/** Begin an index string.
	 * <p>
	 * Code: {@code 0x15}.
	 */
	INDEX_STRING_BEGIN((byte) 0x15),
	/** Begin a len8 byte sequence.
	 * <p>
	 * Code: {@code 0x16}.
	 */
	LEN8_BYTE_SEQ_BEGIN((byte) 0x16),
	/** Begin a len16 byte sequence.
	 * <p>
	 * Code: {@code 0x17}.
	 */
	LEN16_BYTE_SEQ_BEGIN((byte) 0x17),
	/** Begin an encoded index byte length.
	 * <p>
	 * Code: {@code 0x18}.
	 */
	INDEX_BYTE_LENGTH_ENCODED_BEGIN((byte) 0x18),
	/** Begin a len32 byte sequence.
	 * <p>
	 * Code: {@code 0x19}.
	 */
	LEN32_BYTE_SEQ_BEGIN((byte) 0x19);
	
	private final byte code;
	
	private BinString(byte code){
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
