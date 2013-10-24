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
package org.janusproject.jaak.environment;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;

/** This interface defines a grid for the Jaak environment model.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface GridModel {

	/** Replies the width of the grid.
	 * 
	 * @return the width of the grid.
	 */
	public int getWidth();
	
	/** Replies the height of the grid.
	 * 
	 * @return the height of the grid.
	 */
	public int getHeight();
		
	/** Replies the turtle body on the cell at the
	 * given coordinate.
	 * 
	 * @param x is the coordinate of the cell.
	 * @param y is the coordinate of the cell.
	 * @return the turtle body at the given position, or
	 * <code>null</code> if no turtle body is located at
	 * the given position.
	 * @throws IndexOutOfBoundsException if the given position
	 * is outside the grid.
	 */
	public TurtleBody getTurtle(int x, int y);

	/** Replies the environmental objects on the cell at the
	 * given coordinate.
	 * 
	 * @param x is the coordinate of the cell.
	 * @param y is the coordinate of the cell.
	 * @return the environmental objects at the given position, 
	 * never {@link NullPointerException}.
	 * @throws IndexOutOfBoundsException if the given position
	 * is outside the grid.
	 */
	public Iterable<? extends EnvironmentalObject> getObjects(int x, int y);
	
	/** Validate the given position to be on the grid.
	 * This function ensures that the given position is
	 * updated to fit the bounds of the grid.
	 * 
	 * @param isWrapped indicates if the grid is using wrapped coordinate system.
	 * @param allowDiscard indicates if the coordinates are able to be discarded.
	 * @param position is the position to validate.
	 * @return how the <var>position</var> has been changed.
	 */
	public ValidationResult validatePosition(boolean isWrapped, boolean allowDiscard, Point2i position);
	
}