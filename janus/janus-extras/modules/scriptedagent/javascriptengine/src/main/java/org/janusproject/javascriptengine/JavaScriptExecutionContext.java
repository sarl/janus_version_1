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

import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.janusproject.scriptedagent.AbstractScriptExecutionContext;
import org.janusproject.scriptedagent.ScriptedAgent;

/**
 * This class generates a JavaScript execution context.
 * It allows to run several scripts under several forms (path, commands, ...).
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class JavaScriptExecutionContext extends AbstractScriptExecutionContext {

	/**
	 * Name of the Javascript engine for Java script engine manager
	 */
	public static final String JAVASCRIPT_ENGINE_NAME = "javascript"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toScriptSyntax(Object v) {
		if (v==null) return "null"; //$NON-NLS-1$
		if (v instanceof CharSequence || v instanceof Character) {
			String rawValue = v.toString();
			return "\""+rawValue.replaceAll("\"", "\\\\\"")+"\"";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		}
		return v.toString();
	}

	/**
	 * Default constructor.
	 * 
	 * @param scriptManager is the manager of script engines.
	 */
	public JavaScriptExecutionContext(ScriptEngineManager scriptManager) {
		super(
			scriptManager.getEngineByName(JAVASCRIPT_ENGINE_NAME));
	}

	/**
	 * Default constructor.
	 */
	public JavaScriptExecutionContext() {
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
	public String makeMethodCall(Object objectInstance, String functionName,
			Object... params) {
		Object[] parameters = new Object[params.length+1];
		System.arraycopy(params, 0, parameters, 1, params.length);
		parameters[0] = objectInstance;
		return makeFunctionCall(functionName+".call", parameters); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFunction(String functionName) {
		try {
			Object v = evaluate(getScriptEngine(), "(typeof "+functionName+" == 'function')"); //$NON-NLS-1$ //$NON-NLS-2$
			if (v instanceof Boolean)
				return ((Boolean)v).booleanValue();
		}
		catch (ScriptException e) {
			//
		}
		return false;
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
