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

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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
	 * Prefix used to build the name of temporary variables.
	 * It is recommanded that the scripts must not contains any
	 * variable with this prefix.
	 */
	public static final String TEMP_VARIABLE_PREFIX = "__janus_javascript_private_temp_var__"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 * 
	 * @param scriptManager is the manager of script engines.
	 */
	public JavaScriptExecutionContext(ScriptEngineManager scriptManager) {
		super(
			new JavaScriptFileFilter(false),
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
		if (params!=null && params.length>0) {
			String paramName;
			for(int i=0; i<params.length; ++i) {
				if (i>0) command.append(',');
				paramName = makeTempVariable(TEMP_VARIABLE_PREFIX, null);
				context.setAttribute(paramName, params[i], ScriptContext.ENGINE_SCOPE);
				command.append(paramName);
			}
		}
		command.append(')');
		return command.toString();
	}
	
}
