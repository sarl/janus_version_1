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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelEvent.KernelEventType;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.interaction.PrivilegedMessageTransportService;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.organization.PrivilegedPersistentGroupCleanerService;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.repository.Repository;
import org.janusproject.kernel.schedule.Activable;
import org.janusproject.kernel.status.ExceptionStatus;
import org.janusproject.kernel.status.KernelStatusConstants;
import org.janusproject.kernel.status.MultipleStatus;
import org.janusproject.kernel.status.SingleStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.status.StatusSeverity;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.util.sizediterator.SizedIterator;
import org.janusproject.kernel.util.throwable.Throwables;

/** Agent that represents and run the kernel of the Janus platform.
 * <p>
 * If the kernel agent is suicidable, it means that it will
 * stop its execution if no more other agent exists.
 * If the kernel agent is not suicidable, it will persist
 * even if no more other agent is registered.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelAgent
extends ActivatorAgent<AgentActivator> {

	private static final long serialVersionUID = -3746815344176815340L;

	static {
		LoggerUtil.loadDefaultConfiguration(false);
	}
	
	private final KernelContext context;
	private final KernelWrapper kernelWrapper;
	private final AtomicBoolean isNonKernelAgentLaunched = new AtomicBoolean(false);
	private final AtomicBoolean isLaunch = new AtomicBoolean(false);
	
	private final Collection<DifferedLightAgentInfo> differedLightAgents = new LinkedList<DifferedLightAgentInfo>();
	private final Collection<AgentThread> differedHeavyAgents = new LinkedList<AgentThread>();
	
	/** This collection contains the agents that want to become heavy.
	 * They are buffered to ensure that the agent activators had removed
	 * the agents from their scheduling lists, before they are launched
	 * as heavy.
	 */
	private final Collection<Agent> newHeavyAgents = new LinkedList<Agent>();
	
	/**
	 * Create a kernel agent with the default settings.
	 *
	 * @param activator is the activator to use.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param initParameters are the parameters to pass to the activate function.
	 */
	protected KernelAgent(
			AgentActivator activator,
			Boolean commitSuicide,
			KernelTimeManager timeManager,
			EventListener startUpListener,
			Object... initParameters) {
		this(activator,commitSuicide,timeManager,startUpListener,null, null, initParameters);
	}

	/**
	 * Create a kernel agent with the default settings.
	 *
	 * @param activator is the activator to use.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by this kernel.
	 * @param initParameters are the parameters to pass to the activate function.
	 * @since 0.4
	 */
	protected KernelAgent(
			AgentActivator activator,
			Boolean commitSuicide,
			KernelTimeManager timeManager,
			EventListener startUpListener,
			String applicationName,
			Object... initParameters) {
		this(activator,commitSuicide,timeManager,startUpListener,null,applicationName,initParameters);
	}

	/**
	 * Create a kernel agent with the default settings.
	 *
	 * @param activator is the activator to use.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param distantKernelHandler is the handler which will be notified each time a message should
	 * be sent to a distant kernel.
	 * @param initParameters are the parameters to pass to the activate function.
	 */
	protected KernelAgent(
			AgentActivator activator,
			Boolean commitSuicide,
			KernelTimeManager timeManager,
			EventListener startUpListener,
			DistantKernelHandler distantKernelHandler,
			Object... initParameters) {
		this(activator,commitSuicide,timeManager,startUpListener,distantKernelHandler,null, initParameters);
	}

	/**
	 * Create a kernel agent with the default settings.
	 *
	 * @param activator is the activator to use.
	 * @param commitSuicide indicates if this agent is able to commit suicide or not
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param distantKernelHandler is the handler which will be notified each time a message should
	 * be sent to a distant kernel.
	 * @param applicationName is the name of the application supported by this kernel.
	 * @param initParameters are the parameters to pass to the activate function.
	 * @since 0.4
	 */
	protected KernelAgent(
			AgentActivator activator,
			Boolean commitSuicide,
			KernelTimeManager timeManager,
			EventListener startUpListener,
			DistantKernelHandler distantKernelHandler,
			String applicationName,
			Object... initParameters) {
		super(activator, commitSuicide);
		
		if (startUpListener instanceof KernelListener)
			addKernelListener((KernelListener)startUpListener);
		if (startUpListener instanceof AgentLifeStateListener)
			addAgentLifeStateListener((AgentLifeStateListener)startUpListener);
		// Create Kernel wrapper
		this.kernelWrapper = new KernelWrapper();

		AgentAddress adr = getAddress();
		
		// Create kernel context
		this.context = new KernelContext(adr, timeManager,distantKernelHandler);
		this.context.getAgentRepository().add(adr, this);
		this.creator = adr;

		// Force kernel name
		setApplicationNameAndAgentName(applicationName);		
		
		// Register inside global list of kernel agents
		Kernels.add(this);
		
		// Launching kernel agent
		this.context.getExecutorService().execute(new AgentThread(this, initParameters));
		while (!this.isLaunch.get()) {
			Thread.yield();
		}
	}
	
	@Override
	protected final boolean isBindableFromAddress() {
		return false;
	}

	/** Replies the overlooker on the agent repository.
	 * 
	 * @return the overlooker on the agent repository.
	 */
	protected final Repository<AgentAddress,Agent> getAgentRepository() {
		return getKernelContext().getAgentRepository();
	}

	/** Set the name of the application and the kernel agent.
	 * This function  changes the property {@link JanusProperty#JANUS_APPLICATION_NAME}
	 * and the name of the kernel agent.
	 * 
	 * @param name is the name of the application.
	 */
	protected final void setApplicationNameAndAgentName(String name) {
		if (name==null
			|| "".equals(name) //$NON-NLS-1$
			|| JanusProperties.DEFAULT_APPLICATION_NAME.equals(name)) {
			setName(Locale.getString(KernelAgent.class, "KERNEL_AGENT_NAME")); //$NON-NLS-1$
		}
		else {
			getKernelContext().getPrivilegedJanusPropertySetter().setPrivilegedProperty(
					JanusProperty.JANUS_APPLICATION_NAME, name);
			assert(name.equals(System.getProperty(JanusProperty.JANUS_APPLICATION_NAME.getPropertyName())));
			setName(Locale.getString(KernelAgent.class, "KERNEL_AGENT_NAME_WITH_APP", name)); //$NON-NLS-1$
		}
	}

	/** Set the value of a property with privileged access.
	 * Privileged access is required when a property can be set
	 * only from a kernel agent.
	 * 
	 * @param property is the property to set.
	 * @param value is the value of the property.
	 */
	protected final void setPrivilegiedProperty(JanusProperty property, String value) {
		getKernelContext().getPrivilegedJanusPropertySetter().setPrivilegedProperty(
				property, value);
	}

	/** Remove the persistent groups which are not used during the given delay.
	 * 
	 * @param delay is the minimal inactivity delay for a group.
	 * @param unit is the time unit of the delay.
	 */
	protected final void removeInactivePersistentGroups(float delay, TimeUnit unit) {
		PrivilegedPersistentGroupCleanerService serv = getKernelContext().getPrivilegedJanusPersistentGroupCleaner();
		assert(serv!=null);
		serv.removeInactivePersistentGroups(delay, unit);
	}

	/**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's <tt>get</tt> method will
     * return <tt>null</tt> upon <em>successful</em> completion.
	 * 
	 * @param task is the task to run.
     * @return a Future representing pending completion of the task
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if the task is null
	 */
	protected final Future<?> submitTask(Runnable task) {
		ExecutorService service = getKernelContext().getExecutorService();
		assert(service!=null);
		return service.submit(task);
	}

	/**
     * Creates and executes a periodic action that becomes enabled first
     * after the given initial delay, and subsequently with the
     * given delay between the termination of one execution and the
     * commencement of the next.  If any execution of the task
     * encounters an exception, subsequent executions are suppressed.
     * Otherwise, the task will only terminate via cancellation or
     * termination of the executor.
	 * 
     * @param task is the task to execute
     * @param initialDelay is the time to delay first execution
     * @param delay is the delay between the termination of one
     * execution and the commencement of the next
     * @param unit is the time unit of the initialDelay and delay parameters
     * @return a ScheduledFuture representing pending completion of
     *         the task, and whose <tt>get()</tt> method will throw an
     *         exception upon cancellation
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if command is null
     * @throws IllegalArgumentException if delay less than or equal to zero
     * @since 0.4
	 */
	protected final ScheduledFuture<?> submitTaskWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
		ScheduledExecutorService service = getKernelContext().getScheduledExecutorService();
		assert(service!=null);
		return service.scheduleWithFixedDelay(task, initialDelay, delay, unit);
	}

    /**
     * Creates and executes a periodic action that becomes enabled first
     * after the given initial delay, and subsequently with the given
     * period; that is executions will commence after
     * <tt>initialDelay</tt> then <tt>initialDelay+period</tt>, then
     * <tt>initialDelay + 2 * period</tt>, and so on.
     * If any execution of the task
     * encounters an exception, subsequent executions are suppressed.
     * Otherwise, the task will only terminate via cancellation or
     * termination of the executor.  If any execution of this task
     * takes longer than its period, then subsequent executions
     * may start late, but will not concurrently execute.
     *
     * @param task is the task to execute
     * @param initialDelay is the time to delay first execution
     * @param period is the period between successive executions
     * @param unit is the time unit of the initialDelay and period parameters
     * @return a ScheduledFuture representing pending completion of
     *         the task, and whose <tt>get()</tt> method will throw an
     *         exception upon cancellation
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if command is null
     * @throws IllegalArgumentException if period less than or equal to zero
     * @since 0.4
     */
	protected final ScheduledFuture<?> submitTaskAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
		ScheduledExecutorService service = getKernelContext().getScheduledExecutorService();
		assert(service!=null);
		return service.scheduleAtFixedRate(task, initialDelay, period, unit);
	}

    /**
     * Creates and executes a one-shot action that becomes enabled
     * after the given delay.
     *
     * @param task is the task to execute
     * @param delay is the time from now to delay execution
     * @param unit is the time unit of the delay parameter
     * @return a ScheduledFuture representing pending completion of
     *         the task and whose <tt>get()</tt> method will return
     *         <tt>null</tt> upon completion
     * @throws RejectedExecutionException if the task cannot be
     *         scheduled for execution
     * @throws NullPointerException if command is null
     * @since 0.4
     */
	protected final ScheduledFuture<?> submitTaskAtFixedRate(Runnable task, long delay, TimeUnit unit) {
		ScheduledExecutorService service = getKernelContext().getScheduledExecutorService();
		assert(service!=null);
		return service.schedule(task, delay, unit);
	}

	/**
	 * Forward the specified <code>Message</code> to the receiver specified
	 * in the message context.
	 * <p>
	 * This function does not change the emitter not receiver of the message.
	 * 
	 * @param message is the message to send
	 * @return the address of the agent which has received the message.
	 * @MESSAGEAPI
	 */
	protected final Address forwardMessage(Message message) {
		assert(message!=null);
		Address adr = message.getSender();
		if (adr instanceof RoleAddress) {
			// Forward the message in the organizational context
			//
			// The privileged message transport service must be used.
			PrivilegedMessageTransportService pmts = getKernelContext().getPrivilegedMessageTransportService();
			if (pmts==null) return null;
			return pmts.forwardMessage(message);
		}

		// Forward the message in the agent context
		adr = message.getReceiver();
		if (adr instanceof AgentAddress) {
			return forwardMessage(message, (AgentAddress)adr);
		}
		return forwardMessage(message, (AgentAddress[])null);
	}

	/**
	 * Forward the specified broadcast <code>Message</code> to the receiver specified
	 * in the message context.
	 * <p>
	 * This function does not change the emitter not receiver of the message.
	 * 
	 * @param message is the message to send
	 * @MESSAGEAPI
	 */
	protected final void forwardBroadcastMessage(Message message) {
		assert(message!=null);
		Address adr = message.getSender();
		if (adr instanceof RoleAddress) {
			//
			// The privileged message transport service must be used.
			PrivilegedMessageTransportService pmts = getKernelContext().getPrivilegedMessageTransportService();
			if (pmts!=null) 
				pmts.forwardBroadcastMessage(message);
		}
		else {
			forwardBroadcastMessage(message, new AgentAddress[0]);
		}
	}

	/** Replies the Kernel wrapper for this agent.
	 * 
	 * @return the Kernel wrapper for this agent.
	 */
	public Kernel toKernel() {
		return this.kernelWrapper;
	}

	/** 
	 * Remove agent from the kernel.
	 * 
	 * @param agent
	 * @return the status of the removal
	 */
	Status removeAgentFromKernel(Agent agent) {
		Status s;
		
		// Destroy this agent
		assert(this.context!=null);
		AgentAddress adr = agent.getAddress();
		assert(adr!=null);
		if (agent.isAlive()) {
			try {
				s = agent.proceedPrivateDestruction();
				
				if (s!=null && s.isLoggable()) {
					s.logOn(getLogger());
				}
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable e) {
				s = new ExceptionStatus(e);
				getLogger().log(
						Level.SEVERE,
						e.getLocalizedMessage(),
						e);
			}
		}
		else {
			s = StatusFactory.ok(this);
		}
		
		this.context.getProbeManager().release(agent);
		this.context.getAgentRepository().remove(adr);
		agent.executionResource = null;
		agent.creator = null;
		agent.kernel = null;

		boolean isKernel = (agent instanceof KernelAgent);
		if (isKernel) {
			KernelAgent ka = (KernelAgent)agent;
			Kernels.remove(ka);
			ka.context.destroy();
		}
		
		fireAgentKilling(agent, isKernel);

		// Allow to wait on agent termination.
		// Notify waiters about termination.
		synchronized(agent) {
			agent.notifyAll();
		}
		if (isKernel) {
			Kernel k = ((KernelAgent)agent).toKernel();
			synchronized(k) {
				k.notifyAll();
			}
		}
		
		// Remove all memory foot print for the agent.
		agent.dispose();

		getLogger().fine(Locale.getString(
				KernelAgent.class,
				"AGENT_KILLED", //$NON-NLS-1$
				adr.toString()));
		
		return s;
	}

	/**
	 * {@inheritDoc}
	 * @EXECUTIONAPI
	 */
	@Override
	public final CRIOContext getCRIOContext() {
		return this.context;
	}

	/**
	 * {@inheritDoc}
	 * @EXECUTIONAPI
	 */
	@Override
	public final KernelContext getKernelContext() {
		return this.context;
	}

	@Override
	protected Logger createLoggerInstance() {
		return LoggerUtil.createKernelLogger(getClass(), getTimeManager(), getAddress());
	}
	
	/** {@inheritDoc}
	 */
	@Override
	Status proceedPrivateBehaviour() {
		clearMailbox();
		Status s = super.proceedPrivateBehaviour();

		// The creation of the threads is differed to be sure that
		// the agent was properly removed from the activator.
		synchronized(this) {
			Iterator<Agent> iterator = this.newHeavyAgents.iterator();
			Agent a;
			while (iterator.hasNext()) {
				a = iterator.next();
				try {
					this.context.getExecutorService().submit(new AgentThread(a));
				}
				catch(AssertionError e) {
					throw e;
				}
				catch(Throwable e) {
					getLogger().log(Level.SEVERE, Throwables.toString(e), e);
				}
				iterator.remove();
			}
		}
		return s;
	}

	/** {@inheritDoc}
	 * <p>
	 * A KernelAgent has the same suicidal behaviour as a {@link ActivatorAgent},
	 * except that it must take into account light agents (activate by itself),
	 * and the heavy holons.
	 */
	@Override
	boolean isSelfKillableNow() {
		// Does the kernel wan't to commit a suicide from its Agent's point of view?
		// If kernel does not want to commit suicide or has some role or
		// agent to schedule, it must not kill itself.
		// Agent.isSelfKillableNow() gives an answer to this question, except for
		
		// Does any agent was launched, except the kernel?
		int indicator = -1;
		if (this.isNonKernelAgentLaunched.get()) {
			AgentRepository repo = getKernelContext().getAgentRepository();
			assert(repo!=null);
			int idx = repo.contains(getAddress()) ? 1 : 0;
			indicator = (repo.size()<=idx) ? 1 : 0;
		}

		return isSelfKillableIndicator(-1,indicator);
	}

	/** {@inheritDoc}
	 * @KILLAPI
	 */
	@Override
	protected final Status killMe() {
		AgentAddress me = getAddress();
		AgentRepository repository = this.context.getAgentRepository();
		assert(repository!=null);
		if (!repository.contains(getAddress())) return StatusFactory.ok(this);
		// Does any agent exists (repository contains this kernel holon and another one)?
		if (repository.size()>1) {
			// Force the agents to be killed, 
			// except for the kernel agent will may
			// commit a suicide later
			Iterator<AgentAddress> iterator = repository.iterator();
			MultipleStatus ms = new MultipleStatus();
			assert(me!=null);
			AgentAddress adr;
			while (iterator.hasNext()) {
				adr = iterator.next();
				if (!me.equals(adr))
					ms.addStatus(kill(this, adr));
			}
			// Force the activator to be marked as used
			// It will permits to this agent to commit
			// a suicide later
			setCommitSuicide(true);
			AgentActivator activator = getActivator();
			assert(activator!=null);
			activator.used();
			
			// Ensure that the kernel agent will be kill
			// after a given delay.
			JanusProperties props = getCRIOContext().getProperties();
			assert(props!=null);
			ScheduledExecutorService shedServ = this.context.getScheduledExecutorService();
			try {
				shedServ.schedule(new KillAwaiter(this),
						props.getLong(JanusProperty.JANUS_KERNEL_KILL_TIMEOUT),
						TimeUnit.MILLISECONDS);
			}
			catch(AssertionError ae) {
				throw ae;
			}
			catch(RejectedExecutionException e) {
				ms.addStatus(new SingleStatus(
						StatusSeverity.WARNING,
						Integer.toString(System.identityHashCode(this)),
						KernelStatusConstants.NO_KERNEL_TERMINATION_LOOK_UP_TASK,
						null, e));
			}
			
			return ms.pack(this);
		}

		return kill(this, me);
	}


	/** {@inheritDoc}
	 * @KILLAPI
	 */
	@Override
	protected final Status kill(AgentAddress dyingEntityAddress) {
		return kill(this, dyingEntityAddress);
	}
	
	/** Kill all the agents supported by this kernel.
	 * 
	 * @return the status of the request.
	 */
	protected final Status killAll() {
		MultipleStatus s = new MultipleStatus();
		// Force the thread agents to be paused
		getKernelContext().setKernelPaused(true);
		for(AgentAddress adr : getAgentRepository()) {
			kill(this, adr);
		}
		getKernelContext().setKernelPaused(false);
		return s.pack(this);
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
	 * @param killer is the agent which is trying to kill another agent.
	 * @param dyingEntityAddress is the address of the dying entity.
	 * @return the status of the operation
	 * @KILLAPI
	 */
	Status kill(
			Agent killer,
			AgentAddress dyingEntityAddress) {
		assert(killer!=null);
		assert(dyingEntityAddress!=null);
		
		AgentAddress killerAddress = killer.getAddress();
		assert(killerAddress!=null);
		
		AgentRepository agents = this.context.getAgentRepository();
		assert(agents!=null);
		
		Agent dyingAgent = agents.get(dyingEntityAddress);
		if (dyingAgent==null)
			return new SingleStatus(
					StatusSeverity.ERROR,
					killer.getAddress().toString(),
					KernelStatusConstants.INVALID_ADDRESS_AGENT_NOT_FOUND);
		
		AgentLifeState state = dyingAgent.getState();
		if (state!=AgentLifeState.ALIVE)
			return new SingleStatus(
					StatusSeverity.WARNING,
					killerAddress.toString(),
					KernelStatusConstants.AGENT_IS_DEAD);

		//
		// Check if the killer has the right to kill the agent
		//
		
		// If true, the killer is a top-level agent or is trying to kill itself
		boolean hasRemovalRight = (dyingEntityAddress.equals(killerAddress) || killer instanceof KernelAgent);
		
		if (!hasRemovalRight) {
			// the killer is a non-top-level agent
			// it has the right to kill only if it is the agent's creator.
			AgentAddress creator = dyingAgent.getCreator();
			hasRemovalRight = (creator!=null && creator.equals(killerAddress));
		}
			
		if (hasRemovalRight) {
			dyingAgent.setState(AgentLifeState.DYING);
			return StatusFactory.ok(killerAddress.toString());
		}
		
		return new SingleStatus(
				StatusSeverity.ERROR,
				killerAddress.toString(),
				KernelStatusConstants.KILL_AGENT_FORBIDDEN);
	}
	
	/** Kill all the threads manager by this kernel agent.
	 * <p>
	 * This function does not invokes {@link Kernel#kill()} on
	 * all existing heavy agents.
	 * It directly shutting down all the execution resources
	 * (ie. threads).
	 * <p>
	 * Caution: Invoking this function may put your virtual machine
	 * inside an invalid state.
	 */
	void shutdownNow() {
		// Prevent to accept new threads
		this.context.getExecutorService().shutdown();
		this.context.getScheduledExecutorService().shutdown();

		// Clear repositories
		AgentRepository repository = this.context.getAgentRepository();
		Agent[] agents = new Agent[repository.size()];
		repository.values().toArray(agents);
		repository.clear();
		Kernels.remove(this);
		
		// Register for an hard shutdown
		this.context.getExecutorService().shutdownNow();
		this.context.getScheduledExecutorService().shutdownNow();

		// Kill agent threads that were not interrupted
		AgentThread resourceThread;	
		AgentExecutionResource resource;
		for(Agent ag : agents) {
			if (ag!=null) {
				resource = ag.executionResource;
				if (resource instanceof AgentThread) {
					resourceThread = (AgentThread)resource;
					resourceThread.kill();
				}
			}
		}
	}

	/** Launch the given agent as a light agent.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	@Override
	protected final AgentAddress launchLightAgent(
			Agent agent, 
			String name,
			Object... initParameters) {
		return launchLightAgent(false, null, agent, name, null, initParameters);
	}

	/** Launch the given agent as a light agent.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	@Override
	protected final AgentAddress launchLightAgent(
			Agent agent,
			Object... initParameters) {
		return launchLightAgent(false, null, agent, null, null, initParameters);
	}

	/** Launch the given agent as a light agent.
	 * 
	 * @param <T> is the type of the agent.
	 * @param agent is the agent to initialize and launch
	 * @param activator is the activator to use, never <code>null</code>.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	@Override
	protected final <T extends Agent> AgentAddress launchLightAgent(
			T agent, 
			AgentActivator activator,
			Object... initParameters) {
		return launchLightAgent(false, null, agent, null, activator, initParameters);
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
	@Override
	protected final <T extends Agent> AgentAddress launchLightAgent(
			T agent, 
			String name,
			AgentActivator activator,
			Object... initParameters) {
		return launchLightAgent(false, null, agent, name, activator, initParameters);
	}
	
	/** Launch the given agent as a light agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param differedExecution is <code>true</code> if the agent execution may
	 * be differed until the next invocation to {@link #launchDifferedExecutionAgents()},
	 * <code>false</code> to execute the agent immediately.
	 * @param creator is the agent which has created the agent.
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param activator is the activator to use, never <code>null</code>.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	AgentAddress launchLightAgent(
			boolean differedExecution,
			AgentAddress creator,
			Agent agent, 
			String name,
			AgentActivator activator,
			Object[] initParameters) {
		assert(agent!=null);
		
		Logger logger = getLogger();
		
		AgentLifeState state = agent.getState();
		
		if (state==AgentLifeState.ALIVE) {
			if (name!=null)
				logger.warning(Locale.getString(
						KernelAgent.class,
						"AGENT_ALREADY_LAUNCHED_W_NAME", //$NON-NLS-1$
						agent.getAddress(), name));
			else
				logger.warning(Locale.getString(
						KernelAgent.class,
						"AGENT_ALREADY_LAUNCHED_WO_NAME", //$NON-NLS-1$ 
						agent.getAddress()));
			return agent.getAddress();
		}
		
		if (state!=AgentLifeState.UNBORN) {
			if (name!=null)
				logger.warning(Locale.getString(
						KernelAgent.class,
						"AGENT_IS_DYING_W_NAME", //$NON-NLS-1$ 
						agent.getAddress(), name));
			else
				logger.warning(Locale.getString(
						KernelAgent.class,
						"AGENT_IS_DYING_WO_NAME", //$NON-NLS-1$ 
						agent.getAddress()));
			return null;
		}
		
		assert(state==AgentLifeState.UNBORN);
		
		AgentAddress adr = agent.getAddress();
		assert(adr!=null);
		
		if (name!=null) adr.setName(name);
		
		this.context.getAgentRepository().add(adr, agent);
		agent.kernel = new WeakReference<KernelAgent>(this);
		agent.creator = creator;
		
		AgentActivator currentActivator = activator;
		if (currentActivator==null)
			currentActivator = getActivator();
		
		if (differedExecution) {
			synchronized (this.differedLightAgents) {
				this.differedLightAgents.add(new DifferedLightAgentInfo(
						agent,
						initParameters,
						currentActivator,
						(agent instanceof KernelAgent)));
			}
		}
		else {
			currentActivator.addAgent(agent, initParameters);
			agent.creationDate = this.context.getTimeManager().getCurrentTime();
			logger.fine(Locale.getString(
					KernelAgent.class,
					"LIGHT_AGENT_LAUNCHED", //$NON-NLS-1$
					adr.toString()));
			fireAgentLaunching(agent, (agent instanceof KernelAgent));
		}

		return adr;
	}

	/** Change the execution method of the agent, and
	 * switch to an light/nothreaded method if possible.
	 * 
	 * @param agent
	 * @return <code>true</code> if the execution method has changed;
	 * otherwise <code>false</code>.
	 * @EXECUTIONAPI
	 * @since 0.5
	 */
	synchronized final boolean setLightAgent(Agent agent) {
		AgentActivator currentActivator = getActivator();
		if (currentActivator==null) return false;

		AgentExecutionResource thread = agent.executionResource;
		if (thread==null) return false;
		assert(thread instanceof AgentThread);
		
		agent.isMigrating.set(true);
		((AgentThread)thread).kill(new MigrationListener(agent, currentActivator));
		
		return true;
	}

	/** Launch the given agent as a heavy agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the kernel agent initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	@Override
	protected final AgentAddress launchHeavyAgent(
			Agent agent,
			Object... initParameters) {
		return launchHeavyAgent(false, null, agent, null, initParameters);
	}

	/** Launch the given agent as a heavy agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the kernel agent initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	@Override
	protected final AgentAddress launchHeavyAgent(
			Agent agent, 
			String name,
			Object... initParameters) {
		return launchHeavyAgent(false, null, agent, name, initParameters);
	}
	
	/** Launch the given agent as a heavy agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the kernel agent initialization
	 * parameters will be passed.
	 * 
	 * @param differedExecution is <code>true</code> if the agent execution may
	 * be differed until the next invocation to {@link #launchDifferedExecutionAgents()},
	 * <code>false</code> to execute the agent immediately.
	 * @param creator is the agent which has created the agent.
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @LAUNCHINGAPI
	 */
	AgentAddress launchHeavyAgent(
			boolean differedExecution,
			AgentAddress creator, 
			Agent agent, 
			String name,
			Object[] initParameters) {
		assert(agent!=null);
		
		Logger logger = getLogger();
		
		AgentLifeState state = agent.getState();

		if (state==AgentLifeState.ALIVE) {
			if (name!=null)
				logger.warning(Locale.getString(
						KernelAgent.class,
						"AGENT_ALREADY_LAUNCHED_W_NAME", //$NON-NLS-1$
						agent.getAddress(), name));
			else
				logger.warning(Locale.getString(
						KernelAgent.class,
						"AGENT_ALREADY_LAUNCHED_WO_NAME", //$NON-NLS-1$ 
						agent.getAddress()));
			return agent.getAddress();
		}
		
		if (state!=AgentLifeState.UNBORN) {
			if (name!=null)
				logger.warning(Locale.getString(
						KernelAgent.class,
						"AGENT_IS_DYING_W_NAME", //$NON-NLS-1$ 
						agent.getAddress(), name));
			else
				logger.warning(Locale.getString(
						KernelAgent.class,
						"AGENT_IS_DYING_WO_NAME", //$NON-NLS-1$ 
						agent.getAddress()));
			return null;
		}
		
		assert(state==AgentLifeState.UNBORN);

		AgentAddress adr = agent.getAddress();
		assert(adr!=null);
		
		if (name!=null) adr.setName(name);
		
		this.context.getAgentRepository().add(adr, agent);
		agent.kernel = new WeakReference<KernelAgent>(this);
		agent.creator = creator;

		if (initParameters!=null && initParameters.length>0)
			agent.personalInitParameters = initParameters;
		else
			agent.personalInitParameters = null;
		
		AgentThread at = new AgentThread(
				agent,
				getInitializationParameters());
		
		if (differedExecution) {
			synchronized(this.differedHeavyAgents) {
				this.differedHeavyAgents.add(at);
			}
		}
		else {
			this.context.getExecutorService().submit(at);
		}
		
		return adr;
	}

	/** Change the execution method of the agent, and
	 * switch to an heavy/threaded method if possible.
	 *
	 * @param agent
	 * @return <code>true</code> if the execution method has changed;
	 * otherwise <code>false</code>.
	 * @EXECUTIONAPI
	 * @since 0.5
	 */
	synchronized final boolean setHeavyAgent(Agent agent) {
		AgentActivator currentActivator = getActivator();
		if (currentActivator==null) return false;
		agent.isMigrating.set(true);
		if (!currentActivator.removeAgent(agent)) return false;
		
		// The creation of the threads is differed to be sure that
		// the agent was properly removed from the activator.
		this.newHeavyAgents.add(agent);
		
		return true;
	}
	
	/** Launch the agents which were laucnhed with a differed execution.
	 */
	protected void launchDifferedExecutionAgents() {
		synchronized(this.differedHeavyAgents) {
			Iterator<AgentThread> iterator = this.differedHeavyAgents.iterator();
			ExecutorService exec = this.context.getExecutorService();
			while (iterator.hasNext()) {
				exec.submit(iterator.next());
			}
			this.differedHeavyAgents.clear();
		}
		synchronized(this.differedLightAgents) {
			Iterator<DifferedLightAgentInfo> iterator = this.differedLightAgents.iterator();
			DifferedLightAgentInfo info;
			while (iterator.hasNext()) {
				info = iterator.next();
				info.launch();
			}
			this.differedLightAgents.clear();
		}
	}
	
	/** Add listener on kernel events.
	 * 
	 * @param listener
	 */
	public void addKernelListener(KernelListener listener) {
		addEventListener(KernelListener.class, listener);
	}

	/** Remove listener on kernel events.
	 * 
	 * @param listener
	 */
	public void removeKernelListener(KernelListener listener) {
		removeEventListener(KernelListener.class, listener);
	}
	
	/** Fire agent arrival.
	 * 
	 * @param agent is the launched agent.
	 * @param isKernelAgent indicates if the launched agent is a kernel agent or not.
	 */
	void fireAgentLaunching(Agent agent, boolean isKernelAgent) {
		if (!isKernelAgent) {
			this.isNonKernelAgentLaunched.set(true);
		}
		
		KernelEvent event = new KernelEvent(
				KernelEventType.AGENT_LAUNCHING,
				this.kernelWrapper,
				agent.getAddress(),
				isKernelAgent);
		if (isKernelAgent) {
			for(KernelListener listener : getEventListeners(KernelListener.class)) {
				listener.kernelAgentLaunched(event);
			}
		}
		else {
			for(KernelListener listener : getEventListeners(KernelListener.class)) {
				listener.agentLaunched(event);
			}
		}
		
		if (agent instanceof ChannelInteractable) {
			getKernelContext().getChannelManager().fireChannelInteractableLaunched((ChannelInteractable) agent);
		}
		
	}
	
	/** Fire agent departure.
	 * 
	 * @param agent is the killed agent.
	 * @param isKernelAgent indicates if the killed agent is a kernel agent or not.
	 */
	void fireAgentKilling(Agent agent, boolean isKernelAgent) {
		if(agent instanceof ChannelInteractable){
			getKernelContext().getChannelManager().fireChannelInteractableKilled((ChannelInteractable) agent);
		}

		KernelEvent event = new KernelEvent(
				KernelEventType.AGENT_KILLING,
				this.kernelWrapper,
				agent.getAddress(),
				isKernelAgent);
		if (isKernelAgent) {
			for(KernelListener listener : getEventListeners(KernelListener.class)) {
				listener.kernelAgentKilled(event);
			}
		}
		else {
			for(KernelListener listener : getEventListeners(KernelListener.class)) {
				listener.agentKilled(event);
			}
		}
	}

	/** Fire uncatched exception event.
	 * 
	 * @param error is the uncatched exception
	 * @return <code>true</code> if the given error may
	 * be sent to logger, <code>false</code> otherwise.
	 */
	protected boolean fireUncatchedException(Throwable error) {
		boolean sentToLogger = true;
		for(KernelListener listener : getEventListeners(KernelListener.class)) {
			if (!listener.exceptionUncatched(error))
				sentToLogger = false;
		}
		return sentToLogger;
	}
	
	/**
	 * Listener on events for migration of the agents.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @LAUNCHINGAPI
	 */
	private static class MigrationListener implements Runnable {
		
		private final Agent agent;
		private final AgentActivator activator;
		
		/**
		 * @param agent
		 * @param activator
		 */
		public MigrationListener(Agent agent, AgentActivator activator) {
			this.agent = agent;
			this.activator = activator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			this.agent.executionResource = null;
			this.activator.addAgent(this.agent);
		}
		
	}

	/**
	 * Agent that represents and run the kernel of the Janus platform.
	 * 
	 * @author $Author: ngaud$
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @LAUNCHINGAPI
	 */
	private class AgentThread implements AgentExecutionResource, Runnable {

		private Agent agent;
		private Object[] initParameters;
		
		private boolean kill = false;
		private Collection<Runnable> killingListeners = null;
		
		/**
		 * @param runAgent is the agent run by this execution resource.
		 * @param params are the parameters to pass to init functions.
		 */
		public AgentThread(Agent runAgent, Object[] params) {
			assert(runAgent!=null);
			this.agent = runAgent;
			this.initParameters = params;
			this.agent.executionResource = this;
		}
		
		/**
		 * @param runAgent is the agent run by this execution resource.
		 */
		public AgentThread(Agent runAgent) {
			assert(runAgent!=null);
			this.agent = runAgent;
			this.initParameters = null;
			this.agent.executionResource = this;
		}

		/** Kill this thread and the agent.
		 */
		public void kill() {
			this.kill = true;
		}

		/** Kill this thread and the agent.
		 * 
		 * @param listener on kill.
		 */
		public void kill(Runnable listener) {
			if (this.killingListeners==null) {
				this.killingListeners = new ArrayList<Runnable>();
			}
			this.killingListeners.add(listener);
			this.kill = true;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void run() {
			Logger logger = this.agent.getLogger();
			assert(logger!=null);
			try {
				Status s = null;
				
				// Display the welcome message
				// Initialize
				if (!this.agent.isMigrating.get()) {
					this.agent.creationDate = this.agent.getKernelContext().getTimeManager().getCurrentTime();
					AgentAddress adr = this.agent.getAddress();
					logger.fine(Locale.getString(
							KernelAgent.class,
							"HEAVY_AGENT_LAUNCHED", //$NON-NLS-1$
							adr.toString()));
					fireAgentLaunching(this.agent , this.agent instanceof KernelAgent);
					String n = adr.getName();
					if (n!=null) setName(n);
					s = this.agent.proceedPrivateInitialization(this.initParameters);
					this.initParameters = null;
				}

				this.agent.isMigrating.set(false);

				if (s!=null && s.isFailure()) {
					if (s.isLoggable()) {
						s.logOn(logger);
					}
					KernelAgent.this.removeAgentFromKernel(this.agent);
				}
				else {
					
					assert(this.agent.getState()==AgentLifeState.ALIVE);
					
					if (this.agent instanceof KernelAgent) {
						((KernelAgent)this.agent).isLaunch.set(true);
						logger.info( 
								Locale.getString(KernelAgent.class, 
										"INTRODUCTION_MESSAGE")); //$NON-NLS-1$
					}

					// Live
					try {
						while (!this.kill && this.agent.getState()==AgentLifeState.ALIVE) {
							if (!getKernelContext().isKernelPaused() && !this.agent.wakeUpIfSleeping()) {
								s = this.agent.proceedPrivateBehaviour();
								if (s!=null && s.isLoggable()) {
									s.logOn(logger);
								}
								if (s!=null && s.isFailure() && s.getSeverity()!=StatusSeverity.CANCEL) {
									this.kill = true; // Force to kill the agent because is has failed
								}
							}
							Thread.yield();
						}
					}
					finally {
						if (!this.agent.isMigrating.get()) {
							if (this.agent instanceof KernelAgent) {
								((KernelAgent)this.agent).isLaunch.set(false);
								logger.info( 
										Locale.getString(KernelAgent.class, 
												"DEPARTURE_MESSAGE")); //$NON-NLS-1$
							}
							KernelAgent.this.removeAgentFromKernel(this.agent);
						}
						String n = Locale.getString(KernelAgent.class, "KILLED_THREAD_NAME"); //$NON-NLS-1$
						if (n!=null) setName(n);
					}
				}
			}
			catch(Throwable e) {
				if (fireUncatchedException(e)) {
					StringWriter sw = new StringWriter();
					StringBuffer buffer = sw.getBuffer();
					buffer.append(e.getLocalizedMessage());
					buffer.append('\n');
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					logger.severe(buffer.toString());
				}
			}
			finally {			
				this.agent = null;
				this.initParameters = null;
				
				Collection<Runnable> listeners = this.killingListeners;
				this.killingListeners = null;
				if (listeners!=null) {
					for(Runnable listener : listeners) {
						listener.run();
					}
				}
			}
		}
		
		/** {@inheritDoc}
		 */
		@Override
		public long getId() {
			return Thread.currentThread().getId();
		}
		
		/** {@inheritDoc}
		 */
		@Override
		public void setName(String name) {
			try {
				Thread.currentThread().setName(name);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				// ignore this exception because
				// it has no effect on the platform itself.
			}
		}

		/** {@inheritDoc}
		 */
		@Override
		public String getName() {
			return Thread.currentThread().getName();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return getName();
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class KernelWrapper implements Kernel {

		/**
		 */
		public KernelWrapper() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object o) {
			if (o instanceof Kernel) {
				return KernelAgent.this.getAddress().equals(((Kernel)o).getAddress());
			}
			if (o instanceof AgentAddress) {
				return KernelAgent.this.getAddress().equals((AgentAddress)o);
			}
			if (o instanceof Agent) {
				return KernelAgent.this.getAddress().equals(((Agent)o).getAddress());
			}
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return KernelAgent.this.getAddress().hashCode();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void waitUntilTermination() throws InterruptedException {
			KernelAgent.this.waitUntilTermination();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void waitUntilTermination(long timeout) throws InterruptedException {
			KernelAgent.this.waitUntilTermination(timeout);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CRIOContext getCRIOContext() {
			return KernelAgent.this.getCRIOContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public KernelContext getKernelContext() {
			return KernelAgent.this.getKernelContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchHeavyAgent(Agent agent, Object... initParameters) {
			return KernelAgent.this.launchHeavyAgent(
					false, // differed agent?
					null, // creator
					agent, // launched agent
					null, // agent name
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchHeavyAgent(Agent agent, String name, Object... initParameters) {
			return KernelAgent.this.launchHeavyAgent(
					false, // differed agent?
					null, // creator
					agent, // launched agent
					name, // agent name
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchLightAgent(Agent agent, String name, Object... initParameters) {
			return KernelAgent.this.launchLightAgent(
					false, // differed agent?
					null, // creator
					agent, // launched agent
					name, // agent name
					null, // activator
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchLightAgent(Agent agent, Object... initParameters) {
			return KernelAgent.this.launchLightAgent(
					false, // differed agent?
					null, // creator
					agent, // launched agent
					null, // agent name
					null, // activator
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchLightAgent(
				Agent agent, AgentActivator activator, Object... initParameters) {
			return KernelAgent.this.launchLightAgent(
					false, // differed agent?
					null, // creator
					agent, // launched agent
					null, // agent name
					activator, // activator
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchLightAgent(
				Agent agent, String name,
				AgentActivator activator,
				Object... initParameters) {
			return KernelAgent.this.launchLightAgent(
					false, // differed agent?
					null, // creator
					agent, // launched agent
					name, // agent name
					activator, // activator
					initParameters); // init parameters
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitHeavyAgent(Agent agent,
				Object... initParameters) {
			return KernelAgent.this.launchHeavyAgent(
					true, // differed agent?
					null, // creator
					agent, // launched agent
					null, // agent name
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitHeavyAgent(Agent agent, String name,
				Object... initParameters) {
			return KernelAgent.this.launchHeavyAgent(
					true, // differed agent?
					null, // creator
					agent, // launched agent
					name, // agent name
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitLightAgent(Agent agent, String name,
				Object... initParameters) {
			return KernelAgent.this.launchLightAgent(
					true, // differed agent?
					null, // creator
					agent, // launched agent
					name, // agent name
					null, // activator
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitLightAgent(Agent agent,
				Object... initParameters) {
			return KernelAgent.this.launchLightAgent(
					true, // differed agent?
					null, // creator
					agent, // launched agent
					null, // agent name
					null, // activator
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitLightAgent(Agent agent,
				AgentActivator activator, Object... initParameters) {
			return KernelAgent.this.launchLightAgent(
					true, // differed agent?
					null, // creator
					agent, // launched agent
					null, // agent name
					activator, // activator
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitLightAgent(Agent agent,
				String name, AgentActivator activator,
				Object... initParameters) {
			return KernelAgent.this.launchLightAgent(
					true, // differed agent?
					null, // creator
					agent, // launched agent
					name, // agent name
					activator, // activator
					initParameters); // init parameters
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void launchDifferedExecutionAgents() {
			KernelAgent.this.launchDifferedExecutionAgents();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status kill() {
			return KernelAgent.this.killMe();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isAlive() {
			return KernelAgent.this.isAlive();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress getAddress() {
			return KernelAgent.this.getAddress();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentLifeState getState() {
			return KernelAgent.this.getState();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean canCommitSuicide() {
			return KernelAgent.this.canCommitSuicide();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addKernelListener(KernelListener listener) {
			KernelAgent.this.addKernelListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeKernelListener(KernelListener listener) {
			KernelAgent.this.removeKernelListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addAgentLifeStateListener(AgentLifeStateListener listener) {
			KernelAgent.this.addAgentLifeStateListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAgentLifeStateListener(AgentLifeStateListener listener) {
			KernelAgent.this.removeAgentLifeStateListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChannelManager getChannelManager() {
			return KernelAgent.this.getKernelContext().getChannelManager();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProbeManager getProbeManager() {
			return KernelAgent.this.getKernelContext().getProbeManager();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SizedIterator<AgentAddress> getAgents() {
			return KernelAgent.this.getKernelContext().getLocalAgents();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Logger getLogger() {
			return KernelAgent.this.getLogger();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pause() {
			getKernelContext().setKernelPaused(true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isPaused() {
			return getKernelContext().isKernelPaused();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void resume() {
			getKernelContext().setKernelPaused(false);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void addGroupListener(GroupListener listener) {
			KernelAgent.this.addGroupListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void removeGroupListener(GroupListener listener) {
			KernelAgent.this.removeGroupListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress createGroup(
				Class<? extends Organization> organization,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions) {
			return KernelAgent.this.createGroup(organization, obtainConditions, leaveConditions);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress createGroup(
				Class<? extends Organization> organization,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions,
				String groupName) {
			return KernelAgent.this.createGroup(organization, obtainConditions, leaveConditions);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress createGroup(
				OrganizationFactory<? extends Organization> factory,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions) {
			return KernelAgent.this.createGroup(factory, obtainConditions, leaveConditions);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress createGroup(
				OrganizationFactory<? extends Organization> factory,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions,
				String groupName) {
			return KernelAgent.this.createGroup(factory, obtainConditions, leaveConditions, groupName);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress createGroup(Class<? extends Organization> organization) {
			return KernelAgent.this.createGroup(organization);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress createGroup(Class<? extends Organization> organization, String groupName) {
			return KernelAgent.this.createGroup(organization, groupName);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress createGroup(OrganizationFactory<?> factory) {
			return KernelAgent.this.createGroup(factory);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getExistingGroup(Class<? extends Organization> organization) {
			return KernelAgent.this.getExistingGroup(organization);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GroupAddress> getExistingGroups(Class<? extends Organization> organization) {
			return KernelAgent.this.getExistingGroups(organization);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getExistingGroup(OrganizationFactory<? extends Organization> factory) {
			return KernelAgent.this.getExistingGroup(factory);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress getOrCreateGroup(Class<? extends Organization> organization) {
			return KernelAgent.this.getOrCreateGroup(organization);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress getOrCreateGroup(Class<? extends Organization> organization, String groupName) {
			return KernelAgent.this.getOrCreateGroup(organization, groupName);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress getOrCreateGroup(OrganizationFactory<? extends Organization> factory) {
			return KernelAgent.this.getOrCreateGroup(factory);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress getOrCreateGroup(
				OrganizationFactory<? extends Organization> factory,
				String groupName) {
			return KernelAgent.this.getOrCreateGroup(factory, groupName);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress getOrCreateGroup(UUID id,
				Class<? extends Organization> organization,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions,
				MembershipService membership, boolean distributed,
				boolean persistent, String groupName) {
			return KernelAgent.this.getOrCreateGroup(
					id, organization, obtainConditions,
					leaveConditions, membership, distributed, persistent,
					groupName);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public GroupAddress getOrCreateGroup(UUID id,
				Class<? extends Organization> organization, String groupName) {
			return KernelAgent.this.getOrCreateGroup(id, organization, groupName);
		}
		
		/** {@inheritDoc}
		 */
		@Override
		public void createCheckPoint(OutputStream stream) throws IOException {
			// Force the platform to be paused
			if (!isPaused()) {
				pause();
				while(!isPaused()) {
					Thread.yield();
				}
			}
			// Output the state of the platform
			ObjectOutputStream oos = new ObjectOutputStream(stream);
			oos.writeObject(KernelAgent.this);
		}

	}

	/** This runnable task permits to detect a kernel agent
	 * which has queried a killing action on itself and
	 * is not dead after a certain timout.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class KillAwaiter implements Runnable {

		private final WeakReference<KernelAgent> agent;
		
		/**
		 * @param agent
		 */
		public KillAwaiter(KernelAgent agent) {
			this.agent = new WeakReference<KernelAgent>(agent);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			KernelAgent ag = this.agent.get();
			if (ag!=null && !ag.getState().isMortuary()) {
				JanusProperties props = ag.getCRIOContext().getProperties();
				assert(props!=null);
				ag.getLogger().severe(
						Locale.getString(
								KernelAgent.class,
								"KILLING_TIMEOUT", //$NON-NLS-1$
								TimeUnit.SECONDS.convert(
										props.getLong(JanusProperty.JANUS_KERNEL_KILL_TIMEOUT),
										TimeUnit.MILLISECONDS),
								ag.getAddress()));
				ag.shutdownNow();
			}
		}

	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DifferedLightAgentInfo {

		private Agent agent;
		private Object[] initParameters;
		private AgentActivator activator;
		private boolean isKernel;
		
		/**
		 * @param agent is the agent to launch.
		 * @param initParameters are the initializaztion parameters for the agent.
		 * @param activator is the activator which may execute the agent.
		 * @param isKernel is <code>true</code> if the agent is a kernel,
		 * <code>false</code> otherwise.
		 */
		public DifferedLightAgentInfo(
				Agent agent,
				Object[] initParameters,
				AgentActivator activator,
				boolean isKernel) {
			this.agent = agent;
			this.initParameters = initParameters;
			this.activator = activator;
			this.isKernel = isKernel;
		}
		
		/**
		 * Launch the differed agent.
		 */
		public void launch() {
			AgentAddress adr = this.agent.getAddress();
			this.activator.addAgent(this.agent, this.initParameters);
			this.agent.creationDate = this.agent.getKernelContext().getTimeManager().getCurrentTime();
			this.agent.getLogger().fine(Locale.getString(
					KernelAgent.class,
					"LIGHT_AGENT_LAUNCHED", //$NON-NLS-1$
					adr.toString()));
			fireAgentLaunching(this.agent, this.isKernel);
			this.agent = null;
			this.initParameters = null;
			this.activator = null;

		}
		
	}
		
}
