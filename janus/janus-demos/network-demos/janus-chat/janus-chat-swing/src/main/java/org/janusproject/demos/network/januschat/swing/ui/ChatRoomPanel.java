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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.network.januschat.agent.ChatChannel;
import org.janusproject.demos.network.januschat.agent.IncomingChatListener;
import org.janusproject.demos.network.januschat.agent.IncomingPrivateMessageListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * JPanel able to display the messages in a chatroom and the participants to
 * the discussion.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ChatRoomPanel extends JPanel implements ActionListener, MouseListener, IncomingChatListener, IncomingPrivateMessageListener {

	private static final long serialVersionUID = 5026291245883675604L;

	private static final String NAME_UID_SEPARATOR = "$"; //$NON-NLS-1$
	
	private static final String SEND_ACTION = "SEND_MESSAGE_ACTION"; //$NON-NLS-1$
	
	private final GroupAddress chatroom;

	private final JEditorPane messagePane;
	private final JTextField inputMessage;
	private final DefaultListModel participants;
	private final JList participantList;
	
	private final WeakReference<ChatChannel> chatChannel;

	/**
	 * @param chatroom is the chat room managed by this panel.
	 * @param chatChannel is the channel to interact with the personnal agent.
	 */
	public ChatRoomPanel(GroupAddress chatroom, ChatChannel chatChannel) {
		this.chatroom = chatroom;
		this.chatChannel = new WeakReference<ChatChannel>(chatChannel);

		this.chatChannel.get().addIncomingPrivateMessageListener(this);
		
		setPreferredSize(new Dimension(800, 600));
		
		setLayout(new BorderLayout());
		
		JScrollPane scrollPane;
		
		this.messagePane = new JEditorPane("text/html", "<html><body></body></html>"); //$NON-NLS-1$ //$NON-NLS-2$
		scrollPane = new JScrollPane(this.messagePane);
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel sendToolPane = new JPanel();
		sendToolPane.setLayout(new BoxLayout(sendToolPane, BoxLayout.X_AXIS));
		add(sendToolPane, BorderLayout.SOUTH);
		
		this.inputMessage = new JTextField();
		this.inputMessage.setActionCommand(SEND_ACTION);
		this.inputMessage.addActionListener(this);
		sendToolPane.add(this.inputMessage);
		
		JButton sendBt = new JButton(Locale.getString(ChatRoomPanel.class, "SEND")); //$NON-NLS-1$
		sendBt.setActionCommand(SEND_ACTION);
		sendBt.addActionListener(this);
		sendToolPane.add(sendBt);
		
		this.participants = new DefaultListModel();
		this.participantList = new JList(this.participants);
		this.participantList.setCellRenderer(new ParticipantRenderer());
		this.participantList.addMouseListener(this);
		scrollPane = new JScrollPane(this.participantList);
		add(scrollPane, BorderLayout.EAST);
	}
	
	private void addText(String text) {
		String t = this.messagePane.getText();
		Pattern pattern = Pattern.compile("[<]body[>](.*)[<][/]body[>]", Pattern.CASE_INSENSITIVE|Pattern.DOTALL); //$NON-NLS-1$
		Matcher matcher = pattern.matcher(t);
		if (matcher.find()) {
			StringBuilder buffer = new StringBuilder(matcher.group(1).trim());
			if (buffer.length()>0) buffer.append("<br/>"); //$NON-NLS-1$
			buffer.append(text);
			buffer.insert(0, "<html><head></head><body>"); //$NON-NLS-1$
			buffer.append("</body></html>"); //$NON-NLS-1$
			this.messagePane.setText(buffer.toString());
		}
	}
	
	/** Replies the chatroom displayed by this panel.
	 * 
	 * @return the chatroom
	 */
	public GroupAddress getChatRoom() {
		return this.chatroom;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (SEND_ACTION.equals(e.getActionCommand())) {
			String msg = this.inputMessage.getText();
			if (msg!=null && !"".equals(msg)) { //$NON-NLS-1$
				ChatChannel channel = this.chatChannel.get();
				if (channel!=null) {
					channel.postMessage(getChatRoom(), msg);
					this.inputMessage.setText(""); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chatroomCreated(GroupAddress chatroom) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chatroomError(GroupAddress chatroom, Throwable error) {
		if (getChatRoom().equals(chatroom)) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("<font color=\"red\">"); //$NON-NLS-1$
			buffer.append(error.getLocalizedMessage());
			buffer.append("</font>"); //$NON-NLS-1$
			addText(buffer.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exitChatroom(GroupAddress chatroom, AgentAddress exiter) {
		if (getChatRoom().equals(chatroom)) {
			if (this.participants.removeElement(exiter)) {
				String n = exiter.getName();
				if (n==null || "".equals(n)) //$NON-NLS-1$
					n = exiter.toString();
				
				StringBuilder buffer = new StringBuilder();
				buffer.append("<i>"); //$NON-NLS-1$
				buffer.append(Locale.getString(ChatRoomPanel.class, "EXITING_CHATROOM", n)); //$NON-NLS-1$
				buffer.append("</i>"); //$NON-NLS-1$
				addText(buffer.toString());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incomingMessage(GroupAddress chatroom, AgentAddress sender, String message) {
		if (getChatRoom().equals(chatroom) && 
			message!=null && !"".equals(message)) { //$NON-NLS-1$
			StringBuilder buffer = new StringBuilder();
			buffer.append("<b>"); //$NON-NLS-1$
			String n = sender.getName();
			if (n==null || "".equals(n)) n = sender.toString(); //$NON-NLS-1$
			buffer.append(n);
			buffer.append(": </b>"); //$NON-NLS-1$
			buffer.append(message);
			addText(buffer.toString());
		}
	}

	@Override
	public void incomingPrivateMessage(AgentAddress sender, String message) {
		JOptionPane.showMessageDialog(this, message, "Private message from " //$NON-NLS-1$
				+sender.getName(),JOptionPane.INFORMATION_MESSAGE);		
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void joinChatroom(GroupAddress chatroom, AgentAddress joiner) {
		if (getChatRoom().equals(chatroom)) {
			if (!this.participants.contains(joiner)) {
				this.participants.addElement(joiner);
	
				String n = joiner.getName()+ ChatRoomPanel.NAME_UID_SEPARATOR + joiner.getUUID();
				if ("".equals(n)) { //$NON-NLS-1$
					n = joiner.toString();
				}
				StringBuilder buffer = new StringBuilder();
				buffer.append("<i>"); //$NON-NLS-1$
				buffer.append(Locale.getString(ChatRoomPanel.class, "ENTERING_CHATROOM", n)); //$NON-NLS-1$
				buffer.append("</i>"); //$NON-NLS-1$
				addText(buffer.toString());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource()==this.participantList && e.getClickCount()==2) {
			AgentAddress adr = (AgentAddress)this.participantList.getSelectedValue();
			if (adr!=null) {
				String n = adr.getName();
				if (n==null || "".equals(n)) //$NON-NLS-1$
					n = adr.toString();
				String msg = Locale.getString(ChatRoomPanel.class, "SEND_TO", n); //$NON-NLS-1$
				this.inputMessage.setText(msg);
				this.inputMessage.setCaretPosition(msg.length());
				this.inputMessage.requestFocus();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		//
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ParticipantRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -7157354650079387915L;

		/**
		 */
		public ParticipantRenderer() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Component getListCellRendererComponent(
				JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel component = (JLabel)super.getListCellRendererComponent(
					list, value, index, isSelected,
					cellHasFocus);
			if (value instanceof AgentAddress) {
				AgentAddress adr = (AgentAddress)value;
				String n = adr.getName();
				if (n!=null && !"".equals(n)) { //$NON-NLS-1$
					component.setText(n);
				}
			}
			return component;
		}
		
	} // class ParticipanRenderer



}
