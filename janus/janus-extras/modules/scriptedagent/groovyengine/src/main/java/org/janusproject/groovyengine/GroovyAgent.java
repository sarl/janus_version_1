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

import java.io.File;
import java.net.URL;

import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.ScriptedAgent;

/**
 * Agent created to run Groovy commands and scripts
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroovyAgent extends ScriptedAgent<GroovyExecutionContext> {

	private static final long serialVersionUID = -6195345519507255244L;

	/**
	 * Creates a new GroovyAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 */
	public GroovyAgent(ScriptEngineManager scriptManager) {
		super(new GroovyExecutionContext(scriptManager));
	}
	
	/**
	 * Creates a new GroovyAgent. 
	 */
	public GroovyAgent() {
		this(getSharedScriptEngineManager());
	}
	
	/**
	 * Creates a new GroovyAgent and load the script at startup.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public GroovyAgent(ScriptEngineManager scriptManager, String scriptBasename) {
		super(new GroovyExecutionContext(scriptManager), scriptBasename);
	}
	
	/**
	 * Creates a new GroovyAgent and load the script at startup.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public GroovyAgent(String scriptBasename) {
		this(getSharedScriptEngineManager(), scriptBasename);
	}

	/**
	 * Creates a new GroovyAgent and load the script at startup.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public GroovyAgent(ScriptEngineManager scriptManager, File script) {
		super(new GroovyExecutionContext(scriptManager), script);
	}
	
	/**
	 * Creates a new GroovyAgent and load the script at startup.
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public GroovyAgent(File script) {
		this(getSharedScriptEngineManager(), script);
	}

	/**
	 * Creates a new GroovyAgent and load the script at startup.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public GroovyAgent(ScriptEngineManager scriptManager, URL script) {
		super(new GroovyExecutionContext(scriptManager), script);
	}
	
	/**
	 * Creates a new GroovyAgent and load the script at startup.
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public GroovyAgent(URL script) {
		this(getSharedScriptEngineManager(), script);
	}

}
