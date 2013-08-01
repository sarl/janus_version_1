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

import org.janusproject.kernel.crio.organization.GroupEvent;
import org.janusproject.kernel.crio.organization.GroupListener;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class GroupListenerStub implements GroupListener {

	private final List<GroupEvent> creation = new ArrayList<GroupEvent>();
	private final List<GroupEvent> deletion = new ArrayList<GroupEvent>();
	
	/**
	 */
	public GroupListenerStub() {
		//
	}
	
	/**
	 */
	public void assertNull() {
		if (!this.creation.isEmpty()) {
			Assert.fail("unexpected group creation event"); //$NON-NLS-1$
		}
		else if (!this.deletion.isEmpty()) {
			Assert.fail("unexpected group deletion event"); //$NON-NLS-1$
		}
	}
	
	private static void assertEvent(List<GroupEvent> list, GroupAddress address) {
		Iterator<GroupEvent> iterator = list.iterator();
		GroupEvent e;
		while (iterator.hasNext()) {
			e = iterator.next();
			if (e!=null
				&& e.getGroupAddress().equals(address)
				&& e.getGroup().getAddress().equals(address)) {
				iterator.remove();
				return;
			}
		}
		Assert.fail("event not found"); //$NON-NLS-1$
	}

	/**
	 * @param address
	 */
	public void assertCreation(GroupAddress address) {
		assertEvent(this.creation, address);
	}

	/**
	 * @param address
	 */
	public void assertDeletion(GroupAddress address) {
		assertEvent(this.deletion, address);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void groupCreated(GroupEvent event) {
		this.creation.add(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void groupDestroyed(GroupEvent event) {
		this.deletion.add(event);
	}
	
}