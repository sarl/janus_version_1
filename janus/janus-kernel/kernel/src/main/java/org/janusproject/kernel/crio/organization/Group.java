/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.kernel.crio.organization;

import java.util.Collection;

import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.role.RolePlayingListener;

/**
 * Public description of a group.
 * This public description may be used to obtain
 * information about the group.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.4
 */
public interface Group {

	/** Replies the membership for the group.
	 * 
	 * @return the membership for the group.
	 */
	public MembershipService getMembership();
	
	/** Replies all the obtain conditions.
	 * 
	 * @return the obtain conditions.
	 */
	public Collection<GroupCondition> getObtainConditions();
	
	/** Replies all the leave conditions.
	 * 
	 * @return the leave conditions.
	 */
	public Collection<GroupCondition> getLeaveConditions();
		
	/**
	 * Replies the address of the group.
	 * 
	 * @return the address of the group.
	 */
	public GroupAddress getAddress();

	/**
	 * Replies the implemented organization.
	 * 
	 * @return the implemented organization.
	 */
	public Organization getOrganization();

	/**
	 * Replies if this group is able to be distributed among several kernels.
	 * 
	 * @return <code>true</code> if distribution is allowed, otherwise
	 *         <code>false</code>
	 */
	public boolean isDistributed();

	/**
	 * Replies if this group is persistent when no role player is playing a role
	 * inside.
	 * 
	 * @return <code>true</code> if the group is persistent, otherwise
	 *         <code>false</code>
	 */
	public boolean isPersistent();
	
	/** Replies any user data associated to the group
	 * but with only an access from the inside of the group.
	 * Only the members of the group may obtain a
	 * value different than <code>null</code>.
	 * 
	 * @return any user data associated to the group;
	 * or <code>null</code> if the caller is not a member
	 * of the group.
	 * @since 0.5
	 */
	public Object getPrivateUserData();
	
	/** Set any user data associated to the group
	 * but with only an access from the inside of the group.
	 * Only the members of the group may set this
	 * value.
	 * 
	 * @param userData is any user data associated to the group.
	 * @return the value of the user data previously stored in the group.
	 * @since 0.5
	 */
	public Object setPrivateUserData(Object userData);
	
	/** Replies any user data associated to the group.
	 * Only the members of the group may obtain a
	 * value different than <code>null</code>.
	 * 
	 * @param key is the name of the user data.
	 * @return any user data associated to the group;
	 * or <code>null</code> if the caller is not a member
	 * of the group.
	 * @since 0.5
	 */
	public Object getPublicUserData(String key);
	
	/** Set any user data associated to the group.
	 * Only the members of the group may set this
	 * value.
	 * 
	 * @param key is the name of the user data.
	 * @param userData is any user data associated to the group.
	 * @return the value of the user data previously stored in the group.
	 * @since 0.5
	 */
	public Object setPublicUserData(String key, Object userData);

	/**
	 * Add listener on role playing events in the group only.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	public void addRolePlayingListener(RolePlayingListener listener);

	/**
	 * Remove listener on role playing events in the group only.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	public void removeRolePlayingListener(RolePlayingListener listener);

	/**
	 * Returns the number of players for the given role in the group.
	 * 
	 * @param role
	 * @return the number of players for the given role in the group.
	 * @since 0.5
	 */
	public int getPlayerCount(Class<? extends Role> role);

	/**
	 * Rpelies if the given role is currently played.
	 * 
	 * @param role
	 * @return <code>true</code> if the role is played; <code>false</code> otherwise.
	 * @since 0.5
	 */
	public boolean isPlayedRole(Class<? extends Role> role);

	/**
	 * Returns the number of players in the group.
	 * 
	 * @return the number of players in the group.
	 * @since 0.5
	 */
	public int getPlayerCount();
	
}