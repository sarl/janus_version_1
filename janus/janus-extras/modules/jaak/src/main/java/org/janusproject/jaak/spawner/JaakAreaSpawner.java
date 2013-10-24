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
package org.janusproject.jaak.spawner;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.math.discrete.object2d.Rectangle2i;
import org.arakhne.afc.math.discrete.object2d.Shape2i;
import org.janusproject.kernel.util.random.RandomNumber;

/** Provide implementation for a turtle spawner on a rectangle.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class JaakAreaSpawner extends JaakSpawner {
	
	private final int x;
	private final int y;
	private final int w;
	private final int h;
	
	/**
	 * @param x is the position of the spawner.
	 * @param y is the position of the spawner.
	 * @param width is the width of the spawner.
	 * @param height is the width of the spawner.
	 */
	public JaakAreaSpawner(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Point2i computeCurrentSpawningPosition(Point2i desiredPosition) {
		if (desiredPosition!=null
		 && desiredPosition.x()>=this.x
		 && desiredPosition.y()>=this.y
		 && desiredPosition.x()<=this.x+this.w
		 && desiredPosition.y()<=this.y+this.h) {
			return new Point2i(desiredPosition);
		}
		int dx = RandomNumber.nextInt(this.w);
		int dy = RandomNumber.nextInt(this.h);
		return new Point2i(this.x+dx, this.y+dy);
	}
		
	/** {@inheritDoc}
	 */
	@Override
	public Point2i getReferenceSpawningPosition() {
		return new Point2i(this.x, this.y);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Shape2i toShape() {
		return new Rectangle2i(this.x, this.y, this.w, this.h);
	}

}