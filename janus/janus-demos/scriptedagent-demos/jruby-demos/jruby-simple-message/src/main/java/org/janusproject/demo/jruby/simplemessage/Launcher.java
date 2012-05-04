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
package org.janusproject.demo.jruby.simplemessage;

import org.janusproject.demo.jruby.simplemessage.agent.JRubyAgentReceiver;
import org.janusproject.demo.jruby.simplemessage.agent.JRubyAgentSender;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;

/**
 * Launcher for the JRuby Simple Message Demos
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
	 * @param args
	 */
	public static void main(String[] args) {
		Kernel k = Kernels.get(true);
		
		JRubyAgentReceiver receiver = new JRubyAgentReceiver();
		AgentAddress arec = k.launchLightAgent(receiver, "receiver"); //$NON-NLS-1$
		
		JRubyAgentSender sender = new JRubyAgentSender(arec);
		k.launchLightAgent(sender, "sender"); //$NON-NLS-1$
		
		
	}

}
