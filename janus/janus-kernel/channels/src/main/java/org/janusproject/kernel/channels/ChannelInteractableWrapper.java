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
package org.janusproject.kernel.channels;

import java.util.Set;
import java.util.UUID;

/**
 * A simple wrapper to prevent casting to the real agent object.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ChannelInteractableWrapper implements ChannelInteractable {
	
	private final ChannelInteractable channelInteractableObject;
	
	/**
	 * @param ci is the channel interactable to wrap.
	 */
	public ChannelInteractableWrapper(ChannelInteractable ci){
		this.channelInteractableObject = ci;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID getUUID() {
		return this.channelInteractableObject.getUUID();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Set<? extends Class<? extends Channel>> getSupportedChannels() {
		return this.channelInteractableObject.getSupportedChannels();
	}

	/** {@inheritDoc}
	 */
	@Override
	public <C extends Channel> C getChannel(Class<C> channelClass,
			Object... params) {
		return this.channelInteractableObject.getChannel(channelClass, params);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.channelInteractableObject.hashCode();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return this.channelInteractableObject.equals(obj);
	}
}
