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
 * BDIActionStatus
 * Each action execution launches a BDIActionStatus.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BDIActionStatus extends SingleStatus {

	/**
	 */
	private static final long serialVersionUID = 8339648107474441933L;
	
	/**
	 * Status's type. 
	 */
	private BDIActionStatusType type;
	
	/**
	 * Default constructor
	 * @param type is the status's type
	 */
	public BDIActionStatus(BDIActionStatusType type) {
		super();
		this.type = type;
	}
	
	/**
	 * Return the status's type.
	 * @return the status'type
	 */
	public BDIActionStatusType getType() {
		return this.type;
	}
}
