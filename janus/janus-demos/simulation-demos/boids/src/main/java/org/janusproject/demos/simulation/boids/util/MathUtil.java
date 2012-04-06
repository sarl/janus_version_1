/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.simulation.boids.util;

/**
 * Math utilities.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MathUtil {
	
	/** Compute the signed angle between two vectors which are not necessary unit vectors.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return the angle between <code>-PI</code> and <code>PI</code>.
	 */
	public static double signedAngle(double x1, double y1, double x2, double y2) {
		double length1 = Math.sqrt(x1 * x1 + y1 * y1);
		double length2 = Math.sqrt(x2 * x2 + y2 * y2);

		if ((length1 == 0.) || (length2 == 0.))
			return Double.NaN;

		double cx1 = x1;
		double cy1 = y1;
		double cx2 = x2;
		double cy2 = y2;
		
		// A and B are normalized
		if (length1 != 1) {
			cx1 /= length1;
			cy1 /= length1;
		}

		if (length2 != 1) {
			cx2 /= length2;
			cy2 /= length2;
		}

		// A . B = |A|.|B|.cos(theta) = cos(theta)
		double cos = cx1 * cx2 + cy1 * cy2;
		// A x B = |A|.|B|.sin(theta).N = sin(theta) (where N is the unit vector perpendicular to plane AB)
		double sin = cx1*cy2 - cy1*cx2;
		
		double angle = Math.atan2(sin, cos);

		return angle;
	}

	/** Compute the positive angle between two vectors which are not necessary unit vectors.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return the angle between <code>0</code> and <code>PI</code>.
	 */
	public static double angle(double x1, double y1, double x2, double y2) {
		double length1 = Math.sqrt(x1 * x1 + y1 * y1);
		double length2 = Math.sqrt(x2 * x2 + y2 * y2);

		if ((length1 == 0.) || (length2 == 0.))
			return Double.NaN;

		double cx1 = x1;
		double cy1 = y1;
		double cx2 = x2;
		double cy2 = y2;
		
		// A and B are normalized
		if (length1 != 1) {
			cx1 /= length1;
			cy1 /= length1;
		}

		if (length2 != 1) {
			cx2 /= length2;
			cy2 /= length2;
		}

		double dot = cx1*cx2 + cy1*cy2;
		if (dot<-1.) dot = -1.;
		else if (dot>1.) dot = 1.;
		
		return Math.acos(dot);
	}

}
