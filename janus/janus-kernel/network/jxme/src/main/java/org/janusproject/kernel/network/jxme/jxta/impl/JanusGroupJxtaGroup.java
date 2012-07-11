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

import net.jxta.peergroup.PeerGroup;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.network.jxme.jxta.JXTANetworkHandler;

/**
 * Implementation of a JXTA group for a Janus Group.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class JanusGroupJxtaGroup extends JanusJXTAGroup {

	private final GroupAddress janusGroupAddress;

	/**
	 * @param adapter is the adapter associated to the network support.
	 * @param peerGroup is the peer group associated to this group.
	 * @param parent is the JXTA parent group.
	 * @param janusGroup is the Janus group associated to this group.
	 */
	public JanusGroupJxtaGroup(JXTANetworkHandler adapter, PeerGroup peerGroup, JxtaGroup parent, GroupAddress janusGroup) {
		super(adapter, peerGroup, parent);
		this.janusGroupAddress = janusGroup;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Address processIncomingMessage(Message janusMessage, boolean isBroadcast) {
		Class<? extends Role> receiverRole = null;
		Address adr = janusMessage.getSender();
		if (adr instanceof RoleAddress) {
			receiverRole = ((RoleAddress)janusMessage.getReceiver()).getRole();
			return JanusGroupJxtaGroup.this.networkHandler.receiveOrganizationalDistantMessage(JanusGroupJxtaGroup.this.janusGroupAddress, receiverRole, janusMessage, isBroadcast);
		}
		return JanusGroupJxtaGroup.this.networkHandler.receiveAgentAgentDistantMessage(janusMessage, isBroadcast);		
	}


	/** Notifies that a local role was taken.
	 * 
	 * @param role is the taken role.
	 * @param agentAddress is the role player.
	 * @throws Exception
	 */
	public void informLocalRoleTaken(Class<? extends Role> role, AgentAddress agentAddress) throws Exception {
		join();
	}

	/** Notifies that a local role was released.
	 * 
	 * @param role is the released role.
	 * @param agentAddress is the role player.
	 * @throws Exception
	 */
	public void informLocalRoleReleased(Class<? extends Role> role, AgentAddress agentAddress) throws Exception {
		//FIXME: Do something when a role was released?
	}
	
	/** Replies the address of the Janus group.
	 * 
	 * @return the address of the Janus group.
	 */
	public GroupAddress getJanusGroupAddress() {
		return this.janusGroupAddress;
	}

}
