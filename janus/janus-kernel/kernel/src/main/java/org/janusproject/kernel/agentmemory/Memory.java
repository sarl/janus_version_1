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

/**
 * Agent memory.
 * <p>
 * An agent memory contains the knowledge of an agent.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Memory {

	/** Replies the knowledge with the given identifier.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @return the data or <code>null</code>
	 */
	public Object getMemorizedData(String id);
	
	/** Replies the knowledge with the given identifier.
	 *
	 * @param <T> is the type of the data.
	 * @param id is the identifier of the knowledge.
	 * @param type is the type of the data.
	 * @return the data or <code>null</code> if not set or not of the given type.
	 */
	public <T> T getMemorizedData(String id, Class<T> type);

	/** Replies if a knowledge with the given identifier is existing in the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @return <code>true</code> if the knowledge is existing, otherwise <code>false</code>
	 */
	public boolean hasMemorizedData(String id);

	/** Put a knowledge in the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @param value is the data to memorize.
	 * @return <code>true</code> if the knowledge was successfully saved, otherwise <code>false</code>
	 */
	public boolean putMemorizedData(String id, Object value);

	/** Remove a knowledge from the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 */
	public void removeMemorizedData(String id);

	/** Add a listener on memory events.
	 * 
	 * @param listener is the listener
	 */
	public void addMemoryListener(MemoryListener listener);

	/** Add a listener on memory events.
	 * 
	 * @param listener is the listener
	 */
	public void removeMemoryListener(MemoryListener listener);
	
}
