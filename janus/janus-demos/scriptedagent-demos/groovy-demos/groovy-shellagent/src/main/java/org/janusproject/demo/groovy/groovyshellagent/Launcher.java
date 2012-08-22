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
package org.janusproject.demo.groovy.groovyshellagent;

import org.janusproject.demo.agentshell.base.ConsoleGUI;
import org.janusproject.demo.groovy.groovyshellagent.agent.GroovyAgentShell;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;

/**
 * Launcher of Groovy Shellagent
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */

public class Launcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Kernel k = Kernels.get(true);
		
		GroovyAgentShell agentShell = new GroovyAgentShell();
		GroovyAgentShell agentShell2 = new GroovyAgentShell();
		
		AgentAddress address = k.launchLightAgent(agentShell, "Console 1"); //$NON-NLS-1$
		AgentAddress address2 = k.launchLightAgent(agentShell2, "Console 2"); //$NON-NLS-1$
		ConsoleGUI console2 = new ConsoleGUI(address2);
		ConsoleGUI console = new ConsoleGUI(address);
		console.setVisible(true);	
		console2.setVisible(true);

	}

}
