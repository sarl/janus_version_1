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

import java.util.Collection;
import java.util.UUID;

import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.ObjectMessage;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.organization.holonic.message.RequestCapacityMessage;
import org.janusproject.kernel.organization.holonic.message.ResultCapacityMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * 
 * Part role mission : Decision Making : Vote Indirect Atomic capacity : execute
 * the requested capacity
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Part extends Role {

	private RequestCapacityMessage<Capacity> m;
	private UUID lastCapacityCall = null;
	
	private CapacityContext result;

	private int current = 1; // the current state

	//private int n = 0;

	/**
	 */
	public Part() {
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

	@SuppressWarnings("unchecked")
	private int Run() {
		switch (this.current) {
		case 1:
			if (getPlayers(Head.class, getGroupAddress()).totalSize() == 0) {
				return 1;
			}
			return 2;

		case 2:
			Collection<Class<? extends Capacity>> l = getPlayerCapacities();
			sendMessage(Head.class, new ObjectMessage(l));
			return 3;

		case 3:
			Message m = getMailbox().removeFirst();
			if (m instanceof RequestCapacityMessage) {
				this.m = (RequestCapacityMessage<Capacity>) m;
				return 4;
			}
			return 3;

		case 4:
			debug(
					"Got a message of RequestCapacity"); //$NON-NLS-1$
			try {
				this.lastCapacityCall = submitCapacityCall(this.m.getRequestedCapacity(), this.m.getInputValues());
				
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable _) {
				return 7;
			}
			return 5;

		case 5:
			this.result = getCapacityCallResult(this.lastCapacityCall);
			if (this.result != null) {
				debug("Sending result"); //$NON-NLS-1$
				return 6;
			}
			return 5;

		case 6:
			sendMessage(Super.class, new ResultCapacityMessage<Capacity>(
						this.m.getRequestedCapacity(),
						this.m.getInitialCaller(),
						this.m.getCallIdentifier(),
						this.result));
			return 7;

		case 7:
			m = getMailbox().removeFirst();
			if ((m instanceof StringMessage)
					&& ("OK".equals(((StringMessage) m).getContent()))) { //$NON-NLS-1$
				debug(
						"ACK receive for ResultCapacity, Part Finished"); //$NON-NLS-1$
				return 0;
			}
			return 6;

		default:
			return 0;
		}

	}

}
