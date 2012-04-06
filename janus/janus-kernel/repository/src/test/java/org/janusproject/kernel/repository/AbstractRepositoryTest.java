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
package org.janusproject.kernel.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import org.janusproject.kernel.repository.RepositoryChangeEvent.ChangeType;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.sizediterator.ModifiableCollectionSizedIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AbstractRepositoryTest extends TestCase {

	private String k1, k2, k3, k4;
	private String v1, v2;
	private AbstractRepositoryStub repository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.k1 = new String("k1"); //$NON-NLS-1$
		this.v1 = new String("v1"); //$NON-NLS-1$
		this.k2 = new String("k2"); //$NON-NLS-1$
		this.v2 = new String("v2"); //$NON-NLS-1$
		this.k3 = new String("k3"); //$NON-NLS-1$
		this.k4 = new String("k4"); //$NON-NLS-1$
		this.repository = new AbstractRepositoryStub(this.k1, this.v1, this.k2, this.v2);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.repository = null;
		this.k1 = this.k2 = this.k3 = this.k4 = null;
		this.v1 = this.v2 = null;
		super.tearDown();
	}

	/** 
	 */
	public void testGetEventRepository() {
		assertSame(this.repository, this.repository.getEventRepository());
	}
	
	/**
	 */
	public void testContains() {
		assertTrue(this.repository.contains(this.k1));
		assertTrue(this.repository.contains(this.k2));
		assertFalse(this.repository.contains(this.k3));
		assertFalse(this.repository.contains(this.k4));
	}

	/**
	 */
	public void testIsEmpty() {
		assertFalse(this.repository.isEmpty());
	}

	/**
	 */
	public void testIterator() {
		Iterator<String> iterator = this.repository.iterator();
		
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(this.k1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.k2, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testSizedIterator() {
		SizedIterator<String> iterator = this.repository.sizedIterator();
		
		assertNotNull(iterator);
		assertEquals(2, iterator.totalSize());
		assertEquals(2, iterator.rest());
		assertTrue(iterator.hasNext());
		assertSame(this.k1, iterator.next());
		assertEquals(2, iterator.totalSize());
		assertEquals(1, iterator.rest());
		assertTrue(iterator.hasNext());
		assertSame(this.k2, iterator.next());
		assertEquals(2, iterator.totalSize());
		assertEquals(0, iterator.rest());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testFireAddRepositoryChange() {
		RepositoryChangeListenerStub listener = new RepositoryChangeListenerStub();
		this.repository.addRepositoryChangeListener(listener);
		
		String newObject = new String("no"); //$NON-NLS-1$
		String newValue = new String("nv"); //$NON-NLS-1$
		
		this.repository.fireAddRepositoryChange(newObject, newValue);
		
		listener.assertEquals(
				this.repository,
				ChangeType.ADD,
				newObject,
				null,
				newValue);
	}
	
	/**
	 */
	public void testFireRemoveRepositoryChange() {
		RepositoryChangeListenerStub listener = new RepositoryChangeListenerStub();
		this.repository.addRepositoryChangeListener(listener);
		
		String oldObject = new String("oo"); //$NON-NLS-1$
		String oldValue = new String("ov"); //$NON-NLS-1$
		
		this.repository.fireRemoveRepositoryChange(oldObject, oldValue);
		
		listener.assertEquals(
				this.repository,
				ChangeType.REMOVE,
				oldObject,
				oldValue,
				null);
	}
	
	/**
	 */
	public void testFireUpdateRepositoryChange() {
		RepositoryChangeListenerStub listener = new RepositoryChangeListenerStub();
		this.repository.addRepositoryChangeListener(listener);
		
		String changedObject = new String("co"); //$NON-NLS-1$
		String oldValue = new String("ov"); //$NON-NLS-1$
		String newValue = new String("nv"); //$NON-NLS-1$
		
		this.repository.fireUpdateRepositoryChange(changedObject, oldValue, newValue);
		
		listener.assertEquals(
				this.repository,
				ChangeType.UPDATE,
				changedObject,
				oldValue,
				newValue);
	}

	/**
	 */
	public void testRemoveRepositoryChangeListenerRepositoryChangeListener() {
		RepositoryChangeListenerStub listener = new RepositoryChangeListenerStub();
		this.repository.addRepositoryChangeListener(listener);
		
		this.repository.removeRepositoryChangeListener(listener);

		String changedObject = new String("co"); //$NON-NLS-1$
		String oldValue = new String("ov"); //$NON-NLS-1$
		String newValue = new String("nv"); //$NON-NLS-1$
		
		this.repository.fireUpdateRepositoryChange(changedObject, oldValue, newValue);
		
		listener.assertNull();
	}
		
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class AbstractRepositoryStub extends AbstractRepository<String,String> {

		/**
		 * Key 1
		 */
		private final String key1;
		/**
		 * Value 1
		 */
		private final String value1;
		/**
		 * Key 2
		 */
		private final String key2;
		/**
		 * Value 2
		 */
		private final String value2;
		
		/**
		 * @param k1
		 * @param v1
		 * @param k2
		 * @param v2
		 */
		public AbstractRepositoryStub(String k1, String v1, String k2, String v2) {
			this.key1 = k1;
			this.value1 = v1;
			this.key2 = k2;
			this.value2 = v2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(String key, String data) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SizedIterator<Entry<String,String>> getEntryIterator() {
			TreeMap<String,String> map = new TreeMap<String,String>();
			map.put(this.key1, this.value1);
			map.put(this.key2, this.value2);
			return new ModifiableCollectionSizedIterator<Entry<String,String>>(
					map.entrySet(),
					this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String remove(String key) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String get(String id) {
			if (id==this.key1) return this.value1;
			if (id==this.key2) return this.value2;
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<String> identifiers() {
			return Arrays.asList(this.key1, this.key2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<String> values() {
			return Arrays.asList(this.value1, this.value2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return 2;
		}

	}
		
}
