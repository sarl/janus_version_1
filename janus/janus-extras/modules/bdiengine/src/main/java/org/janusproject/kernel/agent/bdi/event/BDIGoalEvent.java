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
package org.janusproject.kernel.agent.bdi.event;

import org.janusproject.kernel.agent.bdi.BDIGoal;

/** Describes a BDI event that is containing a goal.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BDIGoalEvent extends BDIEvent{

	private static final long serialVersionUID = 6739286612897886659L;
	
	/**
	 * Corresponding goal
	 */
	private BDIGoal goal;
	
	/**
	 * Event's type
	 */
	private BDIGoalEventType type;
	
	/**
	 * Create a goal event
	 * @param goal is the associated goal
	 * @param type is the event's type
	 */
	public BDIGoalEvent(BDIGoal goal, BDIGoalEventType type) {
		super(goal);
		this.goal = goal;
		this.type = type;
	}
	
	/**
	 * Get the associated goal.
	 * @return the associated goal
	 */
	public BDIGoal getGoal() {
		return this.goal;
	}
	
	/**
	 * Get the event's type.
	 * @return the event's type
	 */
	public BDIGoalEventType getType() {
		return this.type;
	}
}
