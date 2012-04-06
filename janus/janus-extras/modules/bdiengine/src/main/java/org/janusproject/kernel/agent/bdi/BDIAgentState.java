/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.kernel.agent.bdi;

/**
 * This enumeration lists the different states of a BDI Agent.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum BDIAgentState {
	/**
	 * State during which the agent wait for a goal.
	 */
	NO_GOAL_SELECTED,
	/**
	 * The agent has a goal to reach but is seeking for a plan.
	 */
	NO_PLAN_SELECTED,
	/**
	 * The agent is executing a plan to reach a goal.
	 */
	PLAN_IN_PROGRESS,
	/**
	 * The plan execution has failed, therefore the agent has to reason to define his next state.
	 */
	PLAN_FAILED,
	/**
	 * The plan execution has succeed.
	 */
	PLAN_SUCCESSFUL;
}
