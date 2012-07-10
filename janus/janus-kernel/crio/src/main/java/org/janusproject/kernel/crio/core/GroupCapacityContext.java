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

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityCaller;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;


/**
 * This class stores the informations relative to a call
 * to a capacity implementation.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class GroupCapacityContext 
extends CapacityContext {

	private final WeakReference<KernelScopeGroup> group;
	private final WeakReference<Role> role;
	
	/**
	 * Builds a new call.
	 * @param caller is the caller of this capacity.
	 * @param group is the interactional context of the capacity.
	 * @param role is the role which has invoked this capacity.
	 * @param invokedCapacity is the invoked capacity in this context.
	 * @param type is the type of the invoked capacity implementation.
	 * @param input are the input data required to execute this capacity.
	 */
	public GroupCapacityContext(
			CapacityCaller caller,
			KernelScopeGroup group,
			Role role,
			Class<? extends Capacity> invokedCapacity,
			CapacityImplementationType type,
			Object... input) {
		super(caller, invokedCapacity, type, input);
		this.group = new WeakReference<KernelScopeGroup>(group);
		this.role = new WeakReference<Role>(role);
	}

	/**
	 * Builds a new call
	 * @param caller is the caller of this capacity
	 * @param group is the interactional context of the capacity.
	 * @param role is the role which has invoked this capacity.
	 * @param invokedCapacity is the invoked capacity in this context.
	 * @param type is the type of the invoked capacity implementation.
	 * @param identifier is the unique identifier of the call.
	 * @param input are the input data required to execute this capacity
	 */
	public GroupCapacityContext(
			CapacityCaller caller, 
			KernelScopeGroup group,
			Role role,
			Class<? extends Capacity> invokedCapacity,
			CapacityImplementationType type,
			UUID identifier,
			Object... input) {
		super(caller, invokedCapacity, type, identifier, input);
		this.group = new WeakReference<KernelScopeGroup>(group);
		this.role = new WeakReference<Role>(role);
	}
	
	/** Replies the group in which this capacity invocation lies.
	 * 
	 * @return the group.
	 */
	public KernelScopeGroup getGroup() {
		return this.group.get();
	}

	/** Replies the address of the group in which this capacity invocation lies.
	 * 
	 * @return the group.
	 * @since 0.5
	 */
	public GroupAddress getGroupAddress() {
		KernelScopeGroup g = getGroup();
		assert(g!=null);
		return g.getAddress();
	}

	/** Replies the role which has call the capacity.
	 * 
	 * @return the role.
	 */
	public Role getRole() {
		return this.role.get();
	}

	/** Replies the address of the role which has call the capacity.
	 * 
	 * @return the address of the role.
	 * @since 0.5
	 */
	public RoleAddress getRoleAddress() {
		Role r = getRole();
		if (r==null) return null;
		return r.getAddress();
	}

	/** Replies the type of the role which has call the capacity.
	 * 
	 * @return the role type.
	 */
	public Class<? extends Role> getRoleType() {
		Role r = this.role.get();
		return (r==null) ? null : r.getClass();
	}

}
