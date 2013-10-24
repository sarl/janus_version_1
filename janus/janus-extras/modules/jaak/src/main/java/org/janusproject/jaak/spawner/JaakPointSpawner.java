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

/** Provide implementation for a turtle spawner on a point.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class JaakPointSpawner extends JaakSpawner {
	
	private final Point2i position;
	
	/**
	 * @param x is the position of the spawner.
	 * @param y is the position of the spawner.
	 */
	public JaakPointSpawner(int x, int y) {
		this.position = new Point2i(x,y);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Point2i computeCurrentSpawningPosition(Point2i desiredPosition) {
		return this.position;
	}
		
	/** {@inheritDoc}
	 */
	@Override
	public Point2i getReferenceSpawningPosition() {
		return this.position;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Shape2i toShape() {
		return new Rectangle2i(this.position.x(), this.position.y(), 1, 1);
	}

}