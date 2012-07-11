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
package org.janusproject.jaak.environment.endogenousengine;

import java.util.Collection;

import org.janusproject.jaak.envinterface.influence.Influence;
import org.janusproject.jaak.environment.GridModel;
import org.janusproject.kernel.time.KernelTimeManager;

/** This interface defines the endogenous rules of the environment.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface EnvironmentEndogenousEngine {

	/** Compute and Reply the influences generated by the endogenous engine.
	 * 
	 * @param grid is the grid on which the computations must be done.
	 * @param timeManager is the time manager used to run Jaak.
	 * @return the influences generated by the endogenous engine.
	 */
	public Collection<Influence> computeInfluences(GridModel grid, KernelTimeManager timeManager);

}