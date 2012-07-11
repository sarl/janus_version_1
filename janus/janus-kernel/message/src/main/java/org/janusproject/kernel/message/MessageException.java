/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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

/**
 * Exception thrown when something wrong appends with a message.
 *
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MessageException extends RuntimeException {

	private static final long serialVersionUID = -1942943461704781270L;

	private final Message message;
	
	/**
	 * @param msg is the Message which could not be treated.
	 */
	public MessageException(Message msg) {
		this.message = msg;
	}
	
	/**
	 * @param msg is the Message which could not be treated.
	 * @param message is the explaination of the exception.
	 */
	public MessageException(Message msg, String message) {
		super(message);
		this.message = msg;
	}

	/**
	 * @param msg is the Message which could not be treated.
	 * @param cause is an exception which cause this MessageException.
	 */
	public MessageException(Message msg, Throwable cause) {
		super(cause);
		this.message = msg;
	}

	/**
	 * @param msg is the Message which could not be treated.
	 * @param message is the explaination of the exception.
	 * @param cause is an exception which cause this MessageException.
	 */
	public MessageException(Message msg, String message, Throwable cause) {
		super(message, cause);
		this.message = msg;
	}

	/** Replies the janus message which is at the origin of this exception.
	 * 
	 * @return the Message which has cause this exception.
	 */
	public Message getJanusMessage() {
		return this.message;
	}
	
}
