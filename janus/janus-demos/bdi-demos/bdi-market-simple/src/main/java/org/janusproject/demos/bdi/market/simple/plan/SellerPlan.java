package org.janusproject.demos.bdi.market.simple.plan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.janusproject.demos.bdi.market.simple.BeliefType;
import org.janusproject.demos.bdi.market.simple.agent.ProviderAgent.SendMessageAction;
import org.janusproject.demos.bdi.market.simple.goal.SellerGoal;
import org.janusproject.demos.bdi.market.simple.message.ContractFinalizationMessage;
import org.janusproject.demos.bdi.market.simple.message.TravelContractGroupMessage;
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
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class SellerPlan extends BDIPlan {
	/**
	 * 
	 * Send TravelContractGroupMessage to Broker
	 * Wait ContractFinalizationMessage from Client
	 * Send ContractFinalizationMessage to Client
	 */
	private static List<Class<? extends BDIAction>> actionList = new ArrayList<Class<? extends BDIAction>>();
	static {
		actionList.add(SendMessageAction.class);
		//actionList.add(WaitEventAction.class);
		actionList.add(SendMessageAction.class);
	}
	
	private AgentAddress address = null;
	private List<AgentAddress> receivers = null;
	private Message message = null;
	private State state = State.SEND_CONTRACT_GROUP_TO_BROKER;
	
	/**
	 * @param actionList
	 */
	public SellerPlan(List<BDIAction> actionList) {
		super(actionList);
	}

	public static Collection<Class<? extends BDIAction>> getRequiredActions() {
		return SellerPlan.actionList;
	}
	
	@Override
	public boolean context(List<BDIBelief> beliefs) {
		return true;
	}

	@Override
	public Class<? extends BDIGoal> getSupportedGoalType() {
		return SellerGoal.class;
	}

	@Override
	public boolean isRelevant(Class<? extends BDIGoal> goal) {
		return (goal == SellerGoal.class) ? true : false;
	}
	
	@Override
	public BDIPlanStatus execute(int actionIndex, List<BDIBelief> beliefs) {
		processBeliefs(beliefs);
		switch(this.state) {
		case SEND_CONTRACT_GROUP_TO_BROKER:
			System.out.println("Seller: Send TravelContractGroupMessage to Broker"); //$NON-NLS-1$
			this.message = new TravelContractGroupMessage(this.address);
			break;
		case WAIT_BUYER:
			return new BDIPlanStatus(BDIPlanStatusType.PAUSED);
		case RESPOND_TO_BUYER:
			System.out.println("Seller: Send ContractFinalizationMessage to Client"); //$NON-NLS-1$
			this.message = new ContractFinalizationMessage();
			break;
		case GOOD_BYE:
			break;
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
		case SEND_CONTRACT_GROUP_TO_BROKER:
			for (BDIBelief belief : beliefs) {		
				if (belief.getType() == BeliefType.PROVIDER_ADDRESS) {
					this.address = (AgentAddress) belief.getBelief();
				}
				if (belief.getType() == BeliefType.BROKERS_LIST) {
					this.receivers = (List<AgentAddress>) belief.getBelief();
				}
			}
			break;
		case WAIT_BUYER:
			for (BDIBelief belief : beliefs) {		
				if (belief.getType() == BDIBeliefType.MESSAGE) {
					Message msg = (Message) belief.getBelief();
					
					if (msg instanceof ContractFinalizationMessage) {
						this.receivers.clear();
						this.receivers.add(msg.getSender());
						this.state = this.state.nextState();
					}
				}
			}
			break;
		case GOOD_BYE:
		case RESPOND_TO_BUYER:
			// FIXME: Something to do?
			break;
		}
	}
	
	private enum State {
		SEND_CONTRACT_GROUP_TO_BROKER,
		
		WAIT_BUYER,
		
		RESPOND_TO_BUYER,

		GOOD_BYE;
		
		public State nextState() {
			if (this.ordinal() < State.values().length-1)
				return State.values()[this.ordinal()+1];
			
			return this;
		}
	}
}
