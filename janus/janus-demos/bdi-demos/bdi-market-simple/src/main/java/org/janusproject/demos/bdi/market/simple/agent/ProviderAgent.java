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
import org.janusproject.demos.bdi.market.simple.capacity.Proposal;
import org.janusproject.demos.bdi.market.simple.goal.ProviderGoal;
import org.janusproject.demos.bdi.market.simple.goal.SellerGoal;
import org.janusproject.demos.bdi.market.simple.message.TravelSelectionMessage;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agent.bdi.BDIAction;
import org.janusproject.kernel.agent.bdi.BDIActionStatus;
import org.janusproject.kernel.agent.bdi.BDIActionStatusType;
import org.janusproject.kernel.agent.bdi.BDIAgent;
import org.janusproject.kernel.agent.bdi.BDIAgentState;
import org.janusproject.kernel.agent.bdi.BDIBelief;
import org.janusproject.kernel.agent.bdi.BDIBeliefType;
import org.janusproject.kernel.agent.bdi.BDIGoal;
import org.janusproject.kernel.agent.bdi.BDIPlan;
import org.janusproject.kernel.agent.bdi.event.BDIBeliefEvent;
import org.janusproject.kernel.agent.bdi.event.BDIGoalEvent;
import org.janusproject.kernel.agent.bdi.event.BDIGoalEventType;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.random.RandomNumber;



/** A provider in the market example.
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={Environment.class}
)
public class ProviderAgent extends BDIAgent {

	private static final long serialVersionUID = -4916982362260760216L;
	
	/**
	 */
	public static int providerCount = 0;
	
	private State state = State.INIT;
	private WeakReference<Environment> environment;

	/**
	 */
	public ProviderAgent() {
		//
	}
	
	@Override
	public Status activate(Object... parameters) {
		this.environment = new WeakReference<Environment>((Environment)parameters[0]);
		++providerCount;
		fireSignal(new BDIBeliefEvent(new BDIBelief(
				this.getAddress(), 
				BeliefType.PROVIDER_ADDRESS)));
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
	
	@Override
	protected void processMessages() {
		for (Message msg : getMailbox()) {
			if (msg instanceof TravelSelectionMessage) {
				if (((TravelSelectionMessage) msg).isProposalSelected()) {
					fireSignal(new BDIGoalEvent(new SellerGoal(), BDIGoalEventType.GOAL_CHANGED));
					fireSignal(new BDIBeliefEvent(new BDIBelief(msg, BDIBeliefType.MESSAGE)));
				}
			}
			else {
				fireSignal(new BDIBeliefEvent(new BDIBelief(msg, BDIBeliefType.MESSAGE)));
			}
		}
	}
	
	private State run() {
		switch (this.state) {
		case INIT:
			fireSignal(new BDIGoalEvent(new ProviderGoal(), BDIGoalEventType.GOAL_ARRIVED));
			
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
	
	/**
	 * 
	 */
	@Override
	protected BDIAgentState reason() {
		return BDIAgentState.NO_GOAL_SELECTED;
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
			
			if (status.getType() == BDIActionStatusType.FAILED)
				return status;

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
	
	/**
	 * @author $Author: mbrigaud$
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public class BuildProposalAction implements BDIAction {
		@Override
		public boolean isExecutable(List<BDIBelief> beliefs) {
			return true;
		}
		
		@SuppressWarnings("synthetic-access")
		@Override
		public BDIActionStatus execute(Object[] beliefs) {
			Proposal proposal = null;
			
			proposal = new Proposal(
					ProviderAgent.this.getAddress(), 
					RandomNumber.nextDouble() * 100, 
					RandomNumber.nextDouble() * 1000);
			
			ProviderAgent.this.fireSignal(new BDIBeliefEvent(new BDIBelief(
					proposal, 
					BeliefType.PROVIDER_PROPOSAL)));

			return new BDIActionStatus(BDIActionStatusType.SUCCESSFUL);
		}	
	}
}
