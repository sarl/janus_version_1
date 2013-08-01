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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentmemory.BlackBoardMemory;
import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.capacity.TreeCapacityContainer;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
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
public class RolePlayerTest extends TestCase {

	private CRIOContext context;
	private Organization organization1;
	private Organization organization2;
	private KernelScopeGroup group1;
	private KernelScopeGroup group2;
	private MembershipService membership;
	private boolean distributed, persistent;
	private AgentAddress address1;
	private AgentAddress address2;
	private RolePlayer player1;
	private RolePlayer player2;

	/**
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.context = new CRIOContext(null);
		this.organization1 = OrganizationRepository.organization(this.context,
				Organization1Stub.class, null);
		this.organization2 = OrganizationRepository.organization(this.context,
				Organization2Stub.class, null);
		this.membership = new MembershipServiceStub();
		this.distributed = RandomNumber.nextBoolean();
		this.persistent = RandomNumber.nextBoolean();
		GroupAddress grpAdr = this.organization1.createGroup(null, null,
				this.membership, this.distributed, this.persistent, "group1"); //$NON-NLS-1$
		assertNotNull(grpAdr);
		this.group1 = this.context.getGroupRepository().get(grpAdr);
		assertNotNull(this.group1);
		grpAdr = this.organization2.createGroup(null, null, this.membership,
				this.distributed, this.persistent, "group2"); //$NON-NLS-1$
		assertNotNull(grpAdr);
		this.group2 = this.context.getGroupRepository().get(grpAdr);
		assertNotNull(this.group2);
		this.address1 = new AgentAddressStub("player2"); //$NON-NLS-1$
		this.player1 = new RolePlayerStub(this.context, this.address1);
		this.player1.getCapacityContainer().addCapacity(
				new CapacityImplementationStub(true, 1000));
		this.player1.getCapacityContainer().addCapacity(
				new Capacity2ImplementationStub(true, 10000));
		assertNotNull(this.group1.requestRole(this.player1, RoleStub.class, null,
				null));
		this.address2 = new AgentAddressStub("player2"); //$NON-NLS-1$
		this.player2 = new RolePlayerStub(this.context, this.address2);
	}

	/**
	 */
	@Override
	public void tearDown() throws Exception {
		this.player1 = null;
		this.player2 = null;
		this.address1 = null;
		this.address2 = null;
		this.group1 = null;
		this.group2 = null;
		this.organization1 = null;
		this.organization2 = null;
		this.membership = null;
		this.context = null;
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#getAddress()}.
	 */
	public void testGetAddress() {
		assertEquals(this.address1, this.player1.getAddress());
	}

	/**
	 */
	public void testGetTimeManager() {
		KernelTimeManager tm = this.player1.getTimeManager();
		assertNotNull(tm);
		assertSame(tm, this.player1.getTimeManager());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#getMemory()}.
	 */
	public void testGetMemory() {
		Memory m;
		assertNotNull(m = this.player1.getMemory());
		assertSame(m, this.player1.getMemory());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#setMemory(org.janusproject.kernel.agentmemory.Memory)}
	 * .
	 */
	public void testSetMemory() {
		Memory m;
		assertNotNull(m = this.player1.getMemory());
		assertSame(m, this.player1.getMemory());

		Memory m2 = new BlackBoardMemory();
		this.player1.setMemory(m2);

		assertSame(m2, this.player1.getMemory());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#getRoleActivator()}.
	 */
	public void testGetRoleActivator() {
		RoleActivator m;
		assertNotNull(m = this.player1.getRoleActivator());
		assertSame(m, this.player1.getRoleActivator());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#setRoleActivator(RoleActivator)}
	 * .
	 */
	public void testSetRoleActivatorRoleActivator() {
		RoleActivator m;
		assertNotNull(m = this.player1.getRoleActivator());
		assertSame(m, this.player1.getRoleActivator());

		RoleActivator m2 = new RoleActivator();
		this.player1.setRoleActivator(m2);

		assertSame(m2, this.player1.getRoleActivator());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#getCapacityContainer()}
	 * .
	 */
	public void testGetCapacityContainer() {
		CapacityContainer m;
		assertNotNull(m = this.player1.getCapacityContainer());
		assertSame(m, this.player1.getCapacityContainer());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#setCapacityContainer(org.janusproject.kernel.crio.capacity.CapacityContainer)}
	 * .
	 */
	public void testSetCapacityContainer() {
		CapacityContainer m;
		assertNotNull(m = this.player1.getCapacityContainer());
		assertSame(m, this.player1.getCapacityContainer());

		CapacityContainer m2 = new TreeCapacityContainer();
		this.player1.setCapacityContainer(m2);

		assertSame(m2, this.player1.getCapacityContainer());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#getCredentials()}.
	 */
	public void testGetCredentials() {
		assertNull(this.player1.getCredentials());
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#getRole(org.janusproject.kernel.crio.core.GroupAddress, java.lang.Class)}
	 * .
	 */
	public void testGetRole() {
		Role r;
		assertNotNull(r = this.player1.getRole(this.group1.getAddress(),
				RoleStub.class));
		assertSame(r, this.player1.getRole(this.group1.getAddress(),
				RoleStub.class));
	}

	/**
	 * Test method for
	 * {@link org.janusproject.kernel.crio.core.RolePlayer#getRoles(org.janusproject.kernel.crio.core.GroupAddress)}
	 * .
	 */
	public void testGetRolesGroupAddress() {
		Collection<Class<? extends Role>> roles = this.player1
				.getRoles(this.group1.getAddress());
		assertNotNull(roles);
		Iterator<Class<? extends Role>> iterator = roles.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testIsPlayingRole() {
		assertTrue(this.player1.isPlayingRole());
		assertTrue(this.group1.leaveRole(this.player1, RoleStub.class));
		assertFalse(this.player1.isPlayingRole());
	}

	/**
	 */
	public void testIsPlayingRoleClass() {
		assertTrue(this.player1.isPlayingRole(RoleStub.class));
		assertFalse(this.player1.isPlayingRole(Role2Stub.class));
		assertTrue(this.group1.leaveRole(this.player1, RoleStub.class));
		assertFalse(this.player1.isPlayingRole(RoleStub.class));
		assertFalse(this.player1.isPlayingRole(Role2Stub.class));
	}

	/**
	 */
	public void testIsMemberOfGroupAddress() {
		assertTrue(this.player1.isMemberOf(this.group1.getAddress()));
		assertFalse(this.player1.isMemberOf(this.group2.getAddress()));
		assertTrue(this.group1.leaveRole(this.player1, RoleStub.class));
		assertFalse(this.player1.isMemberOf(this.group1.getAddress()));
		assertFalse(this.player1.isMemberOf(this.group2.getAddress()));

		assertFalse(this.player2.isMemberOf(this.group1.getAddress()));
		assertFalse(this.player2.isMemberOf(this.group2.getAddress()));
	}

	/**
	 */
	public void testIsMemberOfAgentAddressGroupAddress_player1() {
		assertTrue(this.player1.isMemberOf(this.address1, this.group1
				.getAddress()));
		assertFalse(this.player1.isMemberOf(this.address1, this.group2
				.getAddress()));
		assertTrue(this.group1.leaveRole(this.player1, RoleStub.class));
		assertFalse(this.player1.isMemberOf(this.address1, this.group1
				.getAddress()));
		assertFalse(this.player1.isMemberOf(this.address1, this.group2
				.getAddress()));

		assertFalse(this.player1.isMemberOf(this.address2, this.group1
				.getAddress()));
		assertFalse(this.player1.isMemberOf(this.address2, this.group2
				.getAddress()));
	}

	/**
	 */
	public void testIsMemberOfAgentAddressGroupAddress_player2() {
		assertTrue(this.player2.isMemberOf(this.address1, this.group1
				.getAddress()));
		assertFalse(this.player2.isMemberOf(this.address1, this.group2
				.getAddress()));
		assertTrue(this.group1.leaveRole(this.player1, RoleStub.class));
		assertFalse(this.player2.isMemberOf(this.address1, this.group1
				.getAddress()));
		assertFalse(this.player2.isMemberOf(this.address1, this.group2
				.getAddress()));

		assertFalse(this.player2.isMemberOf(this.address2, this.group1
				.getAddress()));
		assertFalse(this.player2.isMemberOf(this.address2, this.group2
				.getAddress()));
	}

	/**
	 */
	public void testIsPlayedRoleClass() {
		assertTrue(this.player1.isPlayedRole(RoleStub.class));
		assertFalse(this.player1.isPlayedRole(Role2Stub.class));
		assertFalse(this.player1.isPlayedRole(Role3Stub.class));

		assertTrue(this.group1.leaveRole(this.player1, RoleStub.class));

		assertFalse(this.player1.isPlayedRole(RoleStub.class));
		assertFalse(this.player1.isPlayedRole(Role2Stub.class));
		assertFalse(this.player1.isPlayedRole(Role3Stub.class));
	}

	/**
	 */
	public void testIsPlayedRoleClassGroupAddress() {
		GroupAddress adr = new GroupAddress(UUID.randomUUID(), this.organization1.getClass());

		assertTrue(this.player1.isPlayingRole(RoleStub.class, this.group1
				.getAddress()));
		assertFalse(this.player1.isPlayingRole(Role2Stub.class, this.group1
				.getAddress()));
		assertFalse(this.player1.isPlayingRole(Role3Stub.class, this.group1
				.getAddress()));
		assertFalse(this.player1.isPlayingRole(RoleStub.class, adr));
		assertFalse(this.player1.isPlayingRole(Role2Stub.class, adr));
		assertFalse(this.player1.isPlayingRole(Role3Stub.class, adr));

		assertTrue(this.group1.leaveRole(this.player1, RoleStub.class));

		assertFalse(this.player1.isPlayingRole(RoleStub.class, this.group1
				.getAddress()));
		assertFalse(this.player1.isPlayingRole(Role2Stub.class, this.group1
				.getAddress()));
		assertFalse(this.player1.isPlayingRole(Role3Stub.class, this.group1
				.getAddress()));
		assertFalse(this.player1.isPlayingRole(RoleStub.class, adr));
		assertFalse(this.player1.isPlayingRole(Role2Stub.class, adr));
		assertFalse(this.player1.isPlayingRole(Role3Stub.class, adr));
	}

	/**
	 */
	public void testGetExistingRoles() {
		Collection<Class<? extends Role>> roles = this.player1
				.getExistingRoles();
		assertNotNull(roles);
		Iterator<Class<? extends Role>> iterator = roles.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetExistingRolesGroupAddress() {
		SizedIterator<Class<? extends Role>> roles;

		roles = this.player1.getExistingRoles(this.group1.getAddress());
		assertNotNull(roles);
		assertTrue(roles.hasNext());
		assertEquals(RoleStub.class, roles.next());
		assertFalse(roles.hasNext());

		roles = this.player1.getExistingRoles(this.group2.getAddress());
		assertNotNull(roles);
		assertFalse(roles.hasNext());
	}

	/**
	 */
	public void testGetRoles() {
		Collection<Class<? extends Role>> roles = this.player1.getRoles();
		assertNotNull(roles);
		Iterator<Class<? extends Role>> iterator = roles.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertFalse(iterator.hasNext());

		roles = this.player2.getRoles();
		assertNotNull(roles);
		iterator = roles.iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetGroups() {
		Collection<GroupAddress> groups = this.player1.getGroups();
		assertNotNull(groups);
		Iterator<GroupAddress> iterator = groups.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(this.group1.getAddress(), iterator.next());
		assertFalse(iterator.hasNext());

		groups = this.player2.getGroups();
		assertNotNull(groups);
		iterator = groups.iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testIsGroup() {
		GroupAddress gadr = new GroupAddress(UUID.randomUUID(), this.organization1.getClass());

		assertTrue(this.player1.isGroup(this.group1.getAddress()));
		assertTrue(this.player1.isGroup(this.group2.getAddress()));
		assertTrue(this.player2.isGroup(this.group1.getAddress()));
		assertTrue(this.player2.isGroup(this.group2.getAddress()));

		assertFalse(this.player1.isGroup(gadr));
		assertFalse(this.player2.isGroup(gadr));
	}

	/**
	 */
	public void testGetPlayersClassGroupAddress() {
		Iterator<AgentAddress> iterator;

		iterator = this.player1.getPlayers(RoleStub.class, this.group1
				.getAddress());
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(this.address1, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = this.player2.getPlayers(RoleStub.class, this.group1
				.getAddress());
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(this.address1, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = this.player1.getPlayers(Role2Stub.class, this.group1
				.getAddress());
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		iterator = this.player2.getPlayers(Role2Stub.class, this.group1
				.getAddress());
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		iterator = this.player1.getPlayers(Role3Stub.class, this.group1
				.getAddress());
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		iterator = this.player2.getPlayers(Role3Stub.class, this.group1
				.getAddress());
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetPlayerClassGroupAddress() {
		AgentAddress selected;

		selected = this.player1.getPlayer(RoleStub.class, this.group1
				.getAddress());
		assertEquals(this.address1, selected);

		selected = this.player2.getPlayer(RoleStub.class, this.group1
				.getAddress());
		assertEquals(this.address1, selected);

		selected = this.player1.getPlayer(Role2Stub.class, this.group1
				.getAddress());
		assertNull(selected);

		selected = this.player2.getPlayer(Role2Stub.class, this.group1
				.getAddress());
		assertNull(selected);

		selected = this.player1.getPlayer(Role3Stub.class, this.group1
				.getAddress());
		assertNull(selected);

		selected = this.player2.getPlayer(Role3Stub.class, this.group1
				.getAddress());
		assertNull(selected);
	}

	/**
	 * @throws Exception
	 */
	public void testGetMessage() throws Exception {
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		assertNull(this.player1.getMessage(this.group1.getAddress(),
				RoleStub.class));

		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), m2);
		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), m1);

		Role r = this.player1.getRole(this.group1.getAddress(), RoleStub.class);
		Mailbox mb = r.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}
		
		assertSame(m1, this.player1.getMessage(this.group1.getAddress(),
				RoleStub.class));
		assertSame(m2, this.player1.getMessage(this.group1.getAddress(),
				RoleStub.class));
	}

	/**
	 * @throws Exception
	 */
	public void testPeekMessage() throws Exception {
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		assertNull(this.player1.peekMessage(this.group1.getAddress(),
				RoleStub.class));

		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), m2);
		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), m1);

		Role r = this.player1.getRole(this.group1.getAddress(), RoleStub.class);
		Mailbox mb = r.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		assertSame(m1, this.player1.peekMessage(this.group1.getAddress(),
				RoleStub.class));
		assertSame(m1, this.player1.peekMessage(this.group1.getAddress(),
				RoleStub.class));
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

		iterator = this.player1.getMessages(this.group1.getAddress(),
				RoleStub.class).iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), m2);
		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), m1);

		Role r = this.player1.getRole(this.group1.getAddress(), RoleStub.class);
		Mailbox mb = r.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		iterator = this.player1.getMessages(this.group1.getAddress(),
				RoleStub.class).iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());

		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		iterator = this.player1.getMessages(this.group1.getAddress(),
				RoleStub.class).iterator();
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

		iterator = this.player1.peekMessages(this.group1.getAddress(),
				RoleStub.class).iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), m2);
		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), m1);

		Role r = this.player1.getRole(this.group1.getAddress(), RoleStub.class);
		Mailbox mb = r.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		iterator = this.player1.peekMessages(this.group1.getAddress(),
				RoleStub.class).iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());

		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		iterator = this.player1.peekMessages(this.group1.getAddress(),
				RoleStub.class).iterator();
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
		assertFalse(this.player1.hasMessage(this.group1.getAddress(),
				RoleStub.class));

		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), new Message());

		Role r = this.player1.getRole(this.group1.getAddress(), RoleStub.class);
		Mailbox mb = r.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		assertTrue(this.player1.hasMessage(this.group1.getAddress(),
				RoleStub.class));
	}

	/**
	 * @throws Exception
	 */
	public void testGetMailboxSize() throws Exception {
		assertEquals(0, this.player1.getMailboxSize(this.group1.getAddress(),
				RoleStub.class));

		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), new Message());

		Role r = this.player1.getRole(this.group1.getAddress(), RoleStub.class);
		Mailbox mb = r.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		assertEquals(1, this.player1.getMailboxSize(this.group1.getAddress(),
				RoleStub.class));

		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, this.player1.getAddress(), new Message());

		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		assertEquals(2, this.player1.getMailboxSize(this.group1.getAddress(),
				RoleStub.class));
	}

	/**
	 * @throws Exception
	 */
	public void testGetMailbox() throws Exception {
		Mailbox mb;
		
		assertNotNull(mb = this.player1.getMailbox(this.group1.getAddress(),
				RoleStub.class));
		assertSame(mb, this.player1.getMailbox(this.group1.getAddress(),
				RoleStub.class));

		Role r = this.player1.getRole(this.group1.getAddress(), RoleStub.class);
		Mailbox rmb = r.getMailbox();
		assertSame(rmb, mb);
	}

	/**
	 * @throws Exception
	 */
	public void testReplyToGroupAddressClassMessageMessageMessage() throws Exception {
		//TODO: create the unit test
	}

	/**
	 * @throws Exception
	 */
	public void testSendMessageClassAgentAddressMessage() throws Exception {
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		this.player1.sendMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, player2.getAddress(), msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		this.player1.sendMessage( 
				RoleStub.class,
				new RoleAddress(this.group1.getAddress(), RoleStub.class, player2.getAddress()),
				msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
	public void testForwardMessageClassAgentAddressMessage() throws Exception {
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		this.player1.forwardMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, player2.getAddress(), msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		this.player1.forwardMessage(
				RoleStub.class,
				new RoleAddress(this.group1.getAddress(), RoleStub.class, player2.getAddress()),
				msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);
		
		player2.setName("player2-test"); //$NON-NLS-1$
		player3.setName("player3-test"); //$NON-NLS-1$
		player4.setName("player4-test"); //$NON-NLS-1$

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		RoleAddress r = this.player1.getRole(this.group1.getAddress(), RoleStub.class).getAddress();
		RoleAddress receiver = this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);
		
		InteractionUtilStub.updateContext(msg, 
				r,
				receiver,
				1024);

		assertEquals(receiver, this.player1.forwardMessage(
				this.group1.getAddress(), RoleStub.class, msg));

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress adr = this.player1.sendMessage(this.group1.getAddress(),
				RoleStub.class, RoleStub.class, msg);
		assertNotNull(adr);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(player2.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
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

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress adr = this.player1.forwardMessage(
				this.group1.getAddress(), RoleStub.class, RoleStub.class, msg);
		assertNotNull(adr);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(player2.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
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

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
	public void testSendMessageClassMessageReceiverSelectionPolicyMessage()
			throws Exception {
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress adr = this.player1.sendMessage(this.group1.getAddress(),
				RoleStub.class, RoleStub.class, new PolicyStub(player3
						.getAddress()), msg);

		assertNotNull(adr);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		RoleAddress adr = this.player1.forwardMessage(
				this.group1.getAddress(), RoleStub.class, RoleStub.class,
				new PolicyStub(player3.getAddress()), msg);

		assertNotNull(adr);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		this.player1.broadcastMessage(this.group1.getAddress(), RoleStub.class,
				RoleStub.class, msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$

		this.group1.requestRole(player2, RoleStub.class, null, null);
		this.group1.requestRole(player3, RoleStub.class, null, null);
		this.group1.requestRole(player4, Role3Stub.class, null, null);

		this.player1.forwardBroadcastMessage(this.group1.getAddress(),
				RoleStub.class, RoleStub.class, msg);

		Role role;
		Mailbox mb;

		assertNotNull(role = this.group1.getPlayedRole(this.player1
				.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player2.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player3.getAddress(),
				RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group1.getPlayedRole(player4.getAddress(),
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
	public void testExecuteCapacityCall() throws Exception {
		CapacityContext context = this.player1.executeCapacityCall(
				CapacityStub.class, RoleStub.class, this.group1.getAddress(),
				'a', 'b', 'c');
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
		UUID id = this.player1.submitCapacityCall(CapacityStub.class,
				RoleStub.class, this.group1.getAddress(), 'a', 'b', 'c');
		assertNotNull(id);
		CapacityContext context = this.player1.waitCapacityCallResult(id);
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
		UUID id = this.player1.submitCapacityCall(Capacity2Stub.class,
				RoleStub.class, this.group1.getAddress(), 'a', 'b', 'c');
		assertNotNull(id);

		assertTrue(this.player1.cancelCapacityCall(id));

		CapacityContext context = this.player1.getCapacityCallResult(id);
		assertNotNull(context);
		assertNotNull(context.getIdentifier());
		assertTrue(context.isFailed());
		assertFalse(context.isResultAvailable());
		assertEquals(0, context.getOutputValueCount());
	}
	
	/**
	 */
	public void testGroupListener() {
		GroupListenerStub listener = new GroupListenerStub();
		
		this.player1.addGroupListener(listener);
		
		GroupAddress adr = this.player1.createGroup(this.organization1.getClass());
		assertNotNull(adr);
		
		listener.assertCreation(adr);
		listener.assertNull();
	}

	/**
	 */
	public void testRolePlayingListener() {
		RolePlayingListenerStub listener = new RolePlayingListenerStub();
		
		this.player1.addRolePlayingListener(listener);
		
		GroupAddress adr = this.player1.createGroup(Organization1Stub.class);
		assertNotNull(adr);
		
		listener.assertNull();
		
		assertNotNull(this.player1.requestRole(RoleStub.class, adr));
		
		listener.assertTaken(RoleStub.class, adr, this.player1.getAddress());
		listener.assertNull();
		
		assertTrue(this.player1.leaveRole(RoleStub.class, adr));

		listener.assertReleased(RoleStub.class, adr, this.player1.getAddress());
		listener.assertNull();
	}

	/**
	 */
	public void testGetRoleAddresses() {
		SizedIterator<RoleAddress> iterator;
		RoleAddress ra;
		
		iterator = this.player1.getRoleAddresses();
		assertNotNull(iterator);
		assertEquals(1, iterator.totalSize());
		assertEquals(1, iterator.rest());
		assertTrue(iterator.hasNext());
		ra = iterator.next();
		assertNotNull(ra);
		assertEquals(this.group1.getAddress(), ra.getGroup());
		assertEquals(RoleStub.class, ra.getRole());
		assertEquals(this.player1.getAddress(), ra.getPlayer());
		assertEquals(1, iterator.totalSize());
		assertEquals(0, iterator.rest());
		assertFalse(iterator.hasNext());
	}

	/** 
	 */
	public void testGetRoleAddressesInGroupGroupAddress() {
		SizedIterator<RoleAddress> iterator;
		RoleAddress ra;
		
		iterator = this.player1.getRoleAddressesInGroup(this.group1.getAddress());
		assertNotNull(iterator);
		assertEquals(1, iterator.totalSize());
		assertEquals(1, iterator.rest());
		assertTrue(iterator.hasNext());
		ra = iterator.next();
		assertNotNull(ra);
		assertEquals(this.group1.getAddress(), ra.getGroup());
		assertEquals(RoleStub.class, ra.getRole());
		assertEquals(this.player1.getAddress(), ra.getPlayer());
		assertEquals(1, iterator.totalSize());
		assertEquals(0, iterator.rest());
		assertFalse(iterator.hasNext());
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
