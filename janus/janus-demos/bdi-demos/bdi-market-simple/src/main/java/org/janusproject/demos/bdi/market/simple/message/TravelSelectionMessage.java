/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.demos.bdi.market.simple.message;

import org.janusproject.kernel.message.ObjectMessage;

/**
 * Message that contains a travel query.
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class TravelSelectionMessage extends ObjectMessage {

	private static final long serialVersionUID = 812286688821571799L;
	
	/**
	 * @param isSelected
	 */
	public TravelSelectionMessage(boolean isSelected) {
		super(isSelected);
	}
	
	/** Replies if the proposal was selected.
	 * 
	 * @return <code>true</code> if the proposal was selected,
	 * otherwise <code>false</code>.
	 */
	public boolean isProposalSelected() {
		return ((Boolean)getContent()).booleanValue();
	}
		
}
