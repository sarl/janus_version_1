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
package org.janusproject.demos.bdi.market.simple.capacity;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.AgentAddress;

/**
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $FullVersion$
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
