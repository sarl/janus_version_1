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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.arakhne.vmutil.locale.Locale;
import org.jruby.embed.jsr223.JanusJRubyEngineFactory;

/**
 * This class generates a ruby execution context It allows to run several scripts under several forms (path, commands, ...)
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gvinson$
 * @author $Author: rbuecher$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class RubyExecutionScriptContext {

	/**
	 * File Extension of JRuby script files
	 */
	public static final String RubyFileExtension = ".rb"; //$NON-NLS-1$

	/**
	 * engine is a ScriptEngine use to execute ruby script
	 */
	private final ScriptEngine engine;

	/**
	 * Logger
	 */
	private final Logger logger = Logger.getLogger(this.getClass().toString());

	/**
	 * Default constructor
	 */
	public RubyExecutionScriptContext() {
		this.engine = new JanusJRubyEngineFactory().getScriptEngine();
	}

	/**
	 * 
	 * @return the JRuby execution engine
	 */
	public ScriptEngine getEngine() {
		return this.engine;
	}

	/**
	 * 
	 * @return the logger associated to this class
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * Function use to build a valid path merging the path of the annotation of the JRubyAgent and the script name
	 * 
	 * @param path
	 * @param scriptName
	 * @return
	 */
	private String validPath(String path, String scriptName) {
		String script = scriptName.trim();
		String goodPath = path.trim();
		File f = new File(script);

		if (f.isFile() && f.getName().endsWith(RubyFileExtension)) {
			return script;
		}

		if (!script.substring(script.length() - 3, script.length()).equals(RubyFileExtension)) {
			script += RubyFileExtension;
		}

		if (goodPath.charAt(goodPath.length() - 1) != '/' && goodPath.charAt(goodPath.length() - 1) != '\\') {
			goodPath += "/"; //$NON-NLS-1$
		}

		f = new File(goodPath + script);

		if (f.isFile()) {
			return goodPath + script;
		}

		this.logger.severe(Locale.getString(RubyExecutionScriptContext.class, "FILE_NOT_FOUND", goodPath + script)); //$NON-NLS-1$
		return null;
	}

	/**
	 * Evaluate a file script JRuby from a path, "puts" and ruby errors output by default
	 * 
	 * @param path
	 *            is the absolute pathname targeting the script file
	 * @return the default output in a String
	 */
	public String runScriptFromPath(String path) {

		String returnScriptStream = ""; //$NON-NLS-1$
		try {
			this.engine.eval(new BufferedReader(new FileReader(path)));
		} catch (FileNotFoundException e) {
			this.logger.severe(Locale.getString(RubyExecutionScriptContext.class, "FILE_NOT_FOUND", path)); //$NON-NLS-1$
		} catch (ScriptException e) {
			this.logger.severe(Locale.getString(RubyExecutionScriptContext.class, "SCRIPT_EXCEPTION", e.getMessage())); //$NON-NLS-1$
		}
		return returnScriptStream;
	}

	/**
	 * same as the runScriptFromPath(String path) adapted for the JRubyAgent annotation
	 * 
	 * @param path
	 * @param ScriptName
	 * @return the default output in a String
	 */
	public String runScriptFromPath(String path, String ScriptName) {
		return runScriptFromPath(validPath(path, ScriptName));
	}

	/**
	 * Evaluate a file script JRuby from a path, "puts" and ruby errors output are redirect in the StringWriters
	 * 
	 * @param path
	 *            - is the absolute pathname targeting the script file
	 * @param errorWriter
	 * @param putsWriter
	 * @return the default output in a String
	 */
	public String runScriptFromPath(String path, StringWriter errorWriter, StringWriter putsWriter) {
		this.engine.getContext().setErrorWriter(errorWriter);
		this.engine.getContext().setWriter(putsWriter);
		return runScriptFromPath(path);
	}

	/**
	 * same as the runScriptFromPath(String path, StringWriter errorWriter,StringWriter putsWriter) adapted for the JRubyAgent annotation
	 * 
	 * @param path
	 * @param scriptName
	 * @param errorWriter
	 * @param putsWriter
	 * @return the default output in a String
	 */
	public String runScriptFromPath(String path, String scriptName, StringWriter errorWriter, StringWriter putsWriter) {
		return runScriptFromPath(validPath(path, scriptName), errorWriter, putsWriter);
	}

	/**
	 * Run the result of the command in the context deployed with default output for "puts" and ruby errors
	 * 
	 * @param a_command
	 *            is the command which will be executed in the context
	 * @return the return of the script context for the command
	 */
	public String runRubyCommand(String a_command) {

		String returnScriptStream = ""; //$NON-NLS-1$
		try {
			this.engine.eval("$result=" + a_command); //$NON-NLS-1$
			Object o = this.engine.getContext().getAttribute("result"); //$NON-NLS-1$
			if (o != null) {
				String str = this.engine.getContext().getAttribute("result").toString(); //$NON-NLS-1$
				returnScriptStream = str;
			}
		} catch (ScriptException e) {
			this.logger.severe(Locale.getString(RubyExecutionScriptContext.class, "SCRIPT_EXCEPTION", e.getMessage())); //$NON-NLS-1$
		}
		return returnScriptStream;
	}

	/**
	 * Run the result of the command in the context deployed redirecting "puts" and ruby errors in the StringWriters
	 * 
	 * @param a_command
	 *            - is the command which will be executed in the context
	 * @param errorWriter
	 * @param putsWriter
	 * @return the return of the script context for the command
	 */
	public String runRubyCommand(String a_command, StringWriter errorWriter, StringWriter putsWriter) {
		this.engine.getContext().setErrorWriter(errorWriter);
		this.engine.getContext().setWriter(putsWriter);
		return runRubyCommand(a_command);
	}

	/**
	 * Execute the Ruby function specified in the script specified, taking parameters with default outputs for "puts" and ruby errors
	 * 
	 * @param scriptPath
	 * @param functionName
	 * @param params
	 * @return an object returned by the ruby function (can returning multiple object in a tab[])
	 */
	public Object runRubyFunction(String scriptPath, String functionName, Object... params) {
		Object res = null;
		String funcString = ""; //$NON-NLS-1$
		try {
			this.engine.eval(new BufferedReader(new FileReader(scriptPath)));
			if (functionName.indexOf("(") >= 0) { //$NON-NLS-1$
				funcString = "$c=" + functionName.substring(0, functionName.indexOf("(")).trim() + "("; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				funcString = "$c=" + functionName.trim() + "("; //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (params.length == 0)
				funcString += ")"; //$NON-NLS-1$
			else {
				for (int i = 0; i < params.length; i++) {
					this.engine.getContext().setAttribute("a" + i, params[i], ScriptContext.ENGINE_SCOPE); //$NON-NLS-1$
					funcString += "$a" + i + ","; //$NON-NLS-1$ //$NON-NLS-2$
				}
				funcString = funcString.substring(0, funcString.length() - 1) + ")"; //$NON-NLS-1$
			}
			this.engine.eval(funcString);
			res = this.engine.getContext().getAttribute("c"); //$NON-NLS-1$

		} catch (FileNotFoundException e) {
			this.logger.severe(Locale.getString(RubyExecutionScriptContext.class, "FILE_NOT_FOUND", scriptPath)); //$NON-NLS-1$
		} catch (ScriptException e) {
			this.logger.severe(Locale.getString(RubyExecutionScriptContext.class, "SCRIPT_EXCEPTION", e.getMessage())); //$NON-NLS-1$
		}
		return res;
	}

	/**
	 * Same as runRubyFunction(String scriptPath, String functionName,Object... params) adapted for the JRubyAgent annotation
	 * 
	 * @param path
	 * @param scriptName
	 * @param functionName
	 * @param params
	 * @return the result of the executed Ruby function
	 */
	public Object runRubyFunction(String path, String scriptName, String functionName, Object... params) {
		return runRubyFunction(validPath(path, scriptName), functionName, params);
	}

	/**
	 * Execute the Ruby function specified in the script specified, taking parameters redirecting outputs for "puts" and ruby errors in StringWriters
	 * 
	 * @param scriptPath
	 * @param functionName
	 * @param errorWriter
	 * @param putsWriter
	 * @param params
	 * @return an object returned by the ruby function (can returning multiple object in a tab[])
	 */
	public Object runRubyFunction(String scriptPath, String functionName, StringWriter errorWriter, StringWriter putsWriter, Object... params) {
		this.engine.getContext().setErrorWriter(errorWriter);
		this.engine.getContext().setWriter(putsWriter);
		return runRubyFunction(scriptPath, functionName, params);
	}

	/**
	 * Same as runRubyFunction(String scriptPath, String functionName, StringWriter errorWriter, StringWriter putsWriter,Object... params) adapted for the JRubyAgent annotation
	 * 
	 * @param path
	 * @param scriptName
	 * @param functionName
	 * @param errorWriter
	 * @param putsWriter
	 * @param params
	 * @return the result of the executed Ruby function
	 */
	public Object runRubyFunction(String path, String scriptName, String functionName, StringWriter errorWriter, StringWriter putsWriter, Object... params) {
		return runRubyFunction(validPath(path, scriptName), functionName, errorWriter, putsWriter, params);
	}

}
