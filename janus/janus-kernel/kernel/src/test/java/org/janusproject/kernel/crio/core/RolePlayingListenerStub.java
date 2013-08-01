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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.role.RolePlayingEvent;
import org.janusproject.kernel.crio.role.RolePlayingListener;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class RolePlayingListenerStub implements RolePlayingListener {

	private final List<RolePlayingEvent> takes = new ArrayList<RolePlayingEvent>();
	private final List<RolePlayingEvent> releases = new ArrayList<RolePlayingEvent>();
	
	/**
	 */
	public RolePlayingListenerStub() {
		//
	}
	
	/**
	 */
	public void assertNull() {
		if (!this.takes.isEmpty()) {
			Assert.fail("unexpected role taking event"); //$NON-NLS-1$
		}
		else if (!this.releases.isEmpty()) {
			Assert.fail("unexpected role release event"); //$NON-NLS-1$
		}
	}
	
	private static void assertEvent(List<RolePlayingEvent> list, Class<? extends Role> role, GroupAddress group, AgentAddress player) {
		Iterator<RolePlayingEvent> iterator = list.iterator();
		RolePlayingEvent e;
		while (iterator.hasNext()) {
			e = iterator.next();
			if (e!=null
				&& e.getGroup().equals(group)
				&& e.getPlayer().equals(player)
				&& e.getRole().equals(role)) {
				iterator.remove();
				return;
			}
		}
		Assert.fail("event not found"); //$NON-NLS-1$
	}

	/**
	 * @param role
	 * @param group
	 * @param player
	 */
	public void assertTaken(Class<? extends Role> role, GroupAddress group, AgentAddress player) {
		assertEvent(this.takes, role, group, player);
	}

	/**
	 * @param role
	 * @param group
	 * @param player
	 */
	public void assertReleased(Class<? extends Role> role, GroupAddress group, AgentAddress player) {
		assertEvent(this.releases, role, group, player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleReleased(RolePlayingEvent event) {
		this.releases.add(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleTaken(RolePlayingEvent event) {
		this.takes.add(event);
	}
	
}