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
package org.janusproject.demos.market.simple.providing;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.simple.contracting.ContractingOrganization;
import org.janusproject.demos.market.simple.contracting.Seller;
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

/** Provider something through a broker.
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
public class Provider extends Role {

	private State state;
	private AgentAddress broker;
	private GroupAddress contractGroup;
	private Object contractDescription;
	
	private final LastSignalAdapter<ContractTerminationSignal> signalListener = new LastSignalAdapter<ContractTerminationSignal>(ContractTerminationSignal.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		this.state = State.WAITING_FOR_BROKER;
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {
		this.state = Run();
		return StatusFactory.ok(this);
	}

	private State Run() {
		switch (this.state) {		
		case WAITING_FOR_BROKER:
			{
				for(Message msg : getMailbox()) {
					if (msg instanceof ContractQueryMessage) {
						ContractQueryMessage cqm = (ContractQueryMessage)msg;
						this.broker = ((RoleAddress)cqm.getSender()).getPlayer();
						this.contractDescription = cqm.getContent();
						getLogger().info(Locale.getString(Provider.class, "RECEIVE_BROKER_REQUEST")); //$NON-NLS-1$
						return State.CREATING_CONTRACT_GROUP;
					}
				}
				return State.WAITING_FOR_BROKER;
			}
		case CREATING_CONTRACT_GROUP:
			{
				print(Locale.getString(Provider.class, "CREATING_CONTRACT_GROUP")); //$NON-NLS-1$
				this.contractGroup = getOrCreateGroup(ContractingOrganization.class);
				if (requestRole(Seller.class,this.contractGroup,
						this.contractDescription)!=null) {
					sendMessage(PBroker.class, this.broker,
								new ContractGroupMessage(getPlayer(), this.contractGroup));
						addSignalListener(this.signalListener);
						return State.WAIT_FOR_CONTRACT_TERMINATION;
				}
				return State.CREATING_CONTRACT_GROUP;
			}
		case WAIT_FOR_CONTRACT_TERMINATION:
			ContractTerminationSignal influence = this.signalListener.getLastReceivedSignal();
			this.signalListener.clear();
			if (influence!=null) {
				removeSignalListener(this.signalListener);
				return State.CONTRACT_PASSED;
			}
			return State.WAIT_FOR_CONTRACT_TERMINATION;
		case CONTRACT_PASSED:
			leaveMe();
			return State.NIL;
		case NIL:
		default:
			return this.state;
		}			
	}

	/** 
	 * This class defines the various possible state of the <code>Providerbefore </code> role
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum State {

		/** Waiting for a broker.
		 */
		WAITING_FOR_BROKER,
		
		/** Creating contract group for a broker.
		 */
		CREATING_CONTRACT_GROUP,
		
		/** Wait for contract termination.
		 */
		WAIT_FOR_CONTRACT_TERMINATION,
	
		/** Terminate contract.
		 */
		CONTRACT_PASSED,
		
		/** Do nothing.
		 */
		NIL;
		
	}
		
}
