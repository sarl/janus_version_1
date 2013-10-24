/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.crio.core.RoleAddress;

/**
 * This exception is thrown when the platform is trying to run a role owned
 * by an agent that has migrated to an other kernel.
 * This exception should never occurs, except in extrem cases or platform bugs.
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class RoleMigratedException extends RuntimeException {

	private static final long serialVersionUID = 5713237314227941798L;

	/**
     * Default constructor, no message put in exception.
     * 
     * @param roleAddress
     */
    public RoleMigratedException(RoleAddress roleAddress) {
    	super(Locale.getString(RoleMigratedException.class, "MESSAGE", roleAddress)); //$NON-NLS-1$
    }
    
}
