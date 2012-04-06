/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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

/**
 * Defines an element which could be activated by an activator.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Activable {
	
	/** Initialize the activable object.
	 * <p>
	 * This function is invoked during the {@link ActivationStage#INITIALIZATION INITIALIZATION stage}
	 * of the execution process.
	 * <p>
	 * Values and types of <var>parameters</var> depends on the type of this activable.
	 * 
	 * @param parameters are a set of parameters given by the activable creator.
	 * @return the status of the initialization stage.
	 */
	public Status activate(Object... parameters);

	/** Run once time the activable's behaviour.
	 * <p>
	 * This function is invoked during the {@link ActivationStage#LIVE LIVE stage}
	 * of the execution process.
	 * <p>
	 * This function must return to avoid Janus dead lock.
	 * @return the status of the live stage.
	 */
	public Status live();
	
	/** Destroy the activable object.
	 * <p>
	 * This function is invoked during the {@link ActivationStage#DESTRUCTION DESTRUCTION stage}
	 * of the execution process.
	 * @return the status of the destructionstage.
	 */
	public Status end();

}
