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
import org.jruby.RubyNil;
import org.jruby.RubyString;
import org.jruby.embed.jsr223.JanusJRubyEngine;
import org.jruby.embed.jsr223.JanusJRubyEngineFactory;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.builtin.IRubyObject;

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
	 * Default constructor.
	 */
	public RubyExecutionContext() {
		super(
				getSharedScriptEngineManager().getScriptEngine());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAgentSeparationCompliant() {
		return true;
	}
	
	private String toRuby(Object v) {
		IRubyObject rObj = JavaUtil.convertJavaToUsableRubyObject(
				((JanusJRubyEngine)getScriptEngine()).getRuntime(),
				v);
		if (rObj instanceof RubyNil) return "nil"; //$NON-NLS-1$
		RubyString str = rObj.asString();
		String rawValue = str.toString();
		if (v instanceof CharSequence || v instanceof Character) {
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
				command.append(toRuby(params[i]));
			}
			else {
				String paramName = makeTempVariable();
				context.setAttribute(paramName, params[i], ScriptContext.ENGINE_SCOPE);
				command.append("$"+paramName); //$NON-NLS-1$
			}
		}
		command.append(')');
		return command.toString();
	}
	
}
