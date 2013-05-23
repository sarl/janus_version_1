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
 * This enumeration describes all available constant for
 * Version as defined by FIPA for Bit-Efficient encoding, 
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

public enum Version {
	
	/**
	 * The version number byte contains the major version
	 * number in the upper four bits and minor version
	 * number in the lower four bits.
	 * <p> 
	 * Version 1.0 (coded as {@code 0x10}).
	 */
	VERSION((byte) 0x10);
	
	private final byte code;
	
	/**
	 * @param code is the constant.
	 */
	private Version(byte code){
		this.code = code;
	}
	
	/** Replies the code from the FIPA specification.
	 * 
	 * @return the code.
	 */
	public byte getCode(){
		return this.code;
	}
	

}
