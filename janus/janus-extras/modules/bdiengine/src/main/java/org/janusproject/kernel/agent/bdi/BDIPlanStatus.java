/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.kernel.agent.bdi;

import org.janusproject.kernel.status.SingleStatus;

/**
 * BDIPlanStatus
 * Each plan execution launches a BDIPlanStatus.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BDIPlanStatus extends SingleStatus{

	/**
	 */
	private static final long serialVersionUID = 7013430482887030364L;
	
	/**
	 * status' type
	 */
	private BDIPlanStatusType type;
	
	/**
	 * Create a plan status
	 * @param type is the status' type
	 */
	public BDIPlanStatus(BDIPlanStatusType type) {
		super();
		this.type = type;
	}
	
	/**
	 * Get the status' type
	 * @return the status' type
	 */
	public BDIPlanStatusType getType() {
		return this.type;
	}	
}
