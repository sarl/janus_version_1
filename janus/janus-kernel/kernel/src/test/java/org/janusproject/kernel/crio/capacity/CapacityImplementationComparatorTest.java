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

import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CapacityImplementationComparatorTest extends TestCase {

	private CapacityImplementationComparator comparator;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.comparator = CapacityImplementationComparator.SINGLETON;
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.comparator = null;
		super.tearDown();
	}
	
	private static void assertNegative(int value) {
		if (value<0) return;
		fail("negative value is expected; actual: "+value); //$NON-NLS-1$
	}

	private static void assertPositive(int value) {
		if (value>0) return;
		fail("positive value is expected; actual: "+value); //$NON-NLS-1$
	}

	private static void assertZero(int value) {
		if (value==0) return;
		fail("zero value is expected; actual: "+value); //$NON-NLS-1$
	}

	/**
	 */
	public void testCompareCapacityImplementationCapacityImplementation() {
		CapacityImplementationStub i1 = new CapacityImplementationStub(CapacityImplementationType.DIRECT_ACTOMIC);
		CapacityImplementationStub i2 = new CapacityImplementationStub(CapacityImplementationType.DIRECT_ACTOMIC);
		CapacityImplementationStub i3 = new CapacityImplementationStub(CapacityImplementationType.INDIRECT_ATOMIC);
		CapacityImplementationStub i4 = new CapacityImplementationStub(CapacityImplementationType.COMPOSED);
		CapacityImplementationStub i5 = new CapacityImplementationStub(CapacityImplementationType.EMERGENT);
		
		int hashOrder = i1.hashCode() - i2.hashCode();
		
		assertZero(this.comparator.compare(null, null));
		assertNegative(this.comparator.compare(null, i1));
		assertNegative(this.comparator.compare(null, i2));
		assertNegative(this.comparator.compare(null, i3));
		assertNegative(this.comparator.compare(null, i4));
		assertNegative(this.comparator.compare(null, i5));

		assertPositive(this.comparator.compare(i1, null));
		assertZero(this.comparator.compare(i1, i1));
		assertEquals(hashOrder,this.comparator.compare(i1, i2));
		assertNegative(this.comparator.compare(i1, i3));
		assertNegative(this.comparator.compare(i1, i4));
		assertNegative(this.comparator.compare(i1, i5));

		assertPositive(this.comparator.compare(i2, null));
		assertEquals(-hashOrder,this.comparator.compare(i2, i1));
		assertZero(this.comparator.compare(i2, i2));
		assertNegative(this.comparator.compare(i2, i3));
		assertNegative(this.comparator.compare(i2, i4));
		assertNegative(this.comparator.compare(i2, i5));

		assertPositive(this.comparator.compare(i3, null));
		assertPositive(this.comparator.compare(i3, i1));
		assertPositive(this.comparator.compare(i3, i2));
		assertZero(this.comparator.compare(i3, i3));
		assertNegative(this.comparator.compare(i3, i4));
		assertNegative(this.comparator.compare(i3, i5));

		assertPositive(this.comparator.compare(i4, null));
		assertPositive(this.comparator.compare(i4, i1));
		assertPositive(this.comparator.compare(i4, i2));
		assertPositive(this.comparator.compare(i4, i3));
		assertZero(this.comparator.compare(i4, i4));
		assertNegative(this.comparator.compare(i4, i5));

		assertPositive(this.comparator.compare(i5, null));
		assertPositive(this.comparator.compare(i5, i1));
		assertPositive(this.comparator.compare(i5, i2));
		assertPositive(this.comparator.compare(i5, i3));
		assertPositive(this.comparator.compare(i5, i4));
		assertZero(this.comparator.compare(i5, i5));
	}

}