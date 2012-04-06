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
package org.janusproject.kernel.agentmemory;

import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.agentmemory.MemoryListener;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
final class MemoryStub implements Memory {
	
	/**
	 */
	public MemoryStub() {
		//
	}

	@Override
	public Object getMemorizedData(String id) {
		return null;
	}
	
	@Override
	public <T> T getMemorizedData(String id, Class<T> type) {
		return null;
	}

	@Override
	public boolean hasMemorizedData(String id) {
		return false;
	}

	@Override
	public boolean putMemorizedData(String id, Object value) {
		return false;
	}

	@Override
	public void removeMemorizedData(String id) {
		//
	}

	@Override
	public void addMemoryListener(MemoryListener listener) {
		//
	}

	@Override
	public void removeMemoryListener(MemoryListener listener) {
		//
	}
	
}
