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
package org.janusproject.jaak.envinterface.influence;

/** Status of the last emitted motion influence.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum MotionInfluenceStatus {

	/** The status is not available.
	 */
	NOT_AVAILABLE,

	/** The influence was completely applied, ie. the motion was
	 * proceeded entirely.
	 */
	COMPLETE_MOTION,
	
	/** The motion was stopped in such a way that the turtle
	 * move on a distance strictly smaller than the influence request.
	 */
	PARTIAL_MOTION,
	
	/** The motion was completely discarted in such a way that the turtle
	 * has not moved.
	 */
	NO_MOTION;

	/** Replies if the status corresponds to a failure.
	 * 
	 * @return <code>true</code> if no motion or status not available.
	 */
	public boolean isFailure() {
		return this==NO_MOTION || this==NOT_AVAILABLE;
	}
	
	/** Replies if the status corresponds to a success.
	 * 
	 * @return <code>true</code> if complete or partial motion.
	 */
	public boolean isSuccess() {
		return this==PARTIAL_MOTION|| this==COMPLETE_MOTION;
	}

}