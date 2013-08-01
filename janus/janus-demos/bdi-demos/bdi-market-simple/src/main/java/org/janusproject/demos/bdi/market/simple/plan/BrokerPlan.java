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
import org.janusproject.demos.bdi.market.simple.agent.BrokerAgent.BroadcastMessageAction;
import org.janusproject.demos.bdi.market.simple.agent.BrokerAgent.SelectProposalAction;
import org.janusproject.demos.bdi.market.simple.agent.BrokerAgent.SendMessageAction;
import org.janusproject.demos.bdi.market.simple.agent.ProviderAgent;
import org.janusproject.demos.bdi.market.simple.capacity.Proposal;
import org.janusproject.demos.bdi.market.simple.goal.BrokerGoal;
import org.janusproject.demos.bdi.market.simple.message.ReadyToStartMessage;
import org.janusproject.demos.bdi.market.simple.message.TravelContractGroupMessage;
import org.janusproject.demos.bdi.market.simple.message.TravelRequestMessage;
import org.janusproject.demos.bdi.market.simple.travel.TravelDestination;
import org.janusproject.demos.bdi.market.simple.travel.TravelSelectionCritera;
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
public class BrokerPlan extends BDIPlan{
	/**
	 * 
	 * Wait Client request
	 * Send ReadyToStartMessage to Client
	 * Wait ReadyToStartMessage from all Provider
	 * Broadcast TravelRequestMessage to all Provider
	 * Wait TravelProposalMessage from all Provider
	 * Select proposal
	 * Send TravelSelectionMessage to all Provider
	 * Wait TravelContractGroupMessage from Provider
	 * Send TravelContractGroupMessage to Client
	 */
	private static List<Class<? extends BDIAction>> actionList = new ArrayList<Class<? extends BDIAction>>();
	static {
		//actionList.add(WaitEventAction.class);
		actionList.add(SendMessageAction.class);
		//actionList.add(WaitEventAction.class);
		actionList.add(BroadcastMessageAction.class);
		//actionList.add(WaitEventAction.class);
		actionList.add(SelectProposalAction.class);
		actionList.add(SendMessageAction.class);
		//actionList.add(WaitEventAction.class);
		actionList.add(SendMessageAction.class);
	}
	
	private List<AgentAddress> receivers = null;
	private Message message = null;
	private TravelSelectionCritera critera = null;
	private TravelDestination travelDestination = null;
	private AgentAddress client;
	private List<AgentAddress> providersReady = new ArrayList<AgentAddress>();
	private List<Proposal> proposals = new ArrayList<Proposal>();
	private State state = State.WAIT_CLIENT;
	private AgentAddress contractProvider = null;
	
	/**
	 * @param actions
	 */
	public BrokerPlan(List<BDIAction> actions) {
		super(actions);
	}

	@Override
	public boolean context(List<BDIBelief> beliefs) {
		return true;
	}

	@Override
	public Class<? extends BDIGoal> getSupportedGoalType() {
		return BrokerGoal.class;
	}

	@Override
	public boolean isRelevant(Class<? extends BDIGoal> goal) {
		return (goal == BrokerGoal.class) ? true : false;
	}
	
	public static Collection<Class<? extends BDIAction>> getRequiredActions() {
		return BrokerPlan.actionList;
	}

	@Override
	public BDIPlanStatus execute(int actionIndex, List<BDIBelief> beliefs) {
		processBeliefs(beliefs);

		switch (this.state) {
		case WAIT_CLIENT:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case RESPOND_TO_CLIENT:
			this.message = new ReadyToStartMessage();
			this.receivers = new ArrayList<AgentAddress>();
			this.receivers.add(this.client);
			System.out.println("Broker: Send ReadyToStartMessage to Client"); //$NON-NLS-1$
			break;
		case WAITING_FOR_PROVIDER_READY:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case CONTACT_PROVIDER:
			this.receivers = this.providersReady;
			this.message = new TravelRequestMessage(this.travelDestination, this.critera);
			System.out.println("Broker: Broadcast TravelRequestMessage to all Provider"); //$NON-NLS-1$
			break;
		case WAIT_PROVIDER_PROPOSAL:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case SELECT_PROPOSAL:
			System.out.println("Broker: Select proposal"); //$NON-NLS-1$
			break;
		case WAIT_CONTRACT_GROUP:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case FORWARD_CONTRACT_GROUP:
			System.out.println("Broker: Send TravelContractGroupMessage to Client"); //$NON-NLS-1$
			this.message = new TravelContractGroupMessage(this.contractProvider);
			this.receivers.clear();
			this.receivers.add(this.client);
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
		else if (action instanceof BroadcastMessageAction){
			parameters = new Object[]{this.receivers, this.message};
		}
		else if (action instanceof SelectProposalAction){
			parameters = new Object[]{this.proposals};
		}
		else {
			parameters = new Object[]{};
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
		case WAIT_CLIENT:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BeliefType.BROKER_CRITERA) {
					this.critera = (TravelSelectionCritera) belief.getBelief();
					if (this.critera != null && this.client != null)
						this.state = this.state.nextState();
				}
				else if (belief.getType() == BeliefType.BROKER_CLIENT) {
					this.client = (AgentAddress) belief.getBelief();	
					if (this.critera != null && this.client != null)
						this.state = this.state.nextState();
				}
			}
			break;

		case WAITING_FOR_PROVIDER_READY:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BDIBeliefType.MESSAGE) {
					Message msg = (Message) belief.getBelief();
					
					if (msg instanceof ReadyToStartMessage) {
						System.out.println("Broker: ReadyToStartMessage received from Provider"); //$NON-NLS-1$
						this.providersReady.add((AgentAddress)msg.getSender());
						if (this.providersReady.size() == ProviderAgent.providerCount) {
							this.state = this.state.nextState();
						}
					}
				}
			}
			break;
			
		case WAIT_PROVIDER_PROPOSAL:
			for (BDIBelief belief : beliefs) {	
				if (belief.getType() == BeliefType.BROKER_PROPOSALS) {
					System.out.println("Broker: BROKER_PROPOSALS received from BrokerAgent"); //$NON-NLS-1$
					this.proposals = (List<Proposal>) belief.getBelief();
					this.state = this.state.nextState();
				}
			}
			break;
			
		case WAIT_CONTRACT_GROUP:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BDIBeliefType.MESSAGE) {
					Message msg = (Message) belief.getBelief();
					
					if (msg instanceof TravelContractGroupMessage) {
						System.out.println("Broker: TravelContractGroupMessage received from Seller"); //$NON-NLS-1$
						this.contractProvider = ((TravelContractGroupMessage) msg).getProvider();
						this.state = this.state.nextState();
					}
				}
			}
			break;
			
		case BYE_BYE:
		case CONTACT_PROVIDER:
		case FORWARD_CONTRACT_GROUP:
		case RESPOND_TO_CLIENT:
		case SELECT_PROPOSAL:
		default:
			break;
		}
	}
			
	private enum State {
		WAIT_CLIENT,
		
		RESPOND_TO_CLIENT,

		WAITING_FOR_PROVIDER_READY,

		CONTACT_PROVIDER,
		
		WAIT_PROVIDER_PROPOSAL,

		SELECT_PROPOSAL,

		WAIT_CONTRACT_GROUP,

		FORWARD_CONTRACT_GROUP, 
		
		BYE_BYE;
		
		public State nextState() {
			if (this.ordinal() < State.values().length-1)
				return State.values()[this.ordinal()+1];
			
			return this;
		}
	}
}


