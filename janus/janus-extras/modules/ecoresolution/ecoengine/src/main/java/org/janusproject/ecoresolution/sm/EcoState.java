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
package org.janusproject.ecoresolution.sm;

/** States of the entities in eco-resolution aprroach:
 * <p><ul>
 * <li>S: Satisfaction or Fulfillment</li>
 * <li>SR: Satisfaction or fulfillment Research</li>
 * <li>FR: Flee Research</li>
 * <li>F: Flee</li>
 * </ul>
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum EcoState {

	/** Entity is under initialization.
	 */
	INITIALIZING,

	/** Entity has been initialized and is waiting for problem solving start.
	 */
	INITIALIZED,

	/** Entity goals are reached.
	 */
	SATISFACTED,
	
	/** Research of the satisfaction state.
	 */
	SATISFACTING,
	
	/** Research of escape.
	 */
	ESCAPING,
	
	/** Escaping proceeded.
	 */
	ESCAPED;

	/** Replies if the state is a terminal state.
	 * 
	 * @return <code>true</code> if the state is a terminal state,
	 * otherwise <code>false</code>.
	 */
	public boolean isTerminalState() {
		return this == SATISFACTED;
	}

	/** Replies if the state corresponds to the initialization states before
	 * the problem solving has been started.
	 * 
	 * @return <code>true</code> if the state is an initialization state,
	 * otherwise <code>false</code>.
	 */
	public boolean isInitializationState() {
		return this == INITIALIZING || this == INITIALIZED;
	}

}