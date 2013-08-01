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
package org.janusproject.kernel.message;

import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CreationDateMessageComparatorTest extends TestCase {

	private CreationDateMessageComparator cmp = CreationDateMessageComparator.SINGLETON;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
	}
	
	/**
	 */
	public void testCompareMessageMessage() {
		
		Message m1 = new MessageStub(1);
		Message m2 = new MessageStub(2);
		Message m3 = new MessageStub(1);
		
		UUID id1 = m1.getIdentifier();
		UUID id2 = m3.getIdentifier();
		int cmpResult = id1.compareTo(id2);
		int cmpResult2 = id2.compareTo(id1);
		
		assertEquals(0, this.cmp.compare(m1, m1));
		assertEquals(-1, this.cmp.compare(m1, m2));
		assertEquals(cmpResult, this.cmp.compare(m1, m3));		

		assertEquals(1, this.cmp.compare(m2, m1));
		assertEquals(0, this.cmp.compare(m2, m2));
		assertEquals(1, this.cmp.compare(m2, m3));		

		assertEquals(cmpResult2, this.cmp.compare(m3, m1));
		assertEquals(-1, this.cmp.compare(m3, m2));
		assertEquals(0, this.cmp.compare(m3, m3));		
	}
		
}
