package org.janusproject.demos.acl.cnp;

import java.util.logging.Level;

import org.arakhne.vmutil.locale.Locale;
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
 * @version $Name$ $Revision$ $Date$
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
