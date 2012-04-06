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
public class TravelContractGroupEvent extends BDIEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2501637881312820992L;

	/**
	 * @param source
	 */
	public TravelContractGroupEvent(Object source) {
		super(source);
	}

}
