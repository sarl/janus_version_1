/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.ecoresolution.event;


import java.util.EventListener;

/** Listener on EcoEntity events.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface EcoEntityListener extends EventListener {
	
	/** Invoked when the problem solving was started.
	 */
	public void problemSolvingStarted();

	/** Invoked when the problem was solved.
	 */
	public void problemSolved();

	/** Invoked when the goal of an entity has changed.
	 * 
	 * @param event
	 */
	public void goalChanged(GoalChangeEvent event);
	
	/** Invoked when the acquaintances of an entity has changed.
	 * 
	 * @param event
	 */
	public void acquaintanceChanged(AcquaintanceEvent event);

	/** Invoked when the attacks against an entity has changed.
	 * 
	 * @param event
	 */
	public void attackChanged(AttackEvent event);

	/** Invoked when the dependency of an entity has changed.
	 * 
	 * @param event
	 */
	public void dependencyChanged(DependencyEvent event);

}