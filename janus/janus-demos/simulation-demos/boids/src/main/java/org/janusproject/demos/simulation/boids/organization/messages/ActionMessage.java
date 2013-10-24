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
import org.janusproject.kernel.message.AbstractContentMessage;

/**
 * Message containing the forces computed by a boid.
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ActionMessage extends AbstractContentMessage<Vector2f> {

	private static final long serialVersionUID = -8596108743195909605L;
	
	private final Vector2f force;
	
	/**
	 * @param iforce is the computed force
	 */
	public ActionMessage(Vector2f iforce) {
		this.force = iforce;
	}

	/** Replies the force.
	 * @return the force.
	 */
	public Vector2f getForce() {
		return this.force;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vector2f getContent() {
		return this.force;
	}
}
