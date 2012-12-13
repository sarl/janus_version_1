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

import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityPrototype;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.RoleAddress;

/**
 * This capacity permits to notifies the user about the
 * reception of a message in the chatroom.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@CapacityPrototype(
		fixedParameters={GroupAddress.class, RoleAddress.class, String.class}
)
public interface SendToUserCapacity extends Capacity {
	
	/** Notify the user about the reception of a message.
	 * 
	 * @param chatroom is the chatroom in which the message was sent.
	 * @param emitter is the emitter of the message.
	 * @param message is the text of the message.
	 */
	public void onChatRoomMessageReceived(GroupAddress chatroom, RoleAddress emitter, String message);
	
}
