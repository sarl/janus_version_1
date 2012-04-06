/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.demos.bdi.market.simple.message;

import org.janusproject.demos.bdi.market.simple.capacity.Proposal;
import org.janusproject.kernel.message.ObjectMessage;


/**
 * Message that contains a travel proposal.
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class TravelProposalMessage extends ObjectMessage {

	private static final long serialVersionUID = 812286688821571799L;
	
	/**
	 * @param proposal
	 */
	public TravelProposalMessage(Proposal proposal) {
		super(proposal);
	}
	
	/** Replies if the proposal.
	 * 
	 * @return the proposal.
	 */
	public Proposal getProposal() {
		return (Proposal)getContent();
	}
		
}
