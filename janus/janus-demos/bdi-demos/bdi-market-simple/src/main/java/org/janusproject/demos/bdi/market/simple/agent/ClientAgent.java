/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.demos.bdi.market.simple.agent;


import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

import org.janusproject.demos.bdi.market.simple.BeliefType;
import org.janusproject.demos.bdi.market.simple.Environment;
import org.janusproject.demos.bdi.market.simple.goal.ClientGoal;
import org.janusproject.demos.bdi.market.simple.travel.TravelDestination;
import org.janusproject.demos.bdi.market.simple.travel.TravelSelectionCritera;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agent.bdi.BDIActionStatus;
import org.janusproject.kernel.agent.bdi.BDIAgent;
import org.janusproject.kernel.agent.bdi.BDIAgentState;
import org.janusproject.kernel.agent.bdi.BDIBelief;
import org.janusproject.kernel.agent.bdi.BDIGoal;
import org.janusproject.kernel.agent.bdi.BDIPlan;
import org.janusproject.kernel.agent.bdi.event.BDIBeliefEvent;
import org.janusproject.kernel.agent.bdi.event.BDIGoalEvent;
import org.janusproject.kernel.agent.bdi.event.BDIGoalEventType;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.random.RandomNumber;





/**
 * Client agent.
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(fixedParameters = {Environment.class})
public class ClientAgent extends BDIAgent {

	private static final long serialVersionUID = 3681714128256239399L;
	
	private WeakReference<Environment> environment;
	private State state = State.INIT;
	private TravelSelectionCritera critera;
	private TravelDestination travelDestination;

	/**
	 */
	public ClientAgent() {
		//
	}
	
	@Override
	public Status activate(Object... parameters) {
		this.environment = new WeakReference<Environment>((Environment)parameters[0]);
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {
		processMessages();
		try {
			super.live();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		this.state = run();

		return StatusFactory.ok(this);
	}

	private State run() {
		switch (this.state) {
		case INIT:
			fireSignal(new BDIGoalEvent(new ClientGoal(), BDIGoalEventType.GOAL_ARRIVED));
			
			this.critera = TravelSelectionCritera.values()[RandomNumber.nextInt(TravelSelectionCritera.values().length)];
			fireSignal(new BDIBeliefEvent(new BDIBelief(
					this.critera, 
					BeliefType.CLIENT_CRITERA)));
			
			this.travelDestination = TravelDestination.values()[RandomNumber.nextInt(TravelDestination.values().length)];
			fireSignal(new BDIBeliefEvent(new BDIBelief(
					this.travelDestination, 
					BeliefType.CLIENT_TRAVEL_DESTINATION)));
			
			fireSignal(new BDIBeliefEvent(new BDIBelief(
					this.environment.get().getBrokerAgents(), 
					BeliefType.BROKERS_LIST)));
			return State.LIVE;
		case LIVE:
			return State.LIVE;
		case NIL:
			return State.NIL;
		default:
			return State.NIL;
		}
	}
	
	@Override
	protected BDIAgentState reason() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected BDIPlan selectFromAPL(BDIGoal goal, List<BDIPlan> plans, List<BDIBelief> beliefs) {
		return plans.get(0);
	}

	@Override
	public BDIGoal selectGoal(Set<BDIGoal> goals, List<BDIBelief> beliefs) {
		return this.waitingGoals.iterator().next();
	}

	@Override
	protected void updateBeliefs(List<BDIBelief> newBeliefs) {
		for (BDIBelief belief : newBeliefs) {
			this.beliefs.add(belief);
		}
	}
	
	@Override
	protected void initActionFactory() {
		this.actionFactory = new BDIDefaultActionFactory();	
	}

	private enum State {
		INIT,
		LIVE,
		NIL;
	}
	
	/**
	 * @author $Author: mbrigaud$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public class SendMessageAction extends BDIAgent.SendMessageAction {
		@Override
		public BDIActionStatus execute(Object[] beliefs) {
			BDIActionStatus status = super.execute(beliefs);

			return status;
		}
	}
	
	/**
	 * @author $Author: mbrigaud$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public class WaitEventAction extends BDIAgent.WaitEventAction {
		@Override
		public BDIActionStatus execute(Object[] beliefs) {
			return super.execute(beliefs);
		}
	}
}
