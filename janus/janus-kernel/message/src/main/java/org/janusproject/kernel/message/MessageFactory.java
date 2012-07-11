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
package org.janusproject.kernel.message;

import org.janusproject.kernel.address.Address;

/**
 * Factory to update message.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class MessageFactory {
	
	/**
	 * Set the receiver in the given message context.
	 * 
	 * @param message is the message to update.
	 * @param receiver is the address of the receiver.
	 */
	protected static void setReceiver(Message message, Address receiver) {
		assert(message!=null);
		message.receiver = receiver;
	}

	/**
	 * Set the sender in the given message.
	 * 
	 * @param message is the message to update.
	 * @param sender is the address of the sender.
	 */
	protected static void setSender(Message message, Address sender) {
		assert(message!=null);
		message.sender = sender;
	}

	/**
	 * Set the creation date in the given message.
	 * 
	 * @param message is the message to update.
	 * @param date is the creation date of the message.
	 */
	protected static void setCreationDate(Message message, float date) {
		assert(message!=null);
		message.creationDate = date;
	}

}
