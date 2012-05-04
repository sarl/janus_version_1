package org.janusproject.demos.acl.request;

import java.util.logging.Level;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.demos.acl.request.agent.ACLProtocolReceiver;
import org.janusproject.demos.acl.request.agent.ACLProtocolSender;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * DEMO : Request Protocol
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
		
		ACLProtocolSender sender = new ACLProtocolSender();
		ACLProtocolReceiver receiver = new ACLProtocolReceiver();
		
		k.submitLightAgent(sender, Locale.getString(Launcher.class, "SENDER")); //$NON-NLS-1$
		k.submitLightAgent(receiver, Locale.getString(Launcher.class, "RECEIVER")); //$NON-NLS-1$
		
		k.launchDifferedExecutionAgents();	
		
		Kernels.killAll();
	}	
}
