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

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.AbstractScriptExecutionContext;
import org.janusproject.scriptedagent.ScriptedAgent;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.script.LuaScriptEngineFactory;

/**
 * This class generates a Lua execution context.
 * It allows to run several scripts under several forms (path, commands, ...).
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class LuaExecutionContext extends AbstractScriptExecutionContext {

	static {
		LuaScriptEngineFactory.setOneEnginePerThread(false);
	}
	
	/**
	 * Name of the lua engine for Java script engine manager
	 */
	public static final String LUA_ENGINE_NAME = "lua"; //$NON-NLS-1$

	private static String toLua(Object v) {
		LuaValue value = CoerceJavaToLua.coerce(v);
		String rawValue = value.toString();
		if (value instanceof LuaString) {
			rawValue = "\""+rawValue.replaceAll("\"", "\\\\\"")+"\"";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		}
		return rawValue;
	}

	private static boolean isSerializable(Object v) {
		return (v==null)
				|| (v instanceof Number)
				|| (v instanceof CharSequence)
				|| (v instanceof Boolean)
				|| (v instanceof Character);
	}

	/**
	 * Default constructor.
	 * 
	 * @param scriptManager is the manager of script engines.
	 */
	public LuaExecutionContext(ScriptEngineManager scriptManager) {
		super(new LuaScriptEngineFactory().getScriptEngine());
				//scriptManager.getEngineByName(LUA_ENGINE_NAME));
	}

	/**
	 * Default constructor.
	 */
	public LuaExecutionContext() {
		this(ScriptedAgent.getSharedScriptEngineManager());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAgentSeparationCompliant() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String makeFunctionCall(String functionName, Object... params) {
		assert(functionName!=null && !functionName.isEmpty());
		ScriptEngine engine = getScriptEngine();
		ScriptContext context = engine.getContext();
		StringBuilder command = new StringBuilder();
		int parenthesis = functionName.indexOf('(');
		if (parenthesis>=0) {
			command.append(functionName.substring(0, parenthesis).trim());
		}
		else {
			command.append(functionName.trim());
		}
		command.append('(');
		for(int i=0; i<params.length; ++i) {
			if (i>0) command.append(',');
			if (isSerializable(params[i])) {
				command.append(toLua(params[i]));
			}
			else {
				String paramName = makeTempVariable();
				context.setAttribute(paramName, params[i], ScriptContext.ENGINE_SCOPE);
				command.append(paramName);
			}
		}
		command.append(')');
		return command.toString();
	}
		
}
