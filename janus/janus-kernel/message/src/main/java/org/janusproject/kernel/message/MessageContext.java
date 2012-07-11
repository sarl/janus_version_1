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
package org.janusproject.kernel.message;

import java.io.Serializable;

import org.janusproject.kernel.address.AgentAddress;

/**
 * Context of the message contains the emitter, the receiver
 * and the several other information which are not related
 * to the message content.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated see {@link Message}
 */
@Deprecated
public class MessageContext
implements Serializable, Cloneable {
	
	private static final long serialVersionUID = -6472437613674609104L;

	/**
	 * Address of the sender entity.
	 */
	AgentAddress sender;
	
	/**
	 * Address of the Receiver entity.
	 */
	AgentAddress receiver;
	
	/**
	 * Message creation date
	 */
	float creationDate;
	
	/**
	 * @param sender is the address of the message sender.
	 * @param receiver is the address of the message receiver.
	 * @param creationDate is the date of creation of the message.
	 */
	public MessageContext(AgentAddress sender, AgentAddress receiver, float creationDate) {
		this.sender = sender;
		this.receiver = receiver;
		this.creationDate = creationDate;
	}
	
	/**
	 * @param creationDate is the date of creation of the message.
	 */
	public MessageContext(float creationDate) {
		this.sender = null;
		this.receiver = null;
		this.creationDate = creationDate;
	}

	/**
	 */
	public MessageContext() {
		this.sender = null;
		this.receiver = null;
		this.creationDate = Float.NaN;
	}

	/**
	 * Replies the address of the receiver.
     * <p>
     * If the replied address is <code>null</code>
     * it means that the message was not sent to
     * a specific agent, eg. broadcasted message.
	 * 
	 * @return the address of the receiver, or <code>null</code> if
	 * the receiver is not known.
	 */
	public final AgentAddress getReceiver() {
		return this.receiver;
	}

	/**
	 * Replies the address of the sender.
	 * 
	 * @return the address of the sender.
	 */
	public final AgentAddress getSender() {
		return this.sender;
	}

	/**
	 * Replies the creation date.
	 * 
	 * @return the creation date.
	 */
	public final float getCreationDate() {
		return this.creationDate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) ((long)this.creationDate ^ ((long)this.creationDate >>> 32));
		result = prime * result
				+ ((this.receiver == null) ? 0 : this.receiver.hashCode());
		result = prime * result + ((this.sender == null) ? 0 : this.sender.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageContext other = (MessageContext) obj;
		if (this.creationDate != other.creationDate)
			return false;
		if (this.receiver == null) {
			if (other.receiver != null)
				return false;
		}
		else if (!this.receiver.equals(other.receiver))
			return false;
		if (this.sender == null) {
			if (other.sender != null)
				return false;
		}
		else if (!this.sender.equals(other.sender))
			return false;
		return true;
	}
		
}