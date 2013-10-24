/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageException;


/**
 * Exception thrown when the receiver of a message was not found in the kernel.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class ReceiverNotFoundException extends MessageException {

	private static final long serialVersionUID = 6073133097638841872L;

	private static String makeMessage(Message msg, String message) {
		if (message!=null && !message.isEmpty()) {
			return Locale.getString(ReceiverNotFoundException.class,
					"RECEIVER_NOT_FOUND_M", //$NON-NLS-1$
					msg.getSender(),
					msg.getReceiver(),
					msg.toString(),
					message);
		}
		return Locale.getString(ReceiverNotFoundException.class,
				"RECEIVER_NOT_FOUND", //$NON-NLS-1$
				msg.getSender(),
				msg.getReceiver(),
				msg.toString());
	}

	/**
	 * @param msg is the Message which could not be treated.
	 */
	public ReceiverNotFoundException(Message msg) {
		super(msg, makeMessage(msg, null));
	}
	
	/**
	 * @param msg is the Message which could not be treated.
	 * @param message is the explaination of the exception.
	 */
	public ReceiverNotFoundException(Message msg, String message) {
		super(msg, makeMessage(msg, message));
	}

	/**
	 * @param msg is the Message which could not be treated.
	 * @param cause is an exception which cause this MessageException.
	 */
	public ReceiverNotFoundException(Message msg, Throwable cause) {
		super(msg, makeMessage(msg, null), cause);
	}

	/**
	 * @param msg is the Message which could not be treated.
	 * @param message is the explaination of the exception.
	 * @param cause is an exception which cause this MessageException.
	 */
	public ReceiverNotFoundException(Message msg, String message, Throwable cause) {
		super(msg, makeMessage(msg, message), cause);
	}
	
}
