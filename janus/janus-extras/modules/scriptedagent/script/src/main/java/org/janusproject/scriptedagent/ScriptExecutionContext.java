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
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.logging.Logger;

import javax.script.ScriptEngine;

/**
 * Execution context for a script language.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public interface ScriptExecutionContext {

	/** Add listener on script errors.
	 * 
	 * @param listener
	 */
	public void addScriptErrorListener(ScriptErrorListener listener);
	
	/** Remove listener on script errors.
	 * 
	 * @param listener
	 */
	public void removeScriptErrorListener(ScriptErrorListener listener);

	/**
	 * Returns the underlying script engine.
	 *  
	 * @return the script execution engine.
	 */
	public ScriptEngine getScriptEngine();
	
	/**
	 * Returns the logger used by this context.
	 * 
	 * @return the logger associated to this class
	 */
	public Logger getLogger();
	
	/**
	 * Set the logger used by this context.
	 * 
	 * @param logger is the logger associated to this class
	 */
	public void setLogger(Logger logger);
	
	/** Replies the preferred file filter for the scripts.
	 * 
	 * @return the preferred file filter for the scripts.
	 */
	public ScriptFileFilter getPreferredFileFilter();
	
	/** Set the preferred file filter for the scripts.
	 * 
	 * @param fileFilter is the preferred file filter for the scripts.
	 */
	public void setPreferredFileFilter(ScriptFileFilter fileFilter);

	/** Set the script repository.
	 * 
	 * @param repository is the manager of script.
	 */
	public void setScriptRepository(ScriptRepository repository);

	/** Replies the script repository.
	 * 
	 * @return the repository
	 */
	public ScriptRepository getScriptRepository();
	
	/** Set the standard input to by used by the interpreter.
	 * 
	 * @param stdin
	 */
	public void setStandardInput(Reader stdin);

	/** Set the standard output to by used by the interpreter.
	 * 
	 * @param stdout
	 */
	public void setStandardOutput(Writer stdout);

	/** Set the standard error output to by used by the interpreter.
	 * 
	 * @param stderr
	 */
	public void setStandardError(Writer stderr);

	/** Replies the standard input to by used by the interpreter.
	 * 
	 * @return the standard reader.
	 */
	public Reader getStandardInput();

	/** Replies the standard output to by used by the interpreter.
	 * 
	 * @return the standard writer.
	 */
	public Writer getStandardOutput();

	/** Replies the standard error output to by used by the interpreter.
	 * 
	 * @return the error writer.
	 */
	public Writer getStandardError();

	/**
	 * Evaluate a script with the given basename in the
	 * directories managed by the script repository.
	 * 
	 * @param scriptBasename is the basename targeting the script file.
	 * @return the value returned by the script.
	 */
	public Object runScript(String scriptBasename);
	
	/**
	 * Evaluate a script in the given file.
	 * 
	 * @param scriptFilename
	 * @return the value returned by the script.
	 */
	public Object runScript(File scriptFilename);

	/**
	 * Evaluate a script in the given file.
	 * 
	 * @param scriptFilename
	 * @return the value returned by the script.
	 */
	public Object runScript(URL scriptFilename);

	/**
	 * Run the command in the context.
	 * 
	 * @param aCommand
	 *            is the command which will be executed in the context
	 * @return the returned value of the script context for the command
	 */
	public Object runCommand(String aCommand);

	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script. The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * This is similar to the calls to {@link #runScript(String)} and
	 * {@link #runCommand(String)} with the function call as command.
	 * 
	 * @param scriptBasename is the basename targeting the script file.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	public Object runFunction(String scriptBasename, String functionName, Object... params);
	
	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script.
	 * This is similar to the calls to {@link #runScript(File)} and
	 * {@link #runCommand(String)} with the function call as command.
	 * 
	 * @param scriptFilename is the filename of the script to load.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	public Object runFunction(File scriptFilename, String functionName, Object... params);

	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script.
	 * This is similar to the calls to {@link #runScript(URL)} and
	 * {@link #runCommand(String)} with the function call as command.
	 * 
	 * @param scriptFilename is the filename of the script to load.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	public Object runFunction(URL scriptFilename, String functionName, Object... params);
	
	/** Replies the value of a global variable.
	 * 
	 * @param name is the name of the variable.
	 * @return the value of the variable; <code>null</code> if undefined.
	 */
	public Object getGlobalValue(String name);

	/** Set the value of a global variable.
	 * 
	 * @param name is the name of the variable.
	 * @param value is the value of the variable; the interpretation
	 * of the <code>null</code> value depends on the underground interpreter.
	 */
	public void setGlobalValue(String name, Object value);

}
