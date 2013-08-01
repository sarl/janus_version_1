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
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CapacityContextTest extends TestCase {

	private CapacityContext context;
	private CapacityCaller caller;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.caller = new CapacityCallerStub();
		this.context = new CapacityContextStub(
				this.caller, 
				CapacityStub.class,
				CapacityImplementationType.DIRECT_ACTOMIC,
				1, 2, 3);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.context = null;
		this.caller = null;
		super.tearDown();
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
	public void testGetIdentifier() {
		assertNotNull(this.context.getIdentifier());
	}

	/**
	 */
	public void testGetInvokedCapacity() {
		assertEquals(CapacityStub.class, this.context.getInvokedCapacity());
	}

	/**
	 */
	public void testGetCaller() {
		assertEquals(this.caller, this.context.getCaller());
	}

	/**
	 */
	public void testGetTimeManager() {
		assertSame(this.caller.getTimeManager(), this.context.getTimeManager());
	}

	/**
	 */
	public void testGetInputValues() {
		assertEquals(Arrays.asList(
				Integer.valueOf(1),
				Integer.valueOf(2),
				Integer.valueOf(3)),
				Arrays.asList(this.context.getInputValues()));
	}


	/**
	 */
	public void testFail() {
		assertFalse(this.context.isFailed());
		assertNull(this.context.getFailureException());
		this.context.fail();
		assertTrue(this.context.isFailed());
		assertNull(this.context.getFailureException());
	}

	/**
	 */
	public void testFailThrowable() {
		Throwable t = new Error();
		
		assertFalse(this.context.isFailed());
		assertNull(this.context.getFailureException());
		this.context.fail(t);
		assertTrue(this.context.isFailed());
		assertSame(t, this.context.getFailureException());
	}
	
	/**
	 */
	public void testGetFailureException() {
		Throwable t = new Error();
		
		assertFalse(this.context.isFailed());
		assertNull(this.context.getFailureException());
		this.context.fail(t);
		assertTrue(this.context.isFailed());
		assertSame(t, this.context.getFailureException());
	}

	/**
	 */
	public void testIsFailed() {
		assertFalse(this.context.isFailed());
		this.context.fail();
		assertTrue(this.context.isFailed());
	}

	/**
	 */
	public void testIsResultAvailable() {
		assertFalse(this.context.isResultAvailable());
		this.context.setOutputValues(3,4,5);
		assertTrue(this.context.isResultAvailable());
	}

	/**
	 */
	public void testGetOutputValues() {
		assertFalse(this.context.isResultAvailable());
		this.context.setOutputValues(3,4,5);
		assertTrue(this.context.isResultAvailable());
		assertEquals(Arrays.asList(
				Integer.valueOf(3),
				Integer.valueOf(4),
				Integer.valueOf(5)),
				Arrays.asList(this.context.getOutputValues()));
	}

	/**
	 */
	public void testGetOutputValuesClass() {
		assertFalse(this.context.isResultAvailable());
		this.context.setOutputValues(3,4,5);
		assertTrue(this.context.isResultAvailable());
		
		assertEquals(Arrays.asList(
				Integer.valueOf(3),
				Integer.valueOf(4),
				Integer.valueOf(5)),
				Arrays.asList(this.context.getOutputValues(Integer.class)));
		
		assertEquals(Arrays.asList(
				Integer.valueOf(3),
				Integer.valueOf(4),
				Integer.valueOf(5)),
				Arrays.asList(this.context.getOutputValues(Number.class)));

		assertEquals(Arrays.<Float>asList(),
				Arrays.asList(this.context.getOutputValues(Float.class)));
	}

	/**
	 */
	public void testGetOutputValue() {
		assertFalse(this.context.isResultAvailable());
		this.context.setOutputValues(3,4,5);
		assertTrue(this.context.isResultAvailable());
		assertEquals(Integer.valueOf(3), this.context.getOutputValue());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class CapacityContextStub extends CapacityContext {

		/**
		 * Builds a new call
		 * @param caller is the caller of this capacity
		 * @param invokedCapacity is the invoked capacity in this context.
		 * @param type is the type of the invoked capacity in this context.
		 * @param input are the input data required to execute this capacity
		 */
		public CapacityContextStub(
				CapacityCaller caller, 
				Class<? extends Capacity> invokedCapacity,
				CapacityImplementationType type,
				Object... input) {
			super(caller, invokedCapacity, type, input);
		}
		
	}
	
}
