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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class UnmodifiableDirectAccessSetListTest extends TestCase {

	private SortedSet<Integer> original1;
	private List<Integer> original2;
	private UnmodifiableDirectAccessSetList<Integer> set;
	private Integer m1, m2, m3, m4, m5, m6;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.original1 = new TreeSet<Integer>();
		this.original2 = new ArrayList<Integer>();
		this.m1 = 1;
		this.m2 = 10;
		this.m3 = 5;
		this.m4 = 42;
		this.m5 = 42;
		this.m6 = 44;
		this.original1.add(this.m1);
		this.original1.add(this.m2);
		this.original1.add(this.m3);
		this.original2.add(this.m4);
		this.set = new UnmodifiableDirectAccessSetList<Integer>(this.original1, this.original2);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.set = null;
		this.original1 = null;
		this.m1 = this.m2 = this.m3 = this.m4 = this.m5 = this.m6 = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetInt() {
		try {
			this.set.get(-1);
			fail("Expecting IndexOutOfBoundException"); //$NON-NLS-1$
		}
		catch(IndexOutOfBoundsException _) {
			//
		}
		assertEquals(this.m1, this.set.get(0));
		assertEquals(this.m3, this.set.get(1));
		assertEquals(this.m2, this.set.get(2));
		assertEquals(this.m4, this.set.get(3));
		try {
			this.set.get(4);
			fail("Expecting IndexOutOfBoundException"); //$NON-NLS-1$
		}
		catch(IndexOutOfBoundsException _) {
			//
		}
	}

    /**
     */
	public void testIterator() {
		Iterator<Integer> iterator = this.set.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.m3, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.m4, iterator.next());
		assertFalse(iterator.hasNext());
	}

    /**
     */
    public void testSize() {
    	assertEquals(4, this.set.size());
    }

    /**
     */
    public void testIsEmpty() {
    	assertFalse(this.set.isEmpty());
    }

    /**
     */
    public void testContainsObject() {
    	assertTrue(this.set.contains(this.m1));
    	assertTrue(this.set.contains(this.m3));
    	assertTrue(this.set.contains(this.m2));
    	assertTrue(this.set.contains(this.m4));
    	assertTrue(this.set.contains(this.m5));
    	assertFalse(this.set.contains(this.m6));
    }

    /**
     */
    public void testToArray() {
    	Object[] tab = this.set.toArray();
    	assertNotNull(tab);
    	assertEquals(4, tab.length);
    	assertEquals(this.m1, tab[0]);
    	assertEquals(this.m3, tab[1]);
    	assertEquals(this.m2, tab[2]);
    	assertEquals(this.m4, tab[3]);
    }

    /**
     */
    public void testToArrayArray_empty() {
    	Object[] tab = this.set.toArray(new Object[0]);
    	assertNotNull(tab);
    	assertEquals(4, tab.length);
    	assertEquals(this.m1, tab[0]);
    	assertEquals(this.m3, tab[1]);
    	assertEquals(this.m2, tab[2]);
    	assertEquals(this.m4, tab[3]);
    }

    /**
     */
    public void testToArrayArray_toosmall() {
    	Object[] tab = this.set.toArray(new Object[2]);
    	assertNotNull(tab);
    	assertEquals(4, tab.length);
    	assertEquals(this.m1, tab[0]);
    	assertEquals(this.m3, tab[1]);
    	assertEquals(this.m2, tab[2]);
    	assertEquals(this.m4, tab[3]);
    }

    /**
     */
    public void testToArrayArray_samesize() {
    	Object[] tab = this.set.toArray(new Object[4]);
    	assertNotNull(tab);
    	assertEquals(4, tab.length);
    	assertEquals(this.m1, tab[0]);
    	assertEquals(this.m3, tab[1]);
    	assertEquals(this.m2, tab[2]);
    	assertEquals(this.m4, tab[3]);
    }

    /**
     */
    public void testToArrayArray_tobig() {
    	Object[] tab = this.set.toArray(new Object[6]);
    	assertNotNull(tab);
    	assertEquals(6, tab.length);
    	assertEquals(this.m1, tab[0]);
    	assertEquals(this.m3, tab[1]);
    	assertEquals(this.m2, tab[2]);
    	assertEquals(this.m4, tab[3]);
    	assertNull(tab[4]);
    	assertNull(tab[5]);
    }

    /**
     */
    public void testContainsAll() {
    	List<Integer> l = new ArrayList<Integer>();
    	l.add(this.m1);
    	l.add(this.m2);
    	assertTrue(this.set.containsAll(l));

    	l = new ArrayList<Integer>();
    	l.add(this.m1);
    	l.add(this.m5);
    	assertTrue(this.set.containsAll(l));

    	l = new ArrayList<Integer>();
    	l.add(this.m1);
    	l.add(this.m6);
    	assertFalse(this.set.containsAll(l));
    }

}
