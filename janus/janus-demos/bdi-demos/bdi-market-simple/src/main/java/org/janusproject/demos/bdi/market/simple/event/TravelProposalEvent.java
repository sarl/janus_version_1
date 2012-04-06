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
public class TravelProposalEvent extends BDIEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3774581545400889859L;

	/**
	 * @param source
	 */
	public TravelProposalEvent(Object source) {
		super(source);
	}

}
