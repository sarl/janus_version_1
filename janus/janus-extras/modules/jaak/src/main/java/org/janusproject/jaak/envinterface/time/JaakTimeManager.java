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
package org.janusproject.jaak.envinterface.time;

import java.util.concurrent.TimeUnit;

import org.janusproject.kernel.time.KernelTimeManager;

/** Time manager for the Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JaakTimeManager extends KernelTimeManager {

	/** Replies the duration of the last simulation step in seconds.
	 * 
	 * @return the duration of the last simulation step in seconds.
	 */
	public float getLastStepDuration();
	
	/** Replies the duration of the last simulation step in the given time unit.
	 * 
	 * @param unit is the time unit used to format the replied value.
	 * @return the duration of the last simulation step.
	 */
	public float getLastStepDuration(TimeUnit unit);
	
	/** Set the waiting duration at the end of each simulation step.
	 * This waiting duration permits to control how fast the simulation
	 * is running.
	 * 
	 * @param duration is the duration in ms.
	 */
	public void setWaitingDuration(long duration);

	/** Replies the waiting duration at the end of each simulation step.
	 * This waiting duration permits to control how fast the simulation
	 * is running.
	 * 
	 * @return the duration in ms.
	 */
	public long getWaitingDuration();

}