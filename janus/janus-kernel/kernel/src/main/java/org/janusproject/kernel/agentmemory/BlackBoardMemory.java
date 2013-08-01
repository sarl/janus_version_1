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

import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of an agent memory using a black board.
 * <p>
 * <code>BlackBoardMemory</code> is not synchronized and uses a {@link TreeMap}.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BlackBoardMemory extends AbstractMemory {

	private final Map<String,Object> blackBoard = new TreeMap<String,Object>();
	
	/**
	 * Create an empty black board.
	 */
	public BlackBoardMemory() {
		//
	}
	
	/** Replies the knowledge with the given identifier.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @return the data or <code>null</code>
	 */
	@Override
	public Object getMemorizedData(String id) {
		return this.blackBoard.get(id);
	}
	
	/** Replies if a knowledge with the given identifier is existing in the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @return <code>true</code> if the knowledge is existing, otherwise <code>false</code>
	 */
	@Override
	public boolean hasMemorizedData(String id) {
		return this.blackBoard.containsKey(id);
	}

	/** Put a knowledge in the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 * @param value is the data to memorize.
	 * @return <code>true</code> if the knowledge was successfully saved, otherwise <code>false</code>
	 */
	@Override
	public boolean putMemorizedData(String id, Object value) {
		if (value==null) {
			Object oldValue = this.blackBoard.remove(id);
			if (oldValue!=null)
				fireKnowledgeRemoved(id, oldValue);
		}
		else {
			Object oldValue = this.blackBoard.put(id, value);
			if (oldValue==null)
				fireKnowledgeAdded(id, value);
			else 
				fireKnowledgeUpdate(id, oldValue, value);
		}
		return true;
	}

	/** Remove a knowledge from the memory.
	 * 
	 * @param id is the identifier of the knowledge.
	 */
	@Override
	public void removeMemorizedData(String id) {
		Object oldValue = this.blackBoard.remove(id);
		if (oldValue!=null)
			fireKnowledgeRemoved(id, oldValue);
	}

}
