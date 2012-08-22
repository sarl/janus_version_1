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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.janusproject.demos.bdi.market.simple.BeliefType;
import org.janusproject.demos.bdi.market.simple.agent.ClientAgent.SendMessageAction;
import org.janusproject.demos.bdi.market.simple.goal.ClientGoal;
import org.janusproject.demos.bdi.market.simple.message.ContractFinalizationMessage;
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
public class ClientPlan extends BDIPlan {
	/**
	 * 
	 * Send TravelRequestMessage to Broker
	 * Wait ReadyToStartMessage from Broker
	 * Wait TravelContractGroupMessage from Broker
	 * Send ContractFinalizationMessage to Seller
	 * Wait ContractFinalizationMessage from Seller
	 */
	private static List<Class<? extends BDIAction>> actionList = new ArrayList<Class<? extends BDIAction>>();
	static {
		actionList.add(SendMessageAction.class);
		//actionList.add(WaitEventAction.class);
		//actionList.add(WaitEventAction.class);
		actionList.add(SendMessageAction.class);
	}
	
	private List<AgentAddress> receivers = null;
	private Message message = null;
	private TravelSelectionCritera critera = null;
	private TravelDestination travelDestination = null;
	private State state = State.INIT;
	
	/**
	 * @param actions
	 */
	public ClientPlan(List<BDIAction> actions) {
		super(actions);
	}

	public static Collection<Class<? extends BDIAction>> getRequiredActions() {
		return ClientPlan.actionList;
	}

	@Override
	public Class<? extends BDIGoal> getSupportedGoalType() {
		return ClientGoal.class;
	}

	@Override
	public boolean isRelevant(Class<? extends BDIGoal> goal) {
		return (goal == ClientGoal.class) ? true : false;
	}
	
	@Override
	public boolean context(List<BDIBelief> beliefs) {
		return true;
	}
	
	@Override
	public BDIPlanStatus execute(int actionIndex, List<BDIBelief> beliefs) {
		processBeliefs(beliefs);
		
		switch(this.state) {
		case INIT:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case BROKER_CONTACT:
			System.out.println("Client: Send TravelRequestMessage to Broker"); //$NON-NLS-1$
			this.message = new TravelRequestMessage(this.travelDestination, this.critera);
			break;
		case WAITING_ACK_FROM_BROKER:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case WAITING_CONTRACT_GROUP:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case CONTACT_SELLER:
			System.out.println("Client: Send ContractFinalizationMessage to Seller"); //$NON-NLS-1$
			this.message = new ContractFinalizationMessage();
			break;
		case WAIT_SELLER_ACK:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
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
				}
				else if (belief.getType() == BeliefType.CLIENT_CRITERA) {
					this.critera = (TravelSelectionCritera) belief.getBelief();
				}
				else if (belief.getType() == BeliefType.CLIENT_TRAVEL_DESTINATION) {
					this.travelDestination = (TravelDestination) belief.getBelief();
				}
				
				if (this.receivers != null && this.critera != null && this.travelDestination != null)
					this.state = this.state.nextState();
			}
			break;
		case WAITING_ACK_FROM_BROKER:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BDIBeliefType.MESSAGE) {
					Message msg = (Message) belief.getBelief();
					
					if (msg instanceof ReadyToStartMessage) {
						System.out.println("Client : ReadyToStartMessage from Broker received"); //$NON-NLS-1$
						this.state = this.state.nextState();
					}
				}
			}
			break;
		case WAITING_CONTRACT_GROUP:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BDIBeliefType.MESSAGE) {
					Message msg = (Message) belief.getBelief();
					
					if (msg instanceof TravelContractGroupMessage) {
						System.out.println("Client : TravelContractGroupMessage from Broker received"); //$NON-NLS-1$
						this.receivers = Arrays.asList(((TravelContractGroupMessage) msg).getProvider());
						this.state = this.state.nextState();
					}
				}
			}
			break;
			
		case WAIT_SELLER_ACK:
			for (BDIBelief belief : beliefs) {
				if (belief.getType() == BDIBeliefType.MESSAGE) {
					Message msg = (Message) belief.getBelief();
					
					if (msg instanceof ContractFinalizationMessage) {
						System.out.println("Client : ContractFinalizationMessage from Seller received"); //$NON-NLS-1$
						this.state = this.state.nextState();
					}
				}
			}
			break;
		case BROKER_CONTACT:
		case BYE_BYE:
		case CONTACT_SELLER:
			//FIXME: Something to do?
			break;
		default:
			throw new IllegalStateException();
		}
	}
	
	private enum State {
		INIT,
		
		BROKER_CONTACT,
		
		WAITING_ACK_FROM_BROKER,
		
		WAITING_CONTRACT_GROUP,
		 
		CONTACT_SELLER,
		
		WAIT_SELLER_ACK,
		
		BYE_BYE;
		
		public State nextState() {
			if (this.ordinal() < State.values().length-1)
				return State.values()[this.ordinal()+1];
			
			return this;
		}
	}
}
