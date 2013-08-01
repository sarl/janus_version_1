/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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

import java.util.Collection;
import java.util.UUID;

import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.repository.ConcurrentHashRepository;

/**
 * This registry stores a reference to each group of the kernel
 * and their respective address: Groups White Pages.
 * <p>
 * This class is a singleton.
 * On a given JVM, there is only one kernel and one GroupRepository.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
final class GroupRepository
extends ConcurrentHashRepository<GroupAddress, KernelScopeGroup> {

	/**
	 * Avoid public construction
	 */
	public GroupRepository() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public KernelScopeGroup get(GroupAddress id) {
		assert(id!=null);
		KernelScopeGroup grp = id.getGroup();
		if (grp!=null) return grp;
		grp = super.get(id);
		if (grp!=null) {
			id.bind(grp);
		}
		return grp;
	}

	/**
	 * Create a new <b>Group</b> with a specific that implements this organization with the
	 * specified set of conditions to access and leave this group.
	 * 
	 * @param id the desired group id
	 * @param organization is the implemented organization.
	 * @param obtainConditions is the set of conditions to access to this group
	 * @param leaveConditions is the set of conditions to leave this group
	 * @param membership is the membership service to use in this group. Use null to
	 *   obtain an open group.
	 * @param distributed whether the group is distributed over the network or not
	 * @param persistent whether the group is persistent when no more role is playing inside
	 * @param groupName is the name of the group. This name could be not unique among all the group addresses.
	 * @return the address of the new group freshly created
	 */
	public GroupAddress newGroup(
			UUID id,
			Organization organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership,
			boolean distributed,
			boolean persistent,
			String groupName) {
		GroupAddress adr = new GroupAddress(id, organization.getClass(), groupName);
		KernelScopeGroup grp = new KernelScopeGroup(
				organization,
				adr,
				distributed,
				persistent,
				membership);
		adr.bind(grp);
		add(adr, grp);
		grp.setObtainLeaveConditions(obtainConditions, leaveConditions);
		return adr;
	}
	
	/**
	 * Replies if the a group instance of <code>organization</code> exists in the repository.
	 * 
	 * @param id The id of the group
	 * @param organization The organization of this group
	 * @return true if the repository contains the group of this type, false otherwise.
	 */
	public GroupAddress containsGroup(UUID id, Class<? extends Organization>  organization){
		KernelScopeGroup g = get(new GroupAddress(id, organization));
		return (g == null? null : g.getAddress()); 
	}
	
	/** Remove a registered group.
	 * 
	 * @param groupAddress is the group address.
	 */
	public void removeGroup(GroupAddress groupAddress) {
		assert(groupAddress!=null);
		KernelScopeGroup grp = remove(groupAddress);
		if (grp!=null) grp.getOrganization().removeGroup(groupAddress);
		groupAddress.unbind();
	}

}
