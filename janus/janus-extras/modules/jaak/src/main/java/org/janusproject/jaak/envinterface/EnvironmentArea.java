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
package org.janusproject.jaak.envinterface;

import org.arakhne.afc.math.discrete.object2d.Point2i;

/** This interface is for the area covered by the environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface EnvironmentArea {

	/** Replies the minimal x coordinate on the environment.
	 * 
	 * @return the min x.
	 */
	public int getX();
	
	/** Replies the minimal y coordinate on the environment.
	 * 
	 * @return the min y.
	 */
	public int getY();

	/** Replies the width of the environment.
	 * 
	 * @return the width of the environment.
	 */
	public int getWidth();
	
	/** Replies the height of the environment.
	 * 
	 * @return the height of the environment.
	 */
	public int getHeight();

	/** Replies if the cell at the given position is free or not.
	 * <p>
	 * A cell is free when no turtle nor obstacle is inside.
	 * Any coordinate outside the environment grid is assumed to be
	 * not free.
	 * 
	 * @param position is the position to test.
	 * @return <code>true</code> if the cell at the given position is
	 * free, otherwise <code>false</code>
	 */
	public boolean isFree(Point2i position);

	/** Replies if the cell at the given position contains an obstacle.
	 * <p>
	 * Any coordinate outside the environment grid is assumed to be
	 * an obstacle.
	 * 
	 * @param position is the position to test.
	 * @return <code>true</code> if the cell at the given position contains
	 * an obstacle, otherwise <code>false</code>
	 */
	public boolean hasObstacle(Point2i position);

}