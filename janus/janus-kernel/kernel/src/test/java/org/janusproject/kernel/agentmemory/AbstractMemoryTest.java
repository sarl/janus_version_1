/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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

import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.agentmemory.AbstractMemory;
import org.janusproject.kernel.agentmemory.MemoryEvent;
import org.janusproject.kernel.agentmemory.MemoryListener;
import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AbstractMemoryTest extends TestCase {

	private static final String A1 = "A1"; //$NON-NLS-1$
	private static final Object REFERENCE = new Object();
	

	private AbstractMemory memory;
	private MemoryListenerStub listener;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.memory = new AbstractMemoryStub();
		this.listener = new MemoryListenerStub();
		this.memory.addMemoryListener(this.listener);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.memory.removeMemoryListener(this.listener);
		this.memory = null;
		this.listener = null;
		super.tearDown();
	}	
	
	/**
	 */
	public void testGetMemorizedDataStringClass() {
		assertSame(REFERENCE, this.memory.getMemorizedData(A1, Object.class));
		assertNull(this.memory.getMemorizedData(A1, Integer.class));
	}


	/**
	 */
	public void testFireKnowledgeEventStringMemoryEvent() {
		MemoryEvent event = new MemoryEvent(
				this.memory, 
				UUID.randomUUID().toString(),
				new Object(), new Object());
		this.memory.fireKnowledgeEvent(event.getKnowledgeIdentifier(), event);
		assertEquals(event.getKnowledgeIdentifier(), this.listener.changed);
	}
	
	/**
	 */
	public void testFireKnowledgeUpdateStringObjectObject() {
		String id = UUID.randomUUID().toString();
		this.memory.fireKnowledgeUpdate(id, new Object(), new Object());
		assertEquals(id, this.listener.changed);
	}

	/**
	 */
	public void testFireKnowledgeAddedStringObject() {
		String id = UUID.randomUUID().toString();
		this.memory.fireKnowledgeAdded(id, new Object());
		assertEquals(id, this.listener.changed);
	}

	/**
	 */
	public void testFireKnowledgeRemovedStringObject() {
		String id = UUID.randomUUID().toString();
		this.memory.fireKnowledgeRemoved(id, new Object());
		assertEquals(id, this.listener.changed);
	}

	/** Remove a listener on memory events.
	 * 
	 * @param listener is the listener
	 */
	public void removeMemoryListener(MemoryListener listener) {
		this.memory.removeMemoryListener(listener);
		this.memory.fireKnowledgeRemoved(UUID.randomUUID().toString(), new Object());
		assertNull(this.listener.changed);
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AbstractMemoryStub extends AbstractMemory {

		/**
		 */
		public AbstractMemoryStub() {
			//
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public Object getMemorizedData(String id) {
			return REFERENCE;
		}

		@Override
		public boolean hasMemorizedData(String id) {
			return true;
		}

		@Override
		public boolean putMemorizedData(String id, Object value) {
			return true;
		}

		@Override
		public void removeMemorizedData(String id) {
			//
		}
		
	}
		
}
