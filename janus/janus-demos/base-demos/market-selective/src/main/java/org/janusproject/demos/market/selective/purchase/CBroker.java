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
package org.janusproject.demos.market.selective.purchase;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.selective.influence.TransfertInfluence;
import org.janusproject.demos.market.selective.influence.TravelRequestInfluence;
import org.janusproject.demos.market.selective.message.ReadyToStartMessage;
import org.janusproject.demos.market.selective.message.TravelContractGroupMessage;
import org.janusproject.demos.market.selective.message.TravelRequestMessage;
import org.janusproject.demos.market.selective.travel.TravelDestination;
import org.janusproject.demos.market.selective.travel.TravelSelectionCritera;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentsignal.LastSignalAdapter;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Broker at the client side.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(fixedParameters = {})
public class CBroker extends Role {

	private State state;
	private AgentAddress client;
	private TravelDestination travelDestination;
	private TravelSelectionCritera critera;
	private GroupAddress contractGroup;
	private AgentAddress contractProvider;
	private LastSignalAdapter<TransfertInfluence> signalListener = new LastSignalAdapter<TransfertInfluence>(TransfertInfluence.class);

	@Override
	public Status activate(Object... parameters) {
		this.state = State.WAIT_CLIENT;
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {
		this.state = Run();
		return StatusFactory.ok(this);
	}

	private State Run() {
		switch (this.state) {
		case WAIT_CLIENT:
			for (Message m : getMailbox()) {
				if (m instanceof TravelRequestMessage) {
					sendMessage(Client.class, ((RoleAddress)m.getSender()).getPlayer(), new ReadyToStartMessage());
					TravelRequestMessage cqm = (TravelRequestMessage) m;
					this.travelDestination = cqm.getDestination();
					if (this.travelDestination != null) {
						this.critera = cqm.getCritera();
						this.client = ((RoleAddress)cqm.getSender()).getPlayer();
						return State.FORWARD_CLIENT_REQUEST;
					}
				}
			}
			return State.WAIT_CLIENT;

		case FORWARD_CLIENT_REQUEST: {
			print(Locale.getString(CBroker.class, "FORWARD_CLIENT_QUERY", this.client)); //$NON-NLS-1$
			addSignalListener(this.signalListener);
			fireSignal(new TravelRequestInfluence(this, this.travelDestination, this.critera));
			return State.WAIT_CONTRACT_GROUP;
		}
		case WAIT_CONTRACT_GROUP: {
			TransfertInfluence influence = this.signalListener.getLastReceivedSignal();
			if (influence != null) {
				this.signalListener.clear();
				removeSignalListener(this.signalListener);
				this.contractProvider = (AgentAddress) influence.getValues()[0];
				this.contractGroup = (GroupAddress) influence.getValues()[1];
				return State.FORWARD_CONTRACT_GROUP;
			}
			return State.WAIT_CONTRACT_GROUP;
		}
		case FORWARD_CONTRACT_GROUP:
			print(Locale.getString(CBroker.class, "FORWARD_GROUP", this.client)); //$NON-NLS-1$
			sendMessage(Client.class, this.client, new TravelContractGroupMessage(this.contractProvider, this.contractGroup));
			leaveMe();
			return State.WAIT_CLIENT;
		default:
			return this.state;
		}
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum State {

		/**
		 * Client send a request to the broker.
		 */
		WAIT_CLIENT,

		/**
		 * Forward client request.
		 */
		FORWARD_CLIENT_REQUEST,

		/**
		 * Waiting contract group.
		 */
		WAIT_CONTRACT_GROUP,

		/**
		 * Forward contract group to client.
		 */
		FORWARD_CONTRACT_GROUP;
	}

}
