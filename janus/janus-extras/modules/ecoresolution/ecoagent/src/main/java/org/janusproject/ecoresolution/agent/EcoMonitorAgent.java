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
package org.janusproject.ecoresolution.agent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.ecoresolution.message.EcoInitializationDoneMessage;
import org.janusproject.ecoresolution.message.EcoProblemSolvedMessage;
import org.janusproject.ecoresolution.message.EcoProblemSolverPresentationMessage;
import org.janusproject.ecoresolution.message.EcoProblemSolvingStartMessage;
import org.janusproject.ecoresolution.problem.EcoProblemMonitor;
import org.janusproject.ecoresolution.sm.EcoState;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agent.ChannelManager;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** The agent which is waiting for the eco-agent initialization to start the problem solving.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={}
)
class EcoMonitorAgent extends Agent implements KernelListener {
	
	private static final long serialVersionUID = 2587335486193699494L;
	
	private final EcoProblemMonitor monitor;
	private final int ecoAgentCount;
	private State state;
	private final Map<AgentAddress,EcoChannel> ecoAgents = new TreeMap<AgentAddress,EcoChannel>();
	
	/**
	 * @param monitor is the monitor that checks if the problem is solved.
	 * @param ecoAgentCount is the number of agents awaited to solve the problem.
	 */
	public EcoMonitorAgent(EcoProblemMonitor monitor, int ecoAgentCount) {
		setName(Locale.getString(EcoMonitorAgent.class, "NAME")); //$NON-NLS-1$
		this.monitor = monitor;
		this.state = State.STARTING;
		this.ecoAgentCount = ecoAgentCount;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		getKernelContext().getKernel().addKernelListener(this);
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized Status live() {
		switch(this.state) {
		case STARTING:
		{
			// be sure that all newly launched eco-agents received the presentation message.
			broadcastMessage(new EcoProblemSolverPresentationMessage());
			this.state = State.PROBLEM_INITIALIZING;
			break;
		}
		case PROBLEM_INITIALIZING:
		{
			AgentAddress ecoAgent;
			EcoChannel channel;
			for(Message msg : getMailbox()) {
				if (msg instanceof EcoInitializationDoneMessage) {
					ecoAgent = (AgentAddress)msg.getSender();
					channel = getKernelContext().getChannelManager().getChannel(ecoAgent, EcoChannel.class);
					this.ecoAgents.put(ecoAgent, channel);
				}
			}
			
			if (this.ecoAgents.size()==this.ecoAgentCount) {
				this.state = State.PROBLEM_SOLVING;
				broadcastMessage(new EcoProblemSolvingStartMessage(), this.ecoAgents.keySet());
			}
			else {
				broadcastMessage(new EcoProblemSolverPresentationMessage());
			}
			
			break;
		}
		case PROBLEM_SOLVING:
		{
			if (isProblemSolved()) {
				this.state = State.PROBLEM_SOLVED;
				broadcastMessage(new EcoProblemSolvedMessage(), this.ecoAgents.keySet());
			}
			break;
		}
		case PROBLEM_SOLVED:
		{
			this.ecoAgents.clear();
			killMe();
			break;
		}
		default:
			throw new IllegalStateException();
		}
		return StatusFactory.ok(this);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Status end() {
		getKernelContext().getKernel().removeKernelListener(this);
		return null;
	}
	
	private boolean isProblemSolved() {
		if (this.monitor!=null) {
			return this.monitor.isProblemSolved();
		}
		List<AgentAddress> newChannels = new LinkedList<AgentAddress>();
		EcoChannel channel;
		for(Entry<AgentAddress,EcoChannel> entry : this.ecoAgents.entrySet()) {
			channel = entry.getValue();
			if (channel==null) {
				newChannels.add(entry.getKey()); 
			}
			else if (channel.getEcoState()!=EcoState.SATISFACTED) {
				return false;
			}
		}
		
		boolean solved = true;
		ChannelManager channelManager = getKernelContext().getChannelManager();
		
		for(AgentAddress adr : newChannels) {
			channel = channelManager.getChannel(adr, EcoChannel.class);
			if (channel!=null) {
				this.ecoAgents.put(adr, channel);
				if (channel.getEcoState()!=EcoState.SATISFACTED) {
					solved = false;
				}
			}
		}
		
		return solved;
	}
	
	/** States of the monitoring agent.
	 * 
	 * @author $Author: sgalland$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum State {

		/** Notify eco-agents about this monitoring agent.
		 */
		STARTING,

		/** Awaiting eco-agent initialization.
		 */
		PROBLEM_INITIALIZING,
		
		/** Problem was initialized and the solving is under progress.
		 */
		PROBLEM_SOLVING,
		
		/** Problem was solved.
		 */
		PROBLEM_SOLVED;

	}

	/** {@inheritDoc}
	 */
	@Override
	public synchronized void agentLaunched(KernelEvent event) {
		AgentAddress newAgent = event.getAgent();
		if (!this.ecoAgents.containsKey(newAgent)) {
			// Late agent arrived
			sendMessage(new EcoProblemSolverPresentationMessage(), newAgent);
		}
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