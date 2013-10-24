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
package org.janusproject.jaak.envinterface.frustum;

import java.util.Iterator;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.EnvironmentArea;

/** This class defines a frustum for for a turtle which is
 * restricted to a square.
 * This frustum is not orientable.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SquareTurtleFrustum implements TurtleFrustum {

	private final int side;
	
	/**
	 * @param side is the length of the square side
	 */
	public SquareTurtleFrustum(int side) {
		this.side = side;
	}
	
	/** Replies the side of the square.
	 * 
	 * @return the side of the square.
	 */
	public int getSideLength() {
		return this.side;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Point2i> getPerceivedCells(Point2i origin, float direction, EnvironmentArea environment) {
		return new PointIterator(origin);
	}
	
	/** This class defines a frustum for for a turtle which is
	 * restricted to a square.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PointIterator implements Iterator<Point2i> {
		
		private final Point2i replied = new Point2i();
		private final int sx, ex, ey;
		private int x, y;
		
		/**
		 * @param center
		 */
		public PointIterator(Point2i center) {
			int s = getSideLength();
			int ds = s / 2; 			
			this.sx = this.x = center.x() - ds;
			this.y = center.y() - ds;

			this.ex = center.x() + ds;
			this.ey = center.y() + ds;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.x<=this.ex && this.y<=this.ey;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Point2i next() {
			this.replied.set(this.x, this.y);
			this.x++;
			if (this.x>this.ex) {
				this.y++;
				this.x = this.sx;
			}
			return this.replied;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			//
		}
		
	}

}