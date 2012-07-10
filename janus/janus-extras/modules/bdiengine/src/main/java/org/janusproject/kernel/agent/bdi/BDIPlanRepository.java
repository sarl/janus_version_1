/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.kernel.agent.bdi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Library of plans available. Singleton.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public final class BDIPlanRepository {
	/**
	 * Unique instance of the plan repository
	 */
	private final static BDIPlanRepository instance = new BDIPlanRepository();
	
	/**
	 * Repository of plans.
	 * Each plan is matched with a goal.
	 * Several plans can manage a goal, but a plan can only manage one goal.
	 */
	private Map<Class<? extends BDIGoal>, List<Class<? extends BDIPlan>>> plans = 
				new HashMap<Class<? extends BDIGoal>, List<Class<? extends BDIPlan>>>();
	
	/**
	 * Create the plan repository
	 */
	private BDIPlanRepository() {
		// default constructor
	}
	
	/**
	 * Get the unique instance of the plan repository
	 * @return the instance of PlanRepository
	 */
	public static BDIPlanRepository getInstance() {
		return instance;
	}
	
	/**
	 * Return a list of plans managing a specific goal.
	 * @param goal is the key map
	 * @return a list of plans managing the goal
	 */
	public List<Class<? extends BDIPlan>> getPlans(BDIGoal goal) {
		return this.plans.get(goal.getClass());
	}
	
	/**
	 * Add a plan to the repository.
	 * If the plan already exists, don't add it.
	 * If the supported goal is already in the list, we add the plan to the corresponding list.
	 * If neither are in the repository, we add the key-value to the map.
	 * @param goal is the goal supported by the plan
	 * @param plan is the plan to add
	 */
	public void addPlan(Class<? extends BDIGoal> goal, Class<? extends BDIPlan> plan) {
		if (this.plans.containsKey(goal)) {
			if (this.plans.get(goal).contains(plan))
				return;
			
			this.plans.get(goal).add(plan);
		}
		else {
			List<Class <? extends BDIPlan>> listPlans = new LinkedList<Class<? extends BDIPlan>>();
			listPlans.add(plan);
			this.plans.put(goal, listPlans);
		}
	}
}
