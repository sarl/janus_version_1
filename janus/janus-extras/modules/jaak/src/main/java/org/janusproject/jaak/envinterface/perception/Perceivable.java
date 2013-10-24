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
package org.janusproject.jaak.envinterface.perception;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.body.TurtleBody;

/** This interface defines a situated object which is perceivable
 * inside the Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Perceivable {

	/** Replies the absolute position of this perceived object.
	 * 
	 * @return the absolute position of this perceived object.
	 */
	public Point2i getPosition();
	
	/** Replies the relative position of this perceived object to the given body.
	 *
	 * @param body
	 * @return the relative position of this perceived object to the given body.
	 */
	public Point2i getRelativePosition(TurtleBody body);

	/** Replies if this perceived object is a turtle.
	 * 
	 * @return <code>true</code> if this perceived object is a turtle,
	 * otherwise <code>false</code>.
	 */
	public boolean isTurtle();
	
	/** Replies if this perceived object is an obstacle.
	 * 
	 * @return <code>true</code> if this perceived object is an obstacle,
	 * otherwise <code>false</code>.
	 */
	public boolean isObstacle();

	/** Replies if this perceived object is a burrow.
	 * 
	 * @return <code>true</code> if this perceived object is a burrow,
	 * otherwise <code>false</code>.
	 */
	public boolean isBurrow();

	/** Replies if this perceived object is a substance.
	 * 
	 * @return <code>true</code> if this perceived object is a substance,
	 * otherwise <code>false</code>.
	 */
	public boolean isSubstance();

	/** Replies the semantic associated to this object.
	 * 
	 * @return the semantic associated to this object.
	 */
	public Object getSemantic();
	
}