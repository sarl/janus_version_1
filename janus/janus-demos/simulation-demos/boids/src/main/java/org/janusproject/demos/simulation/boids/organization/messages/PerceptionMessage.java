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

import java.util.HashMap;
import java.util.Map;

import org.janusproject.demos.simulation.boids.util.PerceivedBoidBody;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.message.AbstractContentMessage;

/**
 * Message containing perceptions for a boid.
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PerceptionMessage extends AbstractContentMessage<Map<AgentAddress,PerceivedBoidBody>> {

	private static final long serialVersionUID = -1164698611711945160L;
	
	private Map<AgentAddress,PerceivedBoidBody> otherBoids;

	/**
	 * @param boids is the collection of perceived boids.
	 */
	public PerceptionMessage(Map<AgentAddress,PerceivedBoidBody> boids) {
		this.otherBoids = new HashMap<AgentAddress,PerceivedBoidBody>(boids);
	}

	/** Replies the perceptions.
	 * 
	 * @return the perceptions.
	 */
	public Map<AgentAddress,PerceivedBoidBody> getOtherBoids() {
		return this.otherBoids;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<AgentAddress,PerceivedBoidBody> getContent() {
		return this.otherBoids;
	}
	
}