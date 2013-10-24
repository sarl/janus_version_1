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
 * restricted to a circle.
 * This frustum is not orientable.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CircleTurtleFrustum implements TurtleFrustum {

	private final int radius;
	
	/**
	 * @param radius is the radius of the perception frustum.
	 */
	public CircleTurtleFrustum(int radius) {
		this.radius = radius;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Point2i> getPerceivedCells(Point2i origin, float direction, EnvironmentArea environment) {
		return new PointIterator(origin);
	}
	
	/** Replies the perception radius.
	 * 
	 * @return the perception radius.
	 */
	public int getRadius() {
		return this.radius;
	}

	/** This class defines a frustum for for a turtle which is
	 * restricted to a circle.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PointIterator implements Iterator<Point2i> {
		
		private final int cx, cy;
		private final Point2i replied = new Point2i();
		private final int sx, ex, ey;
		private int x, y;
		
		/**
		 * @param center
		 */
		public PointIterator(Point2i center) {
			this.cx = center.x();
			this.cy = center.y();
			
			int r = getRadius();
			this.sx = this.x = this.cx - r;
			this.y = this.cy - r;

			this.ex = this.cx + r;
			this.ey = this.cy + r;
			
			searchNext();
		}
		
		private void searchNext() {
			int sr;
			int dx, dy;
			
			sr = getRadius();
			sr = sr * sr;
			
			while (this.x<=this.ex || this.y<=this.ey) {
				dx = Math.abs(this.x - this.cx);
				dy = Math.abs(this.y - this.cy);
				if ((dx*dx+dy*dy)<=sr) {
					return;
				}
				inc();
			}
		}
		
		private void inc() {
			this.x++;
			if (this.x>this.ex) {
				this.y++;
				this.x = this.sx;
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.x<=this.ex || this.y<=this.ey;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Point2i next() {
			this.replied.set(this.x, this.y);
			inc();
			searchNext();
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