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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentsignal.BufferedSignalManager;
import org.janusproject.kernel.agentsignal.SignalManager;
import org.janusproject.kernel.condition.AfterTimeCondition;
import org.janusproject.kernel.condition.TimeCondition;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RolePlayer;
import org.janusproject.kernel.crio.interaction.MailboxUtil;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.organization.holonic.HolonicOrganization;
import org.janusproject.kernel.organization.integration.IntegrationOrganization;
import org.janusproject.kernel.schedule.Activable;
import org.janusproject.kernel.schedule.Activator;
import org.janusproject.kernel.status.KernelStatusConstants;
import org.janusproject.kernel.status.SingleStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusSeverity;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.util.selector.TypeSelector;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * Implements the agent concept of the Janus metamodel.
 * <p>
 * If the agent is able to commit suicide, it means that it will
 * stop its execution if it is no more playing any role.
 * If the agent is not able to commit suicide, it will persist
 * even if it does not play any role.
 * <p>
 * You may invoke {@link #wait()}, {@link #wait(long)} or
 * {@link #wait(long, int)} on a agent instance to wait
 * its termination. When {@link #end()} iscalled,
 * all waiting processes are notified. 
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Agent
extends RolePlayer
implements Activable, Holon, Serializable {

	private static final long serialVersionUID = -377981778136207606L;

	/**
	 * Indicates if this agent is able to commit a suicide or not.
	 * When a agent is able to commit suicide is will automatically
	 * kill itself when its has no more role to play.
	 * By default a heavy agent is able to commit suicide according to
	 * the value of the variable {@link KernelConstants#DEFAULT_KERNEL_AGENT_COMMIT_SUICIDE_FLAG}.
	 */
	private Boolean canCommitSuicide;

	/**
	 * boolean precising if this agent is compound or not the composition
	 * implies that this agent contains :
	 * <ul>
	 * <li> at least an holonic organization precising the decision making
	 * procedure inside the compound agent
	 * <li> possibly a merging organization to assure the recruitment procedure
	 * <li> a set of internal organization for the various goals that the
	 * compound agent have to satisfy
	 * </ul>
	 */
	private boolean isCompound = false;

	/**
	 * boolean precising if this compound agent can or cannot recruit new
	 * members using a merging organization
	 */
	private boolean isRecruitmentAllowed = false;

	/**
	 * The holonic organization : decision making and power distribution among
	 * the members Each member of the compound agent have to play a role in this
	 * organization. So the set of member of this groups represents all the
	 * members of the compound agent
	 */
	private GroupAddress holonicOrganization = null;

	/**
	 * The merging organization : recruitment procedure
	 */
	private GroupAddress mergingOrganization = null;

	/**
	 * The set of goal-dependent organizations integrated into the compound
	 * agent
	 */
	private List<GroupAddress> internalOrganizations = null;
	
	/** Indicates the address of the agent which has created this agent.
	 */
	AgentAddress creator = null;
	
	/** Object that is the current execution resource.
	 */
	transient AgentExecutionResource executionResource = null;
	
	/** Indicates if the agent is currently migrating or changing
	 * its execution method.
	 */
	final AtomicBoolean isMigrating = new AtomicBoolean(false);

	/** Weak reference to the kernel agent on which is agent
	 * is living.
	 */
	transient WeakReference<KernelAgent> kernel = null;
	
	/** Creation date.
	 */
	float creationDate = Float.NaN;
	
	/** Init parameters to pass to <code>activate()</code> 
	 */
	Object[] personalInitParameters = null;
	
	/**
	 * Indicates the state of this agent.
	 */
	private AgentLifeState agentState;
	
	/**
	 * The mailbox of this agent
	 */
	private Mailbox mailbox;

	/**
	 * The mailbox of this agent
	 */
	private MessageTransportService mts;
	
	/** Condition to wake up the agent.
	 */
	private volatile TimeCondition agentWakeUpCondition = null;
	
	/** Indicates if the agent has migrated from a kernel to another one.
	 */
	private final boolean hasMigrated = false;

	/**
	 * Create a new non-compound agent
	 * 
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	protected Agent(Boolean commitSuicide) {
		this.canCommitSuicide = commitSuicide;
		this.agentState = AgentLifeState.UNBORN;
		this.mailbox = null;
		this.mts = null;
	}

	/**
	 * Create a new non-compound agent
	 */
	protected Agent() {
		this((Boolean)null);
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	protected Agent(CapacityContainer capacityContainer, Boolean commitSuicide) {
		super(capacityContainer);
		this.canCommitSuicide = commitSuicide;
		this.agentState = AgentLifeState.UNBORN;
		this.mailbox = null;
		this.mts = null;
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param capacityContainer is the container of capacities.
	 */
	protected Agent(CapacityContainer capacityContainer) {
		this(capacityContainer, null);
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param address is a precomputed address to give to this agent.
	 */
	protected Agent(AgentAddress address) {
		this(address, (Boolean)null);
	}

	/**
	 * Create a new non-compound agent
	 * 
	 * @param address is a precomputed address to give to this agent.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	protected Agent(AgentAddress address, Boolean commitSuicide) {
		super(address);
		this.canCommitSuicide = commitSuicide;
		this.agentState = AgentLifeState.UNBORN;
		this.mailbox = null;
		this.mts = null;
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param address is a precomputed address to give to this agent.
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 */
	protected Agent(AgentAddress address, CapacityContainer capacityContainer, Boolean commitSuicide) {
		super(address, capacityContainer);
		this.canCommitSuicide = commitSuicide;
		this.agentState = AgentLifeState.UNBORN;
		this.mailbox = null;
		this.mts = null;
	}

	/**
	 * Create a new non-compound agent.
	 * 
	 * @param address is a precomputed address to give to this agent.
	 * @param capacityContainer is the container of capacities.
	 */
	protected Agent(AgentAddress address, CapacityContainer capacityContainer) {
		this(address, capacityContainer, (Boolean)null);
	}

	/**
     * Causes the current thread to wait until the kernel has terminated
     * its execution or a thread has invoked {@link Object#notify()}
     * or {@link Object#notifyAll()} on the instance of this object.
     * In other words, this function extends the behavior of 
     * {@link Object#wait()} by adding the termination of the agent
     * as a critera to wake up.
     * <p>
     * In opposite to {@link Object#wait()}, this function does not
     * requires to explicitly get ownership of this ovject's monitor. 
	 * 
	 * @throws InterruptedException
	 * @since 0.5
	 */
	public synchronized void waitUntilTermination() throws InterruptedException {
		while (getState()!=AgentLifeState.DIED) {
			wait(10000);
		}
	}

	/**
     * Causes the current thread to wait until the kernel has terminated
     * its execution or a thread has invoked {@link Object#notify()}
     * or {@link Object#notifyAll()} on the instance of this object.
     * In other words, this function extends the behavior of 
     * {@link Object#wait()} by adding the termination of the kernel
     * as a critera to wake up.
     * <p>
     * In opposite to {@link Object#wait()}, this function does not
     * requires to explicitly get ownership of this object's monitor. 
	 * 
	 * @param timeout is the maximal time to wait for the termination in milliseconds.
	 * @throws InterruptedException
	 * @since 0.5
	 */
	public synchronized void waitUntilTermination(long timeout) throws InterruptedException {
		if (getState()!=AgentLifeState.DIED) {
			wait(timeout);
		}
	}

	/** Replies the agents which are currently running on the same kernel.
	 * This agent itself is replied in the collection of local agents.
	 * The kernel agent is not included in the replied collection.
	 * 
	 * @return the running agents on the current kernel.
	 * @since 0.3
	 */
	protected final SizedIterator<AgentAddress> getLocalAgents() {
		KernelContext context = getKernelContext();
		assert(context!=null);
		return new FilteringAgentIterator(
				context.getKernelAgent(),
				context.getAgentRepository().sizedIterator());
	}

	/**
	 * {@inheritDoc}
	 * @EXECUTIONAPI
	 */
	@Override
	public CRIOContext getCRIOContext() {
		assert(this.kernel!=null);
		KernelAgent ka = this.kernel.get();
		assert(ka!=null);
		return ka.getCRIOContext();
	}

	/**
	 * {@inheritDoc}
	 * @EXECUTIONAPI
	 */
	@Override
	public KernelContext getKernelContext() {
		assert(this.kernel!=null);
		KernelAgent ka = this.kernel.get();
		assert(ka!=null);
		return ka.getKernelContext();
	}

	/**
	 * {@inheritDoc}
	 * @EXECUTIONAPI
	 */
	@Override
	public final float getCreationDate() {
		return this.creationDate;
	}

	/**
	 * {@inheritDoc}
	 * @EXECUTIONAPI
	 */
	@Override
	public final AgentAddress getCreator() {
		return this.creator;
	}

	/** Replies the first agent address associated
	 * to the given name.
	 * 
	 * @param name is the searched name.
	 * @return an agent address or <code>null</code> if the given
	 * name was not found.
	 */
	protected final AgentAddress getAgent(String name) {
		if (name!=null && !"".equals(name)) { //$NON-NLS-1$
			Iterator<AgentAddress> iterator = getKernelContext().getAgentRepository().iterator();
			AgentAddress adr;
			String nam;
			while (iterator.hasNext()) {
				adr = iterator.next();
				assert(adr!=null);
				nam = adr.getName();
				if (nam!=null && name.equals(nam)) {
					return adr;
				}
			}
		}
		return null;
	}
	
	/** Change the execution method of this agent, and
	 * switch to an heavy/threaded method if possible.
	 * 
	 * @return <code>true</code> if the execution method has changed;
	 * otherwise <code>false</code>.
	 * @EXECUTIONAPI
	 * @since 0.5
	 */
	protected final boolean setHeavyAgent() {
		if (this.executionResource==null) {
			KernelAgent k = this.kernel==null ? null : this.kernel.get();
			if (k!=null) {
				return k.setHeavyAgent(this);
			}
		}
		return false;
	}
	
	/** Change the execution method of this agent, and
	 * switch to an light/nothreaded method if possible.
	 * 
	 * @return <code>true</code> if the execution method has changed;
	 * otherwise <code>false</code>.
	 * @EXECUTIONAPI
	 * @since 0.5
	 */
	protected final boolean setLightAgent() {
		if (this.executionResource!=null) {
			KernelAgent k = this.kernel==null ? null : this.kernel.get();
			if (k!=null) {
				return k.setLightAgent(this);
			}
		}
		return false;
	}
	
	/** Replies if this agent is heavy.
	 * <p>
	 * A agent is heavy if it owns its execution resource.
	 * 
	 * @return <code>true</code> if this agent has an execution resource,
	 * otherwise <code>false</code>
	 * @see #setHeavyAgent()
	 * @see #setLightAgent()
	 * @EXECUTIONAPI
	 */
	public final boolean isHeavyAgent() {
		return this.executionResource!=null;
	}
	
	/** Replies if this agent is light.
	 * <p>
	 * A agent is light if it does not own any execution resource.
	 * 
	 * @return <code>true</code> if this agent has not an execution resource,
	 * otherwise <code>false</code>
	 * @see #setHeavyAgent()
	 * @see #setLightAgent()
	 * @EXECUTIONAPI
	 */
	public final boolean isLightAgent() {
		return this.executionResource==null;
	}

	/** Replies the execution resource used by this agent.
	 * <p>
	 * A agent is heavy if it owns its execution resource.
	 * If the agent does not have any execution resource,
	 * it is a light agent.
	 * 
	 * @return the execution resource if this agent is heavy,
	 * <code>null</code> if it is light agent.
	 * @see #setHeavyAgent()
	 * @see #setLightAgent()
	 * @EXECUTIONAPI
	 */
	public final AgentExecutionResource getExecutionResource() {
		return this.executionResource;
	}

	/** Indicates if this agent is alive.
	 * A agent is alive when its activation function was called but not
	 * its destruction function.
	 * 
	 * @return <code>true</code> if the agent is currently alive (inside its {@link Activable#live()} stage),
	 * otherwise <code>false</code>.
	 * @see AgentLifeState#isAlive()
	 * @EXECUTIONAPI
	 */
	@Override
	public synchronized boolean isAlive() {
		return this.agentState.isAlive();
	}

	/**
	 * {@inheritDoc}
	 * @EXECUTIONAPI
	 */
	@Override
	public synchronized AgentLifeState getState() {
		return this.agentState;
	}
	
	/**
	 * Replies if the agent has migrated to another Janus kernel.
	 * 
	 * @return <code>true</code> if the agent has migrated, otherwise <code>false</code>.
	 * @EXECUTIONAPI
	 */
	public boolean hasMigrated() {
		return this.hasMigrated;
	}

	/** Force the value of the agent state
	 * 
	 * @param state
	 */
	synchronized void setState(AgentLifeState state) {
		assert(state!=null);
		if (this.agentState!=state) {
			this.agentState = state;
			
			AgentAddress adr = getAddress();
			for(AgentLifeStateListener listener : getEventListeners(AgentLifeStateListener.class)) {
				listener.agentLifeChanged(adr, this.agentState);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @KILLAPI
	 */
	@Override
	public final boolean canCommitSuicide() {
		if (this.canCommitSuicide==null) {
			return !getCRIOContext().getProperties().getBoolean(JanusProperty.JANUS_AGENT_KEEP_ALIVE);
		}
		return this.canCommitSuicide.booleanValue();
	}

	/**
	 * Change the suicide commitment flag.
	 * 
	 * @param commitSuicide indicates if this agent may commit suicide or not.
	 * If <code>null</code>, set this flag to its default value.
	 * @KILLAPI
	 */
	protected void setCommitSuicide(Boolean commitSuicide) {
		this.canCommitSuicide = commitSuicide;
	}

	/** Kill this entity from this kernel.
	 * <p>
	 * Note that the dying entity will not disappear instantaneouly. Indeed
	 * the dying agent will go through its life-time to reach the {@link Activable#end()} function.
	 * 
	 * @return the status of the operation.
	 * @KILLAPI
	 */
	protected Status killMe() {
		KernelAgent kh = (this.kernel==null) ? null : this.kernel.get();
		if (kh==null)
			return new SingleStatus(StatusSeverity.FATAL,
				getAddress().toString(),
				KernelStatusConstants.NO_KERNEL_AGENT);
		return kh.kill(this, getAddress());
	}


	/** Kill an entity from this kernel.
	 * <p>
	 * Only the entity itself, its creator or a kernel-agent is
	 * able to kill.
	 * <p>
	 * Assuming a kernel agent has no creator address.
	 * <p>
	 * Note that the dying agent will not disappear instantaneouly. Indeed
	 * the dying agent will go through its life-time to reach the {@link Activable#end()} function.
	 * 
	 * @param dyingEntityAddress is the address of the dying entity.
	 * @return the status of the operation
	 * @KILLAPI
	 */
	protected Status kill(AgentAddress dyingEntityAddress) {
		KernelAgent kh = (this.kernel==null) ? null : this.kernel.get();
		if (kh==null)
			return new SingleStatus(
					StatusSeverity.FATAL,
					getAddress().toString(),
					KernelStatusConstants.NO_KERNEL_AGENT);
		return kh.kill(this, dyingEntityAddress);
	}

	/** Launch the given agent as a light agent.
	 * 
	 * @param <T> is the type of the agent.
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 */
	protected <T extends Agent> AgentAddress launchLightAgent(
			T agent, 
			String name,
			Object... initParameters) {
		return this.kernel.get().launchLightAgent(
				false, // differed execution?
				getAddress(), // creator
				agent, // new agent
				name, // agent name
				null, // activator
				initParameters); // initialization parameters
	}

	/** Launch the given agent as a light agent.
	 * 
	 * @param <T> is the type of the agent.
	 * @param agent is the agent to initialize and launch
	 * @return the address of the launched agent on the kernel.
	 * @param initParameters are the parameters to pass to activate.
	 * @LAUNCHINGAPI
	 */
	protected <T extends Agent> AgentAddress launchLightAgent(
			T agent,
			Object... initParameters) {
		return this.kernel.get().launchLightAgent(
				false, // differed execution?
				getAddress(), // creator
				agent, // new agent
				null, // agent name
				null, // activator
				initParameters); // initialization parameters
	}

	/** Launch the given agent as a light agent.
	 * 
	 * @param <T> is the type of the agent.
	 * @param agent is the agent to initialize and launch
	 * @param activator is the activator to use, never <code>null</code>.
	 * @return the address of the launched agent on the kernel.
	 * @param initParameters are the parameters to pass to activate.
	 * @LAUNCHINGAPI
	 */
	protected <T extends Agent> AgentAddress launchLightAgent(
			T agent, 
			AgentActivator activator,
			Object... initParameters) {
		return this.kernel.get().launchLightAgent(
				false, // differed execution?
				getAddress(), // creator
				agent, // new agent
				null, // agent name
				activator, // activator
				initParameters); // initialization parameters
	}

	/** Launch the given agent as a light agent.
	 * 
	 * @param <T> is the type of the agent.
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param activator is the activator to use, never <code>null</code>.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	protected <T extends Agent> AgentAddress launchLightAgent(
			T agent, 
			String name,
			AgentActivator activator,
			Object... initParameters) {
		return this.kernel.get().launchLightAgent(
				false, // differed execution?
				getAddress(), // creator
				agent, // new agent
				name, // agent name
				activator, // activator
				initParameters); // initialization parameters
	}
	
	/** Launch the given agent as a heavy agent.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	protected AgentAddress launchHeavyAgent(
			Agent agent,
			Object... initParameters) {
		return this.kernel.get().launchHeavyAgent(
				false, // differed execution?
				getAddress(), // creator
				agent, // new agent
				null, // agent name
				initParameters); // initialization parameters
	}

	/** Launch the given agent as a heavy agent.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	protected AgentAddress launchHeavyAgent(
			Agent agent,
			String name,
			Object... initParameters) {
		return this.kernel.get().launchHeavyAgent(
				false, // differed execution?
				getAddress(), // creator
				agent, // new agent
				name, // agent name
				initParameters); // initialization parameters
	}
	
	/**
	 * Decompose this agent according to the holonic-organizational model.
	 * 
	 * @param ho is the holonic organization to use for the decomposition
	 * @param obtainConditions is the list of conditions to follow to enter inside the new group
	 * @param leaveConditions is the list of conditions to follow to leave the new group
	 * @return <code>true</code> if the decomposition was done, otherwise <code>false</code>
	 * if the decomposition was done during a previous invokcation of this function.
	 * @HOLARCHYAPI
	 */
	protected final boolean decompose(
			Class<? extends HolonicOrganization> ho,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		if (this.isCompound) return false;
		this.isCompound = true;
		this.isRecruitmentAllowed = false;
		this.holonicOrganization = createGroup(
				ho,
				obtainConditions,
				leaveConditions);
		this.mergingOrganization = null;
		this.internalOrganizations = new ArrayList<GroupAddress>();
		return true;
	}

	/**
	 * Create a recuitement group.
	 * 
	 * @param mo is the integration organization to use for the recruitement
	 * @param obtainConditions is the list of conditions to follow to enter inside the new group
	 * @param leaveConditions is the list of conditions to follow to leave the new group
	 * @return <code>true</code> if the addition was done, otherwise <code>false</code>
	 * if a recuitement group was created during a previous invocation to this function.
	 * @HOLARCHYAPI
	 */
	protected final boolean addRecruitmentGroup(
			Class<? extends IntegrationOrganization> mo,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		if (this.isRecruitmentAllowed) return false;
		this.mergingOrganization = createGroup(
				mo,
				obtainConditions,
				leaveConditions);
		this.isRecruitmentAllowed = true;
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @HOLARCHYAPI
	 */
	@Override
	public final boolean isCompound() {
		return this.isCompound;
	}

	/**
	 * {@inheritDoc}
	 * @HOLARCHYAPI
	 */
	@Override
	public final boolean isRecruitmentAllowed() {
		return this.isRecruitmentAllowed;
	}

	/**
	 * {@inheritDoc}
	 * @HOLARCHYAPI
	 */
	@Override
	public final Collection<GroupAddress> getInternalOrganizations() {
		if (this.internalOrganizations==null) return Collections.emptyList();
		return Collections.unmodifiableCollection(this.internalOrganizations);
	}

	/**
	 * {@inheritDoc}
	 * @HOLARCHYAPI
	 */
	@Override
	public final GroupAddress getMergingOrganization() {
		return this.mergingOrganization;
	}

	/**
	/**
	 * {@inheritDoc}
	 * @HOLARCHYAPI
	 */
	@Override
	public final GroupAddress getHolonicOrganization() {
		return this.holonicOrganization;
	}

	/** Initialize this agent and invoke {@link #activate(Object...)}
	 * 
	 * @param parameters
	 * @return the status of the initialization.
	 */
	Status proceedPrivateInitialization(Object... parameters) {
		Object[] params;
		if (this.personalInitParameters!=null) {
			params = this.personalInitParameters;
			this.personalInitParameters = null;
		}
		else {
			params = parameters;
		}
		
		assert(AgentActivationPrototypeValidator.validateInputParameters(
				getClass(),
				params));

		setState(AgentLifeState.BORN);
		
		Activator<? extends Role> activator = getRoleActivator();
		assert(activator!=null);
		activator.setLoggerProvider(this);
		activator.sync();

		Status s = activate(params);
		
		if (s==null || s.isSuccess())
			setState(AgentLifeState.ALIVE);
		else
			setState(AgentLifeState.DIED);
		
		return s;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		Activator<? extends Role> activator = getRoleActivator();
		assert(activator!=null);
		return activator.activate(parameters);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status live() {
		Activator<? extends Role> activator = getRoleActivator();
		assert(activator!=null);
		activator.sync();
		return activator.live();		
	}

	/** Replies if this agent wants to commit suicide.
	 * <p>
	 * An Agent, commit a suicide according to the following table:
	 * <table>
	 * <tr>
	 * <th>Commit suicide flag</th><th>Role playing</th><th>isSelfKillableNow</th>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td><td>never play a role</td><td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td><td>currently playing one role</td><td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>true</code></td><td>leave all roles</td><td><code>true</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td><td>never play a role</td><td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td><td>currently playing one role</td><td><code>false</code></td>
	 * </tr>
	 * <tr>
	 * <td><code>false</code></td><td>leave all roles</td><td><code>false</code></td>
	 * </tr>
	 * </table> 
	 * 
	 * @return <code>true</code> if this agent whant to commit suicide,
	 * otherwise <code>false</code>
	 * @KILLAPI
	 */
	boolean isSelfKillableNow() {
		Activator<? extends Role> activator = getRoleActivator();
		assert(activator!=null);
		return canCommitSuicide() && activator.isUsed() && !activator.hasActivable();
	}
	
	/** Run the private behaviour of this agent and invoke {@link #live()}
	 * 
	 * @return the status of the behaviour execution.
	 */
	Status proceedPrivateBehaviour() {
		Activator<? extends Role> activator = getRoleActivator();
		assert(activator!=null);
		activator.sync();

		if(this.mailbox instanceof BufferedMailbox) {
			((BufferedMailbox)this.mailbox).synchronizeMessages();
		}
		
		SignalManager sm = getSignalManager();
		if (sm instanceof BufferedSignalManager) {
			((BufferedSignalManager)sm).sync();
		}

		if (isSelfKillableNow()) {
			return killMe();
		}		
		
		return live();
	}

	/** Destroy this agent and invoke {@link #end()}
	 * 
	 * @return the status of the destruction.
	 */
	Status proceedPrivateDestruction() {
		setState(AgentLifeState.BREAKING_DOWN);
		
		// Remove capacity results
		clearCapacityCalls();

		// Force to leave roles
		leaveAllRoles();
						
		Activator<? extends Role> activator = getRoleActivator();
		assert(activator!=null);
		activator.sync();
		
		Status s = end();

		setState(AgentLifeState.DIED);
		
		return s;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispose() {
		super.dispose();
		clearListeners();
		clearMailbox();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Status end() {
		Activator<? extends Role> activator = getRoleActivator();
		assert(activator!=null);
		return activator.end();
	}
	
	/** Stop the agent execution until the given timout is reached.
	 * <p>
	 * <strong>CAUTION:</strong> this function does never sleep the
	 * agent when the execution of the agent's life-cycle functions
	 * ({@link #activate(Object...)}, {@link #live()}, and
	 * {@link #end()}) are under progression.
	 * Sleep request is taken into account
	 * just after {@link #live()} has exited. Sleep function
	 * may be invoked from {@link #activate(Object...)} and
	 * {@link #end()} but these functions are outside the
	 * scope of the sleep feature, ie. sleeping as no effect on
	 * the invocation of these functions.
	 * <p>
	 * This function is available for both heavy and light agents.
	 * <p>
	 * In the underground implementation, if the agent is threaded,
	 * ie. it is an heavy agent, the {@link Thread#sleep(long)} 
	 * is invoked.
	 * If the agent is not threaded, ie. it is a 
	 * light agent, the agent's activator ignore it until the
	 * timeout is reached.
	 * <p>
	 * The timout unit depends on the current time manager.
	 * 
	 * @param timeout is the delay during which the agent must sleep
	 * (in default time manager's unit).
	 * @return <code>true</code> if the agent will be paused,
	 * otherwise <code>false</code>
	 * @see KernelTimeManager
	 * @since 0.5
	 */
	protected final boolean sleep(float timeout) {
		if (!(this instanceof KernelAgent)) {
			float wakeUpTime = getKernelContext().getTimeManager().getCurrentTime() + timeout;
			this.agentWakeUpCondition = new AfterTimeCondition(wakeUpTime);
			return true;
		}
		return false;
	}

	/** Stop the agent execution until the given timout is reached.
	 * <p>
	 * <strong>CAUTION:</strong> this function does never sleep the
	 * agent when the execution of the agent's life-cycle functions
	 * ({@link #activate(Object...)}, {@link #live()}, and
	 * {@link #end()}) are under progression.
	 * Sleep request is taken into account
	 * just after {@link #live()} has exited. Sleep function
	 * may be invoked from {@link #activate(Object...)} and
	 * {@link #end()} but these functions are outside the
	 * scope of the sleep feature, ie. sleeping as no effect on
	 * the invocation of these functions.
	 * <p>
	 * This function is available for both heavy and light agents.
	 * <p>
	 * In the underground implementation, if the agent is threaded,
	 * ie. it is an heavy agent, the {@link Thread#sleep(long)} 
	 * is invoked.
	 * If the agent is not threaded, ie. it is a 
	 * light agent, the agent's activator ignore it until the
	 * timeout is reached.
	 * <p>
	 * The timout unit depends on the current time manager.
	 * 
	 * @param wakeUpCondition is the condition to wake up
	 * the agent.
	 * @return <code>true</code> if the agent will be paused,
	 * otherwise <code>false</code>
	 * @see KernelTimeManager
	 * @since 0.5
	 */
	protected final boolean sleep(TimeCondition wakeUpCondition) {
		if (wakeUpCondition!=null && !(this instanceof KernelAgent)) {
			this.agentWakeUpCondition = wakeUpCondition;
			return true;
		}
		return false;
	}

	/** Test the wake-up condition if present and replies if this agent is 
	 * still sleeping.
	 * 
	 * @return <code>true</code> if this agent continue to sleep,
	 * otherwise <code>false</code>
	 */
	boolean wakeUpIfSleeping() {
		TimeCondition tc = this.agentWakeUpCondition;
		if (tc!=null) {
			KernelContext kc = getKernelContext();
			if (tc.evaluate(kc.getTimeConditionParameterProvider())) {
				this.agentWakeUpCondition = null;
				return false;
			}
			return true;
		}
		return false;
	}
	
	/** Replies if the agent is currently sleeping, ie. it is
	 * waiting for a particular condition to wake up.
	 *  
	 * @return <code>true</code> if the agent is sleeping;
	 * <code>false</code> if not.
	 */
	public boolean isSleeping() {
		return this.agentWakeUpCondition!=null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getAddress().toString());
		if (this.agentWakeUpCondition!=null) {
			buf.append("-SLEEPING"); //$NON-NLS-1$
		}
		return buf.toString();
	}
	
	/**
	 * Clear the content of the mail box if the mail box exists.
	 * @MESSAGEAPI
	 */
	final void clearMailbox() {
		Mailbox m = this.mailbox;
		if (m!=null) m.clear();
	}	

	/**
	 * Replies the mailbox for the agent.
	 * 
	 * @return the mailbox of the given agent, never <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected final Mailbox getMailbox() {
		if (this.mailbox==null) {
			this.mailbox = MailboxUtil.createDefaultMailbox(getClass(), getCRIOContext().getProperties(), getLogger());
		}
		return this.mailbox;
	}	

	/**
	 * Set the mailbox for the agent.
	 * 
	 * @param mailbox is the mailbox of the agent.
	 * @MESSAGEAPI
	 */
	protected final void setMailbox(Mailbox mailbox) {
		if (this.mailbox!=null && mailbox!=null) {
			mailbox.synchronize(this.mailbox);
		}
		this.mailbox = mailbox;
	}	

	/**
	 * Replies the message transport service for the agent.
	 * 
	 * @return the message transport service of the given agent, never <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected final MessageTransportService getMessageTransportService() {
		if (this.mts==null) {
			this.mts = new MessageTransportService();
		}
		return this.mts;
	}
	
	/**
	 * Replies the first available message in the agent mail box
	 * and remove it from the mailbox.
	 * 
	 * @return the first available message, or <code>null</code> if
	 * the mailbox is empty.
	 * @see #peekMessage()
	 * @see #getMessages()
	 * @see #peekMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 */
	protected final Message getMessage() {
		return getMailbox().removeFirst();
	}
	
	/**
	 * Replies the first available message of the specified type
	 * in the agent mail box
	 * and remove it from the mailbox.
	 * 
	 * @param <M> is the type of the messazge to search for.
	 * @param type is the type of the messazge to search for.
	 * @return the first available message, or <code>null</code> if
	 * the mailbox is empty.
	 * @see #peekMessage()
	 * @see #getMessages()
	 * @see #peekMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected final <M extends Message> M getMessage(Class<M> type) {
		return getMailbox().removeFirst(new TypeSelector<M>(type));
	}

	/**
	 * Replies the first available message in the agent mail box
	 * and leave it inside the mailbox.
	 * 
	 * @return the first available message, or <code>null</code> if
	 * the mailbox is empty.
	 * @see #getMessage()
	 * @see #getMessages()
	 * @see #peekMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 */
	protected final Message peekMessage() {
		return getMailbox().getFirst();
	}
	
	/**
	 * Replies the first available message of the given type
	 * in the agent mail box
	 * and leave it inside the mailbox.
	 * 
	 * @param <M> is the type of the message to search for.
	 * @param type is the type of the message to search for.
	 * @return the first available message, or <code>null</code> if
	 * the mailbox is empty.
	 * @see #getMessage()
	 * @see #getMessages()
	 * @see #peekMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected final <M extends Message> M peekMessage(Class<M> type) {
		return getMailbox().getFirst(new TypeSelector<M>(type));
	}

	/**
	 * Replies the messages in the agent mailbox.
	 * Each time an message is consumed
	 * from the replied iterable object,
	 * the corresponding message is removed
	 * from the mailbox.
	 * 
	 * @return all the messages, never <code>null</code>.
	 * @see #getMessage()
	 * @see #peekMessage()
	 * @see #peekMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 */
	protected final Iterable<Message> getMessages() {
		return getMailbox().iterable(true);
	}
	
	/**
	 * Replies the messages of the specified type
	 * in the agent mailbox.
	 * Each time an message is consumed
	 * from the replied iterable object,
	 * the corresponding message is removed
	 * from the mailbox.
	 * 
	 * @param <M> is the type of the messages to search for.
	 * @param type is the type of the messages to search for.
	 * @return all the messages, never <code>null</code>.
	 * @see #getMessage()
	 * @see #peekMessage()
	 * @see #peekMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected final <M extends Message> Iterable<M> getMessages(Class<M> type) {
		return getMailbox().iterable(new TypeSelector<M>(type), true);
	}

	/**
	 * Replies the messages in the agent mailbox.
	 * Each time an message is consumed
	 * from the replied iterable object,
	 * the corresponding message is NOT removed
	 * from the mailbox.
	 * 
	 * @return all the messages, never <code>null</code>.
	 * @see #getMessage()
	 * @see #peekMessage()
	 * @see #getMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 */
	protected final Iterable<Message> peekMessages() {
		return getMailbox().iterable(false);
	}

	/**
	 * Replies the messages of the specified type
	 * in the agent mailbox.
	 * Each time an message is consumed
	 * from the replied iterable object,
	 * the corresponding message is NOT removed
	 * from the mailbox.
	 * 
	 * @param <M> is the type of the messages to search for.
	 * @param type is the type of the messages to search for.
	 * @return all the messages, never <code>null</code>.
	 * @see #getMessage()
	 * @see #peekMessage()
	 * @see #getMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected final <M extends Message> Iterable<M> peekMessages(Class<M> type) {
		return getMailbox().iterable(new TypeSelector<M>(type), false);
	}

	/** Indicates if the agent mailbox contains a message or not.
	 * 
	 * @return <code>true</code> if the message contains at least one message,
	 * otherwise <code>false</code>
	 * @see #getMessage()
	 * @see #peekMessage()
	 * @see #getMessages()
	 * @see #peekMessages()
	 * @MESSAGEAPI
	 */
	protected final boolean hasMessage() {
		return !getMailbox().isEmpty();
	}
	
	/**
	 * Replies the number of messages in the agent mailbox.
	 * 
	 * @return the number of messages in the agent mailbox.
	 * @see #getMessage()
	 * @see #peekMessage()
	 * @see #peekMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 */
	protected final long getMailboxSize() {
		return getMailbox().size();
	}

	/**
	 * Reply to the specified <code>Message</code>.
	 * <p>
	 * This function force the emitter of the message to be this agent.
	 *
	 * @param messageToReplyTo is the message to reply to.
	 * @param message is the message to send
	 * @return the address of the receiver of the freshly sended message, <code>null</code> else.
	 * @since 0.5
	 * @MESSAGEAPI
	 */
	protected final AgentAddress replyToMessage(Message messageToReplyTo, Message message) {
		assert(messageToReplyTo!=null);
		Address adr = messageToReplyTo.getSender();
		if (adr instanceof AgentAddress) {
			return getMessageTransportService().sendMessage(
					message,
					(AgentAddress)adr);
		}
		return null;
	}

	/**
	 * Send the specified <code>Message</code> to one randomly selected agent.
	 * <p>
	 * This function force the emitter of the message to be this agent.
	 * 
	 * @param message is the message to send
	 * @param agents is the collection of receivers.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final AgentAddress sendMessage(Message message, AgentAddress... agents) {
		return getMessageTransportService().sendMessage(message, agents);
	}

	/**
	 * Send the specified <code>Message</code> to one randomly selected agent.
	 * <p>
	 * This function force the emitter of the message to be this agent.
	 * 
	 * @param message is the message to send
	 * @param agents is the collection of receivers.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final AgentAddress sendMessage(Message message, List<? extends AgentAddress> agents) {
		return getMessageTransportService().sendMessage(message, agents);
	}

	/**
	 * Send the specified <code>Message</code> to an arbitrary selected agent.
	 * <p>
	 * This function force the emitter of the message to be this agent.
	 * 
	 * @param message is the message to send
	 * @param policy is the receiver selection policy.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final AgentAddress sendMessage(
			Message message,
			MessageReceiverSelectionPolicy policy) {
		return getMessageTransportService().sendMessage(message, policy);
	}

	/**
	 * Send the specified <code>Message</code> to all the given agents.
	 * <p>
	 * This function force the emitter of the message to be this agent.
	 * <p>
	 * If the list of agent address is empty, all the agents currently
	 * registered inside the kernel will receive the message.
	 * 
	 * @param message is the message to send
	 * @param agents are the addresses of the receivers.
	 * @MESSAGEAPI
	 */
	protected final void broadcastMessage(Message message, AgentAddress... agents) {
		getMessageTransportService().broadcastMessage(message, agents);
	}

	/**
	 * Send the specified <code>Message</code> to all the given agents.
	 * <p>
	 * This function force the emitter of the message to be this agent.
	 * <p>
	 * If the list of agent address is empty, all the agents currently
	 * registered inside the kernel will receive the message.
	 * 
	 * @param message is the message to send
	 * @param agents are the addresses of the receivers.
	 * @MESSAGEAPI
	 */
	protected final void broadcastMessage(Message message, Collection<? extends AgentAddress> agents) {
		getMessageTransportService().broadcastMessage(message, agents);
	}

	/**
	 * Send the specified <code>Message</code> to an arbitrary selected agent.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param message is the message to send
	 * @param policy is the receiver selection policy.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final AgentAddress forwardMessage(
			Message message,
			MessageReceiverSelectionPolicy policy) {
		return getMessageTransportService().forwardMessage(message, policy);
	}

	/**
	 * Send the specified <code>Message</code> to one randomly selected agent.
	 * <p>
	 * This function does not change the emitter of the message.
	 * <p>
	 * If no agent address was specified as parameter, one agent is
	 * arbitrary selected to receive the message.
	 * 
	 * @param message is the message to send
	 * @param agents is the collection of receivers.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final AgentAddress forwardMessage(Message message, AgentAddress... agents) {
		return getMessageTransportService().forwardMessage(message, agents);
	}

	/**
	 * Send the specified <code>Message</code> to one or more agents.
	 * <p>
	 * This function does not change the emitter of the message.
	 * <p>
	 * If no agent address was specified as parameter, one agent is
	 * arbitrary selected to receive the message.
	 * 
	 * @param message is the message to send
	 * @param agents is the collection of receivers.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final AgentAddress forwardMessage(Message message, List<? extends AgentAddress> agents) {
		return getMessageTransportService().forwardMessage(message, agents);
	}

	/**
	 * Send the specified <code>Message</code> to all the given agents.
	 * <p>
	 * This function does not change the emitter of the message.
	 * <p>
	 * If the list of agent address is empty, all the agents currently
	 * registered inside the kernel will receive the message.
	 * 
	 * @param message is the message to send
	 * @param agents are the addresses of the receivers.
	 * @MESSAGEAPI
	 */
	protected final void forwardBroadcastMessage(Message message, AgentAddress... agents) {
		getMessageTransportService().forwardBroadcastMessage(message, agents);
	}

	/**
	 * Send the specified <code>Message</code> to all the given agents.
	 * <p>
	 * This function does not change the emitter of the message.
	 * <p>
	 * If the list of agent address is empty, all the agents currently
	 * registered inside the kernel will receive the message.
	 * 
	 * @param message is the message to send
	 * @param agents are the addresses of the receivers.
	 * @MESSAGEAPI
	 */
	protected final void forwardBroadcastMessage(
			Message message,
			Collection<? extends AgentAddress> agents) {
		getMessageTransportService().forwardBroadcastMessage(message, agents);
	}

	/**
	 * Replies played role in given group.
	 * 
	 * @param <R>
	 * @param group
	 * @param role
	 * @return played role or <code>null</code> if the not is not played.
	 * @GROUPAPI
	 */
	final <R extends Role> R getRoleInstance(GroupAddress group, Class<R> role) {
		return getRole(group, role);
	}

	/** Remove listener on life state changes.
	 * 
	 * @param listener
	 */
	public void removeAgentLifeStateListener(AgentLifeStateListener listener) {
		removeEventListener(AgentLifeStateListener.class, listener);
	}

	/** Add listener on life state changes.
	 * 
	 * @param listener
	 */
	public void addAgentLifeStateListener(AgentLifeStateListener listener) {
		addEventListener(AgentLifeStateListener.class, listener);
	}

	/**
	 * This class describes a message transport service in an
	 * agent context.
	 * It provides all functions to send messages.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public class MessageTransportService
	implements Serializable {

		private static final long serialVersionUID = -86884850361971392L;
		
		private boolean sendFreedBack = true;
		private boolean forwardFreedBack = true;
		private boolean broadcastFreedBack = true;
		
		/**
		 */
		protected MessageTransportService() {
			//
		}
		
		/** Replies if the emitting role is receiving the messages sent
		 * to itself by a <code>sendMessage</code> function.
		 * 
		 * @return <code>true</code> if the role allow to receive self-back
		 * messages, otherwise <code>false</code>.
		 */
		public boolean isSendMessageFeedBack() {
			return this.sendFreedBack;
		}
			
		/** Set if the emitting role is receiving the messages sent
		 * to itself by a <code>sendMessage</code> function.
		 * 
		 * @param feedback is <code>true</code> to allow the role to receive self-back
		 * messages, otherwise <code>false</code>.
		 */
		public void setSendMessageFeedBack(boolean feedback) {
			this.sendFreedBack = feedback;
		}

		/** Replies if the emitting role is receiving the messages sent
		 * to itself by a <code>forwardMessage</code> function.
		 * 
		 * @return <code>true</code> if the role allow to receive self-back
		 * messages, otherwise <code>false</code>.
		 */
		public boolean isForwardMessageFeedBack() {
			return this.forwardFreedBack;
		}
			
		/** Set if the emitting role is receiving the messages sent
		 * to itself by a <code>forwardMessage</code> function.
		 * 
		 * @param feedback is <code>true</code> to allow the role to receive self-back
		 * messages, otherwise <code>false</code>.
		 */
		public void setForwardMessageFeedBack(boolean feedback) {
			this.forwardFreedBack = feedback;
		}

		/** Replies if the emitting role is receiving the messages sent
		 * by a <code>broadcastMessage</code> function.
		 * 
		 * @return <code>true</code> if the role allow to receive self-back
		 * messages, otherwise <code>false</code>.
		 */
		public boolean isBroadcastMessageFeedBack() {
			return this.broadcastFreedBack;
		}
			
		/** Set if the emitting role is receiving the messages sent
		 * by a <code>broadcastMessage</code> function.
		 * 
		 * @param feedback is <code>true</code> to allow the role to receive self-back
		 * messages, otherwise <code>false</code>.
		 */
		public void setBroadcastMessageFeedBack(boolean feedback) {
			this.broadcastFreedBack = feedback;
		}
		

		/**
		 * Send the specified <code>Message</code> to one randomly selected agent.
		 * <p>
		 * This function force the emitter of the message to be this agent.
		 * 
		 * @param message is the message to send
		 * @param agents is the collection of receivers.
		 * @return the address of the receiver of the freshly sended message if it
		 *         was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public AgentAddress sendMessage(Message message, AgentAddress... agents) {
			return InteractionUtil.sendMessage(
					getTimeManager().getCurrentTime(),
					Agent.this, 
					message, 
					(agents==null)
					? null
					: Arrays.asList(agents), 
					true,
					isSendMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to one randomly selected agent.
		 * <p>
		 * This function force the emitter of the message to be this agent.
		 * 
		 * @param message is the message to send
		 * @param agents is the collection of receivers.
		 * @return the address of the receiver of the freshly sended message if it
		 *         was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public AgentAddress sendMessage(Message message, List<? extends AgentAddress> agents) {
			return InteractionUtil.sendMessage(
					getTimeManager().getCurrentTime(),
					Agent.this, 
					message, 
					agents, 
					true,
					isSendMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to an arbitrary selected agent.
		 * <p>
		 * This function force the emitter of the message to be this agent.
		 * 
		 * @param message is the message to send
		 * @param policy is the receiver selection policy.
		 * @return the address of the receiver of the freshly sended message if it
		 *         was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public AgentAddress sendMessage(
				Message message,
				MessageReceiverSelectionPolicy policy) {
			return InteractionUtil.sendMessage(
					getTimeManager().getCurrentTime(),
					Agent.this,
					message,
					policy,
					true,
					isSendMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to all the given agents.
		 * <p>
		 * This function force the emitter of the message to be this agent.
		 * <p>
		 * If the list of agent address is empty, all the agents currently
		 * registered inside the kernel will receive the message.
		 * 
		 * @param message is the message to send
		 * @param agents are the addresses of the receivers.
		 * @MESSAGEAPI
		 */
		public void broadcastMessage(Message message, AgentAddress... agents) {
			InteractionUtil.broadcastMessage(
					getTimeManager().getCurrentTime(),
					Agent.this,
					message,
					(agents==null)
					? null
					: Arrays.asList(agents),
					true,
					isBroadcastMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to all the given agents.
		 * <p>
		 * This function force the emitter of the message to be this agent.
		 * <p>
		 * If the list of agent address is empty, all the agents currently
		 * registered inside the kernel will receive the message.
		 * 
		 * @param message is the message to send
		 * @param agents are the addresses of the receivers.
		 * @MESSAGEAPI
		 */
		public void broadcastMessage(Message message, Collection<? extends AgentAddress> agents) {
			InteractionUtil.broadcastMessage(
					getTimeManager().getCurrentTime(),
					Agent.this,
					message,
					agents,
					true,
					isBroadcastMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to an arbitrary selected agent.
		 * <p>
		 * This function does not change the emitter of the message.
		 * 
		 * @param message is the message to send
		 * @param policy is the receiver selection policy.
		 * @return the address of the receiver of the freshly sended message if it
		 *         was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public AgentAddress forwardMessage(
				Message message,
				MessageReceiverSelectionPolicy policy) {
			return InteractionUtil.sendMessage(
					getTimeManager().getCurrentTime(),
					Agent.this,
					message,
					policy,
					false,
					isForwardMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to one randomly selected agent.
		 * <p>
		 * This function does not change the emitter of the message.
		 * <p>
		 * If no agent address was specified as parameter, one agent is
		 * arbitrary selected to receive the message.
		 * 
		 * @param message is the message to send
		 * @param agents is the collection of receivers.
		 * @return the address of the receiver of the freshly sended message if it
		 *         was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public AgentAddress forwardMessage(Message message, AgentAddress... agents) {
			return InteractionUtil.sendMessage(
					getTimeManager().getCurrentTime(),
					Agent.this,
					message,
					(agents==null)
					? null
					: Arrays.asList(agents),
					false,
					isForwardMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to one or more agents.
		 * <p>
		 * This function does not change the emitter of the message.
		 * <p>
		 * If no agent address was specified as parameter, one agent is
		 * arbitrary selected to receive the message.
		 * 
		 * @param message is the message to send
		 * @param agents is the collection of receivers.
		 * @return the address of the receiver of the freshly sended message if it
		 *         was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public AgentAddress forwardMessage(Message message, List<? extends AgentAddress> agents) {
			return InteractionUtil.sendMessage(
					getTimeManager().getCurrentTime(),
					Agent.this,
					message,
					agents,
					false,
					isForwardMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to all the given agents.
		 * <p>
		 * This function does not change the emitter of the message.
		 * <p>
		 * If the list of agent address is empty, all the agents currently
		 * registered inside the kernel will receive the message.
		 * 
		 * @param message is the message to send
		 * @param agents are the addresses of the receivers.
		 * @MESSAGEAPI
		 */
		public void forwardBroadcastMessage(Message message, AgentAddress... agents) {
			InteractionUtil.broadcastMessage(
					getTimeManager().getCurrentTime(),
					Agent.this,
					message,
					(agents==null)
					? null
					: Arrays.asList(agents),
					false,
					isForwardMessageFeedBack() && isBroadcastMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to all the given agents.
		 * <p>
		 * This function does not change the emitter of the message.
		 * <p>
		 * If the list of agent address is empty, all the agents currently
		 * registered inside the kernel will receive the message.
		 * 
		 * @param message is the message to send
		 * @param agents are the addresses of the receivers.
		 * @MESSAGEAPI
		 */
		public void forwardBroadcastMessage(
				Message message,
				Collection<? extends AgentAddress> agents) {
			InteractionUtil.broadcastMessage(
					getTimeManager().getCurrentTime(),
					Agent.this,
					message,
					agents,
					false,
					isForwardMessageFeedBack() && isBroadcastMessageFeedBack());
		}

	}
	
	/**
	 * Iterator on agents which does not reply the kernel agent address.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FilteringAgentIterator
	implements SizedIterator<AgentAddress> {

		private final Address kernelAdr;
		private final SizedIterator<AgentAddress> iterator;
		private boolean foundKernel = false;
		private AgentAddress next;
		
		/**
		 * @param kernel
		 * @param iterator
		 */
		public FilteringAgentIterator(
				Address kernel,
				SizedIterator<AgentAddress> iterator) {
			assert(iterator!=null);
			assert(kernel!=null);
			this.kernelAdr = kernel;
			this.iterator = iterator;
			searchNext();
		}
		
		private void searchNext() {
			this.next = null;
			AgentAddress adr;
			while (this.iterator.hasNext()) {
				adr = this.iterator.next();
				if (!adr.equals(this.kernelAdr)) {
					this.next = adr;
					return;
				}
				this.foundKernel = true;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int rest() {
			int r = this.iterator.rest();
			if (!this.foundKernel) --r;
			return r;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int totalSize() {
			return this.iterator.totalSize()-1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.next!=null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress next() {
			AgentAddress a = this.next;
			searchNext();
			return a;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			//
		}
		
	}
	
}
