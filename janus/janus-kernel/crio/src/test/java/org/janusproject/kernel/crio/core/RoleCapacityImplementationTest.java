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

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.Role.MessageTransportService;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.random.RandomNumber;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoleCapacityImplementationTest extends TestCase {

	private CRIOContext context;
	private Organization organization;
	private GroupAddress address;
	private KernelScopeGroup group;
	private MembershipService membership;
	private boolean distributed, persistent;
	private RolePlayer player1;
	private RolePlayer player2;

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
		this.distributed = RandomNumber.nextBoolean();
		this.persistent = RandomNumber.nextBoolean();
		this.group = new KernelScopeGroup(this.organization, this.address,
				this.distributed, this.persistent, this.membership);
		this.player1 = new RolePlayerStub(this.context);
		this.player2 = new RolePlayerStub(this.context);
		this.group.requestRole(this.player1, RoleStub.class, null, null);
		this.group.requestRole(this.player2, RoleStub.class, null, null);
	}

	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.player1 = null;
		this.player2 = null;
		this.group = null;
		this.organization = null;
		this.address = null;
		this.membership = null;
		this.context = null;
		super.tearDown();
	}

	/**
	 */
	public void testMailbox() {
		Mailbox m1, m2;
		CapacityContext context;

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		assertNotNull(m1 = RoleCapacityImplementation.getMailbox(context));
		assertSame(m1, RoleCapacityImplementation.getMailbox(context));

		context = new GroupCapacityContext(this.player2, this.group, this.group
				.getPlayedRole(this.player2.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		assertNotNull(m2 = RoleCapacityImplementation.getMailbox(context));
		assertSame(m2, RoleCapacityImplementation.getMailbox(context));

		assertNotSame(m1, m2);
	}

	/**
	 */
	public void testgetMessageTransportService() {
		MessageTransportService m1, m2;
		CapacityContext context;

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);

		assertNotNull(m1 = RoleCapacityImplementation
				.getMessageTransportService(context));
		assertSame(m1, RoleCapacityImplementation.getMessageTransportService(context));

		context = new GroupCapacityContext(this.player2, this.group, this.group
				.getPlayedRole(this.player2.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);

		assertNotNull(m2 = RoleCapacityImplementation
				.getMessageTransportService(context));
		assertSame(m2, RoleCapacityImplementation.getMessageTransportService(context));

		assertNotSame(m1, m2);
	}

	/**
	 * @throws Exception
	 */
	public void testSendMessageCapacityContextClassAgentAddressMessage()
			throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player2
				.getAddress(), msg);

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
	public void testForwardMessageCapacityContextMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Role r = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class);
		
		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(msg, 
				r.getAddress(),
				new RoleAddress(this.group.getAddress(), RoleStub.class, this.player2.getAddress()),
				1024);

		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, r,
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleAddress rAdr = RoleCapacityImplementation.forwardMessage(context, msg);
		assertEquals(this.group.getAddress(), rAdr.getGroup());
		assertEquals(RoleStub.class, rAdr.getRole());
		assertEquals(this.player2.getAddress(), rAdr.getPlayer());

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
	public void testForwardMessageCapacityContextClassAgentAddressMessage()
			throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleCapacityImplementation.forwardMessage(context, RoleStub.class,
				this.player2.getAddress(), msg);

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
	public void testSendMessageCapacityContextClassMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleAddress adr = RoleCapacityImplementation.sendMessage(context,
				RoleStub.class, msg);
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
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());
	}

	/**
	 * @throws Exception
	 */
	public void testForwardMessageCapacityContextClassMessage()
			throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleAddress adr = RoleCapacityImplementation.forwardMessage(context,
				RoleStub.class, msg);
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
	public void testSendMessageCapacityContextClassMessageReceiverSelectionPolicyMessage()
			throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleAddress adr = RoleCapacityImplementation.sendMessage(context,
				RoleStub.class, new PolicyStub(player3.getAddress()), msg);

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
	public void testForwardMessageCapacityContextClassMessageReceiverSelectionPolicyMessage()
			throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleAddress adr = RoleCapacityImplementation.forwardMessage(context,
				RoleStub.class, new PolicyStub(player3.getAddress()), msg);

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
		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleCapacityImplementation.broadcastMessage(context, RoleStub.class, msg);

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
		CapacityContext context;

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		context = new GroupCapacityContext(this.player1, this.group, this.group
				.getPlayedRole(this.player1.getAddress(), RoleStub.class),
				CapacityStub.class, CapacityImplementationType.DIRECT_ACTOMIC);
		RoleCapacityImplementation.forwardBroadcastMessage(context, RoleStub.class,
				msg);

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
	public void testGetMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		CapacityContext context = new GroupCapacityContext(this.player1,
				this.group, this.group.getPlayedRole(this.player1.getAddress(),
						RoleStub.class), CapacityStub.class,
				CapacityImplementationType.DIRECT_ACTOMIC);

		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		assertNull(RoleCapacityImplementation.getMessage(context));

		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), m2);
		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), m1);

		Mailbox mb = RoleCapacityImplementation.getMailbox(context);
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		assertSame(m1, RoleCapacityImplementation.getMessage(context));
		assertSame(m2, RoleCapacityImplementation.getMessage(context));
	}

	/**
	 * @throws Exception
	 */
	public void testPeekMessage() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		CapacityContext context = new GroupCapacityContext(this.player1,
				this.group, this.group.getPlayedRole(this.player1.getAddress(),
						RoleStub.class), CapacityStub.class,
				CapacityImplementationType.DIRECT_ACTOMIC);

		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		assertNull(RoleCapacityImplementation.peekMessage(context));

		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), m2);
		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), m1);

		Mailbox mb = RoleCapacityImplementation.getMailbox(context);
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		assertSame(m1, RoleCapacityImplementation.peekMessage(context));
		assertSame(m1, RoleCapacityImplementation.peekMessage(context));
	}

	/**
	 * @throws Exception
	 */
	public void testGetMessages() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		CapacityContext context = new GroupCapacityContext(this.player1,
				this.group, this.group.getPlayedRole(this.player1.getAddress(),
						RoleStub.class), CapacityStub.class,
				CapacityImplementationType.DIRECT_ACTOMIC);

		Iterator<Message> iterator;
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		iterator = RoleCapacityImplementation.getMessages(context);
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), m2);
		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), m1);

		Mailbox mb = RoleCapacityImplementation.getMailbox(context);
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		iterator = RoleCapacityImplementation.getMessages(context);
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = RoleCapacityImplementation.getMessages(context);
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}

	/**
	 * @throws Exception
	 */
	public void testPeekMessages() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		CapacityContext context = new GroupCapacityContext(this.player1,
				this.group, this.group.getPlayedRole(this.player1.getAddress(),
						RoleStub.class), CapacityStub.class,
				CapacityImplementationType.DIRECT_ACTOMIC);

		Iterator<Message> iterator;
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		iterator = RoleCapacityImplementation.peekMessages(context);
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());

		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), m2);
		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), m1);

		Mailbox mb = RoleCapacityImplementation.getMailbox(context);
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}

		iterator = RoleCapacityImplementation.peekMessages(context);
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = RoleCapacityImplementation.peekMessages(context);
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
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		CapacityContext context = new GroupCapacityContext(this.player1,
				this.group, this.group.getPlayedRole(this.player1.getAddress(),
						RoleStub.class), CapacityStub.class,
				CapacityImplementationType.DIRECT_ACTOMIC);
		{
			Mailbox mb = RoleCapacityImplementation.getMailbox(context);
			if (mb instanceof BufferedMailbox) {
				((BufferedMailbox) mb).synchronizeMessages();
			}
		}
		assertFalse(RoleCapacityImplementation.hasMessage(context));

		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), new Message());
		{
			Mailbox mb = RoleCapacityImplementation.getMailbox(context);
			if (mb instanceof BufferedMailbox) {
				((BufferedMailbox) mb).synchronizeMessages();
			}
		}
		assertTrue(RoleCapacityImplementation.hasMessage(context));
	}

	/**
	 * @throws Exception
	 */
	public void testGetMailboxSize() throws Exception {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);

		CapacityContext context = new GroupCapacityContext(this.player1,
				this.group, this.group.getPlayedRole(this.player1.getAddress(),
						RoleStub.class), CapacityStub.class,
				CapacityImplementationType.DIRECT_ACTOMIC);

		assertEquals(0, RoleCapacityImplementation.getMailboxSize(context));

		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), new Message());
		{
			Mailbox mb = RoleCapacityImplementation.getMailbox(context);
			if (mb instanceof BufferedMailbox) {
				((BufferedMailbox) mb).synchronizeMessages();
			}
		}
		assertEquals(1, RoleCapacityImplementation.getMailboxSize(context));

		RoleCapacityImplementation.sendMessage(context, RoleStub.class, this.player1
				.getAddress(), new Message());

		{
			Mailbox mb = RoleCapacityImplementation.getMailbox(context);
			if (mb instanceof BufferedMailbox) {
				((BufferedMailbox) mb).synchronizeMessages();
			}
		}
		assertEquals(2, RoleCapacityImplementation.getMailboxSize(context));
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

}