/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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
package org.janusproject.kernel.condition;

import java.lang.ref.WeakReference;
import java.util.Date;

import org.janusproject.kernel.time.KernelTimeManager;

/** Provide time-based parameters for a Condition. 
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class TimeConditionParameterProvider {
	
	private final WeakReference<KernelTimeManager> timeManager;
	
	/**
	 * @param timeManager is the time manager to use to provide the parameters.
	 */
	public TimeConditionParameterProvider(KernelTimeManager timeManager) {
		this.timeManager = new WeakReference<KernelTimeManager>(timeManager);
	}

	/** Replies the current time.
	 * 
	 * @return the current time.
	 */
	public float getCurrentTime() {
		return this.timeManager.get().getCurrentTime();
	}
	
	/** Replies the current date.
	 * 
	 * @return the current date.
	 */
	public Date getCurrentDate() {
		return this.timeManager.get().getCurrentDate();
	}
	
}
