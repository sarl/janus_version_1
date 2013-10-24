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
package org.janusproject.kernel.network.jxme.jxta.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import net.jxta.id.ID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.network.NetworkAdapter;
import org.janusproject.kernel.network.NetworkListener;
import org.janusproject.kernel.network.jxme.jxta.JXTANetworkHandler;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.throwable.Throwables;

/**
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractJxtaNetworkAdapter implements NetworkAdapter, JXTANetworkHandler {
	
	private static final ReentrantLock networkConnectLock = new ReentrantLock();
	

	private final ExecutorService executors;
	private final Map<GroupAddress, JanusGroupJxtaGroup> groups = new ConcurrentHashMap<GroupAddress, JanusGroupJxtaGroup>();
	
	private AgentAddress kernelAddress = null;
	private JanusProperties janusProperties = null;
	private NetworkListener listener = null;
	private RendezVousService rendezvousNPG = null;
	private ApplicationJxtaGroup applicationGroup = null;

	/**
	 */
	public AbstractJxtaNetworkAdapter() {
		this.executors = Executors.newCachedThreadPool();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void fireUncatchedError(Throwable error) {
		if (error!=null && this.listener!=null) {
			this.listener.networkError(error);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void fireLogMessage(String message) {
		if (message!=null && !message.isEmpty() && this.listener!=null) {
			this.listener.networkLog(message);
		}
	}

	/**
	 * Replies the executors supported by this network adapter.
	 * 
	 * @return the executors supported by this network adapter.
	 */
	@Override
	public ExecutorService getJXTAExecutorService() {
		return this.executors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdownNetwork() throws Exception {
		this.executors.shutdownNow();
	}

	/** Replies the address of the kernel agent which is running
	 * the network support.
	 * 
	 * @return the address of the kernel agent.
	 */
	@Override
	public AgentAddress getKernelAddress() {
		return this.kernelAddress;
	}

	/** Set the address of the kernel agent which is running
	 * the network support.
	 * 
	 * @param kernelAddress is the address of the kernel agent.
	 */
	void setKernelAddress(AgentAddress kernelAddress) {
		this.kernelAddress = kernelAddress;
	}

	/** Set the application group supported by this network adapter.
	 * 
	 * @param group is the JXTA application group.
	 */
	void setApplicationGroup(ApplicationJxtaGroup group) {
		this.applicationGroup = group;
	}

	/** Set the JXTA rendez-vous to use.
	 * 
	 * @param rdv
	 */
	void setRendezVous(RendezVousService rdv) {
		this.rendezvousNPG = rdv;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNetworkAdapterListener(NetworkListener listener) {
		this.listener = listener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status informLocalGroupCreated(GroupAddress ga, Collection<? extends GroupCondition> obtainConditions, Collection<? extends GroupCondition> leaveConditions, MembershipService membership) {
		if (this.groups.get(ga) == null) {
			try {
				createJanusJxtaGroup(ga, obtainConditions, leaveConditions, membership);
			}
			catch (Exception e) {
				return StatusFactory.error(this, e.getLocalizedMessage(), e);
			}
		}
		return StatusFactory.ok(this);
	}

	/** {@inheritDoc}
	 */
	@Override
	public abstract void initializeNetwork(AgentAddress kernelAddress, JanusProperties properties) throws Exception;

	/** {@inheritDoc}
	 */
	@Override
	public Status informLocalGroupRemoved(GroupAddress ga) {
		//FIXME Do something here
		return StatusFactory.cancel(this);
	}

	/**
	 * Creates the application jxta group. This is the top level peer group for each application.
	 * 
	 * @param properties are the current janus properties from the kernel.
	 * @param parent is the parent JXTA group of the application group.
	 * @return the application JXTA group.
	 * @throws Exception 
	 * @throws URISyntaxException 
	 */
	protected ApplicationJxtaGroup createApplicationGroup(JanusProperties properties, JxtaGroup parent) throws Exception {
		String janusAppName = properties.getProperty(JanusProperty.JANUS_APPLICATION_NAME);
		String appID = properties.getProperty(JanusProperty.JXTA_APPLICATION_ID);
		PeerGroup ppg = parent.getPeerGroup();
		if (appID == null) {
			appID = Utils.createPeerGroupID(ppg.getPeerGroupID(), janusAppName).toString();
		}				
		PeerGroupAdvertisement pga = PeerGroupUtil.createJXTA(
				ppg, 
				janusAppName, 
				Locale.getString(ApplicationJxtaGroup.class,
						"GROUP_NAME", janusAppName), //$NON-NLS-1$
				null, // TODO: set the JXTA password
				0,
				(PeerGroupID)(appID == null ? null : ID.create(new URI(appID))));
		return new ApplicationJxtaGroup(this, ppg.newGroup(pga), parent);		
	}

	/**
	 * Creates the Janus organizational jxta group.
	 * 
	 * @param ga is the address of the Janus group associated to the new JXTA group.
	 * @param obtainConditions are the conditions to enter in the Janus group.
	 * @param leaveConditions are the conditions to leave from the Janus group.
	 * @param membership is the membership checker of the Janus group.
	 * @return the organizational JXTA group.
	 * @throws Exception 
	 * @throws URISyntaxException 
	 */
	protected JanusGroupJxtaGroup createJanusJxtaGroup(GroupAddress ga, Collection<? extends GroupCondition> obtainConditions, Collection<? extends GroupCondition> leaveConditions, MembershipService membership) throws Exception {
		JanusGroupJxtaGroup g = this.groups.get(ga);
		if (g == null) {			
			List<PeerGroupAdvertisement> list = PeerGroupUtil.getJanusGroupAdvs(this.applicationGroup.getPeerGroup(), ga.getUUID());
			String peerGroupId = null;
			if (list != null && !list.isEmpty()) {
				peerGroupId = list.get(0).getID().toURI().toString();
			}
			PeerGroup ppg = this.applicationGroup.getPeerGroup();
			PeerGroupAdvertisement pga = PeerGroupUtil.createJanus(
					ppg, ga.getDescription(), null, 0, 
					(PeerGroupID)(peerGroupId == null ? null : ID.create(new URI(peerGroupId))), 
					ga, obtainConditions, leaveConditions, membership);
			g = new JanusGroupJxtaGroup(this, ppg.newGroup(pga), this.applicationGroup, ga);			
			this.groups.put(ga, g);
		}
		return g;
	}

	/**
	 * Invoked when an event occured on a rendez-vous.
	 * 
	 * @param event
	 */
	@SuppressWarnings("static-method")
	public void rendezvousEvent(RendezvousEvent event) {
		if (event.getType() == RendezvousEvent.RDVCONNECT || event.getType() == RendezvousEvent.RDVRECONNECT || event.getType() == RendezvousEvent.BECAMERDV) {
			synchronized (networkConnectLock) {
				networkConnectLock.notify();
			}
		}
	}

	/**
	 * Blocks only, if not connected to a rendezvous, or until a connection to rendezvousNPG node occurs.
	 * 
	 * @param timeout
	 *            timeout in milliseconds, a zero timeout of waits forever
	 * @return true if connected to a rendezvous, false otherwise
	 */
	protected boolean waitForRendezvousConnection(long timeout) {
		long timeoutCopy = timeout;

		if (0 == timeoutCopy) {
			timeoutCopy = Long.MAX_VALUE;
		}

		long timeoutAt = System.currentTimeMillis() + timeoutCopy;

		if (timeoutAt <= 0) {
			// handle overflow.
			timeoutAt = Long.MAX_VALUE;
		}

		assert(this.rendezvousNPG!=null);
		
		while (!this.rendezvousNPG.isConnectedToRendezVous() && !this.rendezvousNPG.isRendezVous()) {
			try {
				long waitFor = timeoutAt - System.currentTimeMillis();

				if (waitFor > 0) {
					synchronized (networkConnectLock) {
						networkConnectLock.wait(timeoutCopy);
					}
				} else {
					// all done with waiting.
					break;
				}
			}
			catch (InterruptedException e) {
				Thread.interrupted();
				break;
			}
		}

		return this.rendezvousNPG.isConnectedToRendezVous() || this.rendezvousNPG.isRendezVous();
	}

	/** Notifies listeners that a distant group was discovered.
	 * 
	 * @param organizationClass is the name of the organization, ie. its classname.
	 * @param uuid is the identifier of the distant group.
	 * @param groupName is the name of the distant group.
	 * @param obtainConditions are the conditions to enter in the distant group.
	 * @param leaveConditions are the conditions to leave from the distant group.
	 * @param membership is the membership checker of the distant group.
	 * @param advertisement is the JXTA advertisement.
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void informDistantGroupDiscovered(String organizationClass, UUID uuid, String groupName, Collection<? extends GroupCondition> obtainConditions, Collection<? extends GroupCondition> leaveConditions, MembershipService membership, PeerGroupAdvertisement advertisement) throws ClassNotFoundException {
		if (this.listener!=null) {
			Class<? extends Organization> org = (Class<? extends Organization>) Class.forName(organizationClass);
			this.listener.distantGroupDiscovered(org, uuid, obtainConditions, leaveConditions, membership, Boolean.valueOf(this.janusProperties.getProperty(JanusProperty.GROUP_PERSISTENCE)), groupName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress sendMessage(Message message) {
		Address adr = message.getSender();
		if (adr instanceof RoleAddress) {
			JanusGroupJxtaGroup g = this.groups.get(((RoleAddress)adr).getGroup());
			try {
				return g.sendMessage(message);
			}
			catch (IOException e) {
				fireUncatchedError(e);
			}
			return null;
		}
		
		//Send message from agent to agent, outside organizational context
		try {
			return this.applicationGroup.sendMessage(message);
		}
		catch (IOException e) {
			fireUncatchedError(e);
			return null;
		}			
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isRemoteAddress(GroupAddress groupAddress, AgentAddress address) {
		//FIXME do smthg
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void informLocalRoleTaken(GroupAddress groupAddress, Class<? extends Role> role, AgentAddress agentAddress) {
		try {
			JanusGroupJxtaGroup jg = this.groups.get(groupAddress);
			
			//FIXME see what we should if the group doesn't exist since GroupDescription is required to create a group. check if we already joined the group
			if (jg == null) {				
				// Try to get a role in a non exisiting group
				throw new IllegalStateException();
			}
			jg.informLocalRoleTaken(role, agentAddress);

		}
		catch (AssertionError ae) {
			throw ae;
		}
		catch (Throwable e) {
			fireUncatchedError(e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void informLocalRoleReleased(GroupAddress groupAddress, Class<? extends Role> role, AgentAddress agentAddress) {
		Logger logger = Logger.getLogger(getClass().getName());
		try {
			JanusGroupJxtaGroup jg = this.groups.get(groupAddress);
			
			//FIXME see what we should if the group doesn't exist since GroupDescription is required to create a group. check if we already joined the group
			if (jg == null) {
				// Try to leave a role in a non exisiting group
				throw new IllegalStateException();
			}

			jg.informLocalRoleReleased(role, agentAddress);

		}
		catch (AssertionError ae) {
			throw ae;
		}
		catch (Throwable e) {
			logger.severe(Throwables.toString(e));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void broadcastMessage(Message message) {
		Address adr = message.getSender();
		if (adr instanceof RoleAddress) {
			JanusGroupJxtaGroup g = this.groups.get(((RoleAddress)adr).getGroup());
			try {
				g.broadcastMessage(message);
			}
			catch (IOException e) {
				fireUncatchedError(e);
			}
		}
		else {
			//broadcast the message to the distant agents, outside any organizational context.
			try {
				this.applicationGroup.broadcastMessage(message);
			}
			catch (IOException e) {
				fireUncatchedError(e);
			}
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public RoleAddress getRemoteAddress(GroupAddress groupAddress) {
		// FIXME: implement this function
		return null;
	}

	/**
	 * Invoked each time an organizational message was received from a distant kernel.
	 * 
	 * @param group is the address of the group in which the received message is.
	 * @param receiverRole is the role of the message receiver.
	 * @param message is the message itself.
	 * @param isBroadcast indicates if the message was sent in brocast mode, or not.
	 * @return the adress of the agent who has received the message
	 */
	@Override
	public RoleAddress receiveOrganizationalDistantMessage(GroupAddress group, Class<? extends Role> receiverRole, Message message, boolean isBroadcast) {
		return this.listener.receiveOrganizationalDistantMessage(group, receiverRole, message, isBroadcast);
	}
	
	/**
	 * Invoked each time a non-organizational message was received from a distant kernel.
	 * 
	 * @param message is the message itself.
	 * @param isBroadcast indicates if the message was sent in brocast mode, or not.
	 * @return the adress of the agent who has received the message
	 */
	@Override
	public AgentAddress receiveAgentAgentDistantMessage(Message message, boolean isBroadcast) {
		return this.listener.receiveAgentAgentDistantMessage(message, isBroadcast);
	}
	
	
	/** {@inheritDoc}
	 */
	@Override
	public void setJanusProperties(JanusProperties properties) {
		this.janusProperties = properties;
	}

}
