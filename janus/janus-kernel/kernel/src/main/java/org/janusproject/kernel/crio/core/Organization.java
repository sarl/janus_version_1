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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.condition.Condition;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.organization.OrganizationInstanciationError;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * In Janus, organizations are generic descriptions of the way agents interact.
 * An organization can be instanciated.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class Organization
implements Iterable<Class<? extends Role>> {

	/**
	 * the set of conditions to satisfy before accessing/assuming/acquiring a group
	 */
	private Collection<GroupCondition> groupObtainConditions = null;

	/**
	 * the set of conditions to satisfy before leaving/liberating a group
	 */
	private Collection<GroupCondition> groupLeaveConditions = null;
	/**
	 * The set of role classes defined on this organization
	 */
	private final Collection<Class<? extends Role>> definedRoles = new ArrayList<Class<? extends Role>>();

	/**
	 * The set of groups currently instianciated.
	 */
	private final TreeSet<GroupAddress> groups = new TreeSet<GroupAddress>();

	/** CRIO execution context in which this organization is defined.
	 */
	private final WeakReference<CRIOContext> crioContext;
	
	/** Indicates if this organization was previously registered as a singleton.
	 */
	volatile boolean isSingleton = false;
	
	/**
	 * Logger for this organization.
	 */
	private transient SoftReference<Logger> logger = null;

	/**
	 * Builds a new organization.
	 * 
	 * @param crioContext is the CRIO execution context.
	 */
	protected Organization(CRIOContext crioContext) {
		assert(crioContext!=null);
		this.crioContext = new WeakReference<CRIOContext>(crioContext);
	}
	
	private void checkSingleton() {
		if (!this.isSingleton) {
			throw new OrganizationInstanciationError(
					Locale.getString(Organization.class, 
							"NOT_A_REGISTERED_SINGLETON", //$NON-NLS-1$
							getClass().getCanonicalName()));
		}
	}
	
	/** Replies the CRIO context of this organization.
	 * 
	 * @return the CRIO context of this organization.
	 */
	public CRIOContext getCRIOContext() {
		return this.crioContext.get();
	}
	
	/** Invoked when this oranization is no more required in the kernel.
	 * <p>
	 * This function is basically invoked when no more group is implementing
	 * this organization.
	 */
	protected synchronized void destroy() {
		this.definedRoles.clear();
		this.groups.clear();
	}

	/**
	 * Add a new role to the set of roles defined on this organization
	 * 
	 * @param role -
	 *            the type of the role to add
	 */
	protected final void addRole(Class<? extends Role> role) {
		this.definedRoles.add(role);
	}

	/**
	 * Remove a role from the set of roles defined on this organization
	 * 
	 * @param role -
	 *            the type of the role to add
	 */
	protected final void removeRole(Class<? extends Role> role) {
		this.definedRoles.remove(role);
	}

	/**
	 * Verify if the specified role class is defined on this organization.
	 * 
	 * @param role -
	 *            the role to test
	 * @return <tt>true<tt> if the specified role is defined on this organization, false else.
	 */
	public final boolean contains(Class<? extends Role> role) {
		return this.definedRoles.contains(role);
	}

	/**
	 * Replies the roles in the organization that are subclasses of
	 * the given classes.
	 * 
	 * @param type
	 * @return the roles of the organization that are subclasses of <var>type</var>.
	 * @since 0.5
	 */
	public final Iterator<Class<? extends Role>> iterator(Class<? extends Role> type) {
		return new RoleIterator(type, iterator());
	}

	/** {@inheritDoc}
	 */
	@Override
	public Iterator<Class<? extends Role>> iterator() {
		return this.definedRoles.iterator();
	}

	/**
	 * Returns the collection of Role Classes defined on this organization
	 * 
	 * @return the collection of Role Classes defined on this organization
	 */
	public final Collection<Class<? extends Role>> getDefinedRoles() {
		return this.definedRoles;
	}
		
	/**
	 * Ramdomly gets the address of an already existing group implementing this
	 * organization if any, or creates a new one.
	 * 
	 * @return the address of the group, never <code>null</code>.
	 */
	final GroupAddress group() {
		GroupAddress ga = getGroup();
		return (ga==null) ? createGroup() : ga;
	}

	/**
	 * Ramdomly gets the address of an already existing group implementing this
	 * organization if any, or creates a new one.
	 * 
	 * @param groupName is the name of the group, used only when creating a new group.
	 * @return the address of the group, never <code>null</code>.
	 * @since 0.4
	 */
	final GroupAddress group(String groupName) {
		GroupAddress ga = getGroup();
		return (ga==null) ? createGroup(groupName) : ga;
	}
	
	
	/**
	 * Gets the address of an already existing group implementing this
	 * organization and with the given id if any, or if none creates a new one
	 * with the given parameters.
	 * 
	 * @param id is the identifier of the group to retreive or of the newly created group.
	 * @param obtainConditions are the obtain conditions to pass to the newly created group.
	 * @param leaveConditions are the leave conditions to pass to the newly created group.
	 * @param membership is the membership descriptor to pass to the newly created group.
	 * @param distributed indicates if the newly created group is marked as distributed or not. 
	 * @param persistent indicates if the newly created group is marked as persistent or not. 
	 * @param groupName is the name associated to the newly created group. 
	 * @return the address of the group, never <code>null</code>.
	 * @since 0.4
	 */
	final GroupAddress group(UUID id,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership,
			boolean distributed,
			boolean persistent,
			String groupName) {
		GroupAddress ga = getGroup(id,groupName);
		return (ga==null) ? createGroup(id,obtainConditions,leaveConditions,membership,distributed,persistent,groupName) : ga;
	}

	/**
	 * Creates a new group, even if groups already exist.
	 * 
	 * @return the address of the group, never <code>null</code>.
	 */
	final GroupAddress createGroup() {
		JanusProperties props = getCRIOContext().getProperties();
		assert(props!=null);
		return createGroup(
					null, // no obtain condition
					null, // no leave condition
					null, // no membership service
					props.getBoolean(JanusProperty.GROUP_DISTRIBUTION),
					props.getBoolean(JanusProperty.GROUP_PERSISTENCE),
					null);
	}
	
	/**
	 * Creates a new group, even if groups already exist.
	 * 
	 * @param groupName is the name of the group.
	 * @return the address of the group, never <code>null</code>.
	 * @since 0.4
	 */
	final GroupAddress createGroup(String groupName) {
		JanusProperties props = getCRIOContext().getProperties();
		assert(props!=null);
		return createGroup(
					null, // no obtain condition
					null, // no leave condition
					null, // no membership service
					props.getBoolean(JanusProperty.GROUP_DISTRIBUTION),
					props.getBoolean(JanusProperty.GROUP_PERSISTENCE),
					groupName);
	}

	/**
	 * Creates a new group, even if groups already exist.
	 *
	 * @param obtainConditions are the obtain condition for the new group.
	 * @param leaveConditions are the leave condition for the new group.
	 * @return the address of the group, never <code>null</code>.
	 */
	final GroupAddress createGroup(
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		JanusProperties props = getCRIOContext().getProperties();
		assert(props!=null);
		return createGroup(
					obtainConditions,
					leaveConditions,
					null, // no membership service
					props.getBoolean(JanusProperty.GROUP_DISTRIBUTION),
					props.getBoolean(JanusProperty.GROUP_PERSISTENCE),
					null);
	}
	
	/**
	 * Creates a new group, even if groups already exist.
	 *
	 * @param obtainConditions are the obtain condition for the new group.
	 * @param leaveConditions are the leave condition for the new group.
	 * @param groupName is the name of the group.
	 * @return the address of the group, never <code>null</code>.
	 * @since 0.4
	 */
	final GroupAddress createGroup(
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			String groupName) {
		JanusProperties props = getCRIOContext().getProperties();
		assert(props!=null);
		return createGroup(
					obtainConditions,
					leaveConditions,
					null, // no membership service
					props.getBoolean(JanusProperty.GROUP_DISTRIBUTION),
					props.getBoolean(JanusProperty.GROUP_PERSISTENCE),
					groupName);
	}

	/**
	 * Creates a new group, even if groups already exist.
	 *
	 * @param obtainConditions are the obtain condition for the new group.
	 * @param leaveConditions are the leave condition for the new group.
	 * @param membership is the membership service to use in this group. Use null to
	 *   obtain an open group.
	 * @param distributed whether the group is distributed over the network or not
	 * @param persistent whether the group is persistent when no more role is playing inside
	 * @return the address of the group, never <code>null</code>.
	 */
	final GroupAddress createGroup(
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership,
			boolean distributed,
			boolean persistent) {
		return createGroup(null,
				obtainConditions,
				leaveConditions,
				membership,
				distributed,
				persistent,
				null);
	}

	/**
	 * Creates a new group, even if groups already exist.
	 *
	 * @param obtainConditions are the obtain condition for the new group.
	 * @param leaveConditions are the leave condition for the new group.
	 * @param membership is the membership service to use in this group. Use null to
	 *   obtain an open group.
	 * @param distributed whether the group is distributed over the network or not
	 * @param persistent whether the group is persistent when no more role is playing inside
	 * @param groupName is the name of the group.
	 * @return the address of the group, never <code>null</code>.
	 * @since 0.4
	 */
	final GroupAddress createGroup(
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership,
			boolean distributed,
			boolean persistent,
			String groupName) {
		return createGroup(null,
				obtainConditions,
				leaveConditions,
				membership,
				distributed,
				persistent,
				groupName);
	}
	
	/**
	 * Creates a new group, even if groups already exist.
	 *
	 * @param id the desired id for the group.
	 * @param obtainConditions are the obtain condition for the new group.
	 * @param leaveConditions are the leave condition for the new group.
	 * @param membership is the membership service to use in this group. Use null to
	 *   obtain an open group.
	 * @param distributed whether the group is distributed over the network or not
	 * @param persistent whether the group is persistent when no more role is playing inside
	 * @return the address of the group, never <code>null</code>.
	 */
	final synchronized GroupAddress createGroup(UUID id,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership,
			boolean distributed,
			boolean persistent) {
		return createGroup(id,
				obtainConditions, leaveConditions,
				membership, distributed, persistent,
				null);
	}

	/**
	 * Creates a new group, even if groups already exist.
	 *
	 * @param id the desired id for the group.
	 * @param obtainConditions are the obtain condition for the new group.
	 * @param leaveConditions are the leave condition for the new group.
	 * @param membership is the membership service to use in this group. Use null to
	 *   obtain an open group.
	 * @param distributed whether the group is distributed over the network or not
	 * @param persistent whether the group is persistent when no more role is playing inside
	 * @param groupName is the name of the group.
	 * @return the address of the group, never <code>null</code>.
	 * @since 0.4
	 */
	final synchronized GroupAddress createGroup(UUID id,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership,
			boolean distributed,
			boolean persistent,
			String groupName) {
		checkSingleton();
		Collection<? extends GroupCondition> oc = obtainConditions;
		if (oc==null || oc.isEmpty()) {
			oc = new ArrayList<GroupCondition>(getObtainConditions());
		}
		Collection<? extends GroupCondition> lc = leaveConditions;
		if (lc==null || lc.isEmpty()) {
			lc = new ArrayList<GroupCondition>(getLeaveConditions());
		}
		GroupAddress ga = this.crioContext.get().getGroupRepository().newGroup(
					id,
					this,
					oc,
					lc,
					membership,
					distributed,
					persistent,
					groupName);
		if (ga!=null) {
			this.groups.add(ga);
		}
		return ga;
	}

	/** Remove a group for this organization.
	 * 
	 * @param group
	 */
	synchronized void removeGroup(GroupAddress group) {
		checkSingleton();
		this.groups.remove(group);
	}

	/**
	 * Ramdomly gets the address of an already existing group implementing this
	 * organization if any. Never creates a new one.
	 * 
	 * @return the address of the group, or <code>null</code> of no group.
	 */
	final synchronized GroupAddress getGroup() {
		checkSingleton();
		int size = this.groups.size();
		if (size>0) {
			return this.groups.first();//we return the first one			
		}
		return null;
	}
	
	
	/**
	 * Try to get the address of an already existing group implementing this
	 * organization and having the specific UUID if any. Never creates a new one.
	 * 
	 * @param groupId is the identifier of the group to retreive.
	 * @param groupName is the name of the group if any. This name is not used to match
	 * any existing group.
	 * @return the address of the group having the specified id if it exists, <code>null</code> otherwise.
	 * @since 0.4
	 */
	final synchronized GroupAddress getGroup(UUID groupId, String groupName) {
		checkSingleton();
		GroupAddress newGroupAdress = new GroupAddress(groupId, getClass(), groupName);
		if (this.groups.contains(newGroupAdress)) {			
			return newGroupAdress;
		}
		return null;
	}

	/**
	 * List known groups.
	 * 
	 * @return all known groups.
	 */
	final synchronized List<GroupAddress> getGroups(){
		checkSingleton();
		return new ArrayList<GroupAddress>(this.groups); 
	}
	/**
	 * Replies if this organization is implementing by a group.
	 * 
	 * @return <code>true</code> if a group exists, otherwise
	 * <code>false</code>
	 */
	public synchronized final boolean hasGroup() {
		checkSingleton();
		return !this.groups.isEmpty();
	}

	/**
	 * Returns true if the specified group belongs to the set of groups
	 * implementing this organization
	 * 
	 * @param group -
	 *            the address of the group to test
	 * @return Returns true if the specified group belongs to the set of groups
	 *         implementing this organization
	 */
	public synchronized final boolean isGroup(GroupAddress group) {
		checkSingleton();
		return this.groups.contains(group);
	}

	/**
	 * Returns the number of groups which implements this organization
	 * 
	 * @return the number of groups which implements this organization
	 */
	public synchronized final int getGroupCount() {
		checkSingleton();
		return this.groups.size();
	}

	/**
	 * Returns the addresses of all the groups implementing this organization
	 * 
	 * @return the addresses of groups implementing this organization
	 */
	public synchronized final Iterator<GroupAddress> getGroupAddresses() {
		checkSingleton();
		return new ArrayList<GroupAddress>(this.groups).iterator();
	}

	/**
	 * ****************** Conditions
	 * *****************************************
	 */

	/**
	 * Appends the specified condition to the end of the initial obtain conditions
	 * for a group.
	 * 
	 * @param c - the condition to add to the obtain conditions
	 * @return <tt>true</tt> if the obtain conditions changed as a result of the
	 *         call
	 * @since 0.5
	 */
	protected boolean addObtainCondition(GroupCondition c) {
		if (c==null) return false;
		if (this.groupObtainConditions==null)
			this.groupObtainConditions = new LinkedList<GroupCondition>();
		return this.groupObtainConditions.add(c);
	}


	/**
	 * Appends the specified condition to the end of the initial leave conditions
	 * for a group.
	 * @param c - the condition to add to the leave conditions
	 * @return <tt>true</tt> if the leave conditions changed as a result of the
	 *         call
	 * @since 0.5
	 */
	protected boolean addLeaveCondition(GroupCondition c) {
		if (c==null) return false;
		if (this.groupLeaveConditions==null)
			this.groupLeaveConditions = new LinkedList<GroupCondition>();
		return this.groupLeaveConditions.add(c);
	}

	/**
	 * Set the initial leave conditions for a group. 
	 * @param c - leave conditions
	 * @since 0.5
	 */
	protected void setLeaveCondition(Collection<? extends GroupCondition> c) {
		if (c==null || c.isEmpty()) {
			this.groupLeaveConditions = null;		
		}
		else {
			if (this.groupLeaveConditions==null)
				this.groupLeaveConditions = new LinkedList<GroupCondition>();
			this.groupLeaveConditions.addAll(c);
		}
	}

	/**
	 * Set the initial obtains conditions for a group.
	 * @param c - obtain conditions
	 * @since 0.5
	 */
	protected void setObtainCondition(Collection<? extends GroupCondition> c) {
		if (c==null || c.isEmpty()) {
			this.groupObtainConditions = null;		
		}
		else {
			if (this.groupObtainConditions==null)
				this.groupObtainConditions = new LinkedList<GroupCondition>();
			this.groupObtainConditions.addAll(c);
		}
	}

	/**
	 * Remove the specified condition from the initial obtain conditions for a group.
	 * 
	 * @param c - the condition to remove from the obtain conditions.
	 * @return <tt>true</tt> if the obtain conditions changed as a result of the
	 *         call
	 * @since 0.5
	 */
	protected boolean removeObtainCondition(Condition<?> c) {
		if (c!=null && this.groupObtainConditions!=null) {
			if (this.groupObtainConditions.remove(c)) {
				if (this.groupObtainConditions.isEmpty())
					this.groupObtainConditions = null;
				return true;
			}
		}
		return false;
	}


	/**
	 * Remove the specified condition from the initial leave conditions for a group.
	 * @param c - the condition to remove from the leave conditions
	 * @return <tt>true</tt> if the leave conditions changed as a result of the
	 *         call
	 * @since 0.5
	 */
	protected boolean removeLeaveCondition(Condition<?> c) {
		if (c!=null && this.groupLeaveConditions!=null) {
			if (this.groupLeaveConditions.remove(c)) {
				if (this.groupLeaveConditions.isEmpty())
					this.groupLeaveConditions = null;
				return true;
			}
		}
		return false;
	}
	
	/** Replies the initial obtain conditions for a group.
	 * 
	 * @return the initial obtain conditions for a group.
	 * @since 0.5
	 */
	public Collection<GroupCondition> getObtainConditions() {
		if (this.groupObtainConditions==null) return Collections.emptyList();
		return Collections.unmodifiableCollection(this.groupObtainConditions);
	}
	
	/** Replies the initial leave conditions for a group.
	 * 
	 * @return the initial leave conditions for a group.
	 * @since 0.5
	 */
	public Collection<GroupCondition> getLeaveConditions() {
		if (this.groupLeaveConditions==null) return Collections.emptyList();
		return Collections.unmodifiableCollection(this.groupLeaveConditions);
	}

	/**
	 * ****************** Logging API
	 * *****************************************
	 */

	/** Replies the logger associated to this organization.
	 * 
	 * @return the logger associated to this organization.
	 */
	protected Logger getLogger() {
		Logger logger = this.logger==null ? null : this.logger.get();
		if (logger==null) {
			logger = LoggerUtil.createOrganizationLogger(getClass(), getCRIOContext().getTimeManager());
			this.logger = new SoftReference<Logger>(logger);
		}
		return logger;
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private static class RoleIterator implements Iterator<Class<? extends Role>> {

		private final Class<? extends Role> type;
		private final Iterator<Class<? extends Role>> iterator;
		
		private Class<? extends Role> next;
		
		/**
		 * @param type
		 * @param iterator
		 */
		public RoleIterator(Class<? extends Role> type, Iterator<Class<? extends Role>> iterator) {
			this.type = type;
			this.iterator = iterator;
			searchNext();
		}
		
		private void searchNext() {
			Class<? extends Role> n;
			this.next = null;
			while (this.next==null && this.iterator.hasNext()) {
				n = this.iterator.next();
				if (this.type.isAssignableFrom(n)) {
					this.next = n;
				}
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
		public Class<? extends Role> next() {
			Class<? extends Role> n = this.next;
			if (n==null) throw new NoSuchElementException();
			searchNext();
			return n;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	} // class RoleIterator
	
}