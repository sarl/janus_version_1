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
package org.janusproject.kernel.address;

import java.util.UUID;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AbstractAddressTest extends TestCase {

	private static final String NAME = "AbstractAddressUnitTest"; //$NON-NLS-1$
	
	private UUID uid;
	private AbstractAddress address;
	
	/**
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.uid = UUID.randomUUID();
		this.address = new AbstractAddressInstance(this.uid,NAME);
	}

	/**
	 */
	@Override
	protected void tearDown() throws Exception {
		this.address = null;
		this.uid = null;
		super.tearDown();
	}
	
	/**
	 */
	public void testHashCode() {
		assertEquals(this.uid.hashCode(), this.address.hashCode());
	}

	/**
	 */
	public void testGetID() {
		assertSame(this.uid, this.address.getUUID());
	}
	
	/**
	 */
	public void testEqualsObject() {
		assertFalse(this.address.equals((Object)null));
		assertFalse(this.address.equals(new Object()));
		
		assertTrue(this.address.equals((Object)this.address));

		AbstractAddress adr1 = new AbstractAddressInstance(this.uid, NAME);
		assertTrue(this.address.equals((Object)adr1));

		AbstractAddress adr2 = new AbstractAddressInstance(UUID.randomUUID(), NAME);
		assertFalse(this.address.equals((Object)adr2));
	}

	/**
	 */
	public void testEqualsAddress() {
		assertFalse(this.address.equals((Address)null));

		assertTrue(this.address.equals(this.address));

		AbstractAddress adr1 = new AbstractAddressInstance(this.uid, NAME);
		assertTrue(this.address.equals(adr1));

		AbstractAddress adr2 = new AbstractAddressInstance(UUID.randomUUID(), NAME);
		assertFalse(this.address.equals(adr2));
	}

    /**
     */
	public void testCompareToAddress() {
		assertTrue(this.address.compareTo(null)>0);

		assertEquals(0, this.address.compareTo(this.address));

		AbstractAddress adr1 = new AbstractAddressInstance(this.uid, NAME);
		assertTrue(this.address.equals(adr1));

		for(int i=0; i<200; ++i) {
			UUID uid1 = UUID.randomUUID();
			AbstractAddress adr2 = new AbstractAddressInstance(uid1, NAME);
			assertEquals(this.uid.compareTo(uid1), this.address.compareTo(adr2));
		}
	}	

	/**
	 * @author St&eacute;phane GALLAND &lt;stephane.galland@utbm.fr&gt;
	 * @version $FullVersion$
	 * @mavengroupid org.janus-project.kernel
	 * @mavenartifactid address
	 */
	private class AbstractAddressInstance extends AbstractAddress {

		/**
		 */
		private static final long serialVersionUID = -2690537490629450518L;
		
		private String name;
		
		/**
		 * @param uid
		 * @param name
		 */
		public AbstractAddressInstance(UUID uid, String name) {
			super(uid);
			this.name = name;
		}

		@Override
		public String getName() {
			return this.name;
		}

		/** {@inheritDoc}
		 */
		@Override
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
}
