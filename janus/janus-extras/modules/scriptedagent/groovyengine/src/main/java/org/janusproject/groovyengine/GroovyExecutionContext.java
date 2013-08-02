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

import groovy.lang.GroovyClassLoader;

import java.io.Reader;
import java.lang.reflect.Method;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
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
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroovyExecutionContext extends AbstractScriptExecutionContext {

	/**
	 * Name of the groovy engine for Java script engine manager
	 */
	public static final String GROOVY_ENGINE_NAME = "groovy"; //$NON-NLS-1$

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
	public String toScriptSyntax(Object v) {
		if (v==null) return "null"; //$NON-NLS-1$
		if (v instanceof CharSequence || v instanceof Character) {
			String rawValue = v.toString();
			return "\""+rawValue.replaceAll("\"", "\\\\\"")+"\"";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		}
		return v.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFunction(String functionName) {
		GroovyScriptEngineImpl engine = (GroovyScriptEngineImpl)getScriptEngine();
        GroovyClassLoader classLoader = engine.getClassLoader();
        String name;
        for(Class<?> type : classLoader.getLoadedClasses()) {
                name = type.getName();
                // The Groovy engine is naming the global classes with the prefix "Script"
                if (name.startsWith("Script")) { //$NON-NLS-1$
                        try {
                                for(Method method : type.getDeclaredMethods()) {
                                        if (functionName.equals(method.getName()))
                                                return true;
                                }
                        }
                        catch (Throwable e) {
                                //
                        }
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
