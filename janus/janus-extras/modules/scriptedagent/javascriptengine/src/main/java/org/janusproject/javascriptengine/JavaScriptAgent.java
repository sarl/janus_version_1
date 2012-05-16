/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.javascriptengine;

import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.ScriptedAgent;

/**
 * Agent created to run JavaScript commands and scripts.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class JavaScriptAgent extends ScriptedAgent {

	private static final long serialVersionUID = -5485603272382497156L;

	/**
	 * Creates a new JythonAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 */
	public JavaScriptAgent(ScriptEngineManager scriptManager) {
		super(new JavaScriptExecutionContext(scriptManager));
	}
	
	/**
	 * Creates a new JythonAgent. 
	 */
	public JavaScriptAgent() {
		this(getSharedScriptEngineManager());
	}

}
