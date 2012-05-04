package org.janusproject.demos.acl.base.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.ACLMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * A simple agent receiving an ACl Message
 * 
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLReceiver extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;
	
	@Override
	public Status live() {
		
		ACLMessage aMsg = getACLMessage();
		
		if( aMsg != null ){
			
			Logger logger = Logger.getAnonymousLogger();
			
			logger.log(Level.INFO, Locale.getString(ACLReceiver.class, "MESSAGERECEIVED",getName(), aMsg.toString())); //$NON-NLS-1$
			
			this.killMe();
		}
		
		return StatusFactory.ok(this);
	}
	
}