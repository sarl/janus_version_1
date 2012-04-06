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
package org.janusproject.kernel.util.directaccess;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.janusproject.kernel.util.directaccess.AsynchronousThreadSafeCollection;
import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class NoAutoOrderDisableAsynchronousThreadSafeCollectionTest extends TestCase {

	private AsynchronousThreadSafeCollection<DataStub> collection;
	private DataStub m1, m2;
	private ListenerStub listener;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.m1 = new DataStub(10);
		this.m2 = new DataStub(1);

		this.collection = new AsynchronousThreadSafeCollection<DataStub>(DataStub.class);
		this.collection.setAutoApplyEnabled(false);
		this.collection.setSetBehaviorEnabled(false);
		
		this.listener = new ListenerStub();
		this.collection.addAsynchronousThreadSafeCollectionListener(this.listener);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.collection.removeAsynchronousThreadSafeCollectionListener(this.listener);
		this.listener = null;
		this.collection.clear();
		this.collection.applyChanges(false);
		this.collection = null;
		this.m1 = this.m2 = null;
		super.tearDown();
	}

	/**
	 */
	public void testIsIterated() {
		assertFalse(this.collection.isIterated());
	}
	
	/**
	 */
	public void testGetElementType() {
		assertEquals(DataStub.class, this.collection.getElementType());
	}
	
	/**
	 */
	public void testIsEmpty() {
		assertTrue(this.collection.isEmpty());
		
		assertTrue(this.collection.add(this.m1));
		
		assertTrue(this.collection.isEmpty());
		
		assertTrue(this.collection.applyChanges(false));
		
		assertFalse(this.collection.isEmpty());
	}

	/**
	 */
	public void testClear() {
		assertTrue(this.collection.isEmpty());
	
		assertTrue(this.collection.add(this.m1));
		assertTrue(this.collection.add(this.m2));
		assertTrue(this.collection.isEmpty());
		assertTrue(this.collection.applyChanges(false));
		assertFalse(this.collection.isEmpty());
		this.listener.reset();

		this.collection.clear();

		assertFalse(this.collection.isEmpty());
		this.listener.assertNull();
		assertTrue(this.collection.applyChanges(false));
		assertTrue(this.collection.isEmpty());
		this.listener.assertRemoved(this.m1);
		this.listener.assertRemoved(this.m2);
		this.listener.assertNull();

		assertTrue(this.collection.add(this.m1));
		assertTrue(this.collection.add(this.m2));
		assertTrue(this.collection.applyChanges(false));
		assertFalse(this.collection.isEmpty());
		this.listener.reset();

		this.collection.clear();

		assertFalse(this.collection.isEmpty());
		this.listener.assertNull();
		assertTrue(this.collection.applyChanges(false));
		assertTrue(this.collection.isEmpty());
		this.listener.assertRemoved(this.m1);
		this.listener.assertRemoved(this.m2);
		this.listener.assertNull();
	}

	/**
	 */
	public void testContainsObject() {
		assertFalse(this.collection.contains(this.m1));
		assertFalse(this.collection.contains(this.m2));

		assertTrue(this.collection.add(this.m1));

		assertFalse(this.collection.contains(this.m1));
		assertFalse(this.collection.contains(this.m2));

		assertTrue(this.collection.applyChanges(false));

		assertTrue(this.collection.add(this.m2));

		assertTrue(this.collection.contains(this.m1));
		assertFalse(this.collection.contains(this.m2));

		assertTrue(this.collection.applyChanges(false));

		assertTrue(this.collection.contains(this.m1));
		assertTrue(this.collection.contains(this.m2));
	}

	/**
	 */
	public void testSize() {
		assertEquals(0, this.collection.size());

		assertTrue(this.collection.add(this.m1));

		assertEquals(0, this.collection.size());

		assertTrue(this.collection.applyChanges(false));

		assertEquals(1, this.collection.size());
	}
	
	/**
	 */
	public void testGet() {
		try {
			this.collection.get(0);
			fail("expected IndexOutOfBoundsException"); //$NON-NLS-1$
		}
		catch(IndexOutOfBoundsException _) {
			// Expected exception
		}

		assertTrue(this.collection.add(this.m1));
		assertTrue(this.collection.add(this.m2));

		try {
			this.collection.get(0);
			fail("expected IndexOutOfBoundsException"); //$NON-NLS-1$
		}
		catch(IndexOutOfBoundsException _) {
			// Expected exception
		}
		
		assertTrue(this.collection.applyChanges(false));

		assertEquals(this.m2, this.collection.get(0));
		assertEquals(this.m1, this.collection.get(1));
		try {
			this.collection.get(2);
			fail("expected IndexOutOfBoundsException"); //$NON-NLS-1$
		}
		catch(IndexOutOfBoundsException _) {
			// Expected exception
		}
	}

	/**
	 */
	public void testIterator() {
		Iterator<DataStub> iter1, iter2;
		
		assertFalse(this.collection.isIterated());

		iter1 = this.collection.iterator();
		iter2 = this.collection.iterator();
		
		assertTrue(this.collection.isIterated());

		assertTrue(this.collection.add(this.m1));
		
		assertTrue(this.collection.isIterated());

		assertFalse(iter1.hasNext());
		assertFalse(iter2.hasNext());
		
		assertFalse(this.collection.isIterated());

		iter1 = this.collection.iterator();
		iter2 = this.collection.iterator();

		assertTrue(this.collection.add(this.m2));

		assertFalse(iter1.hasNext());
		assertFalse(iter2.hasNext());

		this.collection.applyChanges(false);
		
		iter1 = this.collection.iterator();
		iter2 = this.collection.iterator();

		this.collection.clear();

		assertTrue(iter1.hasNext());
		assertEquals(this.m2, iter1.next());
		assertTrue(iter1.hasNext());
		assertEquals(this.m1, iter1.next());
		assertFalse(iter1.hasNext());

		assertFalse(this.collection.isEmpty());

		assertTrue(iter2.hasNext());
		assertEquals(this.m2, iter2.next());
		assertTrue(iter2.hasNext());
		assertEquals(this.m1, iter2.next());
		assertFalse(iter2.hasNext());
		
		assertFalse(this.collection.isEmpty());

		this.collection.applyChanges(false);
		
		iter1 = this.collection.iterator();
		iter2 = this.collection.iterator();
		assertFalse(iter1.hasNext());
		assertFalse(iter2.hasNext());
	}

	/**
	 */
	public void testIterator_threaded() {
		Iterator<DataStub> iter1, iter2;
		
		iter1 = this.collection.iterator();
		iter2 = this.collection.iterator();
		
		AsynchronousTask task1 = new AsynchronousTask(this.collection, this.m1, 0);
		AsynchronousTask task2 = new AsynchronousTask(this.collection, this.m2, 500);
		ExecutorService service = Executors.newFixedThreadPool(2);
		service.submit(task1);
		service.submit(task2);

		assertFalse(iter1.hasNext());
		assertFalse(iter2.hasNext());

		while (!task1.finished.get() || !task2.finished.get()) {
			Thread.yield();
		}
		
		iter1 = this.collection.iterator();
		iter2 = this.collection.iterator();
		
		assertFalse(iter1.hasNext());
		assertFalse(iter2.hasNext());

		assertTrue(this.collection.applyChanges(false));
		
		iter1 = this.collection.iterator();
		iter2 = this.collection.iterator();

		assertTrue(iter1.hasNext());
		assertEquals(this.m2, iter1.next());
		assertTrue(iter1.hasNext());
		assertEquals(this.m1, iter1.next());
		assertFalse(iter1.hasNext());

		assertTrue(iter2.hasNext());
		assertEquals(this.m2, iter2.next());
		assertTrue(iter2.hasNext());
		assertEquals(this.m1, iter2.next());
		assertFalse(iter2.hasNext());
		
		assertFalse(this.collection.isEmpty());
	}
	
	/**
	 */
	public void testAdd() {
		DataStub nd1 = new DataStub(10000);
		DataStub nd2 = new DataStub(5000);
		
		this.listener.reset();
		
		this.collection.add(nd1);

		assertTrue(this.collection.isEmpty());
		assertFalse(this.collection.contains(nd1));
		assertFalse(this.collection.contains(nd2));
		this.listener.assertNull();
		this.collection.applyChanges(false);
		assertFalse(this.collection.isEmpty());
		assertTrue(this.collection.contains(nd1));
		assertFalse(this.collection.contains(nd2));
		this.listener.assertAdded(nd1);
		this.listener.assertNull();
		
		this.collection.add(nd1);

		assertFalse(this.collection.isEmpty());
		assertTrue(this.collection.contains(nd1));
		assertFalse(this.collection.contains(nd2));
		this.listener.assertNull();
		this.collection.applyChanges(false);
		assertFalse(this.collection.isEmpty());
		assertTrue(this.collection.contains(nd1));
		assertFalse(this.collection.contains(nd2));
		this.listener.assertAdded(nd1);
		this.listener.assertNull();
		
		this.collection.add(nd2);

		assertFalse(this.collection.isEmpty());
		assertTrue(this.collection.contains(nd1));
		assertFalse(this.collection.contains(nd2));
		this.listener.assertNull();
		this.collection.applyChanges(false);
		assertFalse(this.collection.isEmpty());
		assertTrue(this.collection.contains(nd1));
		assertTrue(this.collection.contains(nd2));
		this.listener.assertAdded(nd2);
		this.listener.assertNull();
	}

	/**
	 */
	public void testAddAll() {
		DataStub nd1 = new DataStub(10000);
		DataStub nd2 = new DataStub(5000);
		
		this.listener.reset();
		
		this.collection.addAll(Arrays.asList(nd1, nd2));

		assertTrue(this.collection.isEmpty());
		assertFalse(this.collection.contains(nd1));
		assertFalse(this.collection.contains(nd2));
		this.listener.assertNull();
		this.collection.applyChanges(false);
		assertFalse(this.collection.isEmpty());
		assertTrue(this.collection.contains(nd1));
		assertTrue(this.collection.contains(nd2));
		this.listener.assertAdded(nd1);
		this.listener.assertAdded(nd2);
		this.listener.assertNull();
	}
	
	/**
	 */
	public void testRemove() {
		DataStub nd1 = new DataStub(10000);
		DataStub nd2 = new DataStub(5000);
		DataStub nd3 = new DataStub(50000);
		this.collection.addAll(Arrays.asList(nd1, nd2));
		this.collection.applyChanges(true);
		this.listener.reset();

		assertTrue(this.collection.remove(nd1));
		
		assertFalse(this.collection.isEmpty());
		assertTrue(this.collection.contains(nd1));
		assertTrue(this.collection.contains(nd2));
		assertFalse(this.collection.contains(nd3));
		this.listener.assertNull();
		this.collection.applyChanges(false);
		assertFalse(this.collection.isEmpty());
		assertFalse(this.collection.contains(nd1));
		assertTrue(this.collection.contains(nd2));
		assertFalse(this.collection.contains(nd3));
		this.listener.assertRemoved(nd1);
		this.listener.assertNull();

		assertTrue(this.collection.remove(nd3));
		
		assertFalse(this.collection.isEmpty());
		assertFalse(this.collection.contains(nd1));
		assertTrue(this.collection.contains(nd2));
		assertFalse(this.collection.contains(nd3));
		this.listener.assertNull();
		this.collection.applyChanges(false);
		assertFalse(this.collection.isEmpty());
		assertFalse(this.collection.contains(nd1));
		assertTrue(this.collection.contains(nd2));
		assertFalse(this.collection.contains(nd3));
		this.listener.assertNull();

		assertTrue(this.collection.remove(nd2));
		
		assertFalse(this.collection.isEmpty());
		assertFalse(this.collection.contains(nd1));
		assertTrue(this.collection.contains(nd2));
		assertFalse(this.collection.contains(nd3));
		this.listener.assertNull();
		this.collection.applyChanges(false);
		assertTrue(this.collection.isEmpty());
		assertFalse(this.collection.contains(nd1));
		assertFalse(this.collection.contains(nd2));
		assertFalse(this.collection.contains(nd3));
		this.listener.assertRemoved(nd2);
		this.listener.assertNull();
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class AsynchronousTask implements Runnable {

		/** Indicated if this task has finished its work.
		 */
		public final AtomicBoolean finished = new AtomicBoolean(false);
		
		private DataStub obj;
		private AsynchronousThreadSafeCollection<DataStub> collection;
		private long sleep;
		
		/**
		 * @param c
		 * @param e
		 * @param s
		 */
		public AsynchronousTask(AsynchronousThreadSafeCollection<DataStub> c, DataStub e, long s) {
			this.collection = c;
			this.obj = e;
			this.sleep = s;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			if (this.sleep>0) {
				try {
					Thread.sleep(this.sleep);
				}
				catch(Error e) {
					throw e;
				}
				catch(Throwable e) {
					throw new Error(e);
				}
			}
			this.collection.add(this.obj);
			this.obj = null;
			this.collection = null;
			try {
				Thread.sleep(500);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable e) {
				throw new Error(e);
			}
			this.finished.set(true);
		}
		
	}
	
}
