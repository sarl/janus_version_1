/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-12 Janus Core Developers
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
package org.janusproject.kernel.network.jxme.jxta;

import java.util.Collection;
import java.util.EventListener;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import net.jxta.protocol.PeerGroupAdvertisement;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.message.Message;

/**
 * Interface linking the JXTA network linking the JXTA Network adapter with 
 * the rest of the JXTA implementation
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JXTANetworkHandler extends EventListener {

	/**
	 * Reception of a distant organizational message.
	 * 
	 * @param group is the address of the group in which the received message is.
	 * @param receiverRole is the role of the message receiver.
	 * @param message is the message itself.
	 * @param isBroadcast indicates if the message was sent in brocast mode, or not.
	 * @return the adress of the role who has received the message
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
	 * Invoked each time a group was appeared on  a distant kernel.
	 * 
	 * @param organization is the organization of the new group.
	 * @param groupId is the identifier of the distant group.
	 * @param groupName is the name associated to the distant group.
	 * @param obtainConditions are the conditions to enter in the distant group.
	 * @param leaveConditions are the conditions to leave from the distant group.
	 * @param membership describes how the distant group accept members.
	 * @param advertisement is the JXTA advertisement.
	 * @throws ClassNotFoundException
	 */
	public void informDistantGroupDiscovered(String organization, UUID groupId, String groupName, Collection<? extends GroupCondition> obtainConditions, Collection<? extends GroupCondition> leaveConditions, MembershipService membership, PeerGroupAdvertisement advertisement) throws ClassNotFoundException;
	
	/** Replies the executor service able to run some JXTA background tasks.
	 * 
	 * @return the executor service, never <code>null</code>.
	 */
	public ExecutorService getJXTAExecutorService();
		
	/** Replies address of the Janus kernel associated to this JXTA peer.
	 * 
	 * @return the current Janus kernel address.
	 */
	public AgentAddress getKernelAddress();

	/** Notifies listeners about an uncatched error.
	 * 
	 * @param error
	 * @since 0.5
	 */
	public void fireUncatchedError(Throwable error);

	/** Notifies listeners about the log of a message.
	 * 
	 * @param message
	 * @since 0.5
	 */
	public void fireLogMessage(String message);

}
