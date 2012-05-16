/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
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
 * @version $Name$ $Revision$ $Date$
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
		this.environment = new WeakReference<>((Environment)parameters[0]);
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
