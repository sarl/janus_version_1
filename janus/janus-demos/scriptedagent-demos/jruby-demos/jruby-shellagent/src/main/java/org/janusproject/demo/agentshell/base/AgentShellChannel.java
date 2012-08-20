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

import java.io.File;
import java.util.EventListener;
import java.util.List;
import java.util.logging.LogRecord;

import javax.swing.filechooser.FileFilter;

import org.janusproject.kernel.channels.Channel;

/**
 * Channel that describes the possible interactions between 
 * scripted agents and the shell console.
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

public interface AgentShellChannel extends Channel {

	/**
	 * @return the preferred file filter for the script interpreter.
	 */
	public FileFilter getFileFilter();

	/**
	 * Set the command to run
	 * @param cmd - the Groovy's command to run
	 * @param listener is the listener on the results.
	 */
	public void runCommand(String cmd, ResultListener listener);
	
	/**
	 * 
	 */
	public void killAgent();
	
	/**
	 * set the path to the script to run
	 * @param absolutePath - the path to the script to run
	 * @param listener is the listener on the results.
	 */
	public void runScript(File absolutePath, ResultListener listener);
	
	/**
	 * 
	 * @return the path of the directory containing Groovy's scripts
	 */
	public File getScriptPath();
	
	/** Add listener on log messages.
	 * 
	 * @param listener
	 */
	public void addLogListener(LogListener listener);
	
	/** Remove listener on log messages.
	 * 
	 * @param listener
	 */
	public void removeLogListener(LogListener listener);

	/** Listener on the results of a run.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	public interface ResultListener extends EventListener {

		/** Invoked when results are available.
		 * 
		 * @param results is the list of pairs composed of the command and the corresponding results.
		 */
		public void onResultAvailable(List<String> results);
		
	}
	
	/** Listener on logs.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	public interface LogListener extends EventListener {

		/** Invoked when something was logged.
		 * 
		 * @param log
		 */
		public void onLogAvailable(LogRecord log);
		
	}
	
}
