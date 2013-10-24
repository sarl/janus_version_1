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
package org.janusproject.demos.jaak.ants.organization;

import java.util.Collection;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.demos.jaak.ants.environment.Pheromone;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityPrototype;

/** This interface defines the capacity to select a route of pheromone.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@CapacityPrototype(
		fixedParameters={Point2i.class,Collection.class},
		fixedOutput=Pheromone.class
)
public interface PheromoneFollowingCapacity extends Capacity {
	
	/** Select and replies a pheromone to follow.
	 * 
	 * @param position is the current position of the follower.
	 * @param pheromones are the pheromones in which the selection should be done.
	 * @return a selectesd pheromone to follow or <code>null</code> to follow nothing.
	 */
	public Pheromone followPheromone(Point2i position, Collection<? extends Pheromone> pheromones);
	
}