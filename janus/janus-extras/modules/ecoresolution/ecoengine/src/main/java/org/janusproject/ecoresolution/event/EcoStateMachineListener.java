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
package org.janusproject.ecoresolution.event;

import java.util.EventListener;

import org.janusproject.ecoresolution.sm.EcoState;

/** Listener on state machine for eco-resolution problem solving.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 * @see EcoState
 */
public interface EcoStateMachineListener extends EventListener {

	/** Invoked when state has changed.
	 *
	 * @param oldState is the old state of the state machine.
	 * @param newState is the new state of the state machine.
	 */
	public void stateChanged(EcoState oldState, EcoState newState);
	
}