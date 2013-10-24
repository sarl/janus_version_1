/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.demos.acl.cnp;

import java.util.logging.Level;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.acl.cnp.agent.ACLCallForProposalReceiver;
import org.janusproject.demos.acl.cnp.agent.ACLCallForProposalSender;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * DEMO : Contract Net Protocol
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		LoggerUtil.setGlobalLevel(Level.ALL);
		LoggerUtil.setShortLogMessageEnable(true);
		
		Kernel k = Kernels.get( false );
		
		ACLCallForProposalSender sender = new ACLCallForProposalSender();
		ACLCallForProposalReceiver receiver1 = new ACLCallForProposalReceiver();
		ACLCallForProposalReceiver receiver2 = new ACLCallForProposalReceiver();
		
		k.submitLightAgent(sender, Locale.getString(Launcher.class, "SENDER")); //$NON-NLS-1$
		k.submitLightAgent(receiver1, Locale.getString(Launcher.class, "RECEIVER1")); //$NON-NLS-1$
		k.submitLightAgent(receiver2, Locale.getString(Launcher.class, "RECEIVER2")); //$NON-NLS-1$
		
		k.launchDifferedExecutionAgents();	
		
		Kernels.killAll();
	}	
}
