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
package org.janusproject.jaak.envinterface.channel;

import java.util.Collection;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.math.discrete.object2d.Shape2i;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.JaakObject;
import org.janusproject.kernel.channels.Channel;

/**
 * Channel to obtain information from the grid state in a Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface GridStateChannel extends Channel {
	
	/** Add listener on the channel events.
	 * 
	 * @param listener
	 */
	public void addGridStateChannelListener(GridStateChannelListener listener);
	
	/** Remove listener on the channel events.
	 * 
	 * @param listener
	 */
	public void removeGridStateChannelListener(GridStateChannelListener listener);

	/** Replies the width of the world.
	 * 
	 * @return the width of the world.
	 */
	public int getGridWidth();
	
	/** Replies the height of the world.
	 * 
	 * @return the height of the world.
	 */
	public int getGridHeight();
	
	/** Replies if the cell at the given position contains a turtle.
	 * 
	 * @param x is the position of the cell.
	 * @param y is the position of the cell.
	 * @return <code>true</code> if the cell at the given position
	 * contains an ant.
	 */
	public boolean containsTurtle(int x, int y);

	/** Replies if the instant speed of the turtle at the given position.
	 * 
	 * @param x is the position of the cell.
	 * @param y is the position of the cell.
	 * @return the instant speed of the turtle at the given position, or
	 * {@link Float#NaN} if no turtle.
	 */
	public float getSpeed(int x, int y);

	/** Replies the instant orientation of the turtle at the given position.
	 * 
	 * @param x is the coordinate of the cell.
	 * @param y is the coordinate of the cell.
	 * @return the instant orientation of the turtle in radians,
	 * or {@link Float#NaN} if no turtle.
	 * @throws IndexOutOfBoundsException if the given position
	 * is outside the grid.
	 */
	public float getOrientation(int x, int y);

	/** Replies the instant direction of the turtle at the given position.
	 * 
	 * @param x is the coordinate of the cell.
	 * @param y is the coordinate of the cell.
	 * @return the instant direction of the turtle in radians,
	 * or <code>null</code> if no turtle.
	 * @throws IndexOutOfBoundsException if the given position
	 * is outside the grid.
	 */
	public Vector2f getDirection(int x, int y);

	/** Replies the objects in the cell at the given 
	 * position.
	 *
	 * @param <T> is the type of the desired objects.
	 * @param x is the position of the cell.
	 * @param y is the position of the cell.
	 * @param type is the type of the desired objects.
	 * @return the environment objects in the given cell. 
	 */
	public <T extends EnvironmentalObject> Iterable<T> getEnvironmentalObjects(int x, int y, Class<T> type);

	/** Replies the objects in the cell at the given 
	 * position.
	 *
	 * @param x is the position of the cell.
	 * @param y is the position of the cell.
	 * @return the environment objects in the given cell. 
	 */
	public Iterable<EnvironmentalObject> getEnvironmentalObjects(int x, int y);

	/** Replies the objects and the turtles in the cell at the given 
	 * position.
	 *
	 * @param x is the position of the cell.
	 * @param y is the position of the cell.
	 * @return the environment objects in the given cell. 
	 */
	public Collection<JaakObject> getAllObjects(int x, int y);

	/** Replies the positions of the spawning points.
	 * 
	 * @return the positions of the spawning points.
	 */
	public Point2i[] getSpawningPositions();
	
	/** Replies the areas of spawning.
	 * 
	 * @return the areas of spawning.
	 */
	public Shape2i[] getSpawningLocations();

	/** Replies the number of turtle on the grid.
	 * 
	 * @return the number of turtle on the grid.
	 */
	public int getTurtleCount();
	
}
