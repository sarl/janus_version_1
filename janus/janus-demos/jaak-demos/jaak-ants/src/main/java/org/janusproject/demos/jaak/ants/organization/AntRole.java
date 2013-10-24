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

import java.util.Collection;

import org.arakhne.afc.math.MathConstants;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.demos.jaak.ants.environment.Pheromone;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.turtle.TurtleRole;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.util.random.RandomNumber;

/** This class defines a role for all ants.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AntRole extends TurtleRole {

	/**
	 */
	public AntRole() {
		super();
		addObtainCondition(new HasAllRequiredCapacitiesCondition(PheromoneFollowingCapacity.class));
	}
	
	/** Select and reply a pheromone.
	 * 
	 * @param pheromoneType is the type of pheromone to follow
	 * @return the pheromone to reach.
	 */
	protected Pheromone followPheromone(Class<? extends Pheromone> pheromoneType) {
		Collection<? extends Pheromone> pheromones = getPerceivedObjects(pheromoneType);
		if (pheromones!=null && !pheromones.isEmpty()) {
			try {
				CapacityContext cc = executeCapacityCall(PheromoneFollowingCapacity.class, 
						getPosition(), pheromones);
				return cc.getOutputValueAt(0, Pheromone.class);
			}
			catch (Exception _) {
				//
			}
		}
		return null;
	}
	
	/** Move randomly.
	 */
	protected void randomMotion() {
		float dAngle;
		dAngle = (RandomNumber.nextFloat()-RandomNumber.nextFloat()) * MathConstants.DEMI_PI;
		if (dAngle>0) turnLeft(dAngle);
		else turnRight(-dAngle);
		moveForward(1);
	}

	/** Turn back.
	 */
	protected void randomTurnBack() {
		float dAngle;
		dAngle = (RandomNumber.nextFloat()-RandomNumber.nextFloat()) * MathConstants.DEMI_PI;
		if (dAngle>0) turnLeft(MathConstants.DEMI_PI + dAngle);
		else turnRight(MathConstants.DEMI_PI - dAngle);
		moveForward(1);
	}

	/** Patrock and try to never stop motion.
	 */
	protected void randomPatrol() {
		MotionInfluenceStatus motionStatus = getLastMotionInfluenceStatus();
		if (motionStatus==MotionInfluenceStatus.NO_MOTION) {
			randomTurnBack();
		}
		else {
			randomMotion();
		}
	}
	
	/** Go to the given target position.
	 * 
	 * @param target is the point to reach.
	 * @param enableRandom indicates if the random behavior should be used when
	 * the given point was already reached.
	 * @return <code>true</code> if the ant does not move according to this function,
	 * <code>false</code> if the ant is moving.
	 */
	protected boolean gotoMotion(Point2i target, boolean enableRandom) {
		Point2i position = getPosition();
		int dx = target.x() - position.x();
		int dy = target.y() - position.y();
		if (dx!=0 || dy!=0) {
			Vector2f motion = new Vector2f(dx, dy);
			motion.normalize();
			move(motion, true);
			return false;
		}
		if (enableRandom) {
			randomMotion();
			return false;
		}
		return true;
	}
		
}