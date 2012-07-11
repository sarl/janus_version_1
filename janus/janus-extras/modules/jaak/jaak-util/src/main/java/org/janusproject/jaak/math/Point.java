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

/** 2D Point.
 * 
 * @param <PT> is parent type of the implementation type of the tuple.
 * @param <T> is the implementation type of the tuple.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Point<PT extends Tuple<PT,?>, T extends PT> extends Tuple<PT,T> {

	/**
	 * Computes the square of the distance between this point and point p1.
	 * @param p1 the other point
	 * @return the distance.
	 */
	public int distanceSquared(T p1);

	/**
	 * Computes the square of the distance between this point and point p1.
	 * @param p1 the other point
	 * @return the distance.
	 */
	public float getDistanceSquared(T p1);

	/**
	 * Computes the distance between this point and point p1.
	 * @param p1 the other point
	 * @return the distance. 
	 */    
	public int distance(T p1);

	/**
	 * Computes the distance between this point and point p1.
	 * @param p1 the other point
	 * @return the distance. 
	 */    
	public float getDistance(T p1);

	/**
	 * Computes the L-1 (Manhattan) distance between this point and
	 * point p1.  The L-1 distance is equal to abs(x1-x2) + abs(y1-y2).
	 * @param p1 the other point
	 * @return the distance.
	 */
	public int distanceL1(T p1);

	/**
	 * Computes the L-1 (Manhattan) distance between this point and
	 * point p1.  The L-1 distance is equal to abs(x1-x2) + abs(y1-y2).
	 * @param p1 the other point
	 * @return the distance.
	 */
	public float getDistanceL1(T p1);

	/**
	 * Computes the L-infinite distance between this point and
	 * point p1.  The L-infinite distance is equal to 
	 * MAX[abs(x1-x2), abs(y1-y2)]. 
	 * @param p1 the other point
	 * @return the distance.
	 */
	public int distanceLinf(T p1);

	/**
	 * Computes the L-infinite distance between this point and
	 * point p1.  The L-infinite distance is equal to 
	 * MAX[abs(x1-x2), abs(y1-y2)]. 
	 * @param p1 the other point
	 * @return the distance.
	 */
	public float getDistanceLinf(T p1);

}