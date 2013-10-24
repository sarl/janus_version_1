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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * Dialog to select a chatrooms.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ChatRoomSelectionDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -5940724164033510565L;
	
	private static final String OK_ACTION = "okAction"; //$NON-NLS-1$
	private static final String CANCEL_ACTION = "cancelAction"; //$NON-NLS-1$
	
	private GroupAddress selectedRoom = null;
	private final DefaultListModel model;
	private final JList list;
	
	/**
	 * @param rooms are the available rooms. 
	 */
	public ChatRoomSelectionDialog(List<GroupAddress> rooms) {
		assert(rooms!=null);
		
		getContentPane().setLayout(new BorderLayout());
		setModal(true);
		setTitle(Locale.getString(ChatRoomSelectionDialog.class, "TITLE")); //$NON-NLS-1$

		this.model = new DefaultListModel();
		this.list = new JList(this.model);
		this.list.setCellRenderer(new RoomRenderer());
		JScrollPane scrollPane = new JScrollPane(this.list);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		for(GroupAddress adr : rooms) {
			this.model.addElement(adr);
		}
		
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
		getContentPane().add(bottomPane, BorderLayout.SOUTH);
		
		JButton okBt = new JButton(Locale.getString(ChatRoomSelectionDialog.class, "OK")); //$NON-NLS-1$
		okBt.setActionCommand(OK_ACTION);
		okBt.addActionListener(this);
		bottomPane.add(okBt);
		
		JButton cancelBt = new JButton(Locale.getString(ChatRoomSelectionDialog.class, "CANCEL")); //$NON-NLS-1$
		cancelBt.setActionCommand(CANCEL_ACTION);
		cancelBt.addActionListener(this);
		bottomPane.add(cancelBt);
		
		pack();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (OK_ACTION.equals(cmd)) {
			this.selectedRoom = (GroupAddress)this.list.getSelectedValue();
			setVisible(false);
		}
		else if (CANCEL_ACTION.equals(cmd)) {
			setVisible(false);
		}
	}
	
	/** Replies the selected chat room.
	 * 
	 * @return the selected chat room, or <code>null</code>.
	 */
	public GroupAddress getSelectedChatRoom() {
		return this.selectedRoom;
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class RoomRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -6121546468012658959L;

		/**
		 */
		public RoomRenderer() {
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
			if (value instanceof GroupAddress) {
				GroupAddress adr = (GroupAddress)value;
				String n = adr.getName();
				if (n!=null && !"".equals(n)) { //$NON-NLS-1$
					component.setText(n);
				}
			}
			return component;
		}
		
	} // class RoomRenderer

}
