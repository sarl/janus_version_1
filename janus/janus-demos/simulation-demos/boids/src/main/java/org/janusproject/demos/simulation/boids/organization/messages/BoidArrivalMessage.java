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
package org.janusproject.demos.simulation.boids.organization.messages;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.janusproject.demos.simulation.boids.util.Population;
import org.janusproject.kernel.message.AbstractContentMessage;

/**
 * Message embedding initialiation parameters of a boid.
 * This message is sent by boids to environment.
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BoidArrivalMessage extends AbstractContentMessage<Object[]> {

	private static final long serialVersionUID = 2426090721149754248L;
	
	private final Population population; 
	private final Vector2f initialPosition; 
	private final Vector2f initialSpeed;
	
	/**
	 * @param group is the group of the boid.
	 * @param position is the initial position of the boid when joining the group.
	 * @param speed is the initial speed of the boid when joining the group.
	 */
	public BoidArrivalMessage(Population group, Vector2f position, Vector2f speed) {
		this.population = group;
		this.initialPosition = position;
		this.initialSpeed = speed;
	}

	/** Replies the group for a boid.
	 * 
	 * @return the group for a boid.
	 */
	public Population getPopulation() {
		return this.population;
	}

	/** Replies the initial position for a boid.
	 * 
	 * @return the initial position for a boid.
	 */
	public Vector2f getInitialPosition() {
		return this.initialPosition;
	}

	/** Replies the initial speed for a boid.
	 * 
	 * @return the initial speed for a boid.
	 */
	public Vector2f getInitialSpeed() {
		return this.initialSpeed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getContent() {
		return new Object[]{ this.population, this.initialPosition, this.initialSpeed};
	}
		
}
