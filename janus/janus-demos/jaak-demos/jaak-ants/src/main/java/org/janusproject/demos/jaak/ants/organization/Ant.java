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

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.demos.jaak.ants.environment.Food;
import org.janusproject.demos.jaak.ants.environment.Pheromone;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.body.TurtleBodyFactory;
import org.janusproject.jaak.turtle.Turtle;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.random.RandomNumber;

/** This class defines an ant.
 * <p>
 * The most important characteristic of an ant in this context 
 * is related to its individual and unpredictable tendency 
 * to choose a certain route among the many available. Each instance of 
 * the class Ant must represent an individual agent with singular 
 * characteristics. This can be implemented by using a mathematical 
 * function. As described above the pheromone level over a route is 
 * measured by an integer number. The agent will use a method that 
 * evaluates its tendency of choosing a route based on the 
 * pheromone intensity. A good variability of the behavior of 
 * the agents can be expressed as a sinusoidal function 
 * with at least three coefficients: T(PL) = Alpha * sin(Beta * PL + Gamma).
 * <p>
 * The input PL is the pheromone level over a route. Alfa, Beta and 
 * Gamma will be properties of the Ant class initialized as random 
 * float numbers within the interval [-5..5]. These properties will 
 * make possible to have different individuals in the population.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters=Class.class
)
public class Ant extends Turtle {

	private static final long serialVersionUID = 1348187617397794406L;

	/** Parameter for tendency to follow a pheromone route.
	 */
	float alpha;
	
	/** Parameter for tendency to follow a pheromone route.
	 */
	float beta;
	
	/** Parameter for tendency to follow a pheromone route.
	 */
	float gamma;
	
	/**
	 */
	public Ant() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TurtleBody createTurtleBody(TurtleBodyFactory factory) {
		return factory.createTurtleBody(getAddress());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Status activate(Object... initParameters) {
		CapacityContainer capacityContainer = getCapacityContainer();
		capacityContainer.addCapacity(new PheromoneTendencyCapacityImplementation());
		capacityContainer.addCapacity(new FoodSelectionCapacityImplementation());
		this.alpha = (RandomNumber.nextFloat() - RandomNumber.nextFloat()) * 5f;
		this.beta = (RandomNumber.nextFloat() - RandomNumber.nextFloat()) * 5f;
		this.gamma = (RandomNumber.nextFloat() - RandomNumber.nextFloat()) * 5f;
		GroupAddress group = getOrCreateGroup(AntColony.class);
		
		if (requestRole((Class<? extends AntRole>)initParameters[0], group)==null) {
			return StatusFactory.cancel(this);
		}
		
		return StatusFactory.ok(this);
	}	

	/** This class defines the implementation of a PheromoneTendencyCapacity.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PheromoneTendencyCapacityImplementation
	extends CapacityImplementation
	implements PheromoneFollowingCapacity {

		/**
		 */
		public PheromoneTendencyCapacityImplementation() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void call(CapacityContext call) throws Exception {
			call.setOutputValues(
					followPheromone(
							call.getInputValueAt(0, Point2i.class),
							call.getInputValueAt(1, Collection.class)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pheromone followPheromone(Point2i position, Collection<? extends Pheromone> pheromones) {
			float minAmount = Float.POSITIVE_INFINITY;
			Pheromone currentP = null;
			Pheromone minP = null;
			Point2i pp;
			for(Pheromone p : pheromones) {
				pp = p.getPosition();
				if (!pp.equals(position)) {
					if (minP==null || (p.floatValue()<minAmount)) {
						minP = p;
						minAmount = p.floatValue();
					}
				}
				else {
					currentP = p;
				}
			}
			if (currentP==null || (minP!=null && currentP.floatValue()>=minAmount)) {
				return minP;
			}
			return null;
		}
		
	}
	
	/** This class defines the implementation of a PheromoneTendencyCapacity.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FoodSelectionCapacityImplementation
	extends CapacityImplementation
	implements FoodSelectionCapacity {

		/**
		 */
		public FoodSelectionCapacityImplementation() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void call(CapacityContext call) throws Exception {
			call.setOutputValues(getBestFood(call.getInputValueAt(0, Collection.class)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Food getBestFood(Collection<Food> foods) {
			Food bestFood = null;
			int distance = Integer.MAX_VALUE;
			Point2i ap = Ant.this.getPosition();
			Point2i fp;
			int d;
			for(Food food : foods) {
				fp = food.getPosition();
				d = Math.abs(fp.x() - ap.x()) + Math.abs(fp.y() - ap.y());
				if (bestFood==null || d<distance) {
					distance = d;
					bestFood = food;
				}
			}
			return bestFood;
		}
		
	}
	
}