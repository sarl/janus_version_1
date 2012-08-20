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
package org.janusproject.luaengine;

import java.io.File;
import java.net.URL;

import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.ScriptExecutionContext;
import org.janusproject.scriptedagent.ScriptedAgent;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.LuaJavaClassUtil;

/**
 * Agent created to run LUA commands and scripts.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class LuaAgent extends ScriptedAgent<LuaExecutionContext> {

	private static final long serialVersionUID = -1096878039754819565L;

	private static void initializeLuaContext(Class<?> type) {
		LuaValue luaClass = CoerceJavaToLua.coerce(type);
		LuaJavaClassUtil.loadAgentMembers(luaClass);
	}
	
	/**
	 * Creates a new LuaAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 */
	public LuaAgent(ScriptEngineManager scriptManager) {
		super(new LuaExecutionContext(scriptManager));
		initializeLuaContext(getClass());
	}
	
	/**
	 * Creates a new LuaAgent. 
	 */
	public LuaAgent() {
		this(getSharedScriptEngineManager());
	}

	/**
	 * Creates a new LuaAgent.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public LuaAgent(ScriptEngineManager scriptManager, String scriptBasename) {
		super(new LuaExecutionContext(scriptManager), scriptBasename);
		initializeLuaContext(getClass());
	}
	
	/**
	 * Creates a new LuaAgent. 
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public LuaAgent(String scriptBasename) {
		this(getSharedScriptEngineManager(), scriptBasename);
	}

	/**
	 * Creates a new LuaAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public LuaAgent(ScriptEngineManager scriptManager, File script) {
		super(new LuaExecutionContext(scriptManager), script);
		initializeLuaContext(getClass());
	}
	
	/**
	 * Creates a new LuaAgent. 
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public LuaAgent(File script) {
		this(getSharedScriptEngineManager(), script);
	}

	/**
	 * Creates a new LuaAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public LuaAgent(ScriptEngineManager scriptManager, URL script) {
		super(new LuaExecutionContext(scriptManager), script);
		initializeLuaContext(getClass());
	}
	
	/**
	 * Creates a new LuaAgent. 
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public LuaAgent(URL script) {
		this(getSharedScriptEngineManager(), script);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runAgentFunction(String name, Object... parameters) {
		ScriptExecutionContext context = getScriptExecutionContext();
		String cmd;
		if (parameters==null || parameters.length==0) {
			cmd = context.makeMethodCall(this, name, this);
		}
		else {
			Object[] params = new Object[parameters.length+1];
			System.arraycopy(parameters, 0, params, 1, parameters.length);
			params[0] = this;
			cmd = context.makeMethodCall(this, name, params);
		}
		context.runCommand(cmd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preScriptActivation() {
		LuaExecutionContext luaContext = getScriptExecutionContext();
		LuaJavaClassUtil.addAgentFunction(
				getClass(),
				ACTIVATE_SCRIPT_FUNCTION,
				luaContext.getFunction(ACTIVATE_SCRIPT_FUNCTION));
		LuaJavaClassUtil.addAgentFunction(
				getClass(),
				LIVE_SCRIPT_FUNCTION,
				luaContext.getFunction(LIVE_SCRIPT_FUNCTION));
		LuaJavaClassUtil.addAgentFunction(
				getClass(),
				END_SCRIPT_FUNCTION,
				luaContext.getFunction(END_SCRIPT_FUNCTION));
	}
	
}
