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
package org.janusproject.demos.market.simple.purchase;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.simple.influence.RequestSignal;
import org.janusproject.demos.market.simple.influence.TransfertSignal;
import org.janusproject.demos.market.simple.message.ContractGroupMessage;
import org.janusproject.demos.market.simple.message.ContractQueryMessage;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentsignal.LastSignalAdapter;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** Broker at the client side.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(
		fixedParameters={}
)
public class CBroker extends Role {

	private State state;
	private AgentAddress client;
	private Object contractDescription;
	private GroupAddress contractGroup;
	private AgentAddress contractProvider;
	private LastSignalAdapter<TransfertSignal> signalListener = new LastSignalAdapter<TransfertSignal>(TransfertSignal.class);
	
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
			for(Message m : getMailbox()) {
				if (m instanceof ContractQueryMessage) {
					ContractQueryMessage cqm = (ContractQueryMessage)m;
					this.contractDescription = cqm.getContent();
					if (this.contractDescription!=null) {
						this.client = ((RoleAddress)cqm.getSender()).getPlayer();
						return State.FORWARD_CLIENT_REQUEST;
					}
				}
			}
			return State.WAIT_CLIENT;
			
		case FORWARD_CLIENT_REQUEST:
		{
			print(Locale.getString(CBroker.class, "FORWARD_CLIENT_QUERY", this.client)); //$NON-NLS-1$
			addSignalListener(this.signalListener);
			fireSignal(new RequestSignal(this, this.contractDescription));
			return State.WAIT_CONTRACT_GROUP;
		}
		case WAIT_CONTRACT_GROUP:
		{
			TransfertSignal influence = this.signalListener.getLastReceivedSignal();
			if (influence!=null) {
				this.signalListener.clear();
				removeSignalListener(this.signalListener);
				this.contractProvider = (AgentAddress)influence.getValues()[0];
				this.contractGroup = (GroupAddress)influence.getValues()[1];
				return State.FORWARD_CONTRACT_GROUP;
			}
			return State.WAIT_CONTRACT_GROUP;
		}
		case FORWARD_CONTRACT_GROUP:
			print(Locale.getString(CBroker.class, "FORWARD_GROUP", this.client)); //$NON-NLS-1$
			sendMessage(Client.class, this.client, new ContractGroupMessage(
						this.contractProvider, this.contractGroup));
			leaveMe();
			return State.WAIT_CLIENT;
		default:
			return this.state;
		}				
	}

	/**
	 * This class defines the various possible state of the <code>CBroker</code> role
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum State {

		/** Client send a request to the broker.
		 */
		WAIT_CLIENT,
		
		/** Forward client request.
		 */
		FORWARD_CLIENT_REQUEST,
		
		/** Waiting contract group.
		 */
		WAIT_CONTRACT_GROUP,
		
		/** Forward contract group to client.
		 */
		FORWARD_CONTRACT_GROUP;
	}

}
