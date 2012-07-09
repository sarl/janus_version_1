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
 * This class manages time as a sequence of time steps
 * of the same size.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ConstantKernelTimeManager implements KernelTimeManager {

	private float time;
	private final float stepDuration;

	/** Build a constant time manager starting at time 0.
	 * 
	 * @param stepDuration is the duration of one step (in milliseconds).
	 */
	public ConstantKernelTimeManager(float stepDuration) {
		this.stepDuration = stepDuration;
		this.time = 0f;
	}
	
	/** Build a constant time manager starting at the given time.
	 * 
	 * @param stepDuration is the duration of one step (in milliseconds).
	 * @param startTime is the start time in milliseconds.
	 */
	public ConstantKernelTimeManager(float stepDuration, float startTime) {
		this.stepDuration = stepDuration;
		this.time = startTime;
	}

	/** Replies the duration of one time step.
	 * 
	 * @return the duration of one time step.
	 */
	public double getTimeStepDuration() {
		return this.stepDuration;
	}
	
	/** Increment the time.
	 */
	public void increment() {
		this.time += this.stepDuration;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Date getCurrentDate() {
		return null;
	}

	/** {@inheritDoc}
	 */
	@Override
	public float getCurrentTime() {
		return this.time;
	}

	/** {@inheritDoc}
	 */
	@Override
	public float getCurrentTime(TimeUnit unit) {
		assert(unit!=null);
		switch(unit) {
		case DAYS:
		case HOURS:
			return this.time * 3.6f;
		case MINUTES:
			return this.time * 6e-2f;
		case SECONDS:
			return this.time * 1e-3f;
		case MILLISECONDS:
			return this.time;
		case MICROSECONDS:
			return this.time / 1e-3f;
		case NANOSECONDS:
			return this.time / 1e-6f;
		default:
		}
		throw new IllegalArgumentException();
	}

}