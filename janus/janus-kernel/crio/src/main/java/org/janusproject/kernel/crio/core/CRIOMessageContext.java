/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.kernel.crio.core;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.message.MessageContext;

/**
 * Context of the CRIO message contains the emitter, the receiver
 * and the several other information which are not related
 * to the message content.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CRIOMessageContext
extends MessageContext {
	
	private static final long serialVersionUID = -8518171430394283311L;

	/** Address of the emitter in the group.
	 */
	private RoleAddress senderAddress;

	/** Address of the group.
	 */
	private GroupAddress group;
	
	/** Type of the receiver role.
	 */
	private Class<? extends Role> receiverRole;
		
	/** Address of the receiver in the group.
	 */
	private RoleAddress receiverAddress;

	/**
	 * @param sender is the address of the message sender.
	 * @param receiver is the address of the message receiver.
	 * @param creationDate is the date of creation of the message.
	 */
	public CRIOMessageContext(AgentAddress sender, AgentAddress receiver, float creationDate) {
		super(sender, receiver, creationDate);
	}
	
	/** Replies the role of the sender.
	 * 
	 * @return the role of the sender.
	 */
	public Class<? extends Role> getSenderRole() {
		return this.senderAddress.getRole();
	}
	
	/** Replies the role of the receiver.
	 * 
	 * @return the role of the receiver.
	 */
	public Class<? extends Role> getReceiverRole() {
		return this.receiverRole;
	}
	
	/** Replies the address of the group in which the message
	 * was sent.
	 * 
	 * @return the group of the message.
	 */
	public GroupAddress getGroup() {
		return this.group;
	}

	/** Replies the address of the sending role.
	 * 
	 * @return the address of the sending role.
	 * @since 0.5
	 */
	public RoleAddress getSendingRoleAddress() {
		return this.senderAddress;
	}

	/** Replies the address of the receiving role.
	 * 
	 * @return the address of the receiving role.
	 * @since 0.5
	 */
	public RoleAddress getReceivingRoleAddress() {
		if (this.receiverAddress==null) {
			this.receiverAddress = new RoleAddress(this.group, this.receiverRole, getReceiver());
		}
		return this.receiverAddress;
	}
	
	/** Set the context information.
	 * 
	 * @param emitter
	 * @param receiver
	 * @since 0.5
	 */
	void setEmitterReceiver(RoleAddress emitter, RoleAddress receiver) {
		this.group = emitter.getGroup();
		this.senderAddress = emitter;
		this.receiverRole = receiver.getRole();
		this.receiverAddress = receiver;
	}

	/** Set the context information.
	 * 
	 * @param emitter
	 * @param receiver
	 * @param receivingAgent
	 * @since 0.5
	 */
	void setEmitterReceiver(RoleAddress emitter, Class<? extends Role> receiver, AgentAddress receivingAgent) {
		this.group = emitter.getGroup();
		this.senderAddress = emitter;
		this.receiverRole = receiver;
		this.receiverAddress = null;
	}

	/** Set the context information.
	 * 
	 * @param group
	 * @since 0.5
	 */
	void setGroup(GroupAddress group) {
		this.group = group;
	}

}
