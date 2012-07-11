/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.market.simple.providing;

import org.janusproject.demos.market.simple.influence.RequestSignal;
import org.janusproject.demos.market.simple.influence.TransfertSignal;
import org.janusproject.demos.market.simple.message.ContractGroupMessage;
import org.janusproject.demos.market.simple.message.ContractQueryMessage;
import org.janusproject.demos.market.simple.purchase.CBroker;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentsignal.LastSignalAdapter;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.core.SatisfyRoleDependenciesCondition;
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
@RoleActivationPrototype(
		fixedParameters={}
)
public class PBroker extends Role {

	private Object contractDescription;
	
	private State state;
	private AgentAddress provider;
	private GroupAddress contractGroup;
	private final LastSignalAdapter<RequestSignal> signalListener = new LastSignalAdapter<RequestSignal>(RequestSignal.class);
	
	/**
	 */
	public PBroker() {
		super();
		addObtainCondition(new SatisfyRoleDependenciesCondition(CBroker.class));
	}
	
	@Override
	public Status activate(Object... parameters) {
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
		case WAITING_FOR_CLIENT_BROKER:
		{
			RequestSignal influence = this.signalListener.getLastReceivedSignal();
			if (influence!=null) {
				this.contractDescription = influence.getValues()[0];
				return State.CONTACT_PROVIDER;
			}
			return State.WAITING_FOR_CLIENT_BROKER;
		}
		
		case CONTACT_PROVIDER:
		{
			RoleAddress adr = sendMessage(Provider.class,
						new ContractQueryMessage(this.contractDescription));
			if (adr!=null) {
				this.provider = adr.getPlayer();
				return State.WAIT_PROVIDER_PROPOSAL;
			}
			return State.CONTACT_PROVIDER;
		}
		
		case WAIT_PROVIDER_PROPOSAL:
			for(Message msg : getMailbox()) {
				if (msg instanceof ContractGroupMessage) {
					ContractGroupMessage cgm = (ContractGroupMessage)msg;
					this.contractGroup = cgm.getContent(GroupAddress.class);
					if (this.contractGroup!=null) {
						fireSignal(new TransfertSignal(this, this.provider, this.contractGroup));
						leaveMe();
						return State.WAITING_FOR_CLIENT_BROKER;
					}
				}
			}
			return State.WAIT_PROVIDER_PROPOSAL;

		default:
			return this.state;
		}			
	}

	/** 
	 * This class defines the various possible state of the <code>PBroker</code> role.
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum State {

		/** Waiting for a client broker.
		 */
		WAITING_FOR_CLIENT_BROKER,
		
		/** Contact the provider.
		 */
		CONTACT_PROVIDER,
		
		/** Wait for provider answer.
		 */
		WAIT_PROVIDER_PROPOSAL;
	}

}
