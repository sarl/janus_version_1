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
package org.janusproject.scriptedagent.exception;

import java.net.URL;

import org.arakhne.afc.vmutil.locale.Locale;

/**
 * Invalid directory for Jython scripts.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class InvalidDirectoryException extends RuntimeException {

	private static final long serialVersionUID = 7445830696521857366L;
	
	private final URL directory;
	
	/**
	 * @param directory is the invalid directory.
	 */
	public InvalidDirectoryException(URL directory) {
		super(Locale.getString("MESSAGE", directory)); //$NON-NLS-1$
		this.directory = directory;
	}
	
	/** Replies the invalid directory.
	 * 
	 * @return the invalid directory.
	 */
	public URL getInvalidDirectory() {
		return this.directory;
	}
}
