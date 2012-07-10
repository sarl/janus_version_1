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
package org.janusproject.kernel.util.directaccess;

import java.util.Iterator;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SafeIterator2Test extends TestCase {

	private AsynchronousThreadSafeCollection<Integer> collection;
	private SafeIterator<Integer> iterator;
	private Integer m1, m2, m3, m4;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.collection = new AsynchronousThreadSafeCollection<Integer>(Integer.class);
		this.m1 = 1;
		this.m2 = 10;
		this.m3 = 5;
		this.m4 = 42;
		this.collection.add(this.m1);
		this.collection.add(this.m2);
		this.collection.add(this.m3);
		this.collection.add(this.m4);
		this.collection.applyChanges(true);
		this.iterator = new SafeIterator<Integer>(this.collection, this.collection.iterator());
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.iterator = null;
		this.collection.clear();
		this.collection = null;
		this.m1 = this.m2 = this.m3 = this.m4 = null;
		super.tearDown();
	}

	/**
	 */
	public void testIterator() {
		assertTrue(this.iterator.hasNext());
		assertSame(this.m1, this.iterator.next());
		assertTrue(this.iterator.hasNext());
		assertSame(this.m3, this.iterator.next());
		assertTrue(this.iterator.hasNext());
		assertSame(this.m2, this.iterator.next());
		assertTrue(this.iterator.hasNext());
		assertSame(this.m4, this.iterator.next());
		assertFalse(this.iterator.hasNext());
		
		assertEquals(4, this.collection.size());
		Iterator<Integer> iter = this.collection.iterator();
		assertTrue(iter.hasNext());
		assertEquals(this.m1, iter.next());
		assertTrue(iter.hasNext());
		assertEquals(this.m3, iter.next());
		assertTrue(iter.hasNext());
		assertEquals(this.m2, iter.next());
		assertTrue(iter.hasNext());
		assertEquals(this.m4, iter.next());
		assertFalse(iter.hasNext());
	}

}
