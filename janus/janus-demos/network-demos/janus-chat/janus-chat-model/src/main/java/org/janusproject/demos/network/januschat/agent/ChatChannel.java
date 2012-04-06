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

import java.util.Collection;
import java.util.Iterator;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.crio.core.GroupAddress;

/** A channel between a chat personal agent and a GUI.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ChatChannel extends Channel {

	/** Add listener on private message events.
	 * 
	 * @param listener
	 */
	public void addIncomingPrivateMessageListener(IncomingPrivateMessageListener listener);
	
	/** Remove listener on private message events.
	 * 
	 * @param listener
	 */
	public void removeIncomingPrivateMessageListener(IncomingPrivateMessageListener listener);
	
	
	/** Add listener on chatroom events.
	 * 
	 * @param listener
	 */
	public void addIncomingChatListener(IncomingChatListener listener);
	
	/** Remove listener on chatroom events.
	 * 
	 * @param listener
	 */
	public void removeIncomingChatListener(IncomingChatListener listener);

	/** Replies the opened chatrooms in which the associated agent is participating.
	 * 
	 * @return the opened chatrooms.
	 */
	public Collection<GroupAddress> getParticipatingChatrooms();

	/** Replies all the opened chatrooms.
	 * 
	 * @return the opened chatrooms.
	 */
	public Collection<GroupAddress> getAllChatrooms();

	/** Replies all the participants to an opened chatrooms.
	 * 
	 * @param chatroom
	 * @return the participants.
	 */
	public Iterator<AgentAddress> getChatroomParticipants(GroupAddress chatroom);

	/** Join the given chatroom.
	 * 
	 * @param chatroom is the chatroom.
	 * @return success state.
	 */
	public boolean joinChatroom(GroupAddress chatroom);

	/** Post a message in the chatroom.
	 * 
	 * @param chatroom is the chatroom.
	 * @param message is the message to post.
	 */
	public void postMessage(GroupAddress chatroom, String message);
	
	/**
	 * Send a private messsage to the specified user
	 * @param agent is the desired receiver of the message
	 * @param message is the message to send
	 */
	public void postPrivateMessage(AgentAddress agent, String message);

	/** Exit from the given chatroom.
	 * 
	 * @param chatroom is the chatroom.
	 * @return success state.
	 */
	public boolean exitChatroom(GroupAddress chatroom);

}
