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
package org.janusproject.demos.bdi.market.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.janusproject.demos.bdi.market.simple.agent.BrokerAgent;
import org.janusproject.demos.bdi.market.simple.agent.ClientAgent;
import org.janusproject.demos.bdi.market.simple.agent.ProviderAgent;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.bdi.BDIAgent;

/**
 * 
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Environment {
	private Map<AgentAddress, BDIAgent> agents;
	
	/**
	 */
	public Environment() {
		this.agents = new HashMap<AgentAddress, BDIAgent>();
	}

	/**
	 * @param agent
	 */
	public void addAgent(BDIAgent agent) {
		this.agents.put(agent.getAddress(), agent);
	}

	/**
	 * @return the agents
	 */
	public List<AgentAddress> getAgents() {
		return new ArrayList<AgentAddress>(this.agents.keySet());
	}

	/**
	 * @return the brokers
	 */
	public List<AgentAddress> getBrokerAgents() {
		List<AgentAddress> list = new ArrayList<AgentAddress>();
		
		for (Iterator<AgentAddress> it = this.agents.keySet().iterator(); it.hasNext();) {
			AgentAddress address = it.next();
			if (this.agents.get(address) instanceof BrokerAgent)
				list.add(address);
		}
		
		return list;
	}
	
	/**
	 * @return the providers
	 */
	public List<AgentAddress> getProviderAgents() {
		List<AgentAddress> list = new ArrayList<AgentAddress>();
		
		for (Iterator<AgentAddress> it = this.agents.keySet().iterator(); it.hasNext();) {
			AgentAddress address = it.next();
			if (this.agents.get(address) instanceof ProviderAgent)
				list.add(address);
		}
		
		return list;
	}
	
	/**
	 * @return the clients
	 */
	public List<AgentAddress> getClientAgents() {
		List<AgentAddress> list = new ArrayList<AgentAddress>();
		
		for (Iterator<AgentAddress> it = this.agents.keySet().iterator(); it.hasNext();) {
			AgentAddress address = it.next();
			if (this.agents.get(address) instanceof ClientAgent)
				list.add(address);
		}
		
		return list;
	}
}
