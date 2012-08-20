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

import org.janusproject.demos.jaak.ants.AntColonySystem;
import org.janusproject.demos.jaak.ants.environment.ColonyPheromone;
import org.janusproject.demos.jaak.ants.environment.Food;
import org.janusproject.demos.jaak.ants.environment.FoodPheromone;
import org.janusproject.demos.jaak.ants.environment.Pheromone;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.PickedObject;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** This class defines a forager role.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype
public class Forager extends AntRole {

	private State state;
	private Food bag = null;
	
	/**
	 */
	public Forager() {
		super();
		addObtainCondition(new HasAllRequiredCapacitiesCondition(FoodSelectionCapacity.class));
	}
	
	private Food selectFood(Collection<Food> foods) {
		if (foods!=null && !foods.isEmpty()) {
			try {
				CapacityContext cc = executeCapacityCall(FoodSelectionCapacity.class, foods);
				return cc.getOutputValueAt(0, Food.class);
			}
			catch (Exception _) {
				//
			}
		}
		return null;
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		Status st = super.activate(parameters);
		if (st.isSuccess()) {
			this.state = State.SEARCH_FOOD;
		}
		return st;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		switch(this.state) {
		case SEARCH_FOOD:
			runSearchFoodState();
			break;
		case PICK_UP_FOOD:
			runPickUpFood();
			break;
		case RETURN_TO_COLONY:
			runReturnToColony();
			break;
		default:
		}
		return StatusFactory.ok(this);
	}
	
	private void runSearchFoodState() {
		Collection<Food> foods = getPerceivedObjects(Food.class);
		Food selectedFood = selectFood(foods);
		if (selectedFood!=null) {
			// food found
			if (getLastMotionInfluenceStatus()==MotionInfluenceStatus.NO_MOTION) {
				randomPatrol();
				dropOff(new ColonyPheromone(AntColonySystem.MAX_PHEROMONE_AMOUNT));
			}
			else if (gotoMotion(selectedFood.getPosition(), false)) {
				pickUp(new Food(5));
				this.state = State.PICK_UP_FOOD;
			}
			else {
				dropOff(new ColonyPheromone(AntColonySystem.MAX_PHEROMONE_AMOUNT));
			}
		}
		else {
			if (getLastMotionInfluenceStatus()==MotionInfluenceStatus.NO_MOTION) {
				randomPatrol();
			}
			else {
				Pheromone selected = followPheromone(FoodPheromone.class);
				if (selected!=null) {
					gotoMotion(selected.getPosition(), true);
				}
				else {
					// no food, random search
					randomPatrol();
				}
			}
			dropOff(new ColonyPheromone(AntColonySystem.MAX_PHEROMONE_AMOUNT));
		}
	}
	
	private void runPickUpFood() {
		PickedObject pickedObject = getFirstPerception(PickedObject.class);
		if (pickedObject!=null) {
			EnvironmentalObject obj = pickedObject.getPickedUpObject();
			if (obj instanceof Food) {
				Food food = (Food)obj;
				if (!food.isDisappeared()) {
					this.bag = food;
					dropOff(new FoodPheromone(AntColonySystem.MAX_PHEROMONE_AMOUNT));
					this.state = State.RETURN_TO_COLONY;
					return;
				}
			}
		}
		this.state = State.SEARCH_FOOD;
	}
	
	private void runReturnToColony() {
		Food bag = this.bag;
		if (bag!=null && !bag.isDisappeared()) {
			if (getLastMotionInfluenceStatus()==MotionInfluenceStatus.NO_MOTION) {
				randomPatrol();
				dropOff(new FoodPheromone(AntColonySystem.MAX_PHEROMONE_AMOUNT));
			}
			else {
				EnvironmentalObject colony = getPerceivedObjectWithSemantic(AntColony.class);
				if (colony!=null) {
					if (gotoMotion(colony.getPosition(), false)) {
						// Never drop the food because ut will cause the ants to stay on colony's cell
						this.bag = null;
						this.state = State.SEARCH_FOOD;
					}
					else {
						dropOff(new FoodPheromone(AntColonySystem.MAX_PHEROMONE_AMOUNT));
					}
				}
				else {
					Pheromone selected = followPheromone(ColonyPheromone.class);
					if (selected!=null) {
						gotoMotion(selected.getPosition(), true);
					}
					else {
						randomMotion();
					}
					dropOff(new FoodPheromone(AntColonySystem.MAX_PHEROMONE_AMOUNT));
				}
			}
		}
		else {
			this.state = State.SEARCH_FOOD;
		}
	}

	/** State of the forager role.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum State {
		SEARCH_FOOD,
		PICK_UP_FOOD,
		RETURN_TO_COLONY,
	}
	
}