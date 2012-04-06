/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, 2012 Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.demos.bdi.market.simple.capacity;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.kernel.address.AgentAddress;

/**
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Proposal
{
	private final double duration;
	private final double cost;
	private final AgentAddress provider;
	
	/**
	 * @param provider
	 * @param iduration
	 * @param icost
	 */
	public Proposal(AgentAddress provider, double iduration, double icost) {
		this.provider = provider;
		this.duration = iduration;
		this.cost = icost;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return this.cost;
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return this.duration;
	}
	
	@Override
	public String toString() {
		return Locale.getString(Proposal.class, "TOSTRING",+this.cost,this.duration); //$NON-NLS-1$
	}
	
	/**
	 * @return provider
	 */
	public AgentAddress getProvider() {
		return this.provider;
	}

}
