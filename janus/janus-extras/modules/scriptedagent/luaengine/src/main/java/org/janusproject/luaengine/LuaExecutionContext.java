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

import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.janusproject.scriptedagent.AbstractScriptExecutionContext;
import org.janusproject.scriptedagent.ScriptedAgent;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.script.LuaScriptEngineFactory;

/**
 * This class generates a Lua execution context.
 * It allows to run several scripts under several forms (path, commands, ...).
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class LuaExecutionContext extends AbstractScriptExecutionContext {

	static {
		LuaScriptEngineFactory.setOneEnginePerThread(false);
	}
	
	/**
	 * Default constructor.
	 * 
	 * @param scriptManager is the manager of script engines.
	 */
	public LuaExecutionContext(ScriptEngineManager scriptManager) {
		super(new LuaScriptEngineFactory().getScriptEngine());
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
	public String toScriptSyntax(Object v) {
		LuaValue value = CoerceJavaToLua.coerce(v);
		String rawValue = value.toString();
		if (value instanceof LuaString) {
			rawValue = "\""+rawValue.replaceAll("\"", "\\\\\"")+"\"";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		}
		return rawValue;
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
	public boolean isFunction(String functionName) {
		String cmd = "if (" //$NON-NLS-1$
					+functionName
					+" ~= nil) then return true else return false end"; //$NON-NLS-1$
		try {
			Object o = evaluate(getScriptEngine(), cmd);
			if (o instanceof LuaBoolean) {
				LuaBoolean lb = (LuaBoolean)o;
				return lb.booleanValue();
			}
		}
		catch(Throwable _) {
			//
		}
		return false;
	}
	
	/** Replues the LUA function associated to the given closure.
	 * 
	 * @param functionName
	 * @return the LUA function or <code>null</code>
	 */
	public LuaFunction getFunction(String functionName) {
		String cmd = "return "+functionName; //$NON-NLS-1$
		try {
			Object o = evaluate(getScriptEngine(), cmd);
			if (o instanceof LuaFunction) {
				return (LuaFunction)o;
			}
		}
		catch(Throwable _) {
			//
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object evaluate(ScriptEngine engine, Reader stream)
			throws ScriptException {
		return engine.eval(stream);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object evaluate(ScriptEngine engine, String script)
			throws ScriptException {
		return engine.eval(script);
	}

}
