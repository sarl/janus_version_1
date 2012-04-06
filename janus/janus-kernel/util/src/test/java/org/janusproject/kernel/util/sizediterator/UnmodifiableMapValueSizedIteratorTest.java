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
package org.janusproject.kernel.util.sizediterator;

import java.util.HashMap;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.sizediterator.UnmodifiableMapValueSizedIterator;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class UnmodifiableMapValueSizedIteratorTest extends TestCase {

	private HashMap<Object,DataStub> map;
	private UnmodifiableMapValueSizedIterator<DataStub> iterator;
	private DataStub k1, k2, k3, k4;
	private DataStub m1, m2, m3, m4;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.map = new HashMap<Object,DataStub>();
		this.k1 = new DataStub(1);
		this.k2 = new DataStub(10);
		this.k3 = new DataStub(5);
		this.k4 = new DataStub(42);
		this.m1 = new DataStub(1);
		this.m2 = new DataStub(10);
		this.m3 = new DataStub(5);
		this.m4 = new DataStub(42);
		this.map.put(this.k1,this.m1);
		this.map.put(this.k2,this.m2);
		this.map.put(this.k3,this.m3);
		this.map.put(this.k4,this.m4);
		this.iterator = new UnmodifiableMapValueSizedIterator<DataStub>(this.map);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.iterator = null;
		this.map.clear();
		this.map = null;
		this.k1 = this.k2 = this.k3 = this.k4 = null;
		this.m1 = this.m2 = this.m3 = this.m4 = null;
		super.tearDown();
	}
	
	/**
	 */
	public void testIterator() {
		assertEquals(4, this.iterator.totalSize());
		assertEquals(4, this.iterator.rest());

		assertTrue(this.iterator.hasNext());
		assertSame(this.m1, this.iterator.next());
		assertEquals(4, this.iterator.totalSize());
		assertEquals(3, this.iterator.rest());
		
		assertTrue(this.iterator.hasNext());
		assertSame(this.m3, this.iterator.next());		
		assertEquals(4, this.iterator.totalSize());
		assertEquals(2, this.iterator.rest());
		
		assertTrue(this.iterator.hasNext());
		assertSame(this.m4, this.iterator.next());		
		assertEquals(4, this.iterator.totalSize());
		assertEquals(1, this.iterator.rest());
		
		assertTrue(this.iterator.hasNext());
		assertSame(this.m2, this.iterator.next());		
		assertEquals(4, this.iterator.totalSize());
		assertEquals(0, this.iterator.rest());
		
		assertFalse(this.iterator.hasNext());
	}

}
