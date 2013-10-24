/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.jaak.kernel;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.math.discrete.object2d.Shape2i;
import org.janusproject.jaak.envinterface.body.TurtleBodyFactory;
import org.janusproject.jaak.envinterface.channel.GridStateChannel;
import org.janusproject.jaak.envinterface.channel.GridStateChannelListener;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.JaakObject;
import org.janusproject.jaak.environment.model.JaakEnvironment;
import org.janusproject.jaak.environment.model.JaakEnvironmentListener;
import org.janusproject.jaak.spawner.JaakSpawner;
import org.janusproject.jaak.spawner.JaakWorldSpawner;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agent.AgentActivator;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.multicollection.MultiCollection;

/** Provide the core agent which is responsible of the Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype
class JaakKernelAgent extends Agent implements KernelListener, ChannelInteractable, 
		JaakEnvironmentListener, InfluenceReactionExecutionTokenOwner {
	
	private static final long serialVersionUID = -3198497220511869591L;

	/** Timeout used to force the execution of the environment behaviour (in ms).
	 */
	public static final long ENVIRONMENT_EXECUTION_TIMEOUT = 300000; // 5mn
	
	/** Timeout used to force the kernel to kill itself when no more turtle is running (in ms).
	 */
	public static final long IDDLE_KERNEL_TIMEOUT = 5000; // 5s

	/** Token used to schedule the influence-reaction process.
	 */
	protected final InfluenceReactionExecutionToken influenceReactionExecutionToken = new InfluenceReactionExecutionToken();
	

	private final TurtleActivator turtleActivator = new TurtleActivator();
	private final DefaultJaakTimeManager timeManager;
	private final JaakEnvironment environment;
	private final JaakSpawner[] spawners;
	private final JaakSpawner defaultSpawner;
	private final List<AgentAddress> removedAgents = new LinkedList<AgentAddress>();
	private final List<AgentAddress> addedAgents = new LinkedList<AgentAddress>();
	private final GridChannel gridChannel = new GridChannel();
	private long lastEnvironmentExecution;
	private final AtomicLong kernelKillableDate = new AtomicLong(-1);
	
	/**
	 * @param timeManager is the time manager to used in Jaak simulation.
	 * @param environment is the situated environment to use in Jaak simulation.
	 * @param spawner is the spawner to use to automatically spawn an entity.
	 */
	public JaakKernelAgent(DefaultJaakTimeManager timeManager, JaakEnvironment environment, JaakSpawner... spawner) {
		super(false);
		this.timeManager = timeManager;
		this.environment = environment;
		this.spawners = spawner;
		if (spawner==null || spawner.length==0) {
			this.defaultSpawner = new JaakWorldSpawner(this.environment);
		}
		else {
			this.defaultSpawner = null;
		}
	}
	
	/** Replies the environment.
	 * 
	 * @return the environment.
	 */
	protected JaakEnvironment getEnvironment() {
		return this.environment;
	}

	/** Replies the spawners.
	 * 
	 * @return the spawners.
	 */
	protected JaakSpawner[] getSpawners() {
		return this.spawners;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		Status status = super.activate(parameters);
		if (status.isSuccess()) {
			this.gridChannel.fireJaakStart();
			this.environment.addJaakEnvironmentListener(this);
			Kernel kernel = Kernels.get(getKernelContext().getKernelAgent());
			if (kernel!=null) kernel.addKernelListener(this);
			preStage();
			this.influenceReactionExecutionToken.moveToken(null, this.turtleActivator);
		}
		return status;
	}
	
	private void preStage() {
		if (this.spawners!=null) {
			for(JaakSpawner spawner : this.spawners) {
				spawner.spawn(
						getKernelContext().getKernelAgent(),
						this.environment.getTurtleBodyFactory(),
						this.timeManager);
			}

			synchronized(this.removedAgents) {
				Iterator<AgentAddress> iterator = this.removedAgents.iterator();
				assert(iterator!=null);
				AgentAddress adr;
				while (iterator.hasNext()) {
					adr = iterator.next();
					iterator.remove();
					this.environment.removeBodyFor(adr);
				}
			}

			synchronized(this.addedAgents) {
				Iterator<AgentAddress> iterator = this.addedAgents.iterator();
				assert(iterator!=null);
				AgentAddress kernelAdr = getKernelContext().getKernelAgent();
				TurtleBodyFactory factory = this.environment.getTurtleBodyFactory();
				Random rnd = new Random();
				AgentAddress adr;
				JaakSpawner spawner;
				while (iterator.hasNext()) {
					adr = iterator.next();
					iterator.remove();
					if (this.spawners!=null && this.spawners.length>0) {
						spawner = this.spawners[rnd.nextInt(this.spawners.length)];
					}
					else {
						spawner = this.defaultSpawner;
					}
					spawner.spawnBodyFor(
					            		  adr,
					            		  kernelAdr,
					            		  factory,
					            		  this.timeManager);
				}
			}
		}
		this.environment.runPreTurtles();
		this.lastEnvironmentExecution = System.currentTimeMillis();
	}
	
	private void postStage() {
		this.environment.runPostTurtles();
		this.timeManager.increment();
	}
	
	/** Replies if the environment behavior is runnable.
	 */
	private boolean isRunnableEnvironment() {
		if (this.environment.isAllInfluencesReceived())
			return true;
		long currentTime = System.currentTimeMillis();
		return ((currentTime-this.lastEnvironmentExecution)>ENVIRONMENT_EXECUTION_TIMEOUT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		Status status = super.live();
		if (status.isSuccess()) {
			if (this.influenceReactionExecutionToken.hasToken(this)) {
				try {
					if (isRunnableEnvironment()) {
						postStage();
						preStage();
					}
					
					// Kill the Jaak Kernel if no more turtle is on the grid.
					long killableDate = this.kernelKillableDate.get();
					if (killableDate>0l && ((System.currentTimeMillis()-killableDate)>IDDLE_KERNEL_TIMEOUT)) {
						killMe();
					}
				}
				finally {
					this.influenceReactionExecutionToken.moveToken(this, this.turtleActivator);
				}
			}
		}
				
		return status;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status end() {
		postStage();
		Kernel kernel = Kernels.get(getKernelContext().getKernelAgent());
		if (kernel!=null) kernel.removeKernelListener(this);
		this.environment.removeJaakEnvironmentListener(this);
		this.gridChannel.fireJaakEnd();
		return super.end();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void agentKilled(KernelEvent event) {
		AgentAddress adr = event.getAgent();
		assert(adr!=null);
		synchronized(this.removedAgents) {
			this.removedAgents.remove(adr);
			if (this.environment.getTurtleCount()==0
				&& this.kernelKillableDate.get()<=0) {
				this.kernelKillableDate.set(System.currentTimeMillis());
			}
			else {
				this.kernelKillableDate.set(0);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void agentLaunched(KernelEvent event) {
		AgentAddress adr = event.getAgent();
		assert(adr!=null);
		this.kernelKillableDate.set(0);
		if (!this.environment.hasBodyFor(adr)) {
			synchronized(this.addedAgents) {
				this.addedAgents.add(adr);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exceptionUncatched(Throwable error) {
		// Ignore this event
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void kernelAgentKilled(KernelEvent event) {
		// Kill me
		killMe();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void kernelAgentLaunched(KernelEvent event) {
		// Ignore this event
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Channel> C getChannel(Class<C> type, Object... channelParams) {
		assert(type!=null);
		if (GridStateChannel.class.equals(type)) {
			return type.cast(this.gridChannel);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<? extends Channel>> getSupportedChannels() {
		return Collections.<Class<? extends Channel>>singleton(GridStateChannel.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postAgentScheduling() {
		this.gridChannel.fireStateAvailable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preAgentScheduling() {
		//
	}
	
	/** Replies an agent activator which is suitable for turtle scheduling in this Jaak kernel.
	 * 
	 * @return an agent activator, never <code>null</code>.
	 */
	AgentActivator newTurtleActivator() {
		return this.turtleActivator;
	}
	
	/** Defines the channel to retreive the grid information.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class TurtleActivator extends AgentActivator implements InfluenceReactionExecutionTokenOwner {
		
		/**
		 */
		public TurtleActivator() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Status executeBehaviour(Iterator<? extends Agent> agents) {
			if (JaakKernelAgent.this.influenceReactionExecutionToken.hasToken(this)) { 
				try {
					return super.executeBehaviour(agents);
				}
				finally {
					JaakKernelAgent.this.influenceReactionExecutionToken.moveToken(
							this, JaakKernelAgent.this);
				}
			}
			return StatusFactory.ok(this);
		}
		
	}

	/** Defines the channel to retreive the grid information.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class GridChannel implements GridStateChannel {

		private final List<GridStateChannelListener> channelListeners = new LinkedList<GridStateChannelListener>();
		
		/**
		 */
		public GridChannel() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addGridStateChannelListener(GridStateChannelListener listener) {
			synchronized(this.channelListeners) {
				this.channelListeners.add(listener);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeGridStateChannelListener(GridStateChannelListener listener) {
			synchronized(this.channelListeners) {
				this.channelListeners.remove(listener);
			}
		}
		
		/** Notify listeners about the availability of the grid state.
		 */
		public void fireStateAvailable() {
			GridStateChannelListener[] list;
			synchronized(this.channelListeners) {
				list = new GridStateChannelListener[this.channelListeners.size()];
				this.channelListeners.toArray(list);
			}
			for(GridStateChannelListener listener : list) {
				listener.gridStateChanged();
			}
		}

		/** Notify listeners about the starting of the Jaak environment.
		 */
		public void fireJaakStart() {
			GridStateChannelListener[] list;
			synchronized(this.channelListeners) {
				list = new GridStateChannelListener[this.channelListeners.size()];
				this.channelListeners.toArray(list);
			}
			for(GridStateChannelListener listener : list) {
				listener.jaakStart();
			}
		}

		/** Notify listeners about the ending of the Jaak environment.
		 */
		public void fireJaakEnd() {
			GridStateChannelListener[] list;
			synchronized(this.channelListeners) {
				list = new GridStateChannelListener[this.channelListeners.size()];
				this.channelListeners.toArray(list);
			}
			for(GridStateChannelListener listener : list) {
				listener.jaakEnd();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getGridHeight() {
			return JaakKernelAgent.this.getEnvironment().getHeight();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getGridWidth() {
			return JaakKernelAgent.this.getEnvironment().getWidth();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Address getChannelOwner() {
			return JaakKernelAgent.this.getAddress();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsTurtle(int x, int y) {
			return JaakKernelAgent.this.getEnvironment().hasTurtle(x, y);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getSpeed(int x, int y) {
			return JaakKernelAgent.this.getEnvironment().getTurtleSpeed(x, y);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getOrientation(int x, int y) {
			return JaakKernelAgent.this.getEnvironment().getTurtleOrientation(x, y);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Vector2f getDirection(int x, int y) {
			return JaakKernelAgent.this.getEnvironment().getTurtleDirection(x, y);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T extends EnvironmentalObject> Iterable<T> getEnvironmentalObjects(
				int x, int y, Class<T> type) {
			return JaakKernelAgent.this.getEnvironment().getEnvironmentalObjects(x, y, type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<EnvironmentalObject> getEnvironmentalObjects(int x, int y) {
			return JaakKernelAgent.this.getEnvironment().getEnvironmentalObjects(x, y);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<JaakObject> getAllObjects(int x, int y) {
			MultiCollection<JaakObject> c = new MultiCollection<JaakObject>();
			c.addCollection(JaakKernelAgent.this.getEnvironment().getTurtles(x, y));
			c.addCollection(JaakKernelAgent.this.getEnvironment().getEnvironmentalObjects(x, y));
			return c;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Point2i[] getSpawningPositions() {
			JaakSpawner[] spawners = JaakKernelAgent.this.getSpawners();
			Point2i[] pts = new Point2i[spawners.length];
			for(int i=0; i<spawners.length; i++) {
				pts[i] = spawners[i].getReferenceSpawningPosition();
			}
			return pts;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Shape2i[] getSpawningLocations() {
			JaakSpawner[] spawners = JaakKernelAgent.this.getSpawners();
			Shape2i[] shapes = new Shape2i[spawners.length];
			for(int i=0; i<spawners.length; i++) {
				shapes[i] = spawners[i].toShape();
			}
			return shapes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getTurtleCount() {
			return JaakKernelAgent.this.getEnvironment().getTurtleCount();
		}

	}
	
}