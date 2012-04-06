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
package org.janusproject.kernel.crio.capacity;

/**
 * A capacity implementation was not found.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CapacityImplementationNotFoundException
extends CapacityCallException {

	private static final long serialVersionUID = -4986676702227258704L;
	
	private final Class<? extends Capacity> type;
	
	/**
	 * @param type is the type of capacity.
	 */
	public CapacityImplementationNotFoundException(Class<? extends Capacity> type) {
		this.type = type;
	}
	
    /**
     * Replies the type of capacity which was not found.
     *
     * @return the type of capacity.
     */
	public final Class<? extends Capacity> getCapacityType() {
		return this.type;
	}
	
}