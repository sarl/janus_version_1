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

import java.util.UUID;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.simple.contracting.Buyer;
import org.janusproject.demos.market.simple.influence.ContractTerminationSignal;
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


/** Client in market demo.
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
public class Client extends Role {
	/**
	 * The adress of the Broker in charge of finding a good provider for this client
	 */
	private AgentAddress myBroker = null;	
	
	/**
	 * The current behavioral state of this role
	 */
	private State state;
	
	/**
	 * The 
	 */
	private Object contractDescription;
	private LastSignalAdapter<ContractTerminationSignal> signalListener = new LastSignalAdapter<ContractTerminationSignal>(ContractTerminationSignal.class);
	
	@Override
	public Status activate(Object... parameters) {
		this.state = State.BROKER_CONTACT;
		return StatusFactory.ok(this);
	}
	
	
	@Override
	public Status live() {
		this.state = Run();
		return StatusFactory.ok(this);
	}

	private State Run() {
		switch (this.state) {		
		case BROKER_CONTACT:
			print(Locale.getString(Client.class,"CONTACT_BROKER"));  //$NON-NLS-1$
			this.contractDescription = UUID.randomUUID();
			RoleAddress broker = sendMessage(CBroker.class, new ContractQueryMessage(this.contractDescription));
			if (broker!=null) {
				this.myBroker = broker.getPlayer();
				if (this.myBroker!=null)
					return State.WAITING_CONTRACT_GROUP;
			}
			return State.BROKER_CONTACT;
		case WAITING_CONTRACT_GROUP:
			for(Message msg : getMailbox()) {
				if (msg instanceof ContractGroupMessage) {
					ContractGroupMessage cgm = (ContractGroupMessage)msg;
					Object content = cgm.getContent();
					if (content instanceof GroupAddress) {
						print(Locale.getString(Client.class,
								"CONTRACT_GROUP_RECEIVED",  //$NON-NLS-1$
								content));
						if (requestRole(Buyer.class,(GroupAddress)content,this.contractDescription)!=null) {
							addSignalListener(this.signalListener);
							print(Locale.getString(Client.class,
									"WAIT_CONTRACT_TERMINATION")); //$NON-NLS-1$
							return State.WAITING_CONTRACT_PASSED;
						}
					}
				}
			}
			return State.WAITING_CONTRACT_GROUP;
		case WAITING_CONTRACT_PASSED:
			ContractTerminationSignal contractResult = this.signalListener.getLastReceivedSignal();
			this.signalListener.clear();
			if (contractResult!=null) {
				removeSignalListener(this.signalListener);
				print(Locale.getString(Client.class,
						"CONTRACT_PASSED",  //$NON-NLS-1$
						contractResult.getContractDescription()));
				leaveMe();
				return State.NIL;
			}
			return State.WAITING_CONTRACT_PASSED;
		case NIL:
		default:
			return State.NIL;
		}				
	}

	/**
	 * This class defines the various possible state of the <code>Client</code> role
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum State {

		/** Client send a request to the broker.
		 */
		BROKER_CONTACT,
		
		/**
		 * Waiting to receive group for contract.
		 */
		WAITING_CONTRACT_GROUP,
		
		/** Waiting the contract to be passed.
		 */
		WAITING_CONTRACT_PASSED,
		
		/** Do nothing.
		 */
		NIL;
		
	}
	
}
