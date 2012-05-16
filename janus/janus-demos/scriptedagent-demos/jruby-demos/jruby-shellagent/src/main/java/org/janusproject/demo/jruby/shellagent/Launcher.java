/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.demo.jruby.shellagent;

import org.janusproject.demo.agentshell.base.ConsoleGUI;
import org.janusproject.demo.jruby.shellagent.agent.JRubyAgentShell;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;

/**
 * Launcher of JRuby Shell Agent Demos
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		Kernel k = Kernels.get(true);
		
		JRubyAgentShell jrba = new JRubyAgentShell();
		
		AgentAddress aa = k.launchLightAgent(jrba, "Console 1"); //$NON-NLS-1$
		ConsoleGUI c = new ConsoleGUI(aa);
		c.setVisible(true);	
		
		/*JRubyAgentShell jrba2 = new JRubyAgentShell();
		
		AgentAddress aa2 = k.launchLightAgent(jrba2, "Console 2"); //$NON-NLS-1$
		IHMConsole c2 = new IHMConsole(aa2);
		c2.setVisible(true);	**/
	}
}
