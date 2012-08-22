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
package org.python.jsr223;

import java.lang.reflect.Field;

import org.python.core.PyObject;
import org.python.jsr223.PyScriptEngine;
import org.python.util.PythonInterpreter;

/**
 * Janus implementation of the Jython script engine to
 * enable "hidden" features.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JanusJythonScriptEngine extends PyScriptEngine {

	private final PythonInterpreter interpreter;
	
	/**
	 */
	public JanusJythonScriptEngine() {
		super(new PyScriptEngineFactory());
		try {
			Field field = PyScriptEngine.class.getDeclaredField("interp"); //$NON-NLS-1$
			field.setAccessible(true);
			this.interpreter = (PythonInterpreter)field.get(this);
			field.setAccessible(false);
		}
		catch (Throwable e) {
			throw new Error(e);
		}
	}

	/** Replies the declared function with the specified name
	 * 
	 * @param functionName
	 * @return the Jython function or <code>null</code>.
	 */
	public PyObject getDeclaredFunction(String functionName) {
		try {
			this.interpreter.setLocals(new PyScriptEngineScope(this, getContext()));
			return this.interpreter.get(functionName);
		}
		catch(Throwable _) {
			return null;
		}
	}

}
