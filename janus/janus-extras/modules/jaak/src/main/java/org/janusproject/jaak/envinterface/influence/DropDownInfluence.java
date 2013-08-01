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
package org.janusproject.jaak.envinterface.influence;

import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;

/** This class defines an influence to drop off an object
 * inside the environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DropDownInfluence extends Influence {

	private final EnvironmentalObject object;

	/**
	 * @param emitter is the emitter of the influence.
	 * @param object is the environmental object to drop off.
	 */
	public DropDownInfluence(TurtleBody emitter, EnvironmentalObject object) {
		super(emitter);
		this.object = object;
	}
	
	/** Replies the environmental object which may be dropped off.
	 * 
	 * @return the environmental object which may be dropped off.
	 */
	public EnvironmentalObject getDropOffObject() {
		return this.object;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(getEmitter().getTurtleId().toString());
		buffer.append(": drop "); //$NON-NLS-1$
		buffer.append(this.object==null ? null : this.object.toString());
		return buffer.toString();
	}
	
}