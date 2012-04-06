/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2009 Stephane GALLAND
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

package org.janusproject.demos.simulation.preypredator.gui;

import java.util.Map;

import org.janusproject.kernel.address.AgentAddress;

/** 
 * State of the world.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface GUIWorldState {
	
	/** Replies the address of the prey.
	 * 
	 * @return the address of the prey.
	 */
	public AgentAddress getPrey();
	
	/** Replies the width of the world.
	 * 
	 * @return the number of cells in width
	 */
	public int getWorldWidth();
	
	/** Replies the height of the world.
	 * 
	 * @return the number of cells in height
	 */
	public int getWorldHeight();

	/** Stop the game.
	 */
	public void stopGame();
	
	/** Replies all the last moves.
	 * 
	 * @return all the last moves.
	 */
	public Map<AgentAddress,WorldState> getLastPositions();
	
	/** Add listener on world state changes.
	 * 
	 * @param listener
	 */
	public void addWorldStateChangeListener(WorldStateChangeListener listener);

	/** Remove listener on world state changes.
	 * 
	 * @param listener
	 */
	public void removeWorldStateChangeListener(WorldStateChangeListener listener);

}