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
public class ContractFinalizationEvent extends BDIEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5285088053950142730L;

	/**
	 * @param source
	 */
	public ContractFinalizationEvent(Object source) {
		super(source);
	}

}
