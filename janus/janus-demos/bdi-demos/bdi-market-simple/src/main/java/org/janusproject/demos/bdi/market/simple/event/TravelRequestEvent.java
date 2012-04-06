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
public class TravelRequestEvent extends BDIEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6281530143451338124L;

	/**
	 * @param source
	 */
	public TravelRequestEvent(Object source) {
		super(source);
	}

}
