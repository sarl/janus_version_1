/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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

package org.janusproject.demo.groovy.groovyshellagent.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.ref.WeakReference;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.demo.groovy.groovyshellagent.agent.channel.GroovyScriptExecutorChannel;
import org.janusproject.groovyengine.GroovyExecutionScriptContext;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;

/**
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */

public class GroovyConsoleGUI extends JFrame
{
	private static final long serialVersionUID = -2349693519634914832L;

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	private JLabel groovyCommandLabel;
	private JLabel groovyConsoleLabel;

	private JTextArea groovyCommandTextArea;
	private JTextArea groovyConsoleTextArea;

	private JButton runGroovyCommandButton;
	private JButton runGroovyScriptButton;
	private JButton aboutButton;

	
	private static GroovyScriptExecutorChannel getChannelFor(AgentAddress groovyAgent) {
		Kernel kernel = Kernels.get();
		if (kernel!=null) {
			GroovyScriptExecutorChannel channel = kernel.getChannelManager().getChannel(groovyAgent, GroovyScriptExecutorChannel.class);
			if (channel!=null) return channel;
		}
		throw new IllegalStateException();
	}

	/**
	 * weakref to the agent's channel
	 */
	private final WeakReference<GroovyScriptExecutorChannel> agentChannel;

	/**
	 * Build the GUI
	 * @param groovyAgent
	 */
	public GroovyConsoleGUI(AgentAddress groovyAgent) {
		this(getChannelFor(groovyAgent));
	}

	/**
	 * Build the GUI
	 * @param agentChannel - the channel used to communicate with the associated agent able to execute a groovy script
	 */
	public GroovyConsoleGUI(GroovyScriptExecutorChannel agentChannel) {
		assert (agentChannel != null);

		this.agentChannel = new WeakReference<GroovyScriptExecutorChannel>(agentChannel);

		Address owner = agentChannel.getChannelOwner();
		assert (owner != null);
		String ownerName = owner.getName();
		if (ownerName == null || "".equals(ownerName)) { //$NON-NLS-1$
			ownerName = owner.toString();
		}

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowClosing(WindowEvent e) {
				GroovyConsoleGUI.this.getAgentChannel().killAgent();
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				//
			}

			@Override
			public void windowActivated(WindowEvent e) {
				//
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				//
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				//
			}

			@Override
			public void windowIconified(WindowEvent e) {
				//
			}

			@Override
			public void windowOpened(WindowEvent e) {
				//
			}

		});

		super.setTitle(owner.getName());

		JPanel jpn = new JPanel();
		jpn.setLayout(new BorderLayout(10, 10));

		JPanel jpnn = new JPanel();
		jpnn.setLayout(new BorderLayout(10, 10));

		this.groovyCommandLabel = new JLabel(Locale.getString(GroovyConsoleGUI.class, "LABEL_IN")); //$NON-NLS-1$
		jpnn.add(this.groovyCommandLabel, BorderLayout.WEST);

		this.aboutButton = new JButton(Locale.getString(GroovyConsoleGUI.class, "BTN_ABOUT")); //$NON-NLS-1$
		this.aboutButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame pop = new JFrame();
				JLabel aboutLabel = new JLabel(Locale.getString(GroovyConsoleGUI.class, "ABOUT_MESSAGE")); //$NON-NLS-1$
				pop.add(aboutLabel);
				pop.pack();
				pop.setResizable(false);
				pop.setVisible(true);

			}
		});
		jpnn.add(this.aboutButton, BorderLayout.EAST);
		jpn.add(jpnn, BorderLayout.NORTH);

		this.groovyCommandTextArea = new JTextArea(1, 25);
		this.groovyCommandTextArea.setText(""); //$NON-NLS-1$
		this.groovyCommandTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				//
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				//
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				if (arg0.getKeyChar() == '\n') {
					GroovyConsoleGUI.this.getGroovyCommandTextArea().setText(GroovyConsoleGUI.this.getGroovyCommandTextArea().getText().substring(0, GroovyConsoleGUI.this.getGroovyCommandTextArea().getText().length() - 1));
					GroovyConsoleGUI.this.getRunGroovyCommandButton().doClick();
				}
			}
		});
		jpn.add(this.groovyCommandTextArea, BorderLayout.CENTER);

		JPanel jpe = new JPanel();

		this.runGroovyCommandButton = new JButton(Locale.getString(GroovyConsoleGUI.class, "BTN_GO")); //$NON-NLS-1$		

		this.groovyConsoleTextArea = new JTextArea(20, 30);
		this.groovyConsoleTextArea.setText(">"); //$NON-NLS-1$

		this.runGroovyCommandButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int cpt = GroovyConsoleGUI.this.getAgentChannel().getCommandCounter();
				GroovyConsoleGUI.this.getAgentChannel().setGroovyCommand(GroovyConsoleGUI.this.getGroovyCommandTextArea().getText());
				while (cpt == GroovyConsoleGUI.this.getAgentChannel().getCommandCounter()) {
					// wait for the result comming
					// sometimes a bug could happen if there is no sleep here
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				GroovyConsoleGUI.this.getGroovyConsoleTextArea().setText(GroovyConsoleGUI.this.getGroovyConsoleTextArea().getText() + GroovyConsoleGUI.this.getAgentChannel().getGroovyExecutionResult() + "\n>"); //$NON-NLS-1$

			}
		});
		jpe.add(this.runGroovyCommandButton, BorderLayout.WEST);

		this.runGroovyScriptButton = new JButton(Locale.getString(GroovyConsoleGUI.class, "BTN_RUN")); //$NON-NLS-1$

		this.runGroovyScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				FileFilter groovyFilter = new FileNameExtensionFilter("Fichier Groovy", "gy"); //$NON-NLS-1$  //$NON-NLS-2$ 

				String path = GroovyConsoleGUI.this.getAgentChannel().getScriptPath();
				if (path == null || "".equals(path)) { //$NON-NLS-1$ 
					path = "./"; //$NON-NLS-1$ 
				}
				JFileChooser chooser = new JFileChooser(path);

				chooser.addChoosableFileFilter(groovyFilter);
				chooser.showOpenDialog(null);

				String filePath = ""; //$NON-NLS-1$ 
				if (chooser.getSelectedFile() != null)
					filePath = chooser.getSelectedFile().getAbsolutePath();
				else {
					// no file choose
					return;
				}
				if (!filePath.substring(filePath.length() - 3, filePath.length()).equals(GroovyExecutionScriptContext.GROOVY_FILE_EXTENSION)) {
					GroovyConsoleGUI.this.getLogger().severe(Locale.getString(GroovyConsoleGUI.class, "NOT_A_GROOVY_SCRIPT", filePath)); //$NON-NLS-1$ 
					return;
				}

				int cpt = GroovyConsoleGUI.this.getAgentChannel().getCommandCounter();
				GroovyConsoleGUI.this.getAgentChannel().setGroovyScript(chooser.getSelectedFile().getAbsolutePath());
				while (cpt == GroovyConsoleGUI.this.getAgentChannel().getCommandCounter()) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				GroovyConsoleGUI.this.getGroovyConsoleTextArea().setText(GroovyConsoleGUI.this.getGroovyConsoleTextArea().getText() + GroovyConsoleGUI.this.getAgentChannel().getGroovyExecutionResult() + "\n>"); //$NON-NLS-1$

			}

		});
		jpe.add(this.runGroovyScriptButton, BorderLayout.EAST);
		jpn.add(jpe, BorderLayout.EAST);

		JPanel jps = new JPanel();
		jps.setLayout(new BorderLayout());

		this.groovyConsoleLabel = new JLabel(Locale.getString(GroovyConsoleGUI.class, "LABEL_OUT")); //$NON-NLS-1$ 
		jps.add(this.groovyConsoleLabel, BorderLayout.NORTH);

		jps.add(this.groovyConsoleTextArea, BorderLayout.SOUTH);

		getContentPane().add(jpn, BorderLayout.NORTH);
		getContentPane().add(jps, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * 
	 * @return the Agent's channel associated to this GUI
	 */
	protected GroovyScriptExecutorChannel getAgentChannel() {
		return this.agentChannel.get();
	}

	/**
	 * @return the logger associated to this class
	 */
	protected Logger getLogger() {
		return this.logger;
	}

	/**
	 * @return the text area containing the groovy command to be run
	 */
	protected JTextArea getGroovyCommandTextArea() {
		return this.groovyCommandTextArea;
	}

	/**
	 * @return the text area used as output console for Groovy script execution
	 */
	protected JTextArea getGroovyConsoleTextArea() {
		return this.groovyConsoleTextArea;
	}

	/**
	 * @return the button able to run a groovy command
	 */
	protected JButton getRunGroovyCommandButton() {
		return this.runGroovyCommandButton;
	}

	/**
	 * @return the button able to run a groovy script
	 */
	protected JButton getRunGroovyScriptButton() {
		return this.runGroovyScriptButton;
	}

}
