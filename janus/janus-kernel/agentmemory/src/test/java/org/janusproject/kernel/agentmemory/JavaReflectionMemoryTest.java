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

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.agentmemory.JavaReflectionMemory;
import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JavaReflectionMemoryTest extends TestCase {

	private static final Object A1_VALUE = new Object();
	
	private static final String A1 = "A1"; //$NON-NLS-1$
	private static final String A2 = "A2"; //$NON-NLS-1$

	private ReflectionStub referent;
	private JavaReflectionMemory<ReflectionStub> memory;
	private MemoryListenerStub listener;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.referent = new ReflectionStub();
		this.memory = new JavaReflectionMemory<ReflectionStub>(this.referent);
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
		this.referent = null;
		super.tearDown();
	}	

	/**
	 */
	public void testGetGetter() {
		Method meth = this.memory.getGetter(A1);
		assertNotNull(meth);
		assertEquals("getKA1", meth.getName()); //$NON-NLS-1$

		meth = this.memory.getGetter(A2);
		assertNull(meth);
	}
	
	/**
	 */
	public void testGetSetterString() {
		Method meth = this.memory.getSetter(A1);
		assertNotNull(meth);
		assertEquals("setKA1", meth.getName()); //$NON-NLS-1$

		meth = this.memory.getGetter(A2);
		assertNull(meth);
	}

	/**
	 */
	public void testGetMemorizedDataString() {
		assertSame(A1_VALUE, this.memory.getMemorizedData(A1));
	}
	
	/**
	 */
	public void testPutMemorizedDataStringObject() {
		this.listener.assertEquals(null);

		String id1 = UUID.randomUUID().toString();
		Object v1 = new Object();
		assertNull(this.memory.getMemorizedData(id1));
		assertFalse(this.memory.putMemorizedData(id1, v1));
		assertNull(this.memory.getMemorizedData(id1));

		this.listener.assertEquals(null);

		assertSame(A1_VALUE, this.memory.getMemorizedData(A1));
		assertTrue(this.memory.putMemorizedData(A1, v1));
		assertNotNull(this.memory.getMemorizedData(A1));
		assertSame(v1, this.memory.getMemorizedData(A1));

		this.listener.assertEquals(A1);
		
		String id2 = UUID.randomUUID().toString();
		Object v2 = new Object();
		assertNull(this.memory.getMemorizedData(id2));
		assertFalse(this.memory.putMemorizedData(id2, v2));
		assertNull(this.memory.getMemorizedData(id2));

		this.listener.assertEquals(null);

		assertNull(this.memory.getMemorizedData(A2));
		assertFalse(this.memory.putMemorizedData(A2, v1));
		assertNull(this.memory.getMemorizedData(A2));

		this.listener.assertEquals(null);
	}

	/**
	 */
	public void testHasMemorizedDataString() {
		String id1 = UUID.randomUUID().toString();
		Object v1 = new Object();
		String id2 = UUID.randomUUID().toString();

		this.listener.assertEquals(null);
		assertFalse(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));
		assertTrue(this.memory.hasMemorizedData(A1));
		assertFalse(this.memory.hasMemorizedData(A2));
		
		this.memory.putMemorizedData(id1, v1);

		this.listener.assertEquals(null);
		assertFalse(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));
		assertTrue(this.memory.hasMemorizedData(A1));
		assertFalse(this.memory.hasMemorizedData(A2));

		this.memory.putMemorizedData(A1, v1);

		this.listener.assertEquals(A1);
		assertFalse(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));
		assertTrue(this.memory.hasMemorizedData(A1));
		assertFalse(this.memory.hasMemorizedData(A2));

		this.memory.putMemorizedData(A2, v1);

		this.listener.assertEquals(null);
		assertFalse(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));
		assertTrue(this.memory.hasMemorizedData(A1));
		assertFalse(this.memory.hasMemorizedData(A2));
	}

	/**
	 */
	public void testRemoveMemorizedDataString() {
		String id1 = UUID.randomUUID().toString();
		Object v1 = new Object();
		String id2 = UUID.randomUUID().toString();
		Object v2 = new Object();

		this.listener.assertEquals(null);
		assertFalse(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));		
		assertTrue(this.memory.hasMemorizedData(A1));
		assertFalse(this.memory.hasMemorizedData(A2));

		this.memory.putMemorizedData(id1, v1);
		this.memory.putMemorizedData(id2, v2);
		
		this.memory.removeMemorizedData(A1);
		
		this.listener.assertEquals(A1);
		assertFalse(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));		
		assertFalse(this.memory.hasMemorizedData(A1));
		assertFalse(this.memory.hasMemorizedData(A2));

		this.memory.removeMemorizedData(id1);
		
		this.listener.assertEquals(null);
		assertFalse(this.memory.hasMemorizedData(id1));
		assertFalse(this.memory.hasMemorizedData(id2));		
		assertFalse(this.memory.hasMemorizedData(A1));
		assertFalse(this.memory.hasMemorizedData(A2));
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public class ReflectionStub {

		@SuppressWarnings("synthetic-access")
		private Object a1 = A1_VALUE;
		
		/**
		 */
		public ReflectionStub() {
			//
		}
		
		/**
		 * @return a value
		 */
		public Object getKA1() {
			return this.a1;
		}

		/**
		 * @param val
		 */
		public void setKA1(Object val) {
			this.a1 = val;
		}

	}
	
}
