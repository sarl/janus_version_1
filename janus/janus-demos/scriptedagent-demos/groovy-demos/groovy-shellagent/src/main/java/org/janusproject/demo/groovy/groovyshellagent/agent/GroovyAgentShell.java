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

package org.janusproject.demo.groovy.groovyshellagent.agent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.LogRecord;

import org.janusproject.demo.groovy.groovyshellagent.agent.channel.GroovyScriptExecutorChannel;
import org.janusproject.demo.groovy.groovyshellagent.agent.channel.LoggingChannel;
import org.janusproject.groovyengine.GroovyAgent;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.status.Status;

/**
 * An agent able to execute a groovy Command or Script
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */

public class GroovyAgentShell extends GroovyAgent implements ChannelInteractable
{
	private static final long serialVersionUID = 8374430877811627485L;

	/**
	 * Channel to communicate with the associated GUI
	 */
	private final AgentChannelImpl channelImpl = new AgentChannelImpl();
	
	/**
	 * Channel to communicate logged messages to the associated GUI
	 */
	private final LoggingChannel logChannel = new AgentLoggingChannelImpl();

	/**
	 * the result of the execute command/script
	 */
	private String groovyCommandResult;

	/**
	 * command to execute
	 */
	private String groovyCommand;

	/**
	 * script to execute
	 */
	private String groovyScript;

	/**
	 * Errors sent by Groovy Engine logger
	 */
	private List<LogRecord> logRecords = new LinkedList<LogRecord>();
	
	/**
	 * command counter for waiting result
	 */
	private int commandCounter;

	/**
	 * boolean saying if the agent need to die
	 */
	private boolean alive = true;

	/**
	 * StringWriter for Groovy errors output
	 */
	private PersonnalWriter errors;

	/**
	 * StringWriter for groovy "puts" output
	 */
	private PersonnalWriter puts;

	/**
	 * Default constructor
	 */
	public GroovyAgentShell() 
	{
		super();

		setGroovyCommand(""); //$NON-NLS-1$
		setGroovyScript(""); //$NON-NLS-1$
		setGroovyExecutionResult(""); //$NON-NLS-1$
		
		setCommandCounter(0);
		
		this.errors = new PersonnalWriter();
		this.puts = new PersonnalWriter();
		
		this.getScriptExecutor().getLogger().addHandler(
				new ForwardToChannelLoggingHandler(this.logChannel)
		);
	}

	@Override
	public Status live() {
		Status s = super.live();

		if (s.isSuccess()) {
			// Check if something to do from the Shell
			if (!getGroovyCommand().equals("")) { //$NON-NLS-1$
				// a command to execute appear
				this.groovyCommandResult = runGroovyCommand(this.groovyCommand, this.errors, this.puts) + this.puts.getBuffer() + this.errors.getBuffer();
				this.errors.getBuffer().setLength(0);// errors.flush() doesn't work
				this.puts.getBuffer().setLength(0);// puts.flush() doesn't work
				setCommandCounter(getCommandCounter() + 1);
				setGroovyCommand(""); //$NON-NLS-1$
			}
			if (!getGroovyScript().equals("")) { //$NON-NLS-1$
				// a script to execute appear
				this.groovyCommandResult = runScriptFromPath(getGroovyScript(), this.errors, this.puts) + this.puts.getBuffer() + this.errors.getBuffer();
				this.errors.getBuffer().setLength(0);// errors.flush() doesn't work
				this.puts.getBuffer().setLength(0);// puts.flush() doesn't work
				setCommandCounter(getCommandCounter() + 1);
				setGroovyScript(""); //$NON-NLS-1$
			}
			if (!this.logRecords.isEmpty()) {
				for (LogRecord lr : this.logRecords)
					this.groovyCommandResult += lr.getMessage() + "\n"; //$NON-NLS-1$
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
	
	/**
	 * Add a log record to forward to the UI
	 * @param lr Log record to forward
	 */
	void addLogRecord(LogRecord lr) {
		this.logRecords.add(lr);
	}
	
	// Accessors
	/**
	 * @return the result to send to the IHM
	 */
	public String getGroovyExecutionResult() {
		return this.groovyCommandResult;
	}

	/**
	 * @param result - the result comming from execution
	 */
	public void setGroovyExecutionResult(String result) {
		this.groovyCommandResult = result;
	}

	/**
	 * @return the cmd
	 */
	public String getGroovyCommand() {
		return this.groovyCommand;
	}

	/**
	 * @param cmd
	 */
	public void setGroovyCommand(String cmd) {
		this.groovyCommand = cmd;
	}

	/**
	 * 
	 * @return the current groovy script
	 */
	public String getGroovyScript() {
		return this.groovyScript;
	}

	/**
	 * 
	 * @param script - the current groovy script
	 */
	public void setGroovyScript(String script) {
		this.groovyScript = script;
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

	private class AgentChannelImpl implements GroovyScriptExecutorChannel {

		public AgentChannelImpl() {
			//
		}

		@Override
		public Address getChannelOwner() {
			return getAddress();
		}

		@Override
		public void setGroovyCommand(String cmd) {
			GroovyAgentShell.this.setGroovyCommand(cmd);
		}

		@Override
		public String getGroovyExecutionResult() {
			return GroovyAgentShell.this.getGroovyExecutionResult();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getCommandCounter() {
			return GroovyAgentShell.this.getCommandCounter();
		}

		@Override
		public void killAgent() {
			GroovyAgentShell.this.setLive(false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setGroovyScript(String path) {
			GroovyAgentShell.this.setGroovyScript(path);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getScriptPath() 
		{
			if (GroovyAgentShell.this.getGroovyDirectory() == null) {
				return ""; //$NON-NLS-1$
			}
			return GroovyAgentShell.this.getGroovyDirectory().getDirectory().getAbsolutePath();
		}

	}

	private class AgentLoggingChannelImpl implements LoggingChannel
	{		
		/**
		 * Creates a new channel to forward logging message
		 */
		public AgentLoggingChannelImpl() {
			// default constructor
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Address getChannelOwner() {
			return getAddress();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void loggingMessageReceived(LogRecord lr) {
			GroovyAgentShell.this.addLogRecord(lr);
		}
		
	}
	
}
