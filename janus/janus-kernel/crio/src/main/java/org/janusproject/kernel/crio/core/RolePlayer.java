/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2013 Janus Core Developers
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
package org.janusproject.kernel.crio.core;

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentmemory.BlackBoardMemory;
import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.agentsignal.InstantSignalManager;
import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.agentsignal.SignalListener;
import org.janusproject.kernel.agentsignal.SignalManager;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.credential.Credentials;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityCallException;
import org.janusproject.kernel.crio.capacity.CapacityCaller;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationNotFoundException;
import org.janusproject.kernel.crio.capacity.TreeCapacityContainer;
import org.janusproject.kernel.crio.interaction.InvalidSenderRoleMessageException;
import org.janusproject.kernel.crio.organization.Group;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.role.RoleFactory;
import org.janusproject.kernel.crio.role.RoleNotFoundException;
import org.janusproject.kernel.crio.role.RolePlayingEvent;
import org.janusproject.kernel.crio.role.RolePlayingListener;
import org.janusproject.kernel.logger.LoggerProvider;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.repository.RepositoryOverlooker;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.event.ListenerCollection;
import org.janusproject.kernel.util.multicollection.MultiCollection;
import org.janusproject.kernel.util.random.RandomNumber;
import org.janusproject.kernel.util.sizediterator.EmptyIterator;
import org.janusproject.kernel.util.sizediterator.MultiSizedIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * This class must be implemented by all the objects playing a <code>Role</code>.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class RolePlayer implements CapacityCaller, LoggerProvider {

	private final PlayerAddress address;

	private Memory memory = null;
	private SignalManager signalManager = null;

	private CapacityContainer capacities = null;

	private RoleActivator roleActivator = new RoleActivator();

	private RepositoryGroupWrapper groupEventWrapper = null;
	
	/**
	 * Listeners on events.
	 */
	private ListenerCollection<? extends EventListener> listeners = null;

	/**
	 * Logger for this role player.
	 */
	private transient SoftReference<Logger> logger = null;

	/** Credentials for the role player.
	 */
	private Credentials credentials = null;
	
	/**
	 */
	public RolePlayer() {
		this.address = new PlayerAddress(this);
	}

	/**
	 * @param adr
	 *            is a precomputed address for this player.
	 */
	public RolePlayer(AgentAddress adr) {
		assert (adr != null);
		this.address = new PlayerAddress(this, adr.getUUID(), adr.getName());
	}

	/**
	 * @param capacityContainer
	 *            is the container of capacities.
	 */
	public RolePlayer(CapacityContainer capacityContainer) {
		assert (capacityContainer != null);
		this.address = new PlayerAddress(this);
		this.capacities = capacityContainer;
	}

	/**
	 * @param adr
	 *            is a precomputed address for this player.
	 * @param capacityContainer
	 *            is the container of capacities.
	 */
	public RolePlayer(AgentAddress adr, CapacityContainer capacityContainer) {
		assert (capacityContainer != null);
		this.address = new PlayerAddress(this, adr.getUUID(), adr.getName());
		this.capacities = capacityContainer;
	}
	
	/** Invoked this function when the role player should be disposed from
	 * the system. This function should not be invoked outside the kernel
	 * control.
	 */
	protected void dispose() {
		this.address.unbind();
	}
	
	/** Replies the overlooker on the group repository.
	 * 
	 * @return the overlooker on the group repository.
	 */
	protected final RepositoryOverlooker<GroupAddress> getGroupRepository() {
		return getCRIOContext().getGroupRepository().getOverlooker();
	}

	/** Replies the overlooker on the organization repository.
	 * 
	 * @return the overlooker on the organization repository.
	 */
	protected final RepositoryOverlooker<Class<? extends Organization>> getOrganizationRepository() {
		return getCRIOContext().getOrganizationRepository().getOverlooker();
	}

	/**
	 * Invoked just before the specified role is leaved.
	 * 
	 * @param role
	 * @since 0.5
	 */
	void roleReleasing(Role role) {
		this.roleActivator.removeRole(role);
	}

	/**
	 * Invoked when a role was leaved.
	 * 
	 * @param role
	 * @param event
	 */
	void roleTaken(Role role, RolePlayingEvent event) {
		this.roleActivator.addRole(role);
		firePlayRole(event);
	}

	/**
	 * Replies the CRIO execution context.
	 * 
	 * @return the CRIO context.
	 */
	public abstract CRIOContext getCRIOContext();

	/**
	 * Replies the address of the role player.
	 * 
	 * @return the address of the role player.
	 */
	@Override
	public final AgentAddress getAddress() {
		return this.address;
	}

	/**
	 * Replies an unique identifier that may
	 * represents this player in the current
	 * virtual machine.
	 * <p>
	 * These is no warranty about the unicity
	 * of the UUID among several Janus kernels.
	 * 
	 * @return the UUID of the player in the current
	 * virtual machine.
	 * @see #getAddress()
	 * @since 0.5
	 */
	public UUID getUUID() {
		return this.address.getUUID();
	}

	/**
	 * Replies the name of the role player.
	 * 
	 * @return the name of the role player.
	 */
	@Override
	public final String getName() {
		return this.address.getName();
	}

	/**
	 * Set the name of the role player.
	 * 
	 * @param name
	 *            is the new name of the role player.
	 */
	protected final void setName(String name) {
		this.address.setName(name);
	}

	/**
	 * Replies the logger associated to the role player.
	 * 
	 * @return the logger associated to the role player.
	 * @see #print(Object...)
	 * @see #debug(Object...)
	 * @see #error(Object...)
	 * @see #warning(Object...)
	 * @LOGGINGAPI
	 */
	@Override
	public final Logger getLogger() {
		Logger logger = (this.logger != null) ? this.logger.get() : null;
		if (logger == null) {
			logger = createLoggerInstance();
			this.logger = new SoftReference<Logger>(logger);
		}
		return logger;
	}

	/**
	 * Create a new instance of logger.
	 * The new instance is not binded to {@link #getLogger()}
	 * by <code>createLogger</code>.
	 * <p>
	 * This function should be overridden to  create a logger
	 * with specifical configuration.
	 * 
	 * @return a new logger.
	 * @LOGGINGAPI
	 */
	protected Logger createLoggerInstance() {
		return LoggerUtil.createAgentLogger(getClass(), getTimeManager(), getAddress());
	}

	/**
	 * Send the given message to the logger as information message.
	 * <p>
	 * Each of the given parameters is sent to {@link Logger#info(String)}.
	 * 
	 * @param message
	 *            is the list of object to sent to logger.
	 * @see #getLogger()
	 * @see #debug(Object...)
	 * @see #error(Object...)
	 * @see #warning(Object...)
	 * @LOGGINGAPI
	 */
	protected final void print(Object... message) {
		Logger logger = getLogger();
		for (Object m : message) {
			if (m != null)
				logger.info(m.toString());
		}
	}

	/**
	 * Send the given message to the logger as debugging message.
	 * <p>
	 * Each of the given parameters is sent to {@link Logger#fine(String)}.
	 * 
	 * @param message
	 *            is the list of object to sent to logger.
	 * @see #getLogger()
	 * @see #print(Object...)
	 * @see #error(Object...)
	 * @see #warning(Object...)
	 * @LOGGINGAPI
	 */
	protected final void debug(Object... message) {
		Logger logger = getLogger();
		for (Object m : message) {
			if (m != null)
				logger.fine(m.toString());
		}
	}

	/**
	 * Send the given message to the logger as error message.
	 * <p>
	 * Each of the given parameters is sent to {@link Logger#severe(String)}.
	 * 
	 * @param message
	 *            is the list of object to sent to logger.
	 * @see #getLogger()
	 * @see #print(Object...)
	 * @see #debug(Object...)
	 * @see #warning(Object...)
	 * @LOGGINGAPI
	 */
	protected final void error(Object... message) {
		Logger logger = getLogger();
		for (Object m : message) {
			if (m != null)
				logger.fine(m.toString());
		}
	}

	/**
	 * Send the given message to the logger as warning message.
	 * <p>
	 * Each of the given parameters is sent to {@link Logger#warning(String)}.
	 * 
	 * @param message
	 *            is the list of object to sent to logger.
	 * @see #getLogger()
	 * @see #print(Object...)
	 * @see #debug(Object...)
	 * @see #error(Object...)
	 * @LOGGINGAPI
	 */
	protected final void warning(Object... message) {
		Logger logger = getLogger();
		for (Object m : message) {
			if (m != null)
				logger.warning(m.toString());
		}
	}

	/**
	 * Replies the memory associated to the role player, or create one if a
	 * memory was never created.
	 * <p>
	 * If the memory was never set before, a {@link BlackBoardMemory} is
	 * automatically created.
	 * 
	 * @return the memory associated to the role player or <code>null</code>.
	 * @see #setMemory(Memory)
	 * @MINDAPI
	 */
	protected final Memory getMemory() {
		if (this.memory == null)
			this.memory = new BlackBoardMemory();
		return this.memory;
	}

	/**
	 * Replies the knowledge with the given identifier.
	 * 
	 * @param id
	 *            is the identifier of the knowledge.
	 * @return the data or <code>null</code>
	 * @MINDAPI
	 */
	protected final Object getMemorizedData(String id) {
		return getMemory().getMemorizedData(id);
	}

	/**
	 * Replies the knowledge with the given identifier.
	 * 
	 * @param <T>
	 *            is the type of the data.
	 * @param id
	 *            is the identifier of the knowledge.
	 * @param type
	 *            is the type of the data.
	 * @return the data or <code>null</code> if not set or not of the given
	 *         type.
	 * @MINDAPI
	 */
	protected final <T> T getMemorizedData(String id, Class<T> type) {
		return getMemory().getMemorizedData(id, type);
	}

	/**
	 * Replies if a knowledge with the given identifier is existing in the
	 * memory.
	 * 
	 * @param id
	 *            is the identifier of the knowledge.
	 * @return <code>true</code> if the knowledge is existing, otherwise
	 *         <code>false</code>
	 * @MINDAPI
	 */
	protected final boolean hasMemorizedData(String id) {
		return getMemory().hasMemorizedData(id);
	}

	/**
	 * Put a knowledge in the memory.
	 * 
	 * @param id
	 *            is the identifier of the knowledge.
	 * @param value
	 *            is the data to memorize.
	 * @return <code>true</code> if the knowledge was successfully saved,
	 *         otherwise <code>false</code>
	 * @MINDAPI
	 */
	protected final boolean putMemorizedData(String id, Object value) {
		return getMemory().putMemorizedData(id, value);
	}

	/**
	 * Remove a knowledge from the memory.
	 * 
	 * @param id
	 *            is the identifier of the knowledge.
	 * @MINDAPI
	 */
	protected final void removeMemorizedData(String id) {
		getMemory().removeMemorizedData(id);
	}

	/**
	 * Replies the current time manager.
	 * 
	 * @return the current time manager.
	 */
	@Override
	public final KernelTimeManager getTimeManager() {
		return getCRIOContext().getTimeManager();
	}

	/**
	 * Replies the role activator.
	 * 
	 * @return the role activator for this player.
	 * @GROUPAPI
	 */
	protected final RoleActivator getRoleActivator() {
		return this.roleActivator;
	}

	/**
	 * Set the role activator.
	 * 
	 * @param activator
	 *            is the role activator to use by this player.
	 * @GROUPAPI
	 */
	protected final void setRoleActivator(RoleActivator activator) {
		assert (activator != null);
		this.roleActivator = activator;
	}

	/**
	 * Set the memory manager.
	 * 
	 * @param memory
	 *            is the new memory manager.
	 * @see #getMemory()
	 * @MINDAPI
	 */
	protected final void setMemory(Memory memory) {
		assert (memory != null);
		this.memory = memory;
	}

	/**
	 * Replies the signal emitter associated to the role player, or create one
	 * if a emitter was never created.
	 * <p>
	 * If the emitter was never set before, a {@link InstantSignalManager} is
	 * automatically created.
	 * 
	 * @return the emitter associated to the role player or <code>null</code>.
	 * @see #addSignalListener(SignalListener)
	 * @see #removeSignalListener(SignalListener)
	 * @see #fireSignal(Signal)
	 * @see #setSignalManager(SignalManager)
	 * @MINDAPI
	 */
	protected final SignalManager getSignalManager() {
		if (this.signalManager == null) {
			this.signalManager = new InstantSignalManager(getCRIOContext().getProperties());
		}
		return this.signalManager;
	}

	/**
	 * Register a signal listener.
	 * 
	 * @param listener
	 * @MINDAPI
	 */
	protected final void addSignalListener(SignalListener listener) {
		getSignalManager().addSignalListener(listener);
	}

	/**
	 * Unregister a signal listener.
	 * 
	 * @param listener
	 * @MINDAPI
	 */
	protected final void removeSignalListener(SignalListener listener) {
		getSignalManager().removeSignalListener(listener);
	}

	/**
	 * Fire a signal.
	 * 
	 * @param signal
	 * @MINDAPI
	 */
	protected final void fireSignal(Signal signal) {
		getSignalManager().fireSignal(signal);
	}

	/**
	 * Set the signal manager used by this role player.
	 * 
	 * @param manager
	 *            is the new manager to use.
	 * @see #getSignalManager()
	 * @MINDAPI
	 * @since 0.5
	 */
	protected final void setSignalManager(SignalManager manager) {
		this.signalManager = manager;
	}

	/**
	 * Replies the capacity container associated to the role player, or create
	 * one if a container was never created.
	 * <p>
	 * If the container was never set before, a {@link TreeCapacityContainer} is
	 * automatically created.
	 * 
	 * @return the container associated to the role player or <code>null</code>.
	 * @see #setCapacityContainer(CapacityContainer)
	 * @CAPACITYAPI
	 */
	protected final CapacityContainer getCapacityContainer() {
		if (this.capacities == null)
			this.capacities = new TreeCapacityContainer();
		return this.capacities;
	}

	/**
	 * Set the capacity container used by this role player.
	 * 
	 * @param container
	 *            is the new container to use.
	 * @see #getCapacityContainer()
	 * @CAPACITYAPI
	 */
	protected final void setCapacityContainer(CapacityContainer container) {
		assert (container != null);
		if (this.capacities != null) {
			container.addAll(this.capacities);
		}
		this.capacities = container;
	}

	/**
	 * Replies the credentials for this role player.
	 * 
	 * @return the credentials for this role player.
	 */
	public Credentials getCredentials() {
		return this.credentials;
	}

	/**
	 * Sets the credentials for this role player.
	 * 
	 * @param credentials are the credentials for the player.
	 */
	protected final void setCredentials(Credentials credentials) {
		this.credentials = credentials;
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
	protected final <R extends Role> R getRole(GroupAddress group, Class<R> role) {
		assert (group != null);
		assert (role != null);

		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(group);
		if (grp == null)
			return null;

		return grp.getPlayedRole(getAddress(), role);
	}

	/**
	 * Replies played roles by this role player in given group.
	 * 
	 * @param group
	 * @return played roles.
	 * @GROUPAPI
	 */
	public final Collection<Class<? extends Role>> getRoles(GroupAddress group) {
		assert (group != null);

		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(group);
		if (grp == null)
			return Collections.emptyList();

		return grp.getPlayedRoles(getAddress());
	}

	/** Replies the address of the specified role in
	 * the specified group.
	 *  
	 * @param group is the address of the group.
	 * @param role is the played role.
	 * @param player is the player of the role
	 * @return the address or <code>null</code>.
	 * @since 0.5
	 */
	protected final RoleAddress getRoleAddress(GroupAddress group, Class<? extends Role> role, AgentAddress player) {
		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(group);
		return grp.getRoleAddress(role, player);
	}

	/** Replies the role addresses of this player.
	 *  
	 * @return the role address, never <code>null</code>.
	 * @since 0.5
	 */
	protected final SizedIterator<RoleAddress> getRoleAddresses() {
		MultiSizedIterator<RoleAddress> iterators = new MultiSizedIterator<RoleAddress>();
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		AgentAddress adr = getAddress();
		assert (adr != null);
		for (KernelScopeGroup grp : repo.values()) {
			iterators.addIterator(grp.getRoleAddresses(getAddress()));
		}
		return iterators;
	}

	/** Replies the role addresses of this player in the group.
	 *  
	 * @param group
	 * @return the role address, never <code>null</code>.
	 * @since 0.5
	 */
	protected final SizedIterator<RoleAddress> getRoleAddressesInGroup(GroupAddress group) {
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		AgentAddress adr = getAddress();
		assert (adr != null);
		KernelScopeGroup grp = repo.get(group);
		if (grp!=null) {
			return grp.getRoleAddresses();
		}
		return EmptyIterator.singleton();
	}

	/**
	 * Replies played roles by this role player.
	 * 
	 * @return played roles.
	 * @GROUPAPI
	 */
	public final Collection<Class<? extends Role>> getRoles() {
		MultiCollection<Class<? extends Role>> roles = new MultiCollection<Class<? extends Role>>();
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		AgentAddress adr = getAddress();
		assert (adr != null);
		for (KernelScopeGroup grp : repo.values()) {
			roles.addCollection(grp.getPlayedRoles(adr));
		}
		return roles;
	}

	/**
	 * Replies if this player is currently playing any role.
	 * 
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	public boolean isPlayingRole() {
		assert (this.roleActivator != null);
		return this.roleActivator.hasActivable();
	}

	/**
	 * Replies if this player is currently playing the given role in the given
	 * group.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @param group
	 *            is the group of the role.
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	public boolean isPlayingRole(Class<? extends Role> role, GroupAddress group) {
		assert (role != null);
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		KernelScopeGroup grp = repo.get(group);
		return (grp != null) && (grp.isPlayedRole(getAddress(), role));
	}

	/**
	 * Replies if this player is currently playing the given role in any group.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	public boolean isPlayingRole(Class<? extends Role> role) {
		assert (role != null);
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		for (KernelScopeGroup grp : repo.values()) {
			if ((grp != null) && (grp.isPlayedRole(getAddress(), role)))
				return true;
		}
		return false;
	}

	/**
	 * Replies if the given role is played by any role player in the given
	 * group.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @param group
	 *            is the group of the role.
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	public boolean isPlayedRole(Class<? extends Role> role, GroupAddress group) {
		assert (role != null);
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		KernelScopeGroup grp = repo.get(group);
		return (grp != null) && (grp.isPlayedRole(role));
	}

	/**
	 * Replies if this player is member of the given group.
	 * 
	 * @param group
	 *            is the group to test.
	 * @return <code>true</code> if the player in member of the group, otherwise
	 *         <code>false</code>
	 */
	public final boolean isMemberOf(GroupAddress group) {
		return isMemberOf(getAddress(), group);
	}

	/**
	 * Replies if the given player is member of the given group.
	 * 
	 * @param entity
	 *            is the entity to search for.
	 * @param group
	 *            is the group to test.
	 * @return <code>true</code> if the given entity in member of the group,
	 *         otherwise <code>false</code>
	 */
	public final boolean isMemberOf(AgentAddress entity, GroupAddress group) {
		assert (group != null);
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		KernelScopeGroup grp = repo.get(group);
		return (grp != null) && (grp.isPlayedRole(entity));
	}

	/**
	 * Replies played roles in the whole system.
	 * 
	 * @return played roles.
	 * @GROUPAPI
	 */
	public final Collection<Class<? extends Role>> getExistingRoles() {
		MultiCollection<Class<? extends Role>> multiCollection = new MultiCollection<Class<? extends Role>>();
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		for (KernelScopeGroup grp : repo.values()) {
			multiCollection.addCollection(grp.getPlayedRolesAsCollection());
		}
		return multiCollection;
	}

	/**
	 * Replies played roles in the group.
	 * 
	 * @param group
	 *            is the address of the group from which played roles may be
	 *            replied.
	 * @return played roles in the given group.
	 * @GROUPAPI
	 */
	public final SizedIterator<Class<? extends Role>> getExistingRoles(
			GroupAddress group) {
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		KernelScopeGroup grp = repo.get(group);
		if (grp != null) {
			return grp.getPlayedRoles();
		}
		return EmptyIterator.singleton();
	}

	/**
	 * Replies if this player is currently playing the given role in any group.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	public boolean isPlayedRole(Class<? extends Role> role) {
		assert (role != null);
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		for (KernelScopeGroup grp : repo.values()) {
			if (grp.isPlayedRole(role))
				return true;
		}
		return false;
	}

	//

	/**
	 * Replies the instance for the given organization.
	 * 
	 * @param organization
	 *            is the organization that must be instanced.
	 * @return organization instance in the current context.
	 * @GROUPAPI
	 */
	public Organization getOrganization(
			Class<? extends Organization> organization) {
		assert (organization != null);
		CRIOContext context = getCRIOContext();
		assert (context != null);
		Organization instance = OrganizationRepository.organization(context,
				organization, null);
		assert (instance != null);
		return instance;
	}

	private Organization getOrganizationSingleton(
			OrganizationFactory<? extends Organization> factory) {
		assert (factory != null);
		CRIOContext context = getCRIOContext();
		assert (context != null);
		Organization instance = OrganizationRepository.organization(context,
				factory, null);
		assert (instance != null);
		return instance;
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param organization
	 *            is the organization that must be instanced
	 * @param obtainConditions
	 *            is the list of conditions to respect to enter in the group
	 * @param leaveConditions
	 *            is the list of conditions to respect to leave out of the group
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected final GroupAddress createGroup(
			Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		return getOrganization(organization).createGroup(obtainConditions,
				leaveConditions);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param organization
	 *            is the organization that must be instanced
	 * @param obtainConditions
	 *            is the list of conditions to respect to enter in the group
	 * @param leaveConditions
	 *            is the list of conditions to respect to leave out of the group
	 * @param groupName is the name of the group.
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 * @since 0.4
	 */
	protected final GroupAddress createGroup(
			Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			String groupName) {
		return getOrganization(organization).createGroup(obtainConditions,
				leaveConditions, groupName);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @param obtainConditions
	 *            is the list of conditions to respect to enter in the group
	 * @param leaveConditions
	 *            is the list of conditions to respect to leave out of the group
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected final GroupAddress createGroup(
			OrganizationFactory<? extends Organization> factory,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		return getOrganizationSingleton(factory).createGroup(obtainConditions,
				leaveConditions);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @param obtainConditions
	 *            is the list of conditions to respect to enter in the group
	 * @param leaveConditions
	 *            is the list of conditions to respect to leave out of the group
	 * @param groupName is the name of the group.
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 * @since 0.4
	 */
	protected final GroupAddress createGroup(
			OrganizationFactory<? extends Organization> factory,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			String groupName) {
		return getOrganizationSingleton(factory).createGroup(obtainConditions,
				leaveConditions, groupName);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param organization
	 *            is the organization that must be instanced
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected final GroupAddress createGroup(
			Class<? extends Organization> organization) {
		return getOrganization(organization).createGroup();
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param organization
	 *            is the organization that must be instanced
	 * @param groupName is the name of the group.
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 * @since 0.4
	 */
	protected final GroupAddress createGroup(
			Class<? extends Organization> organization,
			String groupName) {
		return getOrganization(organization).createGroup(groupName);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory which may be used to create the
	 *            organization instance.
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected final GroupAddress createGroup(OrganizationFactory<?> factory) {
		return getOrganizationSingleton(factory).createGroup();
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory which may be used to create the
	 *            organization instance.
	 * @param groupName is the name of the group.
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected final GroupAddress createGroup(OrganizationFactory<?> factory, String groupName) {
		return getOrganizationSingleton(factory).createGroup(groupName);
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param organization
	 *            - the organization that the group have to implement
	 * @return the address of the group
	 * @GROUPAPI
	 */
	protected final GroupAddress getOrCreateGroup(
			Class<? extends Organization> organization) {
		return getOrganization(organization).group();
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param organization
	 *            - the organization that the group have to implement
	 * @param groupName is the name of the group, used only when creating a new group.
	 * @return the address of the group
	 * @GROUPAPI
	 * @since 0.4
	 */
	protected final GroupAddress getOrCreateGroup(
			Class<? extends Organization> organization,
			String groupName) {
		return getOrganization(organization).group(groupName);
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @return the address of the group
	 * @GROUPAPI
	 */
	protected final GroupAddress getOrCreateGroup(
			OrganizationFactory<? extends Organization> factory) {
		return getOrganizationSingleton(factory).group();
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @param groupName is the name of the group, used only when creating a new group.
	 * @return the address of the group
	 * @GROUPAPI
	 * @since 0.4
	 */
	protected final GroupAddress getOrCreateGroup(
			OrganizationFactory<? extends Organization> factory,
			String groupName) {
		return getOrganizationSingleton(factory).group(groupName);
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, do not create a new one.
	 * 
	 * @param organization
	 *            - the organization that the group have to implement
	 * @return the address of the group, or <code>null</code>
	 * @GROUPAPI
	 * @since 0.5
	 */
	public final GroupAddress getExistingGroup(
			Class<? extends Organization> organization) {
		return getOrganization(organization).getGroup();
	}

	/**
	 * Return all known groups of an organization.
	 * 
	 * @param organization
	 *            the organization that the group have to implement
	 * @return all known groups
	 * @GROUPAPI
	 */
	public final List<GroupAddress> getExistingGroups(
			Class<? extends Organization> organization) {
		return getOrganization(organization).getGroups();
	}

	/**
	 * Return all the groups in which this role player is playing a role.
	 * 
	 * @return all the groups in which this role player is playing a role.
	 * @GROUPAPI
	 */
	public final Collection<GroupAddress> getGroups() {
		Collection<GroupAddress> myGroups = new ArrayList<GroupAddress>();
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		AgentAddress adr = getAddress();
		assert (adr != null);
		for (KernelScopeGroup grp : repo.values()) {
			if (grp.isPlayedRole(adr))
				myGroups.add(grp.getAddress());
		}
		return myGroups;
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, do not create a new one.
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @return the address of the group, or <code>null</code>
	 * @GROUPAPI
	 * @since 0.5
	 */
	public final GroupAddress getExistingGroup(
			OrganizationFactory<? extends Organization> factory) {
		return getOrganizationSingleton(factory).getGroup();
	}

	/**
	 * Replies if the given group address corresponds to an existing group.
	 * 
	 * @param group
	 * @return <code>true</code> if the given group address corresponds to an
	 *         existing group, otherwise <code>false</code>
	 * @GROUPAPI
	 */
	public final boolean isGroup(GroupAddress group) {
		GroupRepository repo = getCRIOContext().getGroupRepository();
		assert (repo != null);
		return repo.contains(group);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * specified group.
	 * 
	 * @param role
	 *            is the class of the requested role.
	 * @param group
	 *            is the group where the requested role is defined
	 * @param initParameters
	 *            is the set of parameters to pass to
	 *            {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected final RoleAddress requestRole(Class<? extends Role> role,
			GroupAddress group, Object... initParameters) {
		return requestRole(role, group, null, null, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * specified group.
	 * 
	 * @param role
	 *            is the class of the requested role.
	 * @param group
	 *            is the group where the requested role is defined
	 * @param accessContext
	 *            is the context of access control to use when instanciating the
	 *            role. If <code>null</code>, the default context will be used.
	 * @param initParameters
	 *            is the set of parameters to pass to
	 *            {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected final RoleAddress requestRole(Class<? extends Role> role,
			GroupAddress group, AccessControlContext accessContext,
			Object... initParameters) {
		return requestRole(role, group, null, accessContext, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * specified group.
	 * 
	 * @param role
	 *            is the class of the requested role.
	 * @param group
	 *            is the group where the requested role is defined
	 * @param factory
	 *            is the factory to invoke to create a new instance of the role.
	 *            If <code>null</code> the default factory will be used.
	 * @param initParameters
	 *            is the set of parameters to pass to
	 *            {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected final RoleAddress requestRole(Class<? extends Role> role,
			GroupAddress group, RoleFactory factory, Object... initParameters) {
		return requestRole(role, group, factory, null, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * specified group.
	 * 
	 * @param role
	 *            is the class of the requested role.
	 * @param group
	 *            is the group where the requested role is defined
	 * @param factory
	 *            is the factory to invoke to create a new instance of the role.
	 *            If <code>null</code> the default factory will be used.
	 * @param accessContext
	 *            is the context of access control to use when instanciating the
	 *            role. If <code>null</code>, the default context will be used.
	 * @param initParameters
	 *            is the set of parameters to pass to
	 *            {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected final RoleAddress requestRole(Class<? extends Role> role,
			GroupAddress group, RoleFactory factory,
			AccessControlContext accessContext, Object... initParameters) {
		// Do not pass by the role player to preserve computation time
		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(group);
		if (grp != null) {
			return grp.requestRole(
					this, role, factory, accessContext,
					initParameters);
		}
		return null;
	}

	/**
	 * Function allowing the request of the liberation of this role on the
	 * corresponding group.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @param role
	 *            is the class of the role to leave.
	 * @param group
	 *            is the group where the requested role is defined
	 * @return <code>true</code> if the request was accepted, <code>false</code>
	 *         else
	 * @GROUPAPI
	 */
	protected final boolean leaveRole(Class<? extends Role> role,
			GroupAddress group) {
		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(group);
		if (grp != null) {
			return grp.leaveRole(this, role);
		}
		return false;
	}
	
	/**
	 * Function allowing the request of the liberation of this role on the
	 * corresponding group.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @param role is the address of the role to leave.
	 * @return <code>true</code> if the request was accepted, <code>false</code>
	 *         else
	 * @GROUPAPI
	 * @since 1.0
	 */
	protected final boolean leaveRole(RoleAddress role) {
		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(
				role.getGroup());
		if (grp != null) {
			return grp.leaveRole(this, role.getRole());
		}
		return false;
	}

	/**
	 * Function allowing the request of the liberation of all the roles on the
	 * corresponding group.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @param group
	 *            is the group where the roles is defined
	 * @return <code>true</code> if at least one role was released,
	 *         <code>false</code> otherwise
	 * @GROUPAPI
	 */
	protected final boolean leaveAllRoles(GroupAddress group) {
		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(group);
		if (grp != null) {
			return grp.leaveAllRoles(this);
		}
		return false;
	}

	/**
	 * Function allowing the request of the liberation of all the roles played
	 * by this player.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @return <code>true</code> if at least one role was released,
	 *         <code>false</code> otherwise
	 * @GROUPAPI
	 */
	protected final boolean leaveAllRoles() {
		GroupRepository repos = getCRIOContext().getGroupRepository();
		assert (repos != null);
		boolean released = false;
		for (GroupAddress adr : repos) {
			if (leaveAllRoles(adr))
				released = true;
		}
		return released;
	}

	/**
	 * Replies the first available message in the mail box of the given role and
	 * remove it from the mailbox.
	 * 
	 * @param group
	 *            is the group of the played role.
	 * @param role
	 *            is the played role.
	 * @return the first available message, or <code>null</code> if the mailbox
	 *         is empty.
	 * @MESSAGEAPI
	 */
	protected final Message getMessage(GroupAddress group,
			Class<? extends Role> role) {
		Role r = getRole(group, role);
		if (r == null)
			throw new RoleNotFoundException(role);
		return r.getMessage();
	}

	/**
	 * Replies the first available message in the mail box of the given role and
	 * leave it inside the mailbox.
	 * 
	 * @param group
	 *            is the group of the played role.
	 * @param role
	 *            is the played role.
	 * @return the first available message, or <code>null</code> if the mailbox
	 *         is empty.
	 * @MESSAGEAPI
	 */
	protected final Message peekMessage(GroupAddress group,
			Class<? extends Role> role) {
		Role r = getRole(group, role);
		if (r == null)
			throw new RoleNotFoundException(role);
		return r.peekMessage();
	}

	/**
	 * Replies the messages in the mailbox of the given role. Each time an
	 * message is consumed from the replied iterable object, the corresponding
	 * message is removed from the mailbox.
	 * 
	 * @param group
	 *            is the group of the played role.
	 * @param role
	 *            is the played role.
	 * @return all the messages, never <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected final Iterable<Message> getMessages(GroupAddress group,
			Class<? extends Role> role) {
		Role r = getRole(group, role);
		if (r == null)
			throw new RoleNotFoundException(role);
		return r.getMessages();
	}

	/**
	 * Replies the messages in the mailbox of the given role. Each time an
	 * message is consumed from the replied iterable object, the corresponding
	 * message is NOT removed from the mailbox.
	 * 
	 * @param group
	 *            is the group of the played role.
	 * @param role
	 *            is the played role.
	 * @return all the messages, never <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected final Iterable<Message> peekMessages(GroupAddress group,
			Class<? extends Role> role) {
		Role r = getRole(group, role);
		if (r == null)
			throw new RoleNotFoundException(role);
		return r.peekMessages();
	}

	/**
	 * Indicates if the mailbox of the given role contains a message or not.
	 * 
	 * @param group
	 *            is the group of the played role.
	 * @param role
	 *            is the played role.
	 * @return <code>true</code> if the message contains at least one message,
	 *         otherwise <code>false</code>
	 * @MESSAGEAPI
	 */
	protected final boolean hasMessage(GroupAddress group,
			Class<? extends Role> role) {
		Role r = getRole(group, role);
		if (r == null)
			throw new RoleNotFoundException(role);
		return r.hasMessage();
	}

	/**
	 * Replies the number of messages in the mailbox of the given role.
	 * 
	 * @param group
	 *            is the group of the played role.
	 * @param role
	 *            is the played role.
	 * @return the number of messages in the mailbox.
	 * @MESSAGEAPI
	 */
	protected final long getMailboxSize(GroupAddress group,
			Class<? extends Role> role) {
		Role r = getRole(group, role);
		if (r == null)
			throw new RoleNotFoundException(role);
		return r.getMailboxSize();
	}

	/**
	 * Replies the mailbox of the given role.
	 * 
	 * @param group
	 *            is the group of the played role.
	 * @param role
	 *            is the played role.
	 * @return the mailbox.
	 * @MESSAGEAPI
	 */
	protected final Mailbox getMailbox(GroupAddress group,
			Class<? extends Role> role) {
		Role r = getRole(group, role);
		if (r == null)
			throw new RoleNotFoundException(role);
		return r.getMailbox();
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiverRole
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final RoleAddress sendMessage(GroupAddress group,
			Class<? extends Role> senderRole,
			Class<? extends Role> receiverRole, Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.sendMessage(receiverRole, message);
	}

	/**
	 * Forward the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiverRole
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(GroupAddress group,
			Class<? extends Role> senderRole,
			Class<? extends Role> receiverRole, Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.forwardMessage(receiverRole, message);
	}

	/**
	 * Forward the specified <code>Message</code> to the specified role.
	 * <p>
	 * This function does not change the emitter of the message.
	 * <p>
	 * The group is deducted from the given receiver address.
	 * 
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiver is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(
			Class<? extends Role> senderRole,
			RoleAddress receiver,
			Message message) {
		Role role = getRole(receiver.getGroup(), senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.forwardMessage(receiver, message);
	}

	/**
	 * Reply to the specified <code>Message</code>.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param messageToReplyTo is the message to reply to.
	 * @param message
	 *            is the message to send
	 * @since 0.5
	 * @MESSAGEAPI
	 */
	protected final void replyToMessage(
			GroupAddress group,
			Class<? extends Role> senderRole,
			Message messageToReplyTo,
			Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		role.replyToMessage(messageToReplyTo, message);
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiverRole
	 *            is the role which may receive the message.
	 * @param receiverAddress
	 *            is the address of the receiver.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver.
	 * @MESSAGEAPI
	 */
	protected final RoleAddress sendMessage(GroupAddress group,
			Class<? extends Role> senderRole,
			Class<? extends Role> receiverRole, AgentAddress receiverAddress,
			Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.sendMessage(receiverRole, receiverAddress, message);
	}

	/**
	 * Send the specified <code>Message</code> to the specified role.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * The group is deducted from the address of the receiver.
	 * 
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiver is the address of the receiver.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver.
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected final RoleAddress sendMessage(
			Class<? extends Role> senderRole,
			RoleAddress receiver,
			Message message) {
		Role role = getRole(receiver.getGroup(), senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.sendMessage(receiver, message);
	}

	/**
	 * Forward the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiverRole
	 *            is the role which may receive the message.
	 * @param receiverAddress
	 *            is the address of the receiver.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver.
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(GroupAddress group,
			Class<? extends Role> senderRole,
			Class<? extends Role> receiverRole, AgentAddress receiverAddress,
			Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.forwardMessage(receiverRole, receiverAddress, message);
	}

	/**
	 * Forward the specified <code>Message</code> to the role of the agent in
	 * the given message.
	 * <p>
	 * This function does not change the emitter not receiver of the message.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param message
	 *            is the message to send
	 * @return the receiver address.
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(GroupAddress group,
			Class<? extends Role> senderRole, Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.forwardMessage(message);
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiverRole
	 *            is the role which may receive the message.
	 * @param policy
	 *            is the receiver selection policy.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final RoleAddress sendMessage(GroupAddress group,
			Class<? extends Role> senderRole,
			Class<? extends Role> receiverRole,
			MessageReceiverSelectionPolicy policy, Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.sendMessage(receiverRole, policy, message);
	}

	/**
	 * Forward the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiverRole
	 *            is the role which may receive the message.
	 * @param policy
	 *            is the receiver selection policy.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(GroupAddress group,
			Class<? extends Role> senderRole,
			Class<? extends Role> receiverRole,
			MessageReceiverSelectionPolicy policy, Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		return role.forwardMessage(receiverRole, policy, message);
	}

	/**
	 * Send the specified <code>Message</code> to all the players of the given
	 * role, except the sender if it is playing the role.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiverRole
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 */
	protected final void broadcastMessage(GroupAddress group,
			Class<? extends Role> senderRole,
			Class<? extends Role> receiverRole, Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		role.broadcastMessage(receiverRole, message);
	}

	/**
	 * Forward the specified <code>Message</code> to all the players of the
	 * given role, except the sender if it is playing the role.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param group
	 *            is the group inside which the interaction may occur.
	 * @param senderRole
	 *            is the role played by this entity which is sending the
	 *            message.
	 * @param receiverRole
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @MESSAGEAPI
	 */
	protected final void forwardBroadcastMessage(GroupAddress group,
			Class<? extends Role> senderRole,
			Class<? extends Role> receiverRole, Message message) {
		Role role = getRole(group, senderRole);
		if (role == null)
			throw new InvalidSenderRoleMessageException(message);
		role.forwardBroadcastMessage(receiverRole, message);
	}

	/**
	 * Returns the list of the addresses of the entity currently playing the
	 * specified role defined on the specified group
	 * 
	 * @param role
	 *            is the role of which obtain the number of players
	 * @param group
	 *            is the group where the role is defined
	 * @return the addresses of the entity currently playing the specified role
	 * @GROUPAPI
	 */
	public final Iterator<AgentAddress> getPlayers(Class<? extends Role> role,
			GroupAddress group) {
		assert (role != null);
		assert (group != null);
		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(group);
		if (grp != null) {
			return grp.getRolePlayers(role);
		}
		return EmptyIterator.singleton();
	}

	/**
	 * Returns a randomly selected entity, which is currently playing the
	 * specified role defined on the specified group
	 * 
	 * @param role
	 *            is the role of which obtain the number of players
	 * @param group
	 *            is the group where the role is defined
	 * @return the selected address or <code>null</code> if none.
	 * @GROUPAPI
	 */
	public final AgentAddress getPlayer(Class<? extends Role> role,
			GroupAddress group) {
		assert (role != null);
		assert (group != null);
		KernelScopeGroup grp = getCRIOContext().getGroupRepository().get(group);
		if (grp != null) {
			DirectAccessCollection<AgentAddress> adrs = grp
					.getRolePlayerCollection(role);
			if (adrs != null && !adrs.isEmpty()) {
				if (adrs.size() == 1)
					return adrs.get(0);
				return adrs.get(RandomNumber.nextInt(adrs.size()));
			}
		}
		return null;
	}

	// ------------------------------------------------
	// Synchronious Execution
	// ------------------------------------------------

	/**
	 * Execute immediately the given capacity.
	 * 
	 * @param capacity
	 *            is the invoked capacity.
	 * @param role
	 *            is the role which may invoke the capacity.
	 * @param group
	 *            is the group of the role.
	 * @param parameters
	 *            are the values to pass to the capacity implementation.
	 * @return the execution context after execution.
	 * @throws Exception
	 * @CAPACITYAPI
	 */
	protected final CapacityContext executeCapacityCall(
			Class<? extends Capacity> capacity, Class<? extends Role> role,
			GroupAddress group, Object... parameters) throws Exception {
		assert (capacity != null);
		assert (role != null);
		assert (group != null);

		Role r = getRole(group, role);
		if (r == null)
			throw new CapacityCallException();

		CapacityContainer capacityContainer = getCapacityContainer();
		assert (capacityContainer != null);

		Capacity implementation = capacityContainer
				.selectImplementation(capacity);
		if (implementation == null || !(implementation instanceof CapacityImplementation))
			throw new CapacityImplementationNotFoundException(capacity);

		return CapacityExecutor.executeImmediately(capacity,
				(CapacityImplementation)implementation, this, r.getKernelScopeGroup(), r,
				parameters);
	}

	// ------------------------------------------------
	// Asynchronious Execution of Capacities
	// ------------------------------------------------

	/**
	 * Put the given capacity inside the execution queue.
	 * 
	 * @param capacity
	 *            is the invoked capacity.
	 * @param role
	 *            is the role which may invoke the capacity.
	 * @param group
	 *            is the group of the role.
	 * @param parameters
	 *            are the values to pass to the capacity implementation.
	 * @return the identifier of the task in the queue.
	 * @CAPACITYAPI
	 */
	protected final UUID submitCapacityCall(Class<? extends Capacity> capacity,
			Class<? extends Role> role, GroupAddress group,
			Object... parameters) {
		assert (capacity != null);
		assert (role != null);
		assert (group != null);

		Role r = getRole(group, role);
		if (r == null)
			throw new CapacityCallException();

		CapacityContainer capacityContainer = getCapacityContainer();
		assert (capacityContainer != null);

		Capacity implementation = capacityContainer
				.selectImplementation(capacity);
		if (implementation == null || !(implementation instanceof CapacityImplementation))
			throw new CapacityImplementationNotFoundException(capacity);

		// Do not pass by the role player to preserve computation time
		return getCRIOContext().getCapacityExecutor().submit(capacity,
				(CapacityImplementation)implementation, this, r.getKernelScopeGroup(), r,
				parameters);
	}

	/**
	 * Retrieves the result of the call with the specified identifier, waiting
	 * if necessary up to the specified wait time if the result is not
	 * available.
	 * <p>
	 * After calling this function, the task result is no more available. It
	 * means that following invocation of
	 * <code>consumeCapacityCallResult()</code> with the same identifier as
	 * parameter will always returns <code>null</code>.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link Role#submitCapacityCall(Class, Object...)}
	 * @param timeout
	 *            indicates how long to wait before giving up.
	 * @return the result of the call, or <tt>null</tt> if the specified waiting
	 *         time elapses before the result is available.
	 * @CAPACITYAPI
	 */
	protected final CapacityContext waitCapacityCallResult(UUID taskIdentifier,
			long timeout) {
		assert (taskIdentifier != null);
		return getCRIOContext().getCapacityExecutor().waitResult(getAddress(),
				taskIdentifier, timeout);
	}

	/**
	 * Retrieves the result of the call with the specified identifier, waiting
	 * if necessary up to the specified wait time if the result is not
	 * available.
	 * <p>
	 * After calling this function, the task result is no more available. It
	 * means that following invocation of
	 * <code>consumeCapacityCallResult()</code> with the same identifier as
	 * parameter will always returns <code>null</code>.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link Role#submitCapacityCall(Class, Object...)}
	 * @param timeout
	 *            indicates how long to wait before giving up.
	 * @param unit
	 *            is the time unit of the given timeout.
	 * @return the result of the call, or <tt>null</tt> if the specified waiting
	 *         time elapses before the result is available.
	 * @CAPACITYAPI
	 */
	protected final CapacityContext waitCapacityCallResult(UUID taskIdentifier,
			long timeout, TimeUnit unit) {
		assert (taskIdentifier != null);
		return getCRIOContext().getCapacityExecutor().waitResult(getAddress(),
				taskIdentifier, timeout, unit);
	}

	/**
	 * Replies if the result of the call with the specified identifier is
	 * available.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link Role#submitCapacityCall(Class, Object...)}
	 * @return <code>true</code> if the result is available, otherwhise
	 *         <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean hasCapacityCallResult(UUID taskIdentifier) {
		assert (taskIdentifier != null);
		return getCRIOContext().getCapacityExecutor().hasResult(getAddress(),
				taskIdentifier);
	}

	/**
	 * Retrieves the result of the call with the specified identifier, waiting
	 * if necessary up to the specified wait time if the result is not
	 * available.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link Role#submitCapacityCall(Class, Object...)}
	 * @return the result of the call, or <tt>null</tt> if the given identifier
	 *         is unkwown.
	 * @CAPACITYAPI
	 */
	protected final CapacityContext waitCapacityCallResult(UUID taskIdentifier) {
		assert (taskIdentifier != null);
		return getCRIOContext().getCapacityExecutor().waitResult(getAddress(),
				taskIdentifier);
	}

	/**
	 * Retrieves the result of the call with the specified identifier, do not
	 * wait if the result is not available.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link Role#submitCapacityCall(Class, Object...)}
	 * @return the result of the call, or <tt>null</tt> if the given identifier
	 *         is unkwown or the result is not yet available.
	 * @CAPACITYAPI
	 */
	protected final CapacityContext getCapacityCallResult(UUID taskIdentifier) {
		assert (taskIdentifier != null);
		return getCRIOContext().getCapacityExecutor().instantResult(
				getAddress(), taskIdentifier);
	}

	/**
	 * Cancel a capacity invocation.
	 * <p>
	 * This function stop the capacity running and force it to fail.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link Role#submitCapacityCall(Class, Object...)}
	 * @return <code>true</code> if the capacity was canceled, otherwise
	 *         <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean cancelCapacityCall(UUID taskIdentifier) {
		assert (taskIdentifier != null);
		return getCRIOContext().getCapacityExecutor().cancel(getAddress(),
				taskIdentifier);
	}

	/**
	 * Cancel a capacity invocation.
	 * <p>
	 * This function stop the capacity running and force it to fail.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link Role#submitCapacityCall(Class, Object...)}
	 * @param exception
	 *            is the exception which causes the cancelation.
	 * @return <code>true</code> if the capacity was canceled, otherwise
	 *         <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean cancelCapacityCall(UUID taskIdentifier,
			Throwable exception) {
		assert (taskIdentifier != null);
		return getCRIOContext().getCapacityExecutor().cancel(getAddress(),
				taskIdentifier, exception);
	}

	/**
	 * Force to terminate a capacity invocation with the given results.
	 * <p>
	 * This function stop the capacity running and force it to succeed.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link Role#submitCapacityCall(Class, Object...)}
	 * @param results
	 *            are the results to put back inside capacity invocation.
	 * @return <code>true</code> if the capacity was terminated, otherwise
	 *         <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean terminateCapacityCall(UUID taskIdentifier,
			Object... results) {
		assert (taskIdentifier != null);
		return getCRIOContext().getCapacityExecutor().done(getAddress(),
				taskIdentifier, results);
	}

	/**
	 * Remove all capacity invocations.
	 * <p>
	 * This function stop the capacity running and remove it. Capacity context
	 * will be no more available.
	 * 
	 * @return <code>true</code> if at least on capacity invocation was removed,
	 *         otherwise <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean clearCapacityCalls() {
		return getCRIOContext().getCapacityExecutor().clear(getAddress());
	}

	/** Add an event listener in the list supported by this role player.
	 * 
	 * @param <L> is the type of the listener.
	 * @param type is the type of the listener.
	 * @param listener is the new listener
	 * @since 0.4
	 */
	protected synchronized final <L extends EventListener> void addEventListener(Class<L> type, L listener) {
		if (this.listeners==null) {
			this.listeners = new ListenerCollection<L>();
		}
		this.listeners.add(type, listener);
	}
	
	/** Remove an event listener from the list supported by this role player.
	 * 
	 * @param <L> is the type of the listener.
	 * @param type is the type of the listener.
	 * @param listener is the new listener
	 * @since 0.4
	 */
	protected synchronized final <L extends EventListener> void removeEventListener(Class<L> type, L listener) {
		if (listener!=null && this.listeners!=null) {
			this.listeners.remove(type, listener);
			if (this.listeners.isEmpty())
				this.listeners = null;
		}
	}

	/** Replies the listeners of the given types and supported by this role player.
	 * 
	 * @param <L> is the type of the listener.
	 * @param type is the type of the listener.
	 * @return the listeners.
	 * @since 0.4
	 */
	@SuppressWarnings("unchecked")
	protected synchronized final <L extends EventListener> L[] getEventListeners(Class<L> type) {
		if (this.listeners!=null) {
			return this.listeners.getListeners(type);
		}
		return (L[])Array.newInstance(type, 0);
	}

	/**
	 * Add listener on role playing events in the groups
	 * where this player is involved.
	 * 
	 * @param listener
	 */
	protected final void addRolePlayingListener(RolePlayingListener listener) {
		addEventListener(RolePlayingListener.class, listener);
	}

	/**
	 * Remove listener on role playing events in the groups
	 * where this player is involved.
	 * 
	 * @param listener
	 */
	protected final void removeRolePlayingListener(RolePlayingListener listener) {
		removeEventListener(RolePlayingListener.class, listener);
	}

	/**
	 * Fire play role event.
	 * 
	 * @param event describes the role taking event.
	 */
	void firePlayRole(RolePlayingEvent event) {
		for (RolePlayingListener listener : getEventListeners(RolePlayingListener.class)) {
			listener.roleTaken(event);
		}
	}

	/**
	 * Fire leave role event.
	 * 
	 * @param event
	 */
	void fireLeaveRole(RolePlayingEvent event) {
		for (RolePlayingListener listener : getEventListeners(RolePlayingListener.class)) {
			listener.roleReleased(event);
		}
	}
	
	/**
	 * Add listener on creation or disappearing
	 * of a group from the system.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	protected synchronized final void addGroupListener(GroupListener listener) {
		if (this.groupEventWrapper==null) {
			this.groupEventWrapper = new RepositoryGroupWrapper();
			getCRIOContext().getGroupRepository().addRepositoryChangeListener(this.groupEventWrapper);
		}
		this.groupEventWrapper.addGroupListener(listener);
	}

	/**
	 * Remove listener on creation or disappearing
	 * of a group from the system.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	protected synchronized final void removeGroupListener(GroupListener listener) {
		if (this.groupEventWrapper!=null) {
			this.groupEventWrapper.removeGroupListener(listener);
			if (this.groupEventWrapper.isEmpty()) {
				getCRIOContext().getGroupRepository().removeRepositoryChangeListener(this.groupEventWrapper);
				this.groupEventWrapper = null;
			}
		}
	}
	
	/**
	 * Remove all the listeners register on this role player.
	 * 
	 * @since 0.5
	 */
	protected synchronized final void clearListeners() {
		if (this.listeners!=null) {
			this.listeners.clear();
			this.listeners = null;
		}
		if (this.groupEventWrapper!=null) {
			getCRIOContext().getGroupRepository().removeRepositoryChangeListener(this.groupEventWrapper);
			this.groupEventWrapper.clear();
			this.groupEventWrapper = null;
		}
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization with the specified ID if any, or create a new one with the specified id.
	 * 
	 * @param id is the desired ID for the group.
	 * @param organization is the organization implemented by the group.
	 * @param obtainConditions are the obtain conditions to pass to the newly created group.
	 * @param leaveConditions are the leave conditions to pass to the newly created group.
	 * @param membership is the membership descriptor to pass to the newly created group.
	 * @param distributed indicates if the newly created group is marked as distributed or not. 
	 * @param persistent indicates if the newly created group is marked as persistent or not. 
	 * @param groupName is the name associated to the newly created group. 
	 * @return the address of the group, never <code>null</code>.
	 * @GROUPAPI
	 * @since 0.4
	 */
	protected final GroupAddress getOrCreateGroup(UUID id,
			Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership,
			boolean distributed,
			boolean persistent,
			String groupName) {
		return getOrganization(organization).group(id,obtainConditions,leaveConditions,membership,distributed,persistent,groupName);
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization with the specified ID if any, or create a new one
	 * 
	 * @param id
	 *            The desired ID for the group
	 * @param organization
	 *            - the organization that the group have to implement
	 * @param groupName is the name of the group, used only when creating a new group.
	 * @return the address of the group
	 * @GROUPAPI
	 */
	protected GroupAddress getOrCreateGroup(UUID id,
			Class<? extends Organization> organization,
			String groupName) {
		Organization theOrganization = getOrganization(organization);
		GroupAddress ga = getCRIOContext().getGroupRepository().containsGroup(
				id, organization);
		if (ga == null) {
			JanusProperties props = getCRIOContext().getProperties();
			assert(props!=null);
			ga = theOrganization.createGroup(id, null, null, null,
					props.getBoolean(JanusProperty.GROUP_DISTRIBUTION),
					props.getBoolean(JanusProperty.GROUP_PERSISTENCE),
					groupName);
		}
		return ga;
	}

	/** Replies the public information of the given group.
	 * 
	 * @param groupId is the address of the group.
	 * @return the public informations on a group, or <code>null</code>
	 * if the given id is unknown.
	 * @since 0.5
	 */
	public final Group getGroupObject(GroupAddress groupId) {
		KernelScopeGroup group = getCRIOContext().getGroupRepository().get(groupId);
		if (group!=null) {
			return group.toGroup(isMemberOf(groupId));
		}
		return null;
	}

}