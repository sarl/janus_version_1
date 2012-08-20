/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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


/** Mathematic utilities for Jaak.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MathUtil {
	
	/** Constant {@code PI}.
	 */
	public static final float PI = (float)Math.PI;

	/** Constant {@code 2 * PI}.
	 */
	public static final float TWO_PI = (float)(2. * Math.PI);

	/** Constant {@code PI/4}.
	 */
	public static final float QUARTER_PI = (float)(Math.PI/4.);

	/** Constant {@code 3*PI/4}.
	 */
	public static final float THREE_QUARTER_PI = (float)(3.*Math.PI/4.);

	/** Constant {@code PI/1}.
	 */
	public static final float DEMI_PI = (float)(Math.PI/2.);

	/** Constant for epsilon operations.
	 */
	public static final float EPSILON = 0.0000001f;

	/** Compute a signed angle between the given vectors.
	 * <p>
	 * The signed angle between {@code v1} and {@code v2}
	 * is the rotation angle to apply to vector {@code v1}
	 * to be colinear to {@code v2} and pointing the
	 * same demi-plane. It means that the angle replied
	 * by this function is be negative if the rotation
	 * to apply is clockwise, and positive if
	 * the rotation is counterclockwise.
	 * <p>
	 * The value replied by {@link Vector2f#angle(Vector2f)}
	 * is the absolute value of the vlaue replied by this
	 * function. 
	 * 
	 * @param v1 is the vector to rotate.
	 * @param v2 is the vector to reach.
	 * @return the rotation angle to turn {@code v1} to reach
	 * {@code v2}.
	 */
	public static float signedAngle(Vector2f v1, Vector2f v2) {
		Vector2f a = new Vector2f(v1);
		if (a.length()==0) return Float.NaN;
		Vector2f b = new Vector2f(v2);
		if (b.length()==0) return Float.NaN;
		a.normalize();
		b.normalize();
		
		float cos = a.getX() * b.getX() + a.getY() * b.getY();
		// A x B = |A|.|B|.sin(theta).N = sin(theta) (where N is the unit vector perpendicular to plane AB)
		float sin = a.getX()*b.getY() - a.getY()*b.getX();
		
		float angle = (float)Math.atan2(sin, cos);

		return angle;
	}
	
	/** Compute a signed angle between the given vectors.
	 * <p>
	 * The signed angle between {@code v1} and {@code v2}
	 * is the rotation angle to apply to vector {@code v1}
	 * to be colinear to {@code v2} and pointing the
	 * same demi-plane. It means that the angle replied
	 * by this function is be negative if the rotation
	 * to apply is clockwise, and positive if
	 * the rotation is counterclockwise.
	 * <p>
	 * The value replied by {@link Vector2f#angle(Vector2f)}
	 * is the absolute value of the vlaue replied by this
	 * function. 
	 * 
	 * @param v1 is the vector to rotate.
	 * @param v2 is the vector to reach.
	 * @return the rotation angle to turn {@code v1} to reach
	 * {@code v2}.
	 */
	public static float signedAngle(Vector2i v1, Vector2i v2) {
		Vector2f a = new Vector2f(v1);
		if (a.length()==0) return Float.NaN;
		Vector2f b = new Vector2f(v2);
		if (b.length()==0) return Float.NaN;
		a.normalize();
		b.normalize();
		
		float cos = a.getX() * b.getX() + a.getY() * b.getY();
		// A x B = |A|.|B|.sin(theta).N = sin(theta) (where N is the unit vector perpendicular to plane AB)
		float sin = a.getX()*b.getY() - a.getY()*b.getX();
		
		float angle = (float)Math.atan2(sin, cos);

		return angle;
	}

	/** Clamp the given angle in radians to {@code [0;2PI)}.
	 * 
	 * @param radians is the angle to clamp
	 * @return the angle in {@code [0;2PI)} range.
	 */
	public static float clampRadian(float radians) {
		float r = radians;
		while (r<0f) r += TWO_PI;
		while (r>=TWO_PI) r -= TWO_PI;
		return r;
	}

	/** Clamp the given value to the given range.
	 * <p>
	 * If the value is outside the {@code [min;max]}
	 * range, it is clamp to the nearest bounding value
	 * <var>min</var> or <var>max</var>.
	 * 
	 * @param v is the value to clamp.
	 * @param min is the min value of the range.
	 * @param max is the max value of the range.
	 * @return the value in {@code [min;max]} range.
	 */
	public static float clamp(float v, float min, float max) {
		if (min<max) {
			if (v<min) return min;
			if (v>max) return max;
		}
		else {
			if (v>min) return min;
			if (v<max) return max;
		}
		return v;
	}

	/** Turn the given vector about the given rotation angle.
	 * 
	 * @param v is the vector to rotate.
	 * @param angle is the rotation angle in radians.
	 */
	public static void turnVector(Vector2f v, float angle) {
		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);
		float x =  cos * v.getX() + sin * v.getY(); 
		float y = -sin * v.getX() + cos * v.getY();
		v.set(x,y);
	}
	
	/** Turn the given vector about the given rotation angle.
	 * 
	 * @param v is the vector to rotate.
	 * @param angle is the rotation angle in radians.
	 */
	public static void turnVector(Vector2i v, float angle) {
		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);
		float x =  cos * v.getX() + sin * v.getY(); 
		float y = -sin * v.getX() + cos * v.getY();
		v.set(x,y);
	}

	/** Replies the orientation vector, which is corresponding
	 * to the given angle on a trigonometric circle.
	 * 
	 * @param angle is the angle in radians to translate.
	 * @return the orientation vector which is corresponding to the given angle.
	 */
	public static Vector2f toOrientationVector2f(float angle) {
		return new Vector2f(
				(float)Math.cos(angle),
				(float)Math.sin(angle));
	}
	
	/** Replies the orientation vector, which is corresponding
	 * to the given angle on a trigonometric circle.
	 * 
	 * @param angle is the angle in radians to translate.
	 * @return the orientation vector which is corresponding to the given angle.
	 */
	public static Vector2i toOrientationVector2i(float angle) {
		return new Vector2i(
				(float)Math.cos(angle),
				(float)Math.sin(angle));
	}

	/** Replies the orientation angle on a trigonometric circle
	 * that is corresponding to the given direction.
	 * 
	 * @param orientation is the orientation vector to translate.
	 * @return the angle on a trigonometric circle that is corresponding
	 * to the given orientation vector.
	 */
	public static float toOrientationAngle(Vector2f orientation) {
		float angle = (float)Math.acos(orientation.getX());
		if (orientation.getY()<0f) angle = -angle;
		return clampRadian(angle);
	}
	
	/** Replies the orientation angle on a trigonometric circle
	 * that is corresponding to the given direction.
	 * 
	 * @param orientation is the orientation vector to translate.
	 * @return the angle on a trigonometric circle that is corresponding
	 * to the given orientation vector.
	 */
	public static float toOrientationAngle(Vector2i orientation) {
		float angle = (float)Math.acos(orientation.getX());
		if (orientation.getY()<0f) angle = -angle;
		return clampRadian(angle);
	}

	/** Replies if the given value is near zero.
	 * 
	 * @param value is the value to test.
	 * @return <code>true</code> if the given <var>value</var>
	 * is near zero, otherwise <code>false</code>.
	 */
	public static boolean isEpsilonZero(float value) {
		return Math.abs(value) <= EPSILON;
	}
	
}