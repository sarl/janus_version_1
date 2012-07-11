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

import org.janusproject.kernel.address.AgentAddress;

/**
 * Factory to update message context in message.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated see {@link Message}
 */
@Deprecated
public abstract class MessageContextFactory {
	
	/**
	 * Set the context in the given message.
	 * 
	 * @param message is the message to change.
	 * @param context is the context to put back in the given message.
	 */
	protected static void setContext(Message message, MessageContext context) {
		assert(message!=null);
		//message.context = context;
	}
	
	/**
	 * Set the receiver in the given message context.
	 * 
	 * @param context is the context to change.
	 * @param receiver is the address of the receiver.
	 */
	protected static void setReceiver(MessageContext context, AgentAddress receiver) {
		assert(context!=null);
		context.receiver = receiver;
	}

	/**
	 * Set the sender in the given message context.
	 * 
	 * @param context is the context to change.
	 * @param sender is the address of the sender.
	 */
	protected static void setSender(MessageContext context, AgentAddress sender) {
		assert(context!=null);
		context.sender = sender;
	}

}
