/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-12 Janus Core Developers
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
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.AgentAddress;

/**
 * Simple dialog printing the content of an incoming private message
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PrivateMessageDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 2384007666876594404L;

	private static final String SEND_ACTION = "sendAction"; //$NON-NLS-1$
	private static final String CANCEL_ACTION = "cancelAction"; //$NON-NLS-1$

	/**
	 * Receiver of the message.
	 */
	private JComboBox receiver;

	/**
	 * The content of the message to send
	 */
	private JTextArea message;

	private AgentAddress selected = null;
	
	/**
	 * @param participants
	 */
	public PrivateMessageDialog(Collection<AgentAddress> participants) {
		getContentPane().setLayout(new BorderLayout());
		setModal(true);
		setTitle(Locale.getString(PrivateMessageDialog.class, "TITLE")); //$NON-NLS-1$

		{
			JPanel topPane = new JPanel();

			topPane.setLayout(new BoxLayout(topPane, BoxLayout.X_AXIS));
			getContentPane().add(topPane, BorderLayout.NORTH);

			JLabel receiverNameLabel = new JLabel(Locale.getString(PrivateMessageDialog.class, 
					"RECEIVERNAME")); //$NON-NLS-1$
			receiverNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
			topPane.add(receiverNameLabel);
			
			this.receiver = new JComboBox();
			this.receiver.setEditable(false);
			topPane.add(this.receiver);
						
			for(AgentAddress a : participants) {
				this.receiver.addItem(a);
			}
			
			this.receiver.setSelectedIndex(0);
		}
		{
			JPanel middlePane = new JPanel();
			middlePane.setLayout(new BoxLayout(middlePane, BoxLayout.X_AXIS));
			getContentPane().add(middlePane, BorderLayout.CENTER);

			this.message = new JTextArea();
			this.message.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
			this.message.setPreferredSize(new Dimension(200, 200));
			middlePane.add(this.message);
		}
		{
			JPanel bottomPane = new JPanel();
			bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
			getContentPane().add(bottomPane, BorderLayout.SOUTH);

			JButton sendBt = new JButton(Locale.getString(PrivateMessageDialog.class, "SEND")); //$NON-NLS-1$
			sendBt.setActionCommand(SEND_ACTION);
			sendBt.addActionListener(this);
			bottomPane.add(sendBt);

			JButton cancelBt = new JButton(Locale.getString(PrivateMessageDialog.class, "CANCEL")); //$NON-NLS-1$
			cancelBt.setActionCommand(CANCEL_ACTION);
			cancelBt.addActionListener(this);
			bottomPane.add(cancelBt);
		}
		pack();
	}

	/**
	 * Replies the address of the receiver.
	 * 
	 * @return the address of the receiver.
	 */
	public AgentAddress getReceiverAddress() {
		return this.selected;
	}
	
	
	/** Replies the message.
	 * 
	 * @return the message.
	 */
	public String getMessage() {
		return this.message.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (SEND_ACTION.equals(cmd)) {
			this.selected = (AgentAddress)this.receiver.getSelectedItem();
			setVisible(false);
		} else if (CANCEL_ACTION.equals(cmd)) {
			setVisible(false);
		}
	}

}
