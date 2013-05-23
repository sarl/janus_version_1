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
 * This enumeration describes all available constant for BinDateTimeToken as defined by FIPA for Bit-Efficient encoding, 
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

public enum BinDateTimeToken {
	/** Absolute time.
	 * <p>
	 * Code: {@code 0x20}.
	 */
	ABS_TIME((byte) 0x20),
	/** Relative positive time.
	 * <p>
	 * Code: {@code 0x21}.
	 */
	REL_TIME_POS((byte) 0x21),
	/** Relative negative time.
	 * <p>
	 * Code: {@code 0x22}.
	 */
	REL_TIME_NEG((byte) 0x22),
	/** Absolute time with type designator.
	 * <p>
	 * Code: {@code 0x24}.
	 */
	ABS_TIME_TYPE_DESIGNATOR((byte) 0x24),
	/** Relative time positive with type designator.
	 * <p>
	 * Code: {@code 0x25}.
	 */
	REL_TIME_POS_TYPE_DESIGNATOR((byte) 0x25),
	/** Relative time negative with type designator.
	 * <p>
	 * Code: {@code 0x26}.
	 */
	REL_TIME_NEG_TYPE_DESIGNATOR((byte) 0x26);
	
	private final byte code;
	
	private BinDateTimeToken(byte code){
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
