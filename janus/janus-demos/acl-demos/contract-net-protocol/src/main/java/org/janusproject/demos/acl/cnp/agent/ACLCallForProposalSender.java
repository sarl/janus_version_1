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
package org.janusproject.demos.acl.cnp.agent;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.protocol.AbstractFipaProtocol;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.demos.acl.cnp.organization.ContractNetOrganization;
import org.janusproject.demos.acl.cnp.role.ContractNetRequester;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** Agent that is sending calls for proposals.
 *
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLCallForProposalSender extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;

	/**
	 * The protocol's manager
	 */
	protected FipaConversationManager protocolManager;

	@Override
	public Status activate(Object... parameters) {
		
		this.protocolManager = new FipaConversationManager(this);
		AbstractFipaProtocol protocol =  this.protocolManager.createConversation(EnumFipaProtocol.FIPA_CONTRACT_NET, Locale.getString("INITIALIZATION")); //$NON-NLS-1$		
		Logger.getAnonymousLogger().log(Level.INFO, Locale.getString("CREATION")); //$NON-NLS-1$
		
		SizedIterator<AgentAddress> agents = getLocalAgents();	
		
		ArrayList<AgentAddress> participants = new ArrayList<AgentAddress>();		
		while (agents.hasNext()) 
		{
			AgentAddress participant = agents.next();			
			if ( participant != getAddress() ) 
			{
				participants.add(participant);
			}
		}
		
		protocol.initiate(getAddress(), participants);
		
		GroupAddress groupAddress = getOrCreateGroup(ContractNetOrganization.class);	
		
		if (requestRole( ContractNetRequester.class, groupAddress, protocol )==null) {
			return StatusFactory.cancel(this);
		}
		
		return StatusFactory.ok(this);
	}
}