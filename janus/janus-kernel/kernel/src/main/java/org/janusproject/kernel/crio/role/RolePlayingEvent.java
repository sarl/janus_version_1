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
package org.janusproject.kernel.crio.role;

import java.util.EventObject;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.channels.ChannelInteractableWrapper;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.Group;

/**
 * Event about role playing.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RolePlayingEvent extends EventObject {

	private static final long serialVersionUID = 6849051717834356770L;
	
	private final RoleAddress roleAddress;
	private final Group group;
	private final ChannelInteractable channelInteractable;
	
	/**
	 * @param role is the concerned role
	 * @param group is the group.
	 * @param channelInteractable is the channel source associated to the role, if it exists.
	 */
	public RolePlayingEvent(RoleAddress role, Group group, ChannelInteractable channelInteractable) {
		super(role.getPlayer());
		assert(role!=null);
		assert(group!=null);
		this.roleAddress = role;
		this.group = group;
		this.channelInteractable = channelInteractable;
	}
	
	/** Replies the role player.
	 * 
	 * @return the role player.
	 */
	public AgentAddress getPlayer() {
		return (AgentAddress)getSource();
	}
	
	/** Replies the role concerned by this event.
	 * 
	 * @return a role.
	 */
	public Class<? extends Role> getRole() {
		return this.roleAddress.getRole();
	}
	
	/** Replies the role concerned by this event.
	 * 
	 * @return a role.
	 * @since 0.5
	 */
	public RoleAddress getRoleAddress() {
		return this.roleAddress;
	}

	/** Replies the group of the role.
	 * 
	 * @return a group.
	 */
	public Group getGroup() {
		return this.group;
	}
	
	/** Replies the channel interactable interface of the role supported by this event.
	 * 
	 * @return the channel interactable interface, or <code>null</code> if the role
	 * in this event is not channel interactable.
	 */
	public ChannelInteractable getChannelInteractable() {
		if (this.channelInteractable!=null) {
			return new ChannelInteractableWrapper(this.channelInteractable);
		}
		return null;
	}

}