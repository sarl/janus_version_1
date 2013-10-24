/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2012 Janus Core Developers
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
package org.janusproject.kernel.mmf.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivator;
import org.janusproject.kernel.agent.AgentLifeState;
import org.janusproject.kernel.agent.AgentLifeStateListener;
import org.janusproject.kernel.agent.ChannelManager;
import org.janusproject.kernel.agent.KernelContext;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.agent.ProbeManager;
import org.janusproject.kernel.channels.ChannelInteractableListener;
import org.janusproject.kernel.credential.Credentials;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.mmf.JanusApplication;
import org.janusproject.kernel.mmf.JanusModule;
import org.janusproject.kernel.mmf.KernelAuthority;
import org.janusproject.kernel.mmf.KernelOperation;
import org.janusproject.kernel.mmf.KernelService;
import org.janusproject.kernel.mmf.KernelServiceEvent;
import org.janusproject.kernel.mmf.KernelServiceEvent.KernelServiceEventType;
import org.janusproject.kernel.mmf.KernelServiceListener;
import org.janusproject.kernel.status.MultipleStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.event.ListenerCollection;
import org.janusproject.kernel.util.sizediterator.SizedIterator;
import org.janusproject.kernel.util.throwable.Throwables;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

/**
 * OSGi Implementation of the Janus kernel service.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OSGiKernelService implements KernelService, KernelListener,
		ServiceListener {

	private final Logger logger;

	private Kernel kernel;

	private KernelAuthority kernelAuthority = null;

	private final ListenerCollection<KernelServiceListener> listeners = new ListenerCollection<KernelServiceListener>();

	private final OSGiModuleService moduleService;

	private final BundleContext context;

	private JanusApplication application;

	/**
	 * Create the OSGi service for the Janus kernel. All instances of the OSGi
	 * service may use the same Janus kernel instance.
	 * 
	 * @param context
	 */
	public OSGiKernelService(BundleContext context) {
		this.logger = Logger.getLogger(getClass().getName());
		this.context = context;
		this.moduleService = new OSGiModuleService(context);
		startAlreadyRegisteredApplication();
		this.context.addServiceListener(this);
	}

	/**
	 * @return the moduleService
	 */
	public OSGiModuleService getModuleService() {
		return this.moduleService;
	}

	/**
	 * The authority associted to the kernel.
	 * 
	 * @return the kernel authority.
	 */
	@Override
	public KernelAuthority getKernelAuthority() {
		return this.kernelAuthority;
	}

	/**
	 * Change the kernel authority for this service.
	 * 
	 * @param kernelAuthority
	 *            is the authority to set.
	 */
	public void setKernelAuthority(KernelAuthority kernelAuthority) {
		this.kernelAuthority = kernelAuthority;
	}

	/**
	 * Add listener on service events.
	 * 
	 * @param listener
	 */
	public void addKernelServiceListener(KernelServiceListener listener) {
		this.listeners.add(KernelServiceListener.class, listener);
	}

	/**
	 * Remove listener on service events.
	 * 
	 * @param listener
	 */
	public void removeKernelServiceListener(KernelServiceListener listener) {
		this.listeners.remove(KernelServiceListener.class, listener);
	}

	/**
	 * Fire kernel service event.
	 * 
	 * @param event
	 */
	protected void fireKernelServiceEvent(KernelServiceEvent event) {
		for (KernelServiceListener listener : this.listeners
				.getListeners(KernelServiceListener.class)) {
			listener.kernelServiceEvent(event);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status requestKernelStop(Credentials credentials) {
		Status status = StatusFactory.ok(this);
		if (this.application.isKeepKernelAlive()) {
			String msg = Locale.getString(OSGiKernelService.class, "KERNEL_STOP_CANCEL"); //$NON-NLS-1$
			this.logger.warning(msg);
			return StatusFactory.cancel(msg);
		}

		if (this.kernelAuthority != null) {
			fireKernelServiceEvent(new KernelServiceEvent(this,
					KernelServiceEventType.OPERATION_REQUESTED,
					KernelOperation.KERNEL_STOP));
			status = this.kernelAuthority.authorizeKernelOperation(
					KernelOperation.KERNEL_STOP, credentials, null);
		}

		if (status.isSuccess()) {

			fireKernelServiceEvent(new KernelServiceEvent(this,
					KernelServiceEventType.OPERATION_APPROVED,
					KernelOperation.KERNEL_STOP));
			kill();
			fireKernelServiceEvent(new KernelServiceEvent(this,
					KernelServiceEventType.OPERATION_EXECUTED,
					KernelOperation.KERNEL_STOP));
		}

		return status;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress getAddress() {
		return this.kernel.getAddress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CRIOContext getCRIOContext() {
		return this.kernel.getCRIOContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KernelContext getKernelContext() {
		return this.kernel.getKernelContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentLifeState getState() {
		return this.kernel.getState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAlive() {
		return this.kernel.isAlive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canCommitSuicide() {
		return this.kernel.canCommitSuicide();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status kill() {
		return this.kernel.kill();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress launchHeavyAgent(Agent agent, Object... initParams) {
		return this.kernel.launchHeavyAgent(agent, initParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress launchHeavyAgent(Agent agent, String name,
			Object... initParams) {
		return this.kernel.launchHeavyAgent(agent, name, initParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress launchLightAgent(Agent agent, String name,
			Object... initParams) {
		return this.kernel.launchLightAgent(agent, name, initParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress launchLightAgent(Agent agent, Object... initParams) {
		return this.kernel.launchLightAgent(agent, initParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress launchLightAgent(Agent agent, AgentActivator activator,
			Object... initParams) {
		return this.kernel.launchLightAgent(agent, activator, initParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress launchLightAgent(Agent agent, String name,
			AgentActivator activator, Object... initParams) {
		return this.kernel.launchLightAgent(agent, name, activator, initParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void launchDifferedExecutionAgents() {
		this.kernel.launchDifferedExecutionAgents();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress submitHeavyAgent(Agent agent, Object... initParameters) {
		return this.kernel.submitHeavyAgent(agent, initParameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress submitHeavyAgent(Agent agent, String name,
			Object... initParameters) {
		return this.kernel.submitHeavyAgent(agent, name, initParameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress submitLightAgent(Agent agent, String name,
			Object... initParameters) {
		return this.kernel.submitLightAgent(agent, name, initParameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress submitLightAgent(Agent agent, Object... initParameters) {
		return this.kernel.submitLightAgent(agent, initParameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress submitLightAgent(Agent agent, AgentActivator activator,
			Object... initParameters) {
		return this.kernel.submitLightAgent(agent, activator, initParameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress submitLightAgent(Agent agent, String name,
			AgentActivator activator, Object... initParameters) {
		return this.kernel.submitLightAgent(agent, name, activator,
				initParameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAgentLifeStateListener(AgentLifeStateListener listener) {
		this.kernel.addAgentLifeStateListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addKernelListener(KernelListener listener) {
		this.kernel.addKernelListener(listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAgentLifeStateListener(AgentLifeStateListener listener) {
		this.kernel.removeAgentLifeStateListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeKernelListener(KernelListener listener) {
		this.kernel.removeKernelListener(listener);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void agentKilled(KernelEvent event) {
		// Do nothing
	}

	/** {@inheritDoc}
	 */
	@Override
	public void agentLaunched(KernelEvent event) {
		// Do nothing
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean exceptionUncatched(Throwable error) {
		// Do nothing
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void kernelAgentKilled(KernelEvent event) {
		if (!this.kernel.isAlive()) {
			fireKernelServiceEvent(new KernelServiceEvent(this,
					KernelServiceEventType.OPERATION_EXECUTED,
					KernelOperation.KERNEL_STOP));
		}

	}

	/** {@inheritDoc}
	 */
	@Override
	public void kernelAgentLaunched(KernelEvent event) {
		// Do nothing
	}

	private Status authorizeKernelOperation(KernelOperation op,
			Credentials cred, Object... params) {
		if (getKernelAuthority() == null) {
			return StatusFactory.ok(
					Locale.getString(OSGiKernelService.class,
							"DEFAULT_AUTHORIZATION_STATUS")); //$NON-NLS-1$
		}
		return getKernelAuthority().authorizeKernelOperation(
				op, cred, params);
	}

	// /// Modules methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status startJanusModule(JanusModule module, Credentials credentials) {
		if (this.application != null) {
			this.logger.info(Locale.getString(OSGiKernelService.class,
					"STARTING_JANUS_MODULE", module.getClass().getName())); //$NON-NLS-1$
			Status s = authorizeKernelOperation(KernelOperation.MODULE_START,
					credentials, module);
			if (s.isSuccess()) {
				MultipleStatus ss = new MultipleStatus(s);
				ss.addStatus( module.start(this));
				return ss;
			}
			return s;
		}
		return StatusFactory.error(this, Locale.getString(OSGiKernelService.class,
				"NO_APPLICATION_DEFINED")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status stopJanusModule(JanusModule module, Credentials credentials) {
		if (this.application != null) {
			this.logger.info(Locale.getString(OSGiKernelService.class,
					"STOPPING_JANUS_MODULE", module.getClass().getName())); //$NON-NLS-1$
			Status s = authorizeKernelOperation(KernelOperation.MODULE_STOP,
					credentials, module);
			if (s.isSuccess()) {
				module.stop(this);
			}
			return s;
		}
		return StatusFactory.error(this, Locale.getString(OSGiKernelService.class,
				"NO_APPLICATION_DEFINED")); //$NON-NLS-1$
	}

	private boolean isAutoStartJanusModules() {
		return this.application != null
				&& this.application.isAutoStartJanusModules();
	}

	private void startJanusApplication(JanusApplication application) {
		assert application != null;
		// String appActivator = (String)
		// this.janusApplication.getHeaders().get(
		// IMMFConstants.JANUS_KERNEL_STOP_AUTH);
		if (this.application != null) {
			throw new IllegalStateException(
					Locale.getString(OSGiKernelService.class,
							"TOO_MANY_APPLICATION_ERROR", //$NON-NLS-1$
							application.getClass().getName()));
		}

		this.application = application;
		
		this.kernel = Kernels.create(
				!application.isKeepKernelAlive(), 
				application.getName(),
				application.getKernelAgentFactory());
		
		

		assert (this.kernel != null);
		this.kernel.addKernelListener(this);
		this.context
				.registerService(OSGiKernelService.class.getName(), this, null);
		this.logger.info(
				Locale.getString(OSGiKernelService.class,
						"STARTING_JANUS_APPLICATION", //$NON-NLS-1$
						application.getClass().getName()));

		registerKnownListeners();
		application.start(this);
		if (application.isAutoStartJanusModules()) {
			startRegisteredJanusModules(this.context);
		}

	}

	/** Replies the Janus application associated to this Kernel service.
	 * @return the application
	 */
	public JanusApplication getApplication() {
		return this.application;
	}

	/**
	 * Checks if the application was registered before we start this bundle. If
	 * so it starts it.
	 */
	private void startAlreadyRegisteredApplication() {
		ServiceReference ref = this.context
				.getServiceReference(JanusApplication.class.getName());
		if (ref != null) {
			startJanusApplication((JanusApplication) this.context.getService(ref));
		}

	}

	private Status startRegisteredJanusModules(BundleContext context) {
		ServiceReference[] refs;
		try {
			refs = context.getServiceReferences(JanusModule.class.getName(),
					null);
			if (refs != null) {
				for (int i = 0; i < refs.length; ++i) {
					JanusModule launcher = (JanusModule) context
							.getService(refs[i]);
					this.logger.finer(launcher.getClass().getName());
				}
			}

		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch (InvalidSyntaxException e) {
			this.logger.severe(Throwables.toString(e));
		}

		return StatusFactory.ok(this);
	}

	private void registerKnownListeners() {
		ServiceReference[] ref;
		try {
			ref = this.context.getServiceReferences(
					ChannelInteractableListener.class.getName(), null);
			if (ref != null) {

				for (int i = 0; i < ref.length; ++i) {
					ChannelInteractableListener cl = (ChannelInteractableListener) this.context
							.getService(ref[i]);
					this.kernel.getChannelManager().addChannelInteractableListener(cl);
				}
			}
			ref = this.context.getServiceReferences(KernelListener.class.getName(),
					null);
			if (ref != null) {

				for (int i = 0; i < ref.length; ++i) {
					KernelListener cl = (KernelListener) this.context
							.getService(ref[i]);
					this.kernel.addKernelListener(cl);
				}
			}
			ref = this.context.getServiceReferences(
					AgentLifeStateListener.class.getName(), null);
			if (ref != null) {

				for (int i = 0; i < ref.length; ++i) {
					AgentLifeStateListener cl = (AgentLifeStateListener) this.context
							.getService(ref[i]);
					this.kernel.addAgentLifeStateListener(cl);
				}
			}

			ref = this.context.getServiceReferences(
					KernelServiceListener.class.getName(), null);
			if (ref != null) {

				for (int i = 0; i < ref.length; ++i) {
					KernelServiceListener cl = (KernelServiceListener) this.context
							.getService(ref[i]);
					addKernelServiceListener(cl);
				}
			}
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch (InvalidSyntaxException e) {
			//
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public synchronized void serviceChanged(ServiceEvent event) {

		ServiceReference m_ref = null;
		if (event.getType() == ServiceEvent.REGISTERED) {

			// Get a reference to the service object.
			m_ref = event.getServiceReference();
			Object serv = this.context.getService(m_ref);

			// Janus Application and Janus Module are self-exclusive
			if (serv instanceof JanusApplication) {
				startJanusApplication((JanusApplication) serv);
			} else if (serv instanceof JanusModule && isAutoStartJanusModules()) {
				startJanusModule((JanusModule) serv, null);
			}

			// Others the service can play as well

			if (serv instanceof ChannelInteractableListener) {
				this.getChannelManager().addChannelInteractableListener((ChannelInteractableListener) serv);
			}
			if (serv instanceof KernelListener) {
				addKernelListener((KernelListener) serv);
			}
			if (serv instanceof KernelServiceListener) {
				addKernelServiceListener((KernelServiceListener) serv);
			}

			if (serv instanceof AgentLifeStateListener) {
				addAgentLifeStateListener((AgentLifeStateListener) serv);
			}

		}

		else if (event.getType() == ServiceEvent.UNREGISTERING) {
			// Get a reference to the service object.
			m_ref = event.getServiceReference();
			Object serv = this.context.getService(m_ref);
			if (serv instanceof ChannelInteractableListener) {
				this.getChannelManager().removeChannelInteractableListener((ChannelInteractableListener) serv);
			}
			if (serv instanceof KernelListener) {
				removeKernelListener((KernelListener) serv);
			}
			if (serv instanceof KernelServiceListener) {
				removeKernelServiceListener((KernelServiceListener) serv);
			}

			if (serv instanceof AgentLifeStateListener) {
				removeAgentLifeStateListener((AgentLifeStateListener) serv);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChannelManager getChannelManager() {
		return this.kernel.getChannelManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProbeManager getProbeManager() {
		return this.kernel.getProbeManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<AgentAddress> getAgents() {
		return this.kernel.getAgents();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger getLogger() {
		return this.kernel.getLogger();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pause() {
		this.kernel.pause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPaused() {
		return this.kernel.isPaused();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume() {
		this.kernel.resume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addGroupListener(GroupListener listener) {
		this.kernel.addGroupListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeGroupListener(GroupListener listener) {
		this.kernel.removeGroupListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress createGroup(Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		return this.kernel.createGroup(organization, obtainConditions, leaveConditions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress createGroup(Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			String groupName) {
		return this.kernel.createGroup(organization, obtainConditions, leaveConditions, groupName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress createGroup(
			OrganizationFactory<? extends Organization> factory,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		return this.kernel.createGroup(factory, obtainConditions, leaveConditions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress createGroup(
			OrganizationFactory<? extends Organization> factory,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			String groupName) {
		return this.kernel.createGroup(factory, obtainConditions, leaveConditions, groupName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress createGroup(Class<? extends Organization> organization) {
		return this.kernel.createGroup(organization);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress createGroup(Class<? extends Organization> organization,
			String groupName) {
		return this.kernel.createGroup(organization, groupName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress createGroup(OrganizationFactory<?> factory) {
		return this.kernel.createGroup(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress getExistingGroup(Class<? extends Organization> organization) {
		return this.kernel.getExistingGroup(organization);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GroupAddress> getExistingGroups(Class<? extends Organization> organization) {
		return this.kernel.getExistingGroups(organization);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress getExistingGroup(OrganizationFactory<? extends Organization> factory) {
		return this.kernel.getExistingGroup(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress getOrCreateGroup(Class<? extends Organization> organization) {
		return this.kernel.getOrCreateGroup(organization);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress getOrCreateGroup(Class<? extends Organization> organization, String groupName) {
		return this.kernel.getOrCreateGroup(organization, groupName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress getOrCreateGroup(OrganizationFactory<? extends Organization> factory) {
		return this.kernel.getOrCreateGroup(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress getOrCreateGroup(
			OrganizationFactory<? extends Organization> factory,
			String groupName) {
		return this.kernel.getOrCreateGroup(factory, groupName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress getOrCreateGroup(UUID id,
			Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership, boolean distributed,
			boolean persistent, String groupName) {
		return this.kernel.getOrCreateGroup(id, organization, obtainConditions,
				leaveConditions, membership, distributed, persistent, groupName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupAddress getOrCreateGroup(UUID id,
			Class<? extends Organization> organization, String groupName) {
		return this.kernel.getOrCreateGroup(id, organization, groupName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void waitUntilTermination() throws InterruptedException {
		this.kernel.waitUntilTermination();
		notifyAll(); // Force the waiter on this object to wake up.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void waitUntilTermination(long timeout) throws InterruptedException {
		this.kernel.waitUntilTermination(timeout);
		notifyAll(); // Force the waiter on this object to wake up.
	}

	/** {@inheritDoc}
	 */
	@Override
	public void createCheckPoint(OutputStream stream) throws IOException {
		throw new UnsupportedOperationException();
	}

}
