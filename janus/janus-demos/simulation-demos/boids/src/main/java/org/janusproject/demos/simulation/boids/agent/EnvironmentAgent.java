/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011, 2012 Janus Core Developers
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.boids.capacity.EnvironmentRefreshCapacity;
import org.janusproject.demos.simulation.boids.capacity.RetreiveBoidsCapacity;
import org.janusproject.demos.simulation.boids.organization.BoidOrganization;
import org.janusproject.demos.simulation.boids.organization.Environment;
import org.janusproject.demos.simulation.boids.util.BodyStateProvider;
import org.janusproject.demos.simulation.boids.util.EnvironmentGUI;
import org.janusproject.demos.simulation.boids.util.PerceivedBoidBody;
import org.janusproject.demos.simulation.boids.util.Population;
import org.janusproject.demos.simulation.boids.util.Settings;
import org.janusproject.demos.simulation.boids.util.ShadowGroup;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={}
)
public class EnvironmentAgent extends Agent
implements BodyStateProvider {

	private static final long serialVersionUID = -897884179261528100L;

	/**
	 * This map contains the whole population of boids.
	 * It permits to retreive boid's body from its address.
	 */
	protected final Map<AgentAddress,PerceivedBoidBody> boids;
	
	/**
	 * Contains all existing populations.
	 */
	protected final Set<Population> expectedPopulations = new HashSet<Population>();

	/** GUI.
	 */
	protected final EnvironmentGUI myGUI;
	
	private ShadowGroup shadowGroup = null;
	

	/**
	 * @param expectedPopulations is the list of populations to display in GUI.
	 */
	public EnvironmentAgent(Population... expectedPopulations) {
		super(true);
		this.boids = new ConcurrentHashMap<AgentAddress,PerceivedBoidBody>();
		this.expectedPopulations.addAll(Arrays.asList(expectedPopulations));
		
		this.myGUI = new EnvironmentGUI(
				Settings.ENVIRONMENT_DEMI_WIDTH*2,
				Settings.ENVIRONMENT_DEMI_WIDTH*2,
				this);
		
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new RetreiveBoidsCapacityImplementation());
		cc.addCapacity(new EnvironmentRefreshCapacityImplementation());
	}
	
	/** Replies the GUI.
	 * @return the GUI.
	 */
	public EnvironmentGUI getGUI() {
		return this.myGUI;
	}
	
	@Override
	public Status activate(Object... parameters) {
		print(Locale.getString(EnvironmentAgent.class, "IM_ALIVE")); //$NON-NLS-1$

		GroupAddress boidGA = getOrCreateGroup(BoidOrganization.class);
		
		if (requestRole(Environment.class, boidGA)!=null) {
			this.myGUI.getTopLevelAncestor().setVisible(true);
			return StatusFactory.ok(this);
		}
		
		return StatusFactory.cancel(this);
	}
	
	/** Replies the count of shadows.
	 * 
	 * @return the count of shadows.
	 */
	public int getCurrentShadowCount() {
		if (this.shadowGroup!=null) {
			return this.shadowGroup.size();
		}
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		Population pop = this.myGUI.getSelectedPopulation();
		if (pop!=null) {
			Vector2f pos = this.myGUI.getUserPosition();
			Vector2f speed = this.myGUI.getUserDirection();
			if (this.shadowGroup==null)
				this.shadowGroup = new ShadowGroup(
						Settings.SHADOW_ENTITY_COUNT,
						pos, speed,
						this.boids,
						pop);
			else
				this.shadowGroup.setGroupPosition(pos, speed, this.boids);
		}
		else if (this.shadowGroup!=null){
			this.shadowGroup.clear(this.boids);
			this.shadowGroup = null;
		}

		Status s = super.live();				
		this.myGUI.repaint();
		return s;
	}
	
	@Override
	public Status end() {
		this.myGUI.getTopLevelAncestor().setVisible(false);
		print(Locale.getString(BoidAgent.class,"AGENT_IS_DEAD")); //$NON-NLS-1$
		return StatusFactory.ok(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Iterator<PerceivedBoidBody> iterator() {
		Collection<PerceivedBoidBody> bodies = new ArrayList<PerceivedBoidBody>(this.boids.values());
		return bodies.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<Population> populations() {
		return this.expectedPopulations;
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RetreiveBoidsCapacityImplementation extends CapacityImplementation
	implements RetreiveBoidsCapacity {

		/**
		 */
		public RetreiveBoidsCapacityImplementation() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void call(CapacityContext call) throws Exception {
			call.setOutputValues(
					EnvironmentAgent.this.boids,
					getCurrentShadowCount());
		}
		
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class EnvironmentRefreshCapacityImplementation extends CapacityImplementation
	implements EnvironmentRefreshCapacity {

		/**
		 */
		public EnvironmentRefreshCapacityImplementation() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void call(CapacityContext call) throws Exception {
			EnvironmentAgent.this.myGUI.repaint();
		}
		
	}

}
