package org.janusproject.demos.base.helloagents;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * A simple Agent waiting a message "hello" and sending a "welcome" message in
 * return
 * 
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentB extends Agent {

	private static final long serialVersionUID = -2478042408957087937L;

	private boolean ACKsent = false;

	@Override
	public Status activate(Object... parameters) {
		print(Locale.getString(AgentB.class, "AgentB.0")); //$NON-NLS-1$
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {

		for (Message m : getMessages()) {
			print(Locale.getString(AgentB.class, "AgentB.1")); //$NON-NLS-1$				
			if (m instanceof StringMessage && ((StringMessage) m).getContent().equals(Launcher.WELCOME_MESSAGE_STRING_HEADER)) {
				replyToMessage(m, new StringMessage(Launcher.WELCOME_MESSAGE_ACK_STRING_HEADER));
				AgentAddress messageEmitter = (AgentAddress) m.getSender();
				print(Locale.getString(AgentB.class, "AgentB.4", Launcher.WELCOME_MESSAGE_STRING_HEADER,messageEmitter)); //$NON-NLS-1$
				print(Locale.getString(AgentB.class, "AgentB.5",Launcher.WELCOME_MESSAGE_ACK_STRING_HEADER)); //$NON-NLS-1$			
				this.ACKsent = true;
			}
		}

		if (this.ACKsent) {
			print(Locale.getString(AgentB.class, "AgentB.6")); //$NON-NLS-1$
			killMe();
		}

		return StatusFactory.ok(this);
	}
}