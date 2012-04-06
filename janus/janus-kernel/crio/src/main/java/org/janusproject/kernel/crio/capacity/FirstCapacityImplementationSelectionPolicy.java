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

/**
 * This class permits to select always the first capacity implementation.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FirstCapacityImplementationSelectionPolicy
implements CapacityImplementationSelectionPolicy {
	
	/** Default instance of this policy.
	 */
	public static final CapacityImplementationSelectionPolicy DEFAULT = 
		new FirstCapacityImplementationSelectionPolicy();
	
	/** {@inheritDoc}
	 */
	@Override
	public CapacityImplementation selectImplementation(Collection<? extends CapacityImplementation> capacities) {
		assert(capacities!=null);
		if (capacities.isEmpty()) return null;
		if (capacities instanceof List<?>) 
			return ((List<? extends CapacityImplementation>)capacities).get(0);
		Iterator<? extends CapacityImplementation> iterator = capacities.iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

}
