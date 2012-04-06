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
public class ContactFromClientEvent extends BDIEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4465192815935970243L;

	/**
	 * @param source
	 */
	public ContactFromClientEvent(Object source) {
		super(source);
	}

}
