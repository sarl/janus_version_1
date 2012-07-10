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
package org.janusproject.demos.network.januschat.swing.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import javax.swing.JPanel;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.janusproject.demos.network.januschat.agent.ChatChannel;
import org.janusproject.demos.network.januschat.agent.IncomingChatListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * A panel which is able to contains many chat rooms.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MultiChatRoomPanel extends JPanel implements IncomingChatListener {

	private static final long serialVersionUID = 3560692289794263240L;
	
	private final JTabbedPane tabPanes;
	private final WeakReference<ChatChannel> chatChannel;
	
	/**
	 * @param chatChannel is the channel to interact with the personnal agent.
	 */
	public MultiChatRoomPanel(ChatChannel chatChannel) {
		assert(chatChannel!=null);
		this.chatChannel = new WeakReference<ChatChannel>(chatChannel);
		setLayout(new BorderLayout());
		
		this.tabPanes = new JTabbedPane(SwingConstants.TOP);
		add(this.tabPanes, BorderLayout.CENTER);
		
		for(GroupAddress room : chatChannel.getParticipatingChatrooms()) {
			openRoom(room, chatChannel);
		}
		
		chatChannel.addIncomingChatListener(this);
	}
	
	private void openRoom(GroupAddress chatroom, ChatChannel channel) {
		ChatRoomPanel chatroomPanel = new ChatRoomPanel(chatroom, channel);
		channel.addIncomingChatListener(chatroomPanel);
		String tip = chatroom.getUUID().toString();
		String n = chatroom.getName();
		if (n==null || "".equals(n)) n = channel.toString(); //$NON-NLS-1$
		this.tabPanes.addTab(n, null, chatroomPanel, tip);
		this.tabPanes.setSelectedComponent(chatroomPanel);
		
		Iterator<AgentAddress> participants = channel.getChatroomParticipants(chatroom);
		AgentAddress adr;
		while (participants.hasNext()) {
			adr = participants.next();
			chatroomPanel.joinChatroom(chatroom, adr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chatroomCreated(GroupAddress chatroom) {
		ChatChannel channel = this.chatChannel.get();
		if (channel!=null) {
			openRoom(chatroom, channel);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chatroomError(GroupAddress chatroom, Throwable error) {
		// ignore this event because this object does not manage a chatroom.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exitChatroom(GroupAddress chatroom, AgentAddress exiter) {
		ChatChannel channel = this.chatChannel.get();
		if (channel!=null && exiter.equals(channel.getChannelOwner())) {
			ChatRoomPanel room = getChatRoom(chatroom);
			if (room!=null) {
				int idx = this.tabPanes.indexOfComponent(room);
				this.tabPanes.removeTabAt(idx);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incomingMessage(GroupAddress chatroom, AgentAddress sender,
			String message) {
		// ignore this event because this object does not manage a chatroom.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void joinChatroom(GroupAddress chatroom, AgentAddress joiner) {
		ChatChannel channel = this.chatChannel.get();
		if (channel!=null) {
			ChatRoomPanel p = getChatRoom(chatroom);
			if (p==null) {
				openRoom(chatroom, channel);
			}
		}
	}
	
	private ChatRoomPanel getChatRoom(GroupAddress chatroom) {
		for(Component c : this.tabPanes.getComponents()) {
			if (c instanceof ChatRoomPanel) {
				ChatRoomPanel p = (ChatRoomPanel)c;
				if (chatroom.equals(p.getChatRoom())) {
					return p;
				}
			}
		}
		return null;
	}
	
	/** Replies the currently selected chatroom.
	 * 
	 * @return the currently selected chatroom, or <code>null</code> if none.
	 */
	public ChatRoomPanel getCurrentChatRoom() {
		Component cmp = this.tabPanes.getSelectedComponent();
		if (cmp instanceof ChatRoomPanel) {
			return (ChatRoomPanel)cmp;
		}
		return null;
	}

}
