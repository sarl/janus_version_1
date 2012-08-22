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

import java.io.File;
import java.net.URL;

import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.UnprotectedScriptedAgent;

/**
 * Agent created to run Common Lisp commands and scripts.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class LispAgent extends UnprotectedScriptedAgent<LispExecutionContext> {

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

	/**
	 * Creates a new LispAgent.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public LispAgent(ScriptEngineManager scriptManager, String scriptBasename) {
		super(new LispExecutionContext(scriptManager), scriptBasename);
	}
	
	/**
	 * Creates a new LispAgent. 
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public LispAgent(String scriptBasename) {
		this(getSharedScriptEngineManager(), scriptBasename);
	}

	/**
	 * Creates a new LispAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public LispAgent(ScriptEngineManager scriptManager, File script) {
		super(new LispExecutionContext(scriptManager), script);
	}
	
	/**
	 * Creates a new LispAgent. 
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public LispAgent(File script) {
		this(getSharedScriptEngineManager(), script);
	}

	/**
	 * Creates a new LispAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public LispAgent(ScriptEngineManager scriptManager, URL script) {
		super(new LispExecutionContext(scriptManager), script);
	}
	
	/**
	 * Creates a new LispAgent. 
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public LispAgent(URL script) {
		this(getSharedScriptEngineManager(), script);
	}

}
