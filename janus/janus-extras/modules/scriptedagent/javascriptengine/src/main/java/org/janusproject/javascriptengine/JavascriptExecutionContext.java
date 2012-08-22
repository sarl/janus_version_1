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
 * This class generates a Javascript execution context.
 * It allows to run several scripts under several forms (path, commands, ...).
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class JavascriptExecutionContext extends AbstractScriptExecutionContext {

	/**
	 * Name of the Javascript engine for Java script engine manager
	 */
	public static final String JAVASCRIPT_ENGINE_NAME = "javascript"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 * 
	 * @param scriptManager is the manager of script engines.
	 */
	public JavascriptExecutionContext(ScriptEngineManager scriptManager) {
		super(
			scriptManager.getEngineByName(JAVASCRIPT_ENGINE_NAME));
	}

	/**
	 * Default constructor.
	 */
	public JavascriptExecutionContext() {
		this(ScriptedAgent.getSharedScriptEngineManager());
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isAgentSeparationCompliant() {
		return true;
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isFunction(String functionName) {
		try {
			Object v = getScriptEngine().eval("typeof "+functionName); //$NON-NLS-1$
			return "function".equals(v); //$NON-NLS-1$
		}
		catch (ScriptException e) {
			//
		}
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public String toScriptSyntax(Object value) {
		if (value==null) return "null"; //$NON-NLS-1$
		if (value instanceof CharSequence || value instanceof Character) {
			String rawValue = value.toString();
			return "\""+rawValue.replaceAll("\"", "\\\\\"")+"\"";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		}
		return value.toString();
	}

	/** {@inheritDoc}
	 */
	@Override
	protected Object evaluate(ScriptEngine engine, Reader stream)
			throws ScriptException {
		return engine.eval(stream);
	}

	/** {@inheritDoc}
	 */
	@Override
	protected Object evaluate(ScriptEngine engine, String script)
			throws ScriptException {
		return engine.eval(script);
	}

	/** Run the function with the given name on the wrapped object.
	 * 
	 * @param functionName
	 * @param wrapped
	 * @param params the first element must be always <code>null</code>.
	 * @return the result of the execution.
	 */
	final Object runWrappedFunction(String functionName, Object wrapped, Object[] params) {
		params[0] = "___JANUS_KERNEL_TEMP_WRAP_OBJECT__"; //$NON-NLS-1$
		StringBuilder command = new StringBuilder();
		command.append("var ___JANUS_KERNEL_TEMP_WRAP_OBJECT__="); //$NON-NLS-1$
		command.append(makeFunctionCall("new JSAdapter", wrapped)); //$NON-NLS-1$
		command.append(";"); //$NON-NLS-1$
		command.append(makeFunctionCall(functionName, params));
		return runCommand(command.toString());
	}
	
}
