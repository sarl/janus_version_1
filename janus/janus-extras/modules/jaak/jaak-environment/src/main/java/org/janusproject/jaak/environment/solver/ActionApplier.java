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
package org.janusproject.jaak.environment.solver;

import java.util.Collection;

import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;

/** This interface defines the methods which are used
 * to apply actions in a Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ActionApplier {
		
	/** Remove the turtle body on the given cell.
	 * <p>
	 * If the given body is not on the cell at the given
	 * position, this function does nothing.
	 * 
	 * @param x is the position of the body.
	 * @param y is the position of the body.
	 * @param body is the body to remove.
	 * @return success state.
	 */
	public boolean removeTurtle(int x, int y, TurtleBody body);
	
	/** Add the turtle body on the given cell.
	 * <p>
	 * If a body is already on the cell at the given
	 * position, this function does nothing.
	 * 
	 * @param x is the position of the body.
	 * @param y is the position of the body.
	 * @param body is the body to add.
	 * @return success state.
	 */
	public boolean putTurtle(int x, int y, TurtleBody body);
	
	/** Remove the environmental object on the given cell.
	 * 
	 * @param x is the position of the object.
	 * @param y is the position of the object.
	 * @param object is the object to remove.
	 * @return the removed object, not always the given <var>object</var> in the case
	 * of substances.
	 */
	public EnvironmentalObject removeObject(int x, int y, EnvironmentalObject object);
	
	/** Remove all the environmental objects on the given cell.
	 * 
	 * @param x is the position of the cell.
	 * @param y is the position of the cell.
	 * @return the removed objects.
	 */
	public Collection<EnvironmentalObject> removeObjects(int x, int y);

	/** Add the environmental object on the given cell.
	 * 
	 * @param x is the position of the object.
	 * @param y is the position of the object.
	 * @param object is the object to add.
	 * @return the added object, not always the given <var>object</var> in the case
	 * of substances.
	 */
	public EnvironmentalObject putObject(int x, int y, EnvironmentalObject object);

	/**
	 * Update the body state with the given informations.
	 *
	 * @param x is the position of the body.
	 * @param y is the position of the body.
	 * @param headingAngle is the heading direction
	 * @param speed is the instant speed of the body in cells per second.
	 * @param body is the body to change.
	 * @return success state.
	 */
	public boolean setPhysicalState(int x, int y, float headingAngle, float speed, TurtleBody body);

}