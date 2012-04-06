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

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.jrubyengine.exceptions.NotANormalizedDirectoryException;
import org.janusproject.kernel.agent.Agent;



/**
 * Agent created to run JRuby commands and scripts
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gvinson$
 * @author $Author: rbuecher$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class JRubyAgent extends Agent {

	private static final long serialVersionUID = -3761636243398501378L;
	
	/**
	 * scriptExecutor is the Object use to run commands and scripts
	 */
	private RubyExecutionScriptContext scriptExecutor;
	
	/**
	 * rubyDirectory is used to validate the JRuby script directory of the annotation
	 */
	private RubyDirectoryFinder rubyDirectory;
	
	/**
	 * Looger
	 */
	private final Logger logger = Logger.getLogger(this.getClass().toString());
	
	/**
	 * 
	 */
	public JRubyAgent(){
		super();
		this.scriptExecutor = new RubyExecutionScriptContext();
		
		this.rubyDirectory = null;
		RubyScriptPath rubyPath = getClass().getAnnotation(RubyScriptPath.class);
		
		if (rubyPath != null) {
			try {
				this.rubyDirectory = new RubyDirectoryFinder(rubyPath.path());
			} catch (NullPointerException e) {
				this.logger.severe(Locale.getString(JRubyAgent.class,"NOT_A_RUBY_SCRIPT_DIRECTORY", rubyPath.path())); //$NON-NLS-1$
			} catch (NotANormalizedDirectoryException e) {
				this.logger.severe(Locale.getString(JRubyAgent.class,"NOT_A_NORMALIZED_RUBY_SCRIPT_DIRECTORY", rubyPath.path())); //$NON-NLS-1$
			} catch (Exception e) {
				this.logger.log(Level.SEVERE,Locale.getString(JRubyAgent.class,"RUBY_SCRIPT_DIRECTORY", rubyPath.path()), e); //$NON-NLS-1$
			}
		}
	}
	
	
	/**
	 * @return the scriptExecutor
	 */
	public RubyExecutionScriptContext getScriptExecutor() {
		return this.scriptExecutor;
	}

	/**
	 * @param scriptExecutor
	 *            the scriptExecutor to set
	 */
	public void setScriptExecutor(RubyExecutionScriptContext scriptExecutor) {
		this.scriptExecutor = scriptExecutor;
	}
	/**
	 * @return the rubyDirectory
	 */
	public RubyDirectoryFinder getRubyDirectory() {
		return this.rubyDirectory;
	}

	/**
	 * @param rubyDirectory
	 *            the rubyDirectory to set
	 */
	public void setRubyDirectory(RubyDirectoryFinder rubyDirectory) {
		this.rubyDirectory = rubyDirectory;
	}
	
	/**
	 * Function used to run a JRuby script
	 * @param scriptName - the name of the script if the directory annotation is used or the relative or absolute path of the script
	 * @return String with no puts and no errors inside
	 */
	public String runScriptFromPath(String scriptName){
		return this.scriptExecutor.runScriptFromPath(path(),scriptName);
	}
	/**
	 * Function used to run a JRuby script and change defaults outputs with StringWriters
	 * @param scriptName - the name of the script if the directory annotation is used or the relative or absolute path of the script
	 * @param errorWriter
	 * @param putsWriter
	 * @return all the outputs of the script in a String
	 */
	public String runScriptFromPath(String scriptName,StringWriter errorWriter,StringWriter putsWriter){
		return this.scriptExecutor.runScriptFromPath(path(),scriptName,errorWriter,putsWriter);
	}
	
	/**
	 * Function used to directly run a Ruby command
	 * @param command - the command to execute
	 * @return String with no puts and no errors inside
	 */
	public String runRubyCommand(String command){
		return this.scriptExecutor.runRubyCommand(command);
	}

	/**
	 * Function used to directly run a Ruby command and change defaults outputs with StringWriters
	 * @param command - the command to execute
	 * @param errorWriter
	 * @param putsWriter
	 * @return the result of the executed command
	 */
	public String runRubyCommand(String command,StringWriter errorWriter,StringWriter putsWriter){
		return this.scriptExecutor.runRubyCommand(command,errorWriter,putsWriter);
	}
	
	
	/**
	 * execute a specific function in a script at a given path
	 * @param scriptPath - path to the specified script
	 * @param scriptName - name of the script
	 * @param functionName - name of the function to run within the specified script
	 * @param params
	 * @return the resutl of the executed ruby function
	 */
	public Object runRubyFunction(String scriptPath, String scriptName, String functionName, Object... params) {
		return this.scriptExecutor.runRubyFunction(scriptPath, scriptName, functionName, params);
	}
	
	
	/**
	 * execute a specific function in a script
	 * 
	 * @param scriptName - name of the script
	 * @param functionName - name of the function to run within the specified script
	 * @param params
	 * @return the resutl of the executed ruby function
	 */
	public Object runRubyFunction(String scriptName, String functionName,Object... params){
		return this.scriptExecutor.runRubyFunction(path(),scriptName, functionName, params);
	}
	
	/**
	 * execute a specific function in a script
	 * redirecting "puts" and ruby errors in StringWriters 
	 * 
	 * @param scriptName
	 * @param functionName
	 * @param errorWriter
	 * @param putsWriter
	 * @param params
	 * @return the resutl of the executed ruby function
	 */
	public Object runRubyFunction(String scriptName, String functionName, StringWriter errorWriter, StringWriter putsWriter,Object... params){
		return this.scriptExecutor.runRubyFunction(path(),scriptName,functionName,errorWriter,putsWriter,params);
	}

	/**
	 * Return the path in which Ruby scripts should be found.
	 * 
	 * @return the path in which Ruby scripts should be found.
	 */
	public String path() {
		return this.rubyDirectory.getDirectory().getAbsolutePath();
	}


}
