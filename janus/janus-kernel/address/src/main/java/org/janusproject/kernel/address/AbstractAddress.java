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
package org.janusproject.kernel.address;

import java.util.UUID;

/** This abstract class describes all the addresses used by the kernel
 * to identify its elements
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see AgentAddress
 */
public abstract class AbstractAddress implements Address {

	private static final long serialVersionUID = 1519844913685586094L;
	
	/**
	 * The global unique identifier of this kernel
	 */
	private final UUID id;

	/**
	 * Create an address based on the specified identifier
	 * 
	 * @param id is the identifier of the address.
	 */
	protected AbstractAddress(UUID id) {
		if (id==null)
			this.id = UUID.randomUUID();
		else
			this.id = id;
	}

	/**
	 * Create an address with an random identifier.
	 */
	protected AbstractAddress() {
		this(null);
	}

	/** {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	/** Replies the identifier associated to this address.
	 * 
	 * @return the identifier associated to this address.
	 * @since 0.5
	 */
	@Override
	public UUID getUUID() {
		return this.id;
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
		return address!=null 
				&& this.id.equals(address.getUUID());
	}
	
	/** Test if this address and the given one are equal.
	 * 
	 * @param uid is the address to be compared.
	 * @return <code>true</code> if this address and the given one are
	 * equal, otherwise <code>false</code>
	 */
	public boolean equals(UUID uid) {
		return uid!=null 
				&& this.id.equals(uid);
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
		if (address==null) return 1;
		return this.id.compareTo(address.getUUID());
	}
	
}