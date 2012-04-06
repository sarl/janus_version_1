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

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.schedule.Activator;
import org.janusproject.kernel.status.MultipleStatus;
import org.janusproject.kernel.status.Status;

/**
 * Agent owning its execution resource and scheduling activable objects.
 * 
 * @param <A> is the type of supported activator.
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		variableParameters=Object.class
)
public class ActivatorAgent<A extends Activator<?>>
extends Agent {

	private static final long serialVersionUID = 4296188215383358448L;
	
	/** Current activator.
	 */
	private final A activator;
	private Object[] initializationParameters = null;
	
	/**
	 * Create a new non-compound agent
	 * 
	 * @param activator is the activator to use.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public ActivatorAgent(A activator, Boolean commitSuicide) {
		super(commitSuicide);
		this.activator = activator;
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param activator is the activator to use.
	 */
	public ActivatorAgent(A activator) {
		super();
		this.activator = activator;
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param activator is the activator to use.
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public ActivatorAgent(A activator, CapacityContainer capacityContainer, Boolean commitSuicide) {
		super(capacityContainer, commitSuicide);
		this.activator = activator;
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param activator is the activator to use.
	 * @param capacityContainer is the container of capacities.
	 */
	public ActivatorAgent(A activator, CapacityContainer capacityContainer) {
		super(capacityContainer);
		this.activator = activator;
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param activator is the activator to use.
	 * @param address is a precomputed address to give to this agent.
	 */
	public ActivatorAgent(A activator, AgentAddress address) {
		super(address);
		this.activator = activator;
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param activator is the activator to use.
	 * @param address is a precomputed address to give to this agent.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public ActivatorAgent(A activator, AgentAddress address, Boolean commitSuicide) {
		super(address, commitSuicide);
		this.activator = activator;
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param activator is the activator to use.
	 * @param address is a precomputed address to give to this agent.
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	public ActivatorAgent(A activator, AgentAddress address, CapacityContainer capacityContainer, Boolean commitSuicide) {
		super(address, capacityContainer, commitSuicide);
		this.activator = activator;
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param activator is the activator to use.
	 * @param address is a precomputed address to give to this agent.
	 * @param capacityContainer is the container of capacities.
	 */
	public ActivatorAgent(A activator, AgentAddress address, CapacityContainer capacityContainer) {
		super(address, capacityContainer);
		this.activator = activator;
	}

	/** Replies the activator associated to this agent.
	 * 
	 * @return the scheduler associated to this agent.
	 */
	protected A getActivator() {
		return this.activator;
	}
	
	/** Replies initialization parameters passed to <code>init()</code>.
	 * 
	 *  @return the initialization parameters passed to <code>init()</code>.
	 */
	protected Object[] getInitializationParameters() {
		return this.initializationParameters;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	Status proceedPrivateInitialization(Object... parameters) {
		A a = getActivator();
		if (a!=null) {
			a.setLoggerProvider(this);
			a.sync();
		}
		return super.proceedPrivateInitialization(parameters);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		assert(this.activator!=null);
		this.initializationParameters = parameters;
		Status s = super.activate(parameters);
		Status s2 = this.activator.activate(parameters);
		return new MultipleStatus(s, s2).pack(this);
	}

	/** Replies a numerical indicator about its suicidal state.
	 * <p>
	 * <var>schedulingState<var> is mixed with the agent activator state
	 * to obtain a mixed value representing the global execution state.
	 * 
	 * @param roleState is {@code -1} for never play a role,
	 * {@code 0} currently playing a role, or {@code 1} for no more role to play.
	 * @param schedulingState is {@code -1} for never schedule an agent,
	 * {@code 0} currently scheduling an agent, or {@code 1} for no more agent to schedule.
	 * @return <code>true</code> if killable, otherwise <code>false</code>
	 */
	boolean isSelfKillableIndicator(int roleState, int schedulingState) {
		if (canCommitSuicide()) {
			
			// I want to commit suicide!!!!
			
			Activator<? extends Role> activator = getRoleActivator();
			assert(activator!=null);

			// May have no more role to play? Does all my roles were released?
			// If I never played a role, I can't commit suicide.
			int iRoleState = activator.isUsed() ? (activator.hasActivable() ? 0 : 1 ) : -1;
			int roleDiff = Math.abs(iRoleState-roleState);
			boolean commitSuicideForRolePlaying =
				(roleDiff>=2) || (roleDiff==0 && iRoleState==1);
			
			// May have no more agent to schedule? Does all my agents were killed?
			// If I never schedule an agent, I can't commit suicide.
			int iSchedulingState = this.activator.isUsed() ? (this.activator.hasActivable() ? 0 : 1 ) : -1;
			int schedulingDiff = Math.abs(iSchedulingState-schedulingState);
			boolean commitSuicideForAgentScheduling =
				(schedulingDiff>=2) || (schedulingDiff==0 && iSchedulingState==1);
	
			// No more role and no more agent -> commit suicide! 
			if (commitSuicideForRolePlaying && commitSuicideForAgentScheduling)
				return true;

			// No more role and never had agent -> commit suicide! 
			if (commitSuicideForRolePlaying && schedulingState<0 && iSchedulingState<0)
				return true;
			
			// No more agent and never had role -> commit suicide! 
			if (commitSuicideForAgentScheduling && roleState<0 && iRoleState<0)
				return true;
		}

		// I don't want to kill myself!!!!
		return false;
	}
	
	/** {@inheritDoc}
	 * <p>
	 * An ActivatorAgent, commit a suicide according to the following table:
	 * <table>
	 * <tr>
	 * <th>Commit suicide flag</th>
	 * <th>Role playing</th><th>Agent Activation</th>
	 * <th>isSelfKillableNow</th>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>never play role</td><td>never activate agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>never play role</td><td>has agent to activate</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>never play role</td><td>no more agent</td>
	 * <td><code>true</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>has role to play</td><td>never activate agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>has role to play</td><td>has agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>has role to play</td><td>no more agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>no more role</td><td>never activate agent</td>
	 * <td><code>true</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>no more role</td><td>has agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td>
	 * <td>no more role</td><td>no more agent</td>
	 * <td><code>true</code></td>
	 * </tr>
	 * </table> 
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>never play role</td><td>never activate agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>never play role</td><td>has agent to activate</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>never play role</td><td>no more agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>has role to play</td><td>never activate agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>has role to play</td><td>has agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>has role to play</td><td>no more agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>no more role</td><td>never activate agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>no more role</td><td>has agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td>
	 * <td>no more role</td><td>no more agent</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * </table>
	 *  
	 * @KILLAPI
	 */
	@Override
	boolean isSelfKillableNow() {
		return isSelfKillableIndicator(-1,-1);
	}

	/** {@inheritDoc}
	 */
	@Override
	Status proceedPrivateBehaviour() {
		A a = getActivator();
		if (a!=null) a.sync();
		return super.proceedPrivateBehaviour();
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status live() {
		assert(this.activator!=null);
		Status s = super.live();
		Status s2 = this.activator.live();
		return new MultipleStatus(s, s2).pack(this);
	}

	/** {@inheritDoc}
	 */
	@Override
	Status proceedPrivateDestruction() {
		A a = getActivator();
		if (a!=null) a.sync();
		return super.proceedPrivateDestruction();
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status end() {
		assert(this.activator!=null);
		Status s = this.activator.end();
		Status s2 = super.end();
		this.initializationParameters = null;
		return new MultipleStatus(s, s2).pack(this);
	}

}
