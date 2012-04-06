package org.janusproject.demos.bdi.market.simple.event;

import org.janusproject.kernel.agent.bdi.event.BDIEvent;


/**
 * 
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ContactFromBrokerEvent extends BDIEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1337455960065454037L;

	/**
	 * @param source
	 */
	public ContactFromBrokerEvent(Object source) {
		super(source);
	}

}
