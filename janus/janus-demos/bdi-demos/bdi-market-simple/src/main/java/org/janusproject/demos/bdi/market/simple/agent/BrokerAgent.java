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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.janusproject.demos.bdi.market.simple.BeliefType;
import org.janusproject.demos.bdi.market.simple.capacity.Proposal;
import org.janusproject.demos.bdi.market.simple.goal.BrokerGoal;
import org.janusproject.demos.bdi.market.simple.message.TravelProposalMessage;
import org.janusproject.demos.bdi.market.simple.message.TravelRequestMessage;
import org.janusproject.demos.bdi.market.simple.message.TravelSelectionMessage;
import org.janusproject.demos.bdi.market.simple.travel.TravelDestination;
import org.janusproject.demos.bdi.market.simple.travel.TravelSelectionCritera;
import org.janusproject.kernel.address.AgentAddress;
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


/**
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype(
		fixedParameters={Integer.class}
		)
public class BrokerAgent extends BDIAgent {

	private static final long serialVersionUID = 3116660344802418208L;

	private State state = State.INIT;
	private AgentAddress client;
	private TravelDestination travelDestination;
	private TravelSelectionCritera critera;
	private final List<Proposal> proposals = new ArrayList<Proposal>();
	private Integer providerCount;

	/**
	 */
	public BrokerAgent() {
		//
	}

	@Override
	public Status activate(Object... parameters) {
		this.providerCount = (Integer)parameters[0];
		fireSignal(new BDIBeliefEvent(new BDIBelief(this.providerCount, BeliefType.BROKER_PROVIDER_COUNT)));
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {
		processMessages();
		super.live();
		this.state = run();

		return StatusFactory.ok(this);
	}

	private State run() {
		switch(this.state) {
		case INIT :
			fireSignal(new BDIGoalEvent(new BrokerGoal(), BDIGoalEventType.GOAL_ARRIVED));
			return State.RUN;
		case RUN:
			processMessages();
			return State.RUN;
		default:
			return this.state;
		}
	}

	@Override
	protected void processMessages() {
		for (Message msg : getMailbox()) {
			if (msg instanceof TravelRequestMessage) {
				TravelRequestMessage cqm = (TravelRequestMessage) msg;
				this.travelDestination = cqm.getDestination();
				if (this.travelDestination != null) {
					this.critera = cqm.getCritera();
					fireSignal(new BDIBeliefEvent(new BDIBelief(
							this.critera, 
							BeliefType.BROKER_CRITERA)));

					this.client = cqm.getSender();
					fireSignal(new BDIBeliefEvent(new BDIBelief(
							this.client, 
							BeliefType.BROKER_CLIENT)));
				}
			}
			else if (msg instanceof TravelProposalMessage) {
				this.proposals.add(((TravelProposalMessage) msg).getProposal());
				if (this.proposals.size() == this.providerCount) {
					fireSignal(new BDIBeliefEvent(new BDIBelief(
							this.proposals, 
							BeliefType.BROKER_PROPOSALS)));
				}
			}
			else {
				fireSignal(new BDIBeliefEvent(new BDIBelief(msg, BDIBeliefType.MESSAGE)));
			}
		}
	}

	@Override
	protected void initActionFactory() {
		this.actionFactory = new BDIDefaultActionFactory();	
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

	private enum State {
		INIT,
		RUN;
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
	public class BroadcastMessageAction extends BDIAgent.BroadcastMessageAction {
		@Override
		public boolean isExecutable(List<BDIBelief> beliefs) {
			return true;
		}

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
	public class SelectProposalAction implements BDIAction {
		@Override
		public boolean isExecutable(List<BDIBelief> beliefs) {
			return true;
		}

		@SuppressWarnings({ "unchecked", "synthetic-access" })
		@Override
		public BDIActionStatus execute(Object[] beliefs) {
			Proposal[] proposalArray = null;

			if (beliefs.length == 1 && beliefs[0] != null) {
				List<Proposal> beliefList = (List<Proposal>) beliefs[0];
				proposalArray = beliefList.toArray(new Proposal[beliefList.size()]);
			}


			if (proposalArray == null)
				return new BDIActionStatus(BDIActionStatusType.FAILED);

			try {
				Proposal best = null;

				if (BrokerAgent.this.critera == TravelSelectionCritera.COST) {
					best = findLowestCostProposal(proposalArray);
				} else {
					best = findShortestTimeProposal(proposalArray);
				}

				assert (best != null);

				boolean isSelected;
				for (Proposal p : proposalArray) {
					isSelected = (p.getProvider().equals(best.getProvider()));

					BrokerAgent.this.sendMessage(new TravelSelectionMessage(isSelected), p.getProvider());
				}

				BrokerAgent.this.proposals.clear();
			} catch (Throwable e) {
				error(e.getLocalizedMessage());
				return new BDIActionStatus(BDIActionStatusType.FAILED);
			}

			return new BDIActionStatus(BDIActionStatusType.SUCCESSFUL);
		}

		private Proposal findLowestCostProposal(Proposal[] proposals) {
			Proposal best = null;

			for(Proposal p : proposals) {
				if (p != null) {
					if (best == null || p.getCost() < best.getCost()) {
						best = p;
					}
				}
			}

			return (best != null) ? best : null;
		}

		private Proposal findShortestTimeProposal(Proposal[] proposals) {
			Proposal best = null;

			for(Proposal p : proposals) {
				if (p != null) {
					if (best == null || p.getDuration() < best.getDuration()) {
						best = p;
					}
				}
			}

			return (best != null) ? best : null;
		}
	}
}
