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
import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * This signal is fired when a message may be sent in the chat room.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SendTextSignal extends Signal {

	private static final long serialVersionUID = 907849552158727195L;
	
	private final String text;
	private final GroupAddress chatroom;
	
	/**
	 * @param source is the emitter of the signal
	 * @param chatroom is the address of the chatroom.
	 * @param text is the text to sent.
	 */
	public SendTextSignal(Object source, GroupAddress chatroom, String text) {
		super(source);
		this.text = text;
		this.chatroom = chatroom;
	}

	/** Replies the address of the chatroom.
	 * 
	 * @return the chatroom.
	 */
	public GroupAddress getChatRoom() {
		return this.chatroom;
	}

	/** Replies the text to send in the chatroom.
	 * 
	 * @return the text.
	 */
	public String getText() {
		return this.text;
	}

}
