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
package org.janusproject.demo.groovy.simplemessage;

import java.net.URL;

import org.arakhne.afc.vmutil.Resources;
import org.janusproject.demo.groovy.simplemessage.agent.GroovyAgentReceiver;
import org.janusproject.demo.groovy.simplemessage.agent.GroovyAgentSender;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;

/**
 * Launcher for the Groovy Simple Message Demos
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {

	/** URL of the Groovy receiver script.
	 */
	public static final URL RECEIVER_SCRIPT = Resources.getResource(Launcher.class, "receiver.groovy"); //$NON-NLS-1$

	/** URL of the Groovy receiver script.
	 */
	public static final URL SENDER_SCRIPT = Resources.getResource(Launcher.class, "sender.groovy"); //$NON-NLS-1$

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Kernel k = Kernels.get(true);
		
		GroovyAgentReceiver receiver = new GroovyAgentReceiver();
		AgentAddress arec = k.launchLightAgent(receiver, "receiver"); //$NON-NLS-1$
		
		GroovyAgentSender sender = new GroovyAgentSender(arec);
		k.launchLightAgent(sender, "sender"); //$NON-NLS-1$
		
		
	}

}
