/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.demos.bdi.market.simple.message;

import org.janusproject.demos.bdi.market.simple.travel.TravelDestination;
import org.janusproject.demos.bdi.market.simple.travel.TravelSelectionCritera;
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
public class TravelRequestMessage extends ObjectMessage {

	private static final long serialVersionUID = 812286688821571799L;
	
	private final TravelSelectionCritera critera;
	
	/**
	 * @param destination
	 * @param critera
	 */
	public TravelRequestMessage(TravelDestination destination, TravelSelectionCritera critera) {
		super(destination);
		this.critera = critera;
	}
	
	/** Replies the travel destination.
	 * 
	 * @return the travel destination.
	 */
	public TravelDestination getDestination() {
		return (TravelDestination)getContent();
	}
	
	/** Replies the expected selection critera.
	 * 
	 * @return the expected selection critera.
	 */
	public TravelSelectionCritera getCritera() {
		return this.critera;
	}
		
}
