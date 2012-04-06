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
package org.janusproject.demos.simulation.foragerbots.message;

import org.janusproject.demos.simulation.foragerbots.agents.Grid.Cell;
import org.janusproject.kernel.message.Message;

/**
 * Perception message.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PerceptionMessage extends Message {
	
	private static final long serialVersionUID = 8442953037694761376L;
	
	private final Cell cell;
	
	/**
	 * @param currentCell is the current cell.
	 */
	public PerceptionMessage(Cell currentCell) {
		this.cell = currentCell;
	}
	
	/** Replies the current cell.
	 * 
	 * @return the current cell.
	 */
	public Cell getCurrentCell() {
		return this.cell;
	}
	
}
