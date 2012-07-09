/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.agent;

import java.util.Collection;
import java.util.Iterator;

import org.janusproject.kernel.schedule.AbstractActivator;
import org.janusproject.kernel.schedule.ActivationStage;
import org.janusproject.kernel.status.ExceptionStatus;
import org.janusproject.kernel.status.KernelStatusConstants;
import org.janusproject.kernel.status.MultipleStatus;
import org.janusproject.kernel.status.SingleStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.status.StatusSeverity;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.directaccess.SafeIterator;

/**
 * Determine a execution policy among a set of agents.
 * <p>
 * The activator is empty when no more agent is registered inside.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentActivator
extends AbstractActivator<Agent> {

	/** 
	 */
	public AgentActivator() {
		super(Agent.class);
	}

	/**
	 * @param scheduledAgents is the list of scheduled agents
	 */
	public AgentActivator(Collection<? extends Agent> scheduledAgents) {
		super(Agent.class, scheduledAgents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeInit(Iterator<? extends Agent> agents, Object... parameters) {
		MultipleStatus ms = new MultipleStatus();
		Agent h;
		Status s;
		while (agents.hasNext()) {
			h = agents.next();
			if (h.getState()==AgentLifeState.UNBORN && !h.isMigrating.get()) {
				try {
					s = h.proceedPrivateInitialization(parameters);
				}
				catch(AssertionError e) {
					throw e;
				}
				catch(Throwable e) {
					s = new ExceptionStatus(e);
				}
			}
			else {
				s = StatusFactory.ok(h);
			}
			ms.addStatus(s);
		}
		return ms.pack(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeBehaviour(Iterator<? extends Agent> agents) {
		MultipleStatus ms = new MultipleStatus();
		Agent h;
		Status s;
		while (agents.hasNext()) {
			h = agents.next();
			s = null;
			switch(h.getState()) {
			case ALIVE:
				// standard execution
				try {
					if (!h.wakeUpIfSleeping()) {
						s = h.proceedPrivateBehaviour();
					}
				}
				catch(AssertionError e) {
					throw e;
				}
				catch(Throwable e) {
					s = new ExceptionStatus(e);
				}
				break;
			case DYING:
				// kill the agent
				s = killAgent(h);
				break;
			case DIED:
				// Special case of the failure in the activation function
				killAgent(h);
				break;
			case UNBORN:
			case BORN:
				// void states
				s = new SingleStatus(
						StatusSeverity.WARNING,
						h.getAddress().toString(),
						KernelStatusConstants.UNEXPECTED_AGENT_STATE_DURING_ACTIVATION);
				killAgent(h);
				break;
			case BREAKING_DOWN:
				// void states
				s = new SingleStatus(
						StatusSeverity.WARNING,
						h.getAddress().toString(),
						KernelStatusConstants.UNEXPECTED_AGENT_STATE_DURING_DESTRUCTION);
				killAgent(h);
				break;
			default:
				s = new ExceptionStatus(new IllegalStateException());
			}
			if (s!=null) ms.addStatus(s);
			Thread.yield();
		}
		return ms.pack(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeDestroy(Iterator<? extends Agent> agents) {
		MultipleStatus ms = new MultipleStatus();
		Agent h;
		while (agents.hasNext()) {
			h = agents.next();
			if (h.isAlive() && !h.isMigrating.get()) {
				try {
					ms.addStatus(h.proceedPrivateDestruction());
				}
				catch(AssertionError e) {
					throw e;
				}
				catch(Throwable e) {
					ms.addStatus(new ExceptionStatus(e));
				}
			}
			else {
				ms.addStatus(StatusFactory.ok(h));
			}
		}
		return ms.pack(this);
	}
	
	private Status killAgent(Agent agent) {
		assert(agent!=null);
		try {
			KernelAgent kernel = (agent.kernel==null) ? null : agent.kernel.get();
			Status s;
			if (kernel!=null) {
				// Agent it may not be activated any more 
				removeActivableObject(agent);
				s = kernel.removeAgentFromKernel(agent);
			}
			else {
				s = new SingleStatus(
						StatusSeverity.FAILURE,
						agent.getAddress().toString(),
						KernelStatusConstants.NO_KERNEL_AGENT);
			}
			return s;
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable e) {
			return new ExceptionStatus(e);
		}
	}
	
	/** Add an agent to activate with initialization parameters.
	 * 
	 * @param agent is the new agent.
	 * @param initParameters are the parameters to pass to <code>activate()</code>
	 */
	void addAgent(Agent agent, Object... initParameters) {
		if (initParameters==null || initParameters.length==0)
			agent.personalInitParameters = null;
		else
			agent.personalInitParameters = initParameters;
		addActivableObject(agent);
	}

	/** Remove an agent from the collection of activable agents.
	 * 
	 * @param agent is the agent to remove.
	 * @return <code>true</code> if the agent was removed; otherwise <code>false</code>.
	 */
	boolean removeAgent(Agent agent) {
		return removeActivableObject(agent);
	}

	/** Remove all the agents from the collection of activable agents.
	 */
	void removeAllAgents() {
		removeAllActivableObjects();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SafeIterator<Agent> getExecutionPolicy(ActivationStage stage,
			DirectAccessCollection<Agent> candidates) {
		return candidates.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Iterator<? extends Agent> getExecutionPolicy(
			ActivationStage stage, Collection<? extends Agent> candidates) {
		return candidates.iterator();
	}

}
