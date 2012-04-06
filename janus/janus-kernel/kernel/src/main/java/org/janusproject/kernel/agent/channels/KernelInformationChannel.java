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
package org.janusproject.kernel.agent.channels;

import java.util.Collection;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.Channel;

/**
 * This interfaces describes the functions of a channel which is providing
 * informations on the kernel.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated see {@link Kernel}
 */
@Deprecated
public interface KernelInformationChannel extends Channel {

	/**
	 * Replies the agents launched on the kernel.
	 * 
	 * @return the agents launched on the kernel.
	 */
	public abstract Collection<AgentAddress> getAgents();

	/**
	 * Replies if the given address corresponds to an agent which is able to use
	 * a channel.
	 * 
	 * @param address
	 * @return <code>true</code> if the address is binded to an channel
	 *         interactable, otherwise <code>false</code>.
	 */
	public abstract boolean isChannelIteractable(AgentAddress address);

}