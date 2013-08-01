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
package org.janusproject.kernel.crio.core;

import java.lang.ref.WeakReference;
import java.util.UUID;

import org.janusproject.kernel.address.AgentAddress;

/**
 * This is the address of an agent in the kernel community but
 * with a direct link to the identified entity.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
class PlayerAddress extends AgentAddress {

	private static final long serialVersionUID = -3278716534890058048L;

	private transient WeakReference<RolePlayer> player = null;
	
	/**
	 * @param player is the player that is identifier by this address.
	 */
	public PlayerAddress(RolePlayer player) {
		this(player, null, null);
	}
	
	/**
	 * @param player is the player that is identifier by this address.
	 * @param id is the identifier of the agent.
	 */
	public PlayerAddress(RolePlayer player, UUID id) {
		this(player, id, null);
	}

	/**
	 * @param player is the player that is identifier by this address.
	 * @param id is the identifier of the agent.
	 * @param name is the name of the address/agent.
	 */
	public PlayerAddress(RolePlayer player, UUID id, String name) {
		super(id, name);
		bind(player);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void setName(String iname) {
		String n = getName();
		if ((n==null && iname!=null) || (n!=null && !n.equals(iname))) {
			super.setName(iname);
			RolePlayer player = getRolePlayer();
			if (player!=null) {
				player.setName(iname);
			}
		}
	}
	
	/** Remove the link between this address and its player.
	 */
	synchronized void unbind() {
		this.player = null;
	}
	
	/** Create the link between this address and its player.
	 * 
	 * @param player is the player that is identifier by this address.
	 */
	synchronized void bind(RolePlayer player) {
		if (player==null)
			this.player = null;
		else
			this.player = new WeakReference<RolePlayer>(player);
	}

	/** Replies the role player associated to this address.
	 * 
	 * @return the role player associated to this address.
	 */
	synchronized RolePlayer getRolePlayer() {
		if (this.player==null) return null;
		return this.player.get();
	}

}