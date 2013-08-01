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
package org.janusproject.kernel.crio.role;

import org.janusproject.kernel.crio.core.Role;

/**
 * This exception is thrown when a role is not found.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoleNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4961591482298589272L;
	
	private final Class<? extends Role> roleType;
	
	/**
     * Default constructor.
     * 
     * @param role is the not-found type.
     */
    public RoleNotFoundException(Class<? extends Role> role) {
    	this.roleType = role;
    }
    
    /** Replies the undefined role.
     * 
     * @return the undefined role.
     */
    public Class<? extends Role> getUndefinedRole() {
    	return this.roleType;
    }
    
}
