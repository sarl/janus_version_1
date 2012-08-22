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
package org.janusproject.demos.jaak.ants.organization;

import java.util.concurrent.TimeUnit;

import org.janusproject.demos.jaak.ants.AntColonySystem;
import org.janusproject.demos.jaak.ants.environment.ColonyPheromone;
import org.janusproject.demos.jaak.ants.environment.Pheromone;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.time.JaakTimeManager;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** This class defines a patroller role.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype
public class Patroller extends AntRole {

	private State state;
	private float deadTime = Float.NaN;
	
	/**
	 */
	public Patroller() {
		super();
		addObtainCondition(new HasAllRequiredCapacitiesCondition(PheromoneFollowingCapacity.class));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... objects) {
		this.state = State.PATROL;
		return StatusFactory.ok(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		switch(this.state) {
		case PATROL:
			runPatrol();
			break;
		case RETURN_TO_COLONY:
			runReturnToColony();
			break;
		default:
		}
		return StatusFactory.ok(this);
	}
	
	private void runPatrol() {
		JaakTimeManager tm = getJaakTimeManager();
		float currentTime = tm.getCurrentTime(TimeUnit.SECONDS);

		if (Float.isNaN(this.deadTime)) {
			this.deadTime = currentTime + (AntColonySystem.MAX_PHEROMONE_AMOUNT/ColonyPheromone.EVAPORATION)/3;
			randomPatrol();
		}
		else if (currentTime>=this.deadTime) {
			this.deadTime = Float.NaN;
			this.state = State.RETURN_TO_COLONY;
			Pheromone goHome = followPheromone(ColonyPheromone.class);
			if (goHome!=null) {
				gotoMotion(goHome.getPosition(), true);
			}
			else {
				randomPatrol();
			}
		}
		else {
			randomPatrol();
		}
		
		dropOff(new ColonyPheromone());
	}
	
	private void runReturnToColony() {
		EnvironmentalObject colony = getPerceivedObject(org.janusproject.demos.jaak.ants.environment.AntColony.class);
		if (colony!=null) {
			if (getLastMotionInfluenceStatus().isFailure()
				|| gotoMotion(colony.getPosition(), false)) {
				this.state = State.PATROL;
			}
		}
		else {
			if (getLastMotionInfluenceStatus().isFailure()) {
				randomPatrol();
			}
			else {
				Pheromone selected = followPheromone(ColonyPheromone.class);
				if (selected!=null) {
					gotoMotion(selected.getPosition(), true);
				}
				else {
					randomPatrol();
				}
			}
		}
	}
		
	/** State of the patroller
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum State {

		/** Patrol.
		 */
		PATROL,
		
		/** Return to colony.
		 */
		RETURN_TO_COLONY;
		
	}
	
}