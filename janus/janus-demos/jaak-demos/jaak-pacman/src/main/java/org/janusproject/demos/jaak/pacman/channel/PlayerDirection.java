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
package org.janusproject.demos.jaak.pacman.channel;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.math.discrete.object2d.Vector2i;
import org.janusproject.kernel.util.random.RandomNumber;

/**
 * Direction for the player.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum PlayerDirection {
	
	/** North.
	 */
	NORTH(0,-1) {
		@Override
		public PlayerDirection opposite() { return SOUTH; }
		@Override
		public PlayerDirection[] sides() { return new PlayerDirection[] {EAST,WEST}; }
	},
	/** West.
	 */
	WEST(-1,0) {
		@Override
		public PlayerDirection opposite() { return EAST; }
		@Override
		public PlayerDirection[] sides() { return new PlayerDirection[] {NORTH,SOUTH}; }
	},
	/** South.
	 */
	SOUTH(0,+1) {
		@Override
		public PlayerDirection opposite() { return NORTH; }
		@Override
		public PlayerDirection[] sides() { return new PlayerDirection[] {EAST,WEST}; }
	},
	/** East.
	 */
	EAST(+1,0) {
		@Override
		public PlayerDirection opposite() { return WEST; }
		@Override
		public PlayerDirection[] sides() { return new PlayerDirection[] {NORTH,SOUTH}; }
	};
	
	/** Translation vector.
	 */
	public final int dx;
	/** Translation vector.
	 */
	public final int dy;
	
	PlayerDirection(int x, int y) {
		this.dx = x;
		this.dy = y;
	}

	/** Replies a random direction.
	 * 
	 * @return a random direction.
	 */
	public static PlayerDirection random() {
		return values()[RandomNumber.nextInt(values().length)];
	}
	
	/** Replies the opposite direction.
	 * 
	 * @return the opposite direction.
	 */
	public abstract PlayerDirection opposite();
	
	/** Replies the side directions.
	 * 
	 * @return the side directions.
	 */
	public abstract PlayerDirection[] sides();

	/** Replies the direction which is corresponding
	 * to the movement from point1 to point2.
	 * 
	 * @param point1
	 * @param point2
	 * @return direction
	 */
	public static PlayerDirection makeDirection(Point2i point1, Point2i point2) {
		Vector2i v = new Vector2i(point2.x(), point2.y());
		v.sub(point1.x(), point1.y());
		if (v.x()==0 && v.y()==0) return random();
		if (Math.abs(v.x()) == Math.abs(v.y())) {
			PlayerDirection r = random();
			while (Math.signum(r.dx)!=Math.signum(v.x()) &&
					Math.signum(r.dy)!=Math.signum(v.y())) {
				r = random();
			}
			return r;
		}
		if (Math.abs(v.x()) > Math.abs(v.y())) {
			if (v.x()<0) {
				return PlayerDirection.WEST;
			}
			return PlayerDirection.EAST;
		}
		if (v.y()<0) {
			return PlayerDirection.NORTH;
		}
		return PlayerDirection.SOUTH;
	}
	
}
