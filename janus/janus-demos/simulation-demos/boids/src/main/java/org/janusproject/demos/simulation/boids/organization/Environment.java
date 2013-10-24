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
package org.janusproject.demos.simulation.boids.organization;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.boids.capacity.EnvironmentRefreshCapacity;
import org.janusproject.demos.simulation.boids.capacity.RetreiveBoidsCapacity;
import org.janusproject.demos.simulation.boids.organization.messages.ActionMessage;
import org.janusproject.demos.simulation.boids.organization.messages.BoidArrivalMessage;
import org.janusproject.demos.simulation.boids.organization.messages.PerceptionMessage;
import org.janusproject.demos.simulation.boids.util.PerceivedBoidBody;
import org.janusproject.demos.simulation.boids.util.Population;
import org.janusproject.demos.simulation.boids.util.Settings;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.ExceptionStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** Implementation of a situated 2D environment for boids.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(
		fixedParameters={}
)
public class Environment extends Role {
	
	private Set<AgentAddress> waitingBoids = new TreeSet<AgentAddress>();
	
	/**
	 * The current state of the environment behavior
	 */
	private State current = State.WAIT_FIRST_BOID; 
	

	/**
	 */
	@SuppressWarnings("unchecked")
	public Environment() {
		addObtainCondition(new HasAllRequiredCapacitiesCondition(
				Arrays.asList(
						RetreiveBoidsCapacity.class,
						EnvironmentRefreshCapacity.class)));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {		
		this.current = State.WAIT_FIRST_BOID;
		return StatusFactory.ok(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {		
		try {
			this.current = Run();
		}
		catch (Exception e) {
			return new ExceptionStatus(e);
		}
		return StatusFactory.ok(this);
	}

	@SuppressWarnings("unchecked")
	private State Run() throws Exception {
		CapacityContext cc = executeCapacityCall(RetreiveBoidsCapacity.class);
		Map<AgentAddress,PerceivedBoidBody> boids = (Map<AgentAddress,PerceivedBoidBody>)cc.getOutputValueAt(0);
		
		switch(this.current) {		
		case WAIT_FIRST_BOID:
		{
			boolean hasOneBoid = false;
			for(Message msg : getMailbox()) {
				if (msg instanceof BoidArrivalMessage) {
					BoidArrivalMessage bam = (BoidArrivalMessage)msg;
					hasOneBoid = addBoid(
							boids,
							((RoleAddress)bam.getSender()).getPlayer(),
							bam.getInitialPosition(),
							bam.getInitialSpeed(),
							bam.getPopulation())
						|| hasOneBoid;
				}
			}
			if (hasOneBoid) {
				print(Locale.getString(Environment.class,
						"BOID_PRESENTATION_FINISHED")); //$NON-NLS-1$
				return State.PERCEPTION_COMPUTATION;
			}
			return State.WAIT_FIRST_BOID;
		}
					
		case PERCEPTION_COMPUTATION:
		{
			broadcastMessage(Boid.class, new PerceptionMessage(boids));
			this.waitingBoids.clear();
			return State.INFLUENCE_RECEPTION;
		}
					
		case INFLUENCE_RECEPTION:
		{
			int alreadyArrived = boids.size();
			int shadowCount = (Integer)cc.getOutputValueAt(1);
			if (shadowCount>=0) alreadyArrived -= shadowCount;
			for(Message msg : getMailbox()) {
				if (msg instanceof BoidArrivalMessage) {
					BoidArrivalMessage bam = (BoidArrivalMessage)msg;
					addBoid(
							boids,
							((RoleAddress)bam.getSender()).getPlayer(),
							bam.getInitialPosition(),
							bam.getInitialSpeed(),
							bam.getPopulation());
				}
				else if (msg instanceof ActionMessage) {
					ActionMessage am = (ActionMessage)msg;
					consumeActionMessage(boids, am);
				}
			}
			if (alreadyArrived==this.waitingBoids.size()) {
				return State.PERCEPTION_COMPUTATION;
			}
			return State.INFLUENCE_RECEPTION;
		}
					
		default :
			return this.current;
		}				
	}
	
	private boolean addBoid(
			Map<AgentAddress,PerceivedBoidBody> boids,
			AgentAddress boidAddress,
			Vector2f initialPosition,
			Vector2f initialSpeed,
			Population population) {
		if (!boids.containsKey(boidAddress)) {
			print(Locale.getString(Environment.class,
					"BOID_ARRIVED", boidAddress)); //$NON-NLS-1$
			PerceivedBoidBody boidBody = new PerceivedBoidBody(
					population,
					boidAddress,
					initialPosition,
					initialSpeed);
			try {
				boids.put(boidAddress, boidBody);
			}
			catch (Exception e) {
				return false;
			}
			return true;
		}

		error(Locale.getString(Environment.class,
		"ALREADY REGISTERED BOID")); //$NON-NLS-1$
		return false;
	}
	
	private void consumeActionMessage(Map<AgentAddress,PerceivedBoidBody> boids, ActionMessage message) {
		PerceivedBoidBody body;
		
		AgentAddress actor = ((RoleAddress)message.getSender()).getPlayer();
				
		print(Locale.getString(Environment.class,
				"ENVIRONMENT_RECEIVED_INFLUENCE", actor)); //$NON-NLS-1$
				
		body = boids.get(actor);
		if (body!=null) {
			applyForce(message.getForce(),body);
			this.waitingBoids.add(actor);
		}
		else {
			error(Locale.getString(Environment.class,
					"UNKNOWN_BOID", actor)); //$NON-NLS-1$
		}
	}
	
	/**
	 * Apply the force given by a boid influence.
	 * @param force is the force given by a boid.
	 * @param b is the boid body to move.
	 */
	private static void applyForce(Vector2f force, PerceivedBoidBody b) {
		// on borne la force appliquee.
		if (force.length() > b.getGroup().maxForce) {
			force.normalize();
			force.scale(b.getGroup().maxForce);
		}
		
		// contribution of mass.
		//force.scale( 1. / b.getGroup().mass );
		
		// mise a jour de l'acceleration et de la vitesse.
		Vector2f acceleration = new Vector2f(b.getAcceleration());
		acceleration.add(force);
		Vector2f orientation = new Vector2f(b.getOrientation());
		orientation.add(acceleration);
		
		// Bounds the orientation
		if (orientation.length() > b.getGroup().maxSpeed) {
			orientation.normalize();
			orientation.scale(b.getGroup().maxSpeed);
		}
		
		// on met a jour la position
		Vector2f position = new Vector2f(b.getPosition());
		position.add(orientation);
		
		b.setAcceleration(acceleration);
		b.setOrientation(orientation);
		b.setPosition(position);

		updateWorld(b);
	}
	
    /**
     * Update the position of the given body to lie in
     * the world.
     */
	private static void updateWorld(PerceivedBoidBody b) {
            double posX;
            double posY;

            posX = b.getPosition().getX();
            posY = b.getPosition().getY();
            if (Settings.WALL_AVOIDENCE) {
                if ( posX > Settings.ENVIRONMENT_DEMI_WIDTH )           posX = Settings.ENVIRONMENT_DEMI_WIDTH-0.1;
                if ( posX < ( -1 * Settings.ENVIRONMENT_DEMI_WIDTH ) )  posX = -Settings.ENVIRONMENT_DEMI_WIDTH+0.1;
                if ( posY > Settings.ENVIRONMENT_DEMI_HEIGHT )           posY = Settings.ENVIRONMENT_DEMI_HEIGHT-0.1;
                if ( posY < ( -1 * Settings.ENVIRONMENT_DEMI_HEIGHT ) )  posY = -Settings.ENVIRONMENT_DEMI_HEIGHT+0.1;
            }
            else
            {
                if ( posX > Settings.ENVIRONMENT_DEMI_WIDTH )           posX -= 2 * Settings.ENVIRONMENT_DEMI_WIDTH;
                if ( posX < ( -1 * Settings.ENVIRONMENT_DEMI_WIDTH ) )  posX += 2 * Settings.ENVIRONMENT_DEMI_WIDTH;
                if ( posY > Settings.ENVIRONMENT_DEMI_HEIGHT )           posY -= 2 * Settings.ENVIRONMENT_DEMI_HEIGHT;
                if ( posY < ( -1 * Settings.ENVIRONMENT_DEMI_HEIGHT ) )  posY += 2 * Settings.ENVIRONMENT_DEMI_HEIGHT;
            }

            b.setPosition(new Vector2f(posX,posY));
    }
	
	
	/** State of the environment.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum State {

		/** Wait for initial boids.
		 */
		WAIT_FIRST_BOID,
		
		/** Perception may be computed and boids may run one step.
		 */
		PERCEPTION_COMPUTATION,
		
		/** Boid influences/actions are expecting.
		 */
		INFLUENCE_RECEPTION;

	}
	
}
