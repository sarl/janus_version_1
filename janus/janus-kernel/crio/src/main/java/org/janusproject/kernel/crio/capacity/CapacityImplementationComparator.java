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

import java.util.Comparator;

/**
 * Provide the means to compare two capacity implementation in a TreeSet
 * TreeSet.first() should provide the choosen implementation
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CapacityImplementationComparator implements Comparator<CapacityImplementation> {

	/** Singleton.
	 */
	public static final CapacityImplementationComparator SINGLETON = new CapacityImplementationComparator();
	
	/**
	 */
	private CapacityImplementationComparator() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(CapacityImplementation o1, CapacityImplementation o2) {
		if (o1==o2) return 0;
		if (o1==null) return -1;
		if (o2==null) return 1;
		
		CapacityImplementationType t1 = o1.getImplementationType();
		CapacityImplementationType t2 = o2.getImplementationType();
	
		int cmp = t1.ordinal() - t2.ordinal();
		if (cmp!=0) return cmp;

		return o1.hashCode() - o2.hashCode();
	}

}