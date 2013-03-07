/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
import java.util.UUID;

import org.janusproject.kernel.address.Address;

/**
 * This class precises the minimal set of attributes required by a message to be send and receive.
 * <p>
 * Caution 1: To improve performances, when the message is broadcasted, the sent 
 * Message instance is not cloned. All local receiving mailboxes receive the same
 * Message instance. 
 * <p>
 * Caution 2: According to Caution 1, it is assumed that all Message instances are 
 * read-only data-structures (as String for example). You should not override 
 * the Message class and provide public setters. If you provide such setter 
 * functions, it means that any agent/role receiving the message is able to 
 * change the content of the message, and influences in a wrong way the 
 * behaviours of the other receivers.
 * <p>
 * Caution 3: When broadcasted, the receiver address in the message is always <code>null</code>.
 *
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Message
implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 8380395067046323895L;

	/**
	 * Unique Id for the message.
	 */
	private final UUID id = UUID.randomUUID();

	/**
	 * Address of the sender entity.
	 */
	Address sender = null;
	
	/**
	 * Address of the Receiver entity.
	 */
	Address receiver = null;
	
	/**
	 * Message creation date
	 */
	float creationDate = Float.NaN;

	/**
	 */
	public Message() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message clone() {
		try {
			return (Message)super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	/**
	 * Replies the unique identifier of this message.
	 * 
	 * @return the unique identifier of this message.
	 */
	public final UUID getIdentifier() {
		return this.id;
	}

	/**
	 * Replies the address of the receiver.
	 * <p>
	 * The replied address will be an
	 * {@code AgentAddress} in an agent context,
	 * or a {@code RoleAddress} in an organizational
	 * context. No other type of address will be
	 * returned.
     * <p>
     * If the replied address is <code>null</code>
     * it means that the message was not sent to
     * a specific agent, eg. broadcasted message.
	 * 
	 * @return the address of the receiver, or <code>null</code> if
	 * the receiver is not known.
	 */
	@SuppressWarnings("unchecked")
	public final <A extends Address> A getReceiver() {
		return (A)this.receiver;
	}
	
	/**
	 * Replies the address of the sender.
	 * <p>
	 * The replied address will be an
	 * {@code AgentAddress} in an agent context,
	 * or a {@code RoleAddress} in an organizational
	 * context. No other type of address will be
	 * returned.
	 * 
	 * @param <A> is the type of the address.
	 * @return the address of the sender.
	 */
	@SuppressWarnings("unchecked")
	public final <A extends Address> A getSender() {
		return (A)this.sender;
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
	public String toString() {
		return this.id.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Message) {
			Message m = (Message)o;
			return (this.id.equals(m.id));
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id.hashCode();
		return result;
	}
		
}
