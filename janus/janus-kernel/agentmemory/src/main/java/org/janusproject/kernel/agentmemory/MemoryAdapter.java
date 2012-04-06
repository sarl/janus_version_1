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

import java.util.Set;
import java.util.TreeSet;

/**
 * Listener on agent memory events.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MemoryAdapter implements MemoryListener {

	private final Set<String> eventBuffer = new TreeSet<String>();
	
	/**
	 */
	public MemoryAdapter() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onKnownledgeChanged(MemoryEvent event) {
		this.eventBuffer.add(event.getKnowledgeIdentifier());
	}

	/**
	 * Replies if the knowledge with the given identifier has changed.
	 * 
	 * @param identifier is the identifier of the knowledge.
	 * @return <code>true</code> if the knowledge has changed, otherwise <code>false</code>
	 */
	public boolean hasKnowledgeChanged(String identifier) {
		return this.eventBuffer.contains(identifier);
	}
	
	/** Mark the knowledge with the given identifier as not-changed.
	 * 
	 * @param identifier is the identifier of the knowledge.
	 */
	public void markAsNotChanged(String identifier) {
		this.eventBuffer.remove(identifier);
	}

	/** Mark all the knowledge as not-changed.
	 */
	public void markAsNotChanged() {
		this.eventBuffer.clear();
	}

}
