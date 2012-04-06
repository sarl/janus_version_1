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

import org.janusproject.demos.market.selective.providing.Provider;
import org.janusproject.demos.market.selective.providing.ProvidingOrganization;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** A provider in the market example.
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={}
)
public class ProviderAgent extends Agent {
	
	private static final long serialVersionUID = 5039268838958785484L;
	
	/**
	 * Is the number of launched providers.
	 */
	public static int providerCount;
	
	static {
		providerCount = 0;
	}
	
	/**
	 */
	public ProviderAgent() {
		providerCount++;
	}

	@Override
	public Status activate(Object... parameters) {
		GroupAddress providerGA = getOrCreateGroup(ProvidingOrganization.class);
		if(requestRole(Provider.class,providerGA)==null){
			return StatusFactory.cancel(this);
		}
		return StatusFactory.ok(this);
	}
}
