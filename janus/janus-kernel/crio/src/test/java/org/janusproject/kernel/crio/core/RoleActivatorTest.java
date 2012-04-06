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
package org.janusproject.kernel.crio.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoleActivatorTest extends TestCase {

	private RoleStub r1;
	private Role2Stub r2;
	private Role3Stub r3;
	private Collection<Role> roles;
	private RoleActivator activator;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.activator = new RoleActivator();
		this.r1 = new RoleStub();
		this.r2 = new Role2Stub();
		this.r3 = new Role3Stub();
		this.roles = Arrays.asList(this.r1, this.r2, this.r3);
		for(Role r : this.roles) {
			this.activator.addRole(r);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.activator = null;
		this.r1 = null;
		this.r2 = null;
		this.r3 = null;
		this.roles = null;
		super.tearDown();
	}

	/**
	 */
	public void testHasActivable() {
		assertTrue(this.activator.hasActivable());
	}
	
}
