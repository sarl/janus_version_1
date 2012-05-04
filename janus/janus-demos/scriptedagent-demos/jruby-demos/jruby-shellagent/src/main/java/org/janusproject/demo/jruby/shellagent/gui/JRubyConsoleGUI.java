/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.demo.jruby.shellagent.gui;

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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.demo.jruby.shellagent.agent.JRubyScriptExecutorChannel;
import org.janusproject.jrubyengine.RubyExecutionScriptContext;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;

/**
 * Provides the GUI for running a jruby's command or script
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JRubyConsoleGUI extends JFrame {

	private static final long serialVersionUID = -2349693519634914832L;

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	private JLabel rubyCommandLabel;
	private JLabel rubyConsoleLabel;

	private JTextArea rubyCommandTextArea;
	private JTextArea rubyConsoleTextArea;

	private JButton runRubyCommandButton;
	private JButton runRubyScriptButton;
	private JButton aboutButton;

	private static JRubyScriptExecutorChannel getChannelFor(AgentAddress chatAgent) {
		Kernel kernel = Kernels.get();
		if (kernel != null) {
			JRubyScriptExecutorChannel channel = kernel.getChannelManager().getChannel(chatAgent, JRubyScriptExecutorChannel.class);
			if (channel != null)
				return channel;
		}
		throw new IllegalStateException();
	}

	/**
	 * weakref to the agent's channel
	 */
	private final WeakReference<JRubyScriptExecutorChannel> agentChannel;

	/**
	 * Build the GUI
	 * @param jrubyAgent
	 */
	public JRubyConsoleGUI(AgentAddress jrubyAgent) {
		this(getChannelFor(jrubyAgent));
	}

	/**
	 * Build the GUI
	 * @param agentChannel - the channel used to communicate with the associated agent able to execute a ruby script
	 */
	public JRubyConsoleGUI(JRubyScriptExecutorChannel agentChannel) {
		assert (agentChannel != null);

		this.agentChannel = new WeakReference<JRubyScriptExecutorChannel>(agentChannel);

		Address owner = agentChannel.getChannelOwner();
		assert (owner != null);
		String ownerName = owner.getName();
		if (ownerName == null || "".equals(ownerName)) { //$NON-NLS-1$
			ownerName = owner.toString();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowClosing(WindowEvent e) {
				JRubyConsoleGUI.this.getAgentChannel().killAgent();
				Kernels.killAll();
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

		this.rubyCommandLabel = new JLabel(Locale.getString(JRubyConsoleGUI.class, "LABEL_IN")); //$NON-NLS-1$
		jpnn.add(this.rubyCommandLabel, BorderLayout.WEST);

		this.aboutButton = new JButton(Locale.getString(JRubyConsoleGUI.class, "BTN_ABOUT")); //$NON-NLS-1$
		this.aboutButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame pop = new JFrame();
				JLabel aboutLabel = new JLabel(Locale.getString(JRubyConsoleGUI.class, "ABOUT_MESSAGE")); //$NON-NLS-1$
				pop.add(aboutLabel);
				pop.pack();
				pop.setResizable(false);
				pop.setVisible(true);

			}
		});
		jpnn.add(this.aboutButton, BorderLayout.EAST);
		jpn.add(jpnn, BorderLayout.NORTH);

		this.rubyCommandTextArea = new JTextArea(1, 25);
		this.rubyCommandTextArea.setText(""); //$NON-NLS-1$
		this.rubyCommandTextArea.addKeyListener(new KeyListener() {
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
					JRubyConsoleGUI.this.getRubyCommandTextArea().setText(JRubyConsoleGUI.this.getRubyCommandTextArea().getText().substring(0, JRubyConsoleGUI.this.getRubyCommandTextArea().getText().length() - 1));
					JRubyConsoleGUI.this.getRunRubyCommandButton().doClick();
				}
			}
		});
		jpn.add(this.rubyCommandTextArea, BorderLayout.CENTER);

		JPanel jpe = new JPanel();

		this.runRubyCommandButton = new JButton(Locale.getString(JRubyConsoleGUI.class, "BTN_GO")); //$NON-NLS-1$		

		this.rubyConsoleTextArea = new JTextArea(20, 30);
		this.rubyConsoleTextArea.setText(">"); //$NON-NLS-1$

		this.runRubyCommandButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int cpt = JRubyConsoleGUI.this.getAgentChannel().getCommandCounter();
				JRubyConsoleGUI.this.getAgentChannel().setJRubyCommand(JRubyConsoleGUI.this.getRubyCommandTextArea().getText());
				while (cpt == JRubyConsoleGUI.this.getAgentChannel().getCommandCounter()) {
					// wait for the result comming
					// sometimes a bug could happen if there is no sleep here
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				JRubyConsoleGUI.this.getRubyConsoleTextArea().setText(JRubyConsoleGUI.this.getRubyConsoleTextArea().getText() + JRubyConsoleGUI.this.getAgentChannel().getJRubyExecutionResult() + "\n>"); //$NON-NLS-1$

			}
		});
		jpe.add(this.runRubyCommandButton, BorderLayout.WEST);

		this.runRubyScriptButton = new JButton(Locale.getString(JRubyConsoleGUI.class, "BTN_RUN")); //$NON-NLS-1$

		this.runRubyScriptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				FileFilter rubyFilter = new FileNameExtensionFilter("Fichier Ruby", "rb"); //$NON-NLS-1$  //$NON-NLS-2$ 

				String path = JRubyConsoleGUI.this.getAgentChannel().getScriptPath();
				if (path == null && "".equals(path)) { //$NON-NLS-1$ 
					path = "./"; //$NON-NLS-1$ 
				}
				JFileChooser chooser = new JFileChooser(path);

				chooser.addChoosableFileFilter(rubyFilter);
				chooser.showOpenDialog(null);

				String filePath = ""; //$NON-NLS-1$ 
				if (chooser.getSelectedFile() != null)
					filePath = chooser.getSelectedFile().getAbsolutePath();
				else {
					// no file choose
					return;
				}
				if (!filePath.substring(filePath.length() - 3, filePath.length()).equals(RubyExecutionScriptContext.RubyFileExtension)) {
					JRubyConsoleGUI.this.getLogger().severe(Locale.getString(JRubyConsoleGUI.class, "NOT_A_RUBY_SCRIPT", filePath)); //$NON-NLS-1$ 
					return;
				}

				int cpt = JRubyConsoleGUI.this.getAgentChannel().getCommandCounter();
				JRubyConsoleGUI.this.getAgentChannel().setJRubyScript(chooser.getSelectedFile().getAbsolutePath());
				while (cpt == JRubyConsoleGUI.this.getAgentChannel().getCommandCounter()) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				JRubyConsoleGUI.this.getRubyConsoleTextArea().setText(JRubyConsoleGUI.this.getRubyConsoleTextArea().getText() + JRubyConsoleGUI.this.getAgentChannel().getJRubyExecutionResult() + "\n>"); //$NON-NLS-1$

			}

		});
		jpe.add(this.runRubyScriptButton, BorderLayout.EAST);
		jpn.add(jpe, BorderLayout.EAST);

		JPanel jps = new JPanel();
		jps.setLayout(new BorderLayout());

		this.rubyConsoleLabel = new JLabel(Locale.getString(JRubyConsoleGUI.class, "LABEL_OUT")); //$NON-NLS-1$ 
		jps.add(this.rubyConsoleLabel, BorderLayout.NORTH);

		jps.add(this.rubyConsoleTextArea, BorderLayout.SOUTH);

		getContentPane().add(jpn, BorderLayout.NORTH);
		getContentPane().add(jps, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * 
	 * @return the Agent's channel associated to this GUI
	 */
	protected JRubyScriptExecutorChannel getAgentChannel() {
		return this.agentChannel.get();
	}

	/**
	 * @return the logger associated to this class
	 */
	protected Logger getLogger() {
		return this.logger;
	}

	/**
	 * @return the text area containing the ruby command to be run
	 */
	protected JTextArea getRubyCommandTextArea() {
		return this.rubyCommandTextArea;
	}

	/**
	 * @return the text area used as output console for jruby script execution
	 */
	protected JTextArea getRubyConsoleTextArea() {
		return this.rubyConsoleTextArea;
	}

	/**
	 * @return the button able to run a ruby command
	 */
	protected JButton getRunRubyCommandButton() {
		return this.runRubyCommandButton;
	}

	/**
	 * @return the button able to run a ruby script
	 */
	protected JButton getRunRubyScriptButton() {
		return this.runRubyScriptButton;
	}

}
