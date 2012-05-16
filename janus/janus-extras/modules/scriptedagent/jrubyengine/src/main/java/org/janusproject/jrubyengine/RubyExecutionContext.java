/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.jrubyengine;

import java.lang.ref.SoftReference;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.janusproject.scriptedagent.AbstractScriptExecutionContext;
import org.jruby.embed.jsr223.JanusJRubyEngineFactory;

/**
 * This class generates a Groovy execution context.
 * It allows to run several scripts under several forms (path, commands, ...).
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gvinson$
 * @author $Author: rbuecher$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class RubyExecutionContext extends AbstractScriptExecutionContext {

	private static SoftReference<JanusJRubyEngineFactory> sharedManager = null;
	
	/** Replies the manager of script engines that may be shared between all the
	 * scripts agents.
	 * @return the manager of script engines.
	 */
	public static JanusJRubyEngineFactory getSharedScriptEngineManager() {
		synchronized(RubyExecutionContext.class) {
			JanusJRubyEngineFactory manager = (sharedManager==null) ? null : sharedManager.get();
			if (manager==null) {
				manager = new JanusJRubyEngineFactory();
				sharedManager = new SoftReference<>(manager);
			}
			return manager;
		}
	}

	/**
	 * Prefix used to build the name of temporary variables.
	 * It is recommanded that the scripts must not contains any
	 * variable with this prefix.
	 */
	public static final String TEMP_VARIABLE_PREFIX = "janus_ruby_private_temp_var"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 */
	public RubyExecutionContext() {
		super(
				new RubyFileFilter(false),
				getSharedScriptEngineManager().getScriptEngine());
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
				command.append("$"+paramName); //$NON-NLS-1$
			}
		}
		command.append(')');
		return command.toString();
	}
	
}
