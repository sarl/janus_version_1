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
package org.janusproject.kernel.crio.core;

import java.util.UUID;

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AddressUtilTest extends TestCase {

	private UUID uid;
	
	/**
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.uid = UUID.randomUUID();
	}

	/**
	 */
	@Override
	protected void tearDown() throws Exception {
		this.uid = null;
		super.tearDown();
	}

	/**
	 * @throws IllegalArgumentException 
	 */
	public void testCreateAgentAddressString() throws IllegalArgumentException {
		try {
			AddressUtil.createAgentAddress((String)null);
			fail("IllegalArgumentException was expected"); //$NON-NLS-1$
		}
		catch(IllegalArgumentException _) {
			//$NON-NLS-1$
		}
		
		try {
			AddressUtil.createAgentAddress(""); //$NON-NLS-1$
			fail("IllegalArgumentException was expected"); //$NON-NLS-1$
		}
		catch(IllegalArgumentException _) {
			//$NON-NLS-1$
		}

		try {
			AddressUtil.createAgentAddress("a"); //$NON-NLS-1$
			fail("IllegalArgumentException was expected"); //$NON-NLS-1$
		}
		catch(IllegalArgumentException _) {
			//$NON-NLS-1$
		}

		try {
			AddressUtil.createAgentAddress("a a"); //$NON-NLS-1$
			fail("IllegalArgumentException was expected"); //$NON-NLS-1$
		}
		catch(IllegalArgumentException _) {
			//$NON-NLS-1$
		}
		
		try {
			AddressUtil.createAgentAddress("a::a"); //$NON-NLS-1$
			fail("IllegalArgumentException was expected"); //$NON-NLS-1$
		}
		catch(IllegalArgumentException _) {
			//$NON-NLS-1$
		}

		try {
			AddressUtil.createAgentAddress("a::a@a"); //$NON-NLS-1$
			fail("IllegalArgumentException was expected"); //$NON-NLS-1$
		}
		catch(IllegalArgumentException _) {
			//$NON-NLS-1$
		}

		try {
			AgentAddress hAdr = AddressUtil.createAgentAddress("a::"+this.uid.toString()); //$NON-NLS-1$
			assertNotNull(hAdr);
			assertEquals(this.uid, hAdr.getUUID());
		}
		catch(IllegalArgumentException _) {
			//$NON-NLS-1$
		}
	}

}
