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

import org.janusproject.kernel.status.Status;

/**
 * This exception is thrown when a given referenced role could not be initialized.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoleNotInitializedException extends RuntimeException {

	private static final long serialVersionUID = -7835657819019662638L;

	private final Status status;
	
	/**
     * Default constructor, no message put in exception.
     */
    public RoleNotInitializedException() {
    	this.status = null;
    }

	/**
     * @param status is the status which has caused this exception.
     */
    public RoleNotInitializedException(Status status) {
    	this.status = status;
    }

    /**
     * Constructor with given message put in exception.
     *
     * @param message the detail message.
     */
    public RoleNotInitializedException(String message) {
    	super(message);
    	this.status = null;
    }
    
    /** Replies the status associated to this exception if it is existing.
     * 
     * @return the status or <code>null</code>
     */
    public Status getStatus() {
    	return this.status;
    }
    
}
