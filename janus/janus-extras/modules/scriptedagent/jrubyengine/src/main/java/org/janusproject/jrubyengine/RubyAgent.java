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

import java.io.File;
import java.net.URL;

import javax.script.ScriptEngineManager;

import org.janusproject.scriptedagent.ScriptedAgent;

/**
 * Agent created to run JRuby commands and scripts
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gvinson$
 * @author $Author: rbuecher$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class RubyAgent extends ScriptedAgent<RubyExecutionContext> {

	private static final long serialVersionUID = -2048354743353866599L;

	/**
	 * Creates a new RubyAgent.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 */
	public RubyAgent(ScriptEngineManager scriptManager) {
		super(new RubyExecutionContext());
	}
	
	/**
	 * Creates a new RubyAgent. 
	 */
	public RubyAgent() {
		super(new RubyExecutionContext());
	}
	
	/**
	 * Creates a new RubyAgent and load the script at startup.
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public RubyAgent(ScriptEngineManager scriptManager, String scriptBasename) {
		super(new RubyExecutionContext(), scriptBasename);
	}
	
	/**
	 * Creates a new RubyAgent and load the script at startup. 
	 * The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * 
	 * @param scriptBasename is the basename of the script to load at startup.
	 */
	public RubyAgent(String scriptBasename) {
		super(new RubyExecutionContext(), scriptBasename);
	}

	/**
	 * Creates a new RubyAgent and load the script at startup.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public RubyAgent(ScriptEngineManager scriptManager, File script) {
		super(new RubyExecutionContext(), script);
	}
	
	/**
	 * Creates a new RubyAgent and load the script at startup. 
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public RubyAgent(File script) {
		super(new RubyExecutionContext(), script);
	}

	/**
	 * Creates a new RubyAgent and load the script at startup.
	 * 
	 * @param scriptManager is the manager of the script engines to use.
	 * @param script is the filename of the script to load at startup.
	 */
	public RubyAgent(ScriptEngineManager scriptManager, URL script) {
		super(new RubyExecutionContext(), script);
	}
	
	/**
	 * Creates a new RubyAgent and load the script at startup. 
	 * 
	 * @param script is the filename of the script to load at startup.
	 */
	public RubyAgent(URL script) {
		super(new RubyExecutionContext(), script);
	}

}
