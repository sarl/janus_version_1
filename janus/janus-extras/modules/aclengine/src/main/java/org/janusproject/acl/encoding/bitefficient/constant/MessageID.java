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
 * This enumeration describes all available constant for MessageID as
 * defined by FIPA for Bit-Efficient encoding, 
 * and their setter (used for decoding process - java reflection tips).
 * <p>
 * The first byte defines the message identifier. The identifier byte
 * can be used to separate bit-efficient ACL messages
 * from (for example) string-based messages and separate different
 * coding schemes.
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
public enum MessageID {
	
	/** The value 0xFA defines a bit- efficient coding scheme without dynamic code tables.
	 * <p>
	 * Code: {@code 0xFA}.
	 */
	BITEFFICIENT((byte) 0xFA),
	/** The value 0xFB defines a bit-efficient coding scheme with dynamic code tables.
	 * <p>
	 * Code: {@code 0xFB}.
	 */
	BITEFFICIENT_CODETABLE ((byte) 0xFB),
	/** The message identifier 0xFC is used when dynamic code tables are being used, 
	 * but the sender does not want to update code tables (even if message contains
	 * strings that should be added to code table).
	 * <p>
	 * Code: {@code 0xFC}.
	 */ 
	BITEFFICIENT_NO_CODETABLE ((byte) 0xFC);
	
	private final byte code;
	
	private MessageID(byte code){
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
