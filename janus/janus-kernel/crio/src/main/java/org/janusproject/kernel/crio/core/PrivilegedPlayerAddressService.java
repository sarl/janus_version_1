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

import org.janusproject.kernel.address.AgentAddress;

/**
 * This interface provides privilegied access to the services
 * related to the role player addresses. 
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public interface PrivilegedPlayerAddressService {
	
	/** Bind the given address and the given role player.
	 * 
	 * @param address
	 * @param player
	 */
	public void bind(AgentAddress address, RolePlayer player);

	/** Unbind the given address and the given role player.
	 * 
	 * @param address
	 */
	public void unbind(AgentAddress address);
	
	/** Replies the player binded with the given address.
	 * 
	 * @param address
	 * @return the player binded with the given address; or <code>null</code>
	 * if no player is linked to the address.
	 */
	public RolePlayer getBindedPlayer(AgentAddress address);

}
