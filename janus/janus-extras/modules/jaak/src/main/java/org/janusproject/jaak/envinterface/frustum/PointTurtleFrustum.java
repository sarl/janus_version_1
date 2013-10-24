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

import java.util.Collections;
import java.util.Iterator;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.EnvironmentArea;

/** This class defines a frustum for for a turtle which is
 * restricted to a point.
 * This frustum is not orientable.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PointTurtleFrustum implements TurtleFrustum {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Point2i> getPerceivedCells(Point2i origin, float direction, EnvironmentArea environment) {
		return Collections.singleton(origin).iterator();
	}
	
}