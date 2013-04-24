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

import org.janusproject.acl.Performative;

/**
 * This enumeration describes all available constant for Predefined Msg Type as defined by FIPA for Bit-Efficient encoding, 
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

public enum PredefinedMsgType {
	ACCEPT_PROPOSAL(Performative.ACCEPT_PROPOSAL, (byte) 0x01),
	AGREE(Performative.AGREE, (byte) 0x02),
	CANCEL(Performative.CANCEL, (byte) 0x03),
	CFP(Performative.CFP, (byte) 0x04),
	CONFIRM(Performative.CONFIRM, (byte) 0x05),
	DISCONFIRM(Performative.DISCONFIRM, (byte) 0x06),
	FAILURE(Performative.FAILURE, (byte) 0x07),
	INFORM(Performative.INFORM, (byte) 0x08),
	INFORM_IF(Performative.INFORM_IF, (byte) 0x09),
	INFORM_REF(Performative.INFORM_REF, (byte) 0x0A),
	NOT_UNDERSTOOD(Performative.NOT_UNDERSTOOD, (byte) 0x0B),
	PROPAGATE(Performative.PROPAGATE, (byte) 0x0C),
	PROPOSE(Performative.PROPOSE, (byte) 0x0D),
	PROXY(Performative.PROXY, (byte) 0x0E),
	QUERY_IF(Performative.QUERY_IF, (byte) 0x0F),
	QUERY_REF(Performative.QUERY_REF, (byte) 0x10),
	REFUSE(Performative.REFUSE, (byte) 0x11),
	REJECT_PROPOSAL(Performative.REJECT_PROPOSAL, (byte) 0x12),
	REQUEST(Performative.REQUEST, (byte) 0x13),
	REQUEST_WHEN(Performative.REQUEST_WHEN, (byte) 0x14),
	REQUEST_WHENEVER(Performative.REQUEST_WHENEVER, (byte) 0x15),
	SUBSCRIBE(Performative.SUBSCRIBE, (byte) 0x16);
	
	private final Performative performative;
	private final byte code;
	
	PredefinedMsgType(Performative performative, byte code) {
		this.performative = performative;
		this.code = code;
	}
	
	public byte getCode() {
		return this.code;
	}
	
	public Performative getPerformative() {
		return this.performative;
	}
	
	public static byte getCode(Performative performative) {
		for (PredefinedMsgType value : values()) { 
			if (value.getPerformative() == performative) { 
				return value.getCode();
			} 
		}
		return -1;
	}
	
	public static Performative getPerformative(byte b) {
		for (PredefinedMsgType value : values()) { 
			if (value.getCode() == b) { 
				return value.getPerformative();
			} 
		}
		return Performative.NONE;
	}
}
