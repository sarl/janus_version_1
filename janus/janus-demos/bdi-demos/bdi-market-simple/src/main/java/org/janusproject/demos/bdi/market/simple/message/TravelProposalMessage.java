/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.demos.bdi.market.simple.message;

import org.janusproject.demos.bdi.market.simple.capacity.Proposal;
import org.janusproject.kernel.message.ObjectMessage;


/**
 * Message that contains a travel proposal.
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $FullVersion$
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
