/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.schedule.Activator;
import org.janusproject.kernel.schedule.DefaultScheduler;
import org.janusproject.kernel.schedule.Scheduler;

/**
 * Agent owning its execution resource and scheduling activators.
 * 
 * @param <A> is the type of supported activator.
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SchedulerAgent<A extends Activator<?>>
extends ActivatorAgent<Scheduler<A>> {

	private static final long serialVersionUID = -6891334390696460554L;

	/**
	 * Create a new non-compound agent
	 *
	 * @param scheduler is the scheduler to use.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public SchedulerAgent(Scheduler<A> scheduler, Boolean commitSuicide) {
		super(scheduler, commitSuicide);
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param scheduler is the scheduler to use.
	 */
	public SchedulerAgent(Scheduler<A> scheduler) {
		super(scheduler);
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param scheduler is the scheduler to use.
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public SchedulerAgent(Scheduler<A> scheduler, CapacityContainer capacityContainer, Boolean commitSuicide) {
		super(scheduler, capacityContainer, commitSuicide);
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param scheduler is the scheduler to use.
	 * @param capacityContainer is the container of capacities.
	 */
	public SchedulerAgent(Scheduler<A> scheduler, CapacityContainer capacityContainer) {
		super(scheduler, capacityContainer);
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param scheduler is the scheduler to use.
	 * @param address is a precomputed address to give to this agent.
	 */
	public SchedulerAgent(Scheduler<A> scheduler, AgentAddress address) {
		super(scheduler, address);
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param scheduler is the scheduler to use.
	 * @param address is a precomputed address to give to this agent.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public SchedulerAgent(Scheduler<A> scheduler, AgentAddress address, Boolean commitSuicide) {
		super(scheduler, address, commitSuicide);
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param scheduler is the scheduler to use.
	 * @param address is a precomputed address to give to this agent.
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public SchedulerAgent(Scheduler<A> scheduler, AgentAddress address, CapacityContainer capacityContainer, Boolean commitSuicide) {
		super(scheduler, address, capacityContainer, commitSuicide);
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param scheduler is the scheduler to use.
	 * @param address is a precomputed address to give to this agent.
	 * @param capacityContainer is the container of capacities.
	 */
	public SchedulerAgent(Scheduler<A> scheduler, AgentAddress address, CapacityContainer capacityContainer) {
		super(scheduler, address, capacityContainer);
	}

	/**
	 * Create a new non-compound agent
	 *
	 * @param type is the type of the activators which are scheduled by this agent.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public SchedulerAgent(Class<A> type, Boolean commitSuicide) {
		this(new DefaultScheduler<A>(type), commitSuicide);
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param type is the type of the activators which are scheduled by this agent.
	 */
	public SchedulerAgent(Class<A> type) {
		this(new DefaultScheduler<A>(type));
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param type is the type of the activators which are scheduled by this agent.
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public SchedulerAgent(Class<A> type, CapacityContainer capacityContainer, Boolean commitSuicide) {
		this(new DefaultScheduler<A>(type), capacityContainer, commitSuicide);
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param type is the type of the activators which are scheduled by this agent.
	 * @param capacityContainer is the container of capacities.
	 */
	public SchedulerAgent(Class<A> type, CapacityContainer capacityContainer) {
		this(new DefaultScheduler<A>(type), capacityContainer);
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param type is the type of the activators which are scheduled by this agent.
	 * @param address is a precomputed address to give to this agent.
	 */
	public SchedulerAgent(Class<A> type, AgentAddress address) {
		this(new DefaultScheduler<A>(type), address);
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param type is the type of the activators which are scheduled by this agent.
	 * @param address is a precomputed address to give to this agent.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public SchedulerAgent(Class<A> type, AgentAddress address, Boolean commitSuicide) {
		this(new DefaultScheduler<A>(type), address, commitSuicide);
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param type is the type of the activators which are scheduled by this agent.
	 * @param address is a precomputed address to give to this agent.
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public SchedulerAgent(Class<A> type, AgentAddress address, CapacityContainer capacityContainer, Boolean commitSuicide) {
		this(new DefaultScheduler<A>(type), address, capacityContainer, commitSuicide);
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param type is the type of the activators which are scheduled by this agent.
	 * @param address is a precomputed address to give to this agent.
	 * @param capacityContainer is the container of capacities.
	 */
	public SchedulerAgent(Class<A> type, AgentAddress address, CapacityContainer capacityContainer) {
		this(new DefaultScheduler<A>(type), address, capacityContainer);
	}

}
