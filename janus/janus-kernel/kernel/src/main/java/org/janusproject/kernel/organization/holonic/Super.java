/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.organization.holonic;

import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.agentsignal.SignalListener;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.organization.holonic.influence.RequestCapacityInfluence;
import org.janusproject.kernel.organization.holonic.message.RequestCapacityMessage;
import org.janusproject.kernel.organization.holonic.message.ResultCapacityMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * This role is reserved for the macro-agent in an agent organization.
 * It allow the communication between a super-agent and his members especially his heads.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Super extends Role {

	private RequestCapacityInfluence<?> influence;

	private int current = 1; // the current state

	private Message m;
	
	private final SuperSignalListener signalListener = new SuperSignalListener();

	/**
	 */
	public Super() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		this.current = Run();
		return StatusFactory.ok(this);
	}

	private int Run() {
		switch (this.current) {
		case 1:
			getSignalManager().addSignalListener(this.signalListener);
			return 2;
		case 2:
			this.influence = this.signalListener.consumeSignal();
			if (this.influence != null) {
				return 3;
			}
			return 2;

		case 3:
			if ((getPlayers(Head.class, getGroupAddress()).totalSize() == 0)
					&& (getPlayers(Part.class, getGroupAddress()).totalSize() == 0)) {
				return 3;
			}
			return 4;

		case 4:
			debug(
					"Super get influence : " + this.influence.toString()); //$NON-NLS-1$
			return 5;

		case 5:
			sendMessage(Head.class, newRequestCapaciyMessage(this.influence));
			return 6;

		case 6:
			this.m = getMailbox().removeFirst();
			if ((this.m != null) && (this.m instanceof StringMessage)
					&& ("OK".equals(((StringMessage) this.m).getContent()))) { //$NON-NLS-1$
				debug(
						"ACK receive for request capacity"); //$NON-NLS-1$
				return 7;
			}
			return 5;

		case 7:
			this.m = getMailbox().getFirst();
			if (this.m instanceof ResultCapacityMessage<?>) {
				
				RoleAddress adr = (RoleAddress)this.m.getSender();
				
				sendMessage(
							adr.getRole(), 
							adr.getPlayer(),
							new StringMessage("OK"));//ACK to Part //$NON-NLS-1$
				setResult((ResultCapacityMessage<?>)this.m);
				
				return 0;
			}
			return 7;

		default:
			return 0;
		}
	}
	
	private <CT extends Capacity> void setResult(ResultCapacityMessage<CT> msg) {
		debug(
				"Super got the result :" + msg.getResult()[0]); //$NON-NLS-1$
		terminateCapacityCall(msg.getCallIdentifier(), msg.getResult());
	}

	private static <CT extends Capacity> RequestCapacityMessage<CT> newRequestCapaciyMessage(RequestCapacityInfluence<CT> inf) {
		return new RequestCapacityMessage<CT>(
				inf.getRequestedCapacity(),
				inf.getRoleInfluenceSource(),
				inf.getCallIdentifier(),
				inf.getInputValues());
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SuperSignalListener implements SignalListener {
		
		private RequestCapacityInfluence<?> lastSignal;
		
		/**
		 */
		public SuperSignalListener() {
			this.lastSignal = null;
		}

		@Override
		public void onSignal(Signal signal) {
			if (signal instanceof RequestCapacityInfluence<?>) {
				this.lastSignal = (RequestCapacityInfluence<?>)signal;
			}
		}
		
		/** Replies the last signal.
		 * 
		 * @return the last signal, or <code>null</code>.
		 */
		public RequestCapacityInfluence<?> consumeSignal() {
			RequestCapacityInfluence<?> ls = this.lastSignal;
			this.lastSignal = null;
			return ls;
		}

	}

}
