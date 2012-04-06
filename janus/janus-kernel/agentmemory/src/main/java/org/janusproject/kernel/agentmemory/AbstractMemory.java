/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.agentmemory;

import org.janusproject.kernel.util.event.ListenerCollection;

/**
 * Abstract implementation of an agent memory.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractMemory implements Memory {

	private ListenerCollection<MemoryListener> listeners = null;

	/**
	 * Notify listeners on knownledge change.
	 * @param id is the identifier of the changed knowledge.
	 * @param event describes the change.
	 */
	protected void fireKnowledgeEvent(String id, MemoryEvent event) {
		if (this.listeners!=null && !this.listeners.isEmpty()) {
			for(MemoryListener listener : this.listeners.getListeners(MemoryListener.class)) {
				listener.onKnownledgeChanged(event);
			}
		}
	}
	
	/** Notify listeners on knownledge update.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @param oldValue is the old value of the knowledge.
	 * @param newValue is the new value of the knowledge.
	 */
	protected void fireKnowledgeUpdate(String id, Object oldValue, Object newValue) {
		fireKnowledgeEvent(id, new MemoryEvent(this,id,oldValue,newValue));
	}

	/** Notify listeners on knownledge addition.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @param newValue is the new value of the knowledge.
	 */
	protected void fireKnowledgeAdded(String id, Object newValue) {
		fireKnowledgeEvent(id, new MemoryEvent(this,id,null,newValue));
	}

	/** Notify listeners on knownledge removal.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @param oldValue is the old value of the knowledge.
	 */
	protected void fireKnowledgeRemoved(String id, Object oldValue) {
		fireKnowledgeEvent(id, new MemoryEvent(this,id,oldValue,null));
	}

	/** Remove a listener on memory events.
	 * 
	 * @param listener is the listener
	 */
	@Override
	public void removeMemoryListener(MemoryListener listener) {
		if (this.listeners!=null) {
			this.listeners.remove(MemoryListener.class, listener);
			if (this.listeners.isEmpty()) this.listeners = null;
		}
	}

	/** Add a listener on memory events.
	 * 
	 * @param listener is the listener
	 */
	@Override
	public void addMemoryListener(MemoryListener listener) {
		if (this.listeners==null) 
			this.listeners = new ListenerCollection<MemoryListener>();
		this.listeners.add(MemoryListener.class, listener);
	}

	/** Replies the knowledge with the given identifier.
	 *
	 * @param <T> is the type of the data.
	 * @param id is the identifier of the knowledge.
	 * @param type is the type of the data.
	 * @return the data or <code>null</code> if not set or not of the given type.
	 */
	@Override
	public final <T> T getMemorizedData(String id, Class<T> type) {
		assert(type!=null);
		Object o = getMemorizedData(id);
		if (o!=null && type.isInstance(o))
			return type.cast(o);
		return null;
	}

}
