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

import java.io.Serializable;

/** 2D tuple.
 * 
 * @param <PT> is parent type of the implementation type of the tuple.
 * @param <T> is the implementation type of the tuple.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Tuple<PT extends Tuple<PT,?>, T extends PT>
extends Cloneable, Serializable {

	/** Clone this point.
	 * 
	 * @return the clone.
	 */
	public T clone();

	/**
	 *  Sets each component of this tuple to its absolute value.
	 */
	public void absolute();

	/**
	 *  Sets each component of the tuple parameter to its absolute
	 *  value and places the modified values into this tuple.
	 *  @param t   the source tuple, which will not be modified
	 */
	public void absolute(PT t);

	/**
	 * Sets the value of this tuple to the sum of tuples t1 and t2.
	 * @param t1 the first tuple
	 * @param t2 the second tuple
	 */
	public void add(PT t1, PT t2);


	/**
	 * Sets the value of this tuple to the sum of itself and t1.
	 * @param t1 the other tuple
	 */
	public void add(PT t1);

	/**
	 * Sets the value of this tuple to the sum of itself and x and y.
	 * @param x
	 * @param y
	 */
	public void add(int x, int y);

	/**
	 * Sets the value of this tuple to the sum of itself and x and y.
	 * @param x
	 * @param y
	 */
	public void add(float x, float y);

	/**
	 * Sets the x value of this tuple to the sum of itself and x.
	 * @param x
	 */
	public void addX(int x);

	/**
	 * Sets the x value of this tuple to the sum of itself and x.
	 * @param x
	 */
	public void addX(float x);

	/**
	 * Sets the y value of this tuple to the sum of itself and y.
	 * @param y
	 */
	public void addY(int y);

	/**
	 * Sets the y value of this tuple to the sum of itself and y.
	 * @param y
	 */
	public void addY(float y);

	/**
	 *  Clamps this tuple to the range [low, high].
	 *  @param min  the lowest value in this tuple after clamping
	 *  @param max  the highest value in this tuple after clamping
	 */
	public void clamp(int min, int max);

	/**
	 *  Clamps this tuple to the range [low, high].
	 *  @param min  the lowest value in this tuple after clamping
	 *  @param max  the highest value in this tuple after clamping
	 */
	public void clamp(float min, float max);

	/**
	 *  Clamps the minimum value of this tuple to the min parameter.
	 *  @param min   the lowest value in this tuple after clamping
	 */
	public void clampMin(int min);

	/**
	 *  Clamps the minimum value of this tuple to the min parameter.
	 *  @param min   the lowest value in this tuple after clamping
	 */
	public void clampMin(float min);

	/**
	 *  Clamps the maximum value of this tuple to the max parameter.
	 *  @param max   the highest value in the tuple after clamping
	 */
	public void clampMax(int max);

	/**
	 *  Clamps the maximum value of this tuple to the max parameter.
	 *  @param max   the highest value in the tuple after clamping
	 */
	public void clampMax(float max);

	/**
	 *  Clamps the tuple parameter to the range [low, high] and
	 *  places the values into this tuple.
	 *  @param min   the lowest value in the tuple after clamping
	 *  @param max  the highest value in the tuple after clamping
	 *  @param t   the source tuple, which will not be modified
	 */
	public void clamp(int min, int max, PT t);

	/**
	 *  Clamps the tuple parameter to the range [low, high] and
	 *  places the values into this tuple.
	 *  @param min   the lowest value in the tuple after clamping
	 *  @param max  the highest value in the tuple after clamping
	 *  @param t   the source tuple, which will not be modified
	 */
	public void clamp(float min, float max, PT t);

	/**
	 *  Clamps the minimum value of the tuple parameter to the min
	 *  parameter and places the values into this tuple.
	 *  @param min   the lowest value in the tuple after clamping
	 *  @param t   the source tuple, which will not be modified
	 */
	public void clampMin(int min, PT t);

	/**
	 *  Clamps the minimum value of the tuple parameter to the min
	 *  parameter and places the values into this tuple.
	 *  @param min   the lowest value in the tuple after clamping
	 *  @param t   the source tuple, which will not be modified
	 */
	public void clampMin(float min, PT t);

	/**
	 *  Clamps the maximum value of the tuple parameter to the max
	 *  parameter and places the values into this tuple.
	 *  @param max   the highest value in the tuple after clamping
	 *  @param t   the source tuple, which will not be modified
	 */
	public void clampMax(int max, PT t);

	/**
	 *  Clamps the maximum value of the tuple parameter to the max
	 *  parameter and places the values into this tuple.
	 *  @param max   the highest value in the tuple after clamping
	 *  @param t   the source tuple, which will not be modified
	 */
	public void clampMax(float max, PT t);

	/**
	 * Copies the values of this tuple into the tuple t.
	 * @param t is the target tuple
	 */
	public void get(PT t);

	/**
	 *  Copies the value of the elements of this tuple into the array t.
	 *  @param t the array that will contain the values of the vector
	 */
	public void get(int[] t);

	/**
	 *  Copies the value of the elements of this tuple into the array t.
	 *  @param t the array that will contain the values of the vector
	 */
	public void get(float[] t);

	/**
	 * Sets the value of this tuple to the negation of tuple t1.
	 * @param t1 the source tuple
	 */
	public void negate(PT t1);

	/**
	 * Negates the value of this tuple in place.
	 */
	public void negate();

	/**
	 * Sets the value of this tuple to the scalar multiplication
	 * of tuple t1.
	 * @param s the scalar value
	 * @param t1 the source tuple
	 */
	public void scale(int s, PT t1);

	/**
	 * Sets the value of this tuple to the scalar multiplication
	 * of tuple t1.
	 * @param s the scalar value
	 * @param t1 the source tuple
	 */
	public void scale(float s, PT t1);

	/**
	 * Sets the value of this tuple to the scalar multiplication
	 * of the scale factor with this.
	 * @param s the scalar value
	 */
	public void scale(int s);

	/**
	 * Sets the value of this tuple to the scalar multiplication
	 * of the scale factor with this.
	 * @param s the scalar value
	 */
	public void scale(float s);

	/**
	 * Sets the value of this tuple to the scalar multiplication
	 * of tuple t1 plus tuple t2 (this = s*t1 + t2).
	 * @param s the scalar value
	 * @param t1 the tuple to be multipled
	 * @param t2 the tuple to be added
	 */
	public void scaleAdd(int s, PT t1, PT t2);

	/**
	 * Sets the value of this tuple to the scalar multiplication
	 * of tuple t1 plus tuple t2 (this = s*t1 + t2).
	 * @param s the scalar value
	 * @param t1 the tuple to be multipled
	 * @param t2 the tuple to be added
	 */
	public void scaleAdd(float s, PT t1, PT t2);

	/**
	 * Sets the value of this tuple to the scalar multiplication
	 * of itself and then adds tuple t1 (this = s*this + t1).
	 * @param s the scalar value
	 * @param t1 the tuple to be added
	 */
	public void scaleAdd(int s, PT t1);

	/**
	 * Sets the value of this tuple to the scalar multiplication
	 * of itself and then adds tuple t1 (this = s*this + t1).
	 * @param s the scalar value
	 * @param t1 the tuple to be added
	 */
	public void scaleAdd(float s, PT t1);

	/**
	 * Sets the value of this tuple to the value of tuple t1.
	 * @param t1 the tuple to be copied
	 */
	public void set(PT t1);

	/**
	 * Sets the value of this tuple to the specified x and y
	 * coordinates.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void set(int x, int y);

	/**
	 * Sets the value of this tuple to the specified x and y
	 * coordinates.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void set(float x, float y);

	/**
	 * Sets the value of this tuple from the 2 values specified in 
	 * the array.
	 * @param t the array of length 2 containing xy in order
	 */
	public void set(int[] t);

	/**
	 * Sets the value of this tuple from the 2 values specified in 
	 * the array.
	 * @param t the array of length 2 containing xy in order
	 */
	public void set(float[] t);

	/**
	 * Get the <i>x</i> coordinate.
	 * 
	 * @return the x coordinate.
	 */
	public float getX();

	/**
	 * Get the <i>x</i> coordinate.
	 * 
	 * @return the x coordinate.
	 */
	public int x();

	/**
	 * Set the <i>x</i> coordinate.
	 * 
	 * @param x  value to <i>x</i> coordinate.
	 */
	public void setX(int x);

	/**
	 * Set the <i>x</i> coordinate.
	 * 
	 * @param x  value to <i>x</i> coordinate.
	 */
	public void setX(float x);

	/**
	 * Get the <i>y</i> coordinate.
	 * 
	 * @return  the <i>y</i> coordinate.
	 */
	public float getY();

	/**
	 * Get the <i>y</i> coordinate.
	 * 
	 * @return  the <i>y</i> coordinate.
	 */
	public int y();

	/**
	 * Set the <i>y</i> coordinate.
	 * 
	 * @param y value to <i>y</i> coordinate.
	 */
	public void setY(int y);

	/**
	 * Set the <i>y</i> coordinate.
	 * 
	 * @param y value to <i>y</i> coordinate.
	 */
	public void setY(float y);

	/**
	 * Sets the value of this tuple to the difference
	 * of tuples t1 and t2 (this = t1 - t2).
	 * @param t1 the first tuple
	 * @param t2 the second tuple
	 */
	public void sub(PT t1, PT t2);

	/**
	 * Sets the value of this tuple to the difference
	 * of itself and t1 (this = this - t1).
	 * @param t1 the other tuple
	 */
	public void sub(PT t1);

	/**
	 * Sets the value of this tuple to the difference of itself and x and y.
	 * @param x
	 * @param y
	 */
	public void sub(int x, int y);

	/**
	 * Sets the value of this tuple to the difference of itself and x and y.
	 * @param x
	 * @param y
	 */
	public void sub(float x, float y);

	/**
	 * Sets the x value of this tuple to the difference of itself and x.
	 * @param x
	 */
	public void subX(int x);

	/**
	 * Sets the x value of this tuple to the difference of itself and x.
	 * @param x
	 */
	public void subX(float x);

	/**
	 * Sets the y value of this tuple to the difference of itself and y.
	 * @param y
	 */
	public void subY(int y);

	/**
	 * Sets the y value of this tuple to the difference of itself and y.
	 * @param y
	 */
	public void subY(float y);

	/** 
	 *  Linearly interpolates between tuples t1 and t2 and places the 
	 *  result into this tuple:  this = (1-alpha)*t1 + alpha*t2.
	 *  @param t1  the first tuple
	 *  @param t2  the second tuple
	 *  @param alpha  the alpha interpolation parameter
	 */
	public void interpolate(PT t1, PT t2, float alpha);

	/**  
	 *  Linearly interpolates between this tuple and tuple t1 and 
	 *  places the result into this tuple:  this = (1-alpha)*this + alpha*t1.
	 *  @param t1  the first tuple
	 *  @param alpha  the alpha interpolation parameter  
	 */   
	public void interpolate(PT t1, float alpha); 

	/**   
	 * Returns true if all of the data members of Tuple2f t1 are
	 * equal to the corresponding data members in this Tuple2f.
	 * @param t1  the vector with which the comparison is made
	 * @return  true or false
	 */  
	public boolean equals(PT t1);

	/**   
	 * Returns true if the Object t1 is of type Tuple2f and all of the
	 * data members of t1 are equal to the corresponding data members in
	 * this Tuple2f.
	 * @param t1  the object with which the comparison is made
	 * @return  true or false
	 */  
	@Override
	public boolean equals(Object t1);

	/**
	 * Returns true if the L-infinite distance between this tuple
	 * and tuple t1 is less than or equal to the epsilon parameter, 
	 * otherwise returns false.  The L-infinite
	 * distance is equal to MAX[abs(x1-x2), abs(y1-y2)]. 
	 * @param t1  the tuple to be compared to this tuple
	 * @param epsilon  the threshold value  
	 * @return  true or false
	 */
	public boolean epsilonEquals(T t1, float epsilon);

	/**
	 * Returns a hash code value based on the data values in this
	 * object.  Two different Tuple2f objects with identical data values
	 * (i.e., Tuple2f.equals returns true) will return the same hash
	 * code value.  Two objects with different data members may return the
	 * same hash value, although this is not likely.
	 * @return the integer hash code value
	 */  
	@Override
	public int hashCode();

}