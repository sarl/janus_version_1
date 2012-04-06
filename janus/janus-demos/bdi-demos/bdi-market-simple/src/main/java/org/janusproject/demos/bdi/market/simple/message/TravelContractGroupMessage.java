/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.demos.bdi.market.simple.message;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.message.Message;

/**
 * Message that contains a contract group.
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class TravelContractGroupMessage extends Message {

	private static final long serialVersionUID = 812286688821571799L;
	
	private final AgentAddress provider;
	
	/**
	 * @param provider
	 */
	public TravelContractGroupMessage(AgentAddress provider) {
		this.provider = provider;
	}
	
	/** Replies the provider address.
	 * 
	 * @return the provider address.
	 */
	public AgentAddress getProvider() {
		return this.provider;
	}
}
