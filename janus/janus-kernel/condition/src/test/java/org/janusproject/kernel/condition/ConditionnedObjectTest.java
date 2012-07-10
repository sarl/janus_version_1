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
package org.janusproject.kernel.condition;

import java.util.Iterator;

import org.janusproject.kernel.condition.Condition;
import org.janusproject.kernel.condition.ConditionnedObject;
import org.janusproject.kernel.condition.FalseCondition;
import org.janusproject.kernel.condition.TrueCondition;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ConditionnedObjectTest extends TestCase {

	private ConditionnedObject<Object,Condition<Object>> cond;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.cond = new ConditionnedObject<Object,Condition<Object>>();
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.cond = null;
		super.tearDown();
	}

	/**
	 */
	public void testAddObtainConditionCondition() {
		Condition<Object> c1 = new TrueCondition();
		Condition<Object> c2 = new FalseCondition();

		assertEquals(0, this.cond.getObtainConditions().size());
		
		assertTrue(this.cond.addObtainCondition(c1));
		assertEquals(1, this.cond.getObtainConditions().size());
		Iterator<Condition<Object>> iterator = this.cond.getObtainConditions().iterator();
		assertTrue(iterator.hasNext());
		assertSame(c1, iterator.next());
		assertFalse(iterator.hasNext());

		assertTrue(this.cond.addObtainCondition(c2));
		assertEquals(2, this.cond.getObtainConditions().size());
		iterator = this.cond.getObtainConditions().iterator();
		assertTrue(iterator.hasNext());
		assertSame(c1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(c2, iterator.next());
		assertFalse(iterator.hasNext());
	}


	/**
	 */
	public void testAddLeaveConditionCondition() {
		Condition<Object> c1 = new TrueCondition();
		Condition<Object> c2 = new FalseCondition();

		assertEquals(0, this.cond.getObtainConditions().size());
		
		assertTrue(this.cond.addLeaveCondition(c1));
		assertEquals(1, this.cond.getLeaveConditions().size());
		Iterator<Condition<Object>> iterator = this.cond.getLeaveConditions().iterator();
		assertTrue(iterator.hasNext());
		assertSame(c1, iterator.next());
		assertFalse(iterator.hasNext());

		assertTrue(this.cond.addLeaveCondition(c2));
		assertEquals(2, this.cond.getLeaveConditions().size());
		iterator = this.cond.getLeaveConditions().iterator();
		assertTrue(iterator.hasNext());
		assertSame(c1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(c2, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testRemoveObtainConditionCondition() {
		Condition<Object> c1 = new TrueCondition();
		Condition<Object> c2 = new FalseCondition();
		Condition<Object> c3 = new FalseCondition();
		this.cond.addObtainCondition(c1);
		this.cond.addObtainCondition(c2);
		
		assertFalse(this.cond.removeObtainCondition(c3));
		assertTrue(this.cond.removeObtainCondition(c1));
		assertFalse(this.cond.removeObtainCondition(c3));

		assertEquals(1, this.cond.getObtainConditions().size());
		Iterator<Condition<Object>> iterator = this.cond.getObtainConditions().iterator();
		assertTrue(iterator.hasNext());
		assertSame(c2, iterator.next());
		assertFalse(iterator.hasNext());
	}


	/**
	 */
	public void testRemoveLeaveConditionCondition() {
		Condition<Object> c1 = new TrueCondition();
		Condition<Object> c2 = new FalseCondition();
		Condition<Object> c3 = new FalseCondition();
		this.cond.addLeaveCondition(c1);
		this.cond.addLeaveCondition(c2);
		
		assertFalse(this.cond.removeLeaveCondition(c3));
		assertTrue(this.cond.removeLeaveCondition(c1));
		assertFalse(this.cond.removeLeaveCondition(c3));

		assertEquals(1, this.cond.getLeaveConditions().size());
		Iterator<Condition<Object>> iterator = this.cond.getLeaveConditions().iterator();
		assertTrue(iterator.hasNext());
		assertSame(c2, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testIsObtainableConditionParameterOwner() {
		assertTrue(this.cond.isObtainable(new Object()));

		Condition<Object> c1 = new TrueCondition();
		this.cond.addObtainCondition(c1);
		
		assertTrue(this.cond.isObtainable(new Object()));

		Condition<Object> c2 = new FalseCondition();
		this.cond.addObtainCondition(c2);
		
		assertFalse(this.cond.isObtainable(new Object()));

		Condition<Object> c3 = new TrueCondition();
		this.cond.addObtainCondition(c3);
		
		assertFalse(this.cond.isObtainable(new Object()));
	}

	/**
	 */
	public void testIsLeavableConditionParameterOwner() {
		assertTrue(this.cond.isLeavable(new Object()));

		Condition<Object> c1 = new TrueCondition();
		this.cond.addLeaveCondition(c1);
		
		assertTrue(this.cond.isLeavable(new Object()));

		Condition<Object> c2 = new FalseCondition();
		this.cond.addLeaveCondition(c2);
		
		assertFalse(this.cond.isLeavable(new Object()));

		Condition<Object> c3 = new TrueCondition();
		this.cond.addLeaveCondition(c3);
		
		assertFalse(this.cond.isLeavable(new Object()));
	}
		
	/**
	 */
	public void testGetObtainFailureConditionParameterOwner() {
		assertNull(this.cond.getObtainFailure(new Object()));

		Condition<Object> c1 = new TrueCondition();
		this.cond.addObtainCondition(c1);
		
		assertNull(this.cond.getObtainFailure(new Object()));

		Condition<Object> c2 = new FalseCondition();
		this.cond.addObtainCondition(c2);
		
		assertNotNull(this.cond.getObtainFailure(new Object()));

		Condition<Object> c3 = new TrueCondition();
		this.cond.addObtainCondition(c3);
		
		assertNotNull(this.cond.getObtainFailure(new Object()));
	}

	/**
	 */
	public void testGetLeaveFailureConditionParameterOwner() {
		assertNull(this.cond.getLeaveFailure(new Object()));

		Condition<Object> c1 = new TrueCondition();
		this.cond.addLeaveCondition(c1);
		
		assertNull(this.cond.getLeaveFailure(new Object()));

		Condition<Object> c2 = new FalseCondition();
		this.cond.addLeaveCondition(c2);
		
		assertNotNull(this.cond.getLeaveFailure(new Object()));

		Condition<Object> c3 = new TrueCondition();
		this.cond.addLeaveCondition(c3);
		
		assertNotNull(this.cond.getLeaveFailure(new Object()));
	}

}
