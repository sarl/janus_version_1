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
package org.janusproject.demo.jruby.shellagent.agent;

import java.util.Collections;
import java.util.Set;

import org.janusproject.jrubyengine.JRubyAgent;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.status.Status;

/**
 * A simple agent able to execute a Ruby Command or Script
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JRubyAgentShell extends JRubyAgent implements ChannelInteractable {

	private static final long serialVersionUID = 8374430877811627485L;

	/**
	 * Channel to communicate with the associated GUI
	 */
	private final AgentChannelImpl channelImpl = new AgentChannelImpl();

	/**
	 * the result of the execute command/script
	 */
	private String jRubyCommandResult;

	/**
	 * command to execute
	 */
	private String jRubyCommand;

	/**
	 * script to execute
	 */
	private String jRubyScript;

	/**
	 * command counter for waiting result
	 */
	private int commandCounter;

	/**
	 * boolean saying if the agent need to die
	 */
	private boolean alive = true;

	/**
	 * StringWriter for ruby errors output
	 */
	private PersonnalWriter errors;

	/**
	 * StringWriter for ruby "puts" output
	 */
	private PersonnalWriter puts;

	/**
	 * Default constructor
	 */
	public JRubyAgentShell() {
		super();

		setJRubyCommand(""); //$NON-NLS-1$
		setJRubyScript(""); //$NON-NLS-1$
		setJRubyExecutionResult(""); //$NON-NLS-1$
		
		setCommandCounter(0);
		
		this.errors = new PersonnalWriter();
		this.puts = new PersonnalWriter();
	}

	@Override
	public Status live() {
		Status s = super.live();

		if (s.isSuccess()) {
			// Check if something to do from the Shell
			if (!getJRubyCommand().equals("")) { //$NON-NLS-1$
				// a command to execute appear
				this.jRubyCommandResult = runRubyCommand(this.jRubyCommand, this.errors, this.puts) + this.puts.getBuffer() + this.errors.getBuffer();
				this.errors.getBuffer().setLength(0);// errors.flush() doesn't work
				this.puts.getBuffer().setLength(0);// puts.flush() doesn't work
				setCommandCounter(getCommandCounter() + 1);
				setJRubyCommand(""); //$NON-NLS-1$
			}
			if (!getJRubyScript().equals("")) { //$NON-NLS-1$
				// a script to execute appear
				this.jRubyCommandResult = runScriptFromPath(getJRubyScript(), this.errors, this.puts) + this.puts.getBuffer() + this.errors.getBuffer();
				this.errors.getBuffer().setLength(0);// errors.flush() doesn't work
				this.puts.getBuffer().setLength(0);// puts.flush() doesn't work
				setCommandCounter(getCommandCounter() + 1);
				setJRubyScript(""); //$NON-NLS-1$
			}
			if (!isLive())
				// this agent need to die
				this.killMe();
		}
		return s;
	}

	@Override
	public <C extends Channel> C getChannel(Class<C> type, Object... p) {
		if (type.isAssignableFrom(AgentChannelImpl.class)) {
			return type.cast(getChannelImpl());
		}
		return null;
	}

	@Override
	public Set<? extends Class<? extends Channel>> getSupportedChannels() {
		return Collections.singleton(AgentChannelImpl.class);
	}

	// Accessors
	/**
	 * @return the result to send to the IHM
	 */
	public String getJRubyExecutionResult() {
		return this.jRubyCommandResult;
	}

	/**
	 * @param result - the result comming from execution
	 */
	public void setJRubyExecutionResult(String result) {
		this.jRubyCommandResult = result;
	}

	/**
	 * @return the cmd
	 */
	public String getJRubyCommand() {
		return this.jRubyCommand;
	}

	/**
	 * @param cmd
	 */
	public void setJRubyCommand(String cmd) {
		this.jRubyCommand = cmd;
	}

	/**
	 * 
	 * @return the current JRuby script
	 */
	public String getJRubyScript() {
		return this.jRubyScript;
	}

	/**
	 * 
	 * @param script - the current JRuby script
	 */
	public void setJRubyScript(String script) {
		this.jRubyScript = script;
	}

	/**
	 * @return the cptCmd
	 */
	public int getCommandCounter() {
		return this.commandCounter;
	}

	/**
	 * @param cmdCounter - the cptCmd to set
	 */
	public void setCommandCounter(int cmdCounter) {
		this.commandCounter = cmdCounter;
	}

	/**
	 * 
	 * @return true 
	 */
	public boolean isLive() {
		return this.alive;
	}

	/**
	 * 
	 * @param live
	 */
	public void setLive(boolean live) {
		this.alive = live;
	}

	/**
	 * @return the channelImpl
	 */
	public AgentChannelImpl getChannelImpl() {
		return this.channelImpl;
	}

	/** INNER CLASS */

	private class AgentChannelImpl implements JRubyScriptExecutorChannel {

		public AgentChannelImpl() {
			//
		}

		@Override
		public Address getChannelOwner() {
			return getAddress();
		}

		@Override
		public void setJRubyCommand(String cmd) {
			JRubyAgentShell.this.setJRubyCommand(cmd);
		}

		@Override
		public String getJRubyExecutionResult() {
			return JRubyAgentShell.this.getJRubyExecutionResult();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getCommandCounter() {
			return JRubyAgentShell.this.getCommandCounter();
		}

		@Override
		public void killAgent() {
			JRubyAgentShell.this.setLive(false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setJRubyScript(String path) {
			JRubyAgentShell.this.setJRubyScript(path);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getScriptPath() {
			if (JRubyAgentShell.this.getRubyDirectory() == null) {
				return ""; //$NON-NLS-1$
			}
			return JRubyAgentShell.this.getRubyDirectory().getDirectory().getAbsolutePath();
		}

	}

}
