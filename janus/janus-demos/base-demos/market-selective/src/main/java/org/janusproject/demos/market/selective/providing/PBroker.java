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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.selective.capacity.FindLowestCostProposalCapacity;
import org.janusproject.demos.market.selective.capacity.FindShortestTimeProposalCapacity;
import org.janusproject.demos.market.selective.capacity.Proposal;
import org.janusproject.demos.market.selective.influence.TransfertInfluence;
import org.janusproject.demos.market.selective.influence.TravelRequestInfluence;
import org.janusproject.demos.market.selective.message.ReadyToStartMessage;
import org.janusproject.demos.market.selective.message.TravelContractGroupMessage;
import org.janusproject.demos.market.selective.message.TravelProposalMessage;
import org.janusproject.demos.market.selective.message.TravelRequestMessage;
import org.janusproject.demos.market.selective.message.TravelSelectionMessage;
import org.janusproject.demos.market.selective.travel.TravelDestination;
import org.janusproject.demos.market.selective.travel.TravelSelectionCritera;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentsignal.LastSignalAdapter;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * A broker that is contacting providers for contracts.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(fixedParameters = { Number.class })
public class PBroker extends Role {

	private TravelDestination destination;
	private TravelSelectionCritera critera;

	private int expectedProviderCount;
	private State state;
	private AgentAddress provider;
	private GroupAddress contractGroup;
	private final LastSignalAdapter<TravelRequestInfluence> signalListener = new LastSignalAdapter<TravelRequestInfluence>(TravelRequestInfluence.class);
	private final List<Proposal> proposals = new ArrayList<Proposal>();

	private Set<AgentAddress> providersReady = new HashSet<AgentAddress>();

	/**
	 */
	@SuppressWarnings("unchecked")
	public PBroker() {
		addObtainCondition(new HasAllRequiredCapacitiesCondition(Arrays.asList(FindLowestCostProposalCapacity.class, FindShortestTimeProposalCapacity.class)));
	}

	@Override
	public Status activate(Object... parameters) {
		this.expectedProviderCount = ((Number) parameters[0]).intValue();
		this.state = State.WAITING_FOR_CLIENT_BROKER;
		addSignalListener(this.signalListener);
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {
		this.state = Run();
		return StatusFactory.ok(this);
	}

	private State Run() {
		switch (this.state) {
		case WAITING_FOR_CLIENT_BROKER: {
			TravelRequestInfluence influence = this.signalListener.getLastReceivedSignal();
			if (influence != null) {
				this.destination = influence.getDestination();
				this.critera = influence.getCritera();
				print(Locale.getString(PBroker.class, "RECEIVE_CLIENT_REQUEST", this.destination.toString(), this.critera.toString()));//$NON-NLS-1$
				return State.WAITING_FOR_PROVIDER_READY;
			}
			return State.WAITING_FOR_CLIENT_BROKER;
		}

		case WAITING_FOR_PROVIDER_READY: {
			for (Message msg : getMailbox()) {
				if (msg instanceof ReadyToStartMessage) {
					this.providersReady.add(((RoleAddress)msg.getSender()).getPlayer());
					this.sendMessage(Provider.class, ((RoleAddress)msg.getSender()).getPlayer(), new ReadyToStartMessage());
				}
			}
			if (this.providersReady.size() == this.expectedProviderCount) {
				print(Locale.getString(PBroker.class, "ALL_PROVIDER_READY")); //$NON-NLS-1$
				return State.CONTACT_PROVIDER;
			}
			print(Locale.getString(PBroker.class, "PROVIDER_PARTIALLY_READY", //$NON-NLS-1$
						this.providersReady.size(),this.expectedProviderCount));					
			return State.WAITING_FOR_PROVIDER_READY;
		}

		case CONTACT_PROVIDER: {
			this.proposals.clear();
			broadcastMessage(Provider.class, new TravelRequestMessage(this.destination, this.critera));
			print(Locale.getString(PBroker.class, "REQUESTING_PROPOSALS", //$NON-NLS-1$
					this.destination.toString(), this.critera.toString()));
			return State.WAIT_PROVIDER_PROPOSAL;
		}

		case WAIT_PROVIDER_PROPOSAL:
			for (Message msg : getMailbox()) {
				if (msg instanceof TravelProposalMessage) {
					TravelProposalMessage tpm = (TravelProposalMessage) msg;
					this.proposals.add(tpm.getProposal());
					print(Locale.getString(PBroker.class, "RECEVING_PROPOSAL", //$NON-NLS-1$
							tpm.getSender().toString(), tpm.getProposal().toString()));
				}
			}
			if (this.proposals.size() == this.expectedProviderCount) {
				print(Locale.getString(PBroker.class, "ALL_PROPOSALS_RECEIVED")); //$NON-NLS-1$
				return State.SELECT_PROPOSAL;
			}
			return State.WAIT_PROVIDER_PROPOSAL;

		case SELECT_PROPOSAL:

			print(Locale.getString(PBroker.class, "SELECTING_PROPOSAL")); //$NON-NLS-1$
			Object[] proposalArray = new Proposal[this.proposals.size()];
			this.proposals.toArray(proposalArray);

			CapacityContext cc;
			try {
				if (this.critera == TravelSelectionCritera.COST) {
					cc = executeCapacityCall(FindLowestCostProposalCapacity.class, proposalArray);
				} else {
					cc = executeCapacityCall(FindShortestTimeProposalCapacity.class, proposalArray);
				}
				assert (cc != null);
				Proposal best = (Proposal) cc.getOutputValue();
				assert (best != null);

				boolean isSelected;
				for (Proposal p : this.proposals) {
					isSelected = (p.getProvider().equals(best.getProvider()));
					if (isSelected) {
						print(Locale.getString(PBroker.class, "SEND_PROPOSAL_ACCEPTANCE", //$NON-NLS-1$
								p.getProvider().toString()));
					} else {
						print(Locale.getString(PBroker.class, "SEND_PROPOSAL_REJECTION", //$NON-NLS-1$
								p.getProvider().toString()));
					}
					sendMessage(Provider.class, p.getProvider(), new TravelSelectionMessage(isSelected));

				}

				this.proposals.clear();

				return State.WAIT_CONTRACT_GROUP;
			} catch (Throwable e) {
				error(e.getLocalizedMessage());
				return State.SELECT_PROPOSAL;
			}

		case WAIT_CONTRACT_GROUP:
			for (Message msg : getMailbox()) {
				if (msg instanceof TravelContractGroupMessage) {
					TravelContractGroupMessage cgm = (TravelContractGroupMessage) msg;
					this.contractGroup = cgm.getContent(GroupAddress.class);
					if (this.contractGroup != null) {
						print(Locale.getString(PBroker.class, "CONTRACT_GROUP_RECEIVED", //$NON-NLS-1$
								this.contractGroup.toString()));
						fireSignal(new TransfertInfluence(this, this.provider, this.contractGroup));
						leaveMe();
						return State.WAITING_FOR_CLIENT_BROKER;
					}
				}
			}
			return State.WAIT_CONTRACT_GROUP;
			
		case NOTIFY_PROVIDERS:
		default:
			return this.state;
		}
	}

	/**
	 * State of a broker.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum State {

		/**
		 * Waiting for a client broker.
		 */
		WAITING_FOR_CLIENT_BROKER,

		/**
		 * Wait until all providers are ready
		 */
		WAITING_FOR_PROVIDER_READY,

		/**
		 * Contact the provider.
		 */
		CONTACT_PROVIDER,

		/**
		 * Wait for provider answer.
		 */
		WAIT_PROVIDER_PROPOSAL,

		/**
		 * Select a proposal.
		 */
		SELECT_PROPOSAL,

		/**
		 * Send proposal selection result.
		 */
		NOTIFY_PROVIDERS,

		/**
		 * Waiting for the selected provider.
		 */
		WAIT_CONTRACT_GROUP;
	}

}
