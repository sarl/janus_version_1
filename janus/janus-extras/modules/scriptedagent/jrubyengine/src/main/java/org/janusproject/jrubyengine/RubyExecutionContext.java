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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.SoftReference;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.janusproject.scriptedagent.AbstractScriptExecutionContext;
import org.janusproject.scriptedagent.BlackHoleWriter;
import org.jruby.RubyMethod;
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
 * @version $FullVersion$
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
				sharedManager = new SoftReference<JanusJRubyEngineFactory>(manager);
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toScriptSyntax(Object v) {
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String makeFunctionCall(String functionName, Object... params) {
		assert(functionName!=null && !functionName.isEmpty());
		ScriptEngine engine = getScriptEngine();
		ScriptContext context = engine.getContext();
		StringBuilder command = new StringBuilder();
		command.append(functionName.trim());
		command.append('(');
		for(int i=0; i<params.length; ++i) {
			if (i>0) command.append(',');
			if (isSerializable(params[i])) {
				command.append(toScriptSyntax(params[i]));
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String makeMethodCall(Object objectInstance, String functionName,
			Object... params) {
		ScriptEngine engine = getScriptEngine();
		ScriptContext context = engine.getContext();

		// Translate the object instance
		String paramName;
		String instanceName;
		if (isSerializable(objectInstance)) {
			instanceName = toScriptSyntax(objectInstance);
		}
		else {
			paramName = makeTempVariable();
			context.setAttribute(paramName, objectInstance, ScriptContext.ENGINE_SCOPE);
			instanceName = "$"+paramName; //$NON-NLS-1$
		}
		
		// Translate the parameters
		String[] p = new String[params.length];
		for(int i=0; i<p.length; ++i) {
			if (isSerializable(params[i])) {
				p[i] = toScriptSyntax(params[i]);
			}
			else {
				paramName = makeTempVariable();
				context.setAttribute(paramName, params[i], ScriptContext.ENGINE_SCOPE);
				p[i] = "$"+paramName; //$NON-NLS-1$
			}
		}
		
		// Create the call
		return engine.getFactory().getMethodCallSyntax(instanceName, functionName, p);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("resource")
	@Override
	public boolean isFunction(String functionName) {
		Writer old = getStandardError();
		Writer w = new BlackHoleWriter();
		setStandardError(w);
		try {
			Object m = evaluate(getScriptEngine(), ":self.method( :"+functionName+" )");  //$NON-NLS-1$//$NON-NLS-2$
			return (m instanceof RubyMethod);
		}
		catch (Throwable _) {
			//
		}
		finally {
			setStandardError(old);

            try {
				if (old!=null) old.close();
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
