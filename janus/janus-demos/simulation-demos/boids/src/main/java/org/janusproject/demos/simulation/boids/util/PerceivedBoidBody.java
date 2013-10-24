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
public class PerceivedBoidBody {
	/**
	 * Boid position
	 */
	private final Vector2f position;
	/**
	 * Address of the body owner.
	 */
	private final AgentAddress address;
	/**
	 * Speed of the boid.
	 */
	private final Vector2f orientation;
	/**
	 * Boid acceleration
	 */
	private final Vector2f acceleration;
	/**
	 * Population in which the boid is.
	 */
	private final Population group;
	
	/**
	 * @param igroupe
	 * @param iaddress
	 * @param iposition
	 * @param ivitesse
	 */
	public PerceivedBoidBody(Population igroupe, AgentAddress iaddress, Vector2f iposition, Vector2f ivitesse) {
		this.position = new Vector2f(iposition);
		this.address = iaddress;
		this.orientation = new Vector2f(ivitesse);
		this.acceleration = new Vector2f();
		this.group = igroupe;
	}

	/**
	 * Replies the owner of this body.
	 * 
	 * @return the owner of this body.
	 */
	public AgentAddress getAddress() {
		return this.address;
	}
	
	/**
	 * Replies the position of this body.
	 * 
	 * @return the position of this body.
	 */
	public Vector2f getPosition() {
		return this.position;
	}

	/**
	 * Replies the orientation of this body.
	 * 
	 * @return the orientation of this body.
	 */
	public Vector2f getOrientation() {
		return this.orientation;
	}

	/**
	 * Replies the group of this body.
	 * 
	 * @return the group of this body.
	 */
	public Population getGroup() {
		return this.group;
	}

	/**
	 * Replies the acceleration of this body.
	 * 
	 * @return the acceleration of this body.
	 */
	public Vector2f getAcceleration() {
		return this.acceleration;
	}

	/**
	 * Set the position of this body.
	 * 
	 * @param newPosition
	 */
	public void setPosition(Vector2f newPosition) {
		assert(newPosition!=null);
		this.position.set(newPosition);
	}

	/**
	 * Set the orientation of this body.
	 * 
	 * @param newSpeed
	 */
	public void setOrientation(Vector2f newSpeed) {
		assert(newSpeed!=null);
		this.orientation.set(newSpeed);
	}

	/**
	 * Set the acceleration of this body.
	 * 
	 * @param newAcceleration
	 */
	public void setAcceleration(Vector2f newAcceleration) {
		assert(newAcceleration!=null);
		this.acceleration.set(newAcceleration);
	}
	
}
