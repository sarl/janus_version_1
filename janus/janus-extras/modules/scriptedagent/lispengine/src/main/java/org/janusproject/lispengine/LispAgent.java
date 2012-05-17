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
package org.janusproject.lispengine;

import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.ScriptedAgent;

/**
 * Agent created to run Common Lisp commands and scripts.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class LispAgent extends ScriptedAgent {

	private static final long serialVersionUID = 2223144942117074910L;

	/**
	 * Creates a new LispAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 */
	public LispAgent(ScriptEngineManager scriptManager) {
		super(new LispExecutionContext(scriptManager));
	}
	
	/**
	 * Creates a new LispAgent. 
	 */
	public LispAgent() {
		this(getSharedScriptEngineManager());
	}

}
