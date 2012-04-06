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
public class ContactFromProviderEvent extends BDIEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5368286194212866428L;

	/**
	 * @param source
	 */
	public ContactFromProviderEvent(Object source) {
		super(source);
	}
	
}
