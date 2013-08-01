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

import org.janusproject.kernel.address.AbstractAddress;

/**
 * This class assures the identification a group, unique within a network under
 * conditions precising the unicity of a <code>GUID</code>.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class GroupAddress extends AbstractAddress {

	private static final long serialVersionUID = 8125611673846747544L;

	private String name = null;

	private String description = null;

	private Class<? extends Organization> organization;
	
	private transient WeakReference<KernelScopeGroup> group;

	/**
	 * Create a new GroupAddress and add the address of the current kernel to
	 * the list of its hosts.
	 *
	 * @param id is the identifier to use to build the address.
	 * @param organization is the organization associated to this address.
	 */
	GroupAddress(UUID id, Class<? extends Organization> organization) {
		super(id);
		this.organization = organization;
		this.group = null;
	}

	/**
	 * Create a new GroupAddress and add the address of the current kernel to
	 * the list of its hosts.
	 *
	 * @param id is the identifier to use to build the address.
	 * @param organization is the organization associated to this address.
	 * @param name is the name of the address
	 * @since 0.4
	 */
	GroupAddress(UUID id, Class<? extends Organization> organization, String name) {
		super(id);
		this.organization = organization;
		this.name = name;
		this.group = null;
	}

	/**
	 * @param id is the identifier to use to build the address.
	 * @param organization is the organization associated to this address.
	 * @param group is the private instance of the group identifier by this address.
	 * @param name is the name of the address
	 * @param description is the description of the address.
	 */
	GroupAddress(UUID id,
			Class<? extends Organization> organization, 
			KernelScopeGroup group,
			String name,
			String description) {
		super(id);
		this.organization = organization;
		this.name = name;
		this.description = description;
		if (group==null)
			this.group = null;
		else
			this.group = new WeakReference<KernelScopeGroup>(group);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final String toString() {
		StringBuilder b = new StringBuilder();
		String n = getName();
		if (n!=null && !n.isEmpty()) {
			b.append(n);
		}
		b.append("::"); //$NON-NLS-1$
		b.append(getUUID());
		b.append("::"); //$NON-NLS-1$
		b.append(getOrganization().toString());
		return b.toString();
	}

	/** Replies the organization instancied by this group.
	 * 
	 * @return the organization associated to this address.
	 */
	public Class<? extends Organization> getOrganization() {
		return this.organization;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		String n = getName();
		if ((n==null && name!=null) || (n!=null && !n.equals(name))) {
			this.name = name;
			KernelScopeGroup group = getGroup();
			if (group!=null) {
				group.setPublicUserData("name", name); //$NON-NLS-1$
			}
		}
	}
	
	/** Remove the link between this address and its group.
	 */
	synchronized void unbind() {
		this.group = null;
	}
	
	/** Create the link between this address and its group.
	 * 
	 * @param group is the group that is identifier by this address.
	 */
	synchronized void bind(KernelScopeGroup group) {
		if (group==null)
			this.group = null;
		else
			this.group = new WeakReference<KernelScopeGroup>(group);
	}

	/** Replies the group associated to this address.
	 * 
	 * @return the group associated to this address.
	 */
	synchronized KernelScopeGroup getGroup() {
		if (this.group==null) return null;
		return this.group.get();
	}

}