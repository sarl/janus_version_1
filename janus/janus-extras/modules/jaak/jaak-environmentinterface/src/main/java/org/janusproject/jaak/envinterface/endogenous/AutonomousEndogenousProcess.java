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
package org.janusproject.jaak.envinterface.endogenous;

import org.janusproject.jaak.envinterface.influence.Influence;

/** This interface defines an endogenous environmental process.
 * <p>
 * An endogenous process is a process which do something
 * inside the environment. An autonomous endogenous process
 * does not provide a side effect in environment but
 * does not output any influence.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface AutonomousEndogenousProcess {

	/** Run the endogenous process.
	 * 
	 * @param currentTime is the current simulation time
	 * @param simulationStepDuration is the duration of the current simulation step.
	 * @return the influence created by the autonomous process, or <code>null</code> if
	 * no external influence was generated.
	 */
	public Influence runAutonomousEndogenousProcess(float currentTime, float simulationStepDuration);

}