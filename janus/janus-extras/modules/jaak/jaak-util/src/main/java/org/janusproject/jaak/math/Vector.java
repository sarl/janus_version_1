/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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
package org.janusproject.jaak.math;

/** 2D Vector.
 * 
 * @param <PT> is parent type of the implementation type of the tuple.
 * @param <T> is the implementation type of the tuple.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Vector<PT extends Tuple<PT,?>, T extends PT> extends Tuple<PT,T> {

	/**
	 * Computes the dot product of the this vector and vector v1.
	 * @param v1 the other vector
	 * @return the dot product.
	 */
	public float dot(T v1);

	/**  
	 * Returns the length of this vector.
	 * @return the length of this vector
	 */  
	public float length();

	/**  
	 * Returns the squared length of this vector.
	 * @return the squared length of this vector
	 */  
	public float lengthSquared();

	/**
	 * Sets the value of this vector to the normalization of vector v1.
	 * @param v1 the un-normalized vector
	 */  
	public void normalize(T v1);

	/**
	 * Normalizes this vector in place.
	 */  
	public void normalize();


	/**
	 *   Returns the angle in radians between this vector and the vector
	 *   parameter; the return value is constrained to the range [0,PI].
	 *   @param v1    the other vector
	 *   @return   the angle in radians in the range [0,PI]
	 */
	public float angle(T v1);

	/** Compute a signed angle between this vector and the given vector.
	 * <p>
	 * The signed angle between this vector and {@code v}
	 * is the rotation angle to apply to this vector
	 * to be colinear to {@code v} and pointing the
	 * same demi-plane. It means that the angle replied
	 * by this function is be negative if the rotation
	 * to apply is clockwise, and positive if
	 * the rotation is counterclockwise.
	 * <p>
	 * The value replied by {@link #angle(Tuple)}
	 * is the absolute value of the vlaue replied by this
	 * function. 
	 * 
	 * @param v is the vector to reach.
	 * @return the rotation angle to turn this vector to reach
	 * {@code v}.
	 */
	public float signedAngle(T v);

	/** Turn this vector about the given rotation angle.
	 * 
	 * @param angle is the rotation angle in radians.
	 */
	public void turnVector(float angle);

}