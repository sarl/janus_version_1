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
package org.janusproject.kernel.configuration;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PropertiesJanusPropertyProviderTest extends TestCase {

	private PropertiesJanusPropertyProvider provider;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.provider = new PropertiesJanusPropertyProvider();
		this.provider.setProperty("A", "aaa"); //$NON-NLS-1$ //$NON-NLS-2$
		this.provider.setProperty("B", "bbb"); //$NON-NLS-1$ //$NON-NLS-2$
		this.provider.setProperty("C", "ccc"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	protected void tearDown() throws Exception {
		this.provider = null;
		super.tearDown();
	}
	
	/**
	 */
	public void testContainsProperty() {
		assertTrue(this.provider.containsProperty("A")); //$NON-NLS-1$
		assertTrue(this.provider.containsProperty("B")); //$NON-NLS-1$
		assertTrue(this.provider.containsProperty("C")); //$NON-NLS-1$
		assertFalse(this.provider.containsProperty("D")); //$NON-NLS-1$
		assertFalse(this.provider.containsProperty("E")); //$NON-NLS-1$
		assertFalse(this.provider.containsProperty("F")); //$NON-NLS-1$
	}

	/**
	 */
	public void testIsReadOnlyProperty() {
		assertFalse(this.provider.isReadOnlyProperty("A")); //$NON-NLS-1$
		assertFalse(this.provider.isReadOnlyProperty("B")); //$NON-NLS-1$
		assertFalse(this.provider.isReadOnlyProperty("C")); //$NON-NLS-1$
		assertFalse(this.provider.isReadOnlyProperty("D")); //$NON-NLS-1$
		assertFalse(this.provider.isReadOnlyProperty("E")); //$NON-NLS-1$
		assertFalse(this.provider.isReadOnlyProperty("F")); //$NON-NLS-1$
	}

}
