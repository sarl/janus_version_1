/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010, 2012 Janus Core Developers
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
package org.janusproject.demos.simulation.boids.agent;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.boids.organization.Boid;
import org.janusproject.demos.simulation.boids.organization.BoidOrganization;
import org.janusproject.demos.simulation.boids.util.Population;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * A boid agent.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={}
)
public class BoidAgent extends Agent {
	
	private static final long serialVersionUID = -2346639047149740958L;
	
	private final Population p;
	private final Vector2f initialPosition;
	private final Vector2f initialSpeed;

	/**
	 * @param p - la population auquel appartient ce boid
	 * @param initialPosition - sa position initiale dans l'environnement
	 * @param initialVitesse - sa vitesse initiale
	 */
	public BoidAgent(Population p, Vector2f initialPosition, Vector2f initialVitesse) {
		super(true);
		this.p = p;
		this.initialPosition = initialPosition;
		this.initialSpeed = initialVitesse;
	}
	
	/**
	 * Parameters:
	 */
	@Override
	public Status activate(Object... parameters) {
		print(Locale.getString(BoidAgent.class, "IM_ALIVE", getAddress())); //$NON-NLS-1$

		GroupAddress boidGA = getOrCreateGroup(BoidOrganization.class);
		
		//Request the role and intialize it using the three last parameters that should correspond to the interface of one of the init methods of the Boid role
		if (requestRole(Boid.class, boidGA, this.p, this.initialPosition, this.initialSpeed)!=null) {
			return StatusFactory.ok(this);
		}
		
		return StatusFactory.cancel(this);
	}

	@Override
	public Status end() {
		print(Locale.getString(BoidAgent.class,"AGENT_IS_DEAD", getAddress())); //$NON-NLS-1$
		return StatusFactory.ok(this);
	}
	
}
