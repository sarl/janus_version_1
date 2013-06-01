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
package org.janusproject.kernel.network;

import java.util.Collection;
import java.util.EventListener;
import java.util.UUID;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.message.Message;

/**
 * Listener on network events.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface NetworkListener extends EventListener {

	/**
	 * Invoked each time a group was appeared on  a distant kernel.
	 * 
	 * @param organization is the organization of the new group.
	 * @param id is the identifier of the distant group.
	 * @param obtainConditions are the conditions to enter in the distant group.
	 * @param leaveConditions are the conditions to leave from the distant group.
	 * @param membership describes how the distant group accept members.
	 * @param persistent indicates if the distant group is persistent or not.
	 * @param groupName is the name associated to the distant group.
	 */
	public void distantGroupDiscovered(Class<? extends Organization> organization, UUID id, Collection<? extends GroupCondition> obtainConditions, Collection<? extends GroupCondition> leaveConditions, MembershipService membership, boolean persistent, String groupName);

	/**
	 * Invoked each time an organizational message was received from a distant kernel.
	 * 
	 * @param group is the address of the group in which the received message is.
	 * @param receiverRole is the role of the message receiver.
	 * @param message is the message itself.
	 * @param isBroadcast indicates if the message was sent in brocast mode, or not.
	 * @return the adress of the agent who has received the message
	 */
	public RoleAddress receiveOrganizationalDistantMessage(GroupAddress group, Class<? extends Role> receiverRole, Message message, boolean isBroadcast);
	
	
	/**
	 * Invoked each time a non-organizational message was received from a distant kernel.
	 * 
	 * @param message is the message itself.
	 * @param isBroadcast indicates if the message was sent in brocast mode, or not.
	 * @return the adress of the agent who has received the message
	 */
	public AgentAddress receiveAgentAgentDistantMessage(Message message, boolean isBroadcast);

	/**
	 * Invoked when an error occurs in the network layer.
	 * 
	 * @param e
	 */
	public void networkError(Throwable e);

	/**
	 * Invoked when a message should be logged.
	 * 
	 * @param message
	 */
	public void networkLog(String message);

}
