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
package org.janusproject.kernel.crio.organization;

import java.util.EventObject;

import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * Event about groups.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class GroupEvent extends EventObject {

	private static final long serialVersionUID = -3820066149570363398L;
	
	private final Group group;
	
	/**
	 * @param source is the source of the event.
	 * @param group is the group.
	 */
	public GroupEvent(Object source, Group group) {
		super(source);
		assert(group!=null);
		this.group = group;
	}
	
	/** Replies the group.
	 * 
	 * @return the group.
	 */
	public Group getGroup() {
		return this.group;
	}

	/** Replies the address of the group.
	 * 
	 * @return the address of the group.
	 */
	public GroupAddress getGroupAddress() {
		return getGroup().getAddress();
	}

}