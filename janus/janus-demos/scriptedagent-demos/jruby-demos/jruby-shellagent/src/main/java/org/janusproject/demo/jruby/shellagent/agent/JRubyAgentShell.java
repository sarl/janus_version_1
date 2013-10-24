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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.filechooser.FileFilter;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demo.agentshell.base.AgentShellChannel;
import org.janusproject.demo.agentshell.base.AgentShellChannel.LogListener;
import org.janusproject.demo.agentshell.base.AgentShellChannel.ResultListener;
import org.janusproject.jrubyengine.RubyAgent;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.event.ListenerCollection;

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
public class JRubyAgentShell extends RubyAgent implements ChannelInteractable {

	private static final long serialVersionUID = 8374430877811627485L;

	/**
	 * Channel to communicate with the associated GUI
	 */
	private final AgentChannelImpl channelImpl = new AgentChannelImpl();

	/** Buffer for the next command to run.
	 */
	private final List<String> command = new LinkedList<String>();
	
	/** Buffer for the next command to run.
	 */
	private final Collection<ResultListener> listeners = new LinkedList<ResultListener>();
	
	/** Buffer for the next script to run.
	 */
	private final List<File> scripts = new LinkedList<File>();

	/** Listeners on log events.
	 */
	private final ListenerCollection<LogListener> logListeners = new ListenerCollection<LogListener>();
	
	/**
	 * Default constructor
	 */
	public JRubyAgentShell() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		Status s = super.activate(parameters);
		getLogger().addHandler(new LogHandler());
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Status live() {
		Status s = super.live();
		if (s.isSuccess()) {
			String r;
			
			List<String> results = new ArrayList<String>();
			
			Iterator<String> commandIterator = this.command.iterator();
			while (commandIterator.hasNext()) {
				String cmd = commandIterator.next();
				commandIterator.remove();
				r = runCommandAsInteractiveInterpreter(cmd);
				results.add(cmd);
				results.add(r);
			}
			
			Iterator<File> scriptIterator = this.scripts.iterator();
			while (scriptIterator.hasNext()) {
				File file = scriptIterator.next();
				scriptIterator.remove();
				r = runScriptAsInteractiveInterpreter(file);
				results.add(Locale.getString(JRubyAgentShell.class, "RUN_SCRIPT_CMD", file.getAbsolutePath())); //$NON-NLS-1$
				results.add(r);
			}

			Iterator<ResultListener> listenerIterator = this.listeners.iterator();
			while (listenerIterator.hasNext()) {
				ResultListener listener = listenerIterator.next();
				listener.onResultAvailable(results);
			}
		}
		return s;
	}

	@Override
	public <C extends Channel> C getChannel(Class<C> type, Object... p) {
		if (type.isAssignableFrom(AgentShellChannel.class)) {
			return type.cast(this.channelImpl);
		}
		return null;
	}

	@Override
	public Set<? extends Class<? extends Channel>> getSupportedChannels() {
		return Collections.singleton(AgentShellChannel.class);
	}

	/**
	 * @author $Author: sgalland$
	 * @author $Author: lcabasson$
	 * @author $Author: cwintz$
	 * @author $Author: ngaud$
	 */
	private class AgentChannelImpl implements AgentShellChannel {

		public AgentChannelImpl() {
			//
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
		@SuppressWarnings("synthetic-access")
		@Override
		public FileFilter getFileFilter() {
			return JRubyAgentShell.this.getScriptExecutionContext().getFileFilter(true);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void runCommand(String cmd, ResultListener listener) {
			synchronized(JRubyAgentShell.this) {
				JRubyAgentShell.this.command.add(cmd);
				JRubyAgentShell.this.listeners.add(listener);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void killAgent() {
			JRubyAgentShell.this.killMe();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void runScript(File absolutePath, ResultListener listener) {
			synchronized(JRubyAgentShell.this) {
				JRubyAgentShell.this.scripts.add(absolutePath);
				JRubyAgentShell.this.listeners.add(listener);
			}	
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public File getScriptPath() {
			return getScriptRepository().getLocalDirectories().next();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void addLogListener(LogListener listener) {
			JRubyAgentShell.this.logListeners.add(LogListener.class, listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void removeLogListener(LogListener listener) {
			JRubyAgentShell.this.logListeners.remove(LogListener.class, listener);
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @author $Author: lcabasson$
	 * @author $Author: cwintz$
	 * @author $Author: ngaud$
	 */
	private class LogHandler extends Handler {

		public LogHandler() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void publish(LogRecord record) {
			LogListener[] list = JRubyAgentShell.this.logListeners.getListeners(LogListener.class);
			for(LogListener listener : list) {
				listener.onLogAvailable(record);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void flush() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws SecurityException {
			//
		}

	}

}
