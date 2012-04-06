/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010, 2012 Janus Core Developers
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
package org.janusproject.demos.market.selective.agent;


import org.janusproject.demos.market.selective.capacity.FindLowestCostProposalCapacityImpl;
import org.janusproject.demos.market.selective.capacity.FindShortestTimeProposalCapacityImpl;
import org.janusproject.demos.market.selective.providing.PBroker;
import org.janusproject.demos.market.selective.providing.ProvidingOrganization;
import org.janusproject.demos.market.selective.purchase.CBroker;
import org.janusproject.demos.market.selective.purchase.PurchaseOrganization;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={Integer.class}
)
public class BrokerAgent extends Agent {

	private static final long serialVersionUID = -1353488844272854623L;
	
	private int providerCount;
	
	/**
	 * 
	 */
	public BrokerAgent() {
		//
	}
	
	@Override
	public Status activate(Object... params) {
		
		this.providerCount = (Integer)params[0];
		
		//Capacity initialization : adding the capacity required by the PBroker role
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new FindLowestCostProposalCapacityImpl());
		cc.addCapacity(new FindShortestTimeProposalCapacityImpl());		
		
		//Client side
		GroupAddress clientGA = getOrCreateGroup(PurchaseOrganization.class);
		
		if (requestRole(CBroker.class, clientGA)==null) {
			return StatusFactory.cancel(this);
		}
		
		//Provider side
		GroupAddress providerGA = getOrCreateGroup(ProvidingOrganization.class);
		
		if (requestRole(PBroker.class, providerGA, this.providerCount)==null) {
			return StatusFactory.cancel(this);
		}
		
		return StatusFactory.ok(this);
	}
	
}
