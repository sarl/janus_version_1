/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010 Janus Core Developers
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
package org.janusproject.demos.simulation.foragerbots.agents;

import java.util.EventListener;

import org.janusproject.demos.simulation.foragerbots.agents.Grid.Cell;

/** Listener on grid GUI events.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface GridListener extends EventListener {

	/**
	 * Invoked when the grid content has changed.
	 * @param width is the width of the grid.
	 * @param height is the height of the grid.
	 * @param cells is the grid content.
	 * @param baseCoordinates are the coordinates of the bases.
	 */
	public void onGridChanged(int width, int height, Cell[][] cells, int[] baseCoordinates);
	
}
