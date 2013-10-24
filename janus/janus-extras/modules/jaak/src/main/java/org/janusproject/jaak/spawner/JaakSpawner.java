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
package org.janusproject.jaak.spawner;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.math.discrete.object2d.Shape2i;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.body.TurtleBodyFactory;
import org.janusproject.jaak.envinterface.frustum.TurtleFrustum;
import org.janusproject.jaak.turtle.Turtle;
import org.janusproject.jaak.turtle.TurtleBindingChannel;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.time.KernelTimeManager;

/** Provide implementation for a turtle spawner in Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class JaakSpawner {

	/**
	 * Is the number of retries to compute the position while
	 * the cell at the given position is not free.
	 */
	public static final int FREE_POSITION_COMPUTATION_RETRIES = 10;
	
	/**
	 */
	public JaakSpawner() {
		//
	}
	
	/** Spawn only a turtle body in environment and bind it with the
	 * turtle with the given identifier.
	 * <p>
	 * It is assumed that the turtle was already launched, but never
	 * binded with Jaak environment.
	 * 
	 * @param turtleId is the identifier of the agent for which a body should be spawn.
	 * @param kernelAddress is the address of the kernel on which the spawned agent may be run.
	 * @param bodyFactory is the body factory to use.
	 * @param timeManager is the time manager used by the Jaak simulation.
	 */
	public final void spawnBodyFor(
			AgentAddress turtleId,
			AgentAddress kernelAddress,
			TurtleBodyFactory bodyFactory,
			KernelTimeManager timeManager) {
		assert(turtleId!=null);
		assert(kernelAddress!=null);
		assert(bodyFactory!=null);

		TurtleBodyFactory wrappedFactory = new SpawnedBodyFactory(
				bodyFactory,
				computeSpawnedTurtleOrientation(timeManager));

		TurtleBindingChannel channel = Kernels.get(kernelAddress).getChannelManager().getChannel(
				turtleId, TurtleBindingChannel.class);
		if (channel!=null) {
			TurtleBody body = channel.bindBody(wrappedFactory);
			if (body!=null) {
				turtleSpawned(turtleId, body, timeManager);
			}
		}
	}

	/** Spawn turtles and its body in environment.
	 * <p>
	 * The spawned turtle is assumed to be a light agent, according to Janus specifications.
	 * 
	 * @param kernelAddress is the address of the kernel on which the spawned agent may be run.
	 * @param bodyFactory is the body factory to use.
	 * @param timeManager is the time manager used by the Jaak simulation.
	 */
	public final void spawn(
			AgentAddress kernelAddress,
			TurtleBodyFactory bodyFactory,
			KernelTimeManager timeManager) {
		assert(kernelAddress!=null);
		assert(bodyFactory!=null);
		assert(timeManager!=null);
		
		Kernel kernel = Kernels.get(kernelAddress);
		assert(kernel!=null);
		
		if (isSpawnable(timeManager)) {
			TurtleBodyFactory wrappedFactory = new SpawnedBodyFactory(
					bodyFactory,
					computeSpawnedTurtleOrientation(timeManager));
			Turtle turtle = createTurtle(timeManager);
			if (turtle!=null) {
				TurtleBody body = Turtle.bindTurtleBody(turtle, wrappedFactory);
				if (body!=null) {
					if (kernel.launchLightAgent(
							turtle,
							getTurtleActivationParameters(turtle, timeManager))!=null) {
						turtleSpawned(turtle, body, timeManager);
					}
				}
			}
		}
	}
	
	/** Replies a list of parameters to pass to the spawned turtle when it is activated.
	 * 
	 * @param turtle is the turtle for which parameters are dedicated.
	 * @param timeManager is the time manager used by the Jaak simulation.
	 * @return the parameters, never <code>null</code>;
	 */
	protected abstract Object[] getTurtleActivationParameters(Turtle turtle, KernelTimeManager timeManager);

	/** Replies if a turtle is spawnable according to the spawning law and 
	 * the current time mananager.
	 * 
	 * @param timeManager is the time manager used by the Jaak simulation.
	 * @return <code>true</code> if a turtle is spawnable, otherwise <code>false</code>.
	 */
	protected abstract boolean isSpawnable(KernelTimeManager timeManager);
	
	/** Replies the orientation for a newly spawned turtle.
	 * 
	 * @param timeManager is the time manager used by the Jaak simulation.
	 * @return the orientation angle for a newly spawned turtle.
	 */
	protected abstract float computeSpawnedTurtleOrientation(KernelTimeManager timeManager);

	/** Create an instance of a turtle.
	 * 
	 * @param timeManager is the time manager used by the Jaak simulation.
	 * @return the turtle instance.
	 */
	protected abstract Turtle createTurtle(KernelTimeManager timeManager);
	
	/** Invoked when a turtle was successfully spawned by the spawner itself.
	 *
	 * @param turtle is the spawned turtle.
	 * @param body is the spawned turtle body.
	 * @param timeManager is the time manager used by the Jaak simulation.
	 */
	protected abstract void turtleSpawned(Turtle turtle, TurtleBody body, KernelTimeManager timeManager);

	/** Invoked when a turtle was successfully spawned outside the spawner and 
	 * a body was created by this spawner.
	 *
	 * @param turtle is the spawned turtle.
	 * @param body is the spawned turtle body.
	 * @param timeManager is the time manager used by the Jaak simulation.
	 */
	protected abstract void turtleSpawned(AgentAddress turtle, TurtleBody body, KernelTimeManager timeManager);

	/** Replies the position where to spawn a turtle.
	 * The replied position could be different from the reference
	 * position replied by {@link #getReferenceSpawningPosition()}.
	 * 
	 * @param desiredPosition is the position desired by the factory invoker.
	 * @return a position.
	 */
	protected abstract Point2i computeCurrentSpawningPosition(Point2i desiredPosition);
	
	/** Replies the position of this spawner.
	 * 
	 * @return a position.
	 */
	public abstract Point2i getReferenceSpawningPosition();

	/** Replies the shape of spawning for this spawner.
	 * 
	 * @return a shape.
	 */
	public abstract Shape2i toShape();

	/** Provide implementation for a body factory dedicated to spawners.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SpawnedBodyFactory implements TurtleBodyFactory {

		private final TurtleBodyFactory factory;
		private final float orientation;
		
		/**
		 * @param factory is the turtle body factory to wrap.
		 * @param orientation is the turtle body.
		 */
		public SpawnedBodyFactory(TurtleBodyFactory factory, float orientation) {
			this.factory = factory;
			this.orientation = orientation;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isFreeCell(Point2i position) {
			return this.factory.isFreeCell(position);
		}
		
		/** Compute a free position.
		 * 
		 * @param desiredPosition is the desired position given by the factory invoker.
		 * @return a free position
		 */
		public Point2i computeValidPosition(Point2i desiredPosition) {
			Point2i dp = desiredPosition;
			Point2i p;
			for(int i=0; i<FREE_POSITION_COMPUTATION_RETRIES; ++i) {
				p = computeCurrentSpawningPosition(dp);
				assert(p!=null);
				if (isFreeCell(p)) return p;
				dp = null;
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Point2i desiredPosition, float desiredAngle, Object semantic) {
			Point2i p = computeValidPosition(desiredPosition);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					semantic);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Point2i desiredPosition, float desiredAngle) {
			Point2i p = computeValidPosition(desiredPosition);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Point2i desiredPosition) {
			Point2i p = computeValidPosition(desiredPosition);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId) {
			Point2i p = computeValidPosition(null);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				float desiredAngle, Object semantic) {
			Point2i p = computeValidPosition(null);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					semantic);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Object semantic) {
			Point2i p = computeValidPosition(null);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					semantic);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Point2i desiredPosition, Object semantic) {
			Point2i p = computeValidPosition(null);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					semantic);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Point2i desiredPosition, float desiredAngle, Object semantic,
				TurtleFrustum frustum) {
			Point2i p = computeValidPosition(desiredPosition);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					semantic,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Point2i desiredPosition, float desiredAngle,
				TurtleFrustum frustum) {
			Point2i p = computeValidPosition(desiredPosition);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Point2i desiredPosition, TurtleFrustum frustum) {
			Point2i p = computeValidPosition(desiredPosition);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId, TurtleFrustum frustum) {
			Point2i p = computeValidPosition(null);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				float desiredAngle, Object semantic, TurtleFrustum frustum) {
			Point2i p = computeValidPosition(null);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					desiredAngle,
					semantic,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Object semantic, TurtleFrustum frustum) {
			Point2i p = computeValidPosition(null);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					semantic,
					frustum);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody createTurtleBody(AgentAddress turtleId,
				Point2i desiredPosition, Object semantic, TurtleFrustum frustum) {
			Point2i p = computeValidPosition(desiredPosition);
			if (p==null) return null;
			return this.factory.createTurtleBody(
					turtleId,
					p,
					this.orientation,
					semantic,
					frustum);
		}
		
	}
	
}