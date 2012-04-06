/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.crio.capacity;

import java.util.Collection;

import org.janusproject.kernel.repository.Repository;

/**
 * Stores the capacities of a given entity and their associated implementation.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see Capacity
 */
public interface CapacityContainer
extends Repository<Class<? extends Capacity>,Collection<CapacityImplementation>> {
	
	/**
	 * Remove a capacity and all of its implementations from this CapacityContainer
	 * @param capacity the capacity to remove
	 */
	public void removeCapacity(Class<? extends Capacity> capacity);

	/**
	 * Remove a capacity implementation.
	 * @param capacity the capacity to remove
	 */
	public void removeCapacity(CapacityImplementation capacity);

	/**
	 * Add a capacity implementation to this container.
	 * 
	 * @param capacity is the capacity implementation to add.
	 */
	public void addCapacity(CapacityImplementation capacity);

	/**
	 * Add all capacity implementations from the given container to this container.
	 * 
	 * @param container is the capacity container to read.
	 */
	public void addAll(CapacityContainer container);

	/** Applies a selection mechanism to get one implementation of the
	 * given capacity.
	 * <p>
	 * The implementation selection is based on the {@link CapacityImplementationSelectionPolicy}
	 * of this container.
	 *
	 * @param <CI> is the type of the capacity implements to select.
	 * @param capacity is the type of capacity to select. 
	 * @return the capacity implementation or <code>null</code> if none inside.
	 */
	public <CI extends Capacity> CI selectImplementation(Class<CI> capacity);

}
