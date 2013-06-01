/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2013 Janus Core Developers
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

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.DistantKernelHandler;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.status.Status;

/**
 * Handles relations with known distant kernels.
 * <p>
 * The Kernel strongly relays on this implementation to deliver messages to
 * agents hosted by other kernels.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface NetworkAdapter extends DistantKernelHandler {

	/**
	 * Initialize all required resources.
	 * 
	 * @param kernelAddress 
	 * @param properties 
	 * @throws Exception 
	 */
	public void initializeNetwork(AgentAddress kernelAddress, JanusProperties properties) throws Exception;

	/**
	 * Shutdown the network adapter, release all related resources, etc.
	 * 
	 * @throws Exception 
	 */
	public void shutdownNetwork() throws Exception;

	/**
	 * Request the adapter to inform distant kernels that a new group has been
	 * created locally.
	 * 
	 * @param ga
	 *            The address of the new group
	 * @param obtainConditions are the obtain conditions of the group.
	 * @param leaveConditions are the leave conditions of the group.
	 * @param membership describes access to the group.
	 * @return {@link Status#isSuccess()} == true if the message was sent
	 *         properly.
	 */
	public Status informLocalGroupCreated(GroupAddress ga, Collection<? extends GroupCondition> obtainConditions, Collection<? extends GroupCondition> leaveConditions, MembershipService membership);

	/**
	 * Request the adapter to inform distant kernels that the group was removed
	 * locally.
	 * 
	 * @param ga
	 * @return {@link Status#isSuccess()} == true if the message was sent
	 *         properly.
	 */
	public Status informLocalGroupRemoved(GroupAddress ga);

	/**
	 * Request the adapter to inform distant kernels that the agent was added
	 * locally.
	 * 
	 * @param agentAdress
	 * @since 1.0
	 */
	public void informLocalAgentAdded(AgentAddress agentAdress);

	/**
	 * Request the adapter to inform distant kernels that the agent was removed
	 * locally.
	 * 
	 * @param agentAddress
	 * @since 1.0
	 */
	public void informLocalAgentRemoved(AgentAddress agentAddress);

	/**
	 * Set the listener associated to this adapter.
	 * 
	 * @param listener - the listener associated to this adapter 
	 */
	public void setNetworkAdapterListener(NetworkListener listener);
	
	/**
	 * Set the support for managing janus specific properties.
	 * 
	 * @param properties - the support for managing janus specific properties
	 */
	public void setJanusProperties(JanusProperties properties);
}
