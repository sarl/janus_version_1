/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010, 2012 Janus Core Developers
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
package org.janusproject.demos.market.selective.providing;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.selective.capacity.Proposal;
import org.janusproject.demos.market.selective.contracting.ContractingOrganization;
import org.janusproject.demos.market.selective.contracting.Seller;
import org.janusproject.demos.market.selective.influence.ContractTerminationInfluence;
import org.janusproject.demos.market.selective.message.ReadyToStartMessage;
import org.janusproject.demos.market.selective.message.TravelContractGroupMessage;
import org.janusproject.demos.market.selective.message.TravelProposalMessage;
import org.janusproject.demos.market.selective.message.TravelRequestMessage;
import org.janusproject.demos.market.selective.message.TravelSelectionMessage;
import org.janusproject.demos.market.selective.travel.TravelDestination;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentsignal.LastSignalAdapter;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.random.RandomNumber;

/**
 * Provider something through a broker.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(fixedParameters = {})
public class Provider extends Role {

	private State state;
	private AgentAddress broker;
	private GroupAddress contractGroup;
	private TravelDestination destination;
	private Proposal proposal;

	private final LastSignalAdapter<ContractTerminationInfluence> signalListener = new LastSignalAdapter<ContractTerminationInfluence>(ContractTerminationInfluence.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		this.state = State.INFORM_BROKER_READY;
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {
		this.state = Run();
		return StatusFactory.ok(this);
	}

	private State Run() {
		switch (this.state) {
		case INFORM_BROKER_READY: {
			sendMessage(PBroker.class, new ReadyToStartMessage());
			return State.WAITING_FOR_BROKER_READY;
		}

		case WAITING_FOR_BROKER_READY: {
			for (Message msg : getMailbox()) {
				if (msg instanceof ReadyToStartMessage) {
					print(Locale.getString(Provider.class, "WAITING_PBROKER_REQUEST")); //$NON-NLS-1$
					return State.WAITING_FOR_BROKER_REQUEST;
				}
			}
			return State.INFORM_BROKER_READY;
		}

		case WAITING_FOR_BROKER_REQUEST: {
			for (Message msg : getMailbox()) {
				if (msg instanceof TravelRequestMessage) {
					TravelRequestMessage cqm = (TravelRequestMessage) msg;
					this.broker = ((RoleAddress)cqm.getSender()).getPlayer();
					this.destination = cqm.getDestination();
					print(Locale.getString(Provider.class, "RECEIVE_PBROKER_REQUEST", this.destination.toString()));//$NON-NLS-1$
					return State.BUILD_PROPOSAL;
				}
			}
			return State.WAITING_FOR_BROKER_REQUEST;
		}
		case BUILD_PROPOSAL:

			this.proposal = new Proposal(getPlayer(), RandomNumber.nextDouble() * 100, RandomNumber.nextDouble() * 1000);
			print(Locale.getString(Provider.class, "SEND_PROPOSAL", //$NON-NLS-1$
					this.destination.toString(), this.proposal.toString()));
			sendMessage(PBroker.class, this.broker, new TravelProposalMessage(this.proposal));

			return State.WAIT_PROPOSAL_ACCEPTANCE;
		case WAIT_PROPOSAL_ACCEPTANCE:
			for (Message msg : getMailbox()) {
				if (msg instanceof TravelSelectionMessage) {
					TravelSelectionMessage tsm = (TravelSelectionMessage) msg;
					if (tsm.isProposalSelected()) {

						print(Locale.getString(Provider.class, "RECEIVE_PROPOSAL_ACCEPTANCE")); //$NON-NLS-1$

						return State.CREATING_CONTRACT_GROUP;
					}

					print(Locale.getString(Provider.class, "RECEIVE_PROPOSAL_REJECTION")); //$NON-NLS-1$

					return State.CONTRACT_REFUSED;
				}
			}
			return State.WAIT_PROPOSAL_ACCEPTANCE;

		case CREATING_CONTRACT_GROUP: {
			print(Locale.getString(Provider.class, "CREATING_CONTRACT_GROUP")); //$NON-NLS-1$
			this.contractGroup = getOrCreateGroup(ContractingOrganization.class);
			if (requestRole(Seller.class, this.contractGroup, this.destination)!=null) {
				sendMessage(PBroker.class, this.broker, new TravelContractGroupMessage(getPlayer(), this.contractGroup));
				addSignalListener(this.signalListener);
				return State.WAIT_FOR_CONTRACT_TERMINATION;

			}
			return State.CREATING_CONTRACT_GROUP;
		}
		case WAIT_FOR_CONTRACT_TERMINATION:
			ContractTerminationInfluence influence = this.signalListener.getLastReceivedSignal();
			this.signalListener.clear();
			if (influence != null) {
				removeSignalListener(this.signalListener);
				return State.CONTRACT_PASSED;
			}
			return State.WAIT_FOR_CONTRACT_TERMINATION;
		case CONTRACT_PASSED:
			print(Locale.getString(Provider.class, "CONTRACT_PASSED")); //$NON-NLS-1$
			leaveMe();
			return State.NIL;
		case CONTRACT_REFUSED:
			print(Locale.getString(Provider.class, "CONTRACT_REFUSED")); //$NON-NLS-1$
			leaveMe();
			return State.NIL;
		case NIL:
		default:
			return this.state;
		}
	}

	/**
	 * State of a provider
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum State {

		/**
		 * Inform broker that I'm ready to start
		 */
		INFORM_BROKER_READY,

		/**
		 * Wait until he receives a ready to start message from PBroker
		 */
		WAITING_FOR_BROKER_READY,

		/**
		 * Waiting for a broker request.
		 */
		WAITING_FOR_BROKER_REQUEST,

		/**
		 * Building proposal.
		 */
		BUILD_PROPOSAL,

		/**
		 * Wait for proposal acceptance.
		 */
		WAIT_PROPOSAL_ACCEPTANCE,

		/**
		 * Creating contract group for a broker.
		 */
		CREATING_CONTRACT_GROUP,

		/**
		 * Wait for contract termination.
		 */
		WAIT_FOR_CONTRACT_TERMINATION,

		/**
		 * Terminate contract on acceptance.
		 */
		CONTRACT_PASSED,

		/**
		 * Terminate contract on a refusal.
		 */
		CONTRACT_REFUSED,

		/**
		 * Do nothing.
		 */
		NIL;

	}

}
