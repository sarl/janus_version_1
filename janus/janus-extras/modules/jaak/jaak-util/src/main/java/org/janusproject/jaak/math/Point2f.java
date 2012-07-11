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

/** 2D Point with 2 floating-point numbers.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Point2f extends Tuple2f<Point2f> implements Point<Tuple2f<Point2f>,Point2f> {

	private static final long serialVersionUID = 8963319137253544821L;

	/**
	 */
	public Point2f() {
		//
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Point2f(Tuple2f<?> tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Point2f(Tuple2i<?> tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Point2f(int[] tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Point2f(float[] tuple) {
		super(tuple);
	}

	/**
	 * @param x
	 * @param y
	 */
	public Point2f(int x, int y) {
		super(x,y);
	}

	/**
	 * @param x
	 * @param y
	 */
	public Point2f(float x, float y) {
		super(x,y);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2f clone() {
		return super.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int distanceSquared(Point2f p1) {
	      float dx, dy;
	      dx = this.x-p1.x;  
	      dy = this.y-p1.y;
	      return (int)(dx*dx+dy*dy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDistanceSquared(Point2f p1) {
	      float dx, dy;
	      dx = this.x-p1.x;  
	      dy = this.y-p1.y;
	      return dx*dx+dy*dy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int distance(Point2f p1) {
	      float  dx, dy;
	      dx = this.x-p1.x;  
	      dy = this.y-p1.y;
	      return (int)Math.sqrt(dx*dx+dy*dy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDistance(Point2f p1) {
	      float  dx, dy;
	      dx = this.x-p1.x;  
	      dy = this.y-p1.y;
	      return (float)Math.sqrt(dx*dx+dy*dy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int distanceL1(Point2f p1) {
	      return (int)(Math.abs(this.x-p1.x) + Math.abs(this.y-p1.y));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDistanceL1(Point2f p1) {
	      return Math.abs(this.x-p1.x) + Math.abs(this.y-p1.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int distanceLinf(Point2f p1) {
	      return (int)Math.max( Math.abs(this.x-p1.x), Math.abs(this.y-p1.y));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDistanceLinf(Point2f p1) {
	      return Math.max( Math.abs(this.x-p1.x), Math.abs(this.y-p1.y));
	}

}