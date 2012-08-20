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
import javax.script.ScriptEngineFactory;

/**
 * Execution context for a script language.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public interface ScriptExecutionContext {

	/** Bind this context to the given agent.
	 * By default this method does nothing.
	 * It is here to be overridden by the subclasses.
	 * 
	 * @param agent
	 */
	public void bindTo(ScriptedAgent<?> agent);

	/** Replies if the interpreter supported by this script execution context
	 * is compliant with the agent-separation concern.
	 * <p>
	 * If the interpreter is compliant with the agent-separation convern; it
	 * means that each agent has its own memory context in the interpreter.
	 * <p>
	 * If the interpreter is not compliant with the agent-separation convern; it
	 * means that all the agents have the same global memory context in the interpreter.
	 * 
	 * @return <code>true</code> if each agent has its own memory context;
	 * <code>false</code> if all the agents have the same memory context.
	 */
	public boolean isAgentSeparationCompliant();
	
	/** Replies the name of the language supported by this execution context.
	 * 
	 * @return the name of the language.
	 */
	public String getLanguageName();
	
	/** Replies the version of the language supported by this execution context.
	 * 
	 * @return the version of the language.
	 */
	public String getLanguageVersion();
	
	/** Replies a file filter that is matching any file that has the extension
	 * associated to the supported language.
	 * 
	 * @param allowDirectories indicates if the accepting function is accepting
	 * the directories or not. 
	 * @return the file filter.
	 */
	public ScriptFileFilter getFileFilter(boolean allowDirectories);
	
	/** Add listener on script errors.
	 * If a listener on errors is existing in this {@link ScriptExecutionContext},
	 * then the {@link ScriptExecutionContext} does not log nor throw
	 * the error.
	 * 
	 * @param listener
	 * @see #removeScriptErrorListener(ScriptErrorListener)
	 * @see #setCatchAllExceptions(boolean)
	 * @see #isCatchAllExceptions()
	 */
	public void addScriptErrorListener(ScriptErrorListener listener);
	
	/** Remove listener on script errors.
	 * If a listener on errors is existing in this {@link ScriptExecutionContext},
	 * then the {@link ScriptExecutionContext} does not log nor throw
	 * the error.
	 * 
	 * @param listener
	 * @see #addScriptErrorListener(ScriptErrorListener)
	 * @see #setCatchAllExceptions(boolean)
	 * @see #isCatchAllExceptions()
	 */
	public void removeScriptErrorListener(ScriptErrorListener listener);
	
	/** Indicates if the errors in the scripts may be catched
	 * by the {@link ScriptExecutionContext} and never thrown outside.
	 * If a listener on errors is existing in this {@link ScriptExecutionContext},
	 * then the {@link ScriptExecutionContext} does not log nor throw
	 * the error.
	 * 
	 * @param catchAll is <code>true</code> to force the context to catch
	 * all the exceptions. It is <code>false</code> to enable the exceptions
	 * to be thrown outside the context.
	 * @see #addScriptErrorListener(ScriptErrorListener)
	 * @see #removeScriptErrorListener(ScriptErrorListener)
	 * @see #isCatchAllExceptions()
	 */
	public void setCatchAllExceptions(boolean catchAll);

	/** Indicates if the errors in the scripts may be catched
	 * by the {@link ScriptExecutionContext} and never thrown outside.
	 * If a listener on errors is existing in this {@link ScriptExecutionContext},
	 * then the {@link ScriptExecutionContext} does not log nor throw
	 * the error.
	 * 
	 * @return <code>true</code> if the context is catching
	 * all the exceptions; <code>false</code> if the exceptions
	 * are thrown outside the context.
	 * @see #addScriptErrorListener(ScriptErrorListener)
	 * @see #removeScriptErrorListener(ScriptErrorListener)
	 * @see #isCatchAllExceptions()
	 */
	public boolean isCatchAllExceptions();

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
	 * Find a script with the given basename in the
	 * directories managed by the script repository.
	 * 
	 * @param scriptBasename is the basename targeting the script file.
	 * @return the URL of the script or <code>null</code> if the script
	 * was not found.
	 */
	public URL findScript(String scriptBasename);

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
	 * Evaluate a script in the specified stream.
	 * 
	 * @param stream
	 * @return the value returned by the script.
	 */
	public Object runScript(Reader stream);

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
	
	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in an already loaded script.
	 * This function is similar to a call to {@link #runCommand(String)}
	 * with the result of {@link #makeFunctionCall(String, Object...)}
	 * as parameter.
	 * 
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	public Object runFunction(String functionName, Object... params);

	/** Create and replies the call to the function with the specified
	 * name and the specified parameters.
	 * This method is similar to but does not invokes
	 * {@link ScriptEngineFactory#getMethodCallSyntax(String, String, String...)}.
	 * 
	 * @param functionName is the name of the function to call.
	 * @param params are the parameters to pass to the function.
	 * @return the script command.
	 */
	public String makeFunctionCall(String functionName, Object... params);

	/** Replies if a function with the specified name is defined in the script engine.
	 * 
	 * @param functionName is the name of the function to search for.
	 * @return <code>true</code> if the function was defined; <code>false</code> if not.
	 */
	public boolean isFunction(String functionName);

	/** Create and replies the call to the method on the given object
	 * instance and the specified parameters.
	 * This method invokes {@link ScriptEngineFactory#getMethodCallSyntax(String, String, String...)}
	 * with the proper parameters.
	 * 
	 * @param objectInstance is the object to call on.
	 * @param functionName is the name of the function to call.
	 * @param params are the parameters to pass to the function.
	 * @return the script command.
	 */
	public String makeMethodCall(Object objectInstance, String functionName, Object... params);

	/** Translate and reply a string representation of the specified value
	 * that is suitable to be put inside the script source code.
	 * 
	 * @param value is the value to translation
	 * @return the string representation of the value.
	 */
	public String toScriptSyntax(Object value);

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
