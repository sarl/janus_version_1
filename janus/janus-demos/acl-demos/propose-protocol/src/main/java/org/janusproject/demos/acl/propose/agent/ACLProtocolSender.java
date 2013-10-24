/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.demos.acl.propose.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.protocol.AbstractFipaProtocol;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.demos.acl.propose.organization.ProposeOrganization;
import org.janusproject.demos.acl.propose.role.Requester;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** Agent that is sending the proposals.
 * 
 * @author $Author: bfeld$
 * @author $Author: ngrenie$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLProtocolSender extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;

	/**
	 * The protocol's manager
	 */
	protected FipaConversationManager protocolManager;

	@Override
	public Status activate(Object... parameters) {

		this.protocolManager = new FipaConversationManager(this);
		AbstractFipaProtocol protocol = this.protocolManager.createConversation(EnumFipaProtocol.FIPA_PROPOSE, Locale.getString("INITIALIZATION")); //$NON-NLS-1$ 		
		Logger.getAnonymousLogger().log(Level.INFO, Locale.getString("PROTOCOLCREATED")); //$NON-NLS-1$ 

		SizedIterator<AgentAddress> agents = getLocalAgents();

		while (agents.hasNext()) {
			AgentAddress participant = agents.next();
			if (participant != getAddress()) {
				protocol.initiate(getAddress(), participant);
			}
		}

		GroupAddress groupAddress = getOrCreateGroup(ProposeOrganization.class);

		if (requestRole(Requester.class, groupAddress, protocol) == null) {
			return StatusFactory.cancel(this);
		}

		return StatusFactory.ok(this);
	}
}