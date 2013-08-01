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

import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageException;

/**
 * Exception thrown when a receiver address is expected in a message.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class UnspecifiedReceiverAddressMessageException extends MessageException {

	private static final long serialVersionUID = 492336883782800376L;

	/**
	 * @param msg is the Message which could not be treated.
	 */
	public UnspecifiedReceiverAddressMessageException(Message msg) {
		super(msg);
	}
	
	/**
	 * @param msg is the Message which could not be treated.
	 * @param message is the explaination of the exception.
	 */
	public UnspecifiedReceiverAddressMessageException(Message msg, String message) {
		super(msg, message);
	}

	/**
	 * @param msg is the Message which could not be treated.
	 * @param cause is an exception which cause this MessageException.
	 */
	public UnspecifiedReceiverAddressMessageException(Message msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @param msg is the Message which could not be treated.
	 * @param message is the explaination of the exception.
	 * @param cause is an exception which cause this MessageException.
	 */
	public UnspecifiedReceiverAddressMessageException(Message msg, String message, Throwable cause) {
		super(msg, message, cause);
	}
	
}
