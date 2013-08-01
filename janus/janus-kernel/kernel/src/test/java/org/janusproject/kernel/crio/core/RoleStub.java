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
package org.janusproject.kernel.crio.core;

import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class RoleStub extends Role {

	/**
	 */
	public boolean isInit = false;
	/**
	 */
	public boolean isRun = false;
	/**
	 */
	public boolean isDestroyed = false;
	
	/**
	 */
	public RoleStub() {
		super();
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status live() {
		this.isRun = true;
		return StatusFactory.ok(this);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Status activate(Object... params) {
		this.isInit = true;
		return StatusFactory.ok(this);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status end() {
		this.isDestroyed = true;
		return StatusFactory.ok(this);
	}

}