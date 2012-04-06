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

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;
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
public class InteractionUtilTest extends TestCase {

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
		this.group = new KernelScopeGroup(
				this.organization,
				this.address,
				this.distributed,
				this.persistent,
				this.membership);
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
		this.group = null;
		this.player1 = null;
		this.player2 = null;
		this.organization = null;
		this.address = null;
		this.membership = null;
		this.context = null;
		super.tearDown();
	}
	
	private RoleAddress makeRoleAddress(GroupAddress group, Class<? extends Role> role, AgentAddress player) {
		RoleAddress ra = new RoleAddress(group, role, player);
		if (player!=null) {
			ra.bind(this.group.getPlayedRole(player, role));
		}
		return ra;
	}

	/**
	 */
	public void testSendMessageRoleAddressRoleAddressMessageBooleanBoolean_truefalse() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		assertNotNull(this.group.requestRole(player3, RoleStub.class, null, null));
		assertNotNull(this.group.requestRole(player4, Role3Stub.class, null, null));
		
		InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player2.getAddress()),
				msg,
				true,
				false);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(), Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());
	}

	/**
	 */
	public void testSendMessageRoleAddressRoleAddressMessageBooleanBoolean_truetrue() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player2.getAddress()),
				msg,
				true,
				true);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player4.getAddress(), Role3Stub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());
	}

	/**
	 */
	public void testSendMessageRoleAddressRoleAddressMessageBooleanBoolean_falsefalse() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player2.getAddress()),
				msg,
				false,
				false);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
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
	public void testSendMessageRoleAddressRoleAddressMessageBooleanBoolean_falsetrue() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player2.getAddress()),
				msg,
				false,
				true);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
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
	public void testSendMessageRoleAddressClassMessageBooleanBoolean_truefalse() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress adr = InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, null),
				msg,
				true,
				false);
		assertNotNull(adr);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(this.player2.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(player3.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
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
	public void testSendMessageRoleAddressClassMessageBooleanBoolean_truetrue() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress adr = InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, null),
				msg,
				true,
				true);
		assertNotNull(adr);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(this.player2.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(player3.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
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
	public void testSendMessageRoleAddressClassMessageBooleanBoolean_falsefalse() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress adr = InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, null),
				msg,
				false,
				false);
		assertNotNull(adr);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(this.player2.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(player3.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
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
	public void testSendMessageRoleAddressClassMessageBooleanBoolean_falsetrue() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress adr = InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, null),
				msg,
				false,
				true);
		assertNotNull(adr);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(this.player2.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(player3.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		if (adr.getPlayer().equals(player3.getAddress())) {
			assertFalse(mb.isEmpty());
			assertSame(msg, mb.removeFirst());
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
	public void testSendMessageRoleAddressClassMessageReceiverSelectionPolicyMessageBooleanBoolean_truefalse() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress adr = InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, null),
				new PolicyStub(player3.getAddress()),
				msg,
				true,
				false);
		
		assertNotNull(adr);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
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
	public void testSendMessageRoleAddressClassMessageReceiverSelectionPolicyMessageBooleanBoolean_truetrue() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress adr = InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, null),
				new PolicyStub(player3.getAddress()),
				msg,
				true,
				true);
		
		assertNotNull(adr);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
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
	public void testSendMessageRoleAddressClassMessageReceiverSelectionPolicyMessageBooleaBoolean_falsefalse() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress adr = InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, null),
				new PolicyStub(player3.getAddress()),
				msg,
				false,
				false);
		
		assertNotNull(adr);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
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
	public void testSendMessageRoleAddressClassMessageReceiverSelectionPolicyMessageBooleaBoolean_falsetrue() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		RoleAddress adr = InteractionUtil.sendMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				makeRoleAddress(this.group.getAddress(), RoleStub.class, null),
				new PolicyStub(player3.getAddress()),
				msg,
				false,
				true);
		
		assertNotNull(adr);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
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
	public void testBroadcastRoleAddressClassMessageBooleanBoolean_truefalse() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		InteractionUtil.broadcastMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				RoleStub.class,
				msg,
				true,
				false);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
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
	public void testBroadcastRoleAddressClassMessageBooleanBoolean_truetrue() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		InteractionUtil.broadcastMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				RoleStub.class,
				msg,
				true,
				true);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
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
	public void testBroadcastRoleAddressClassMessageBooleanBoolean_falsefalse() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		InteractionUtil.broadcastMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				RoleStub.class,
				msg,
				false,
				false);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
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
	public void testBroadcastRoleAddressClassMessageBooleanBoolean_falsetrue() {
		RolePlayer player3 = new RolePlayerStub(this.context);
		RolePlayer player4 = new RolePlayerStub(this.context);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		this.group.requestRole(player3, RoleStub.class, null, null);
		this.group.requestRole(player4, Role3Stub.class, null, null);
		
		InteractionUtil.broadcastMessage(
				1024,
				makeRoleAddress(this.group.getAddress(), RoleStub.class, this.player1.getAddress()),
				RoleStub.class,
				msg,
				false,
				true);
		
		Role role;
		Mailbox mb;
		
		assertNotNull(role = this.group.getPlayedRole(this.player1.getAddress(), RoleStub.class));
		assertNotNull(mb = role.getMailbox());
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox) mb).synchronizeMessages();
		}
		assertFalse(mb.isEmpty());
		assertSame(msg, mb.removeFirst());
		assertTrue(mb.isEmpty());

		assertNotNull(role = this.group.getPlayedRole(this.player2.getAddress(), RoleStub.class));
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
		public AgentAddress selectEntity(
				AgentAddress sender,
				List<? extends AgentAddress> availableEntities) {
			return this.adr;
		}
		
		@Override
		public AgentAddress selectEntity(
				AgentAddress sender,
				DirectAccessCollection<? extends AgentAddress> availableEntities) {
			return this.adr;
		}

		@Override
		public AgentAddress selectEntity(
				AgentAddress sender,
				SizedIterator<? extends AgentAddress> availableEntities) {
			return this.adr;
		}

	}
	
}