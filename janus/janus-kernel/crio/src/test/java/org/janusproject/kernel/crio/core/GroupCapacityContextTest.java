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
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.crio.capacity.CapacityCaller;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class GroupCapacityContextTest extends TestCase {

	private CRIOContext crioContext;
	private GroupCapacityContext context;
	private CapacityCaller caller;
	private Organization organization;
	private KernelScopeGroup group;
	private Role role;
	private Object[] input;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.crioContext = new CRIOContext(null);
		this.caller = new CapacityCallerStub();
		this.organization = new Organization1Stub(this.crioContext);
		this.group = new KernelScopeGroup(this.organization, new GroupAddress(UUID.randomUUID(), this.organization.getClass()), false, true, null);
		this.role = new RoleStub();
		this.input = new Object[] { 1, 2, 3 };
		this.context = new GroupCapacityContext(
				this.caller, this.group,
				this.role,
				CapacityStub.class,
				CapacityImplementationType.DIRECT_ACTOMIC,
				this.input);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.caller = null;
		this.organization = null;
		this.group = null;
		this.role = null;
		this.context = null;
		this.input = null;
		this.crioContext = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetGroup() {
		assertSame(this.group, this.context.getGroup());
	}

	/**
	 */
	public void testGetRole() {
		assertSame(this.role, this.context.getRole());
	}

	/**
	 */
	public void testGetRoleType() {
		assertEquals(RoleStub.class, this.context.getRoleType());
	}

}
