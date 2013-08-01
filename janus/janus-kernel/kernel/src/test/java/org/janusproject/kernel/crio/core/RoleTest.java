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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.Role.MessageTransportService;
import org.janusproject.kernel.crio.organization.Group;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.mailbox.TreeSetMailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.random.RandomNumber;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoleTest extends TestCase {

	private CRIOContext context;
	private Organization organization;
	private GroupAddress address;
	private KernelScopeGroup group;
	private MembershipService membership;
	private boolean distributed, persistent;
	private RolePlayer player1;
	private RolePlayer player2;
	private Role role1;
	private Role role2;

	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.context = new CRIOContext(null);
		this.organization = OrganizationRepository.organization(this.context,
				Organization1Stub.class, null);
		this.membership = new MembershipServiceStub();
		this.distributed = RandomNumber.nextBoolean();
		this.persistent = RandomNumber.nextBoolean();
		this.address = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);
		assertNotNull(this.address);
		this.group = this.context.getGroupRepository().get(this.address);
		assertNotNull(this.group);
		this.player1 = new RolePlayerStub(this.context);
		this.player1.getCapacityContainer().addCapacity(
				new CapacityImplementationStub(true, 1000));
		this.player1.getCapacityContainer().addCapacity(
				new Capacity2ImplementationStub(true, 10000));
		assertNotNull(this.group.requestRole(this.player1, RoleStub.class, null,
				null));
		this.player2 = new RolePlayerStub(this.context);
		assertNotNull(this.group.requestRole(this.player2, RoleStub.class, null,
				null));
		this.role1 = this.group.getPlayedRole(this.player1.getAddress(),
				RoleStub.class);
		assertNotNull(this.role1);
		this.role2 = this.group.getPlayedRole(this.player2.getAddress(),
				RoleStub.class);
		assertNotNull(this.role2);
	}

	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.context.getOrganizationRepository().clear();
		this.role1 = null;
		this.role2 = null;
		this.player1 = null;
		this.group = null;
		this.organization = null;
		this.address = null;
		this.membership = null;
		this.context = null;
		super.tearDown();
	}

	private static void assertContains(Iterator<?> iterator, Object... objects) {
		ArrayList<Object> objs = new ArrayList<Object>(Arrays.asList(objects));
		assertNotNull(iterator);
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			assertTrue(objs.remove(obj));
		}
		assertTrue(objs.isEmpty());
	}

	private static void assertOneOf(Object actualValue, Object... expectedValues) {
		Object o;
		StringBuilder buffer = new StringBuilder();
		buffer.append("unexpected value. Expecting one of {"); //$NON-NLS-1$
		for (int i = 0; i < expectedValues.length; ++i) {
			o = expectedValues[i];
			if (o == actualValue || (o != null && o.equals(actualValue)))
				return;
			if (i > 0)
				buffer.append(", "); //$NON-NLS-1$
			buffer.append(o);
		}
		buffer.append("}; actual="); //$NON-NLS-1$
		buffer.append(actualValue);
		fail(buffer.toString());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#getPlayer()}.
	 */
	public void testGetPlayer() {
		assertEquals(this.player1.getAddress(), this.role1.getPlayer());
	}

	/**
	 */
	public void testGetExistingRoles() {
		Collection<Class<? extends Role>> roles;

		roles = this.role1.getExistingRoles();
		assertNotNull(roles);
		assertEquals(1, roles.size());
		assertTrue(roles.contains(RoleStub.class));
		assertFalse(roles.contains(Role2Stub.class));
		assertFalse(roles.contains(Role3Stub.class));

		assertNotNull(this.group.requestRole(this.player2, Role3Stub.class, null,
				null));

		roles = this.role1.getExistingRoles();
		assertNotNull(roles);
		assertEquals(2, roles.size());
		assertTrue(roles.contains(RoleStub.class));
		assertFalse(roles.contains(Role2Stub.class));
		assertTrue(roles.contains(Role3Stub.class));
	}

	/**
	 */
	public void testGetExistingRolesGroupAddress() {
		SizedIterator<Class<? extends Role>> roles;

		roles = this.role1.getExistingRoles(this.address);
		assertNotNull(roles);
		assertEquals(1, roles.totalSize());
		assertContains(roles, RoleStub.class);

		assertNotNull(this.group.requestRole(this.player2, Role3Stub.class, null,
				null));

		roles = this.role1.getExistingRoles(this.address);
		assertNotNull(roles);
		assertEquals(2, roles.totalSize());
		assertContains(roles, RoleStub.class, Role3Stub.class);

		roles = this.role1.getExistingRoles(new GroupAddress(UUID.randomUUID(), this.organization
				.getClass()));
		assertNotNull(roles);
		assertEquals(0, roles.totalSize());
	}

	/**
	 */
	public void testGetPlayerClass() {
		AgentAddress adr;

		adr = this.role1.getPlayer(RoleStub.class);
		assertOneOf(adr, this.player1.getAddress(), this.player2.getAddress());

		adr = this.role1.getPlayer(Role2Stub.class);
		assertNull(adr);

		adr = this.role1.getPlayer(Role3Stub.class);
		assertNull(adr);
	}

	/**
	 */
	public void testGetPlayerClassGroupAddress() {
		AgentAddress adr;

		adr = this.role1.getPlayer(RoleStub.class, this.address);
		assertOneOf(adr, this.player1.getAddress(), this.player2.getAddress());

		adr = this.role1.getPlayer(Role2Stub.class, this.address);
		assertNull(adr);

		adr = this.role1.getPlayer(Role3Stub.class, this.address);
		assertNull(adr);

		GroupAddress gadr = new GroupAddress(UUID.randomUUID(), this.organization.getClass());

		adr = this.role1.getPlayer(RoleStub.class, gadr);
		assertNull(adr);

		adr = this.role1.getPlayer(Role2Stub.class, gadr);
		assertNull(adr);

		adr = this.role1.getPlayer(Role3Stub.class, gadr);
		assertNull(adr);
	}

	/**
	 */
	public void testGetRoleAddressClass() {
		RoleAddress adr;

		adr = this.role1.getRoleAddress(RoleStub.class);
		assertNotNull(adr);
		assertEquals(this.group.getAddress(), adr.getGroup());
		assertEquals(RoleStub.class, adr.getRole());
		assertEquals(this.player1.getAddress(), adr.getPlayer());

		adr = this.role1.getRoleAddress(Role2Stub.class);
		assertNull(adr);

		adr = this.role1.getRoleAddress(Role3Stub.class);
		assertNull(adr);
	}

	/**
	 */
	public void testGetRoleAddressClassAgentAddress() {
		RoleAddress adr;

		adr = this.role1.getRoleAddress(RoleStub.class, this.player1.getAddress());
		assertNotNull(adr);
		assertEquals(this.group.getAddress(), adr.getGroup());
		assertEquals(RoleStub.class, adr.getRole());
		assertEquals(this.player1.getAddress(), adr.getPlayer());

		adr = this.role1.getRoleAddress(Role2Stub.class, this.player1.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(Role3Stub.class, this.player1.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(RoleStub.class, this.player2.getAddress());
		assertNotNull(adr);
		assertEquals(this.group.getAddress(), adr.getGroup());
		assertEquals(RoleStub.class, adr.getRole());
		assertEquals(this.player2.getAddress(), adr.getPlayer());

		adr = this.role1.getRoleAddress(Role2Stub.class, this.player2.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(Role3Stub.class, this.player2.getAddress());
		assertNull(adr);
	}

	/**
	 */
	public void testGetRoleAddressGroupAddressClassAgentAddress() {
		RoleAddress adr;

		adr = this.role1.getRoleAddress(this.address, RoleStub.class, this.player1.getAddress());
		assertNotNull(adr);
		assertEquals(this.group.getAddress(), adr.getGroup());
		assertEquals(RoleStub.class, adr.getRole());
		assertEquals(this.player1.getAddress(), adr.getPlayer());

		adr = this.role1.getRoleAddress(this.address, Role2Stub.class, this.player1.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(this.address, Role3Stub.class, this.player1.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(this.address, RoleStub.class, this.player2.getAddress());
		assertNotNull(adr);
		assertEquals(this.group.getAddress(), adr.getGroup());
		assertEquals(RoleStub.class, adr.getRole());
		assertEquals(this.player2.getAddress(), adr.getPlayer());

		adr = this.role1.getRoleAddress(this.address, Role2Stub.class, this.player2.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(this.address, Role3Stub.class, this.player2.getAddress());
		assertNull(adr);

		GroupAddress gAdr = new GroupAddress(UUID.randomUUID(), this.organization.getClass());

		adr = this.role1.getRoleAddress(gAdr, RoleStub.class, this.player1.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(gAdr, Role2Stub.class, this.player1.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(gAdr, Role3Stub.class, this.player1.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(gAdr, RoleStub.class, this.player2.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(gAdr, Role2Stub.class, this.player2.getAddress());
		assertNull(adr);

		adr = this.role1.getRoleAddress(gAdr, Role3Stub.class, this.player2.getAddress());
		assertNull(adr);
	}

	/**
	 */
	public void testGetRoleAddresses() {
		SizedIterator<RoleAddress> iterator;
		RoleAddress ra;
		
		iterator = this.role1.getRoleAddresses();
		assertNotNull(iterator);
		assertEquals(1, iterator.totalSize());
		assertEquals(1, iterator.rest());
		assertTrue(iterator.hasNext());
		ra = iterator.next();
		assertEquals(this.group.getAddress(), ra.getGroup());
		assertEquals(RoleStub.class, ra.getRole());
		assertEquals(this.player1.getAddress(), ra.getPlayer());

		assertEquals(1, iterator.totalSize());
		assertEquals(0, iterator.rest());
		assertFalse(iterator.hasNext());
	}
	
	/**
	 */
	public void testGetRoleAddressesInGroupAddress() {
		SizedIterator<RoleAddress> iterator;
		RoleAddress ra;
		
		iterator = this.role1.getRoleAddressesIn(this.group.getAddress());
		assertNotNull(iterator);
		assertEquals(2, iterator.totalSize());
		assertEquals(2, iterator.rest());
		assertTrue(iterator.hasNext());
		ra = iterator.next();
		
		boolean a = (ra.getPlayer().equals(this.player1.getAddress()));
		
		assertEquals(this.group.getAddress(), ra.getGroup());
		
		if (a) {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player1.getAddress(), ra.getPlayer());
		}
		else {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player2.getAddress(), ra.getPlayer());
		}

		assertEquals(2, iterator.totalSize());
		assertEquals(1, iterator.rest());
		assertTrue(iterator.hasNext());

		ra = iterator.next();
		assertEquals(this.group.getAddress(), ra.getGroup());
		
		if (a) {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player2.getAddress(), ra.getPlayer());
		}
		else {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player1.getAddress(), ra.getPlayer());
		}

		assertEquals(2, iterator.totalSize());
		assertEquals(0, iterator.rest());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetRoleAddressesInGroupAddressClass() {
		SizedIterator<RoleAddress> iterator;
		RoleAddress ra;
		
		iterator = this.role1.getRoleAddressesIn(this.group.getAddress(), RoleStub.class);
		assertNotNull(iterator);
		assertEquals(2, iterator.totalSize());
		assertEquals(2, iterator.rest());
		assertTrue(iterator.hasNext());
		ra = iterator.next();
		
		boolean a = (ra.getPlayer().equals(this.player1.getAddress()));
		
		assertEquals(this.group.getAddress(), ra.getGroup());
		
		if (a) {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player1.getAddress(), ra.getPlayer());
		}
		else {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player2.getAddress(), ra.getPlayer());
		}

		assertEquals(2, iterator.totalSize());
		assertEquals(1, iterator.rest());
		assertTrue(iterator.hasNext());

		ra = iterator.next();
		assertEquals(this.group.getAddress(), ra.getGroup());
		
		if (a) {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player2.getAddress(), ra.getPlayer());
		}
		else {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player1.getAddress(), ra.getPlayer());
		}

		assertEquals(2, iterator.totalSize());
		assertEquals(0, iterator.rest());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetRoleAddressesInGroupClass() {
		SizedIterator<RoleAddress> iterator;
		RoleAddress ra;
		
		iterator = this.role1.getRoleAddressesInGroup(RoleStub.class);
		assertNotNull(iterator);
		assertEquals(2, iterator.totalSize());
		assertEquals(2, iterator.rest());
		assertTrue(iterator.hasNext());
		ra = iterator.next();
		
		boolean a = (ra.getPlayer().equals(this.player1.getAddress()));
		
		assertEquals(this.group.getAddress(), ra.getGroup());
		
		if (a) {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player1.getAddress(), ra.getPlayer());
		}
		else {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player2.getAddress(), ra.getPlayer());
		}

		assertEquals(2, iterator.totalSize());
		assertEquals(1, iterator.rest());
		assertTrue(iterator.hasNext());

		ra = iterator.next();
		assertEquals(this.group.getAddress(), ra.getGroup());
		
		if (a) {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player2.getAddress(), ra.getPlayer());
		}
		else {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player1.getAddress(), ra.getPlayer());
		}

		assertEquals(2, iterator.totalSize());
		assertEquals(0, iterator.rest());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetRoleAddressesInGroup() {
		SizedIterator<RoleAddress> iterator;
		RoleAddress ra;
		
		iterator = this.role1.getRoleAddressesInGroup();
		assertNotNull(iterator);
		assertEquals(2, iterator.totalSize());
		assertEquals(2, iterator.rest());
		assertTrue(iterator.hasNext());
		ra = iterator.next();
		
		boolean a = (ra.getPlayer().equals(this.player1.getAddress()));
		
		assertEquals(this.group.getAddress(), ra.getGroup());
		
		if (a) {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player1.getAddress(), ra.getPlayer());
		}
		else {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player2.getAddress(), ra.getPlayer());
		}

		assertEquals(2, iterator.totalSize());
		assertEquals(1, iterator.rest());
		assertTrue(iterator.hasNext());

		ra = iterator.next();
		assertEquals(this.group.getAddress(), ra.getGroup());
		
		if (a) {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player2.getAddress(), ra.getPlayer());
		}
		else {
			assertEquals(RoleStub.class, ra.getRole());
			assertEquals(this.player1.getAddress(), ra.getPlayer());
		}

		assertEquals(2, iterator.totalSize());
		assertEquals(0, iterator.rest());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetPlayerGroups() {
		Collection<GroupAddress> grps;

		grps = this.role1.getPlayerGroups();
		assertNotNull(grps);
		assertEquals(1, grps.size());
		assertTrue(grps.contains(this.address));
	}

	/**
	 */
	public void testGetPlayerRolesGroupAddress() {
		Collection<Class<? extends Role>> roles;

		GroupAddress gadr = new GroupAddress(UUID.randomUUID(), this.organization.getClass());

		roles = this.role1.getPlayerRoles(this.address);
		assertNotNull(roles);
		assertEquals(1, roles.size());
		assertTrue(roles.contains(RoleStub.class));

		roles = this.role1.getPlayerRoles(gadr);
		assertNotNull(roles);
		assertEquals(0, roles.size());

		gadr = this.organization.createGroup(null, null, this.membership,
				this.distributed, this.persistent);
		assertNotNull(gadr);
		KernelScopeGroup g = this.context.getGroupRepository().get(gadr);
		assertNotNull(g);
		assertNotNull(g.requestRole(this.player1, Role3Stub.class, null, null));

		roles = this.role1.getPlayerRoles(gadr);
		assertNotNull(roles);
		assertEquals(1, roles.size());
		assertFalse(roles.contains(RoleStub.class));
		assertTrue(roles.contains(Role3Stub.class));

		assertNotNull(this.group.requestRole(this.player1, Role3Stub.class, null,
				null));

		roles = this.role1.getPlayerRoles(this.address);
		assertNotNull(roles);
		assertEquals(2, roles.size());
		assertTrue(roles.contains(RoleStub.class));
		assertTrue(roles.contains(Role3Stub.class));
	}

	/**
	 */
	public void testIsPlayingRoleClass() {
		assertTrue(this.role1.isPlayingRole(RoleStub.class));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class));

		GroupAddress gadr = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);
		KernelScopeGroup grp = this.context.getGroupRepository().get(gadr);

		assertTrue(this.role1.isPlayingRole(RoleStub.class));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class));

		assertNotNull(grp.requestRole(this.player2, Role3Stub.class, null, null));

		assertTrue(this.role1.isPlayingRole(RoleStub.class));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class));

		assertNotNull(this.group.requestRole(this.player2, Role3Stub.class, null,
				null));

		assertTrue(this.role1.isPlayingRole(RoleStub.class));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class));

		assertNotNull(this.group.requestRole(this.player1, Role3Stub.class, null,
				null));

		assertTrue(this.role1.isPlayingRole(RoleStub.class));
		assertTrue(this.role1.isPlayingRole(Role3Stub.class));
	}

	/**
	 */
	public void testIsPlayingRoleClassGroupAddress() {
		assertTrue(this.role1.isPlayingRole(RoleStub.class, this.address));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class, this.address));

		GroupAddress gadr = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);
		KernelScopeGroup grp = this.context.getGroupRepository().get(gadr);

		assertTrue(this.role1.isPlayingRole(RoleStub.class, this.address));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class, this.address));

		assertNotNull(grp.requestRole(this.player2, Role3Stub.class, null, null));

		assertTrue(this.role1.isPlayingRole(RoleStub.class, this.address));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class, this.address));

		assertNotNull(this.group.requestRole(this.player2, Role3Stub.class, null,
				null));

		assertTrue(this.role1.isPlayingRole(RoleStub.class, this.address));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class, this.address));

		assertNotNull(this.group.requestRole(this.player1, Role3Stub.class, null,
				null));

		assertTrue(this.role1.isPlayingRole(RoleStub.class, this.address));
		assertTrue(this.role1.isPlayingRole(Role3Stub.class, this.address));

		assertFalse(this.role1.isPlayingRole(RoleStub.class, gadr));
		assertFalse(this.role1.isPlayingRole(Role3Stub.class, gadr));
	}

	/**
	 */
	public void testIsPlayedRoleClass() {
		assertTrue(this.role1.isPlayedRole(RoleStub.class));
		assertFalse(this.role1.isPlayedRole(Role3Stub.class));

		GroupAddress gadr = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);
		KernelScopeGroup grp = this.context.getGroupRepository().get(gadr);

		assertTrue(this.role1.isPlayedRole(RoleStub.class));
		assertFalse(this.role1.isPlayedRole(Role3Stub.class));

		assertNotNull(grp.requestRole(this.player2, Role3Stub.class, null, null));

		assertTrue(this.role1.isPlayedRole(RoleStub.class));
		assertFalse(this.role1.isPlayedRole(Role3Stub.class));

		assertNotNull(this.group.requestRole(this.player2, Role3Stub.class, null,
				null));

		assertTrue(this.role1.isPlayedRole(RoleStub.class));
		assertTrue(this.role1.isPlayedRole(Role3Stub.class));
	}

	/**
	 */
	public void testIsPlayedRoleClassGroupAddress() {
		assertTrue(this.role1.isPlayedRole(RoleStub.class));
		assertFalse(this.role1.isPlayedRole(Role3Stub.class));

		GroupAddress gadr = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);
		KernelScopeGroup grp = this.context.getGroupRepository().get(gadr);

		assertTrue(this.role1.isPlayedRole(RoleStub.class, this.address));
		assertFalse(this.role1.isPlayedRole(Role3Stub.class, this.address));

		assertNotNull(grp.requestRole(this.player2, Role3Stub.class, null, null));

		assertTrue(this.role1.isPlayedRole(RoleStub.class, this.address));
		assertFalse(this.role1.isPlayedRole(Role3Stub.class, this.address));

		assertNotNull(this.group.requestRole(this.player2, Role3Stub.class, null,
				null));

		assertTrue(this.role1.isPlayedRole(RoleStub.class, this.address));
		assertTrue(this.role1.isPlayedRole(Role3Stub.class, this.address));

		assertFalse(this.role1.isPlayedRole(RoleStub.class, gadr));
		assertTrue(this.role1.isPlayedRole(Role3Stub.class, gadr));
	}

	/**
	 */
	public void testIsMemberOfGroupAddress() {
		GroupAddress gadr = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);
		KernelScopeGroup grp = this.context.getGroupRepository().get(gadr);

		assertTrue(this.role1.isMemberOf(this.address));
		assertFalse(this.role1.isMemberOf(gadr));

		assertNotNull(this.group.requestRole(this.player2, Role3Stub.class, null,
				null));

		assertTrue(this.role1.isMemberOf(this.address));
		assertFalse(this.role1.isMemberOf(gadr));

		assertNotNull(this.group.requestRole(this.player1, Role3Stub.class, null,
				null));

		assertTrue(this.role1.isMemberOf(this.address));
		assertFalse(this.role1.isMemberOf(gadr));

		assertNotNull(grp.requestRole(this.player1, Role3Stub.class, null, null));

		assertTrue(this.role1.isMemberOf(this.address));
		assertTrue(this.role1.isMemberOf(gadr));
	}

	/**
	 */
	public void testIsMemberOfAgentAddressGroupAddress() {
		GroupAddress gadr = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);
		KernelScopeGroup grp = this.context.getGroupRepository().get(gadr);

		assertTrue(this.role1
				.isMemberOf(this.player1.getAddress(), this.address));
		assertFalse(this.role1.isMemberOf(this.player1.getAddress(), gadr));

		assertNotNull(this.group.requestRole(this.player2, Role3Stub.class, null,
				null));

		assertTrue(this.role1
				.isMemberOf(this.player1.getAddress(), this.address));
		assertFalse(this.role1.isMemberOf(this.player1.getAddress(), gadr));

		assertNotNull(this.group.requestRole(this.player1, Role3Stub.class, null,
				null));

		assertTrue(this.role1
				.isMemberOf(this.player1.getAddress(), this.address));
		assertFalse(this.role1.isMemberOf(this.player1.getAddress(), gadr));

		assertNotNull(grp.requestRole(this.player1, Role3Stub.class, null, null));

		assertTrue(this.role1
				.isMemberOf(this.player1.getAddress(), this.address));
		assertTrue(this.role1.isMemberOf(this.player1.getAddress(), gadr));

		assertTrue(this.role1
				.isMemberOf(this.player2.getAddress(), this.address));
		assertFalse(this.role1.isMemberOf(this.player2.getAddress(), gadr));
	}

	/**
	 */
	public void testIsGroup() {
		GroupAddress gadr = new GroupAddress(UUID.randomUUID(), this.organization.getClass());

		assertTrue(this.role1.isGroup(this.address));
		assertFalse(this.role1.isGroup(gadr));

		gadr = this.organization.createGroup(null, null, this.membership,
				this.distributed, this.persistent);

		assertTrue(this.role1.isGroup(this.address));
		assertTrue(this.role1.isGroup(gadr));
	}

	/**
	 */
	public void testGetGroups() {
		List<GroupAddress> grps;

		grps = this.role1.getExistingsGroupsOfSameOrganization();
		assertNotNull(grps);
		assertEquals(1, grps.size());
		assertTrue(grps.contains(this.address));

		GroupAddress gadr = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);

		grps = this.role1.getExistingsGroupsOfSameOrganization();
		assertNotNull(grps);
		assertEquals(2, grps.size());
		assertTrue(grps.contains(this.address));
		assertTrue(grps.contains(gadr));
	}

	/**
	 */
	public void testGetGroupsClass() {
		List<GroupAddress> grps;

		grps = this.role1.getExistingGroups(Organization1Stub.class);
		assertNotNull(grps);
		assertEquals(1, grps.size());
		assertTrue(grps.contains(this.address));

		GroupAddress gadr = this.organization.createGroup(null, null,
				this.membership, this.distributed, this.persistent);

		grps = this.role1.getExistingGroups(Organization1Stub.class);
		assertNotNull(grps);
		assertEquals(2, grps.size());
		assertTrue(grps.contains(this.address));
		assertTrue(grps.contains(gadr));

		grps = this.role1.getExistingGroups(Organization2Stub.class);
		assertNotNull(grps);
		assertEquals(0, grps.size());
	}

	/**
	 * Test method for {@link org.janusproject.kernel.crio.core.Role#getGroupAddress()}
	 * .
	 */
	public void testGetGroupAddress() {
		assertEquals(this.group.getAddress(), this.role1.getGroupAddress());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#getMemory()}.
	 */
	public void testGetMemory() {
		Memory m;
		assertNotNull(m = this.role1.getMemory());
		assertSame(m, this.role1.getMemory());
	}

	/**
	 */
	public void testGetMailbox() {
		Mailbox m;
		assertNotNull(m = this.role1.getMailbox());
		assertSame(m, this.role1.getMailbox());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#setMailbox(org.janusproject.kernel.mailbox.Mailbox)}
	 * .
	 */
	public void testSetMailbox() {
		Mailbox m;
		assertNotNull(m = this.role1.getMailbox());
		assertSame(m, this.role1.getMailbox());

		Mailbox m2 = new TreeSetMailbox();
		this.role1.setMailbox(m2);

		assertSame(m2, this.role1.getMailbox());
	}

	/**
	 */
	public void testGetMessageTransportService() {
		MessageTransportService mts;
		assertNotNull(mts = this.role1.getMessageTransportService());
		assertSame(mts, this.role1.getMessageTransportService());
	}

	/**
	 * @throws Exception
	 */
	public void testGetMessage() throws Exception {
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		assertNull(this.role1.getMessage());

		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(), m2);
		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(), m1);

		Mailbox mb = this.role1.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		assertSame(m1, this.role1.getMessage());
		assertSame(m2, this.role1.getMessage());
	}

	/**
	 * @throws Exception
	 */
	public void testPeekMessage() throws Exception {
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		assertNull(this.role1.peekMessage());

		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(), m2);
		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(), m1);

		Mailbox mb = this.role1.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		assertSame(m1, this.role1.peekMessage());
		assertSame(m1, this.role1.peekMessage());
	}

	/**
	 * @throws Exception
	 */
	public void testGetMessages() throws Exception {
		Iterator<Message> iterator;
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		iterator = this.role1.getMessages().iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(), m2);
		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(), m1);

		Mailbox mb = this.role1.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		iterator = this.role1.getMessages().iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = this.role1.getMessages().iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	/**
	 * @throws Exception
	 */
	public void testPeekMessages() throws Exception {
		Iterator<Message> iterator;
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		iterator = this.role1.peekMessages().iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(), m2);
		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(), m1);

		Mailbox mb = this.role1.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		iterator = this.role1.peekMessages().iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = this.role1.peekMessages().iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 * @throws Exception
	 */
	public void testHasMessage() throws Exception {
		assertFalse(this.role1.hasMessage());

		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(),
				new Message());

		Mailbox mb = this.role1.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		assertTrue(this.role1.hasMessage());
	}

	/**
	 * @throws Exception
	 */
	public void testGetMailboxSize() throws Exception {
		assertEquals(0, this.role1.getMailboxSize());

		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(),
				new Message());

		{
			Mailbox mb = this.role1.getMailbox();
			if (mb instanceof BufferedMailbox) {
				((BufferedMailbox) mb).synchronizeMessages();
			}
		}
		assertEquals(1, this.role1.getMailboxSize());

		this.role1.sendMessage(RoleStub.class, this.player1.getAddress(),
				new Message());
		{
			Mailbox mb = this.role1.getMailbox();
			if (mb instanceof BufferedMailbox) {
				((BufferedMailbox) mb).synchronizeMessages();
			}
		}

		assertEquals(2, this.role1.getMailboxSize());
	}

	/**
	 * @throws Exception
	 */
	public void testSendMessageClassAgentAddressMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress r = this.role1.sendMessage(RoleStub.class, this.player2.getAddress(), msg);
		assertNotNull(r);
		assertEquals(this.group.getAddress(), this.group.getAddress());
		assertEquals(RoleStub.class, r.getRole());
		assertEquals(this.player2.getAddress(), r.getPlayer());

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testSendMessageRoleAddressMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress r = this.role1.sendMessage(new RoleAddress(
				this.group.getAddress(), RoleStub.class, this.player2.getAddress()), msg);
		assertNotNull(r);
		assertEquals(this.group.getAddress(), this.group.getAddress());
		assertEquals(RoleStub.class, r.getRole());
		assertEquals(this.player2.getAddress(), r.getPlayer());

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testReplyToMessageMessageMessage() throws Exception {
		//TODO: create the unit test
	}

	/**
	 * @throws Exception
	 */
	public void testForwardMessageClassAgentAddressMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		this.role1.forwardMessage(RoleStub.class, this.player2.getAddress(), msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testForwardMessageRoleAddressMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		this.role1.forwardMessage(new RoleAddress(this.group.getAddress(), RoleStub.class, this.player2.getAddress()), msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testForwardMessageMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		RoleAddress r = this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress receiver = new RoleAddress(this.group.getAddress(), RoleStub.class, this.player2.getAddress());
		
		InteractionUtilStub.updateContext(msg, 
				r,
				receiver,
				1024);

		assertEquals(receiver, this.role1.forwardMessage(msg));

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testSendMessageClassMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress adr = this.role1.sendMessage(RoleStub.class, msg);
		assertNotNull(adr);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(this.player2.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(player3.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testForwardMessageClassMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress adr = this.role1.forwardMessage(RoleStub.class, msg);
		assertNotNull(adr);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(this.player2.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(player3.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testSendMessageClassMessageReceiverSelectionPolicyMessage()
			throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress adr = this.role1.sendMessage(RoleStub.class,
				new PolicyStub(player3.getAddress()), msg);

		assertNotNull(adr);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testForwardMessageClassMessageReceiverSelectionPolicyMessage()
			throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress adr = this.role1.forwardMessage(RoleStub.class,
				new PolicyStub(player3.getAddress()), msg);

		assertNotNull(adr);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testBroadcastMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		this.role1.broadcastMessage(RoleStub.class, msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testForwardBroadcastMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		this.role1.forwardBroadcastMessage(RoleStub.class, msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group.getPlayedRole(
				this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(
				this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(),
				Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#executeCapacityCall(java.lang.Class, java.lang.Object[])}
	 * .
	 * 
	 * @throws Exception
	 */
	public void testExecuteCapacityCall() throws Exception {
		CapacityContext context = this.role1.executeCapacityCall(
				CapacityStub.class, 'a', 'b', 'c');
		assertNotNull(context);
		assertNotNull(context.getIdentifier());
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals(1, context.getOutputValueAt(0));
		assertEquals(2, context.getOutputValueAt(1));
		assertEquals(3, context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#submitCapacityCall(java.lang.Class, java.lang.Object[])}
	 * .
	 * 
	 * @throws Exception
	 */
	public void testSubmitCapacityCall() throws Exception {
		UUID id = this.role1.submitCapacityCall(CapacityStub.class, 'a', 'b',
				'c');
		assertNotNull(id);
		CapacityContext context = this.role1.waitCapacityCallResult(id);
		assertNotNull(context);
		assertNotNull(context.getIdentifier());
		assertFalse(context.isFailed());
		assertTrue(context.isResultAvailable());
		assertEquals(3, context.getOutputValueCount());
		assertNull(context.getOutputValueAt(-1));
		assertEquals(1, context.getOutputValueAt(0));
		assertEquals(2, context.getOutputValueAt(1));
		assertEquals(3, context.getOutputValueAt(2));
		assertNull(context.getOutputValueAt(3));
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#cancelCapacityCall(UUID)}.
	 * 
	 * @throws Exception
	 */
	public void testCancelCapacityCall() throws Exception {
		UUID id = this.role1.submitCapacityCall(Capacity2Stub.class, 'a', 'b',
				'c');
		assertNotNull(id);

		assertTrue(this.role1.cancelCapacityCall(id));

		CapacityContext context = this.role1.getCapacityCallResult(id);
		assertNotNull(context);
		assertNotNull(context.getIdentifier());
		assertTrue(context.isFailed());
		assertFalse(context.isResultAvailable());
		assertEquals(0, context.getOutputValueCount());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#getOrganization(Class)}.
	 */
	public void testOrganizationClass() {
		Organization o = this.role1.getOrganization(Organization2Stub.class);
		assertNotNull(o);
		assertSame(o, this.role1.getOrganization(Organization2Stub.class));
	}

	/**
	 */
	public void testCreateGroupOrganization() {
		GroupAddress adr = this.role1.createGroup(Organization1Stub.class);
		assertNotNull(adr);
		assertNotSame(this.group.getAddress(), adr);
	}

	/**
	 */
	public void testGetGroupOrganization() {
		assertEquals(this.address, this.role1.getExistingGroup(Organization1Stub.class));
		GroupAddress adr = this.role1.createGroup(Organization1Stub.class);
		assertNotNull(adr);
		for (int i = 0; i < 50; ++i) {
			GroupAddress a = this.role1.getExistingGroup(Organization1Stub.class);
			assertNotNull(a);
			assertTrue(a.equals(this.address) || a.equals(adr));
		}
	}

	/**
	 */
	public void testGetGroupGroupAddress_userData_validAccess() {
		Group grp = this.role1.getGroupObject(this.address);
		assertNotNull(grp);
		
		assertNull(grp.getPrivateUserData());
		Object userData1 = new Object();
		Object userData2 = new Object();
		assertNull(grp.setPrivateUserData(userData1));
		assertSame(userData1, grp.getPrivateUserData());
		assertSame(userData1, grp.setPrivateUserData(userData2));
		assertSame(userData2, grp.getPrivateUserData());
		assertSame(userData2, grp.setPrivateUserData(null));
		assertNull(grp.getPrivateUserData());
	}

	/**
	 */
	public void testGetGroupGroupAddress_userData_invalidAccess() {
		GroupAddress otherAddress = this.organization.createGroup(
				null, null, this.membership, this.distributed,
				this.persistent);
		assertNotNull(otherAddress);
		KernelScopeGroup otherGroup = this.context.getGroupRepository().get(otherAddress);
		assertNotNull(otherGroup);

		Group grp = this.role1.getGroupObject(otherAddress);
		assertNotNull(grp);
		
		Object userData1 = new Object();
		Object userData2 = new Object();

		assertNull(grp.getPrivateUserData());
		assertNull(grp.setPrivateUserData(userData1));
		assertNull(grp.getPrivateUserData());
		assertNull(grp.setPrivateUserData(userData2));
		assertNull(grp.getPrivateUserData());
		assertNull(grp.setPrivateUserData(null));
		assertNull(grp.getPrivateUserData());
	}

	/**
	 */
	public void testGetOrCreateGroup() {
		assertEquals(this.address, this.role1
				.getOrCreateGroup(Organization1Stub.class));
		assertFalse(this.address.equals(this.role1
				.getOrCreateGroup(Organization2Stub.class)));
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#requestRole(java.lang.Class, org.janusproject.kernel.crio.core.GroupAddress, java.lang.Object[])}
	 * .
	 */
	public void testRequestRole() {
		assertNull(this.role1.requestRole(Role2Stub.class, this.address));
		assertNotNull(this.role1.requestRole(Role3Stub.class, this.address));
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#leaveRole(Class)}.
	 */
	public void testLeaveRoleClass() {
		assertFalse(this.role1.leaveRole(Role2Stub.class));
		assertTrue(this.role1.leaveRole(RoleStub.class));
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#leaveRole(Class, GroupAddress)}
	 * .
	 */
	public void testLeaveRoleClassGroupAddress() {
		assertFalse(this.role1.leaveRole(Role2Stub.class, this.address));
		assertTrue(this.role1.leaveRole(RoleStub.class, this.address));
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#getPlayers()}.
	 */
	public void testGetPlayers() {
		Iterator<AgentAddress> iterator = this.role1.getPlayers();
		assertNotNull(iterator);
		assertContains(iterator, this.player1.getAddress(), this.player2
				.getAddress());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#getPlayers(java.lang.Class)}
	 * .
	 */
	public void testGetPlayersClass() {
		Iterator<AgentAddress> iterator = this.role1.getPlayers(RoleStub.class);
		assertNotNull(iterator);
		assertContains(iterator, this.player1.getAddress(), this.player2
				.getAddress());

		iterator = this.role1.getPlayers(Role2Stub.class);
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.Role#getPlayers(java.lang.Class, org.janusproject.kernel.crio.core.GroupAddress)}
	 * .
	 */
	public void testGetPlayersClassGroupAddress() {
		Iterator<AgentAddress> iterator = this.role1.getPlayers(RoleStub.class,
				this.group.getAddress());
		assertNotNull(iterator);
		assertContains(iterator, this.player1.getAddress(), this.player2
				.getAddress());

		iterator = this.role1.getPlayers(Role2Stub.class, this.group
				.getAddress());
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetTimeManager() {
		KernelTimeManager tm = this.role1.getTimeManager();
		assertNotNull(tm);
		assertSame(tm, this.role1.getTimeManager());
	}

	/**
	 */
	public void testGroupListener() {
		GroupListenerStub listener = new GroupListenerStub();
		
		this.role1.addGroupListener(listener);
		
		GroupAddress adr = this.role1.createGroup(this.organization.getClass());
		assertNotNull(adr);
		
		listener.assertCreation(adr);
		listener.assertNull();
	}

	/**
	 */
	public void testRolePlayingListener() {
		RolePlayingListenerStub listener = new RolePlayingListenerStub();
				
		GroupAddress adr = this.role1.createGroup(Organization1Stub.class);
		assertNotNull(adr);
		
		this.role1.getGroupObject(adr).addRolePlayingListener(listener);

		listener.assertNull();
		
		assertNotNull(this.role1.requestRole(RoleStub.class, adr));
		
		listener.assertTaken(RoleStub.class, adr, this.role1.getPlayer());
		listener.assertNull();
		
		assertTrue(this.role1.leaveRole(RoleStub.class, adr));

		listener.assertReleased(RoleStub.class, adr, this.role1.getPlayer());
		listener.assertNull();
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class PolicyStub implements MessageReceiverSelectionPolicy {

		private final AgentAddress adr;

		/**
		 * @param a
		 */
		public PolicyStub(AgentAddress a) {
			this.adr = a;
		}

		@Override
		public AgentAddress selectEntity(AgentAddress sender,
				List<? extends AgentAddress> availableEntities) {
			return this.adr;
		}

		@Override
		public AgentAddress selectEntity(AgentAddress sender,
				DirectAccessCollection<? extends AgentAddress> availableEntities) {
			return this.adr;
		}

		@Override
		public AgentAddress selectEntity(AgentAddress sender,
				SizedIterator<? extends AgentAddress> availableEntities) {
			return this.adr;
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class CapacityImplementationStub extends
			CapacityImplementation implements CapacityStub {

		private final boolean failed;
		private final int sleep;

		/**
		 * @param f
		 * @param s
		 */
		public CapacityImplementationStub(boolean f, int s) {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
			this.failed = f;
			this.sleep = s;
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			assertEquals('a', call.getInputValues()[0]);
			assertEquals('b', call.getInputValues()[1]);
			assertEquals('c', call.getInputValues()[2]);

			if (this.sleep > 0) {
				Thread.sleep(this.sleep);
			}

			call.setOutputValues(1, 2, 3);
			if (!this.failed)
				call.fail();
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Capacity2ImplementationStub extends
			CapacityImplementation implements Capacity2Stub {

		private final boolean failed;
		private final int sleep;

		/**
		 * @param f
		 * @param s
		 */
		public Capacity2ImplementationStub(boolean f, int s) {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
			this.failed = f;
			this.sleep = s;
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			assertEquals('a', call.getInputValues()[0]);
			assertEquals('b', call.getInputValues()[1]);
			assertEquals('c', call.getInputValues()[2]);

			if (this.sleep > 0) {
				Thread.sleep(this.sleep);
			}

			call.setOutputValues(1, 2, 3);
			if (!this.failed)
				call.fail();
		}

	}

}
