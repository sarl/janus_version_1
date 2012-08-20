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
package org.janusproject.jaak.kernel;

import java.util.concurrent.TimeUnit;

import org.janusproject.jaak.envinterface.time.JaakTimeManager;
import org.janusproject.kernel.time.ConstantKernelTimeManager;

/** Time manager for Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class DefaultJaakTimeManager extends ConstantKernelTimeManager implements JaakTimeManager {

	/** Define the default duration of a simulation step.
	 */
	public static final float STEP_DURATION = 1.f;
	
	private long waitingDuration = 0;
	
	/**
	 */
	public DefaultJaakTimeManager() {
		super(STEP_DURATION);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void increment() {
		if (this.waitingDuration>0) {
			try {
				Thread.sleep(this.waitingDuration);
			}
			catch (InterruptedException e) {
				//
			}
		}
		super.increment();
	}



	/** Replies the duration of the last simulation step in seconds.
	 * 
	 * @return the duration of the last simulation step in seconds.
	 */
	@Override
	public float getLastStepDuration() {
		return getLastStepDuration(TimeUnit.SECONDS);
	}
	
	/** Replies the duration of the last simulation step in the given time unit.
	 * 
	 * @param unit is the time unit used to format the replied value.
	 * @return the duration of the last simulation step.
	 */
	@Override
	public float getLastStepDuration(TimeUnit unit) {
		assert(unit!=null);
		float duration = (float)getTimeStepDuration();
		switch(unit) {
		case DAYS:
		case HOURS:
			return duration * 3.6f;
		case MINUTES:
			return duration * 6e-2f;
		case SECONDS:
			return duration * 1e-3f;
		case MILLISECONDS:
			return duration;
		case MICROSECONDS:
			return duration / 1e-3f;
		case NANOSECONDS:
			return duration / 1e-6f;
		default:
		}
		throw new IllegalArgumentException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized long getWaitingDuration() {
		return this.waitingDuration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setWaitingDuration(long duration) {
		if (duration>0) {
			this.waitingDuration = duration;
		}
		else {
			this.waitingDuration = 0;
		}
	}
}