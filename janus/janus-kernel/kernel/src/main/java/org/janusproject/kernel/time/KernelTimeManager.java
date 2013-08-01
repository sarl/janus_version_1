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
 * This interface permits to retreive time information.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface KernelTimeManager {
	
	/** Replies the current time as a <code>Date</code>.
	 * 
	 * @return the current time, or <code>null</code> if
	 * the time manager is incompatible with <code>Date</code>
	 */
	public Date getCurrentDate();

	/** Replies the current time in milliseconds.
	 * 
	 * @return the current time in milliseconds.
	 */
	public float getCurrentTime();

	/** Replies the current time in the given time unit.
	 * 
	 * @param unit is the time unit to use for replied value.
	 * @return the current time.
	 */
	public float getCurrentTime(TimeUnit unit);

}