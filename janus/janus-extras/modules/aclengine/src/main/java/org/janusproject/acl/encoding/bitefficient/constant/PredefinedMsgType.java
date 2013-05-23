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
 * This enumeration describes all available constant for
 * Predefined Msg Type as defined by FIPA for Bit-Efficient encoding, 
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

public enum PredefinedMsgType {
	/** Accept a proposal.
	 * <p>
	 * Code: {@code 0x01}.
	 */
	ACCEPT_PROPOSAL(Performative.ACCEPT_PROPOSAL, (byte) 0x01),
	/** Agree.
	 * <p>
	 * Code: {@code 0x02}.
	 */
	AGREE(Performative.AGREE, (byte) 0x02),
	/** Cancel.
	 * <p>
	 * Code: {@code 0x03}.
	 */
	CANCEL(Performative.CANCEL, (byte) 0x03),
	/** Call for proposal.
	 * <p>
	 * Code: {@code 0x04}.
	 */
	CFP(Performative.CFP, (byte) 0x04),
	/** Confirm.
	 * <p>
	 * Code: {@code 0x05}.
	 */
	CONFIRM(Performative.CONFIRM, (byte) 0x05),
	/** Disconfirm.
	 * <p>
	 * Code: {@code 0x06}.
	 */
	DISCONFIRM(Performative.DISCONFIRM, (byte) 0x06),
	/** Failure.
	 * <p>
	 * Code: {@code 0x07}.
	 */
	FAILURE(Performative.FAILURE, (byte) 0x07),
	/** Inform.
	 * <p>
	 * Code: {@code 0x08}.
	 */
	INFORM(Performative.INFORM, (byte) 0x08),
	/** Inform if.
	 * <p>
	 * Code: {@code 0x09}.
	 */
	INFORM_IF(Performative.INFORM_IF, (byte) 0x09),
	/** Inform ref.
	 * <p>
	 * Code: {@code 0x0A}.
	 */
	INFORM_REF(Performative.INFORM_REF, (byte) 0x0A),
	/** Not understood.
	 * <p>
	 * Code: {@code 0x0B}.
	 */
	NOT_UNDERSTOOD(Performative.NOT_UNDERSTOOD, (byte) 0x0B),
	/** Propagate.
	 * <p>
	 * Code: {@code 0x0C}.
	 */
	PROPAGATE(Performative.PROPAGATE, (byte) 0x0C),
	/** Propose.
	 * <p>
	 * Code: {@code 0x0D}.
	 */
	PROPOSE(Performative.PROPOSE, (byte) 0x0D),
	/** Proxy.
	 * <p>
	 * Code: {@code 0x0E}.
	 */
	PROXY(Performative.PROXY, (byte) 0x0E),
	/** Query if.
	 * <p>
	 * Code: {@code 0x0F}.
	 */
	QUERY_IF(Performative.QUERY_IF, (byte) 0x0F),
	/** Query ref.
	 * <p>
	 * Code: {@code 0x10}.
	 */
	QUERY_REF(Performative.QUERY_REF, (byte) 0x10),
	/** Refuse.
	 * <p>
	 * Code: {@code 0x11}.
	 */
	REFUSE(Performative.REFUSE, (byte) 0x11),
	/** Reject proposal.
	 * <p>
	 * Code: {@code 0x12}.
	 */
	REJECT_PROPOSAL(Performative.REJECT_PROPOSAL, (byte) 0x12),
	/** Request.
	 * <p>
	 * Code: {@code 0x13}.
	 */
	REQUEST(Performative.REQUEST, (byte) 0x13),
	/** REquest when.
	 * <p>
	 * Code: {@code 0x14}.
	 */
	REQUEST_WHEN(Performative.REQUEST_WHEN, (byte) 0x14),
	/** Request whenever.
	 * <p>
	 * Code: {@code 0x15}.
	 */
	REQUEST_WHENEVER(Performative.REQUEST_WHENEVER, (byte) 0x15),
	/** Subscribe.
	 * <p>
	 * Code: {@code 0x16}.
	 */
	SUBSCRIBE(Performative.SUBSCRIBE, (byte) 0x16);
	
	private final Performative performative;
	private final byte code;
	
	private PredefinedMsgType(Performative performative, byte code) {
		this.performative = performative;
		this.code = code;
	}
	
	/** Replies the code from the FIPA specification.
	 * 
	 * @return the code.
	 */
	public byte getCode() {
		return this.code;
	}
	
	/** Replies the performative associated to this message type.
	 * 
	 * @return the performative.
	 */
	public Performative getPerformative() {
		return this.performative;
	}
	
	/** Replies the code associated to the given performative.
	 * 
	 * @param performative
	 * @return the code of the performative.
	 */
	public static byte getCode(Performative performative) {
		for (PredefinedMsgType value : values()) { 
			if (value.getPerformative() == performative) { 
				return value.getCode();
			} 
		}
		return -1;
	}
	
	/** Replies the performative associated to the given code.
	 * 
	 * @param code
	 * @return the performative for the code.
	 */
	public static Performative getPerformative(byte code) {
		for (PredefinedMsgType value : values()) { 
			if (value.getCode() == code) { 
				return value.getPerformative();
			} 
		}
		return Performative.NONE;
	}
}
