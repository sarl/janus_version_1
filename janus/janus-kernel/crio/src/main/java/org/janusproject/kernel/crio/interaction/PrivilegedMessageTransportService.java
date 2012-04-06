/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.kernel.crio.interaction;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.message.Message;

/**
 * This interface provides privilegied access to message transport service. 
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface PrivilegedMessageTransportService {

	/**
	 * Forward the specified <code>Message</code> to the receiver specified
	 * in the message context.
	 * <p>
	 * This function does not change the emitter not receiver of the message.
	 * <p>
	 * This function automatically allows the message sender to receive the message.
	 * 
	 * @param message is the message to send
	 * @return the address of the agent or the role which has received the message.
	 */
	public Address forwardMessage(Message message);
	
	/**
	 * Forward the specified broadcast <code>Message</code> to the receiver specified
	 * in the message context.
	 * <p>
	 * This function does not change the emitter not receiver of the message.
	 * <p>
	 * This function automatically allows the message sender to receive the message.
	 * 
	 * @param message is the message to send
	 * @return <code>true</code> if sucessfully sent, otrherwise <code>false</code>.
	 */
	public boolean forwardBroadcastMessage(Message message);

}
