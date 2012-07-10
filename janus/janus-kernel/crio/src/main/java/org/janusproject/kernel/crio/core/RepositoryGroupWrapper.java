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

import org.janusproject.kernel.crio.organization.Group;
import org.janusproject.kernel.crio.organization.GroupEvent;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.repository.RepositoryChangeEvent;
import org.janusproject.kernel.repository.RepositoryChangeListener;
import org.janusproject.kernel.util.event.ListenerCollection;

/**
 * Wrapper that translate group repository events
 * into group events.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
class RepositoryGroupWrapper implements RepositoryChangeListener {

	private final ListenerCollection<GroupListener> listeners = new ListenerCollection<GroupListener>();
	
	/**
	 */
	public RepositoryGroupWrapper() {
		//
	}
	
	/** Replies if this wrapper contains a listener.
	 * 
	 * @return <code>true</code> if the wrapper is empty;
	 * <code>false</code> if a listener is registered inside
	 * this wrapper.
	 */
	public boolean isEmpty() {
		return this.listeners.isEmpty();
	}
	
	/** Clear this wrapper.
	 */
	public void clear() {
		this.listeners.clear();
	}
	
	/**
	 * Add listener on creation or disappearing
	 * of a group from the system.
	 * 
	 * @param listener
	 */
	public void addGroupListener(GroupListener listener) {
		this.listeners.add(GroupListener.class, listener);
	}

	/**
	 * Add listener on creation or disappearing
	 * of a group from the system.
	 * 
	 * @param listener
	 */
	public void removeGroupListener(GroupListener listener) {
		this.listeners.remove(GroupListener.class, listener);
	}

	private void fireCreation(Group group) {
		GroupEvent event = new GroupEvent(this, group);
		for(GroupListener listener : this.listeners.getListeners(GroupListener.class)) {
			listener.groupCreated(event);
		}
	}
	
	private void fireDeletion(Group group) {
		GroupEvent event = new GroupEvent(this, group);
		for(GroupListener listener : this.listeners.getListeners(GroupListener.class)) {
			listener.groupDestroyed(event);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repositoryChanged(RepositoryChangeEvent evt) {
		switch(evt.getType()) {
		case ADD:
			fireCreation(((KernelScopeGroup)evt.getNewValue()).toGroup(false));
			break;
		case REMOVE:
			fireDeletion(((KernelScopeGroup)evt.getOldValue()).toGroup(false));
			break;
		case UPDATE:
		default:
		}
	}
	
}