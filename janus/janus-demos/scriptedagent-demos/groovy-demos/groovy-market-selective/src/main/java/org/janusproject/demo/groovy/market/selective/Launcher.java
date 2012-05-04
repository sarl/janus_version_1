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
package org.janusproject.demo.groovy.market.selective;


import org.janusproject.groovyengine.GroovyExecutionScriptContext;


/**
 * Launcher of Groovy Selective Market Demos
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	
	private static final String GroovyScriptPath = Launcher.class.getClassLoader().getResource("org/janusproject/demo/groovy/market/selective/").getPath(); //$NON-NLS-1$
	
	/**
	 * Execute a script which is the Launcher.java
	 * from the simpleMarketSelectiv demo in groovy version
	 * @param args
	 */
	public static void main(String[] args) {		
		GroovyExecutionScriptContext resc = new GroovyExecutionScriptContext();
		resc.runScriptFromPath(GroovyScriptPath,"Launcher.gy"); //$NON-NLS-1$
	}

}
