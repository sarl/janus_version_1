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


/** 2D Vector with 2 floating-point values.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Vector2f extends Tuple2f<Vector2f> implements Vector<Tuple2f<Vector2f>,Vector2f> {

	private static final long serialVersionUID = -2062941509400877679L;

	/**
	 */
	public Vector2f() {
		//
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Vector2f(Tuple2f<?> tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Vector2f(Tuple2i<?> tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Vector2f(int[] tuple) {
		super(tuple);
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Vector2f(float[] tuple) {
		super(tuple);
	}

	/**
	 * @param x
	 * @param y
	 */
	public Vector2f(int x, int y) {
		super(x,y);
	}

	/**
	 * @param x
	 * @param y
	 */
	public Vector2f(float x, float y) {
		super(x,y);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Vector2f clone() {
		return super.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float angle(Vector2f v1) {
		double vDot = dot(v1) / ( length()*v1.length() );
		if( vDot < -1.) vDot = -1.;
		if( vDot >  1.) vDot =  1.;
		return((float) (Math.acos( vDot )));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float dot(Vector2f v1) {
	      return (this.x*v1.x + this.y*v1.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float length() {
        return (float) Math.sqrt(this.x*this.x + this.y*this.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float lengthSquared() {
        return (this.x*this.x + this.y*this.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void normalize(Vector2f v1) {
        float norm;
        norm = (float) (1./Math.sqrt(v1.x*v1.x + v1.y*v1.y));
        this.x = (int)(v1.x*norm);
        this.y = (int)(v1.y*norm);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void normalize() {
        float norm;
        norm = (float)(1./Math.sqrt(this.x*this.x + this.y*this.y));
        this.x *= norm;
        this.y *= norm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float signedAngle(Vector2f v) {
		return MathUtil.signedAngle(this, v);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void turnVector(float angle) {
		MathUtil.turnVector(this, angle);
	}

}