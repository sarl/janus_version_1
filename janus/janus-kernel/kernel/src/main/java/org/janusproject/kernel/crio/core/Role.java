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
package org.janusproject.kernel.crio.core;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.agentmemory.MemoryEvent;
import org.janusproject.kernel.agentmemory.MemoryListener;
import org.janusproject.kernel.agentsignal.BufferedSignalManager;
import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.agentsignal.SignalListener;
import org.janusproject.kernel.agentsignal.SignalManager;
import org.janusproject.kernel.agentsignal.SignalPolicy;
import org.janusproject.kernel.condition.Condition;
import org.janusproject.kernel.condition.ConditionnedObject;
import org.janusproject.kernel.condition.TimeCondition;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationNotFoundException;
import org.janusproject.kernel.crio.interaction.MailboxUtil;
import org.janusproject.kernel.crio.organization.Group;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.role.RoleActivationPrototypeValidator;
import org.janusproject.kernel.crio.role.RoleCondition;
import org.janusproject.kernel.crio.role.RoleFactory;
import org.janusproject.kernel.crio.role.RoleMigratedException;
import org.janusproject.kernel.crio.role.RoleReleasedException;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageException;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.schedule.Activable;
import org.janusproject.kernel.status.MultipleStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.multicollection.MultiCollection;
import org.janusproject.kernel.util.random.RandomNumber;
import org.janusproject.kernel.util.selector.TypeSelector;
import org.janusproject.kernel.util.sizediterator.EmptyIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * Basic Implementation of the <code>Role</code>.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class Role extends
		ConditionnedObject<RolePlayer, RoleCondition> implements Activable {

	/**
	 * The group where this role is defined
	 */
	private WeakReference<KernelScopeGroup> group;

	/**
	 * The reference to the role player owning this role
	 */
	private WeakReference<RolePlayer> owner;

	/** Address of the role.
	 */
	private RoleAddress address;
	
	/**
	 * The mailbox of this role
	 */
	private Mailbox mailbox;

	/**
	 * The message transport service of this role
	 */
	private MessageTransportService mts;

	/**
	 * Wrapper to the player's memory.
	 */
	private RoleMemory memory;

	/**
	 * Wrapper to the player's signal manager.
	 */
	private BufferedSignalManager signalManager;

	private WeakReference<CRIOContext> crioContext;
	
	/** Condition to wake up the role.
	 */
	private volatile Condition<?> roleWakeUpCondition = null;

	/**
	 * Logger for this role.
	 */
	private transient SoftReference<Logger> logger = null;

	/**
	 * When invoking leaveRole function, do not release this role instance
	 * immediately. It permits to avoid exception during the execution of the
	 * live function after the leaveRole invocation. See
	 * proceedPrivateBehaviour() for role release.
	 */
	private boolean leaveMe = false;
	
	/** Indicates if the role has migrated.
	 */
	private final boolean hasMigrated = false;

	private RepositoryGroupWrapper groupEventWrapper = null;
	
	/**
	 * Create a role outside a group, without owner and mailbox.
	 */
	protected Role() {
		this.crioContext = null;
		this.group = null;
		this.owner = null;
		this.mailbox = null;
		this.mts = null;
		this.memory = null;
		this.signalManager = null;
	}
	
	/** Replies the address of the role.
	 * 
	 * @return the address of the role.
	 * @since 0.5
	 */
	public RoleAddress getAddress() {
		return this.address;
	}
	
	/** Replies the address of the specified role in
	 * the current group for the player of this role.
	 *  
	 * @param role is the played role.
	 * @return the address or <code>null</code>.
	 * @since 0.5
	 */
	protected final RoleAddress getRoleAddress(Class<? extends Role> role) {
		return this.group.get().getRoleAddress(role, getPlayer());
	}

	/** Replies the address of the specified role in
	 * the current group.
	 *  
	 * @param role is the played role.
	 * @param player is the player of the role
	 * @return the address or <code>null</code>.
	 * @since 0.5
	 */
	protected final RoleAddress getRoleAddress(Class<? extends Role> role, AgentAddress player) {
		return this.group.get().getRoleAddress(role, player);
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
		if (getGroupAddress().equals(group))
			return getRoleAddress(role, player);
		KernelScopeGroup grp = this.crioContext.get().getGroupRepository().get(group);
		if (grp==null) return null;
		return grp.getRoleAddress(role, player);
	}

	/**
	 * Replies all the roles played by the player of this role.
	 * 
	 * @return played roles.
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected final SizedIterator<RoleAddress> getRoleAddresses() {
		return this.getPlayerInstance().getRoleAddresses();
	}

	/**
	 * Replies all the roles played in the current group.
	 * 
	 * @return played roles.
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected final SizedIterator<RoleAddress> getRoleAddressesInGroup() {
		KernelScopeGroup grp = this.group.get();
		assert(grp!=null);
		return grp.getRoleAddresses();
	}

	/**
	 * Replies all the roles of the given type and played in the current group.
	 * 
	 * @param role is the type of the role for which the addresses may be retreived.
	 * @return played roles.
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected final SizedIterator<RoleAddress> getRoleAddressesInGroup(Class<? extends Role> role) {
		KernelScopeGroup grp = this.group.get();
		assert(grp!=null);
		return grp.getRoleAddresses(role);
	}

	/**
	 * Replies all the roles of the given type and played in the specified group.
	 * 
	 * @param group is the group to explore.
	 * @param role is the type of the role for which the addresses may be retreived.
	 * @return played roles.
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected final SizedIterator<RoleAddress> getRoleAddressesIn(GroupAddress group, Class<? extends Role> role) {
		KernelScopeGroup grp = this.crioContext.get().getGroupRepository().get(group);
		assert(grp!=null);
		assert(grp!=null);
		return grp.getRoleAddresses(role);
	}

	/**
	 * Replies all played roles in the given group.
	 * 
	 * @param group
	 *            is the address of the group from which played roles may be
	 *            replied.
	 * @return played roles in the given group.
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected final SizedIterator<RoleAddress> getRoleAddressesIn(GroupAddress group) {
		KernelScopeGroup grp = this.crioContext.get().getGroupRepository().get(group);
		assert(grp!=null);
		return grp.getRoleAddresses();
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
			this.crioContext.get().getGroupRepository().addRepositoryChangeListener(this.groupEventWrapper);
		}
		this.groupEventWrapper.addGroupListener(listener);
	}

	/**
	 * Add listener on creation or disappearing
	 * of a group from the system.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	protected synchronized final void removeGroupListener(GroupListener listener) {
		if (this.groupEventWrapper!=null) {
			this.groupEventWrapper.removeGroupListener(listener);
			if (this.groupEventWrapper.isEmpty()) {
				this.crioContext.get().getGroupRepository().removeRepositoryChangeListener(this.groupEventWrapper);
				this.groupEventWrapper = null;
			}
		}
	}

	/** Replies the public information of the given group.
	 * 
	 * @param groupId is the address of the group.
	 * @return the public informations on a group, or <code>null</code>
	 * if the given id is unknown.
	 * @since 0.5
	 */
	public final Group getGroupObject(GroupAddress groupId) {
		if (getGroupAddress().equals(groupId))
			return getCurrentGroup();
		return getPlayerInstance().getGroupObject(groupId);
	}

	/** Replies the public information of the current group where the role is playing.
	 * 
	 * @return the public informations on a group, or <code>null</code>
	 * if the given id is unknown.
	 * @since 0.5
	 */
	public final Group getCurrentGroup() {
		KernelScopeGroup group = getKernelScopeGroup();
		if (group!=null) {
			return group.toGroup(true);
		}
		return null;
	}

	/**
	 * Replies if this role was released by the player on the current Janus kernel.
	 * 
	 * @return <code>true</code> if the role was released, otherwise
	 *         <code>false</code>
	 */
	public boolean isReleased() {
		return this.owner == null || this.owner.get() == null;
	}

	/**
	 * Replies if this role has migrated from this kernel to another one.
	 * 
	 * @return <code>true</code> if the role has migrated, otherwise
	 *         <code>false</code>
	 */
	boolean hasMigrated() {
		return this.hasMigrated;
	}

	/**
	 * Returns the address of the entity currently playing this roles.
	 * 
	 * @return the address of the entity currently playing this roles.
	 * @GROUPAPI
	 */
	public final AgentAddress getPlayer() {
		assert (this.owner != null);
		RolePlayer player = this.owner.get();
		assert (player != null);
		return player.getAddress();
	}

	/**
	 * Returns the address of the entity currently playing the given role in the
	 * current organization.
	 * <p>
	 * The replied address is randomly selected.
	 * 
	 * @param role
	 *            is the role which must be played by the replied address.
	 * @return the address of the entity currently playing the given role, or
	 *         <code>null</code> if no entity is playing such role.
	 * @GROUPAPI
	 */
	protected final AgentAddress getPlayer(Class<? extends Role> role) {
		return getPlayer(role, getGroupAddress());
	}

	/**
	 * Returns the address of the entity currently playing the given role in the
	 * given organization.
	 * <p>
	 * The replied address is randomly selected.
	 * 
	 * @param role
	 *            is the role which must be played by the replied address.
	 * @param group
	 *            is the address of the group in which an player may be
	 *            selected.
	 * @return the address of the entity currently playing the given role, or
	 *         <code>null</code> if no entity is playing such role.
	 * @GROUPAPI
	 */
	protected final AgentAddress getPlayer(Class<? extends Role> role,
			GroupAddress group) {
		assert (role != null);
		assert (group != null);
		// Do not pass by the role player to preserve computation time
		KernelScopeGroup grp = this.crioContext.get().getGroupRepository().get(group);
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

	/**
	 * Return all known groups of the given organization.
	 * 
	 * @param organization
	 *            the organization that the group have to implement
	 * @return all known groups
	 * @GROUPAPI
	 */
	protected final List<GroupAddress> getExistingGroups(
			Class<? extends Organization> organization) {
		return getOrganization(organization).getGroups();
	}

	/**
	 * Return all known groups in the same organization as the current group.
	 * The current group is also replied.
	 * 
	 * @return all known groups in current organization
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected final List<GroupAddress> getExistingsGroupsOfSameOrganization() {
		return getKernelScopeGroup().getOrganization().getGroups();
	}

	/**
	 * Replies the address of the group where this role is defined.
	 * 
	 * @return the address of the group where this role is defined.
	 * @since 0.5, replace previous versions of
	 * {@code GroupAddress getGroup()}.
	 * @GROUPAPI
	 */
	public final GroupAddress getGroupAddress() {
		KernelScopeGroup grp = this.group.get();
		assert (grp != null);
		return grp.getAddress();
	}

	/**
	 * Replies the group where this role is defined.
	 * 
	 * @return the group where this role is defined.
	 * @GROUPAPI
	 */
	final KernelScopeGroup getKernelScopeGroup() {
		return this.group.get();
	}

	/**
	 * Replies the player of this role.
	 * 
	 * @return the player of this role.
	 * @GROUPAPI
	 * @since 0.4
	 */
	final RolePlayer getPlayerInstance() {
		return this.owner.get();
	}

	/**
	 * Replies the current time manager.
	 * 
	 * @return the current time manager.
	 */
	protected final KernelTimeManager getTimeManager() {
		CRIOContext c = this.crioContext.get();
		assert (c != null);
		return c.getTimeManager();
	}

	/**
	 * Replies the logger associated to this group.
	 * 
	 * @return the logger associated to this role.
	 * @see #print(Object...)
	 * @see #debug(Object...)
	 * @see #error(Object...)
	 * @see #warning(Object...)
	 * @LOGGINGAPI
	 */
	protected Logger getLogger() {
		Logger logger = (this.logger != null) ? this.logger.get() : null;
		if (logger == null) {
			logger = LoggerUtil.createRoleLogger(getClass(), getTimeManager(), getPlayer(), getClass().getSimpleName());
			this.logger = new SoftReference<Logger>(logger);
		}
		return logger;
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
	 * The initialization method of the role that will be called one time at the
	 * beginning of the role life cycle.
	 * <p>
	 * If role is annotated with <code>@ReflectInit</code>, Janus kernel will
	 * automatically find a method called <code>init</code> with an arbitrary
	 * set of parameters (if any) in the role declaration and try to match it
	 * with the set of specified parameters if any method init is found, no role
	 * initialization will be done. Caution: Be careful to pass arguments of
	 * exactly the same type as whose declared in the init method of the role.
	 * 
	 * @param context
	 *            is the CRIO execution context inside which this role was
	 *            created.
	 * @param group
	 *            is the instance of group in which this role is playing.
	 * @param player
	 *            is the role player.
	 * @param params
	 *            is the list of input parameters passed to the initialization
	 *            function
	 * @return the status of the destruction.
	 */
	synchronized final Status proceedPrivateInitialization(CRIOContext context, KernelScopeGroup group,
			RolePlayer player, Object... params) {

		assert (RoleActivationPrototypeValidator.validateInputParameters(
				getClass(), params));
		this.crioContext = new WeakReference<CRIOContext>(context);
		this.group = new WeakReference<KernelScopeGroup>(group);
		this.owner = new WeakReference<RolePlayer>(player);
		this.address = new RoleAddress(this);
		this.leaveMe = false;
		return activate(params);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... params) {
		return StatusFactory.ok(this);
	}

	/**
	 * Run once time the activable's behaviour.
	 * <p>
	 * This function is invoked during the LIVE stage of the execution process.
	 * It fires memory and signal events, and invoke {@link #live()}.
	 * <p>
	 * This function must return quickly to avoid Janus dead lock.
	 * 
	 * @return the status of the live stage.
	 */
	synchronized final Status proceedPrivateBehaviour() {
		if (isReleased()) {
			return StatusFactory.cancel(this, new RoleReleasedException(getAddress()));
		}
		if (hasMigrated()) {
			return StatusFactory.cancel(this, new RoleMigratedException(getAddress()));
		}
		
		if(this.mailbox instanceof BufferedMailbox) {
			((BufferedMailbox)this.mailbox).synchronizeMessages();
		}
		
		if (this.memory != null)
			this.memory.fireEvents();

		if (this.signalManager != null)
			this.signalManager.sync();

		MultipleStatus ms = new MultipleStatus(live());
		
		if (this.leaveMe) {
			if (releaseRole(getClass(), getGroupAddress())) {
				ms.addStatus(StatusFactory.ok(this));
			}
			else {
				ms.addStatus(StatusFactory.error(this, Locale.getString(
						Role.class, "CANNOT_RELEASE_ROLE", //$NON-NLS-1$
						getClass().getCanonicalName(), getPlayer()
								.toString())));
			}
		}
		return ms.pack(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Status live();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status end() {
		return StatusFactory.ok(this);
	}

	/**
	 * Dispose this role. This is invoked when the role was released. This
	 * function release private resources and invokes {@link #end()}.
	 * 
	 * @return the status of the destruction.
	 */
	synchronized final Status proceedPrivateDestruction() {
		Status s = end();
		if (this.memory != null) {
			this.memory.clear();
			this.memory = null;
		}
		if (this.signalManager != null) {
			this.signalManager.reset();
			this.signalManager = null;
		}
		if (this.groupEventWrapper!=null) {
			this.crioContext.get().getGroupRepository().removeRepositoryChangeListener(this.groupEventWrapper);
			this.groupEventWrapper.clear();
			this.groupEventWrapper = null;
		}
		this.owner = null;
		this.group = null;
		this.address.unbind();
		return s;
	}

	/**
	 * Replies the buffered player's memory.
	 * 
	 * @return the buffered player's memory.
	 * @MINDAPI
	 */
	protected final Memory getMemory() {
		if (this.memory == null) {
			RolePlayer player = this.owner.get();
			assert (player != null);
			Memory mem = player.getMemory();
			if (mem != null)
				this.memory = new RoleMemory(mem);
		}
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
	 * Replies the buffered player's signal manager.
	 * 
	 * @return the buffered player's signal manager.
	 * @MINDAPI
	 * @since 0.5
	 */
	private BufferedSignalManager getBufferedSignalManager() {
		if (this.signalManager == null) {
			RolePlayer player = this.owner.get();
			assert (player != null);
			SignalManager emm = player.getSignalManager();
			this.signalManager = new BufferedSignalManager(
					this.crioContext.get().getProperties(),
					emm);
		}
		return this.signalManager;
	}

	/**
	 * Replies the buffered player's signal manager.
	 * 
	 * @return the buffered player's signal manager.
	 * @MINDAPI
	 * @since 0.5
	 */
	protected final SignalManager getSignalManager() {
		return getBufferedSignalManager();
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
	
	/** Replies the next available signal is possible.
	 * <p>
	 * A signal is available only if the current
	 * policy is {@link SignalPolicy#STORE_IN_QUEUE}
	 * and if a signal is inside the queue.
	 * 
	 * @return the next available signal or <code>null</code>.
	 * @MINDAPI
	 * @since 0.5
	 */
	protected final Signal getSignal() {
		return getBufferedSignalManager().getSignal();
	}
	
	/** Replies if a signal is available.
	 * <p>
	 * A signal is available only if the current
	 * policy is {@link SignalPolicy#STORE_IN_QUEUE}
	 * and if a signal is inside the queue.
	 * 
	 * @return <code>true</code> if a signal is available,
	 * otherwise <code>false</code>.
	 * @MINDAPI
	 * @since 0.5
	 */
	protected final boolean hasSignal() {
		return getBufferedSignalManager().hasSignal();
	}
	
	/**
	 * ************************* Message handling
	 * ********************************
	 */

	/**
	 * Replies the first available message in the mail box and remove it from
	 * the mailbox.
	 * 
	 * @return the first available message, or <code>null</code> if the mailbox
	 *         is empty.
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
	 * Replies the first available message in the mail box and leave it inside
	 * the mailbox.
	 * 
	 * @return the first available message, or <code>null</code> if the mailbox
	 *         is empty.
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
	 * Replies the messages in the mailbox. Each time an message is consumed
	 * from the replied iterable object, the corresponding message is removed
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
		return getMailbox();
	}

	/**
	 * Replies the messages in the mailbox that has the given type.
	 * Each time an message is consumed
	 * from the replied iterable object, the corresponding message is removed
	 * from the mailbox.
	 * 
	 * @param <T> is the type of the expected messages.
	 * @param type is the type of the expected messages.
	 * @return all the messages, never <code>null</code>.
	 * @see #getMessage()
	 * @see #peekMessage()
	 * @see #peekMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 * @since 5.0
	 */
	protected final <T extends Message> Iterable<T> getMessages(Class<T> type) {
		return getMailbox().iterable(type);
	}

	/**
	 * Replies the messages in the mailbox. Each time an message is consumed
	 * from the replied iterable object, the corresponding message is NOT
	 * removed from the mailbox.
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
	 * Replies the messages in the mailbox. Each time an message is consumed
	 * from the replied iterable object, the corresponding message is NOT
	 * removed from the mailbox.
	 * 
	 * @param <T> is the type of the expected messages.
	 * @param type is the type of the expected messages.
	 * @return all the messages, never <code>null</code>.
	 * @see #getMessage()
	 * @see #peekMessage()
	 * @see #getMessages()
	 * @see #hasMessage()
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected final <T extends Message> Iterable<T> peekMessages(Class<T> type) {
		return getMailbox().iterable(new TypeSelector<T>(type), false);
	}

	/**
	 * Indicates if the mailbox contains a message or not.
	 * 
	 * @return <code>true</code> if the message contains at least one message,
	 *         otherwise <code>false</code>
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
	 * Replies the number of messages in the mailbox.
	 * 
	 * @return the number of messages in the mailbox.
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
	 * Send the specified <code>Message</code> to a role of one agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param role
	 *            is the role which may receive the message.
	 * @param receiver
	 *            is the agent which may receive the message.
	 * @param message
	 *            is the message to send
	 * @return the receiver address if the delivery was successful, specially
	 *         useful if the message was sent to a distant kernel where the
	 *         agent might have died.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final RoleAddress sendMessage(Class<? extends Role> role,
			AgentAddress receiver, Message message) {
		return getMessageTransportService()
				.sendMessage(role, receiver, message);
	}

	/**
	 * Reply to the specified <code>Message</code>.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param messageToReplyTo is the message to reply to.
	 * @param message
	 *            is the message to send
	 * @return the receiver address if the delivery was successful, specially
	 *         useful if the message was sent to a distant kernel where the
	 *         agent might have died.
	 * @since 0.5
	 * @MESSAGEAPI
	 */
	protected final RoleAddress replyToMessage(Message messageToReplyTo, Message message) {
		assert(messageToReplyTo!=null);
		assert(messageToReplyTo.getSender() instanceof RoleAddress);
		RoleAddress newReceiver = (RoleAddress)messageToReplyTo.getSender();
		return getMessageTransportService().sendMessage(newReceiver, message);
	}

	/**
	 * Forward the specified <code>Message</code> to the role of the agent
	 * specified in the message.
	 * <p>
	 * This function does not change the emitter nor receiver of the message.
	 * 
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(Message message) {
		return getMessageTransportService().forwardMessage(message);
	}

	/**
	 * Forward the specified <code>Message</code> to a role of one agent.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param role
	 *            is the role which may receive the message.
	 * @param receiver
	 *            is the agent which may receive the message.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(Class<? extends Role> role,
			AgentAddress receiver, Message message) {
		return getMessageTransportService().forwardMessage(role, receiver, message);
	}

	/**
	 * Forward the specified <code>Message</code> to the specified role.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param receiver
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected final RoleAddress forwardMessage(RoleAddress receiver, Message message) {
		return getMessageTransportService().forwardMessage(receiver, message);
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param role
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final RoleAddress sendMessage(Class<? extends Role> role,
			Message message) {
		return getMessageTransportService().sendMessage(role, message);
	}

	/**
	 * Foward the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param role
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(Class<? extends Role> role,
			Message message) {
		return getMessageTransportService().forwardMessage(role, message);
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param role
	 *            is the role which may receive the message.
	 * @param policy
	 *            is the receiver selection policy.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final RoleAddress sendMessage(Class<? extends Role> role,
			MessageReceiverSelectionPolicy policy, Message message) {
		return getMessageTransportService().sendMessage(role, policy, message);
	}

	/**
	 * Send the specified <code>Message</code> to the specified receiver.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param receiver
	 *            is the receiver of the message.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if
	 *         it was found, <code>null</code> else.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected final RoleAddress sendMessage(RoleAddress receiver, Message message) {
		return getMessageTransportService().sendMessage(receiver, message);
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary
	 * selected agent.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param role
	 *            is the role which may receive the message.
	 * @param policy
	 *            is the receiver selection policy.
	 * @param message
	 *            is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final RoleAddress forwardMessage(Class<? extends Role> role,
			MessageReceiverSelectionPolicy policy, Message message) {
		return getMessageTransportService().forwardMessage(role, policy,
				message);
	}

	/**
	 * Send the specified <code>Message</code> to all the players of the given
	 * role, except the sender if it is playing the role.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param role
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final void broadcastMessage(Class<? extends Role> role,
			Message message) {
		getMessageTransportService().broadcastMessage(role, message);
	}

	/**
	 * Forward the specified <code>Message</code> to all the players of the
	 * given role, except the sender if it is playing the role.
	 * <p>
	 * This function does not change the emitter of the message.
	 * 
	 * @param role
	 *            is the role which may receive the message.
	 * @param message
	 *            is the message to send
	 * @throws MessageException when something wrong appended and the assertions were enabled. 
	 * @MESSAGEAPI
	 */
	protected final void forwardBroadcastMessage(Class<? extends Role> role,
			Message message) {
		getMessageTransportService().forwardBroadcastMessage(role, message);
	}

	/**
	 * Replies the mailbox for the role.
	 * 
	 * @return the mailbox of the given role, never <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected final Mailbox getMailbox() {
		if (this.mailbox == null) {
			this.mailbox = MailboxUtil.createDefaultMailbox(getClass(), this.crioContext.get().getProperties(), getLogger());
		}
		return this.mailbox;
	}

	/**
	 * Set the mailbox for the role.
	 * 
	 * @param mailbox
	 *            is the mailbox of the role.
	 * @MESSAGEAPI
	 */
	protected void setMailbox(Mailbox mailbox) {
		if (this.mailbox!=null && mailbox!=null) {
			mailbox.synchronize(this.mailbox);
		}
		this.mailbox = mailbox;
	}

	/**
	 * Replies the message transport service used by this role.
	 * 
	 * @return the the message transport service, never <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected final MessageTransportService getMessageTransportService() {
		if (this.mts == null) {
			this.mts = new MessageTransportService();
		}
		return this.mts;
	}

	/**
	 * *************************** Autonomous entity Basic capacity
	 * **************************
	 */

	// ------------------------------------------------
	// Synchronious Execution
	// ------------------------------------------------

	/**
	 * Execute immediately the given capacity.
	 * 
	 * @param capacity
	 *            is the invoked capacity.
	 * @param parameters
	 *            are the values to pass to the capacity implementation.
	 * @return the execution context after execution.
	 * @throws Exception
	 * @CAPACITYAPI
	 */
	protected final CapacityContext executeCapacityCall(
			Class<? extends Capacity> capacity, Object... parameters)
			throws Exception {
		assert (capacity != null);

		// Do not pass by the role player to preserve computation time

		RolePlayer player = this.owner.get();
		assert (player != null);

		CapacityContainer capacityContainer = player.getCapacityContainer();
		assert (capacityContainer != null);

		Capacity implementation = capacityContainer
				.selectImplementation(capacity);
		if (implementation == null || !(implementation instanceof CapacityImplementation))
			throw new CapacityImplementationNotFoundException(capacity);

		return CapacityExecutor
				.executeImmediately(
						capacity, (CapacityImplementation)implementation, player, this.group.get(),
						this, parameters);
	}

	// ------------------------------------------------
	// Asynchronious Execution
	// ------------------------------------------------

	/**
	 * Put the given capacity inside the execution queue.
	 * 
	 * @param capacity
	 *            is the invoked capacity.
	 * @param parameters
	 *            are the values to pass to the capacity implementation.
	 * @return the identifier of the task in the queue.
	 * @CAPACITYAPI
	 */
	protected final UUID submitCapacityCall(Class<? extends Capacity> capacity,
			Object... parameters) {
		assert (capacity != null);

		RolePlayer player = this.owner.get();
		assert (player != null);

		CapacityContainer capacityContainer = player.getCapacityContainer();
		assert (capacityContainer != null);

		Capacity implementation = capacityContainer
				.selectImplementation(capacity);
		if (implementation == null || !(implementation instanceof CapacityImplementation))
			throw new CapacityImplementationNotFoundException(capacity);

		// Do not pass by the role player to preserve computation time
		return this.crioContext
				.get()
				.getCapacityExecutor()
				.submit(capacity, (CapacityImplementation)implementation, player, this.group.get(),
						this, parameters);
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
	 *            {@link #submitCapacityCall(Class, Object...)}
	 * @param timeout
	 *            indicates how long to wait before giving up.
	 * @return the result of the call, or <tt>null</tt> if the specified waiting
	 *         time elapses before the result is available.
	 * @CAPACITYAPI
	 */
	protected final CapacityContext waitCapacityCallResult(UUID taskIdentifier,
			long timeout) {
		assert (taskIdentifier != null);
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor()
				.waitResult(getPlayer(), taskIdentifier, timeout);
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
	 *            {@link #submitCapacityCall(Class, Object...)}
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
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor()
				.waitResult(getPlayer(), taskIdentifier, timeout, unit);
	}

	/**
	 * Replies if the result of the call with the specified identifier is
	 * available.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link #submitCapacityCall(Class, Object...)}
	 * @return <code>true</code> if the result is available, otherwhise
	 *         <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean hasCapacityCallResult(UUID taskIdentifier) {
		assert (taskIdentifier != null);
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor()
				.hasResult(getPlayer(), taskIdentifier);
	}

	/**
	 * Retrieves the result of the call with the specified identifier, waiting
	 * if necessary up to the specified wait time if the result is not
	 * available.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link #submitCapacityCall(Class, Object...)}
	 * @return the result of the call, or <tt>null</tt> if the given identifier
	 *         is unkwown.
	 * @CAPACITYAPI
	 */
	protected final CapacityContext waitCapacityCallResult(UUID taskIdentifier) {
		assert (taskIdentifier != null);
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor()
				.waitResult(getPlayer(), taskIdentifier);
	}

	/**
	 * Retrieves the result of the call with the specified identifier, do not
	 * wait if the result is not available.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link #submitCapacityCall(Class, Object...)}
	 * @return the result of the call, or <tt>null</tt> if the given identifier
	 *         is unkwown or the result is not yet available.
	 * @CAPACITYAPI
	 */
	protected final CapacityContext getCapacityCallResult(UUID taskIdentifier) {
		assert (taskIdentifier != null);
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor()
				.instantResult(getPlayer(), taskIdentifier);
	}

	/**
	 * Cancel a capacity invocation.
	 * <p>
	 * This function stop the capacity running and force it to fail.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link #submitCapacityCall(Class, Object...)}
	 * @return <code>true</code> if the capacity was canceled, otherwise
	 *         <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean cancelCapacityCall(UUID taskIdentifier) {
		assert (taskIdentifier != null);
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor()
				.cancel(getPlayer(), taskIdentifier);
	}

	/**
	 * Cancel a capacity invocation.
	 * <p>
	 * This function stop the capacity running and force it to fail.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link #submitCapacityCall(Class, Object...)}
	 * @param exception
	 *            is the exception which causes the cancelation.
	 * @return <code>true</code> if the capacity was canceled, otherwise
	 *         <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean cancelCapacityCall(UUID taskIdentifier,
			Throwable exception) {
		assert (taskIdentifier != null);
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor()
				.cancel(getPlayer(), taskIdentifier, exception);
	}

	/**
	 * Force to terminate a capacity invocation with the given results.
	 * <p>
	 * This function stop the capacity running and force it to succeed.
	 * 
	 * @param taskIdentifier
	 *            is the identifier of the call, given by
	 *            {@link #submitCapacityCall(Class, Object...)}
	 * @param results
	 *            are the results to put back inside capacity invocation.
	 * @return <code>true</code> if the capacity was terminated, otherwise
	 *         <code>false</code>
	 * @CAPACITYAPI
	 */
	protected final boolean terminateCapacityCall(UUID taskIdentifier,
			Object... results) {
		assert (taskIdentifier != null);
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor()
				.done(getPlayer(), taskIdentifier, results);
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
		// Do not pass by the role player to preserve computation time
		return this.crioContext.get().getCapacityExecutor().clear(getPlayer());
	}

	// ------------------------------------------------
	// Group Management
	// ------------------------------------------------

	/**
	 * Replies the instance for the given organization.
	 * 
	 * @param organization
	 *            is the organization that must be instanced.
	 * @return organization instance in the current context.
	 * @GROUPAPI
	 */
	protected Organization getOrganization(
			Class<? extends Organization> organization) {
		assert (organization != null);
		assert (this.crioContext != null);
		CRIOContext context = this.crioContext.get();
		assert (context != null);
		Organization instance = OrganizationRepository.organization(context,
				organization, null);
		assert (instance != null);
		return instance;
	}

	private Organization getOrganizationSingleton(
			OrganizationFactory<? extends Organization> factory) {
		assert (factory != null);
		assert (this.crioContext != null);
		CRIOContext context = this.crioContext.get();
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
		// Do not pass by the role player to preserve computation time
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
		// Do not pass by the role player to preserve computation time
		return getOrganization(organization).createGroup(obtainConditions,
				leaveConditions, groupName);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory to use to create an organization
	 *            instance when required by the CRIO context.
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
		// Do not pass by the role player to preserve computation time
		return getOrganizationSingleton(factory).createGroup(obtainConditions,
				leaveConditions);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory to use to create an organization
	 *            instance when required by the CRIO context.
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
		// Do not pass by the role player to preserve computation time
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
		// Do not pass by the role player to preserve computation time
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
		// Do not pass by the role player to preserve computation time
		return getOrganization(organization).createGroup(groupName);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory to use to create an organization
	 *            instance when required by the CRIO context.
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected final GroupAddress createGroup(
			OrganizationFactory<? extends Organization> factory) {
		// Do not pass by the role player to preserve computation time
		return getOrganizationSingleton(factory).createGroup();
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory to use to create an organization
	 *            instance when required by the CRIO context.
	 * @param groupName is the name of the group.
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 * @since 0.4
	 */
	protected final GroupAddress createGroup(
			OrganizationFactory<? extends Organization> factory,
			String groupName) {
		// Do not pass by the role player to preserve computation time
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
		// Do not pass by the role player to preserve computation time
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
		// Do not pass by the role player to preserve computation time
		return getOrganization(organization).group(groupName);
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param factory
	 *            is the organization factory to use to create an organization
	 *            instance when required by the CRIO context.
	 * @return the address of the group
	 * @GROUPAPI
	 */
	protected final GroupAddress getOrCreateGroup(
			OrganizationFactory<? extends Organization> factory) {
		// Do not pass by the role player to preserve computation time
		return getOrganizationSingleton(factory).group();
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param factory
	 *            is the organization factory to use to create an organization
	 *            instance when required by the CRIO context.
	 * @param groupName is the name of the group, used only when creating a new group.
	 * @return the address of the group
	 * @GROUPAPI
	 * @since 0.4
	 */
	protected final GroupAddress getOrCreateGroup(
			OrganizationFactory<? extends Organization> factory,
			String groupName) {
		// Do not pass by the role player to preserve computation time
		return getOrganizationSingleton(factory).group(groupName);
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
	 * organization if any, do not create a new one.
	 * 
	 * @param organization
	 *            - the organization that the group have to implement
	 * @return the address of the group, or <code>null</code>
	 * @GROUPAPI
	 */
	protected final GroupAddress getExistingGroup(
			Class<? extends Organization> organization) {
		// Do not pass by the role player to preserve computation time
		return getOrganization(organization).getGroup();
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, do not create a new one.
	 * 
	 * @param factory
	 *            is the organization factory to use to create an organization
	 *            instance when required by the CRIO context.
	 * @return the address of the group, or <code>null</code>
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected final GroupAddress getExistingGroup(
			OrganizationFactory<? extends Organization> factory) {
		// Do not pass by the role player to preserve computation time
		return getOrganizationSingleton(factory).getGroup();
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
		KernelScopeGroup grp = this.crioContext.get().getGroupRepository().get(group);
		if (grp != null) {
			return grp.requestRole(this.owner.get(), role, factory,
					accessContext, initParameters);
		}
		return null;
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * current group.
	 * 
	 * @param role
	 *            is the class of the requested role.
	 * @param initParameters
	 *            is the set of parameters to pass to
	 *            {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected final RoleAddress requestRole(Class<? extends Role> role,
			Object... initParameters) {
		return requestRole(role, getGroupAddress(), initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * current group.
	 * 
	 * @param role
	 *            is the class of the requested role.
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
			AccessControlContext accessContext, Object... initParameters) {
		return requestRole(role, getGroupAddress(), accessContext, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * current group.
	 * 
	 * @param role
	 *            is the class of the requested role.
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
			RoleFactory factory, Object... initParameters) {
		return requestRole(role, getGroupAddress(), factory, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * current group.
	 * 
	 * @param role
	 *            is the class of the requested role.
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
			RoleFactory factory, AccessControlContext accessContext,
			Object... initParameters) {
		return requestRole(role, getGroupAddress(), factory, accessContext,
				initParameters);
	}

	/**
	 * Function allowing the request of the liberation of this role on the
	 * corresponding group.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @GROUPAPI
	 */
	protected final void leaveMe() {
		// Do not leave this role instance immediately to
		// prevent exception during the rest of the live function.
		// See proceedPrivateBehaviour() for the role release.
		this.leaveMe = true;
	}

	/**
	 * Function allowing the request of the liberation of a role on the current
	 * group.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @param role
	 *            is the role to leave.
	 * @return <code>true</code> if the request was accepted, <code>false</code>
	 *         else
	 * @GROUPAPI
	 */
	protected final boolean leaveRole(Class<? extends Role> role) {
		// Do not leave this role instance immediately to
		// prevent exception during the rest of the live function.
		// See proceedPrivateBehaviour() for the role release.
		if (getClass().equals(role)) {
			this.leaveMe = true;
			return true;
		}
		return releaseRole(role, getGroupAddress());
	}

	/**
	 * Function allowing the request of the liberation of a role on the given
	 * group.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @param role
	 *            is the role to leave.
	 * @param group
	 *            is the address of the group of the role to leave.
	 * @return <code>true</code> if the request was accepted, <code>false</code>
	 *         else
	 * @GROUPAPI
	 */
	protected final boolean leaveRole(Class<? extends Role> role,
			GroupAddress group) {
		// Do not leave this role instance immediately to
		// prevent exception during the rest of the live function.
		// See proceedPrivateBehaviour() for the role release.
		if (getClass().equals(role) && getGroupAddress().equals(group)) {
			this.leaveMe = true;
			return true;
		}
		return releaseRole(role, group);
	}

	/**
	 * Function allowing the request of the liberation of a role on the given
	 * group.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @param role
	 *            is the role to leave.
	 * @return <code>true</code> if the request was accepted, <code>false</code>
	 *         else
	 * @GROUPAPI
	 * @since 1.0
	 */
	protected final boolean leaveRole(RoleAddress role) {
		// Do not leave this role instance immediately to
		// prevent exception during the rest of the live function.
		// See proceedPrivateBehaviour() for the role release.
		if (role!=null && getPlayer().equals(role.getPlayer())) {
			return releaseRole(role.getRole(), role.getGroup());
		}
		return false;
	}

	/**
	 * Function allowing the request of the liberation of a role on the given
	 * group.
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @param role
	 *            is the role to leave.
	 * @param group
	 *            is the address of the group of the role to leave.
	 * @return <code>true</code> if the request was accepted, <code>false</code>
	 *         else
	 * @GROUPAPI
	 */
	private boolean releaseRole(Class<? extends Role> role, GroupAddress group) {
		// Do not pass by the role player to preserve computation time
		KernelScopeGroup grp = this.crioContext.get().getGroupRepository().get(group);
		if (grp != null) {
			return grp.leaveRole(this.owner.get(), role);
		}
		return false;
	}

	/**
	 * Returns the list of the addresses of the role players currently playing
	 * the current role in the specified group.
	 * 
	 * @return the addresses of the entities currently playing the current role.
	 * @GROUPAPI
	 */
	protected final SizedIterator<AgentAddress> getPlayers() {
		return getPlayers(getClass(), getGroupAddress());
	}

	/**
	 * Returns the list of the addresses of the role players currently playing
	 * the specified role in the specified group..
	 * 
	 * @param role
	 *            is the role of which obtain the number of players.
	 * @return the addresses of the entities currently playing the specified
	 *         role.
	 * @GROUPAPI
	 */
	protected final SizedIterator<AgentAddress> getPlayers(
			Class<? extends Role> role) {
		return getPlayers(role, getGroupAddress());
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
	protected final SizedIterator<AgentAddress> getPlayers(
			Class<? extends Role> role, GroupAddress group) {
		assert (role != null);
		assert (group != null);
		// Do not pass by the role player to preserve computation time
		KernelScopeGroup grp = this.crioContext.get().getGroupRepository().get(group);
		if (grp != null) {
			return grp.getRolePlayers(role);
		}
		return EmptyIterator.singleton();
	}

	/**
	 * Replies the player's capacities.
	 * 
	 * @return the player's capacities.
	 * @CAPACITYAPI
	 */
	protected Collection<Class<? extends Capacity>> getPlayerCapacities() {
		RolePlayer player = this.owner.get();
		assert (player != null);
		return player.getCapacityContainer().identifiers();
	}

	/**
	 * Replies the player's roles in all the groups.
	 * 
	 * @return the player's roles in all the groups.
	 * @ROLEAPI
	 */
	protected final Collection<Class<? extends Role>> getPlayerRoles() {
		RolePlayer player = this.owner.get();
		assert (player != null);
		return player.getRoles();
	}

	/**
	 * Replies the player's roles in the given group.
	 * 
	 * @param group
	 *            is the address of the group from which roles may be extracted.
	 * @return the player's roles in the given group.
	 * @ROLEAPI
	 */
	protected final Collection<Class<? extends Role>> getPlayerRoles(
			GroupAddress group) {
		RolePlayer player = this.owner.get();
		assert (player != null);
		return player.getRoles(group);
	}

	/**
	 * Replies if the player of this role is currently also playing the given
	 * role in the current group.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	protected final boolean isPlayingRole(Class<? extends Role> role) {
		return isPlayingRole(role, getGroupAddress());
	}

	/**
	 * Replies if the given role is playing by any player in the current group.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	protected final boolean isPlayedRole(Class<? extends Role> role) {
		return isPlayedRole(role, getGroupAddress());
	}

	/**
	 * Replies if the given group address corresponds to an existing group.
	 * 
	 * @param group
	 * @return <code>true</code> if the given group address corresponds to an
	 *         existing group, otherwise <code>false</code>
	 * @GROUPAPI
	 */
	protected final boolean isGroup(GroupAddress group) {
		GroupRepository repo = this.crioContext.get().getGroupRepository();
		assert (repo != null);
		return repo.contains(group);
	}

	/**
	 * Replies if the player of this role is currently also playing the given
	 * role in the given group.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @param group
	 *            is the group of the role.
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	protected boolean isPlayingRole(Class<? extends Role> role,
			GroupAddress group) {
		assert (role != null);
		assert (group != null);
		GroupRepository repo = this.crioContext.get().getGroupRepository();
		assert (repo != null);
		KernelScopeGroup grp = repo.get(group);
		return (grp != null) && (grp.isPlayedRole(getPlayer(), role));
	}

	/**
	 * Replies if the given role is played by any player in the given group.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @param group
	 *            is the group of the role.
	 * @return <code>true</code> if a role is played, otherwise
	 *         <code>false</code>
	 */
	protected final boolean isPlayedRole(Class<? extends Role> role,
			GroupAddress group) {
		assert (role != null);
		assert (group != null);
		GroupRepository repo = this.crioContext.get().getGroupRepository();
		assert (repo != null);
		KernelScopeGroup grp = repo.get(group);
		return (grp != null) && (grp.isPlayedRole(role));
	}

	/**
	 * Replies if the player of this role is member of the given group.
	 * 
	 * @param group
	 *            is the group to test.
	 * @return <code>true</code> if the player in memeber of the group,
	 *         otherwise <code>false</code>
	 */
	protected boolean isMemberOf(GroupAddress group) {
		return isMemberOf(getPlayer(), group);
	}

	/**
	 * Replies if the given player is member of the given group.
	 * 
	 * @param player
	 *            is the address of the role player to test.
	 * @param group
	 *            is the group to test.
	 * @return <code>true</code> if the given player in memeber of the group,
	 *         otherwise <code>false</code>
	 */
	protected boolean isMemberOf(AgentAddress player, GroupAddress group) {
		assert (group != null);
		GroupRepository repo = this.crioContext.get().getGroupRepository();
		assert (repo != null);
		KernelScopeGroup grp = repo.get(group);
		return (grp != null) && (grp.isPlayedRole(player));
	}

	/**
	 * Return all the groups in which this role player is playing a role.
	 * 
	 * @return all the groups in which this role player is playing a role.
	 * @GROUPAPI
	 */
	protected final Collection<GroupAddress> getPlayerGroups() {
		RolePlayer player = this.owner.get();
		assert (player != null);
		return player.getGroups();
	}

	/**
	 * Replies all played roles in the whole system.
	 * 
	 * @return played roles.
	 * @GROUPAPI
	 */
	protected final Collection<Class<? extends Role>> getExistingRoles() {
		MultiCollection<Class<? extends Role>> multiCollection = new MultiCollection<Class<? extends Role>>();
		GroupRepository repo = this.crioContext.get().getGroupRepository();
		assert (repo != null);
		for (KernelScopeGroup grp : repo.values()) {
			multiCollection.addCollection(grp.getPlayedRolesAsCollection());
		}
		return multiCollection;
	}

	/**
	 * Replies all played roles in the given group.
	 * 
	 * @param group
	 *            is the address of the group from which played roles may be
	 *            replied.
	 * @return played roles in the given group.
	 * @GROUPAPI
	 */
	protected final SizedIterator<Class<? extends Role>> getExistingRoles(
			GroupAddress group) {
		GroupRepository repo = this.crioContext.get().getGroupRepository();
		assert (repo != null);
		KernelScopeGroup grp = repo.get(group);
		if (grp != null) {
			return grp.getPlayedRoles();
		}
		return EmptyIterator.singleton();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(getClass().getName());
		String name = getPlayer().getName();
		if (name != null) {
			buffer.append('/');
			buffer.append(name);
		}
		return buffer.toString();
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
	 * @see #sleep(Condition)
	 * @since 2.0
	 */
	protected final boolean sleep(TimeCondition wakeUpCondition) {
		if (wakeUpCondition!=null) {
			this.roleWakeUpCondition = wakeUpCondition;
			return true;
		}
		return false;
	}

	/** Stop the agent execution until the given condition is true.
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
	 * 
	 * @param wakeUpCondition is the condition to wake up
	 * the agent.
	 * @return <code>true</code> if the agent will be paused,
	 * otherwise <code>false</code>
	 * @since 2.0
	 * @see #sleep(TimeCondition)
	 */
	protected final boolean sleep(Condition<? extends RolePlayer> wakeUpCondition) {
		if (wakeUpCondition!=null) {
			this.roleWakeUpCondition = wakeUpCondition;
			return true;
		}
		return false;
	}

	/** Test the wake-up condition if present and replies if this role is 
	 * still sleeping.
	 * 
	 * @return <code>true</code> if this role continue to sleep,
	 * otherwise <code>false</code>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	boolean wakeUpIfSleeping() {
		Condition c = this.roleWakeUpCondition;
		if (c!=null) {
			if (c instanceof TimeCondition) {
				CRIOContext kc = getPlayerInstance().getCRIOContext();
				TimeCondition tc = (TimeCondition)c;
				if (tc.evaluate(kc.getTimeConditionParameterProvider())) {
					this.roleWakeUpCondition = null;
					return false;
				}
			}
			else if (c.evaluate(this)) {
				this.roleWakeUpCondition = null;
				return false;
			}
			return true;
		}
		return false;
	}

	/** Replies if the role is currently sleeping, ie. it is
	 * waiting for a particular condition to wake up.
	 *  
	 * @return <code>true</code> if the role is sleeping;
	 * <code>false</code> if not.
	 */
	public boolean isSleeping() {
		return this.roleWakeUpCondition!=null;
	}

	/**
	 * Wrapper to player's memory to avoid invalid accesses from role.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RoleMemory implements Memory, MemoryListener {

		private Memory playerMemory;

		/**
		 * Memory listeners.
		 */
		private List<MemoryListener> listeners = null;

		/**
		 * Memory events.
		 */
		private List<MemoryEvent> events = null;

		private boolean isListener = false;

		/**
		 * @param m
		 */
		public RoleMemory(Memory m) {
			assert (m != null);
			this.playerMemory = m;
		}

		public void clear() {
			if (this.listeners != null) {
				this.listeners.clear();
				this.listeners = null;
			}
			if (this.events != null) {
				this.events.clear();
				this.events = null;
			}
			if (this.isListener)
				this.playerMemory.removeMemoryListener(this);
			this.playerMemory = null;
		}

		@Override
		public void addMemoryListener(MemoryListener listener) {
			if (this.listeners == null)
				this.listeners = new ArrayList<MemoryListener>();
			this.listeners.add(listener);
			if (!this.isListener) {
				this.isListener = true;
				this.playerMemory.addMemoryListener(this);
			}
		}

		@Override
		public void removeMemoryListener(MemoryListener listener) {
			if (this.listeners != null) {
				this.listeners.remove(listener);
				if (this.listeners.isEmpty()) {
					this.listeners = null;
					if (this.isListener) {
						this.playerMemory.removeMemoryListener(this);
						this.isListener = false;
					}
				}
			}
		}

		@Override
		public Object getMemorizedData(String id) {
			return this.playerMemory.getMemorizedData(id);
		}

		@Override
		public <T> T getMemorizedData(String id, Class<T> type) {
			return this.playerMemory.getMemorizedData(id, type);
		}

		@Override
		public boolean hasMemorizedData(String id) {
			return this.playerMemory.hasMemorizedData(id);
		}

		@Override
		public boolean putMemorizedData(String id, Object value) {
			return this.playerMemory.putMemorizedData(id, value);
		}

		@Override
		public void removeMemorizedData(String id) {
			this.playerMemory.removeMemorizedData(id);
		}

		@Override
		public void onKnownledgeChanged(MemoryEvent event) {
			if (this.events == null) {
				this.events = new LinkedList<MemoryEvent>();
			}
			this.events.add(event);
		}

		/**
		 * Fire buffered events in role's listeners.
		 */
		public void fireEvents() {
			if (this.listeners != null && this.events != null) {
				for (MemoryEvent event : this.events) {
					for (MemoryListener listener : this.listeners) {
						listener.onKnownledgeChanged(event);
					}
				}
			}
			if (this.events != null) {
				this.events = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Role.this.toString();
		}

	}

	/**
	 * This class describes a message transport service in an organizational
	 * context. It provides all functions to send messages.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public final class MessageTransportService implements Serializable {

		private static final long serialVersionUID = -1791740214107873015L;

		private boolean sendFreedBack = true;
		private boolean forwardFreedBack = true;
		private boolean broadcastFreedBack = true;

		/**
		 */
		protected MessageTransportService() {
			//
		}

		/**
		 * Replies if the emitting role is receiving the messages sent to itself
		 * by a <code>sendMessage</code> function.
		 * 
		 * @return <code>true</code> if the role allow to receive self-back
		 *         messages, otherwise <code>false</code>.
		 */
		public boolean isSendMessageFeedBack() {
			return this.sendFreedBack;
		}

		/**
		 * Set if the emitting role is receiving the messages sent to itself by
		 * a <code>sendMessage</code> function.
		 * 
		 * @param feedback
		 *            is <code>true</code> to allow the role to receive
		 *            self-back messages, otherwise <code>false</code>.
		 */
		public void setSendMessageFeedBack(boolean feedback) {
			this.sendFreedBack = feedback;
		}

		/**
		 * Replies if the emitting role is receiving the messages sent to itself
		 * by a <code>forwardMessage</code> function.
		 * 
		 * @return <code>true</code> if the role allow to receive self-back
		 *         messages, otherwise <code>false</code>.
		 */
		public boolean isForwardMessageFeedBack() {
			return this.forwardFreedBack;
		}

		/**
		 * Set if the emitting role is receiving the messages sent to itself by
		 * a <code>forwardMessage</code> function.
		 * 
		 * @param feedback
		 *            is <code>true</code> to allow the role to receive
		 *            self-back messages, otherwise <code>false</code>.
		 */
		public void setForwardMessageFeedBack(boolean feedback) {
			this.forwardFreedBack = feedback;
		}

		/**
		 * Replies if the emitting role is receiving the messages sent by a
		 * <code>broadcastMessage</code> function.
		 * 
		 * @return <code>true</code> if the role allow to receive self-back
		 *         messages, otherwise <code>false</code>.
		 */
		public boolean isBroadcastMessageFeedBack() {
			return this.broadcastFreedBack;
		}

		/**
		 * Set if the emitting role is receiving the messages sent by a
		 * <code>broadcastMessage</code> function.
		 * 
		 * @param feedback
		 *            is <code>true</code> to allow the role to receive
		 *            self-back messages, otherwise <code>false</code>.
		 */
		public void setBroadcastMessageFeedBack(boolean feedback) {
			this.broadcastFreedBack = feedback;
		}

		/**
		 * Send the specified <code>Message</code> to a role of one agent.
		 * <p>
		 * This function force the emitter of the message to be this role.
		 * 
		 * @param role
		 *            is the role which may receive the message.
		 * @param receiver
		 *            is the agent which may receive the message.
		 * @param message
		 *            is the message to send
		 * @return the receiver address if the delivery was successful,
		 *         specially useful if the message was sent to a distant kernel
		 *         where the agent might have died.
		 * @MESSAGEAPI
		 */
		public RoleAddress sendMessage(Class<? extends Role> role,
				AgentAddress receiver, Message message) {
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					new RoleAddress(Role.this.getGroupAddress(), role, receiver),
					message, true, isSendMessageFeedBack());
		}

		/**
		 * Forward the specified <code>Message</code> to the role of the agent
		 * specified in the message.
		 * <p>
		 * This function does not change the emitter nor receiver of the
		 * message.
		 * 
		 * @param message
		 *            is the message to send
		 * @return the address of the receiver.
		 * @MESSAGEAPI
		 */
		public RoleAddress forwardMessage(Message message) {
			assert (message != null);
			assert (message.getReceiver() instanceof RoleAddress);
			RoleAddress receiver = (RoleAddress)message.getReceiver();
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					receiver,
					message, false,
					isForwardMessageFeedBack());
		}

		/**
		 * Forward the specified <code>Message</code> to a role of one agent.
		 * <p>
		 * This function does not change the emitter of the message.
		 * 
		 * @param role
		 *            is the role which may receive the message.
		 * @param receiver
		 *            is the agent which may receive the message.
		 * @param message
		 *            is the message to send
		 * @return the address of the receiver.
		 * @MESSAGEAPI
		 */
		public RoleAddress forwardMessage(Class<? extends Role> role,
				AgentAddress receiver, Message message) {
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					new RoleAddress(Role.this.getGroupAddress(), role, receiver),
					message, false, isForwardMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to a role of one arbitrary
		 * selected agent.
		 * <p>
		 * This function force the emitter of the message to be this role.
		 * 
		 * @param role
		 *            is the role which may receive the message.
		 * @param message
		 *            is the message to send
		 * @return the address of the receiver of the freshly sended message if
		 *         it was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public RoleAddress sendMessage(Class<? extends Role> role,
				Message message) {
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					new RoleAddress(Role.this.getGroupAddress(), role, null),
					message,
					true, isSendMessageFeedBack());
		}

		/**
		 * Forward the specified <code>Message</code> to the specified role.
		 * <p>
		 * This function does not change the emitter of the message.
		 * 
		 * @param receiver
		 *            is the receiver of the message.
		 * @param message
		 *            is the message to send
		 * @return the address of the receiver.
		 * @MESSAGEAPI
		 * @since 0.5
		 */
		public RoleAddress forwardMessage(RoleAddress receiver, Message message) {
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					receiver,
					message, false, isForwardMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to the specified receiver.
		 * <p>
		 * This function force the emitter of the message to be this role.
		 * 
		 * @param receiver
		 *            is the receiver of the message.
		 * @param message
		 *            is the message to send
		 * @return the address of the receiver of the freshly sended message if
		 *         it was found, <code>null</code> else.
		 * @MESSAGEAPI
		 * @since 0.5
		 */
		public RoleAddress sendMessage(RoleAddress receiver, Message message) {
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					receiver,
					message,
					true, isSendMessageFeedBack());
		}

		/**
		 * Foward the specified <code>Message</code> to a role of one arbitrary
		 * selected agent.
		 * <p>
		 * This function does not change the emitter of the message.
		 * 
		 * @param role
		 *            is the role which may receive the message.
		 * @param message
		 *            is the message to send
		 * @return the address of the receiver of the freshly sended message if
		 *         it was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public RoleAddress forwardMessage(Class<? extends Role> role,
				Message message) {
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					new RoleAddress(Role.this.getGroupAddress(), role, null),
					message, false, isForwardMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to a role of one arbitrary
		 * selected agent.
		 * <p>
		 * This function force the emitter of the message to be this role.
		 * 
		 * @param role
		 *            is the role which may receive the message.
		 * @param policy
		 *            is the receiver selection policy.
		 * @param message
		 *            is the message to send
		 * @return the address of the receiver of the freshly sended message if
		 *         it was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public RoleAddress sendMessage(Class<? extends Role> role,
				MessageReceiverSelectionPolicy policy, Message message) {
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					new RoleAddress(Role.this.getGroupAddress(), role, null),
					policy,
					message, true, isSendMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to a role of one arbitrary
		 * selected agent.
		 * <p>
		 * This function does not change the emitter of the message.
		 * 
		 * @param role
		 *            is the role which may receive the message.
		 * @param policy
		 *            is the receiver selection policy.
		 * @param message
		 *            is the message to send
		 * @return the address of the receiver of the freshly sended message if
		 *         it was found, <code>null</code> else.
		 * @MESSAGEAPI
		 */
		public RoleAddress forwardMessage(Class<? extends Role> role,
				MessageReceiverSelectionPolicy policy, Message message) {
			return InteractionUtil.sendMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					new RoleAddress(Role.this.getGroupAddress(), role, null),
					policy,
					message, false, isForwardMessageFeedBack());
		}

		/**
		 * Send the specified <code>Message</code> to all the players of the
		 * given role, except the sender if it is playing the role.
		 * <p>
		 * This function force the emitter of the message to be this role.
		 * 
		 * @param role
		 *            is the role which may receive the message.
		 * @param message
		 *            is the message to send
		 * @MESSAGEAPI
		 */
		public void broadcastMessage(Class<? extends Role> role, Message message) {
			InteractionUtil.broadcastMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					role,
					message, true, isBroadcastMessageFeedBack());
		}

		/**
		 * Forward the specified <code>Message</code> to all the players of the
		 * given role, except the sender if it is playing the role.
		 * <p>
		 * This function does not change the emitter of the message.
		 * 
		 * @param role
		 *            is the role which may receive the message.
		 * @param message
		 *            is the message to send
		 * @MESSAGEAPI
		 */
		public void forwardBroadcastMessage(Class<? extends Role> role,
				Message message) {
			InteractionUtil.broadcastMessage(
					Role.this.getTimeManager().getCurrentTime(),
					Role.this.getAddress(),
					role, message,
					false, isBroadcastMessageFeedBack()
							&& isForwardMessageFeedBack());
		}

	}

}