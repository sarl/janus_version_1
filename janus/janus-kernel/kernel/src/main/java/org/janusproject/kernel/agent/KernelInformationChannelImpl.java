/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.kernel.agent;

import java.util.Collection;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.channels.KernelInformationChannel;
import org.janusproject.kernel.channels.ChannelInteractable;

/**
 * Default implementation of the <code>KernelInformationChannel</code>.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated see {@link Kernel}
 */
@Deprecated
class KernelInformationChannelImpl implements KernelInformationChannel {
	
	private KernelAgent kernel = null;

	/**
	 * @param kernelAgent
	 */
	KernelInformationChannelImpl(KernelAgent kernelAgent) {
		this.kernel = kernelAgent;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Collection<AgentAddress> getAgents() {
		return this.kernel.getKernelContext().getAgentRepository().identifiers();
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isChannelIteractable(AgentAddress address) {
		return this.kernel.getKernelContext().getAgentRepository().get(address) instanceof ChannelInteractable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Address getChannelOwner() {
		if (this.kernel==null) return null;
		return this.kernel.getAddress();
	}
	


}
