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
import java.util.Iterator;
import java.util.List;

import org.janusproject.kernel.util.random.RandomNumber;

/**
 * This class permits to randomly select a capacity implementation.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RandomCapacityImplementationSelectionPolicy
implements CapacityImplementationSelectionPolicy {
	
	/** Default instance of this policy.
	 */
	public static final CapacityImplementationSelectionPolicy DEFAULT = 
		new RandomCapacityImplementationSelectionPolicy();
	
	/** {@inheritDoc}
	 */
	@Override
	public CapacityImplementation selectImplementation(Collection<? extends CapacityImplementation> capacities) {
		assert(capacities!=null);
		int index = RandomNumber.nextInt(capacities.size());
		if (capacities instanceof List<?>) 
			return ((List<? extends CapacityImplementation>)capacities).get(index);
		Iterator<? extends CapacityImplementation> iterator = capacities.iterator();
		for(int i=0; iterator.hasNext() && i<index; ++i) {
			iterator.next();
		}
		return iterator.hasNext() ? iterator.next() : null;
	}

}
