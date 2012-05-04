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

import org.janusproject.kernel.channels.Channel;

/**
 * 
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JRubyScriptExecutorChannel extends Channel{

	/**
	 * set the JRuby's command to run
	 * @param cmd - the JRuby's command to run
	 */
	public void setJRubyCommand(String cmd);
	
	/**
	 * get the result of the execute command/script
	 * @return the result of the execute command/script
	 */
	public String getJRubyExecutionResult();
	
	/**
	 * 
	 */
	public void killAgent();
	
	/**
	 * get command counter for waiting result
	 * @return command counter for waiting result
	 */
	public int getCommandCounter();
	
	/**
	 * set the path to the script to run
	 * @param absolutePath - the path to the script to run
	 */
	public void setJRubyScript(String absolutePath);
	
	/**
	 * 
	 * @return the path of the directory containing JRuby's scripts
	 */
	public String getScriptPath();
}
