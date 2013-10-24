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

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.condition.ConditionFailure;
import org.janusproject.kernel.condition.ConditionnedObject;
import org.janusproject.kernel.crio.interaction.MailboxNotFoundException;
import org.janusproject.kernel.crio.interaction.ReceiverNotFoundException;
import org.janusproject.kernel.crio.organization.Group;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.role.RoleFactory;
import org.janusproject.kernel.crio.role.RoleNotInitializedException;
import org.janusproject.kernel.crio.role.RolePlayingEvent;
import org.janusproject.kernel.crio.role.RolePlayingListener;
import org.janusproject.kernel.crio.role.UndefinedRoleException;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.comparator.GenericComparator;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.directaccess.UnmodifiableDirectAccessCollection;
import org.janusproject.kernel.util.directaccess.UnmodifiableDirectAccessSet;
import org.janusproject.kernel.util.directaccess.UnmodifiableDirectAccessSetSet;
import org.janusproject.kernel.util.event.ListenerCollection;
import org.janusproject.kernel.util.multicollection.DoubleSizedIterator;
import org.janusproject.kernel.util.sizediterator.EmptyIterator;
import org.janusproject.kernel.util.sizediterator.ModifiableCollectionSizedIterator;
import org.janusproject.kernel.util.sizediterator.MultiSizedIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;
import org.janusproject.kernel.util.sizediterator.UnmodifiableCollectionSizedIterator;
import org.janusproject.kernel.util.sizediterator.UnmodifiableMapKeySizedIterator;

/**
 * Private implementation of a message inside Janus kernel.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
final class KernelScopeGroup extends ConditionnedObject<RolePlayer, GroupCondition> {

	/**
	 * The address of the group
	 */
	private final GroupAddress address;

	/**
	 * The organization that this group implements
	 */
	private final WeakReference<Organization> organization;

	/**
	 * boolean precising if this group is distributed over various kernels
	 */
	private final boolean isDistributed;

	/**
	 * boolean precising if this group is persistent when no entity is playing a
	 * role inside
	 */
	private final boolean isPersistent;

	/**
	 * Membership service.
	 */
	private final MembershipService membership;

	/**
	 * Logger for this group.
	 */
	private transient SoftReference<Logger> logger = null;
	
	/**
	 * Role-Agent pairs.
	 */
	private final Map<Class<? extends Role>, RoleDescriptor> playersPerRole = new TreeMap<Class<? extends Role>, RoleDescriptor>(
			GenericComparator.SINGLETON);

	/**
	 * Agent-Role pairs.
	 */
	private final Map<AgentAddress, Collection<Class<? extends Role>>> rolesPerPlayer = new TreeMap<AgentAddress, Collection<Class<? extends Role>>>();

	/**
	 * Use to synchronize internal data structures.
	 */
	protected final ReentrantLock internalStructureLock = new ReentrantLock();
	
	/** Indicates the last date when this group was marked as used.
	 * If the value is <code>null</code>, the date is unknown.
	 * @since 0.4
	 */
	private Float lastUsage = null;
	
	/** Any user data associated to the group and with public access.
	 * @since 0.5
	 */
	private Map<String,Object> publicUserData = null;
	
	/** Any user data associated to the group with group access.
	 * @since 0.5
	 */
	private Object privateUserData = null;

	private ListenerCollection<EventListener> eventListeners = null;
	
	/**
	 * @param organization
	 *            is the implemented organization.
	 * @param address
	 *            is the addres of the group.
	 * @param distributed
	 *            whether the group is distributed over the network or not
	 * @param persistent
	 *            whether the group is persistent when no more role is playing
	 *            inside
	 * @param membership
	 *            is a service to control membership on the group.
	 *            <code>null</code> if none.
	 */
	public KernelScopeGroup(Organization organization, GroupAddress address,
			boolean distributed, boolean persistent,
			MembershipService membership) {
		assert (organization != null);
		assert (address != null);
		this.organization = new WeakReference<Organization>(organization);
		this.address = address;
		this.address.bind(this);
		this.isDistributed = distributed;
		this.isPersistent = persistent;
		this.membership = membership;
	}
	
	/**
	 * Add listener on role playing events in the group only.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	public synchronized void addRolePlayingListener(RolePlayingListener listener) {
		if (this.eventListeners==null)
			this.eventListeners = new ListenerCollection<EventListener>();
		this.eventListeners.add(RolePlayingListener.class, listener);
	}

	/**
	 * Remove listener on role playing events in the group only.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	public synchronized void removeRolePlayingListener(RolePlayingListener listener) {
		if (this.eventListeners!=null) {
			this.eventListeners.remove(RolePlayingListener.class, listener);
			if (this.eventListeners.isEmpty())
				this.eventListeners = null;
		}
	}
	
	/** Replies the listeners of the given types and supported by this role player.
	 * 
	 * @param <L> is the type of the listener.
	 * @param type is the type of the listener.
	 * @return the listeners.
	 * @since 0.5
	 */
	@SuppressWarnings("unchecked")
	public synchronized final <L extends EventListener> L[] getEventListeners(Class<L> type) {
		if (this.eventListeners!=null) {
			return this.eventListeners.getListeners(type);
		}
		return (L[])Array.newInstance(type, 0);
	}
	
	private void firePlayRole(RolePlayingEvent event) {
		for(RolePlayingListener listener : getEventListeners(RolePlayingListener.class)) {
			listener.roleTaken(event);
		}
	}
	
	private void fireLeaveRole(RolePlayingEvent event) {
		for(RolePlayingListener listener : getEventListeners(RolePlayingListener.class)) {
			listener.roleReleased(event);
		}
	}

	/** Replies the public description of the group.
	 * 
	 * @param callerIsMember indicates if the caller
	 * is a member of the group.
	 * @return the public description of the group.
	 */
	Group toGroup(boolean callerIsMember) {
		return new Description(callerIsMember);
	}

	/** Replies if this group could be removed because
	 * it is persistent and too old since its last use.
	 * 
	 * @param currentTime is the current time.
	 * @param delay is the minimal delay of inactivity allowed.
	 * @return <code>true</code> if the group is removable, otherwise
	 * <code>false</code>.
	 * @since 0.4
	 */
	synchronized boolean isTooOldGroup(float currentTime, float delay) {
		if (this.isPersistent && this.playersPerRole.isEmpty()) {
			if (this.lastUsage==null) {
				this.lastUsage = Float.valueOf(currentTime);
			}
			else {
				float lastTime = this.lastUsage.floatValue();
				float delta = currentTime - lastTime;
				if (delta>delay) {
					return true;
				}
			}
		}
		else {
			this.lastUsage = null;
		}
		return false;
	}
	
	/**
	 * Replies the address of the group.
	 * 
	 * @return the address of the group.
	 */
	public GroupAddress getAddress() {
		assert (this.address != null);
		return this.address;
	}

	/**
	 * Replies the implemented organization.
	 * 
	 * @return the implemented organization.
	 */
	public Organization getOrganization() {
		Organization orga = this.organization.get();
		assert (orga != null);
		return orga;
	}

	/**
	 * Replies if this group is able to be distributed among several kernels.
	 * 
	 * @return <code>true</code> if distribution is allowed, otherwise
	 *         <code>false</code>
	 */
	public boolean isDistributed() {
		return this.isDistributed
				&& getOrganization().getCRIOContext().getDistantCRIOContextHandler() != null;
	}

	/**
	 * Replies if this group is persistent when no role player is playing a role
	 * inside.
	 * 
	 * @return <code>true</code> if the group is persistent, otherwise
	 *         <code>false</code>
	 */
	public boolean isPersistent() {
		return this.isPersistent;
	}

	/**
	 * Function allowing the request of the liberation of all the roles played
	 * by the given player..
	 * <p>
	 * This function assumes that a role could be played only one time by a
	 * entity inside a one group.
	 * 
	 * @param player
	 *            is the reference to the requester player.
	 * @return <code>true</code> if at least one role was released,
	 *         <code>false</code> otherwise
	 */
	public boolean leaveAllRoles(RolePlayer player) {
		assert (player != null);
		this.internalStructureLock.lock();
		try {
			Collection<Class<? extends Role>> roles = this.rolesPerPlayer
					.get(player.getAddress());
			if (roles != null) {
				// Copy the roles to avoid concurrent modification exception
				List<Class<? extends Role>> copy = new ArrayList<Class<? extends Role>>(
						roles);
				boolean released = false;
				for (Class<? extends Role> role : copy) {
					if (leaveRole(player, role)) {
						released = true;
					}
				}
				return released;
			}
			return false;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Function allowing the request of the liberation of a given role on this
	 * group
	 * 
	 * @param player
	 *            is the reference to the requester player.
	 * @param role
	 *            is the class of the role to leave.
	 * @return <code>true</code> if the request was accepted, <code>false</code>
	 *         else.
	 */
	public boolean leaveRole(RolePlayer player, Class<? extends Role> role) {
		assert (player != null);
		assert (role != null);
		this.internalStructureLock.lock();
		try {
			RoleDescriptor roleDescriptor = this.playersPerRole.get(role);
	
			if (roleDescriptor != null) {
				AgentAddress adr = player.getAddress();
				assert (adr != null);
				Role roleToRemove = roleDescriptor.getLocalRole(adr);
				if (roleToRemove != null) {
					ConditionFailure failedCondition = verifiesLeaveConditions(
							player, roleToRemove);
					if (failedCondition == null) {
	
						RoleAddress roleAddress = roleToRemove.getAddress();
						roleAddress.unbind();

						if (!roleDescriptor.unregisterAddress(adr)) {
							return false;
						}
						
						GroupAddress myAdr = getAddress(); 
						
						ChannelInteractable ci = (roleToRemove instanceof ChannelInteractable)
									? (ChannelInteractable)roleToRemove : null;
	
						RolePlayingEvent event = new RolePlayingEvent(roleAddress, toGroup(true), ci);

						// Notify player about role releasing to avoid role
						// scheduling
						player.roleReleasing(roleToRemove);
	
						if (roleDescriptor.isEmpty()) {
							this.playersPerRole.remove(role);
						}
	
						Collection<Class<? extends Role>> playedRoles = this.rolesPerPlayer
								.get(adr);
						if (playedRoles != null) {
							playedRoles.remove(role);
							if (playedRoles.isEmpty()) {
								this.rolesPerPlayer.remove(adr);
							}
						}
	
						Status status = roleToRemove.proceedPrivateDestruction();
	
						if (status!=null && status.isLoggable()) {
							status.logOn(player.getLogger());
						}
	
						if (!this.isPersistent && this.playersPerRole.isEmpty()) {
							getOrganization().getCRIOContext().getGroupRepository()
									.removeGroup(myAdr);
						}
						
						// Notify player about role releasing
						player.fireLeaveRole(event);

						// Notifies the other members of the group that a role was released
						for(RoleDescriptor rDesc : this.playersPerRole.values()) {
							for(Role playedRole : rDesc.getLocalRoles()) {
								RolePlayer playerInstance = playedRole.getPlayerInstance();
								if (!adr.equals(playerInstance.getAddress())) {
									playerInstance.fireLeaveRole(event);
								}
							}
						}
						
						// Notifies the listeners on the group
						fireLeaveRole(new RolePlayingEvent(roleAddress, toGroup(false), ci));

						if (isDistributed()) {
							DistantCRIOContextHandler distantKernel = getDistantCRIOContextHandler();
							if (distantKernel!=null)
								distantKernel.informLocalRoleReleased(
									this.address, role, player.getAddress());
						}
	
						return status==null || status.isSuccess();
					}
					player.getLogger().severe(
							Locale.getString(
									KernelScopeGroup.class,
									"INVALID_LEAVE_CONDITIONS", //$NON-NLS-1$
									player.getAddress().toString(),
									role.toString(), failedCondition.toString()));
				} else {
					player.getLogger()
							.warning(
									Locale.getString(
											KernelScopeGroup.class,
											"ROLE_ALREADY_LEAVED", //$NON-NLS-1$
											player.getAddress().toString(),
											role.toString()));
				}
			}
	
			return false;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}
	
	/** Replies the membership.
	 * 
	 * @return the membership.
	 * @since 0.4
	 */
	public MembershipService getMembership() {
		return this.membership;
	}

	/**
	 * Verifies if the obtain contraints for the group and the specified role
	 * are respected by the specified entity.
	 * 
	 * @param player
	 *            - entity requesting the role
	 * @param role
	 *            - reqeuested role
	 * @return <code>null</code> if all conditions are matching, otherwise the
	 *         first failed condition.
	 */
	protected ConditionFailure verifiesObtainConditions(RolePlayer player, Role role) {
		assert (player != null);
		assert (role != null);
		ConditionFailure cf = null;
		
		// Test the membership
		if (this.membership != null) {
			Status s = this.membership.validateRoleTaker(player.getAddress(),
					role.getClass(), player.getCredentials());
			if (s!=null && !s.isSuccess()) {
				return new StatusConditionFailure(s);
			}
		}

		// Test the obtain conditions from the group
		Collection<GroupCondition> conditions = getObtainConditions();
		if (!conditions.isEmpty()) {
			Group grp = toGroup(false);
			Class<? extends Role> roleType = role.getClass();
			for (GroupCondition c : conditions) {
				cf = c.evaluateFailureOnGroup(player, roleType, grp);
				if (cf!=null) return cf;
			}
		}

		// Test the obtain conditions from the role itself
		return role.getObtainFailure(player);
	}

	/**
	 * Verifies if the leave contraints for the group and the specified role are
	 * respected by the specified entity.
	 * 
	 * @param player
	 *            - entity requesting the role
	 * @param role
	 *            - reqeuested role
	 * @return <code>null</code> if all conditions are matching, otherwise the
	 *         first failed condition.
	 */
	protected ConditionFailure verifiesLeaveConditions(RolePlayer player,
			Role role) {
		assert (player != null);
		assert (role != null);
		ConditionFailure cf;

		// Test the leave conditions of the group.
		Collection<GroupCondition> conditions = getLeaveConditions();
		if (!conditions.isEmpty()) {
			Group grp = toGroup(false);
			Class<? extends Role> roleType = role.getClass();
			for (GroupCondition c : conditions) {
				cf = c.evaluateFailureOnGroup(player, roleType, grp);
				if (cf!=null) return cf;
			}
		}

		// Test the leave conditions of the role itself.
		return role.getLeaveFailure(player);
	}

	/**
	 * Function allowing the request of the obtention of a given role on this
	 * group.
	 * 
	 * @param player
	 *            is the reference to the requester player.
	 * @param role
	 *            is the class of the requested role.
	 * @param factory
	 *            is the factory to use to create an instance of the role.
	 * @param accessContext
	 *            is the context of access control to use when instanciating the
	 *            role. If <code>null</code>, the default context will be used.
	 * @param initObjects
	 *            are the parameters to pass to the
	 *            {@link Role#activate(Object[]) initialization function} of the
	 *            role.
	 * @return the address of the receiver.
	 */
	public RoleAddress requestRole(RolePlayer player, Class<? extends Role> role,
			RoleFactory factory, AccessControlContext accessContext,
			Object... initObjects) {
		assert (player != null);
		assert (role != null);
		this.internalStructureLock.lock();
		try {
			// Check if role is defined in organization
			Organization organization = getOrganization();
			assert (organization != null);
			if (!organization.contains(role)) {
				player.getLogger().warning(
						Locale.getString(KernelScopeGroup.class, "ROLE_NOT_DEFINED", //$NON-NLS-1$
								role.getCanonicalName(), organization.getClass()
										.getCanonicalName()));
				return null;
			}
	
			boolean assigned = false;
			Role roleToTake = null;
			GroupAddress myAdr = getAddress();
			AgentAddress adr = player.getAddress();			
	
			try {
				roleToTake = AccessController.doPrivileged(new RoleInstanciator(role,
						factory), accessContext);
			}
			catch(AssertionError ae) {
				throw ae;
			}
			catch (PrivilegedActionException e) {
				Throwable t = e;
				while (t.getCause() != null) {
					t = t.getCause();
				}
				player.getLogger().log(
						Level.SEVERE, t.getLocalizedMessage(),
						t);
			}
	
			if (roleToTake != null) {
				ConditionFailure failedCondition = verifiesObtainConditions(player,
						roleToTake);
				if (failedCondition == null) {
	
					Status s = roleToTake.proceedPrivateInitialization(
							organization.getCRIOContext(), this, player,
							initObjects);
					if (s!=null) {
						if (s.isFailure()) {
							throw new RoleNotInitializedException(s);
						}
						else if (s.isLoggable()) {
							s.logOn(roleToTake.getLogger());
						}
					}
	
					RoleDescriptor roleDescriptor = getRoleDescriptor(role);
	
					if (roleDescriptor.containsLocalPlayer(adr)) {
						player.getLogger().warning(
								Locale.getString(KernelScopeGroup.class,
										"INVALID_DOUBLE_ROLE_TAKING", //$NON-NLS-1$
										role,
										myAdr,
										adr));
					} else {
						assigned = roleDescriptor.playLocalRole(adr, roleToTake);
						if (assigned) {
							Collection<Class<? extends Role>> playedRoles = this.rolesPerPlayer
									.get(adr);
							if (playedRoles == null) {
								playedRoles = new TreeSet<Class<? extends Role>>(
										GenericComparator.SINGLETON);
								this.rolesPerPlayer.put(adr, playedRoles);
							}
							playedRoles.add(role);
							
							ChannelInteractable ci = (roleToTake instanceof ChannelInteractable)
									? (ChannelInteractable)roleToTake : null;

							RolePlayingEvent event = new RolePlayingEvent(roleToTake.getAddress(), toGroup(true), ci);
							
							// Notify player about role taking
							player.roleTaken(roleToTake, event);

							// Notifies the other members of the group that a role was taken
							RolePlayer playerInstance;
							for(RoleDescriptor rDesc : this.playersPerRole.values()) {
								for(Role playedRole : rDesc.getLocalRoles()) {
									playerInstance = playedRole.getPlayerInstance();
									assert(playerInstance!=null);
									if (!adr.equals(playerInstance.getAddress()))
										playerInstance.firePlayRole(event);
								}
							}

							// Notifies the listeners on the group
							firePlayRole(new RolePlayingEvent(roleToTake.getAddress(), toGroup(false), ci));
						}
					}
	
				} else {
					player.getLogger().warning(
							Locale.getString(
									KernelScopeGroup.class,
									"INVALID_OBTAIN_CONDITIONS", //$NON-NLS-1$
									player.getAddress().toString(),
									role.toString(), failedCondition.toString()));
				}
			}
	
			if (assigned && isDistributed()) {
				DistantCRIOContextHandler distantKernel = getDistantCRIOContextHandler();
				if (distantKernel!=null)
					distantKernel.informLocalRoleTaken(myAdr,
						role, adr);
			}
	
			if (assigned) {
				assert(roleToTake!=null);
				return roleToTake.getAddress();
			}
			return null;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Replies the {@link RoleDescriptor} associated to the role class. If none
	 * is found it creates one.
	 * 
	 * @param role
	 *            the role class
	 * @return
	 */
	private RoleDescriptor getRoleDescriptor(Class<? extends Role> role) {
		this.internalStructureLock.lock();
		try {
			RoleDescriptor roleDescriptor;
			roleDescriptor = this.playersPerRole.get(role);
			if (roleDescriptor == null) {
				roleDescriptor = new RoleDescriptor(this.address);
				this.playersPerRole.put(role, roleDescriptor);
			}
			return roleDescriptor;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Sends a message to a distant role player.
	 * 
	 * @param msg
	 *            the message to send
	 * @return the address of the receiver or <code>null</code> if the
	 * message was not sent.
	 */
	protected RoleAddress sendMessageToRemoteKernel(Message msg) {
		DistantCRIOContextHandler distantKernel = getDistantCRIOContextHandler();
		if (distantKernel!=null) {
			Address adr = distantKernel.sendMessage(msg);
			assert(adr instanceof RoleAddress);
			return (RoleAddress)adr;
		}
		return null;
	}

	private DistantCRIOContextHandler getDistantCRIOContextHandler() {
		return this.organization.get().getCRIOContext().getDistantCRIOContextHandler();
	}

	/**
	 * Broadcast the specified <code>Message</code> to all entities playing the
	 * <code>Role</code> in the field <code>receiverRole</code> of the message.
	 * 
	 * @param message
	 *            is the message to broadcast
	 * @param includeSender
	 *            indicates if the message sender may also receive the message.
	 */
	public void broadcastMessage(Message message, boolean includeSender) {
		this.internalStructureLock.lock();
		try {
			assert (message != null);
			assert (message.getSender() instanceof RoleAddress);
			assert (message.getReceiver() instanceof RoleAddress);
			RoleAddress senderAddress = (RoleAddress)message.getSender();
			RoleAddress receiverAddress = (RoleAddress)message.getReceiver();
			
			Class<? extends Role> receiverRole = receiverAddress.getRole();
			assert(receiverRole!=null);
			
			Organization orga = this.organization.get();
			assert (orga != null);
	
			Iterator<Class<? extends Role>> roles = orga.iterator(receiverRole);
			assert(roles!=null);

			if (!roles.hasNext())
				throw new UndefinedRoleException(orga.getClass(), receiverRole);
			
			Class<? extends Role> realReceiverRole;
			
			while (roles.hasNext()) {
				realReceiverRole = roles.next();
		
				RoleDescriptor roleDescriptor = this.playersPerRole.get(realReceiverRole);
		
				if (roleDescriptor != null) {
		
					assert(senderAddress.getGroup().equals(getAddress()));
		
					assert (senderAddress.getRole() != null);
		
					// local broadcast
					if (includeSender) {
						for (Role r : roleDescriptor.getLocalRoles()) {
							r.getMailbox().add(message);
						}
					}
					else {
						for (Role r : roleDescriptor.getLocalRoles()) {
							if ((!senderAddress.getPlayer().equals(r.getPlayer()))
								|| (!senderAddress.getRole().equals(r.getClass()))) {
								r.getMailbox().add(message);
							}
						}
					}
				}
				else {
					getLogger().fine(Locale.getString(KernelScopeGroup.class, "NO_ROLE_DESCRIPTOR_WHEN_BROADCASTING_MESSAGE",  //$NON-NLS-1$
							getAddress().toString(), realReceiverRole.toString()));
				}
				// remote broadcast
		
				if (isDistributed()) {
		
					RoleDescriptor senderRoleDescriptor = this.playersPerRole.get(senderAddress.getRole());
					// if senderRoleDescriptor == null no local players are here so it
					// is a distant message => DONT LOOP
					// check if the sender is local or not
					if (senderRoleDescriptor != null
							&& senderRoleDescriptor.containsLocalPlayer(senderAddress.getPlayer())) {
						DistantCRIOContextHandler distantKernel = getDistantCRIOContextHandler();
						if (distantKernel!=null) {
							distantKernel.broadcastMessage(message);
						}
					}
		
				}
			}
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Send the specified <code>Message</code>.
	 * 
	 * @param message
	 *            is the message to send
	 * @param includeSender
	 *            indicates if the message sender may also receive the message.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 */
	public RoleAddress sendMessage(Message message, boolean includeSender) {
		this.internalStructureLock.lock();
		try {
			assert (message != null);
			assert (message.getSender() instanceof RoleAddress);
			assert (message.getReceiver() instanceof RoleAddress);
			RoleAddress senderAddress = (RoleAddress)message.getSender();
			RoleAddress receiverAddress = (RoleAddress)message.getReceiver();
			
			Role receivingRole = receiverAddress.getRoleObject();
			
			if (receivingRole==null || receivingRole.isReleased() || receivingRole.hasMigrated()) {
				// The address is not binded to an instance of role.
				// Find the best one from the role
				
				receiverAddress.unbind(); // Force to be unbind
				receivingRole = null;
				
				Organization orga = this.organization.get();
				assert (orga != null);
				Iterator<Class<? extends Role>> roles = orga.iterator(receiverAddress.getRole());
				assert(roles!=null);
				if (!roles.hasNext())
					throw new UndefinedRoleException(orga.getClass(), receiverAddress.getRole());

				Class<? extends Role> candidateRole;

				if (includeSender
					|| (!senderAddress.equals(receiverAddress))) {

					while (receivingRole==null && roles.hasNext()) {
						candidateRole = roles.next();
	
						RoleDescriptor roleDescriptor = this.playersPerRole.get(candidateRole);
	
						if (roleDescriptor != null) {
							Role r = roleDescriptor.getLocalRole(receiverAddress.getPlayer());
							if (r != null) {
								receivingRole = r;
							}
							else if (isDistributed()) {
								//
								// MESSAGE IS FORWARDED TO REMOTE KERNEL
								//
								return sendMessageToRemoteKernel(message);
							}
							else {
								throw new MailboxNotFoundException(message);
							}
						}
					}
					
				}
				
				receiverAddress.bind(receivingRole);
			}
			
			if (receivingRole!=null) {
				//
				// MESSAGE IS ENQUEUED
				//
				receivingRole.getMailbox().add(message);
				return receivingRole.getAddress();
			}

			throw new ReceiverNotFoundException(message);
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Replies a logger dedicated to this group.
	 * 
	 * @return the dedicated logger.
	 */
	protected Logger getLogger() {
		Logger logger = this.logger==null ? null : this.logger.get();
		if (logger==null) {
			logger = LoggerUtil.createGroupLogger(getClass(),
					getOrganization().getCRIOContext().getTimeManager(),
					getAddress());
			this.logger = new SoftReference<Logger>(logger);
		}
		return logger;

	}

	/**
	 * Set the obtain and leave conditions at same time.
	 * <p>
	 * This method is here to avoid {@link #setLeaveCondition(Collection)} and
	 * {@link #setObtainCondition(Collection)} to be public.
	 * 
	 * @param obtainConditions
	 * @param leaveConditions
	 */
	void setObtainLeaveConditions(
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		setObtainCondition(obtainConditions);
		setLeaveCondition(leaveConditions);
	}

	/** Replies the address of the specified role in
	 * the current group.
	 *  
	 * @param role is the played role.
	 * @param player is the player of the role
	 * @return the address or <code>null</code>.
	 * @since 0.5
	 */
	public RoleAddress getRoleAddress(Class<? extends Role> role, AgentAddress player) {
		this.internalStructureLock.lock();
		try {
			RoleDescriptor roleDescriptor = this.playersPerRole.get(role);
			if (roleDescriptor != null) {
				Role r = roleDescriptor.getLocalRole(player);
				if (r!=null) {
					return r.getAddress();
				}
			}
			return null;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/** Replies the addresses of the specified role in
	 * the current group.
	 *  
	 * @param role is the played role.
	 * @return the address, never <code>null</code>.
	 * @since 0.5
	 */
	public SizedIterator<RoleAddress> getRoleAddresses(Class<? extends Role> role) {
		this.internalStructureLock.lock();
		try {
			RoleDescriptor roleDescriptor = this.playersPerRole.get(role);
			if (roleDescriptor != null) {
				return roleDescriptor.getRoleAddresses();
			}
			return EmptyIterator.singleton();
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/** Replies the role addresses in
	 * the current group.
	 *  
	 * @return the addresses, never <code>null</code>.
	 * @since 0.5
	 */
	public SizedIterator<RoleAddress> getRoleAddresses() {
		this.internalStructureLock.lock();
		try {
			MultiSizedIterator<RoleAddress> iterators = new MultiSizedIterator<RoleAddress>();
			for(RoleDescriptor desc : this.playersPerRole.values()) {
				iterators.addIterator(desc.getRoleAddresses());
			}
			return iterators;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns the list of the addresses of the role players currently playing
	 * the specified role.
	 * 
	 * @param role
	 *            is the role of which obtain the number of players.
	 * @return the addresses of the entities currently playing the specified
	 *         role.
	 */
	public SizedIterator<AgentAddress> getRolePlayers(Class<? extends Role> role) {
		this.internalStructureLock.lock();
		try {
			RoleDescriptor roleDescriptor = this.playersPerRole.get(role);
			if (roleDescriptor != null) {
				return roleDescriptor.getAddresses();
			}
			return EmptyIterator.singleton();
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns the number of players for the given role in the group.
	 * 
	 * @param role
	 * @return the number of players for the given role in the group.
	 * @since 0.5
	 */
	public int getPlayerCount(Class<? extends Role> role) {
		this.internalStructureLock.lock();
		try {
			RoleDescriptor roleDescriptor = this.playersPerRole.get(role);
			if (roleDescriptor != null) {
				return roleDescriptor.getAddressCollection().size();
			}
			return 0;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns the number of players in the group.
	 * 
	 * @return the number of players in the group.
	 * @since 0.5
	 */
	public int getPlayerCount() {
		this.internalStructureLock.lock();
		try {
			return this.rolesPerPlayer.size();
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns the list of the addresses of the role players currently playing
	 * the specified role.
	 * 
	 * @param role
	 *            is the role of which obtain the number of players.
	 * @return the addresses of the entities currently playing the specified
	 *         role.
	 */
	public DirectAccessCollection<AgentAddress> getRolePlayerCollection(
			Class<? extends Role> role) {
		this.internalStructureLock.lock();
		try {
			RoleDescriptor roleDescriptor = this.playersPerRole.get(role);
			if (roleDescriptor != null) {
				return roleDescriptor.getAddressCollection();
			}
			return UnmodifiableDirectAccessCollection.empty();
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}
	
	/** Select and reply a role player according to the given policy.
	 * <p>
	 * This function was introduced to avoid to copy the role players
	 * into a temp collection and select the player from this collection.
	 * 
	 * @param role is the played role. 
	 * @param exceptFor is the address that cannot be selected.
	 * @param policy is the selection policy.
	 * @return the selected player, or <code>null</code> if no selection found.
	 * @since 0.5
	 */
	public AgentAddress selectRolePlayer(Class<? extends Role> role, AgentAddress exceptFor, MessageReceiverSelectionPolicy policy) {
		this.internalStructureLock.lock();
		try {
			RoleDescriptor roleDescriptor = this.playersPerRole.get(role);
			if (roleDescriptor != null) {
				return roleDescriptor.selectRolePlayer(role, exceptFor, policy);
			}
			return null;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns the list of the roles currently played.
	 * 
	 * @return the played roles.
	 */
	public SizedIterator<Class<? extends Role>> getPlayedRoles() {
		this.internalStructureLock.lock();
		try {
			return new UnmodifiableMapKeySizedIterator<Class<? extends Role>>(
					this.playersPerRole);
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns the list of the roles currently played in the form of a
	 * collection.
	 * 
	 * @return the played roles.
	 */
	Collection<Class<? extends Role>> getPlayedRolesAsCollection() {
		this.internalStructureLock.lock();
		try {
			return Collections.unmodifiableCollection(this.playersPerRole.keySet());
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns the list of the roles currently played by the given agent.
	 * 
	 * @param player
	 * @return the played roles.
	 */
	SizedIterator<Role> getRoles(AgentAddress player) {
		this.internalStructureLock.lock();
		try {
			Collection<Class<? extends Role>> roles = this.rolesPerPlayer.get(player);
			if (roles!=null) {
				return new RoleIterator(player, new ArrayList<Class<? extends Role>>(roles));
			}
			return EmptyIterator.singleton();
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns the list of the roles currently played by the given agent.
	 * 
	 * @param player
	 * @return the played roles.
	 */
	SizedIterator<RoleAddress> getRoleAddresses(AgentAddress player) {
		return new RoleAddressIterator(getRoles(player));
	}

	/**
	 * Returns the list of played roles by the given player.
	 * 
	 * @param player
	 *            is the address of the role player for which roles may be
	 *            replied.
	 * @return the played roles.
	 */
	public Collection<Class<? extends Role>> getPlayedRoles(AgentAddress player) {
		this.internalStructureLock.lock();
		try {
			Collection<Class<? extends Role>> collection = this.rolesPerPlayer
					.get(player);
			if (collection == null)
				return Collections.emptyList();
			return Collections.unmodifiableCollection(collection);
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Returns instance of of the given Role for the given entity.
	 * 
	 * @param <R>
	 *            is the role type.
	 * @param player
	 *            is the address of the role player for which role mays be
	 *            replied.
	 * @param role
	 *            is the role type.
	 * @return the played role or <code>null</code>.
	 */
	public <R extends Role> R getPlayedRole(AgentAddress player, Class<R> role) {
		assert (role != null);
		assert (player != null);
		this.internalStructureLock.lock();
		try {
			RoleDescriptor descriptor = this.playersPerRole.get(role);
			if (descriptor != null) {
				Role r = descriptor.getLocalRole(player);
				if (r != null && role.isInstance(r))
					return role.cast(r);
			}
			return null;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Replies if the given role is played.
	 * 
	 * @param role
	 *            is the role to search for.
	 * @return <code>true</code> if the given role is played, otherwise
	 *         <code>false</code>
	 */
	public boolean isPlayedRole(Class<? extends Role> role) {
		assert (role != null);
		this.internalStructureLock.lock();
		try {
			return this.playersPerRole.containsKey(role);
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Replies if the given role is played by the given entity.
	 * 
	 * @param player
	 *            is the address of the player to search for.
	 * @param role
	 *            is the role to search for.
	 * @return <code>true</code> if the given role is played, otherwise
	 *         <code>false</code>
	 */
	public boolean isPlayedRole(AgentAddress player, Class<? extends Role> role) {
		assert (role != null);
		this.internalStructureLock.lock();
		try {
			Collection<Class<? extends Role>> roles = this.rolesPerPlayer
					.get(player);
			return roles != null && roles.contains(role);
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Replies if the player is playing a rolein this group.
	 * 
	 * @param player
	 *            is the address of the player to search for.
	 * @return <code>true</code> if the given player is a player, otherwise
	 *         <code>false</code>
	 */
	public boolean isPlayedRole(AgentAddress player) {
		assert (player != null);
		this.internalStructureLock.lock();
		try {
			return this.rolesPerPlayer.containsKey(player);
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}
	
	/** Replies any public user data associated to the group.
	 * 
	 * @param key is the name of the data to set.
	 * @return any public user data associated to the group.
	 * @since 0.5
	 */
	public Object getPublicUserData(String key) {
		this.internalStructureLock.lock();
		try {
			if (this.publicUserData==null) return null;
			return this.publicUserData.get(key);
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/** Set any public user data associated to the group.
	 *
	 * @param key is the name of the data to set.
	 * @param userData is any user data associated to the group.
	 * @return the value of the public user data previously stored in the group.
	 * @since 0.5
	 */
	public Object setPublicUserData(String key, Object userData) {
		this.internalStructureLock.lock();
		try {
			if (this.publicUserData==null) {
				this.publicUserData = new TreeMap<String,Object>();
			}
			return this.publicUserData.put(key, userData);
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/** Replies any private user data associated to the group.
	 * 
	 * @return any private user data associated to the group.
	 * @since 0.5
	 */
	public Object getPrivateUserData() {
		this.internalStructureLock.lock();
		try {
			return this.privateUserData;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/** Set any private user data associated to the group.
	 * 
	 * @param userData is any user data associated to the group.
	 * @return the value of the user data previously stored in the group.
	 * @since 0.5
	 */
	public Object setPrivateUserData(Object userData) {
		this.internalStructureLock.lock();
		try {
			Object old = this.privateUserData;
			this.privateUserData = userData;
			return old;
		}
		finally {
			this.internalStructureLock.unlock();
		}
	}

	/**
	 * Describes the players of a role inside a group.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RoleDescriptor {

		private SortedMap<AgentAddress, Role> localEntities = null;

		private Set<AgentAddress> remoteEntities = null;

		/**
		 * Create a role descriptor.
		 * 
		 * @param myGroup is the address of the group in which the role is played.
		 */
		public RoleDescriptor(GroupAddress myGroup) {
			//
		}
		
		/**
		 * Replies the list of roles, which are played locally.
		 * 
		 * @return the list of local roles.
		 */
		public Collection<Role> getLocalRoles() {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				if (this.localEntities == null)
					return Collections.emptyList();
				return Collections.unmodifiableCollection(this.localEntities
						.values());
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}
		
		/**
		 * Replies the addresses of the players ever they are local or distant.
		 * 
		 * @return the addresses.
		 */
		public SizedIterator<AgentAddress> getAddresses() {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				boolean noLocal = (this.localEntities == null || this.localEntities
						.isEmpty());
				boolean noRemote = (this.remoteEntities == null || this.remoteEntities
						.isEmpty());
				if (noLocal && noRemote)
					return EmptyIterator.singleton();
				if (noRemote)
					return new UnmodifiableMapKeySizedIterator<AgentAddress>(
							this.localEntities);
				if (noLocal)
					return new UnmodifiableCollectionSizedIterator<AgentAddress>(
							this.remoteEntities);
				return new DoubleSizedIterator<AgentAddress>(
						this.localEntities.keySet(), this.remoteEntities);
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

		/** Replies the address of the specified role in
		 * the current group.
		 *  
		 * @return the address or <code>null</code>.
		 * @since 0.5
		 */
		public SizedIterator<RoleAddress> getRoleAddresses() {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				return new RoleAddressIterator(this.localEntities.values());
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

		/**
		 * Replies the addresses of the player ever they are local or distant.
		 * 
		 * @return the addresses.
		 */
		public DirectAccessCollection<AgentAddress> getAddressCollection() {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				boolean noLocal = (this.localEntities == null || this.localEntities
						.isEmpty());
				boolean noRemote = (this.remoteEntities == null || this.remoteEntities
						.isEmpty());
				if (noLocal && noRemote)
					return UnmodifiableDirectAccessCollection.empty();
				if (noRemote)
					return new UnmodifiableDirectAccessSet<AgentAddress>(
							this.localEntities.keySet());
				if (noLocal)
					return new UnmodifiableDirectAccessSet<AgentAddress>(
							this.remoteEntities);
				return new UnmodifiableDirectAccessSetSet<AgentAddress>(
						this.localEntities.keySet(), this.remoteEntities);
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

		/** Select and reply a role player according to the given policy.
		 * <p>
		 * This function was introduced to avoid to copy the role players
		 * into a temp collection and select the player from this collection.
		 * 
		 * @param role is the played role. 
		 * @param exceptFor is the address that cannot be selected.
		 * @param policy is the selection policy.
		 * @return the selected player, or <code>null</code> if no selection found.
		 * @since 0.5
		 */
		public AgentAddress selectRolePlayer(Class<? extends Role> role, AgentAddress exceptFor, MessageReceiverSelectionPolicy policy) {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				return policy.selectEntity(
						exceptFor,
						new UnmodifiableDirectAccessSetSet<AgentAddress>(
								this.localEntities.keySet(), this.remoteEntities));	
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

		/**
		 * Replies the role instance associated to the specified entity.
		 * 
		 * @param entity
		 *            is the entity to use.
		 * @return the role for the given entity or <code>null</code> if
		 * the given player is not playing role locally.
		 */
		public Role getLocalRole(AgentAddress entity) {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				if (this.localEntities == null)
					return null;
				return this.localEntities.get(entity);
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

		/**
		 * Replies if a entity is playing this role.
		 * 
		 * @return <code>true</code> if this descriptor contains no player,
		 *         otherwise <code>false</code>
		 */
		public boolean isEmpty() {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				return (this.localEntities == null || this.localEntities.isEmpty())
						&& (this.remoteEntities == null || this.remoteEntities
								.isEmpty());
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

		/**
		 * Replies if the specified entity is registered as local for
		 * the role.
		 * 
		 * @param entity
		 *            is the entity to test.
		 * @return <code>true</code> if the given entity is inside this
		 *         descriptor, otherwise <code>false</code>
		 */
		public boolean containsLocalPlayer(AgentAddress entity) {
			assert (entity != null);
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				return (this.localEntities != null && this.localEntities
						.containsKey(entity));
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

		/**
		 * Register the specified entity as local player.
		 * 
		 * @param entity
		 *            is the entity to mark as local.
		 * @param role
		 *            is the role played by the entity.
		 * @return <code>true</code> if the entity was successfully added,
		 *         otherwise <code>false</code>
		 */
		public boolean playLocalRole(AgentAddress entity, Role role) {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				if (this.localEntities == null)
					this.localEntities = new TreeMap<AgentAddress,Role>();
				this.localEntities.put(entity, role);
				return true;
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

		/**
		 * Unregister the specified entity.
		 * 
		 * @param entity
		 *            is the entity to unmark.
		 * @return <code>true</code> if the entity was successfully removed,
		 *         otherwise <code>false</code>
		 */
		public boolean unregisterAddress(AgentAddress entity) {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				boolean removed = false;
				if (this.localEntities != null) {
					removed = (this.localEntities.remove(entity) != null)
							|| removed;
					if (this.localEntities.isEmpty())
						this.localEntities = null;
				}
				if (this.remoteEntities != null) {
					removed = this.remoteEntities.remove(entity) || removed;
					if (this.remoteEntities.isEmpty())
						this.remoteEntities = null;
				}
				return removed;
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
		}

	} /* class RoleDescriptor */

	/**
	 * Priviligied instanciator of role.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class RoleInstanciator implements
			PrivilegedExceptionAction<Role> {

		private final Class<? extends Role> type;
		private final RoleFactory factory;

		/**
		 * @param type
		 *            is the type of the expected role.
		 * @param factory
		 *            is the object to invoke to create a new instance of the
		 *            role.
		 */
		public RoleInstanciator(Class<? extends Role> type, RoleFactory factory) {
			this.type = type;
			this.factory = factory;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Role run() throws Exception {
			if (this.factory != null)
				return this.factory.newInstance(this.type);
			return this.type.newInstance();
		}

	} // class RoleInstanciator

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class StatusConditionFailure implements ConditionFailure {

		private static final long serialVersionUID = -2582834464424545616L;

		private final Status status;

		/**
		 * @param status
		 *            is the failure status.
		 */
		public StatusConditionFailure(Status status) {
			this.status = status;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.status.toString();
		}

	} // class StatusConditionFailure
	
	/**
	 * Private implementation of the group description.
	 * 
	 * @author $Author: ngaud$
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.4
	 */
	private class Description implements Group {

		private final boolean isForMember;
		
		/**
		 * @param isForMember indicates if this description
		 * is for a member of the group.
		 */
		public Description(boolean isForMember) {
			this.isForMember = isForMember;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return KernelScopeGroup.this.getAddress().hashCode();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Group) {
				KernelScopeGroup.this.getAddress().equals(((Group)obj).getAddress());
			}
			return KernelScopeGroup.this.getAddress().equals(obj);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getAddress() {
			return KernelScopeGroup.this.getAddress();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<GroupCondition> getLeaveConditions() {
			return KernelScopeGroup.this.getLeaveConditions();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MembershipService getMembership() {
			return KernelScopeGroup.this.getMembership();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<GroupCondition> getObtainConditions() {
			return KernelScopeGroup.this.getObtainConditions();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Organization getOrganization() {
			return KernelScopeGroup.this.getOrganization();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDistributed() {
			return KernelScopeGroup.this.isDistributed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isPersistent() {
			return KernelScopeGroup.this.isPersistent();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getPublicUserData(String key) {
			return KernelScopeGroup.this.getPublicUserData(key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object setPublicUserData(String key, Object userData) {
			if (this.isForMember) {
				return KernelScopeGroup.this.setPublicUserData(key, userData);
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getPrivateUserData() {
			if (this.isForMember)
				return KernelScopeGroup.this.getPrivateUserData();
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object setPrivateUserData(Object userData) {
			if (this.isForMember) {
				return KernelScopeGroup.this.setPrivateUserData(userData);
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addRolePlayingListener(RolePlayingListener listener) {
			KernelScopeGroup.this.addRolePlayingListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeRolePlayingListener(RolePlayingListener listener) {
			KernelScopeGroup.this.removeRolePlayingListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getPlayerCount(Class<? extends Role> role) {
			return KernelScopeGroup.this.getPlayerCount(role);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isPlayedRole(Class<? extends Role> role) {
			return KernelScopeGroup.this.isPlayedRole(role);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getPlayerCount() {
			return KernelScopeGroup.this.getPlayerCount();
		}

	} // class Description
	
	/**
	 * @author $Author: ngaud$
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.4
	 */
	private static class RoleAddressIterator implements SizedIterator<RoleAddress> {

		private final SizedIterator<Role> roles;
		
		public RoleAddressIterator(Collection<Role> roles) {
			this.roles = new UnmodifiableCollectionSizedIterator<Role>(roles);
		}

		public RoleAddressIterator(SizedIterator<Role> roles) {
			this.roles = roles;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.roles.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public RoleAddress next() {
			Role r = this.roles.next();
			return r.getAddress();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			this.roles.remove();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int totalSize() {
			return this.roles.totalSize();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int rest() {
			return this.roles.rest();
		}
		
	} // class RoleAddressIterator
	
	/**
	 * @author $Author: ngaud$
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.4
	 */
	private class RoleIterator implements SizedIterator<Role> {

		private final AgentAddress adr;
		private final SizedIterator<Class<? extends Role>> roleTypes;
		private Role next;
		private int rest;
		
		public RoleIterator(AgentAddress adr, Collection<Class<? extends Role>> roles) {
			this.adr = adr;
			this.roleTypes = new ModifiableCollectionSizedIterator<Class<? extends Role>>(roles);
			this.rest = this.roleTypes.totalSize();
			searchNext();
		}
		
		@SuppressWarnings("synthetic-access")
		private void searchNext() {
			KernelScopeGroup.this.internalStructureLock.lock();
			try {
				this.next = null;
				while (this.next==null && this.roleTypes.hasNext()) {
					Class<? extends Role> r  = this.roleTypes.next();
					assert(r!=null);
					RoleDescriptor rd = KernelScopeGroup.this.playersPerRole.get(r);
					assert(rd!=null);
					this.next = rd.getLocalRole(this.adr);
				}
			}
			finally {
				KernelScopeGroup.this.internalStructureLock.unlock();
			}
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
		public Role next() {
			Role r = this.next;
			if (r==null) throw new NoSuchElementException();
			--this.rest;
			searchNext();
			return r;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			this.roleTypes.remove();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int totalSize() {
			return this.roleTypes.totalSize();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int rest() {
			return this.rest;
		}
		
	} // class RoleIterator

}