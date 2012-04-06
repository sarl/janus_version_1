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

/**
 * The different kind of capacity's implementation :
 * <ul>
 * <li>DirectAtomic : the implementation is own by the caller 
 * <li>IndirectAtomic : the capacity is owned by a member of the caller
 * <li>Composed : the implementation is obtained following a predefined sequence of interaction
 * <li>Emergent : the implementation emerged from caller's members interactions 
 * </ul>
 *
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum CapacityImplementationType {
	/**
	 * the implementation is own by the caller.
	 */
	DIRECT_ACTOMIC,
	/**
	 * the capacity is owned by a member of the caller
	 */
	INDIRECT_ATOMIC, 
	/**
	 * the implementation is obtained following a predefined sequence of interaction
	 */
	COMPOSED, 
	/**
	 * the implementation emerged from caller's members interactions
	 */
	EMERGENT;
	
}
