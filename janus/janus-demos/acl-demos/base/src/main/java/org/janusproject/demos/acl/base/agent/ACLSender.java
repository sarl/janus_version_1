package org.janusproject.demos.acl.base.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.Performative;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.SizedIterator;



/**
 * 
 * A simple agent sending an ACl Message
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLSender extends ACLAgent {

	private static final long serialVersionUID = -7078376398776599011L;
	
	private boolean isSent = false;
	
	@Override
	public Status live() {
		
		ACLMessage message = new ACLMessage(Locale.getString(ACLSender.class, "NAME",getName()), Performative.PROPOSE); //$NON-NLS-1$
		SizedIterator<AgentAddress> agents = getLocalAgents();
		
		if (!this.isSent) {
			while (agents.hasNext()) {
				AgentAddress agent = agents.next();
				if (agent != getAddress()) {
					sendACLMessage(message, agent);
					Logger logger = Logger.getAnonymousLogger();

					logger.log(Level.INFO,Locale.getString(ACLSender.class, "MESSAGESENT",getName(), message.toString())); //$NON-NLS-1$
					this.isSent = true;
				}
			}
		}  else {
			this.killMe();
		}
		
		return StatusFactory.ok(this);
	}
	
}
