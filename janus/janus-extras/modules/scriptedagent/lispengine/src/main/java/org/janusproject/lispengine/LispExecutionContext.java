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
package org.janusproject.lispengine;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.UUID;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.armedbear.lisp.Function;
import org.armedbear.lisp.JavaObject;
import org.armedbear.lisp.LispObject;
import org.armedbear.lisp.Symbol;
import org.armedbear.lisp.scripting.AbclScriptEngine;
import org.janusproject.scriptedagent.AbstractScriptExecutionContext;
import org.janusproject.scriptedagent.ScriptedAgent;

/**
 * This class generates a Common Lisp execution context.
 * It allows to run several scripts under several forms (path, commands, ...).
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class LispExecutionContext extends AbstractScriptExecutionContext {

	/**
	 * Name of the Common Lisp engine for Java script engine manager
	 */
	public static final String LISP_ENGINE_NAME = "cl"; //$NON-NLS-1$

	private static LispObject toLispObject(Object v) {
		// special case that is not supported by the Lisp interpreter because
		// common lisp has not a equivalent type for BigDecimal
		Object vv = v;
		if (v instanceof BigDecimal)
			vv = ((BigDecimal)v).doubleValue();
		return JavaObject.getInstance(vv, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toScriptSyntax(Object v) {
		return toLispObject(v).printObject();
	}

	private final org.armedbear.lisp.Package lispPackage;
	private final String packageName;
	private final String packageNameHeader;

	/**
	 * Default constructor.
	 * 
	 * @param scriptManager is the manager of script engines.
	 */
	public LispExecutionContext(ScriptEngineManager scriptManager) {
		super(
				scriptManager.getEngineByName(LISP_ENGINE_NAME));
		this.packageName = "janus-platform-scope-"+UUID.randomUUID().toString(); //$NON-NLS-1$
		this.packageNameHeader = "(in-package :"+this.packageName+") ";  //$NON-NLS-1$//$NON-NLS-2$
		try {
			this.lispPackage = (org.armedbear.lisp.Package)
					getScriptEngine().eval("(defpackage :"+ //$NON-NLS-1$
							this.packageName+
							" (:use :cl :ext :java :abcl-script :abcl-script-user))"); //$NON-NLS-1$
		}
		catch (ScriptException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Default constructor.
	 */
	public LispExecutionContext() {
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
		StringBuilder call = new StringBuilder();
		call.append('(');
		call.append(functionName.trim());
		for(int i=0; i<params.length; ++i) {
			call.append(' ');
			if (isSerializable(params[i])) {
				call.append(toScriptSyntax(params[i]));
			}
			else {
				String paramName = makeTempVariable();
				LispObject obj = toLispObject(params[i]);
				context.setAttribute(paramName, obj, ScriptContext.ENGINE_SCOPE);
				Symbol symbol = this.lispPackage.addInternalSymbol(paramName);
				symbol.setSymbolValue(obj);
				call.append(paramName);
			}
		}
		call.append(')');
		return call.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String makeMethodCall(Object instance, String functionName, Object... params) {
		assert(functionName!=null && !functionName.isEmpty());
		ScriptEngine engine = getScriptEngine();
		ScriptContext context = engine.getContext();
		StringBuilder call = new StringBuilder();
		call.append("(jcall "); //$NON-NLS-1$
		call.append(functionName.trim());
		call.append(' ');
		if (isSerializable(instance)) {
			call.append(toScriptSyntax(instance));
		}
		else {
			String paramName = makeTempVariable();
			LispObject obj = toLispObject(instance);
			context.setAttribute(paramName, obj, ScriptContext.ENGINE_SCOPE);
			Symbol symbol = this.lispPackage.addInternalSymbol(paramName);
			symbol.setSymbolValue(obj);
			call.append(paramName);
		}
		for(int i=0; i<params.length; ++i) {
			call.append(' ');
			if (isSerializable(params[i])) {
				call.append(toScriptSyntax(params[i]));
			}
			else {
				String paramName = makeTempVariable();
				LispObject obj = toLispObject(params[i]);
				context.setAttribute(paramName, obj, ScriptContext.ENGINE_SCOPE);
				Symbol symbol = this.lispPackage.addInternalSymbol(paramName);
				symbol.setSymbolValue(obj);
				call.append(paramName);
			}
		}
		call.append(')');
		return call.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getGlobalValue(String name) {
		return runCommand(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGlobalValue(String name, Object value) {
		runCommand(
				"(set "+ //$NON-NLS-1$
						name+
						" "+ //$NON-NLS-1$
						toScriptSyntax(value)+
				")"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object evaluate(ScriptEngine engine, Reader stream)
			throws ScriptException {
		PrefixedReader reader = new PrefixedReader(this.packageNameHeader, stream);
		try {
			return engine.eval(reader);
		}
		finally {
			try {
				reader.close();
			}
			catch(IOException e) {
				throw new ScriptException(e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object evaluate(ScriptEngine engine, String script)
			throws ScriptException {
		return engine.eval(this.packageNameHeader+script);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFunction(String functionName) {
		try {
			AbclScriptEngine engine = (AbclScriptEngine)getScriptEngine();
			Symbol s;
			if(functionName.indexOf(':') >= 0) {
				s = engine.findSymbol(functionName);
			} else {
				s = engine.findSymbol(functionName, this.packageName);
			}
			if(s != null) {
				LispObject f = s.getSymbolFunction();
				return (f != null && f instanceof Function);
			}
		}
		catch(Throwable _) {
			//
		}
		return false;
	}

	/**
	 * Reader that read the package prefix of the LispExecutionContext.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $Groupid$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class PrefixedReader extends Reader {

		private final String header;
		private final Reader reader;

		private int headerIndex = 0;

		public PrefixedReader(String prefix, Reader reader) {
			this.header = prefix;
			this.reader = reader;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			int rest = len;
			int offset = off;
			while (rest>0 && this.headerIndex<this.header.length()) {
				cbuf[offset] = this.header.charAt(this.headerIndex);
				++offset;
				--rest;
				++this.headerIndex;
			}
			return this.reader.read(cbuf, offset, rest);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
			this.reader.close();
		}

	}

}