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
package org.janusproject.kernel.crio.capacity;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FirstCapacityImplementationSelectionPolicyTest
extends TestCase {

	private FirstCapacityImplementationSelectionPolicy policy;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.policy = new FirstCapacityImplementationSelectionPolicy();
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.policy = null;
		super.tearDown();
	}	
	
	/**
	 */
	public void testSelectImplementationCollection() {
		List<CapacityImplementationStub> capacities = Arrays.asList(
				new CapacityImplementationStub(),
				new CapacityImplementationStub(),
				new CapacityImplementationStub());
		CapacityImplementationStub first = capacities.get(0);
		for(int i=0; i<50; ++i) {
			assertSame(
					first,
					this.policy.selectImplementation(capacities));
		}
	}

}
