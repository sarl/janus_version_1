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
package org.janusproject.groovyengine;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.janusproject.groovyengine.exceptions.NotANormalizedDirectoryException;
import org.janusproject.kernel.agent.Agent;
import org.arakhne.vmutil.locale.Locale;

/**
 * Agent created to run Groovy commands and scripts
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroovyAgent extends Agent {

	private static final long serialVersionUID = -5609194561664743380L;
	
	/**
	 * Key for not a Groovy directory error message in properties file
	 */
	private static final String NOT_A_GROOVY_SCRIPT_DIRECTORY_PROPERTIES_KEY = "NOT_A_GROOVY_SCRIPT_DIRECTORY"; //$NON-NLS-1$

	/**
	 * Key for not a normalized directory in properties file
	 */
	private static final String NOT_A_NORMALIZED_GROOVY_SCRIPT_DIRECTORY_PROPERTIES_KEY = "NOT_A_NORMALIZED_GROOVY_SCRIPT_DIRECTORY"; //$NON-NLS-1$

	/**
	 * Key for not a script directory properties file
	 */
	private static final String GROOVY_SCRIPT_DIRECTORY_PROPERTIES_KEY = "GROOVY_SCRIPT_DIRECTORY"; //$NON-NLS-1$
	
	/**
	 * scriptExecutor is the Object use to run commands and scripts
	 */
	private GroovyExecutionScriptContext scriptExecutor;
	
	/**
	 * groovyDirectory is used to validate the Groovy script directory of the annotation
	 */	
	private GroovyDirectoryFinder groovyDirectory;
	
	/**
	 * Looger
	 */	
	private final Logger logger = Logger.getLogger(this.getClass().toString());
	
	/**
	 * Creates a new GroovyAgent 
	 */
	public GroovyAgent() {
		super();
		this.scriptExecutor = new GroovyExecutionScriptContext();
		
		this.groovyDirectory = null;
		GroovyScriptPath groovyPath = getClass().getAnnotation(GroovyScriptPath.class);
		
		if ( groovyPath != null )
		{
			try {
				this.groovyDirectory = new GroovyDirectoryFinder(groovyPath.path());
			} catch (NullPointerException e) {
				this.logger.severe(Locale.getString(GroovyAgent.class,NOT_A_GROOVY_SCRIPT_DIRECTORY_PROPERTIES_KEY, groovyPath.path()));
			} catch (NotANormalizedDirectoryException e) {
				this.logger.severe(Locale.getString(GroovyAgent.class,NOT_A_NORMALIZED_GROOVY_SCRIPT_DIRECTORY_PROPERTIES_KEY, groovyPath.path()));
			} catch (Exception e) {
				this.logger.log(Level.SEVERE,Locale.getString(GroovyAgent.class,GROOVY_SCRIPT_DIRECTORY_PROPERTIES_KEY, groovyPath.path()), e);
			}
		}
	}
	
	/**
	 * Returns the script context associated with this agent
	 * @return the scriptExecutor
	 */
	public GroovyExecutionScriptContext getScriptExecutor() {
		return this.scriptExecutor;
	}

	/**
	 * Set the script context to be used by this agent
	 * @param scriptExecutor the scriptExecutor to set
	 */
	public void setScriptExecutor(GroovyExecutionScriptContext scriptExecutor) {
		this.scriptExecutor = scriptExecutor;
	}
	/**
	 * Returns the GroovyDirectory where Groovy scripts file are located
	 * @return the GroovyDirectory
	 */
	public GroovyDirectoryFinder getGroovyDirectory() {
		return this.groovyDirectory;
	}
	
	/**
	 * Set the directory where groovy scripts files are located 
	 * @param groovyDirectory the groovyDirectory to use
	 */
	public void setGroovyDirectory(GroovyDirectoryFinder groovyDirectory) {
		this.groovyDirectory = groovyDirectory;
	}
	
	/**
	 * Function used to run a Groovy script
	 * @param scriptName - the name of the script if the directory annotation is used or the relative or absolute path of the script
	 * @return String with no puts and no errors inside
	 */
	public String runScriptFromPath(String scriptName){
		return this.scriptExecutor.runScriptFromPath(path(),scriptName);
	}
	
	/**
	 * Function used to run a Groovy script and change defaults outputs with StringWriters
	 * @param scriptName - the name of the script if the directory annotation is used or the relative or absolute path of the script
	 * @param errorWriter
	 * @param putsWriter
	 * @return all the outputs of the script in a String
	 */
	public String runScriptFromPath(String scriptName,StringWriter errorWriter,StringWriter putsWriter){
		return this.scriptExecutor.runScriptFromPath(path(),scriptName,errorWriter,putsWriter);
	}

	/**
	 * Function used to directly run a Groovy command
	 * @param command - the command to execute
	 * @return String with no puts and no errors inside
	 */
	public String runGroovyCommand(String command){
		return this.scriptExecutor.runGroovyCommand(command);
	}

	/**
	 * Function used to directly run a Groovy command and change defaults outputs with StringWriters
	 * @param command - the command to execute
	 * @param errorWriter
	 * @param putsWriter
	 * @return the result of the executed command
	 */
	public String runGroovyCommand(String command,StringWriter errorWriter,StringWriter putsWriter){
		return this.scriptExecutor.runGroovyCommand(command,errorWriter,putsWriter);
	}
	
	
	/**
	 * Execute a specific function in a script at a given path
	 * @param scriptPath - path to the specified script
	 * @param scriptName - name of the script
	 * @param functionName - name of the function to run within the specified script
	 * @param params
	 * @return the result of the executed Groovy function
	 */
	public Object runGroovyFunction(String scriptPath, String scriptName, String functionName, Object... params) {
		return this.scriptExecutor.runGroovyFunction(scriptPath, scriptName, functionName, params);
	}
	
	/**
	 * Execute a specific function in a script
	 * 
	 * @param scriptName - name of the script
	 * @param functionName - name of the function to run within the specified script
	 * @param params
	 * @return the result of the executed Groovy function
	 */
	public Object runGroovyFunction(String scriptName, String functionName,Object... params){
		return this.scriptExecutor.runGroovyFunction(path(),scriptName, functionName, params);
	}
	
	/**
	 * Execute a specific function in a script
	 * redirecting output and Groovy errors in StringWriters 
	 * 
	 * @param scriptName
	 * @param functionName
	 * @param errorWriter
	 * @param putsWriter
	 * @param params
	 * @return the result of the executed Groovy function
	 */
	public Object runGroovyFunction(String scriptName, String functionName, StringWriter errorWriter, StringWriter putsWriter,Object... params){
		return this.scriptExecutor.runGroovyFunction(path(),scriptName,functionName,errorWriter,putsWriter,params);
	}

	/**
	 * Return the path in which Groovy scripts should be found.
	 * 
	 * @return the path in which Groovy scripts should be found.
	 */
	public String path() {
		// TODO A VERIFIER
		if ( this.groovyDirectory == null )
			return ""; //$NON-NLS-1$
			
		return this.groovyDirectory.getDirectory().getAbsolutePath();
	}
}
