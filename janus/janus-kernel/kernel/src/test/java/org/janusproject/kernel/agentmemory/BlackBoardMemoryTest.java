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

import org.janusproject.kernel.agentmemory.BlackBoardMemory;
import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BlackBoardMemoryTest extends TestCase {

	private static final String A1 = "A1"; //$NON-NLS-1$

	private BlackBoardMemory memory;
	private MemoryListenerStub listener;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.memory = new BlackBoardMemory();
		this.listener = new MemoryListenerStub();
		this.memory.addMemoryListener(this.listener);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.memory.removeMemoryListener(this.listener);
		this.listener = null;
		this.memory = null;
		super.tearDown();
	}	

	/**
	 */
	public void testGetMemorizedDataString() {
		assertNull(this.memory.getMemorizedData(A1));
	}
	
	/**
	 */
	public void testPutMemorizedDataStringObject() {
		this.listener.assertEquals(null);

		String id1 = UUID.randomUUID().toString();
		Object v1 = new Object();
		assertNull(this.memory.getMemorizedData(id1));
		assertTrue(this.memory.putMemorizedData(id1, v1));
		assertNotNull(this.memory.getMemorizedData(id1));
		assertSame(v1, this.memory.getMemorizedData(id1));

		this.listener.assertEquals(id1);
		
		String id2 = UUID.randomUUID().toString();
		Object v2 = new Object();
		assertNull(this.memory.getMemorizedData(id2));
		assertTrue(this.memory.putMemorizedData(id2, v2));
		assertNotNull(this.memory.getMemorizedData(id2));
		assertSame(v2, this.memory.getMemorizedData(id2));

		this.listener.assertEquals(id2);

		assertTrue(this.memory.putMemorizedData(id2, v1));
		assertNotNull(this.memory.getMemorizedData(id2));
		assertSame(v1, this.memory.getMemorizedData(id2));

		this.listener.assertEquals(id2);
	}

	/**
	 */
	public void testHasMemorizedDataString() {
		String id1 = UUID.randomUUID().toString();
		Object v1 = new Object();
		String id2 = UUID.randomUUID().toString();
		Object v2 = new Object();

		assertFalse(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));
		assertFalse(this.memory.hasMemorizedData(A1));
		
		this.listener.assertEquals(null);

		this.memory.putMemorizedData(id1, v1);

		this.listener.assertEquals(id1);

		assertTrue(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));
		assertFalse(this.memory.hasMemorizedData(A1));

		this.memory.putMemorizedData(id2, v2);
		
		this.listener.assertEquals(id2);

		assertTrue(this.memory.hasMemorizedData(id1));
		assertTrue(this.memory.hasMemorizedData(id2));		
		assertFalse(this.memory.hasMemorizedData(A1));
	}

	/**
	 */
	public void testRemoveMemorizedDataString() {
		String id1 = UUID.randomUUID().toString();
		Object v1 = new Object();
		String id2 = UUID.randomUUID().toString();
		Object v2 = new Object();

		this.memory.putMemorizedData(id1, v1);
		this.memory.putMemorizedData(id2, v2);
		
		this.memory.removeMemorizedData(A1);
		
		assertTrue(this.memory.hasMemorizedData(id1));
		assertTrue(this.memory.hasMemorizedData(id2));		
		assertFalse(this.memory.hasMemorizedData(A1));

		this.memory.removeMemorizedData(id1);
		
		assertFalse(this.memory.hasMemorizedData(id1));
		assertTrue(this.memory.hasMemorizedData(id2));		
		assertFalse(this.memory.hasMemorizedData(A1));
	}

}
