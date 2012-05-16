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

import java.util.Set;
import java.util.TreeSet;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.ecoresolution.message.EcoInitializationDoneMessage;
import org.janusproject.ecoresolution.message.EcoProblemSolverPresentationMessage;
import org.janusproject.ecoresolution.message.EcoProblemSolvingStartMessage;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** The agent which is waiting for the eco-agent initialization to start the problem solving.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={}
)
class EcoMonitorAgent extends Agent implements KernelListener {
	
	private static final long serialVersionUID = 2587335486193699494L;
	
	private final int ecoAgentCount;
	private State state;
	private final Set<AgentAddress> addresses = new TreeSet<>();
	
	/**
	 * @param ecoAgentCount is the number of agents awaited to solve the problem.
	 */
	public EcoMonitorAgent(int ecoAgentCount) {
		setName(Locale.getString(EcoMonitorAgent.class, "NAME")); //$NON-NLS-1$
		this.state = State.STARTING;
		this.ecoAgentCount = ecoAgentCount;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		Kernel k = Kernels.get(getKernelContext().getKernelAgent());
		assert(k!=null);
		k.addKernelListener(this);
		return StatusFactory.ok(this);
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
			for(Message msg : getMailbox()) {
				if (msg instanceof EcoInitializationDoneMessage) {
					this.addresses.add(msg.getSender());
				}
			}
			
			if (this.addresses.size()==this.ecoAgentCount) {
				this.state = State.PROBLEM_INITIALIZED;
			}
			
			break;
		}
		case PROBLEM_INITIALIZED:
		{
			broadcastMessage(new EcoProblemSolvingStartMessage(), this.addresses);
			this.addresses.clear();
			killMe();
			break;
		}
		}
		return StatusFactory.ok(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status end() {
		Kernel k = Kernels.get(getKernelContext().getKernelAgent());
		assert(k!=null);
		k.removeKernelListener(this);
		return StatusFactory.ok(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void agentKilled(KernelEvent event) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void agentLaunched(KernelEvent event) {
		if (this.state==State.PROBLEM_INITIALIZING) {
			this.state = State.STARTING;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exceptionUncatched(Throwable error) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void kernelAgentKilled(KernelEvent event) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void kernelAgentLaunched(KernelEvent event) {
		//
	}

	/** States of the monitoring agent.
	 * 
	 * @author $Author: sgalland$
	 * @author $Author: ngaud$
	 * @version $Name$ $Revision$ $Date$
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
		
		/** Problem was initialized.
		 */
		PROBLEM_INITIALIZED;
		
	}
	
}