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
import java.util.UUID;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelScopeGroupTest extends TestCase {

	private CRIOContext context;
	private Organization organization;
	private GroupAddress address;
	private KernelScopeGroup group;
	private KernelScopeGroup persistentGroup;
	private MembershipService membership;
	private boolean distributed, persistent;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.context = new CRIOContext(null);
		this.organization = new Organization1Stub(this.context);
		this.address = new GroupAddress(UUID.randomUUID(), this.organization.getClass());
		this.membership = new MembershipServiceStub();
		this.distributed = false;
		this.persistent = false;
		this.group = new KernelScopeGroup(
				this.organization,
				this.address,
				this.distributed,
				this.persistent,
				this.membership);
		this.persistentGroup = new KernelScopeGroup(
				this.organization,
				new GroupAddress(UUID.randomUUID(), this.organization.getClass()),
				false,
				true,
				this.membership);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.group = null;
		this.persistentGroup = null;
		this.organization = null;
		this.address = null;
		this.membership = null;
		this.context = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetAddress() {
		assertSame(this.address, this.group.getAddress());
	}

	/**
	 */
	public void testGetOrganization() {
		assertSame(this.organization, this.group.getOrganization());
	}


	/**
	 */
	public void testIsPersistent() {
		assertEquals(this.persistent, this.group.isPersistent());
	}

	/**
	 */
	public void testVerifiesObtainConditionsRolePlayerRole() {
		assertNull(this.group.verifiesObtainConditions(new RolePlayerStub(this.context), new RoleStub()));
	}

	/**
	 */
	public void testVerifiesLeaveConditionsRolePlayerRole() {
		assertNull(this.group.verifiesObtainConditions(new RolePlayerStub(this.context), new RoleStub()));
	}
	
	/**
	 */
	public void testRequestRoleRolePlayerClass_onPublicClasses() {
		RolePlayer player1 = new RolePlayerStub(this.context);
		RolePlayer player2 = new RolePlayerStub(this.context);
		
		assertNotNull(this.group.requestRole(player1, RoleStub.class, null, null));
		assertNotNull(this.group.requestRole(player2, RoleStub.class, null, null));

		assertNull(this.group.requestRole(player1, Role2Stub.class, null, null));
		assertNull(this.group.requestRole(player2, Role2Stub.class, null, null));

		assertNull(this.group.requestRole(player1, RoleStub.class, null, null));
		assertNull(this.group.requestRole(player2, RoleStub.class, null, null));
	}

	/**
	 */
	public void testRequestRoleRolePlayerClass_onPrivateClasses() {
		RolePlayer player = new RolePlayerStub(this.context);
		
		Organization orga = OrganizationRepository.organization(
				this.context, 
				InnerClassOrganizationStub.class,
				null);
		
		KernelScopeGroup group = new KernelScopeGroup(
				orga,
				new GroupAddress(UUID.randomUUID(), orga.getClass()),
				false,
				false,
				null);
		
		
		assertNotNull(group.requestRole(player, InnerClassRoleStub.class, null, null));
	}

	/**
	 */
	public void testGetRolePlayersClass() {
		SizedIterator<AgentAddress> players;

		players = this.group.getRolePlayers(RoleStub.class);
		assertNotNull(players);
		assertFalse(players.hasNext());
		
		RolePlayer player = new RolePlayerStub(this.context);
		
		this.group.requestRole(player, RoleStub.class, null, null);
		
		players = this.group.getRolePlayers(RoleStub.class);
		assertNotNull(players);
		assertTrue(players.hasNext());
		assertEquals(player.getAddress(), players.next());
		assertFalse(players.hasNext());
	}

	/**
	 */
	public void testGetRolePlayerCollectionClass() {
		DirectAccessCollection<AgentAddress> players;

		players = this.group.getRolePlayerCollection(RoleStub.class);
		assertNotNull(players);
		assertTrue(players.isEmpty());
		
		RolePlayer player = new RolePlayerStub(this.context);
		
		this.group.requestRole(player, RoleStub.class, null, null);
		
		players = this.group.getRolePlayerCollection(RoleStub.class);
		assertNotNull(players);
		assertEquals(1, players.size());
		assertEquals(player.getAddress(), players.get(0));
	}

	/**
	 */
	public void testGetRoleAddressClassAgentAddress() {
		RoleAddress roleAddress;

		roleAddress = this.group.getRoleAddress(RoleStub.class, null);
		assertNull(roleAddress);
		
		roleAddress = this.group.getRoleAddress(RoleStub.class,
				new AgentAddress(UUID.randomUUID(),null) {
			private static final long serialVersionUID = 2052897212713875693L;
		} );
		assertNull(roleAddress);

		RolePlayer player = new RolePlayerStub(this.context);
		
		RoleAddress r = this.group.requestRole(player, RoleStub.class, null, null);
		assertNotNull(r);
		
		roleAddress = this.group.getRoleAddress(RoleStub.class, player.getAddress());
		assertNotNull(roleAddress);
		assertSame(r, roleAddress);
	}

	/**
	 */
	public void testGetRoleAddressesClass() {
		SizedIterator<RoleAddress> roleAddresses;
		RoleAddress roleAddress;

		roleAddresses = this.group.getRoleAddresses(RoleStub.class);
		assertNotNull(roleAddresses);
		assertEquals(0, roleAddresses.totalSize());
		assertEquals(0, roleAddresses.rest());
		assertFalse(roleAddresses.hasNext());
		
		RolePlayer player1 = new RolePlayerStub(this.context);
		RoleAddress r1 = this.group.requestRole(player1, RoleStub.class, null, null);
		assertNotNull(r1);
		
		RolePlayer player2 = new RolePlayerStub(this.context);
		RoleAddress r2 = this.group.requestRole(player2, Role3Stub.class, null, null);
		assertNotNull(r2);

		roleAddresses = this.group.getRoleAddresses(RoleStub.class);
		assertNotNull(roleAddresses);
		assertEquals(1, roleAddresses.totalSize());
		assertEquals(1, roleAddresses.rest());
		assertTrue(roleAddresses.hasNext());
		roleAddress = roleAddresses.next();
		assertNotNull(roleAddress);
		assertEquals(r1, roleAddress);
		assertEquals(this.group.getAddress(), roleAddress.getGroup());
		assertEquals(player1.getAddress(), roleAddress.getPlayer());
		assertEquals(RoleStub.class, roleAddress.getRole());
		assertEquals(1, roleAddresses.totalSize());
		assertEquals(0, roleAddresses.rest());
		assertFalse(roleAddresses.hasNext());
	}

	/**
	 */
	public void testGetRoleAddresses() {
		SizedIterator<RoleAddress> roleAddresses;
		RoleAddress roleAddress;

		roleAddresses = this.group.getRoleAddresses();
		assertNotNull(roleAddresses);
		assertEquals(0, roleAddresses.totalSize());
		assertEquals(0, roleAddresses.rest());
		assertFalse(roleAddresses.hasNext());
		
		RolePlayer player1 = new RolePlayerStub(this.context);
		RoleAddress r1 = this.group.requestRole(player1, RoleStub.class, null, null);
		assertNotNull(r1);
		
		RolePlayer player2 = new RolePlayerStub(this.context);
		RoleAddress r2 = this.group.requestRole(player2, Role3Stub.class, null, null);
		assertNotNull(r2);

		roleAddresses = this.group.getRoleAddresses();
		assertNotNull(roleAddresses);
		assertEquals(2, roleAddresses.totalSize());
		assertEquals(2, roleAddresses.rest());
		assertTrue(roleAddresses.hasNext());
		roleAddress = roleAddresses.next();
		if (r1.equals(roleAddress)) {
			assertNotNull(roleAddress);
			assertEquals(r1, roleAddress);
			assertEquals(this.group.getAddress(), roleAddress.getGroup());
			assertEquals(player1.getAddress(), roleAddress.getPlayer());
			assertEquals(RoleStub.class, roleAddress.getRole());
			assertEquals(2, roleAddresses.totalSize());
			assertEquals(1, roleAddresses.rest());
			assertTrue(roleAddresses.hasNext());
			roleAddress = roleAddresses.next();
			assertNotNull(roleAddress);
			assertEquals(r2, roleAddress);
			assertEquals(this.group.getAddress(), roleAddress.getGroup());
			assertEquals(player2.getAddress(), roleAddress.getPlayer());
			assertEquals(Role3Stub.class, roleAddress.getRole());
		}
		else {
			assertNotNull(roleAddress);
			assertEquals(r2, roleAddress);
			assertEquals(this.group.getAddress(), roleAddress.getGroup());
			assertEquals(player2.getAddress(), roleAddress.getPlayer());
			assertEquals(Role3Stub.class, roleAddress.getRole());
			assertEquals(2, roleAddresses.totalSize());
			assertEquals(1, roleAddresses.rest());
			assertTrue(roleAddresses.hasNext());
			roleAddress = roleAddresses.next();
			assertNotNull(roleAddress);
			assertEquals(r1, roleAddress);
			assertEquals(this.group.getAddress(), roleAddress.getGroup());
			assertEquals(player1.getAddress(), roleAddress.getPlayer());
			assertEquals(RoleStub.class, roleAddress.getRole());
		}
		assertEquals(2, roleAddresses.totalSize());
		assertEquals(0, roleAddresses.rest());
		assertFalse(roleAddresses.hasNext());
	}

	/**
	 */
	public void testGetRoleAddressesAgentAddress() {
		SizedIterator<RoleAddress> roleAddresses;
		RoleAddress roleAddress;

		RolePlayer player1 = new RolePlayerStub(this.context);
		RolePlayer player2 = new RolePlayerStub(this.context);

		roleAddresses = this.group.getRoleAddresses(player1.getAddress());
		assertNotNull(roleAddresses);
		assertEquals(0, roleAddresses.totalSize());
		assertEquals(0, roleAddresses.rest());
		assertFalse(roleAddresses.hasNext());
		
		RoleAddress r1 = this.group.requestRole(player1, RoleStub.class, null, null);
		assertNotNull(r1);
		
		RoleAddress r2 = this.group.requestRole(player2, Role3Stub.class, null, null);
		assertNotNull(r2);

		roleAddresses = this.group.getRoleAddresses(player1.getAddress());
		assertNotNull(roleAddresses);
		assertEquals(1, roleAddresses.totalSize());
		assertEquals(1, roleAddresses.rest());
		assertTrue(roleAddresses.hasNext());
		roleAddress = roleAddresses.next();
		assertNotNull(roleAddress);
		assertEquals(r1, roleAddress);
		assertEquals(this.group.getAddress(), roleAddress.getGroup());
		assertEquals(player1.getAddress(), roleAddress.getPlayer());
		assertEquals(RoleStub.class, roleAddress.getRole());
		assertEquals(1, roleAddresses.totalSize());
		assertEquals(0, roleAddresses.rest());
		assertFalse(roleAddresses.hasNext());
	}

	/**
	 */
	public void testIsPlayedRoleClass() {
		assertFalse(this.group.isPlayedRole(RoleStub.class));
		assertFalse(this.group.isPlayedRole(Role2Stub.class));
		assertFalse(this.group.isPlayedRole(Role3Stub.class));
	}

	/**
	 */
	public void testIsPlayedRoleAgentAddress() {
		RolePlayerStub player = new RolePlayerStub(this.context);
		RolePlayerStub player2 = new RolePlayerStub(this.context);
		this.group.requestRole(player, RoleStub.class, null, null);
		assertTrue(this.group.isPlayedRole(player.getAddress()));
		assertFalse(this.group.isPlayedRole(player2.getAddress()));
		assertFalse(this.group.isPlayedRole(new AgentAddressStub()));
	}

	/**
	 */
	public void testIsPlayedRoleAgentAddressClass() {
		RolePlayerStub player = new RolePlayerStub(this.context);
		RolePlayerStub player2 = new RolePlayerStub(this.context);
		AgentAddress adr = new AgentAddressStub();
		this.group.requestRole(player, RoleStub.class, null, null);
		assertTrue(this.group.isPlayedRole(player.getAddress(), RoleStub.class));
		assertFalse(this.group.isPlayedRole(player.getAddress(), Role2Stub.class));
		assertFalse(this.group.isPlayedRole(player2.getAddress(), RoleStub.class));
		assertFalse(this.group.isPlayedRole(player2.getAddress(), Role2Stub.class));
		assertFalse(this.group.isPlayedRole(adr, RoleStub.class));
		assertFalse(this.group.isPlayedRole(adr, Role2Stub.class));
	}

	/**
	 */
	public void testGetPlayedRoles() {
		SizedIterator<Class<? extends Role>> roles;
		roles = this.group.getPlayedRoles();
		assertNotNull(roles);
		assertFalse(roles.hasNext());

		this.group.requestRole(new RolePlayerStub(this.context), RoleStub.class, null, null);
		
		roles = this.group.getPlayedRoles();
		assertNotNull(roles);
		assertTrue(roles.hasNext());
		assertEquals(RoleStub.class, roles.next());
		assertFalse(roles.hasNext());
	}

	/**
	 */
	public void testGetPlayedRolesAgentAddress() {
		Collection<Class<? extends Role>> roles;
		Iterator<Class<? extends Role>> iterator;

		RolePlayer player1 = new RolePlayerStub(this.context);
		RolePlayer player2 = new RolePlayerStub(this.context);
		
		this.group.requestRole(player1, RoleStub.class, null, null);
		
		roles = this.group.getPlayedRoles(player1.getAddress());
		assertNotNull(roles);
		iterator = roles.iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(RoleStub.class, iterator.next());
		assertFalse(iterator.hasNext());

		roles = this.group.getPlayedRoles(player2.getAddress());
		assertNotNull(roles);
		iterator = roles.iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetPlayedRoleAgentAddressClass() {
		RolePlayer player1 = new RolePlayerStub(this.context);
		RolePlayer player2 = new RolePlayerStub(this.context);
		
		this.group.requestRole(player1, RoleStub.class, null, null);
		
		assertNotNull(this.group.getPlayedRole(player1.getAddress(), RoleStub.class));
		assertNull(this.group.getPlayedRole(player2.getAddress(), RoleStub.class));
		assertNull(this.group.getPlayedRole(player1.getAddress(), Role2Stub.class));
		assertNull(this.group.getPlayedRole(player2.getAddress(), Role2Stub.class));
	}

	/**
	 */
	public void testLeaveRoleRolePlayerClass() {
		RolePlayer player = new RolePlayerStub(this.context);

		assertNull(this.group.getPlayedRole(player.getAddress(), RoleStub.class));
		
		assertNotNull(this.group.requestRole(player, RoleStub.class, null, null));		
		assertNotNull(this.group.getPlayedRole(player.getAddress(), RoleStub.class));
		
		assertFalse(this.group.leaveRole(player, Role2Stub.class));
		assertNotNull(this.group.getPlayedRole(player.getAddress(), RoleStub.class));

		assertTrue(this.group.leaveRole(player, RoleStub.class));
		assertNull(this.group.getPlayedRole(player.getAddress(), RoleStub.class));
	}	

	/**
	 */
	public void testLeaveAllRolesRolePlayer() {
		RolePlayer player = new RolePlayerStub(this.context);

		assertNull(this.group.getPlayedRole(player.getAddress(), RoleStub.class));
		
		assertNotNull(this.group.requestRole(player, RoleStub.class, null, null));		
		assertNotNull(this.group.requestRole(player, Role3Stub.class, null, null));		

		assertNotNull(this.group.getPlayedRole(player.getAddress(), RoleStub.class));
		assertNull(this.group.getPlayedRole(player.getAddress(), Role2Stub.class));
		assertNotNull(this.group.getPlayedRole(player.getAddress(), Role3Stub.class));

		assertTrue(this.group.leaveAllRoles(player));
		
		assertNull(this.group.getPlayedRole(player.getAddress(), RoleStub.class));
		assertNull(this.group.getPlayedRole(player.getAddress(), Role2Stub.class));
		assertNull(this.group.getPlayedRole(player.getAddress(), Role3Stub.class));
	}
	
	/**
	 */
	public void testSendMessageMessageBoolean_discartSender() {
		RolePlayer player1 = new RolePlayerStub(this.context);
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(
				msg,
				new RoleAddress(this.group.getAddress(), RoleStub.class, player1.getAddress()),
				new RoleAddress(this.group.getAddress(), RoleStub.class, player2.getAddress()),
				1024);
				
		this.group.requestRole(player1, RoleStub.class, null, null);
		this.group.requestRole(player2, RoleStub.class, null, null);
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress rAdr = this.group.sendMessage(msg, false);
		assertNotNull(rAdr);
		assertEquals(this.group.getAddress(), rAdr.getGroup());
		assertEquals(RoleStub.class, rAdr.getRole());
		assertEquals(player2.getAddress(), rAdr.getPlayer());
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(), Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 */
	public void testSendMessageMessageBoolean_allowSender() {
		RolePlayer player1 = new RolePlayerStub(this.context);
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(
				msg,
				new RoleAddress(this.group.getAddress(), RoleStub.class, player1.getAddress()),
				new RoleAddress(this.group.getAddress(), RoleStub.class, player2.getAddress()),
				1024);
				
		this.group.requestRole(player1, RoleStub.class, null, null);
		this.group.requestRole(player2, RoleStub.class, null, null);
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress rAdr = this.group.sendMessage(msg, true);
		assertNotNull(rAdr);
		assertEquals(this.group.getAddress(), rAdr.getGroup());
		assertEquals(RoleStub.class, rAdr.getRole());
		assertEquals(player2.getAddress(), rAdr.getPlayer());
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(), Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 */
	public void testBroadcastMessageMessageBoolean_discartSender() {
		RolePlayer player1 = new RolePlayerStub(this.context);
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(
				msg,
				new RoleAddress(this.group.getAddress(), RoleStub.class, player1.getAddress()),
				new RoleAddress(this.group.getAddress(), RoleStub.class, null),
				1024);
				
		this.group.requestRole(player1, RoleStub.class, null, null);
		this.group.requestRole(player2, RoleStub.class, null, null);
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		this.group.broadcastMessage(msg, false);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());
		
		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(), Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 */
	public void testBroadcastMessageMessageBoolean_allowSender() {
		RolePlayer player1 = new RolePlayerStub(this.context);
		RolePlayer player2 = new RolePlayerStub(this.context);
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(
				msg,
				new RoleAddress(this.group.getAddress(), RoleStub.class, player1.getAddress()),
				new RoleAddress(this.group.getAddress(), RoleStub.class, null),
				1024);
				
		this.group.requestRole(player1, RoleStub.class, null, null);
		this.group.requestRole(player2, RoleStub.class, null, null);
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		this.group.broadcastMessage(msg, true);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());
		
		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(), Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testIsTooOldGroup_notPersistent_noPlayer() throws Exception {
		assertFalse(this.group.isPersistent());
		
		assertFalse(this.group.isTooOldGroup(0, 10));
		assertFalse(this.group.isTooOldGroup(2, 10));
		assertFalse(this.group.isTooOldGroup(4, 10));
		assertFalse(this.group.isTooOldGroup(6, 10));
		assertFalse(this.group.isTooOldGroup(8, 10));
		assertFalse(this.group.isTooOldGroup(10, 10));
		assertFalse(this.group.isTooOldGroup(11, 10));
		assertFalse(this.group.isTooOldGroup(12, 10));
	}

	/**
	 * @throws Exception
	 */
	public void testIsTooOldGroup_notPersistent_onePlayer() throws Exception {
		assertFalse(this.group.isPersistent());
		
		RolePlayer player = new RolePlayerStub(this.context);		
		assertNotNull(this.group.requestRole(player, RoleStub.class, null, null));
		
		assertFalse(this.group.isTooOldGroup(0, 10));
		assertFalse(this.group.isTooOldGroup(2, 10));
		assertFalse(this.group.isTooOldGroup(4, 10));
		assertFalse(this.group.isTooOldGroup(6, 10));
		assertFalse(this.group.isTooOldGroup(8, 10));
		assertFalse(this.group.isTooOldGroup(10, 10));
		assertFalse(this.group.isTooOldGroup(11, 10));
		assertFalse(this.group.isTooOldGroup(12, 10));
	}

	/**
	 * @throws Exception
	 */
	public void testIsTooOldGroup_persistent_noPlayer() throws Exception {
		assertTrue(this.persistentGroup.isPersistent());
		
		assertFalse(this.persistentGroup.isTooOldGroup(0, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(2, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(4, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(6, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(8, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(10, 10));
		assertTrue(this.persistentGroup.isTooOldGroup(11, 10));
		assertTrue(this.persistentGroup.isTooOldGroup(12, 10));
	}

	/**
	 * @throws Exception
	 */
	public void testIsTooOldGroup_persistent_onePlayer() throws Exception {
		assertTrue(this.persistentGroup.isPersistent());
		
		RolePlayer player = new RolePlayerStub(this.context);		
		assertNotNull(this.persistentGroup.requestRole(player, RoleStub.class, null, null));
		
		assertFalse(this.persistentGroup.isTooOldGroup(0, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(2, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(4, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(6, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(8, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(10, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(11, 10));
		assertFalse(this.persistentGroup.isTooOldGroup(12, 10));
	}

	/**
	 * @throws Exception
	 */
	public void testGetMembership() throws Exception {
		assertSame(this.membership, this.group.getMembership());
		assertSame(this.membership, this.persistentGroup.getMembership());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@RoleActivationPrototype()
	private static class InnerClassRoleStub extends Role {

		/**
		 */
		@SuppressWarnings("unused")
		public InnerClassRoleStub() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status live() {
			return StatusFactory.ok(this);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class InnerClassOrganizationStub extends Organization {

		/**
		 * @param context
		 */
		public InnerClassOrganizationStub(CRIOContext context) {
			super(context);
			addRole(InnerClassRoleStub.class);
		}
		
	}
	
}
