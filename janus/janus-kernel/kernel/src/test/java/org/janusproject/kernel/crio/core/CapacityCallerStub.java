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

import java.util.logging.Logger;

import org.janusproject.kernel.crio.capacity.CapacityCaller;
import org.janusproject.kernel.time.ConstantKernelTimeManager;
import org.janusproject.kernel.time.KernelTimeManager;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class CapacityCallerStub implements CapacityCaller {
	
	private final AgentAddressStub adr;
	private final ConstantKernelTimeManager tm = new ConstantKernelTimeManager(1);
	
	/**
	 */
	public CapacityCallerStub() {
		this.adr = new AgentAddressStub();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddressStub getAddress() {
		return this.adr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KernelTimeManager getTimeManager() {
		return this.tm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger getLogger() {
		return Logger.getLogger(getClass().getCanonicalName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.adr.getName();
	}
	
	
	
}