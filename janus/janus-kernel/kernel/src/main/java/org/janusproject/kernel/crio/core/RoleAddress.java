/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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

import org.arakhne.afc.vmutil.ClassComparator;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;

/**
 * This class assures the identification a role inside a group,
 * unique within a network under
 * conditions precising the unicity of a <code>GUID</code>.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class RoleAddress implements Address {

	private static final long serialVersionUID = 3259345815785534277L;

	private String name = null;

	private String description = null;

	private final GroupAddress group;
	private final Class<? extends Role> roleType;
	private AgentAddress player;
	
	private transient WeakReference<Role> role;

	/**
	 * @param role is the instance of the played role.
	 */
	RoleAddress(Role role) {
		assert(role!=null);
		this.group = role.getGroupAddress();
		this.roleType = role.getClass();
		this.player = role.getPlayer();
		this.role = new WeakReference<Role>(role);
	}

	/**
	 * @param group is the address of the group of the role.
	 * @param role is the role.
	 * @param player is the player of the role.
	 */
	RoleAddress(GroupAddress group, Class<? extends Role> role, AgentAddress player) {
		this(group, role, player, null);
	}

	/**
	 * @param group is the address of the group of the role.
	 * @param role is the role.
	 * @param player is the player of the role.
	 * @param name is the name associated to the address.
	 * @since 1.0
	 */
	RoleAddress(GroupAddress group, Class<? extends Role> role, AgentAddress player, String name) {
		assert(group!=null);
		assert(role!=null);
		this.group = group;
		this.roleType = role;
		this.player = player;
		this.role = null;
		this.name = name;
	}

	/** {@inheritDoc}
	 */
	@Override
	public final String toString() {
		StringBuilder b = new StringBuilder();
		if (this.name!=null && !this.name.isEmpty()) {
			b.append(this.name);
		}
		b.append("||"); //$NON-NLS-1$
		b.append(this.group.toString());
		b.append("||"); //$NON-NLS-1$
		b.append(this.roleType.getSimpleName());
		b.append("||"); //$NON-NLS-1$
		if (this.player!=null) {
			b.append(this.player.toString());
		}
		return b.toString();
	}

	/** Replies the group in which the role with this address is played.
	 * 
	 * @return the group in which the role with this address is played.
	 */
	public GroupAddress getGroup() {
		return this.group;
	}

	/** Replies the type of the role with this address.
	 * 
	 * @return the type of the role with this address.
	 */
	public Class<? extends Role> getRole() {
		return this.roleType;
	}

	/** Replies the player of the role with this address.
	 * 
	 * @return the player of the role with this address.
	 */
	public AgentAddress getPlayer() {
		return this.player;
	}

	/** Set the player of the role with this address.
	 * 
	 * @param player is the player of the role with this address.
	 */
	void setPlayer(AgentAddress player) {
		this.player = player;
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
		this.name = name;
	}
	
	/** Remove the link between this address and its role.
	 */
	synchronized void unbind() {
		this.role = null;
	}
	
	/** Create the link between this address and its role.
	 * 
	 * @param role is the role that is identifier by this address.
	 */
	synchronized void bind(Role role) {
		if (role==null)
			this.role = null;
		else
			this.role = new WeakReference<Role>(role);
	}

	/** Replies the role associated to this address.
	 * 
	 * @return the role associated to this address.
	 */
	synchronized Role getRoleObject() {
		if (this.role==null) return null;
		return this.role.get();
	}

	/** Replies the role associated to this address.
	 * 
	 * @return the role associated to this address.
	 */
	synchronized KernelScopeGroup getGroupObject() {
		if (this.group==null) return null;
		return this.group.getGroup();
	}

	/** {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int h = 1;
		h = h * 31 + this.group.hashCode();
		h = h * 31 + this.roleType.hashCode();
		h = h * 31 + ((this.player==null) ? 0 : this.player.hashCode());
		return h;
	}
	
	/** Replies the identifier associated to this address.
	 * 
	 * @return the identifier associated to this address.
	 */
	@Override
	public UUID getUUID() {
		return UUID.nameUUIDFromBytes(toString().getBytes());
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean equals(Object address) {
		if (address instanceof Address)
			return equals((Address)address);
		if (address instanceof UUID)
			return equals((UUID)address);
		return false;
	}

	/** Test if this address and the given one are equal.
	 * 
	 * @param address is the address to be compared.
	 * @return <code>true</code> if this address and the given one are
	 * equal, otherwise <code>false</code>
	 */
	@Override
	public boolean equals(Address address) {
		if (address instanceof RoleAddress) {
			RoleAddress r = (RoleAddress)address;
			if (this.group.equals(r.group) && this.roleType.equals(r.roleType)) {
				if (this.player==r.player) return true;
				if (this.player!=null && this.player.equals(r.player)) return true;
			}
		}
		return false;
	}
	
	/** Test if this address and the given one are equal.
	 * 
	 * @param uid is the address to be compared.
	 * @return <code>true</code> if this address and the given one are
	 * equal, otherwise <code>false</code>
	 */
	public boolean equals(UUID uid) {
		return uid!=null 
				&& getUUID().equals(uid);
	}

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
	 * @param address is the address to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
	@Override
	public int compareTo(Address address) {
		if (address==null) return Integer.MAX_VALUE;
		if (address instanceof RoleAddress) {
			RoleAddress ra = (RoleAddress)address;
			int cmp = this.group.compareTo(ra.group);
			if (cmp!=0) return cmp;
			cmp = ClassComparator.SINGLETON.compare(this.roleType, ra.roleType);
			if (cmp!=0) return cmp;
			if (this.player!=null) {
				cmp = this.player.compareTo(ra.player);
			}
			else {
				cmp = (ra.player==null) ? 0 : Integer.MAX_VALUE;
			}
			return cmp;
		}
		return System.identityHashCode(this) - System.identityHashCode(address);
	}

}