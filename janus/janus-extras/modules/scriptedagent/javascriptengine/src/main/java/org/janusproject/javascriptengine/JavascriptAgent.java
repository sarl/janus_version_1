/* 

 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;

import javax.script.ScriptEngineManager;

import org.arakhne.afc.vmutil.ReflectionUtil;
import org.janusproject.scriptedagent.ScriptedAgent;

import sun.org.mozilla.javascript.Context;
import sun.org.mozilla.javascript.Function;
import sun.org.mozilla.javascript.Scriptable;

/**
 * Agent created to run Javascript commands and scripts
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 * @since 1.0
 */
@SuppressWarnings("restriction")
public class JavascriptAgent extends ScriptedAgent<JavascriptExecutionContext> {

	private static final long serialVersionUID = 1625234838339209226L;

	/**
	 * @param modifiers
	 * @param originalType
	 * @param currentType
	 * @return the scope validity
	 */
	static boolean isValidScope(int modifiers, Class<?> originalType, Class<?> currentType) {
		if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers))
			return true;
		if (Modifier.isPrivate(modifiers)) {
			return originalType.equals(currentType);
		}
		return originalType.getPackage().equals(currentType.getPackage());
	}

	/**
	 * Creates a new JavascriptAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 */
	public JavascriptAgent(ScriptEngineManager scriptManager) {
		super(new JavascriptExecutionContext(scriptManager));
	}

	/**
	 * Creates a new JavascriptAgent. 
	 */
	public JavascriptAgent() {
		this(getSharedScriptEngineManager());
	}

	/**
	 * Creates a new JavascriptAgent and load the script at startup.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public JavascriptAgent(ScriptEngineManager scriptManager, String scriptBasename) {
		super(new JavascriptExecutionContext(scriptManager), scriptBasename);
	}

	/**
	 * Creates a new JavascriptAgent and load the script at startup.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public JavascriptAgent(String scriptBasename) {
		this(getSharedScriptEngineManager(), scriptBasename);
	}

	/**
	 * Creates a new JavascriptAgent and load the script at startup.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public JavascriptAgent(ScriptEngineManager scriptManager, File script) {
		super(new JavascriptExecutionContext(scriptManager), script);
	}

	/**
	 * Creates a new JavascriptAgent and load the script at startup.
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public JavascriptAgent(File script) {
		this(getSharedScriptEngineManager(), script);
	}

	/**
	 * Creates a new JavascriptAgent and load the script at startup.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public JavascriptAgent(ScriptEngineManager scriptManager, URL script) {
		super(new JavascriptExecutionContext(scriptManager), script);
	}

	/**
	 * Creates a new JavascriptAgent and load the script at startup.
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public JavascriptAgent(URL script) {
		this(getSharedScriptEngineManager(), script);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	protected void runAgentFunction(String name, Object... parameters) {
		JavascriptExecutionContext interpreter = getScriptExecutionContext();
		JSAgentWrapper wrapper = new JSAgentWrapper();

		if (parameters==null || parameters.length==0) {
			interpreter.runWrappedFunction(name, wrapper, new Object[1]);
		}
		else {
			Object[] params = new Object[parameters.length+1];
			System.arraycopy(parameters, 0, params, 1, parameters.length);
			interpreter.runWrappedFunction(name, wrapper, params);
		}
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 * @since 1.0
	 */
	public final class JSAgentWrapper {
		
		/**
		 */
		public JSAgentWrapper() {
			//
		}
		
		/** Replies the property with the given name.
		 * <p>
		 * In JS, this function is invoked on x in:
		 * <code>name in x</code>
		 * 
		 * @param name is the name of the property to search for.
		 * @return the property value or <code>null</code> if no property.
		 */
		public Object __get__(String name) {
			Class<?> original = JavascriptAgent.this.getClass();
			Class<?> type = original;
			do {
				try {
					Field f = type.getDeclaredField(name);
					if (isValidScope(f.getModifiers(), original, type)) {
						f.setAccessible(true);
						Object nativeValue = f.get(JavascriptAgent.this);
						return nativeValue;
					}
				}
				catch(Exception _) {
					//
				}
				try {
					for(Method m : type.getDeclaredMethods()) {
						if (name.equalsIgnoreCase(m.getName()) &&
							isValidScope(m.getModifiers(), original, type)) {
							return new JSMethodWrapper(name, type);
						}
					}
				}
				catch(Exception _) {
					//
				}
				type = type.getSuperclass();
			}
			while (!Object.class.equals(type));
			throw new NoSuchFieldError();
		}
		
		/** Replies if the object has a property with the given name.
		 * <p>
		 * In JS, this function is invoked on x in:
		 * <code>name in x</code>
		 * 
		 * @param name is the name of the property to search for.
		 * @return <code>true</code> if the property exists, otherwise
		 * <code>false</code>.
		 */
		public boolean __has__(String name) {
			Class<?> original = JavascriptAgent.this.getClass();
			Class<?> type = original;
			do {
				try {
					Field f = type.getDeclaredField(name);
					return isValidScope(f.getModifiers(), original, type); 
				}
				catch(Exception _) {
					//
				}
				try {
					for(Method m : type.getDeclaredMethods()) {
						if (name.equalsIgnoreCase(m.getName()) &&
							isValidScope(m.getModifiers(), original, type)) {
							return true;
						}
					}
					throw new IllegalAccessException();
				}
				catch(Exception _) {
					//
				}
				type = type.getSuperclass();
			}
			while (!Object.class.equals(type));
			return false;
		}

		/** Add an element in the object.
		 * <p>
		 * In JS, this function is invoked on x in:
		 * <code>x.name = value</code>
		 * 
		 * @param name is the name of the property to set.
		 * @param value is the value of the property to set.
		 */
		public void __put__(String name, Object value) {
			Class<?> original = JavascriptAgent.this.getClass();
			Class<?> type = original;
			do {
				try {
					Field f = type.getDeclaredField(name);
					if (isValidScope(f.getModifiers(), original, type)) {
						f.setAccessible(true);
						f.set(JavascriptAgent.this,
								Context.jsToJava(value, f.getType()));
						return;
					}
				}
				catch(Exception _) {
					//
				}
				type = type.getSuperclass();
			}
			while (!Object.class.equals(type));
			throw new NoSuchFieldError();
		}

		/** Delete an element of the object.
		 * <p>
		 * In JS, this function is invoked on x in:
		 * <code>delete x.name</code>
		 * 
		 * @param name is the name of the property to delete.
		 */
		public void __delete__(String name) {
			//
		}

		/** Replies the elements of the agent.
		 * <p>
		 * In JS, this function is invoked on x in:
		 * <code>for (i in x) { print(i); }</code>
		 * 
		 * @return empty array
		 */
		public Object[] __getIds__() {
			return new Object[0];
		}

	}
		
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 * @since 1.0
	 */
	private final class JSMethodWrapper implements Function {

		private final String functionName;
		private final Class<?> lowestType;
		
		/**
		 * @param functionName
		 * @param lowestType
		 */
		public JSMethodWrapper(String functionName, Class<?> lowestType) {
			this.functionName = functionName;
			this.lowestType = lowestType;
		}

		/** {@inheritDoc}
		 */
		@Override
		public String getClassName() {
			return getClass().getName();
		}
		

		/** {@inheritDoc}
		 */
		@Override
		public void delete(String name) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public void delete(int index) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public Object get(String name, Scriptable s) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public Object get(int index, Scriptable s) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public Object getDefaultValue(Class<?> type) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public Object[] getIds() {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public Scriptable getParentScope() {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public Scriptable getPrototype() {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public boolean has(String arg0, Scriptable arg1) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public boolean has(int arg0, Scriptable arg1) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public boolean hasInstance(Scriptable arg0) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public void put(String arg0, Scriptable arg1, Object arg2) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public void put(int arg0, Scriptable arg1, Object arg2) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public void setParentScope(Scriptable arg0) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public void setPrototype(Scriptable arg0) {
			throw new UnsupportedOperationException();
		}

		/** {@inheritDoc}
		 */
		@Override
		public Object call(Context context, Scriptable scope, Scriptable thisObj,
				Object[] args) {
			Class<?> original = JavascriptAgent.this.getClass();
			Class<?> type = this.lowestType;
			do {
				for(Method m : type.getDeclaredMethods()) {
					if (this.functionName.equalsIgnoreCase(m.getName())
						&& isValidScope(m.getModifiers(), original, type)) {
						if (ReflectionUtil.matchesParameters(m, args)) {
							try {
								m.setAccessible(true);
								Object value = m.invoke(JavascriptAgent.this, args);
								return value;
							}
							catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
						else if (ReflectionUtil.matchesParameters(m, (Object)args)) {
							try {
								m.setAccessible(true);
								Object value = m.invoke(JavascriptAgent.this, (Object)args);
								return value;
							}
							catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
				type = type.getSuperclass();
			}
			while (!Object.class.equals(type));
			throw new NoSuchMethodError(this.functionName);
		}

		/** {@inheritDoc}
		 */
		@Override
		public Scriptable construct(Context arg0, Scriptable arg1, Object[] arg2) {
			throw new UnsupportedOperationException();
		}

	}
	
}