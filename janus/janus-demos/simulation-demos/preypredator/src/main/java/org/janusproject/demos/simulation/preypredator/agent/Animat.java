/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2009 Stephane GALLAND
 * Copyright (C) 2010 Janus Core Developers
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

package org.janusproject.demos.simulation.preypredator.agent;

import org.janusproject.demos.simulation.preypredator.organization.Predator;
import org.janusproject.demos.simulation.preypredator.organization.Prey;
import org.janusproject.demos.simulation.preypredator.organization.WildWorld;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** 
 * Animat is an agent which is an animal in a virtual world.
 * <p>
 * Copied from <a href="http://www.arakhne.org/tinymas/index.html">TinyMAS Platform Demos</a>
 * and adapted for Janus platform.
 * <p>
 * Thanks to Julia Nikolaeva, aka. <a href="mailto:flameia@zerobias.com">Flameia</a>, for the icons.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={}
)
public class Animat extends Agent {

	private static final long serialVersionUID = 6140514824090238641L;
	
	private final boolean isPrey;
	
	
	/**
	 * @param isPrey indicates if the animat may be a prey.
	 */
	public Animat(boolean isPrey) {
		this.isPrey = isPrey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getAddress().getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		GroupAddress group = getOrCreateGroup(WildWorld.class);
		if (this.isPrey) {
			requestRole(Prey.class, group);
		}
		else {
			requestRole(Predator.class, group);
		}
		return StatusFactory.ok(this);
	}

}