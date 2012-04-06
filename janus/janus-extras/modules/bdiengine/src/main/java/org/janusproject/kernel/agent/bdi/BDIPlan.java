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

import java.util.Collection;
import java.util.List;

import org.janusproject.kernel.condition.ConditionnedObject;

/**
 * Represents a plan executed by a BDI agent.
 * A plan calls procedurally the execute method of his actions.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class BDIPlan extends ConditionnedObject<BDIAgent, BDIPlanCondition>{
	/**
	 * List of actions to run in order to execute the plan
	 */
	protected final List<BDIAction> actions;
	
	/**
	 * Constructor.
	 * @param actionList 
	 */
	public BDIPlan(List<BDIAction> actionList) {
		this.actions = actionList;
	}

	/**
	 * Return the plan's actions.
	 * @return list of actions
	 */
	public final List<BDIAction> getActions() {
		return this.actions;
	}
	
	/**
	 * Return the actions needed by the plan in order to run.
	 * It's mandatory to rewrite this function in the subclass.
	 * @return list of actions
	 */
	public static Collection<Class<? extends BDIAction>> getRequiredActions() {
		return null;
	}

	/**
	 * Return the supported goal.
	 * @return class of the supported goal
	 */
	public abstract Class<? extends BDIGoal> getSupportedGoalType();
	
	/**
	 * Check if a plan is relevant, i.e. can manage a goal in particular.
	 * @param goal is the goal we want to test (if it's manageable by the plan)
	 * @return true if the plan can manage the goal, false otherwise
	 */
	public abstract boolean isRelevant(Class<? extends BDIGoal> goal);

	/**
	 * Check if a plan is applicable according to the agent's current beliefs.
	 * @param beliefs is the agent's beliefs
	 * @return true if the plan is applicable, false otherwise.
	 */
	public abstract boolean context(List<BDIBelief> beliefs);
	
	/**
	 * Execute method. Shall calls the execute method of the actions.
	 * @param actionIndex allow the execution of the currentAction
	 * @param beliefs is the agent's beliefs
	 * @return a plan status
	 */
	public abstract BDIPlanStatus execute(int actionIndex, List<BDIBelief> beliefs);
}
