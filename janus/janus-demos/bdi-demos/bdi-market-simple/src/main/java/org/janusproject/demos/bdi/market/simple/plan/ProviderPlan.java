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
package org.janusproject.demos.bdi.market.simple.plan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.janusproject.demos.bdi.market.simple.BeliefType;
import org.janusproject.demos.bdi.market.simple.agent.ProviderAgent.BuildProposalAction;
import org.janusproject.demos.bdi.market.simple.agent.ProviderAgent.SendMessageAction;
import org.janusproject.demos.bdi.market.simple.capacity.Proposal;
import org.janusproject.demos.bdi.market.simple.goal.ProviderGoal;
import org.janusproject.demos.bdi.market.simple.message.ReadyToStartMessage;
import org.janusproject.demos.bdi.market.simple.message.TravelProposalMessage;
import org.janusproject.demos.bdi.market.simple.message.TravelRequestMessage;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.bdi.BDIAction;
import org.janusproject.kernel.agent.bdi.BDIActionStatus;
import org.janusproject.kernel.agent.bdi.BDIActionStatusType;
import org.janusproject.kernel.agent.bdi.BDIBelief;
import org.janusproject.kernel.agent.bdi.BDIBeliefType;
import org.janusproject.kernel.agent.bdi.BDIGoal;
import org.janusproject.kernel.agent.bdi.BDIPlan;
import org.janusproject.kernel.agent.bdi.BDIPlanStatus;
import org.janusproject.kernel.agent.bdi.BDIPlanStatusType;
import org.janusproject.kernel.message.Message;

/**
 * 
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ProviderPlan extends BDIPlan{
	/**
	 * 
	 * Send ReadyToStartMessage to Broker
	 * Wait ReadyToStartMessage from Broker
	 * Wait TravelRequestMessage from Broker
	 * Build proposal
	 * Send TravelProposalMessage to Broker
	 * Wait TravelSelectionMessage from Broker
	 */
	private static List<Class<? extends BDIAction>> actionList = new ArrayList<Class<? extends BDIAction>>();
	static {
		actionList.add(SendMessageAction.class);
		//actionList.add(WaitEventAction.class);
		//actionList.add(WaitEventAction.class);
		actionList.add(BuildProposalAction.class);
		actionList.add(SendMessageAction.class);
		//actionList.add(WaitEventAction.class);
	}
	
	private List<AgentAddress> receivers = null;
	private Message message = null;
	private State state = State.INIT;
	
	/**
	 * @param actionList
	 */
	public ProviderPlan(List<BDIAction> actionList) {
		super(actionList);
		this.message = new ReadyToStartMessage();
	}
	
	public static Collection<Class<? extends BDIAction>> getRequiredActions() {
		return ProviderPlan.actionList;
	}

	@Override
	public boolean context(List<BDIBelief> beliefs) {
		return true;
	}

	@Override
	public Class<? extends BDIGoal> getSupportedGoalType() {
		return ProviderGoal.class;
	}

	@Override
	public boolean isRelevant(Class<? extends BDIGoal> goal) {
		return (goal == ProviderGoal.class) ? true : false;
	}
	
	@Override
	public BDIPlanStatus execute(int actionIndex, List<BDIBelief> beliefs) {
		processBeliefs(beliefs);
		
		switch(this.state) {
		case INIT:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case INFORM_BROKER_READY:
			System.out.println("Provider: Send ReadyToStartMessage to Broker"); //$NON-NLS-1$
			this.message = new ReadyToStartMessage();
			break;
		case WAITING_FOR_BROKER_REQUEST:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case BUILD_PROPOSAL:
			System.out.println("Provider: Build proposal"); //$NON-NLS-1$
			break;
		case WAIT_PROPOSAL:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case SEND_PROPOSAL_TO_BROKER:
			System.out.println("Provider: Send TravelProposalMessage to Broker"); //$NON-NLS-1$
			break;
		case BYE_BYE:
			return new BDIPlanStatus(BDIPlanStatusType.SUCCESSFUL);
		default:
			throw new IllegalStateException();
	}
	
		BDIAction action = this.actions.get(actionIndex);
		Object parameters[] = null;
		
		assert(action != null);
		
		if (!action.isExecutable(beliefs))
			return new BDIPlanStatus(BDIPlanStatusType.FAILED);
		
		if (action instanceof SendMessageAction) {
			parameters = new Object[]{this.receivers, this.message};
		}
		
		BDIActionStatus actionStatus = action.execute(parameters);
		if (actionStatus.getType() == BDIActionStatusType.FAILED)
			return new BDIPlanStatus(BDIPlanStatusType.FAILED);
		
		this.state = this.state.nextState();

		return new BDIPlanStatus(BDIPlanStatusType.IN_PROGRESS);
	}
	
	@SuppressWarnings("unchecked")
	private void processBeliefs(List<BDIBelief> beliefs) {
		switch(this.state) {
		case INIT:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BeliefType.BROKERS_LIST) {
					this.receivers = (List<AgentAddress>) belief.getBelief();
					this.state = this.state.nextState();
				}
			}
			break;
			
		case WAITING_FOR_BROKER_REQUEST:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BDIBeliefType.MESSAGE) {
					Message msg = (Message) belief.getBelief();
					
					if (msg instanceof TravelRequestMessage) {
						System.out.println("Provider : TravelRequestMessage from Broker received"); //$NON-NLS-1$
						this.state = this.state.nextState();
					}
				}
			}
			break;
			
		case WAIT_PROPOSAL:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BeliefType.PROVIDER_PROPOSAL) {
					this.message = new TravelProposalMessage((Proposal) belief.getBelief());
					this.state = this.state.nextState();
				}
			}
			break;
		case BUILD_PROPOSAL:
		case BYE_BYE:
		case INFORM_BROKER_READY:
		case SEND_PROPOSAL_TO_BROKER:
			// FIXME: Something to do?
			break;
		default:
			throw new IllegalStateException();
		}
	}
	
	private enum State {
		INIT,
		
		INFORM_BROKER_READY,
		
		WAITING_FOR_BROKER_REQUEST,

		BUILD_PROPOSAL,

		WAIT_PROPOSAL,
		
		SEND_PROPOSAL_TO_BROKER,
		
		BYE_BYE;
		
		public State nextState() {
			if (this.ordinal() < State.values().length-1)
				return State.values()[this.ordinal()+1];
			
			return this;
		}
	}
}
