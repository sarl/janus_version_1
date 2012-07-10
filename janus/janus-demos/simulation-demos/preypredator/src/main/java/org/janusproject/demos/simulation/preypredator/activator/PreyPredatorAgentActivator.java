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

package org.janusproject.demos.simulation.preypredator.activator;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.janusproject.demos.simulation.preypredator.organization.Terrain;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivator;
import org.janusproject.kernel.schedule.ActivationStage;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.directaccess.SafeIterator;

/** 
 * Activator of animats in a prey predator game.
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
public class PreyPredatorAgentActivator extends AgentActivator {

	/**
	 */
	public PreyPredatorAgentActivator() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Iterator<? extends Agent> getExecutionPolicy(
			ActivationStage stage, Collection<? extends Agent> candidates) {
		if (stage!=ActivationStage.DESTRUCTION) {
			List<Agent> terrains = new LinkedList<Agent>();
			List<Agent> animats = new LinkedList<Agent>();
			
			// Search for terrain
			for(Agent animat : candidates) {
				if (animat.isPlayingRole(Terrain.class)) {
					terrains.add(animat);
				}
				else {
					animats.add(animat);
				}
			}
	
			animats.addAll(terrains);
			terrains.clear();
			
			return animats.iterator();
		}
		
		return candidates.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SafeIterator<Agent> getExecutionPolicy(ActivationStage stage,
			DirectAccessCollection<Agent> candidates) {
		if (stage!=ActivationStage.DESTRUCTION) {
			List<Agent> terrains = new LinkedList<Agent>();
			List<Agent> animats = new LinkedList<Agent>();
			
			// Search for terrain
			Agent animat;
			SafeIterator<Agent> iterator = candidates.iterator();
			try {
				while (iterator.hasNext()) {
					animat = iterator.next();
					if (animat.isPlayedRole(Terrain.class)) {
						terrains.add(animat);
					}
					else {
						animats.add(animat);
					}
				}
			}
			finally {
				iterator.release();
			}
	
			animats.addAll(terrains);
			terrains.clear();
			
			return new SafeIterator<Agent>(animats.iterator());
		}
		
		return new SafeIterator<Agent>(candidates.iterator());
	}
	
}