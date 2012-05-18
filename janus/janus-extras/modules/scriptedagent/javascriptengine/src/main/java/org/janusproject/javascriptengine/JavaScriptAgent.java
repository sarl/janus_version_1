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

import java.io.File;
import java.net.URL;

import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.ScriptExecutionContext;
import org.janusproject.scriptedagent.UnprotectedScriptedAgent;

/**
 * Agent created to run JavaScript commands and scripts.
 * <p>
 * To invoke protected methods from the agent, please use
 * {@link #invoke(String, Object...)}.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class JavaScriptAgent extends UnprotectedScriptedAgent {

	private static final long serialVersionUID = -5485603272382497156L;

	
	/**
	 * Creates a new JavaScriptAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 */
	public JavaScriptAgent(ScriptEngineManager scriptManager) {
		super(new JavaScriptExecutionContext(scriptManager));
	}

	/**
	 * Creates a new JavaScriptAgent. 
	 */
	public JavaScriptAgent() {
		this(getSharedScriptEngineManager());
	}

	/**
	 * Creates a new JavaScriptAgent and load the script at startup.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public JavaScriptAgent(ScriptEngineManager scriptManager, String scriptBasename) {
		super(new JavaScriptExecutionContext(scriptManager), scriptBasename);
	}

	/**
	 * Creates a new JavaScriptAgent and load the script at startup. 
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public JavaScriptAgent(String scriptBasename) {
		this(getSharedScriptEngineManager(), scriptBasename);
	}

	/**
	 * Creates a new JavaScriptAgent and load the script at startup.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public JavaScriptAgent(ScriptEngineManager scriptManager, File script) {
		super(new JavaScriptExecutionContext(scriptManager), script);
	}

	/**
	 * Creates a new JavaScriptAgent and load the script at startup. 
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public JavaScriptAgent(File script) {
		this(getSharedScriptEngineManager(), script);
	}

	/**
	 * Creates a new JavaScriptAgent and load the script at startup.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public JavaScriptAgent(ScriptEngineManager scriptManager, URL script) {
		super(new JavaScriptExecutionContext(scriptManager), script);
	}

	/**
	 * Creates a new JavaScriptAgent and load the script at startup. 
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public JavaScriptAgent(URL script) {
		this(getSharedScriptEngineManager(), script);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runAgentFunction(String name, Object... parameters) {
		ScriptExecutionContext executor = getScriptExecutionContext();
		String cmd;
		if (parameters==null || parameters.length==0) {
			cmd = executor.makeMethodCall(this, name, this);
		}
		else {
			Object[] params = new Object[parameters.length+1];
			System.arraycopy(parameters, 0, params, 1, parameters.length);
			params[0] = this;
			cmd = executor.makeMethodCall(this, name, params);
		}
		executor.runCommand(cmd);
	}
		
}
