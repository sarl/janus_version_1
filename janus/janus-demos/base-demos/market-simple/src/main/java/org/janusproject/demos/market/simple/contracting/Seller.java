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
package org.janusproject.demos.market.simple.contracting;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.simple.influence.ContractTerminationSignal;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * Seller.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(
		fixedParameters={Object.class}
)
public class Seller extends Role {
	
	private State state;
	private Object contractDescription;
	
	@Override
	public Status activate(Object... parameters) {
		this.state = State.WAIT_BUYER;
		this.contractDescription = parameters[0];
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {
		this.state = Run();	
		return StatusFactory.ok(this);
	}

	private State Run() {
		switch(this.state) {
		case WAIT_BUYER:
			SizedIterator<AgentAddress> players = getPlayers(Buyer.class);
			if (players.totalSize()>0) {
				print(Locale.getString(Seller.class,"HELLO")); //$NON-NLS-1$
				return State.DO_SOMETHING;
			}
			return State.WAIT_BUYER;
		case DO_SOMETHING:
			broadcastMessage(Buyer.class, new StringMessage("CONTRACT_PASSED")); //$NON-NLS-1$
			if (hasMessage()) {
				getMailbox().clear();
				return State.GOOD_BYE;
			}
			return State.DO_SOMETHING;
		case GOOD_BYE:
			fireSignal(new ContractTerminationSignal(this, this.contractDescription));
			leaveMe();
			return State.GOOD_BYE;
		default:
			return this.state;
		}		
	}
	
	/**
	 * This class defines the various possible state of the <code>Seller</code> role
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum State {

		/** Wait for buyer
		 */
		WAIT_BUYER,

		/** Do something with client.
		 */
		DO_SOMETHING,
		
		/** Good bye.
		 */
		GOOD_BYE;
		
	}
	
}
