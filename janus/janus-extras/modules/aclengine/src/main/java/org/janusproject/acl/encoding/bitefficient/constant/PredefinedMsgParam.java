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
 * This enumeration describes all available constant 
 * for PredefinedMsgParam as defined by FIPA for Bit-Efficient encoding, 
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

public enum PredefinedMsgParam {

	/**
	 * The parameter is the sender.
	 * <p>
	 * Code: {@code 0x02}.
	 */
	PARAM_SENDER ( (byte) 0x02),
	/**
	 * The parameter is the receiver.
	 * <p>
	 * Code: {@code 0x03}.
	 */
	PARAM_RECEIVER ( (byte) 0x03),
	/**
	 * The parameter is the content.
	 * <p>
	 * Code: {@code 0x04}.
	 */
	PARAM_CONTENT ( (byte) 0x04),
	/**
	 * The parameter is the reply-with.
	 * <p>
	 * Code: {@code 0x05}.
	 */
	PARAM_REPLY_WITH ( (byte) 0x05),
	/**
	 * The parameter is the reply-by.
	 * <p>
	 * Code: {@code 0x06}.
	 */
	PARAM_REPLY_BY ( (byte) 0x06),
	/**
	 * The parameter is the in-reply-to.
	 * <p>
	 * Code: {@code 0x07}.
	 */
	PARAM_IN_REPLY_TO ( (byte) 0x07),
	/**
	 * The parameter is the reply-to.
	 * <p>
	 * Code: {@code 0x08}.
	 */
	PARAM_REPLY_TO ( (byte) 0x08),
	/**
	 * The parameter is the language.
	 * <p>
	 * Code: {@code 0x09}.
	 */
	PARAM_LANGUAGE ( (byte) 0x09),
	/**
	 * The parameter is the encoding.
	 * <p>
	 * Code: {@code 0x0A}.
	 */
	PARAM_ENCODING ( (byte) 0x0A),
	/**
	 * The parameter is the ontology id.
	 * <p>
	 * Code: {@code 0x0B}.
	 */
	PARAM_ONTOLOGY ( (byte) 0x0B),
	/**
	 * The parameter is the protocol id.
	 * <p>
	 * Code: {@code 0x0C}.
	 */
	PARAM_PROTOCOL ( (byte) 0x0C),
	/**
	 * The parameter is the conversation id.
	 * <p>
	 * Code: {@code 0x0D}.
	 */
	PARAM_CONVERSATION_ID ( (byte) 0x0D);

	private final byte code;

	private PredefinedMsgParam(byte code){
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
