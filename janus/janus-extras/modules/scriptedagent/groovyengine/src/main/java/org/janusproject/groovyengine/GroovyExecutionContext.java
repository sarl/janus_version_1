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
package org.janusproject.groovyengine;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.AbstractScriptExecutionContext;
import org.janusproject.scriptedagent.ScriptedAgent;

/**
 * This class generates a Groovy execution context.
 * It allows to run several scripts under several forms (path, commands, ...).
 * 
 * @author $Author: sgalland$
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroovyExecutionContext extends AbstractScriptExecutionContext {

	/**
	 * Name of the groovy engine for Java script engine manager
	 */
	public static final String GROOVY_ENGINE_NAME = "groovy"; //$NON-NLS-1$

	private static String toGroovy(Object v) {
		if (v==null) return "null"; //$NON-NLS-1$
		if (v instanceof CharSequence || v instanceof Character) {
			String rawValue = v.toString();
			return "\""+rawValue.replaceAll("\"", "\\\\\"")+"\"";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		}
		return v.toString();
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
	public GroovyExecutionContext(ScriptEngineManager scriptManager) {
		super(
			scriptManager.getEngineByName(GROOVY_ENGINE_NAME));
	}

	/**
	 * Default constructor.
	 */
	public GroovyExecutionContext() {
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
		if (params!=null && params.length>0) {
			String paramName;
			for(int i=0; i<params.length; ++i) {
				if (i>0) command.append(',');
				if (isSerializable(params[i])) {
					command.append(toGroovy(params[i]));
				}
				else {
					paramName = makeTempVariable();
					context.setAttribute(paramName, params[i], ScriptContext.ENGINE_SCOPE);
					command.append(paramName);
				}
			}
		}
		
		command.append(')');
		return command.toString();
	}
	
}
