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
package org.janusproject.kernel.schedule;

import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Stub for Activable.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class ActivableStub implements Activable {

	/** Indicates if this object was initialized.
	 */
	public boolean isInit = false;

	/** Indicates if this object was run.
	 */
	public boolean isRun = false;
	
	/** Indicates if this object was destroyed.
	 */
	public boolean isDestroy = false;

	/**
	 */
	public ActivableStub() {
		//
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		this.isInit = true;
		return StatusFactory.ok(this);
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
	public Status end() {
		this.isDestroy = true;
		return StatusFactory.ok(this);
	}

}
