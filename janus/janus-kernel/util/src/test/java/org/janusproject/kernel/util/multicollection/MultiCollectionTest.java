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
package org.janusproject.kernel.util.multicollection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.multicollection.MultiCollection;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MultiCollectionTest extends TestCase {

	private MultiCollection<DataStub> collection;
	private DataStub m1, m2, m3, m4, m5, m6;
	private Collection<DataStub> c1, c2, c3;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.m1 = new DataStub(1);
		this.m2 = new DataStub(10);
		this.m3 = new DataStub(5);
		this.m4 = new DataStub(42);
		this.m5 = new DataStub(42);
		this.m5 = new DataStub(64);
		this.c1 = Arrays.asList(this.m2, this.m1);
		this.c2 = Arrays.asList(this.m3);
		this.c3 = Arrays.asList(this.m4);

		this.collection = new MultiCollection<DataStub>();
		
		this.collection.addCollection(this.c1);
		this.collection.addCollection(this.c2);
		this.collection.addCollection(this.c3);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.collection.clear();
		this.collection = null;
		this.c1 = this.c2 = this.c3 = null;
		this.m1 = this.m2 = this.m3 = this.m4 = this.m5 = this.m6 = null;
		super.tearDown();
	}
	
	/**
	 */
	public void testClear() {
		assertFalse(this.collection.isEmpty());
		this.collection.clear();
		assertTrue(this.collection.isEmpty());
	}

	/**
	 */
	public void testContainsObject() {
		assertFalse(this.collection.contains(null));
		assertTrue(this.collection.contains(this.m1));
		assertTrue(this.collection.contains(this.m2));
		assertTrue(this.collection.contains(this.m3));
		assertTrue(this.collection.contains(this.m4));
		assertFalse(this.collection.contains(this.m5));
		assertFalse(this.collection.contains(this.m6));
	}

	/**
	 */
	public void testIsEmpty() {
		assertFalse(this.collection.isEmpty());
		this.collection.clear();
		assertTrue(this.collection.isEmpty());
	}

	/**
	 */
	public void testSize() {
		assertEquals(4, this.collection.size());
		this.collection.clear();
		assertEquals(0, this.collection.size());
	}

	/**
	 */
	public void testIterator() {
		Iterator<DataStub> iterator = this.collection.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.m3, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.m4, iterator.next());
		assertFalse(iterator.hasNext());
	}

}
