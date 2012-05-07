/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010, 2012 Janus Core Developers
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
package org.janusproject.demos.network.januschat.organization;

import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.agentsignal.SignalPolicy;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.AbstractContentMessage;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * This role is played by the agents which want to chat inside
 * a chat room.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@RoleActivationPrototype(
		fixedParameters={}
)
public class ChatterRole extends Role {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... params) {
		getSignalManager().setPolicy(SignalPolicy.STORE_IN_QUEUE);
		return super.activate(params);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		// Receive the messages fro mthe other chatters
		for(Message m : getMailbox()) {
			if (m instanceof AbstractContentMessage<?>) {
				try {
					Object content = ((AbstractContentMessage<?>)m).getContent();
					if (content!=null) content = content.toString(); // force String format
					executeCapacityCall(
							SendToUserCapacity.class,
							getGroupAddress(),
							m.getSender(),
							content);
				}
				catch (Exception e) {
					fireSignal(new ErrorSignal(this, getGroupAddress(), e));
				}
			}
		}

		// Send my messages
		Signal sig;
		while ((sig = getSignal())!=null) {
			if (sig instanceof SendTextSignal) {
				SendTextSignal s = (SendTextSignal)sig;
				if (s.getChatRoom().equals(getGroupAddress())) {
					broadcastMessage(
							ChatterRole.class,
							new StringMessage(s.getText()));
				}
			}
		}
		
		return StatusFactory.ok(this);
	}

}
