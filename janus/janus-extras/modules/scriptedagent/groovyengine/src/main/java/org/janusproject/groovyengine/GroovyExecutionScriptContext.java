package org.janusproject.groovyengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.arakhne.vmutil.locale.Locale;

/**
 * This class generates a Groovy execution context It allows to run several scripts under several forms (path, commands, ...)
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroovyExecutionScriptContext {

	/**
	 * File Extension of Groovy script files
	 */
	public static final String GROOVY_FILE_EXTENSION = ".gy"; //$NON-NLS-1$
	
	/**
	 * Name of the groovy engine for Java script engine manager
	 */
	public static final String GROOVY_ENGINE_NAME = "groovy"; //$NON-NLS-1$

	/**
	 * Key of File Not Found error message in properties file
	 */
	private static final String FILE_NOT_FOUND_PROPERTY_KEY = "FILE_NOT_FOUND"; //$NON-NLS-1$
	
	/**
	 * Key of Script Exception error message in properties file
	 */
	private static final String	SCRIPT_EXCEPTION_PROPERTY_KEY = "SCRIPT_EXCEPTION"; //$NON-NLS-1$
	
	/**
	 * Key of Script command exception error message in properties file
	 */
	private static final String	SCRIPT_COMMAND_EXCEPTION_PROPERTY_KEY = "SCRIPT_COMMAND_EXCEPTION"; //$NON-NLS-1$
	
	/**
	 * engine is a ScriptEngine use to execute Groovy script
	 */
	private final ScriptEngine engine;
	
	/**
	 * Logger
	 */
	private final Logger logger = Logger.getLogger(this.getClass().toString());
	
	/**
	 * Default constructor
	 */
	public GroovyExecutionScriptContext()
	{
		ScriptEngineManager factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName(GROOVY_ENGINE_NAME);
	}

	/**
	 * Returns the underlying script engine 
	 * @return the Groovy execution engine
	 */
	public ScriptEngine getEngine() {
		return this.engine;
	}
	
	/**
	 * Returns the logger used by this context
	 * @return the logger associated to this class
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * Function use to build a valid path merging the path of the annotation of the GroovyAgent and the script name
	 * 
	 * @param path
	 * @param scriptName
	 * @return
	 */
	private String validPath(String path, String scriptName) {
		String script = scriptName.trim();
		String goodPath = path.trim();
		File f = new File(script);

		if (f.isFile() && f.getName().endsWith(GROOVY_FILE_EXTENSION)) {
			return script;
		}

		if (!script.substring(script.length() - 3, script.length()).equals(GROOVY_FILE_EXTENSION)) {
			script += GROOVY_FILE_EXTENSION;
		}

		if (goodPath.charAt(goodPath.length() - 1) != File.separatorChar) {
			goodPath += File.separatorChar;
		}

		f = new File(goodPath + script);

		if (f.isFile()) {
			return goodPath + script;
		}

		this.logger.severe(Locale.getString(GroovyExecutionScriptContext.class, FILE_NOT_FOUND_PROPERTY_KEY, goodPath + script));
		return null;
	}
	
	/**
	 * Evaluate a Groovy file script from a path, standard and Groovy errors output by default
	 * 
	 * @param path is the absolute pathname targeting the script file
	 * @return the default output in a String
	 */
	public String runScriptFromPath(String path) {

		String returnScriptStream = ""; //$NON-NLS-1$
		try {
			this.engine.eval(new BufferedReader(new FileReader(path)));
		} catch (FileNotFoundException e) {
			this.logger.severe(Locale.getString(GroovyExecutionScriptContext.class, FILE_NOT_FOUND_PROPERTY_KEY, path));
		} catch (ScriptException e) {
			this.logger.severe(Locale.getString(GroovyExecutionScriptContext.class, SCRIPT_EXCEPTION_PROPERTY_KEY, e.getMessage()));
		}
		return returnScriptStream;
	}

	/**
	 * Same as the runScriptFromPath(String path) adapted for the Groovy annotation
	 * 
	 * @param path
	 * @param ScriptName
	 * @return the default output in a String
	 */
	public String runScriptFromPath(String path, String ScriptName) {
		return runScriptFromPath(validPath(path, ScriptName));
	}

	/**
	 * Evaluate a Groovy file script from a path, standard and Groovy errors output are redirect in the StringWriters
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
	 * Same as the runScriptFromPath(String path, StringWriter errorWriter,StringWriter putsWriter) adapted for the Groovy annotation
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
	 * Run the result of the command in the context deployed with default output for standard and Groovy errors
	 * 
	 * @param a_command
	 *            is the command which will be executed in the context
	 * @return the return of the script context for the command
	 */
	public String runGroovyCommand(String a_command) {
		
		String returnScriptStream = ""; //$NON-NLS-1$
		try {
			this.engine.eval(" result = " + a_command); //$NON-NLS-1$
			Object o = this.engine.getContext().getAttribute("result"); //$NON-NLS-1$
			if ( o != null)
			{
				String str = this.engine.getContext().getAttribute("result").toString(); //$NON-NLS-1$
				returnScriptStream  = str;
			}
		} catch (ScriptException e) {
			this.logger.severe(Locale.getString(GroovyExecutionScriptContext.class, SCRIPT_COMMAND_EXCEPTION_PROPERTY_KEY, e.getMessage()));
		}
		return returnScriptStream ;
	}

	/**
	 * Run the result of the command in the context deployed redirecting standard ouput and Groovy errors in the StringWriters
	 * 
	 * @param a_command
	 *            - is the command which will be executed in the context
	 * @param errorWriter
	 * @param putsWriter
	 * @return the return of the script context for the command
	 */
	public String runGroovyCommand(String a_command, StringWriter errorWriter, StringWriter putsWriter) {
		this.engine.getContext().setErrorWriter(errorWriter);
		this.engine.getContext().setWriter(putsWriter);
		return runGroovyCommand(a_command);
	}
	
	/**
	 * Execute the Groovy function specified of the given script, taking parameters with default standard and Groovy error outputs
	 * 
	 * @param scriptPath
	 * @param functionName
	 * @param params
	 * @return an object returned by the Groovy function (can returning multiple object in a tab[])
	 */
	public Object runGroovyFunction(String scriptPath, String functionName, Object... params) {
		Object res = null;
		String funcString = ""; //$NON-NLS-1$
		try {
			this.engine.eval(new BufferedReader(new FileReader(scriptPath)));
			if (functionName.indexOf("(") >= 0) { //$NON-NLS-1$
				funcString = functionName.substring(0, functionName.indexOf("(")).trim() + "("; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				funcString = functionName.trim() + "("; //$NON-NLS-1$
			}
			if (params.length == 0)
				funcString += ")"; //$NON-NLS-1$
			else {
				for (int i = 0; i < params.length; i++) {
					this.engine.getContext().setAttribute("a" + i, params[i], ScriptContext.ENGINE_SCOPE); //$NON-NLS-1$
					funcString += "a" + i + ","; //$NON-NLS-1$ //$NON-NLS-2$
				}
				funcString = funcString.substring(0, funcString.length() - 1) + ")"; //$NON-NLS-1$
			}
			res = this.engine.eval(funcString);

		} catch (FileNotFoundException e) {
			this.logger.severe(Locale.getString(GroovyExecutionScriptContext.class, FILE_NOT_FOUND_PROPERTY_KEY, scriptPath));
		} catch (ScriptException e) {
			this.logger.severe(Locale.getString(GroovyExecutionScriptContext.class, SCRIPT_EXCEPTION_PROPERTY_KEY, e.getMessage()));
		}
		return res;
	}
	
	/**
	 * Same as runGroovyFunction(String scriptPath, String functionName,Object... params) adapted for the Groovy annotation
	 * 
	 * @param path
	 * @param scriptName
	 * @param functionName
	 * @param params
	 * @return the result of the executed Groovy function
	 */
	public Object runGroovyFunction(String path, String scriptName, String functionName, Object... params) {
		return runGroovyFunction(validPath(path, scriptName), functionName, params);
	}
	
	/**
	 * Execute the Groovy function specified in the given script, taking parameters redirecting standard and Groovy errors outputs
	 * 
	 * @param scriptPath
	 * @param functionName
	 * @param errorWriter
	 * @param putsWriter
	 * @param params
	 * @return an object returned by the Groovy function (can returning multiple object in a tab[])
	 */
	public Object runGroovyFunction(String scriptPath, String functionName, StringWriter errorWriter, StringWriter putsWriter, Object... params) {
		this.engine.getContext().setErrorWriter(errorWriter);
		this.engine.getContext().setWriter(putsWriter);
		return runGroovyFunction(scriptPath, functionName, params);
	}

	/**
	 * Same as runGroovyFunction(String scriptPath, String functionName, StringWriter errorWriter, StringWriter putsWriter,Object... params) adapted for the GroovyAgent annotation
	 * 
	 * @param path
	 * @param scriptName
	 * @param functionName
	 * @param errorWriter
	 * @param putsWriter
	 * @param params
	 * @return the result of the executed Groovy function
	 */
	public Object runGroovyFunction(String path, String scriptName, String functionName, StringWriter errorWriter, StringWriter putsWriter, Object... params) {
		return runGroovyFunction(validPath(path, scriptName), functionName, errorWriter, putsWriter, params);
	}

}
