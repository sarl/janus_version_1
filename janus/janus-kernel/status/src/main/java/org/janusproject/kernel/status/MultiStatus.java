/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.status;

import java.util.List;

/**
 * A multi-status object represents a collection of outcome of an operation.
 * <p>
 * Severity of a multistatus is the highest severity of the status in collection.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface MultiStatus extends Status {

	/**
	 * Replies all the inner status.
	 * 
	 * @return an unmodifiable collection of inner status.
	 */
	public List<Status> getInnerStatus();
	
	/**
	 * Replies higher status.
	 * 
	 * @return the first status with the higher priority.
	 */
	public Status getHigherStatus();

}
