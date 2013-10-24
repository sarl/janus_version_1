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
package org.janusproject.demos.market.simple.agent;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.simple.providing.PBroker;
import org.janusproject.demos.market.simple.providing.ProvidingOrganization;
import org.janusproject.demos.market.simple.purchase.CBroker;
import org.janusproject.demos.market.simple.purchase.PurchaseOrganization;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
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
		fixedParameters={}
)
public class BrokerAgent extends Agent {

	private static final long serialVersionUID = 6706121180015743382L;

	/**
	 */
	public BrokerAgent() {
		//
	}
	
	@Override
	public Status activate(Object... parameters) {
		//Client side
		print(Locale.getString(BrokerAgent.class, "LAUNCHING")); //$NON-NLS-1$
		
		GroupAddress clientGA = getOrCreateGroup(PurchaseOrganization.class);
		
		if (requestRole(CBroker.class,clientGA)==null) {
			return StatusFactory.cancel(this);
		}
		
		//Provider side
		GroupAddress providerGA = getOrCreateGroup(ProvidingOrganization.class);
		
		if (requestRole(PBroker.class,providerGA)==null) {
			return StatusFactory.cancel(this);
		}
		
		return StatusFactory.ok(this);
	}

}
