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
package org.janusproject.scriptedagent;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of a scripted agent that is
 * providing the means to invoke its protected
 * functions from a script that is not able to
 * invoke the functions with a protected scope.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class UnprotectedScriptedAgent extends ScriptedAgent {

	private static final long serialVersionUID = -5142920607712732405L;

	private static final Map<String, Collection<Method>> methodDeclarations = new TreeMap<String, Collection<Method>>();
	
	private static Collection<Method> getDeclaredMethods(String name, Class<?> type) {
		synchronized(methodDeclarations) {
			Collection<Method> methods = methodDeclarations.get(name);
			if (methods==null) {
				methods = new ArrayList<Method>();
				methodDeclarations.put(name, methods);

				Class<?> t = type;
				while (t!=null && !Object.class.equals(t)) {
					for(Method declaredMethod : t.getDeclaredMethods()) {
						if (declaredMethod.getName().equals(name)
							&&(Modifier.isProtected(declaredMethod.getModifiers())
							   ||Modifier.isPublic(declaredMethod.getModifiers()))) {
							methods.add(declaredMethod);
						}
					}
					t = t.getSuperclass();
				}
			}
			return methods;
		}
	}
	
	private static boolean mathesParameters(Method method, Object... parameters) {
		Class<?>[] types = method.getParameterTypes();
		if (types.length==parameters.length) {
			boolean success = true;
			for(int i=0; success && i<types.length; ++i) {
				success = types[i].isInstance(parameters[i]);
			}
			return success;
		}
		return false;
	}
	
	private static Method findMethod(Class<?> type, String name, Object... parameters) {
		synchronized(methodDeclarations) {
			Class<?> t = type;
			while (t!=null && !Object.class.equals(t)) {
				for(Method declaredMethod : t.getDeclaredMethods()) {
					if (declaredMethod.getName().equals(name)
						&&(Modifier.isProtected(declaredMethod.getModifiers())
						   ||Modifier.isPublic(declaredMethod.getModifiers()))
						&&mathesParameters(declaredMethod, parameters)) {
						Collection<Method> methods = methodDeclarations.get(name);
						if (methods==null) {
							methods = new ArrayList<Method>();
							methodDeclarations.put(name, methods);
						}
						methods.add(declaredMethod);
						return declaredMethod;
					}
				}
				t = t.getSuperclass();
			}
			return null;
		}
	}

	/**
	 * Creates a new scripted agent.
	 * 
	 * @param interpreter is the script interpreter to use.
	 */
	public UnprotectedScriptedAgent(ScriptExecutionContext interpreter) {
		super(interpreter);
	}

	/**
	 * Creates a new scripted agent and load the given script.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param interpreter is the script interpreter to use.
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public UnprotectedScriptedAgent(ScriptExecutionContext interpreter, String scriptBasename) {
		super(interpreter, scriptBasename);
	}

	/**
	 * Creates a new scripted agent and load the given script.
	 * 
	 * @param interpreter is the script interpreter to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public UnprotectedScriptedAgent(ScriptExecutionContext interpreter, File script) {
		super(interpreter, script);
	}

	/**
	 * Creates a new scripted agent and load the given script.
	 * 
	 * @param interpreter is the script interpreter to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public UnprotectedScriptedAgent(ScriptExecutionContext interpreter, URL script) {
		super(interpreter, script);
	}
	
	/** This function permits to invoke all the public and protected
	 * functions from this agent. This is mandatory because the JavaScript
	 * engine is only able to invoke the public methods.
	 * This method is assumed to be secure if these is not reference
	 * on the agent that is passed to other functions by the scripts.
	 *  
	 * @param name is the name of the method to invoke.
	 * @param parameters are the parameter to pass to.e invoked function.
	 * @return the value returns by the invoked method.
	 */
	public Object invoke(String name, Object[] parameters) {
		Collection<Method> methods = getDeclaredMethods(name, getClass());
		assert(methods!=null);
		
		for(Method method : methods) {
			if (mathesParameters(method, parameters)) {
				try {
					return method.invoke(this, parameters);
				}
				catch (Throwable e) {
					throw new ScriptRuntimeException(e);
				}
			}
		}
		
		Method method = findMethod(getClass(), name, parameters);
		if (method!=null) {
			try {
				return method.invoke(this, parameters);
			}
			catch (Throwable e) {
				throw new ScriptRuntimeException(e);
			}
		}
		
		throw new NoSuchMethodError(name);
	}

	/** This function permits to invoke all the public and protected
	 * functions from this agent. This is mandatory because the JavaScript
	 * engine is only able to invoke the public methods.
	 * This method is assumed to be secure if these is not reference
	 * on the agent that is passed to other functions by the scripts.
	 *  
	 * @param name is the name of the method to invoke.
	 * @return the value returns by the invoked method.
	 */
	public Object invoke(String name) {
		Collection<Method> methods = getDeclaredMethods(name, getClass());
		assert(methods!=null);
		
		for(Method method : methods) {
			if (method.getParameterTypes().length==0) {
				try {
					return method.invoke(this);
				}
				catch (Throwable e) {
					throw new ScriptRuntimeException(e);
				}
			}
		}
		
		Method method = findMethod(getClass(), name);
		if (method!=null) {
			try {
				return method.invoke(this);
			}
			catch (Throwable e) {
				throw new ScriptRuntimeException(e);
			}
		}
		
		throw new NoSuchMethodError(name);
	}

}
