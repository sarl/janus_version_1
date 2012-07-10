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

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.janusproject.demos.simulation.preypredator.capacity.RefreshViewerCapacity;
import org.janusproject.demos.simulation.preypredator.gui.GUIWorldState;
import org.janusproject.demos.simulation.preypredator.gui.WorldState;
import org.janusproject.demos.simulation.preypredator.gui.WorldStateChangeListener;
import org.janusproject.demos.simulation.preypredator.organization.Terrain;
import org.janusproject.demos.simulation.preypredator.organization.WildWorld;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.event.ListenerCollection;

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
public class GroundAgent extends Agent implements GUIWorldState {

	private static final long serialVersionUID = 6872690216031788260L;
	
	private final int awaitingAgents;
	private final ListenerCollection<WorldStateChangeListener> listeners = new ListenerCollection<WorldStateChangeListener>();
	private volatile boolean stop = false;
	
	/**
	 * @param width is the width of the world.
	 * @param height is the height of the world.
	 * @param awaitingAgents number of prey and predators.
	 */
	public GroundAgent(int width, int height, int awaitingAgents) {
		this.awaitingAgents = awaitingAgents;
		Memory memory = getMemory();
		assert(memory!=null);
		memory.putMemorizedData(Terrain.WORLD_STATE, new AgentAddress[height][width]);
		memory.putMemorizedData(Terrain.WORLD_WIDTH, width);
		memory.putMemorizedData(Terrain.WORLD_HEIGHT, height);
		memory.putMemorizedData(Terrain.POSITIONS, new TreeMap<AgentAddress,WorldState>());
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
		getCapacityContainer().addCapacity(new RefreshViewerCapacityImplementation());
		
		GroupAddress group = getOrCreateGroup(WildWorld.class);
		requestRole(Terrain.class, group, this.awaitingAgents);
		return StatusFactory.ok(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		Status status = super.live();
		if (this.stop) {
			leaveAllRoles();
			this.stop = false;
		}
		return status;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<AgentAddress, WorldState> getLastPositions() {
		Memory memory = getMemory();
		assert(memory!=null);
		Map<AgentAddress,WorldState> m = (Map<AgentAddress,WorldState>)memory.getMemorizedData(Terrain.POSITIONS);
		Map<AgentAddress,WorldState> r = new TreeMap<AgentAddress, WorldState>();
		for(Entry<AgentAddress,WorldState> entry : m.entrySet()) {
			r.put(entry.getKey(), entry.getValue().clone());
		}
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress getPrey() {
		Memory memory = getMemory();
		assert(memory!=null);
		return memory.getMemorizedData(Terrain.PREY_ID, AgentAddress.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getWorldHeight() {
		Memory memory = getMemory();
		assert(memory!=null);
		return memory.getMemorizedData(Terrain.WORLD_HEIGHT, Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getWorldWidth() {
		Memory memory = getMemory();
		assert(memory!=null);
		return memory.getMemorizedData(Terrain.WORLD_WIDTH, Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopGame() {
		this.stop = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addWorldStateChangeListener(WorldStateChangeListener listener) {
		this.listeners.add(WorldStateChangeListener.class, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeWorldStateChangeListener(WorldStateChangeListener listener) {
		this.listeners.remove(WorldStateChangeListener.class, listener);
	}
	
	/** Replies if the prey was catched.
	 * 
	 * @return <code>true</code> if catched, otherwise <code>false</code>
	 */
	public boolean isPreyCatched() {
		Memory memory = getMemory();
		assert(memory!=null);
		Boolean catched = memory.getMemorizedData(Terrain.IS_PREY_CATCHED, Boolean.class);
		return catched!=null && catched.booleanValue();
	}
	
	/** Update viewer.
	 */
	protected void fireStateChanged() {
		for(WorldStateChangeListener listener : this.listeners.getListeners(WorldStateChangeListener.class)) {
			listener.stateChanged();
		}
	}

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
	private class RefreshViewerCapacityImplementation extends CapacityImplementation implements RefreshViewerCapacity {

		/**
		 */
		public RefreshViewerCapacityImplementation() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void call(CapacityContext call) throws Exception {
			GroundAgent.this.fireStateChanged();
			Thread.sleep(1000);
		}
		
	}
	
}