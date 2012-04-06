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
 * Represent an agent's belief.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BDIBelief {
	/**
	 * object belief
	 */
	private Object belief;
	
	/**
	 * belief's type
	 * A BDIBeliefType already exists but the user can add his own types.
	 * Therefore the type is represented by an Object.
	 */
	private Object type;
	
	/**
	 * Constructor.
	 * @param belief is the belief's object
	 * @param type is the blief's type
	 */
	public BDIBelief(Object belief, Object type){
		this.belief = belief;
		this.type = type;
	}
	
	/**
	 * Returns the belief object.
	 * @return the belief
	 */
	public Object getBelief(){
		return this.belief;
	}
	
	/**
	 * Returns the belief's type.
	 * @return the type of belief
	 */
	public Object getType(){
		return this.type;
	}
}
