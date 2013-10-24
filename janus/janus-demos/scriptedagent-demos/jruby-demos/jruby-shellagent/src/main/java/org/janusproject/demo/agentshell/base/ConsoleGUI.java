/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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

package org.janusproject.demo.agentshell.base;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;

/** UI for the agent console.
 * 
 * @author $Author: sgalland$
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ConsoleGUI extends JFrame implements AgentShellChannel.ResultListener, AgentShellChannel.LogListener {

	private static final long serialVersionUID = 4558331958680043895L;

	private static AgentShellChannel getChannelFor(AgentAddress agent) {
		Kernel kernel = Kernels.get();
		if (kernel!=null) {
			AgentShellChannel channel = kernel.getChannelManager().getChannel(agent, AgentShellChannel.class);
			if (channel!=null) return channel;
		}
		throw new IllegalStateException();
	}

	private final JTextArea commandInput;
	private final JTextArea textConsole;
	private final JButton goButton;

	/**
	 * weakref to the agent's channel
	 */
	private final WeakReference<AgentShellChannel> agentChannel;

	/**
	 * Build the GUI
	 * @param groovyAgent
	 */
	public ConsoleGUI(AgentAddress groovyAgent) {
		this(getChannelFor(groovyAgent));
	}

	/**
	 * Build the GUI
	 * @param agentChannel - the channel used to communicate with the associated agent able to execute a groovy script
	 */
	public ConsoleGUI(AgentShellChannel agentChannel) {
		assert (agentChannel != null);
		JButton button;
		JLabel label;

		this.agentChannel = new WeakReference<AgentShellChannel>(agentChannel);

		Address owner = agentChannel.getChannelOwner();
		assert (owner != null);
		String ownerName = owner.getName();
		if (ownerName == null || "".equals(ownerName)) { //$NON-NLS-1$
			ownerName = owner.toString();
		}

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ConsoleGUI.this.getAgentChannel().killAgent();
			}
		});

		setTitle(owner.getName());
		setLayout(new BorderLayout());

		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
		add(BorderLayout.EAST, toolPanel);

		this.goButton = new JButton(Locale.getString(ConsoleGUI.class, "BTN_GO")); //$NON-NLS-1$
		this.goButton.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = ConsoleGUI.this.commandInput.getText().trim();
				if (!cmd.isEmpty()) {
					getAgentChannel().runCommand(cmd, ConsoleGUI.this);
				}
			}
		});
		toolPanel.add(this.goButton);

		button = new JButton(Locale.getString(ConsoleGUI.class, "BTN_RUN")); //$NON-NLS-1$
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(getAgentChannel().getScriptPath());
				chooser.setFileFilter(getAgentChannel().getFileFilter());
				if (chooser.showOpenDialog(ConsoleGUI.this)==JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					assert(selectedFile!=null);
					getAgentChannel().runScript(selectedFile, ConsoleGUI.this);
				}
			}
		});
		toolPanel.add(button);

		button = new JButton(Locale.getString(ConsoleGUI.class, "BTN_ABOUT")); //$NON-NLS-1$
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog pop = new JDialog(ConsoleGUI.this);
				pop.setResizable(false);
				pop.setModal(true);
				JLabel aboutLabel = new JLabel(Locale.getString(ConsoleGUI.class, "ABOUT_MESSAGE")); //$NON-NLS-1$
				pop.add(aboutLabel);
				pop.pack();
				pop.setVisible(true);
			}
		});
		toolPanel.add(button);

		JPanel inputPanel = new JPanel(new BorderLayout());
		add(BorderLayout.NORTH, inputPanel);

		label = new JLabel(Locale.getString(ConsoleGUI.class, "LABEL_IN")); //$NON-NLS-1$
		inputPanel.add(label, BorderLayout.WEST);

		this.commandInput = new JTextArea(1, 25);
		this.commandInput.setText(""); //$NON-NLS-1$
		label.setLabelFor(this.commandInput);
		inputPanel.add(this.commandInput, BorderLayout.CENTER);
		this.commandInput.addKeyListener(new KeyAdapter() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void keyTyped(KeyEvent event) {
				if (event.getKeyChar() == '\n') {
					ConsoleGUI.this.goButton.doClick();
				}
			}
		});

		this.textConsole = new JTextArea(20, 30);
		this.textConsole.setText("> "); //$NON-NLS-1$
		add(BorderLayout.CENTER, new JScrollPane(this.textConsole));

		pack();
		setLocationRelativeTo(null);
	}

	/** Replies the channel used by this UI.
	 * 
	 * @return the Agent's channel associated to this GUI
	 */
	protected AgentShellChannel getAgentChannel() {
		return this.agentChannel.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResultAvailable(List<String> results) {
		for(int i=0; i<results.size(); i+=2) {
			this.textConsole.append(results.get(i).trim());
			this.textConsole.append("\n"); //$NON-NLS-1$
			this.textConsole.append(results.get(i+1).trim());
			this.textConsole.append("\n> "); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLogAvailable(LogRecord log) {
		this.textConsole.append(log.toString());
	}

}
