/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.simulation.boids.util;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.janusproject.kernel.address.AgentAddress;

/**
 * The boid representation in the environment, ie. the body of a boid.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PerceivedShadowBody extends PerceivedBoidBody {

	/** Relative position for the center of the shadow group.
	 */
	private final Vector2f groupPosition;
	
	/**
	 * @param group
	 * @param address
	 * @param globalPosition
	 * @param speed
	 * @param groupPosition
	 */
	public PerceivedShadowBody(
			Population group, AgentAddress address, Vector2f globalPosition, Vector2f speed, Vector2f groupPosition) {
		super(group, address, globalPosition, speed);
		this.groupPosition = groupPosition;
	}
	
	/** Replies the position of this body relatively to its group center.
	 * 
	 * @return the position from the group center.
	 */
	public Vector2f getPositionInShadowGroup() {
		return this.groupPosition;
	}
	
}
