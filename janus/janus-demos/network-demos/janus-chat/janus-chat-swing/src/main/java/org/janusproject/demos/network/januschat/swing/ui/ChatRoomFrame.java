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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.network.januschat.ChatUtil;
import org.janusproject.demos.network.januschat.agent.ChatChannel;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * JFrame which is displaying chat rooms.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ChatRoomFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 6246602005044429324L;
	
	private static final String QUIT_ACTION = "quitAction"; //$NON-NLS-1$
	private static final String JOIN_CHATROOM_ACTION = "joinChatRoomAction"; //$NON-NLS-1$
	private static final String EXIT_CHATROOM_ACTION = "exitChatroomAction"; //$NON-NLS-1$
	private static final String NEW_CHATTER_ACTION = "newChatterAction"; //$NON-NLS-1$	
	private static final String SEND_PRIVATE_MESSAGE_ACTION = "privateMessageAction"; //$NON-NLS-1$
	
	private final MultiChatRoomPanel chatrooms;
	private final WeakReference<ChatChannel> chatChannel;
	
	/**
	 * @param chatAgent is the address of the chat agent. 
	 */
	public ChatRoomFrame(AgentAddress chatAgent) {
		this(ChatUtil.getChannelFor(chatAgent));
	}

	/**
	 * @param chatChannel is the channel to interact with the personnal agent.
	 */
	public ChatRoomFrame(ChatChannel chatChannel) {
		assert(chatChannel!=null);
		this.chatChannel = new WeakReference<ChatChannel>(chatChannel);

		Address owner = chatChannel.getChannelOwner();
		assert(owner!=null);
		
		String ownerName = owner.getName();
		if (ownerName==null || "".equals(ownerName)) { //$NON-NLS-1$
			ownerName = owner.toString();
		}
		
		setTitle(Locale.getString(ChatRoomFrame.class, "TITLE", ownerName)); //$NON-NLS-1$
		setPreferredSize(new Dimension(800, 600));		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
			public void windowClosing(WindowEvent winEvt) {
				Kernels.killAll();
				setVisible(false);
				try {
					Thread.sleep(2000);
				}
				catch (InterruptedException e1) {
					//
				}
				System.exit(0);
		    }
		});
		getContentPane().setLayout(new BorderLayout());
		
		this.chatrooms = new MultiChatRoomPanel(chatChannel);
		getContentPane().add(this.chatrooms, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu chatRoomMenu = new JMenu(Locale.getString(ChatRoomFrame.class, "MENU_CHATROOMS")); //$NON-NLS-1$
		menuBar.add(chatRoomMenu);
		
		JMenuItem item;
		
		item = new JMenuItem(Locale.getString(ChatRoomFrame.class, "MENU_NEW_CHATTER")); //$NON-NLS-1$
		item.setActionCommand(NEW_CHATTER_ACTION);
		item.addActionListener(this);
		chatRoomMenu.add(item);

		item = new JMenuItem(Locale.getString(ChatRoomFrame.class, "MENU_JOIN_CHATROOM")); //$NON-NLS-1$
		item.setActionCommand(JOIN_CHATROOM_ACTION);
		item.addActionListener(this);
		chatRoomMenu.add(item);

		item = new JMenuItem(Locale.getString(ChatRoomFrame.class, "MENU_EXIT_CHATROOM")); //$NON-NLS-1$
		item.setActionCommand(EXIT_CHATROOM_ACTION);
		item.addActionListener(this);
		chatRoomMenu.add(item);
		
		item = new JMenuItem(Locale.getString(ChatRoomFrame.class, "MENU_SEND_PRIVATE_MESSAGE")); //$NON-NLS-1$
		item.setActionCommand(SEND_PRIVATE_MESSAGE_ACTION);
		item.addActionListener(this);
		chatRoomMenu.add(item);
		
		item = new JMenuItem(Locale.getString(ChatRoomFrame.class, "MENU_QUIT")); //$NON-NLS-1$
		item.setActionCommand(QUIT_ACTION);
		item.addActionListener(this);
		chatRoomMenu.add(item);
		
		pack();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (JOIN_CHATROOM_ACTION.equals(cmd)) {
			ChatChannel channel = this.chatChannel.get();
			if (channel!=null) {
				Collection<GroupAddress> myRooms = channel.getParticipatingChatrooms();
				Collection<GroupAddress> rooms = channel.getAllChatrooms();			
				List<GroupAddress> notParticipatingRooms = new ArrayList<GroupAddress>();
				notParticipatingRooms.addAll(rooms);
				notParticipatingRooms.removeAll(myRooms);
				ChatRoomSelectionDialog dialog = new ChatRoomSelectionDialog(notParticipatingRooms);
				dialog.setVisible(true);
				GroupAddress room = dialog.getSelectedChatRoom();
				if (room!=null) {
					channel.joinChatroom(room);
				}
			}
		}
		else if (EXIT_CHATROOM_ACTION.equals(cmd)) {
			ChatChannel channel = this.chatChannel.get();
			if (channel!=null) {
				ChatRoomPanel cr = this.chatrooms.getCurrentChatRoom();
				if (cr!=null) {
					GroupAddress chatroom = cr.getChatRoom();
					assert(chatroom!=null);
					channel.exitChatroom(chatroom);
				}
			}
		}
		else if (NEW_CHATTER_ACTION.equals(cmd)) {
			String chatterName = JOptionPane.showInputDialog(this, 
					Locale.getString(ChatRoomFrame.class, "INPUT_CHATTER_NAME")); //$NON-NLS-1$
			if (chatterName!=null && !"".equals(chatterName)) { //$NON-NLS-1$
				ChatUtil.createChatter(chatterName);
			}
		}
		else if (QUIT_ACTION.equals(cmd)) {
			Kernels.killAll();
			setVisible(false);
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException e1) {
				//
			}
			System.exit(0);
		} else if (SEND_PRIVATE_MESSAGE_ACTION.equals(cmd)) {
			ChatChannel channel = this.chatChannel.get();
			if (channel!=null) {
				Collection<AgentAddress> participants = new TreeSet<AgentAddress>();
				for(GroupAddress room : channel.getParticipatingChatrooms()) {
					Iterator<AgentAddress> iterator = channel.getChatroomParticipants(room);
					while (iterator.hasNext()) {
						AgentAddress a = iterator.next();
						if (!a.equals(channel.getChannelOwner())) {
							participants.add(a);
						}
					}
				}
				PrivateMessageDialog privateMessageDialog = new PrivateMessageDialog(participants);
				privateMessageDialog.setVisible(true);
				if (privateMessageDialog.getReceiverAddress() != null) {
					channel.postPrivateMessage(privateMessageDialog.getReceiverAddress(), privateMessageDialog.getMessage());
				}
			}
		}
	}

}
