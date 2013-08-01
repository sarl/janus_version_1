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

/** 2D Point with 2 integers.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Point2i extends Tuple2i<Point2i> implements Point<Tuple2i<Point2i>,Point2i> {

	private static final long serialVersionUID = 6087683508168847436L;

	/**
	 */
	public Point2i() {
		//
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Point2i(Tuple2i<?> tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Point2i(Tuple2f<?> tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Point2i(int[] tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Point2i(float[] tuple) {
		super(tuple);
	}

	/**
	 * @param x
	 * @param y
	 */
	public Point2i(int x, int y) {
		super(x,y);
	}

	/**
	 * @param x
	 * @param y
	 */
	public Point2i(float x, float y) {
		super(x,y);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2i clone() {
		return super.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int distanceSquared(Point2i p1) {
	      float dx, dy;
	      dx = this.x-p1.x;  
	      dy = this.y-p1.y;
	      return (int)(dx*dx+dy*dy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDistanceSquared(Point2i p1) {
	      float dx, dy;
	      dx = this.x-p1.x;  
	      dy = this.y-p1.y;
	      return dx*dx+dy*dy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int distance(Point2i p1) {
	      float  dx, dy;
	      dx = this.x-p1.x;  
	      dy = this.y-p1.y;
	      return (int)Math.sqrt(dx*dx+dy*dy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDistance(Point2i p1) {
	      float  dx, dy;
	      dx = this.x-p1.x;  
	      dy = this.y-p1.y;
	      return (float)Math.sqrt(dx*dx+dy*dy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int distanceL1(Point2i p1) {
	      return Math.abs(this.x-p1.x) + Math.abs(this.y-p1.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDistanceL1(Point2i p1) {
	      return Math.abs(this.x-p1.x) + Math.abs(this.y-p1.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int distanceLinf(Point2i p1) {
	      return Math.max( Math.abs(this.x-p1.x), Math.abs(this.y-p1.y));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getDistanceLinf(Point2i p1) {
	      return Math.max( Math.abs(this.x-p1.x), Math.abs(this.y-p1.y));
	}

}