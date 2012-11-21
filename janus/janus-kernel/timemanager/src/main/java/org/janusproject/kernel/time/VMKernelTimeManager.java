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
package org.janusproject.kernel.time;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class retreives time according to how the time
 * on the virtual machine evolves.
 * The time in the time manager is initialized with {@code 0}.
 * And the time evolves at the same rate as the current
 * operating system clock. 
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class VMKernelTimeManager implements KernelTimeManager {

	private final long initialTime;
	
	/**
	 */
	public VMKernelTimeManager() {
		this.initialTime = System.nanoTime();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	private double getCurrentTimeAsMillis() {
		return (System.nanoTime() - this.initialTime) * 1e-6;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public float getCurrentTime() {
		return (float) getCurrentTimeAsMillis();
	}

	/** {@inheritDoc}
	 */
	@Override
	public float getCurrentTime(TimeUnit unit) {
		assert(unit!=null);
		return unit.convert((long)getCurrentTimeAsMillis(), TimeUnit.MILLISECONDS);
	}

}