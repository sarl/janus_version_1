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
package org.janusproject.kernel.channels;

import java.util.Set;
import java.util.UUID;

/**
 * Agents that understand channel communications must implement this interface.
 * <p>
 * All agents that implement this interface will be wrapped and informed to
 * {@link ChannelInteractableListener}s.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * 
 */
public interface ChannelInteractable {

	/**
	 * Replies an unique identifer that may represents
	 * this channel interactable.
	 * 
	 * @return the uuid of the channel interactable object.
	 * @since 0.5
	 */
	public UUID getUUID();

	/**
	 * Replies the communication channels supported by this agent.
	 * 
	 * @return the list of the supported channels
	 */
	public Set<? extends Class<? extends Channel>> getSupportedChannels();

	/**
	 * Intanciates a channel of a given class.
	 * <p>
	 * The parameters depend on the requested channel. Please refer to the
	 * channel's documentation for further details.
	 * 
	 * @param <C> is the type of the channel to reply.
	 * @param channelClass is the type of the channel to reply.
	 * @param params is the parameters to pass to the channel instance.
	 * @return a Channel instance or <code>null</code> if not supported.
	 * @throws IllegalArgumentException
	 *             if the params do not comply to the channel's required
	 *             creations parameters.
	 */
	public <C extends Channel> C getChannel(Class<C> channelClass,
			Object... params);
}
