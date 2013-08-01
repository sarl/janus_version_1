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

import java.util.EventObject;

/**
 * Describes a memory event.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MemoryEvent extends EventObject {

	private static final long serialVersionUID = -8688415157182807078L;
	
	private final String identifier;
	private final Object newValue;
	private final Object oldValue;
	
	/**
	 * @param source is the source of the event
	 * @param identifier is the identifier of the changed knowledge
	 * @param oldValue is the old value of the knowledge
	 * @param newValue is then ew value of the knowledge
	 */
	public MemoryEvent(Memory source, String identifier, Object oldValue, Object newValue) {
		super(source);
		this.identifier = identifier;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/** Replies the memory at the source of this event.
	 * 
	 * @return a memory
	 */
	public Memory getMemory() {
		return (Memory)getSource();
	}
	
	/** Replies the identifier of the changed or removed knowledge.
	 * 
	 * @return an identifier
	 */
	public String getKnowledgeIdentifier() {
		return this.identifier;
	}
	
	/** Replies the old value of the knowledge.
	 * 
	 * @return the old value or <code>null</code>
	 */
	public Object getOldValue() {
		return this.oldValue;
	}

	/** Replies the knowledge's old value of the given type.
	 *
	 * @param <T> is the type of the value to reply.
	 * @param type is the type of the value to reply.
	 * @return the old value or <code>null</code>
	 */
	public <T> T getOldValue(Class<T> type) {
		if (this.oldValue!=null && type.isInstance(this.oldValue))
			return type.cast(this.oldValue);
		return null;
	}

	/** Replies the new value of the knowledge.
	 * 
	 * @return the new value or <code>null</code>
	 */
	public Object getNewValue() {
		return this.newValue;
	}

	/** Replies the knowledge's new value of the given type.
	 *
	 * @param <T> is the type of the value to reply.
	 * @param type is the type of the value to reply.
	 * @return the old value or <code>null</code>
	 */
	public <T> T getNewValue(Class<T> type) {
		if (this.newValue!=null && type.isInstance(this.newValue))
			return type.cast(this.newValue);
		return null;
	}
	
}
