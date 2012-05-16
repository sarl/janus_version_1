/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.ecoresolution.agent;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.janusproject.ecoresolution.problem.EcoProblem;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;

/** This class defines an eco-resolution problem based on the use
 * of agents to solve the problem.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AgentBasedEcoProblem extends EcoProblem {

	private final Set<EcoAgent> agents = new TreeSet<>(new Comparator<EcoAgent>() {
		@Override
		public int compare(EcoAgent o1, EcoAgent o2) {
			return System.identityHashCode(o1) - System.identityHashCode(o2);
		}
	});
	
	private final int ecoAgentCount;
	
	/**
	 * @param ecoAgentCount is the number of agents awaited to solve the problem.
	 */
	public AgentBasedEcoProblem(int ecoAgentCount) {
		this.ecoAgentCount = ecoAgentCount;
	}
	
	/** Initialize the given eco-entity.
	 * <p>
	 * If goal is not <code>null</code>, the goal's master entity must be the given <var>entity</var>.
	 * For each acquaintance, the relation master must be the given <var>entity</var>.
	 * <p>
	 * The given master agent is registered as an agent to launch.
	 * 
	 * @param agent is the agent which is participating to the given relation.
	 * @param goal is the goald of the entity.
	 * @param aquaintances is the knowledge to insert at startup.
	 */
	protected void init(EcoAgent agent, EcoRelation goal, EcoRelation... aquaintances) {
		init(agent.getEcoEntity(), goal, aquaintances);
		this.agents.add(agent);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public final void solve() {
		Kernel kernel = Kernels.get();
		for(EcoAgent ag : this.agents) {
			kernel.submitLightAgent(ag);
		}
		this.agents.clear();
		kernel.launchDifferedExecutionAgents();
		
		EcoMonitorAgent monitor = new EcoMonitorAgent(this.ecoAgentCount);
		kernel.launchHeavyAgent(monitor);
	}
		
}