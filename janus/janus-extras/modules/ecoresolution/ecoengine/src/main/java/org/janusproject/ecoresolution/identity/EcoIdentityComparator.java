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
package org.janusproject.ecoresolution.identity;

import java.util.Comparator;
import java.util.UUID;


/** Comparator of eco-identity.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class EcoIdentityComparator implements Comparator<EcoIdentity> {

	/** Singleton of the comparator.
	 */
	public static final EcoIdentityComparator SINGLETON = new EcoIdentityComparator();
	
	private EcoIdentityComparator() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(EcoIdentity o1, EcoIdentity o2) {
		if (o1==o2) return 0;
		if (o1==null) return Integer.MIN_VALUE;
		if (o2==null) return Integer.MAX_VALUE;

		UUID i1 = o1.getEcoEntityId();
		UUID i2 = o2.getEcoEntityId();
		
		if (i1==i2) return 0;
		if (i1==null) return Integer.MIN_VALUE;
		if (i2==null) return Integer.MAX_VALUE;
		return i1.compareTo(i2);
	}
	
}