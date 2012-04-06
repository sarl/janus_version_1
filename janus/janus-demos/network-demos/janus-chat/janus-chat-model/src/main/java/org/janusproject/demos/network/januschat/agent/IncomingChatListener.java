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
package org.janusproject.demos.network.januschat.agent;

import java.util.EventListener;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;

/** Listener on events in chat rooms.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface IncomingChatListener extends EventListener {

	/**
	 * Invoked when a message is arrived.
	 * 
	 * @param chatroom is the chat room
	 * @param sender is the message sender
	 * @param message is the message.
	 */
	public void incomingMessage(GroupAddress chatroom, AgentAddress sender, String message);

	/**
	 * Invoked when an error has occured in the chatroom.
	 * 
	 * @param chatroom is the chat room
	 * @param error is the error.
	 */
	public void chatroomError(GroupAddress chatroom, Throwable error);

	/**
	 * Invoked when the personal agent has joined a chatroom.
	 * 
	 * @param chatroom is the chat room
	 * @param joiner is the address of the chatter who entering the chatroom
	 */
	public void joinChatroom(GroupAddress chatroom, AgentAddress joiner);

	/**
	 * Invoked when the personal agent has created a chatroom.
	 * 
	 * @param chatroom is the chat room
	 */
	public void chatroomCreated(GroupAddress chatroom);

	/**
	 * Invoked when the personal agent has exited a chatroom.
	 * 
	 * @param chatroom is the chat room
	 * @param exiter is the address of the chatter who exited the chatroom
	 */
	public void exitChatroom(GroupAddress chatroom, AgentAddress exiter);

}
