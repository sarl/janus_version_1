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

/** 2D tuple with 2 floating-point numbers.
 * 
 * @param <T> is the implementation type of the tuple.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Tuple2f<T extends Tuple2f<T>> implements Tuple<Tuple2f<T>,T> {

	private static final long serialVersionUID = 6447733811545555778L;
	
	/** x coordinate.
	 */
	protected float x;

	/** y coordinate.
	 */
	protected float y;

	/**
	 */
	public Tuple2f() {
		this.x = this.y = 0;
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Tuple2f(Tuple2f<?> tuple) {
		this.x = tuple.x;
		this.y = tuple.y;
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Tuple2f(Tuple2i<?> tuple) {
		this.x = tuple.x;
		this.y = tuple.y;
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Tuple2f(int[] tuple) {
		this.x = tuple[0];
		this.y = tuple[1];
	}

	/**
	 * @param tuple is the tuple to copy.
	 */
	public Tuple2f(float[] tuple) {
		this.x = tuple[0];
		this.y = tuple[1];
	}

	/**
	 * @param x
	 * @param y
	 */
	public Tuple2f(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param x
	 * @param y
	 */
	public Tuple2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T clone() {
		try {
			return (T)super.clone();
		}
		catch(CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void absolute() {
		this.x = Math.abs(this.x);
		this.y = Math.abs(this.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void absolute(Tuple2f<T> t) {
		t.x = Math.abs(this.x);
		t.y = Math.abs(this.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Tuple2f<T> t1, Tuple2f<T> t2) {
		this.x = t1.x + t2.x;
		this.y = t1.y + t2.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Tuple2f<T> t1) {
		this.x += t1.x;
		this.y += t1.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int x, int y) {
		this.x += x;
		this.y += y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(float x, float y) {
		this.x += x;
		this.y += y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addX(int x) {
		this.x += x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addX(float x) {
		this.x += x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addY(int y) {
		this.y += y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addY(float y) {
		this.y += y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clamp(int min, int max) {
		if (this.x < min) this.x = min;
		else if (this.x > max) this.x = max;
		if (this.y < min) this.y = min;
		else if (this.y > max) this.y = max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clamp(float min, float max) {
		clamp(min, max);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clampMin(int min) {
		if (this.x < min) this.x = min;
		if (this.y < min) this.y = min;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clampMin(float min) {
		clampMin(min);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clampMax(int max) {
		if (this.x > max) this.x = max;
		if (this.y > max) this.y = max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clampMax(float max) {
		clampMax(max);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clamp(int min, int max, Tuple2f<T> t) {
		if (this.x < min) t.x = min;
		else if (this.x > max) t.x = max;
		if (this.y < min) t.y = min;
		else if (this.y > max) t.y = max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clamp(float min, float max, Tuple2f<T> t) {
		clamp(min, max, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clampMin(int min, Tuple2f<T> t) {
		if (this.x < min) t.x = min;
		if (this.y < min) t.y = min;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clampMin(float min, Tuple2f<T> t) {
		clampMin(min, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clampMax(int max, Tuple2f<T> t) {
		if (this.x > max) t.x = max;
		if (this.y > max) t.y = max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clampMax(float max, Tuple2f<T> t) {
		clampMax(max, t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(Tuple2f<T> t) {
		t.x = this.x;
		t.y = this.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int[] t) {
		t[0] = (int)this.x;
		t[1] = (int)this.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(float[] t) {
		t[0] = this.x;
		t[1] = this.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void negate(Tuple2f<T> t1) {
		this.x = -t1.x;
		this.y = -t1.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void negate() {
		this.x = -this.x;
		this.y = -this.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scale(int s, Tuple2f<T> t1) {
		this.x = s * t1.x;
		this.y = s * t1.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scale(float s, Tuple2f<T> t1) {
		this.x = (s * t1.x);
		this.y = (s * t1.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scale(int s) {
		this.x = s * this.x;
		this.y = s * this.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scale(float s) {
		this.x = (s * this.x);
		this.y = (s * this.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scaleAdd(int s, Tuple2f<T> t1, Tuple2f<T> t2) {
		this.x = s * t1.x + t2.x; 
		this.y = s * t1.y + t2.y; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scaleAdd(float s, Tuple2f<T> t1, Tuple2f<T> t2) {
		this.x = (s * t1.x + t2.x); 
		this.y = (s * t1.y + t2.y); 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scaleAdd(int s, Tuple2f<T> t1) {
		this.x = s * this.x + t1.x;
		this.y = s * this.y + t1.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scaleAdd(float s, Tuple2f<T> t1) {
		this.x = (s * this.x + t1.x);
		this.y = (s * this.y + t1.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(Tuple2f<T> t1) {
		this.x = t1.x;
		this.y = t1.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int[] t) {
		this.x = t[0];
		this.y = t[1];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(float[] t) {
		this.x = t[0];
		this.y = t[1];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getX() {
		return this.x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int x() {
		return (int)this.x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getY() {
		return this.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int y() {
		return (int)this.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sub(Tuple2f<T> t1, Tuple2f<T> t2) {
		this.x = t1.x - t2.x;
		this.y = t1.y - t2.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sub(Tuple2f<T> t1) {
		this.x -= t1.x;
		this.y -= t1.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sub(int x, int y) {
		this.x -= x;
		this.y -= y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void subX(int x) {
		this.x -= x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void subY(int y) {
		this.y -= y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sub(float x, float y) {
		this.x -= x;
		this.y -= y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void subX(float x) {
		this.x -= x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void subY(float y) {
		this.y -= y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void interpolate(Tuple2f<T> t1, Tuple2f<T> t2, float alpha) {
		this.x = ((1f-alpha)*t1.x + alpha*t2.x);
		this.y = ((1f-alpha)*t1.y + alpha*t2.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void interpolate(Tuple2f<T> t1, float alpha) {
		this.x = ((1f-alpha)*this.x + alpha*t1.x);
		this.y = ((1f-alpha)*this.y + alpha*t1.y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Tuple2f<T> t1) {
		try {
			return(this.x == t1.x && this.y == t1.y);
		}
		catch (NullPointerException e2) {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object t1) {
		try {
			T t2 = (T) t1;
			return(this.x == t2.x && this.y == t2.y);
		}
		catch(AssertionError e) {
			throw e;
		}
		catch (Throwable e2) {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean epsilonEquals(T t1, float epsilon) {
		float diff;

		diff = this.x - t1.x;
		if(Float.isNaN(diff)) return false;
		if((diff<0?-diff:diff) > epsilon) return false;

		diff = this.y - t1.y;
		if(Float.isNaN(diff)) return false;
		if((diff<0?-diff:diff) > epsilon) return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int bits = 1;
		bits = 31 * bits + Float.floatToIntBits(this.x);
		bits = 31 * bits + Float.floatToIntBits(this.y);
		return bits ^ (bits >> 32);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "(" //$NON-NLS-1$
				+this.x
				+";" //$NON-NLS-1$
				+this.y
				+")"; //$NON-NLS-1$
	}

}