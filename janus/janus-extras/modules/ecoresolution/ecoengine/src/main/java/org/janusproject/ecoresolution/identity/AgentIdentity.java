/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.ecoresolution.identity;

import org.janusproject.kernel.address.AgentAddress;

/** Identity of an eco-entity based on agent addressing features. 
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public final class AgentIdentity extends EcoIdentity {
	
	private static final long serialVersionUID = -4278765628193329523L;
	
	private final AgentAddress address;
	
	/**
	 * @param address is the address of the agent.
	 */
	public AgentIdentity(AgentAddress address) {
		super(address.getUUID());
		this.address = address;
	}
	
	/** Replies the address of the agent.
	 * 
	 * @return the address of the agent.
	 */
	public AgentAddress getAgentAddress() {
		return this.address;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof AgentAddress) {
			return this.address.equals(o);
		}
		if (o instanceof AgentIdentity) {
			return this.address.equals(((AgentIdentity)o).address);
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.address.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String name = this.address.getName();
		if (name!=null && !"".equals(name)) { //$NON-NLS-1$
			return name;
		}
		return this.address.getUUID().toString();
	}
	
}