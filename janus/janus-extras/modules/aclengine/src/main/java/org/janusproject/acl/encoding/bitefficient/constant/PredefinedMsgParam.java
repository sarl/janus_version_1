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
 * This enumeration describes all available constant for PredefinedMsgParam as defined by FIPA for Bit-Efficient encoding, 
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

public enum PredefinedMsgParam {

	 PARAM_SENDER ( (byte) 0x02),
	 PARAM_RECEIVER ( (byte) 0x03),
	 PARAM_CONTENT ( (byte) 0x04),
	 PARAM_REPLY_WITH ( (byte) 0x05),
	 PARAM_REPLY_BY ( (byte) 0x06),
	 PARAM_IN_REPLY_TO ( (byte) 0x07),
	 PARAM_REPLY_TO ( (byte) 0x08),
	 PARAM_LANGUAGE ( (byte) 0x09),
	 PARAM_ENCODING ( (byte) 0x0A),
	 PARAM_ONTOLOGY ( (byte) 0x0B),
	 PARAM_PROTOCOL ( (byte) 0x0C),
	 PARAM_CONVERSATION_ID ( (byte) 0x0D);
	
	private final byte code;
	
	PredefinedMsgParam(byte code){
		this.code = code;
	}
	
	public byte getCode(){
		return this.code;
	}
}
