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
package org.janusproject.demos.simulation.boids.organization;

import java.util.Collection;
import java.util.Map;

import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.boids.organization.messages.ActionMessage;
import org.janusproject.demos.simulation.boids.organization.messages.BoidArrivalMessage;
import org.janusproject.demos.simulation.boids.organization.messages.KillBoidMessage;
import org.janusproject.demos.simulation.boids.organization.messages.PerceptionMessage;
import org.janusproject.demos.simulation.boids.util.PerceivedBoidBody;
import org.janusproject.demos.simulation.boids.util.Population;
import org.janusproject.demos.simulation.boids.util.Settings;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Boid is a simple animal model dedicated to motion of bird flocks and fish schools.
 * <p>
 * In 1986 Reynolds made a computer model of coordinated animal motion such as 
 * bird flocks and fish schools. It was based on three dimensional computational 
 * geometry of the sort normally used in computer animation or computer aided 
 * design. I called the generic simulated flocking creatures boids. The basic 
 * flocking model consists of three simple steering behaviors which describe 
 * how an individual boid maneuvers based on the positions and velocities 
 * its nearby flockmates:<ul>
 * <li><strong>Separation:</strong> steer to avoid crowding local flockmates,</li>
 * <li><strong>Alignment:</strong> steer towards the average heading of local flockmates,</li>
 * <li><strong>Cohesion:</strong> steer to move toward the average position of local flockmates.</li>
 * </ul>
 * <p>
 * Each boid has direct access to the whole scene's geometric description, but 
 * flocking requires that it reacts only to flockmates within a certain small 
 * neighborhood around itself. The neighborhood is characterized by a 
 * distance (measured from the center of the boid) and an angle, measured 
 * from the boid's direction of flight. Flockmates outside this local 
 * neighborhood are ignored. The neighborhood could be considered a model 
 * of limited perception (as by fish in murky water) but it is probably 
 * more correct to think of it as defining the region in which flockmates 
 * influence a boids steering. 
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(
		fixedParameters={Population.class, Vector2f.class, Vector2f.class}
)
public class Boid extends Role {
	
	/**
	 * Position of the boid
	 */
	private final Vector2f position;
	
	/**
	 * Velocity vector of the boid.
	 */
	private final Vector2f orientation;
	
	/**
	 * The group inside which the boid is.
	 */
	private Population group;	
	
	/**
	 * The current state of the boid behavior.
	 */
	private State current = State.PRESENTATION;
	
	private PerceptionMessage lastPerceptionMessage;
	

	/**
	 */
	public Boid() {
		super();
		this.position     = new Vector2f();
		this.orientation      = new Vector2f();		
	}
	
	
	
	/**
	 * Initialize boid.
	 * 
	 * @param p is the group of the boid.
	 * @param initialPosition is the initial position of the boid
	 * @param initialSpeed is the initial speed of the boid
	 */
	private void init(Population p, Vector2f initialPosition, Vector2f initialSpeed) {
		this.group = p;
		
		// initialisation de la this.position
		if (initialPosition==null)
			this.position.set(
					(float)(Math.random() - 0.5)*Settings.ENVIRONMENT_DEMI_WIDTH, 
					(float)(Math.random() - 0.5)*Settings.ENVIRONMENT_DEMI_HEIGHT);
		else
			this.position.set(initialPosition);
				
		// initialisation de la vitesse
		if (initialSpeed==null)
			this.orientation.set((float)(Math.random() - 0.5), (float)(Math.random() - 0.5	));
		else
			this.orientation.set(initialSpeed);
		
		this.orientation.normalize();
		this.orientation.scaleAdd(0.25f, new Vector2f(0,0.75));
		this.orientation.scale(this.group.maxSpeed);
		
		this.current = State.PRESENTATION;
		
		this.lastPerceptionMessage = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		init((Population)parameters[0], (Vector2f)parameters[1], (Vector2f)parameters[2]);
		return StatusFactory.ok(this);
	}
	
	@Override
	public Status live() {		
		this.current = Run();
		return StatusFactory.ok(this);
	}

	private State Run() {
		switch (this.current) {		
		case PRESENTATION: 
			sendMessage(Environment.class, new BoidArrivalMessage(this.group,this.position, this.orientation));
			print(Locale.getString(Boid.class, "BOID_INITIALIZATION", getPlayer())); //$NON-NLS-1$
			return State.WAIT_PERCEPTIONS;					
					
		case WAIT_PERCEPTIONS:
			this.lastPerceptionMessage = null;
			for(Message msg : getMailbox()) {
				if (msg instanceof PerceptionMessage) {
					this.lastPerceptionMessage = (PerceptionMessage)msg;
					return State.SEND_INFLUENCE;
				}
				else if (msg instanceof KillBoidMessage) {
					leaveMe();
					return this.current;
				}
			}
			return State.WAIT_PERCEPTIONS;
		
		case SEND_INFLUENCE:
			think(perceive());
			return State.WAIT_PERCEPTIONS;
					
		default:
			return this.current;
		}				
	}
	
	
	
	/**
	 * This function retreive perceptions for this boid.
	 * 
	 * @return all the boids
	 */
	private Collection<PerceivedBoidBody> perceive() {		
		if (this.lastPerceptionMessage!=null) {
			Map<AgentAddress,PerceivedBoidBody> boids = this.lastPerceptionMessage.getOtherBoids();			
			print(Locale.getString(Boid.class,"BOID_PERCEPTION",getPlayer())); //$NON-NLS-1$
			PerceivedBoidBody myboid = boids.get(getPlayer());
			if ((myboid!=null) && (myboid.getAddress().equals(this.getPlayer()))){
				//Update internal data
				this.position.set(myboid.getPosition());
				this.orientation.set(myboid.getOrientation());						
			}
			return boids.values();
		}
		return null;
	}
	
	/**
	 * One instant of the boid life.
	 * The real boid behviour is coded here.
	 */
	private void think(Collection<PerceivedBoidBody> perception) {
		if (perception==null) return;
		Vector2f force;
		Vector2f influence = new Vector2f();

		influence.set(0,0);

		if(this.group.separationOn) {
			force = separation(perception);
			if (force.length()!=0) {
				force.normalize();
				force.scale(this.group.separationForce);
				influence.add(force);
			}
		}

		if(this.group.cohesionOn) {
			force = cohesion(perception);
			if (force.length()!=0) {
				force.normalize();
				force.scale(this.group.cohesionForce);
				influence.add(force);
			}
		}

		if(this.group.alignementOn) {
			force = alignement(perception);
			if (force.length()!=0) {
				force.normalize();
				force.scale(this.group.alignmentForce);
				influence.add(force);
			}
		}

		if(this.group.repulsionOn) {
			force = repulsion(perception);
			if (force.length()!=0) {
				force.normalize();
				force.scale(this.group.repulsionForce);
				influence.add(force);
			}
		}

		if(Settings.WALL_AVOIDENCE) {
			force = obstacles();
			if (force.length()!=0) {
				force.normalize();
				force.scale(Settings.OBSTACLE_FORCE);
				influence.add(force);
			}
		}

		// Restrict applicable force to bounds
		if (influence.length() > this.group.maxForce) {
			influence.normalize();
			influence.scale(this.group.maxForce);
		}

		// Mass contribution
		influence.scale( 1.f / this.group.mass );

		//Act: send influence to environment
		act(influence);
	}	
	
	/**
	 * Send a desire of action from the boid to the environment
	 * 
	 * @param force is the force computed by the boid.
	 */
	public void act(Vector2f force) {
		sendMessage(Environment.class, new ActionMessage(force));
		print(Locale.getString(Boid.class,
				"BOID_ACTION", getPlayer(), force)); //$NON-NLS-1$
	}
	
/************** Boid Methods *****************************/
	
	/**
	 * Replies if a boid inside the field of view of this boid.
	 * 
	 * @param otherBoid is the boid to test. 
	 * @param distance is the perception distance.
	 * @return <code>true</code> is inside the boid's field of view,
	 * otherwise <code>false</code>
	 */
	private boolean isVisible(PerceivedBoidBody otherBoid, double distance) {
		Vector2f tmp;
		
		tmp = new Vector2f(otherBoid.getPosition());
		tmp.sub(this.position);
		
		// si on est trop loin tand-pis.
		if ( tmp.length() > distance )
			return false;
				
		double angle = MathUtil.signedAngle(this.orientation.getX(), this.orientation.getY(), tmp.getX(), tmp.getY());
		
		return ( Math.abs(angle) <= this.group.visibleAngle);
	}

	/**
	 * Replies the force required to separate from the group.
	 * @return the force required to separate from the group.
	 */
	private Vector2f separation(Collection<PerceivedBoidBody> otherBoids) {
		Vector2f tmp = new Vector2f();
		Vector2f force = new Vector2f();
		float   len;

		force.set(0,0);
		for (PerceivedBoidBody otherBoid : otherBoids) {
			if ((!otherBoid.getAddress().equals(getPlayer()))
				&&(otherBoid.getGroup().equals(this.group))
				&& (isVisible(otherBoid,this.group.separationDistance))) {
				tmp.set(this.position);
				tmp.sub(otherBoid.getPosition());
				len = tmp.length();
				// force en 1/r
				tmp.scale( 1.f / (len*len) );
				force.add(tmp);
			}
		}
		return force;
	}

	/**
	 * Replies the force required to stay in cohesion with the group.
	 * @return the force required to stay in cohesion with the group.
	 */
	private Vector2f cohesion(Collection<PerceivedBoidBody> otherBoids) {
		int nbTot = 0;
		Vector2f force = new Vector2f();
		force.set(0,0);
		for (PerceivedBoidBody otherBoid : otherBoids) {
			if ((!otherBoid.getAddress().equals(getPlayer()))
				&& (otherBoid.getGroup() == this.group)
				&& (isVisible(otherBoid,this.group.cohesionDistance)) )
			{
				++nbTot;
				force.add(otherBoid.getPosition());
			}
		}
		
		// compute barycenter...
		if (nbTot > 0) {
			force.scale(1.f / nbTot);
			force.sub(this.position);
		}
		return force;
	}
	
	/**
	 * Reploes the force required to be aligned on other boids.
	 * @return the force required to be aligned on other boids.
	 */
	private Vector2f alignement(Collection<PerceivedBoidBody> otherBoids) {
		int nbTot = 0;
		Vector2f tmp = new Vector2f();
		Vector2f force = new Vector2f();
		force.set(0,0);
		
		for (PerceivedBoidBody otherBoid : otherBoids) {
			if ((otherBoid != null)
				&& (!otherBoid.getAddress().equals(this.getPlayer()))
				&& (otherBoid.getGroup().equals(this.group))
				&& (isVisible(otherBoid,this.group.alignmentDistance))) {
				++nbTot;
				tmp.set(otherBoid.getOrientation());
				tmp.scale( 1.f / tmp.length() );
				force.add(tmp);
			}
		}
		
		if (nbTot > 0) {
			force.scale( 1.f / nbTot );
			/*Vector3d pcross = new Vector3d();//FAUX
			pcross.cross(new Vector3d(vitesse.x,vitesse.y,0),new Vector3d(force.x,force.y,0));
			pcross.scale(force.length());
			pcross.negate();
			force.setXY(pcross.x,pcross.y);*/
		}
		return force;
	}
	
	/**
	 * Replies the force to keep repulsed by other boids.
	 * @return the force to keep repulsed by other boids.
	 */
	private Vector2f repulsion(Collection<PerceivedBoidBody> otherBoids) {
		Vector2f force = new Vector2f();
		Vector2f tmp= new Vector2f();
		float   len;
		
		force.set(0,0);
		for (PerceivedBoidBody otherBoid : otherBoids) {
			if ((!otherBoid.getAddress().equals(this.getPlayer()))
				&& (!otherBoid.getGroup().equals(this.group))
				&& isVisible(otherBoid,this.group.repulsionDistance)) {
					tmp.set(this.position);
					tmp.sub(otherBoid.getPosition());
					len = tmp.length();
					tmp.scale( 1.f / (len*len) );
					force.add(tmp);
			}
		}
		return force;
	}
		
    /**
     * Replies the force to avoid collision on obstacles.
     * Here obstacles are the borders of the environment.
     * @return the force to avoid collision on obstacles.
     */
    private Vector2f obstacles() {
        Vector2f tmp= new Vector2f();
        Vector2f force= new Vector2f();
        force.set(0,0);

        if (((Settings.ENVIRONMENT_DEMI_WIDTH-this.position.getX())<Settings.OBSTACLE_DISTANCE)
        	&& this.orientation.getX()>0) {
            tmp.set(-this.orientation.getX()/(Settings.ENVIRONMENT_DEMI_WIDTH-this.position.getX()),0);
            force.add(tmp);                    
        }
        if ((this.position.getX()<-Settings.ENVIRONMENT_DEMI_WIDTH+Settings.OBSTACLE_DISTANCE)
        	&& this.orientation.getX()<0) {
            tmp.set(-this.orientation.getX()/(Settings.ENVIRONMENT_DEMI_WIDTH+this.position.getX()),0);
            force.add(tmp);                    
        }

        if ((this.position.getY()>Settings.ENVIRONMENT_DEMI_HEIGHT-Settings.OBSTACLE_DISTANCE)
        	&& this.orientation.getY()>0) {
            tmp.set(0,-this.orientation.getY()/(Settings.ENVIRONMENT_DEMI_HEIGHT-this.position.getY()));
            force.add(tmp);                    
        }
    
        if ((this.position.getY()<-Settings.ENVIRONMENT_DEMI_HEIGHT+Settings.OBSTACLE_DISTANCE)
        	&& this.orientation.getY()<0) {
            tmp.set(0,-this.orientation.getY()/(Settings.ENVIRONMENT_DEMI_HEIGHT+this.position.getY()));
            force.add(tmp);                    
        }    

        force.scale(this.group.mass);
        
        return force;
    }	

    /**
     * State of a boid.
     * 
     * @author $Author: sgalland$
     * @version $FullVersion$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     */
    public enum State {
    	/** Initialization and presentation to the environmental agent.
    	 */
    	PRESENTATION,
    	/** Waiting perceptions.
    	 */
    	WAIT_PERCEPTIONS,
    	/** Compute influence and send.
    	 */
    	SEND_INFLUENCE;
    }
    
}
