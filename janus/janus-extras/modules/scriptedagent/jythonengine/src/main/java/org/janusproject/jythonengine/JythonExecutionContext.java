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
package org.janusproject.jythonengine;

import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.janusproject.scriptedagent.AbstractScriptExecutionContext;
import org.janusproject.scriptedagent.ScriptedAgent;
import org.python.core.Py;
import org.python.core.PyBaseString;
import org.python.core.PyObject;
import org.python.core.PyUnicode;
import org.python.jsr223.JanusJythonScriptEngine;

/**
 * This class generates a Jython execution context.
 * It allows to run several scripts under several forms (path, commands, ...).
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class JythonExecutionContext extends AbstractScriptExecutionContext {

	/**
	 * Default constructor.
	 * 
	 * @param scriptManager is the manager of script engines.
	 */
	public JythonExecutionContext(ScriptEngineManager scriptManager) {
		super(new JanusJythonScriptEngine());
	}

	/**
	 * Default constructor.
	 */
	public JythonExecutionContext() {
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
		PyObject obj = Py.java2py(v);
		String rawValue;
		if (obj instanceof PyBaseString) {
			if (obj instanceof PyUnicode) {
				rawValue = ((PyUnicode)obj).encode();
			}
			else {
				rawValue = obj.asString();
			}
			rawValue = "\""+rawValue.replaceAll("\"", "\\\\\"")+"\"";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		}
		else {
			rawValue = obj.__str__().toString();
		}

		return rawValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFunction(String functionName) {
		JanusJythonScriptEngine engine = (JanusJythonScriptEngine)getScriptEngine();
		return engine.getDeclaredFunction(functionName)!=null;
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
