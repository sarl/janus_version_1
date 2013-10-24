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
package org.janusproject.demos.market.selective.contracting;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.selective.influence.ContractTerminationInfluence;
import org.janusproject.demos.market.selective.message.ContractFinalizationMessage;
import org.janusproject.demos.market.selective.travel.TravelDestination;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Buyer.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(fixedParameters = { TravelDestination.class })
public class Buyer extends Role {

	private TravelDestination destination;
	private State state;

	@Override
	public Status activate(Object... parameters) {
		this.destination = (TravelDestination) parameters[0];
		this.state = State.WAIT_FOR_SELLER;
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {
		this.state = Run();
		return StatusFactory.ok(this);
	}

	private State Run() {
		switch (this.state) {
		case WAIT_FOR_SELLER: {
			print(Locale.getString(Buyer.class, "HELLO")); //$NON-NLS-1$
			if (sendMessage(Seller.class, new ContractFinalizationMessage())!=null) {
				print(Locale.getString(Buyer.class, "WAIT")); //$NON-NLS-1$
				return State.WAIT_SELLER_ACK;
			}
			return State.WAIT_FOR_SELLER;
		}
		case WAIT_SELLER_ACK: {
			for (Message msg : getMailbox()) {
				print(Locale.getString(Buyer.class, "RECEIVE")); //$NON-NLS-1$
				if (msg instanceof ContractFinalizationMessage) {
					return State.GOOD_BYE;
				}
			}
			return State.WAIT_FOR_SELLER;
		}
		case GOOD_BYE: {
			print(Locale.getString(Buyer.class, "GOODBYE")); //$NON-NLS-1$
			fireSignal(new ContractTerminationInfluence(this, this.destination));
			leaveMe();
			return State.GOOD_BYE;
		}
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
	public enum State {

		/**
		 * Wait for seller, and send it a finalization message.
		 */
		WAIT_FOR_SELLER,

		/**
		 * Wait for seller ACK
		 */
		WAIT_SELLER_ACK,

		/**
		 * Good bye.
		 */
		GOOD_BYE;

	}

}
