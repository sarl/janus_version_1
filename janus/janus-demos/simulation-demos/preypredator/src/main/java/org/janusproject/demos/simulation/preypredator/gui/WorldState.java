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

import org.janusproject.demos.simulation.preypredator.message.MoveDirection;

/** 
 * State of the world.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class WorldState implements Cloneable {

	/**
	 * X.
	 */
	public int X;
	/**
	 * Y.
	 */
	public int Y;
	/**
	 * Move
	 */
	public MoveDirection DIRECTION;

	/**
	 * @param x
	 * @param y
	 * @param move
	 */
	public WorldState(int x, int y, MoveDirection move) {
		this.X = x;
		this.Y = y;
		this.DIRECTION = move==null ? MoveDirection.NONE : move;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public WorldState clone() {
		return new WorldState(this.X, this.Y, this.DIRECTION);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof WorldState) {
			WorldState s = (WorldState)o;
			return this.X==s.X && this.Y==s.Y;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 1;
		code = 37 * code + this.X;
		code = 37 * code + this.Y;
		return code;
	}
	
}