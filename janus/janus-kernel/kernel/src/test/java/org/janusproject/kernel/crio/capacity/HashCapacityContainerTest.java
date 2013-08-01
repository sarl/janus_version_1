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
package org.janusproject.kernel.crio.capacity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class HashCapacityContainerTest extends TestCase {

	private HashCapacityContainer container;
	private CapacityImplementationStub capacity;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.capacity = new CapacityImplementationStub();
		this.container = new HashCapacityContainer();
		this.container.addCapacity(this.capacity);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.container = null;
		this.capacity = null;
		super.tearDown();
	}	
	
	private static void assertContains(Iterator<?> iterator, Object... objects) {
		ArrayList<Object> objs = new ArrayList<Object>(Arrays.asList(objects));
		assertNotNull("iterator is null", iterator); //$NON-NLS-1$
		while(iterator.hasNext()) {
			Object obj = iterator.next();
			assertTrue("unable to find object:"+obj, objs.remove(obj)); //$NON-NLS-1$
		}
		assertTrue("iterator contains more objects than expected", objs.isEmpty());		 //$NON-NLS-1$
	}
	
	private static void assertEquals(Collection<?> expected, Collection<?> actual) {
		if (expected==actual) return;
		if (expected!=null && actual!=null && expected.size()==actual.size()) {
			try {
				ArrayList<Object> obj = new ArrayList<Object>(actual);
				Iterator<?> iterator = expected.iterator();
				boolean failure = false;
				Object o1;
				while (iterator.hasNext() && !failure) {
					o1 = iterator.next();
					failure = !obj.remove(o1);
				}
				if (!failure && obj.isEmpty()) return;
			}
			catch(AssertionError ae) {
				throw ae;
			}
			catch(Throwable _) {
				//
			}
		}
		fail("collections are not equal. Expected: " //$NON-NLS-1$
				+((expected==null)?null:expected.toString())
				+"; Actual: " //$NON-NLS-1$
				+((actual==null)?null:actual.toString()));
	}

	/**
	 */
	public void testAddCapacityCapacityImplementation() {
		Collection<CapacityImplementation> implementations;
		
		CapacityImplementationStub c1 = new CapacityImplementationStub();
		Capacity2ImplementationStub c2 = new Capacity2ImplementationStub();
		
		this.container.addCapacity(c1);
		this.container.addCapacity(c2);
		
		implementations = this.container.get(CapacityStub.class);		
		assertNotNull(implementations);
		assertEquals(2, implementations.size());
		assertEquals(Arrays.asList(this.capacity,c1), implementations);

		implementations = this.container.get(Capacity2Stub.class);		
		assertNotNull(implementations);
		assertEquals(1, implementations.size());
		assertEquals(Arrays.asList(c2), implementations);

		implementations = this.container.get(Capacity3Stub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());
	}


	/**
	 */
	public void testRemoveCapacityClass() {
		Collection<CapacityImplementation> implementations;
		Iterator<CapacityImplementation> iterator;

		this.container.removeCapacity(Capacity2Stub.class);

		implementations = this.container.get(CapacityStub.class);		
		assertNotNull(implementations);
		assertEquals(1, implementations.size());
		iterator = implementations.iterator();
		assertTrue(iterator.hasNext());
		assertSame(this.capacity, iterator.next());
		assertFalse(iterator.hasNext());

		implementations = this.container.get(Capacity2Stub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());

		implementations = this.container.get(Capacity3Stub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());
	}

	/**
	 */
	public void testRemoveCapacityCapacityImplementation_1() {
		Collection<CapacityImplementation> implementations;

		this.container.removeCapacity(new Capacity2ImplementationStub());

		implementations = this.container.get(CapacityStub.class);		
		assertNotNull(implementations);
		assertEquals(1, implementations.size());
		assertEquals(Arrays.asList(this.capacity), implementations);

		implementations = this.container.get(Capacity2Stub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());

		implementations = this.container.get(Capacity3Stub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());
	}

	/**
	 */
	public void testRemoveCapacityCapacityImplementation_2() {
		Collection<CapacityImplementation> implementations;

		this.container.removeCapacity(this.capacity);

		implementations = this.container.get(CapacityStub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());

		implementations = this.container.get(Capacity2Stub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());

		implementations = this.container.get(Capacity3Stub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());
	}

	/**
	 */
	public void testAddAllCapacityContainer() {
		Collection<CapacityImplementation> implementations;

		HashCapacityContainer newContainer = new HashCapacityContainer();
		Capacity2ImplementationStub c2 = new Capacity2ImplementationStub();
		newContainer.addCapacity(c2);
		
		this.container.addAll(newContainer);
		
		implementations = this.container.get(CapacityStub.class);
		assertNotNull(implementations);
		assertEquals(1, implementations.size());
		assertEquals(Arrays.asList(this.capacity), implementations);

		implementations = this.container.get(Capacity2Stub.class);		
		assertNotNull(implementations);
		assertEquals(1, implementations.size());
		assertEquals(Arrays.asList(c2), implementations);

		implementations = this.container.get(Capacity3Stub.class);		
		assertNotNull(implementations);
		assertEquals(0, implementations.size());		
	}

	/**
	 */
	public void testContainsClass() {
		assertTrue(this.container.contains(CapacityStub.class));
		assertFalse(this.container.contains(Capacity2Stub.class));
		assertFalse(this.container.contains(Capacity3Stub.class));
	}

	/**
	 */
	public void testIsEmpty() {
		assertFalse(this.container.isEmpty());
		this.container.removeCapacity(CapacityStub.class);
		assertTrue(this.container.isEmpty());
	}

	/**
	 */
	public void testSize() {
		assertEquals(1, this.container.size());
		this.container.removeCapacity(CapacityStub.class);
		assertEquals(0, this.container.size());
	}

	/**
	 */
	public void testIterator() {
		Capacity2ImplementationStub c2 = new Capacity2ImplementationStub();
		this.container.addCapacity(c2);
		
		Iterator<Class<? extends Capacity>> iterator = this.container.iterator();
		
		assertContains(iterator, 
				CapacityStub.class, 
				Capacity2Stub.class);
	}

	/**
	 */
	public void testSizedIterator() {
		Capacity2ImplementationStub c2 = new Capacity2ImplementationStub();
		this.container.addCapacity(c2);
		
		SizedIterator<Class<? extends Capacity>> iterator = this.container.sizedIterator();
		
		assertContains(iterator, 
				CapacityStub.class, 
				Capacity2Stub.class);
	}

}
