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
import org.janusproject.ecoresolution.problem.EcoProblemMonitor;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;

/** This class defines an eco-resolution problem based on the use
 * of agents to solve the problem.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AgentBasedEcoProblem extends EcoProblem {

	private final Set<EcoAgent> agents = new TreeSet<EcoAgent>(new Comparator<EcoAgent>() {
		@Override
		public int compare(EcoAgent o1, EcoAgent o2) {
			return o1.getUUID().compareTo(o2.getUUID());
		}
	});
	
	/**
	 */
	public AgentBasedEcoProblem() {
		//
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
	public final void solve(EcoProblemMonitor monitor) {
		Kernel kernel = Kernels.get();

		int nbAgents = this.agents.size();
		LaunchingListener listener = new LaunchingListener(nbAgents);
		kernel.addKernelListener(listener);
		for(EcoAgent ag : this.agents) {
			kernel.submitLightAgent(ag);
		}
		this.agents.clear();
		kernel.launchDifferedExecutionAgents();
		
		while (listener.isAllLaunched()) {
			Thread.yield();
		}
		kernel.removeKernelListener(listener);
		
		EcoMonitorAgent monitorAgent = new EcoMonitorAgent(monitor, nbAgents);
		kernel.launchHeavyAgent(monitorAgent);
		
		
	}
	
	/** 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class LaunchingListener implements KernelListener {

		private final int nb;
		private Set<AgentAddress> launched = new TreeSet<AgentAddress>();
		
		/**
		 * @param nb
		 */
		public LaunchingListener(int nb) {
			this.nb = nb;
		}
		
		public synchronized boolean isAllLaunched() {
			if (this.launched.size()>=this.nb) {
				this.launched.clear();
				return true;
			}
			return false;
		}

		/** {@inheritDoc}
		 */
		@Override
		public synchronized void agentLaunched(KernelEvent event) {
			this.launched.add(event.getAgent());
		}

		/** {@inheritDoc}
		 */
		@Override
		public void agentKilled(KernelEvent event) {
			//
		}

		/** {@inheritDoc}
		 */
		@Override
		public boolean exceptionUncatched(Throwable error) {
			return true;
		}

		/** {@inheritDoc}
		 */
		@Override
		public void kernelAgentLaunched(KernelEvent event) {
			//
		}

		/** {@inheritDoc}
		 */
		@Override
		public void kernelAgentKilled(KernelEvent event) {
			//
		}
		
	}
	
}