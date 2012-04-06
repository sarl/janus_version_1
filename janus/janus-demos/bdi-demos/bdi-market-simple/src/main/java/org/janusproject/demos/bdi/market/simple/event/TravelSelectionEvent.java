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
public class TravelSelectionEvent extends BDIEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2324028322685948357L;

	/**
	 * @param source
	 */
	public TravelSelectionEvent(Object source) {
		super(source);
	}

}
