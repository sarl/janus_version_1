/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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

import java.util.logging.Logger;

import org.janusproject.kernel.logger.LoggerProvider;
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
class ActivatorStub implements Activator<Activable> {

	/** Indicates if this object was initialized.
	 */
	public boolean isInit = false;

	/** Indicates if this object was run.
	 */
	public boolean isRun = false;
	
	/** Indicates if this object was destroyed.
	 */
	public boolean isDestroy = false;
	
	private final boolean hasActivable;

	/**
	 * @param hasActivable
	 */
	public ActivatorStub(boolean hasActivable) {
		this.hasActivable = hasActivable;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void sync() {
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

	@Override
	public boolean canActivate(Class<?> type) {
		return Activable.class.isAssignableFrom(type);
	}

	@Override
	public boolean hasActivable() {
		return this.hasActivable;
	}

	@Override
	public boolean isUsed() {
		return true;
	}

	@Override
	public void used() {
		//
	}

	@Override
	public void unused() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.hasActivable ? 1 : 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int toArray(Activable[] a) {
		if (a!=null && a.length>1) {
			a[0] = new ActivableStub();
			return 1;
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger getLogger() {
		return Logger.getAnonymousLogger();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoggerProvider(LoggerProvider loggerProvider) {
		//
	}

}
