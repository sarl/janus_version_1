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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;

import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.ScriptedAgent;
import org.python.core.Options;
import org.python.core.PyObject;
import org.python.core.PyType;

/**
 * Agent created to run Jython commands and scripts.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JythonAgent extends ScriptedAgent<JythonExecutionContext> {

	private static final long serialVersionUID = -1096878039754819565L;

	private static boolean isReset = false;
	
	private static void resetMemberAccess() {
		synchronized(JythonAgent.class) {
			if (!isReset) {
				isReset = true;
				Class<?> type = JythonAgent.class;
				// Force the Python interpreter to ignore the Java access rights
				Options.respectJavaAccessibility = false;
				
				// Revert the access right changes for the private members
				while (type!=null && !type.equals(Object.class)) {
					PyType ptype = PyType.fromClass(type);
					PyObject dictionary = ptype.fastGetDict();
					for(Method method : type.getDeclaredMethods()) {
						if (!Modifier.isPublic(method.getModifiers())
							&&!Modifier.isProtected(method.getModifiers())) {
							try {
								dictionary.__delitem__(method.getName());
							}
							catch(Throwable _) {
								// Silently pass the exception
							}
						}
					}
					for(Field field : type.getDeclaredFields()) {
						if (!Modifier.isPublic(field.getModifiers())
							&&!Modifier.isProtected(field.getModifiers())) {
							try {
								dictionary.__delitem__(field.getName());
							}
							catch(Throwable _) {
								// Silently pass the exception
							}
						}
					}
					type = type.getSuperclass();
				}
		
				// Restore the accessibility checking for the other classes than JythonAgent
				Options.respectJavaAccessibility = false;
			}
		}
	}
	
	/**
	 * Creates a new JythonAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 */
	public JythonAgent(ScriptEngineManager scriptManager) {
		super(new JythonExecutionContext(scriptManager));
		resetMemberAccess();
	}
	
	/**
	 * Creates a new JythonAgent. 
	 */
	public JythonAgent() {
		this(getSharedScriptEngineManager());
	}

	/**
	 * Creates a new JythonAgent.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public JythonAgent(ScriptEngineManager scriptManager, String scriptBasename) {
		super(new JythonExecutionContext(scriptManager), scriptBasename);
		resetMemberAccess();
	}
	
	/**
	 * Creates a new JythonAgent. 
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public JythonAgent(String scriptBasename) {
		this(getSharedScriptEngineManager(), scriptBasename);
	}

	/**
	 * Creates a new JythonAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public JythonAgent(ScriptEngineManager scriptManager, File script) {
		super(new JythonExecutionContext(scriptManager), script);
		resetMemberAccess();
	}
	
	/**
	 * Creates a new JythonAgent. 
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public JythonAgent(File script) {
		this(getSharedScriptEngineManager(), script);
	}

	/**
	 * Creates a new JythonAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public JythonAgent(ScriptEngineManager scriptManager, URL script) {
		super(new JythonExecutionContext(scriptManager), script);
		resetMemberAccess();
	}
	
	/**
	 * Creates a new JythonAgent. 
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public JythonAgent(URL script) {
		this(getSharedScriptEngineManager(), script);
	}

}
