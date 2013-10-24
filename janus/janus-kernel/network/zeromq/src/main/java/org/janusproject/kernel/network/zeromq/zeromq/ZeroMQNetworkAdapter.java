/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012-2013 Janus Core Developers
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
package org.janusproject.kernel.network.zeromq.zeromq;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.network.NetworkAdapter;
import org.janusproject.kernel.network.NetworkListener;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.EmptyIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** Object that is listening on the networking events from the local kernels
 * to notify the distant kernels.
 * 
 * @author $Author: bfeld$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ZeroMQNetworkAdapter implements NetworkAdapter {

	private final ZeroMQNode node;
	private final Logger logger;
	private JanusProperties janusProperties = null;

	/**
	 * @param node is the zeromq node associated to this object.
	 */
	public ZeroMQNetworkAdapter(ZeroMQNode node) {
		super();
		this.node = node;
		this.logger = Logger.getLogger("ZeroMQAdapter"); //$NON-NLS-1$
	}

	@Override
	public SizedIterator<AgentAddress> getRemoteKernels() {
		this.logger.log(Level.SEVERE, Locale.getString("UNSUPPORTED_ACTION", "getRemoteKernels"));  //$NON-NLS-1$//$NON-NLS-2$
		return EmptyIterator.singleton();
	}

	@Override
	public void informLocalRoleTaken(GroupAddress groupAddress,
			Class<? extends Role> role, AgentAddress agentAddress) {
		this.logger.log(Level.SEVERE, Locale.getString("UNSUPPORTED_ACTION", "informLocalRoleTaken"));  //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public void informLocalRoleReleased(GroupAddress groupAddress,
			Class<? extends Role> role, AgentAddress agentAddress) {
		this.node.unsubscribe(agentAddress.getUUID());

	}

	@Override
	public Address sendMessage(Message message) {
		this.node.publish(message.getReceiver().getUUID(),
				"message", SerializationUtil.encode(message).getBytes()); //$NON-NLS-1$
		return null;
	}

	@Override
	public void broadcastMessage(Message message) {
		Address adr = message.getSender();
		if (adr instanceof RoleAddress) {
			GroupAddress group = ((RoleAddress) adr).getGroup();
			this.logger.info(Locale.getString("BROADCAST_MESSAGE", message, group.getUUID())); //$NON-NLS-1$
			this.node.publish(group.getUUID(), "broadcast", //$NON-NLS-1$
					SerializationUtil.encode(message).getBytes());
		}
	}

	@Override
	public void initializeNetwork(AgentAddress kernelAddress,
			JanusProperties properties) throws Exception {
		this.logger.info(Locale.getString("INITIALIZE_NETWORK")); //$NON-NLS-1$
		String strAdr = properties.getProperty(JanusProperty.ZEROMQ_MULICAT_GROUP_ADDRESS);
		InetAddress groupAdr = InetAddress.getByName(strAdr);
		this.node.init(kernelAddress, groupAdr);
		this.node.register();
	}

	@Override
	public void shutdownNetwork() throws Exception {
		this.node.unregister();
		this.node.destroy();
	}

	@Override
	public Status informLocalGroupCreated(GroupAddress ga,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership) {
		this.logger.info(Locale.getString("NEW_GROUP", ga.getUUID(), ga.getName())); //$NON-NLS-1$
		// Register to group address
		this.node.subscribe(ga.getUUID());

		// Send created group on network
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("organization", ga.getOrganization()); //$NON-NLS-1$
		data.put("groupId", ga.getUUID()); //$NON-NLS-1$
		data.put("groupName", ga.getName()); //$NON-NLS-1$
		data.put("obtainConditions", obtainConditions); //$NON-NLS-1$
		data.put("leaveConditions", leaveConditions); //$NON-NLS-1$
		data.put("membership", SerializationUtil.encode(membership)); //$NON-NLS-1$
		data.put("persistent", Boolean.valueOf(this.janusProperties //$NON-NLS-1$
				.getProperty(JanusProperty.GROUP_PERSISTENCE)));
		try {
			this.node.publishToApplication("localGroupCreated", data); //$NON-NLS-1$
		}
		catch (IOException e) {
			return StatusFactory.error(this, e.getLocalizedMessage(), e);
		}
		return StatusFactory.ok(this);
	}

	@Override
	public Status informLocalGroupRemoved(GroupAddress ga) {
		this.node.unsubscribe(ga.getUUID());
		return StatusFactory.ok(this);
	}

	@Override
	public void setNetworkAdapterListener(NetworkListener listener) {
		this.node.setNetworkAdapterListener(listener);
	}

	@Override
	public void setJanusProperties(JanusProperties properties) {
		this.janusProperties = properties;
		this.node.setApplicationName(properties.getProperty(JanusProperty.JANUS_APPLICATION_NAME));
	}

	// Deprecated functions

	@Override
	public boolean isRemoteAddress(GroupAddress groupAddress,
			AgentAddress address) {
		return false;
	}

	@Override
	public RoleAddress getRemoteAddress(GroupAddress groupAddress) {
		return null;
	}

	@Override
	public void informLocalAgentAdded(AgentAddress agentAdress) {
		// TODO Auto-generated method stub
	}

	@Override
	public void informLocalAgentRemoved(AgentAddress agentAddress) {
		// TODO Auto-generated method stub

	}

}
