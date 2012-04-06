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
package org.janusproject.kernel.crio.core;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.message.Message;

/**
 * Handles relations with known distant CRIO context.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.4
 */
public interface DistantCRIOContextHandler {

	/**
	 * Replies if an agent exists in a remote group.
	 * 
	 * @param groupAddress
	 * @param address
	 * @return <code>true</code> if an agent identified by the given address
	 * is living is a remote group identified by the given group address.
	 */
	public boolean isRemoteAddress(GroupAddress groupAddress, AgentAddress address);

	/**
	 * Informs that a local agent has taken the role <code>role</code>
	 * 
	 * @param groupAddress
	 * @param role
	 * @param agentAddress
	 */
	public void informLocalRoleTaken(GroupAddress groupAddress,
			Class<? extends Role> role, AgentAddress agentAddress);

	/**
	 * Informs that a local agent has released the role <code>role</code>
	 * 
	 * @param groupAddress
	 * @param role
	 * @param agentAddress
	 * @since 0.4
	 */
	public void informLocalRoleReleased(GroupAddress groupAddress,
			Class<? extends Role> role, AgentAddress agentAddress);

	/**
	 * Replies the address of a role player in a remote group identified
	 * by the given address.
	 * 
	 * @param groupAddress
	 * @return the address of a role player in the remote group, or <code>null</code>
	 * if no group nor player was found. 
	 */
	public RoleAddress getRemoteAddress(GroupAddress groupAddress);

	/**
	 * Sends a message to a distant kernel. The message's receiver might be
	 * <code>null</code> in which case the implementation should select a
	 * receiver agent at random.
	 * <p>
	 * The implementation must ensure that the returned AgentAddress belongs to
	 * an <b>living</b> agent at the time of the delivery.
	 * <p>
	 * The <code>MessageContext</code> of the message must contains all the
	 * information on the receiver (agent address or group address).
	 * 
	 * @param message
	 * @return the address of the agent or the role which has received the message, or <code>null</code>
	 * if no receiver was immediately found.
	 * @since 0.4
	 */
	public Address sendMessage(Message message);

	/**
	 * Broadcast the specified <code>Message</code> to all entities in the
	 * remote group identified by the given address.
	 * <p>
	 * The <code>MessageContext</code> of the message must contains all the
	 * information on the receiver (agent address or group address).
	 *
	 * @param message
	 *            is the message to broadcast
	 * @since 0.4
	 */
	public void broadcastMessage(Message message);

}
