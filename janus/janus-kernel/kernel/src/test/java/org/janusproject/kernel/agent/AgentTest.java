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
package org.janusproject.kernel.agent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentLifeState;
import org.janusproject.kernel.agent.KernelAgent;
import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.agentsignal.SignalListener;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.role.RoleFactory;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.LinkedListMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusSeverity;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentTest extends TestCase {

	private KernelAgent kernel;
	private Agent agent;
	private String name;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		Kernels.shutdownNow();
		this.name = "MyNaMe"; //$NON-NLS-1$
		this.kernel = new KernelAgent(new AgentActivator(), true, null, null);
		this.agent = new Agent();
		this.agent.getAddress().setName(this.name);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.kernel.killMe();
		this.kernel = null;
		this.agent = null;
		this.name = null;
		Kernels.shutdownNow();
		super.tearDown();
	}
	
	private void bindToKernel(Agent h) {
		h.kernel = new WeakReference<KernelAgent>(this.kernel);
		this.kernel.getKernelContext().getAgentRepository().add(h.getAddress(), h);
	}

	/**
	 */
	public void testGetCRIOContext() {
		try {
			this.agent.getCRIOContext();
			fail("agent is not attached to a kernel. It may thrown an exception."); //$NON-NLS-1$
		}
		catch(Throwable _) {
			// Expected exception
		}
		bindToKernel(this.agent);
		assertSame(this.kernel.getCRIOContext(), this.agent.getCRIOContext());
	}

	/**
	 */
	public void testGetKernelContext() {
		try {
			this.agent.getKernelContext();
			fail("agent is not attached to a kernel. It may thrown an exception."); //$NON-NLS-1$
		}
		catch(Throwable _) {
			// Expected exception
		}
		bindToKernel(this.agent);
		assertSame(this.kernel.getKernelContext(), this.agent.getKernelContext());
	}
	
	/**
	 */
	public void testGetCreator() {
		Agent h = new Agent();
		h.kernel = new WeakReference<KernelAgent>(this.kernel);

		assertNull(h.getCreator());
		
		this.kernel.launchLightAgent(
				false, // differed execution?
				this.agent.getAddress(), // creator
				h, // new agent
				null, // agent name
				null, // activator
				null); // initialization parameters
		
		assertEquals(this.agent.getAddress(), h.getCreator());
	}

	/**
	 */
	public void testGetAgentString() {
		bindToKernel(this.agent);
		assertEquals(this.agent.getAddress(), this.agent.getAgent(this.name));
		assertNull(this.agent.getAgent("noname")); //$NON-NLS-1$
	}

	/**
	 */
	public final void testIsHeavyAgent() {
		bindToKernel(this.agent);
		assertFalse(this.agent.isHeavyAgent());

		Agent h = new Agent();
		try {
			assertNotNull(this.kernel.launchLightAgent(
					false, // differed execution?
					this.agent.getAddress(), // creator
					h, // new agent
					null, // agent name
					null, // activator
					null)); // initialization parameters
		}
		catch(RejectedExecutionException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		assertFalse(h.isHeavyAgent());
		
		Agent h2 = new Agent();
		try {
			assertNotNull(this.kernel.launchHeavyAgent(
					false, // differed execution?
					this.agent.getAddress(), // creator
					h2, // new agent
					null, // agent name
					null)); // initialization parameters
			assertTrue(h2.isHeavyAgent());
		}
		catch(RejectedExecutionException e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		h.killMe();
		h2.killMe();
	}
	
	/**
	 */
	public final void testIsLightAgent() {
		bindToKernel(this.agent);
		assertTrue(this.agent.isLightAgent());

		Agent h = new Agent();
		assertNotNull(this.kernel.launchLightAgent(
				false, // differed execution?
				this.agent.getAddress(), // creator
				h, // new agent
				null, // agent name
				null, // activator
				null)); // initialization parameters
		
		assertTrue(h.isLightAgent());
		
		Agent h2 = new Agent();
		assertNotNull(this.kernel.launchHeavyAgent(
				false, // differed execution?
				this.agent.getAddress(), // creator
				h2, // new agent
				null, // agent name
				null)); // initialization parameters
		
		assertFalse(h2.isLightAgent());
		
		h2.killMe();
	}

	/**
	 */
	public void testGetExecutionResource() {
		bindToKernel(this.agent);
		assertNull(this.agent.getExecutionResource());

		Agent h = new Agent();
		assertNotNull(this.kernel.launchLightAgent(
				false, // differed execution?
				this.agent.getAddress(), // creator
				h, // new agent
				null, // agent name
				null, // activator
				null)); // initialization parameters
		
		assertNull(h.getExecutionResource());
		
		Agent h2 = new Agent();
		assertNotNull(this.kernel.launchHeavyAgent(
				false, // differed execution?
				this.agent.getAddress(), // creator
				h2, // new agent
				null, // agent name
				null)); // initialization parameters
		
		assertNotNull(h2.getExecutionResource());
		
		h2.killMe();
	}

	/**
	 */
	public void testIsAlive() {
		bindToKernel(this.agent);
		assertFalse(this.agent.isAlive());
		
		this.agent.proceedPrivateInitialization();

		assertTrue(this.agent.isAlive());

		this.agent.proceedPrivateDestruction();

		assertFalse(this.agent.isAlive());
	}

	/**
	 */
	public void testGetState() {
		bindToKernel(this.agent);
		assertSame(AgentLifeState.UNBORN, this.agent.getState());
		
		this.agent.proceedPrivateInitialization();

		assertSame(AgentLifeState.ALIVE, this.agent.getState());

		this.agent.proceedPrivateDestruction();

		assertSame(AgentLifeState.DIED, this.agent.getState());
	}

	/**
	 */
	public void testCanCommitSuicide() {
		bindToKernel(this.agent);
		boolean b = this.agent.getCRIOContext().getProperties().getBoolean(JanusProperty.JANUS_AGENT_KEEP_ALIVE);
		assertEquals(!b, this.agent.canCommitSuicide());

		Agent h = new Agent(false);
		
		assertFalse(h.canCommitSuicide());
		
		Agent h2 = new Agent(true);
		
		assertTrue(h2.canCommitSuicide());
	}

	/**
	 */
	public void testKillMe() {
		bindToKernel(this.agent);
		assertEquals(StatusSeverity.WARNING, this.agent.killMe().getSeverity());
		this.agent.proceedPrivateInitialization();
		this.agent.proceedPrivateBehaviour();
		
		assertTrue(this.agent.killMe().isSuccess());
		assertEquals(StatusSeverity.WARNING, this.agent.killMe().getSeverity());
		
		assertSame(AgentLifeState.DYING, this.agent.getState());
	}

	/**
	 */
	public void testWakeUpIfSleeping() {
		bindToKernel(this.agent);
		assertFalse(this.agent.wakeUpIfSleeping());
	}

	/**
	 */
	public void testSleepFloat() {
		bindToKernel(this.agent);
		assertFalse(this.agent.wakeUpIfSleeping());
		assertTrue(this.agent.sleep(50));
		assertTrue(this.agent.wakeUpIfSleeping());
	}

	/**
	 */
	public void testKillAgentAddress_self() {
		bindToKernel(this.agent);
		this.agent.proceedPrivateInitialization();
		this.agent.proceedPrivateBehaviour();
		
		assertTrue(this.agent.kill(this.agent.getAddress()).isSuccess());
		
		assertSame(AgentLifeState.DYING, this.agent.getState());
	}

	/**
	 */
	public void testKillAgentAddress_nocreator() {
		bindToKernel(this.agent);
		this.agent.proceedPrivateInitialization();
		this.agent.proceedPrivateBehaviour();
		
		assertTrue(this.agent.kill(this.agent.getAddress()).isSuccess());
		
		assertSame(AgentLifeState.DYING, this.agent.getState());
	}

	/**
	 */
	public void testLaunchLightAgentAgentAddressAgentStringAgentActivator_nocreator() {
		assertFalse(this.kernel.getKernelContext().getAgentRepository().contains(this.agent.getAddress()));
		this.kernel.launchLightAgent(
				false, // differed execution?
				(AgentAddress)null, // creator
				this.agent, // new agent
				null, // agent name
				null, // activator
				null); // initialization parameters
		assertTrue(this.kernel.getKernelContext().getAgentRepository().contains(this.agent.getAddress()));
	}

	/**
	 */
	public void testLaunchHeavyAgentAgentAddressAgentString() {
		assertFalse(this.kernel.getKernelContext().getAgentRepository().contains(this.agent.getAddress()));
		this.kernel.launchHeavyAgent(
				false, // differed execution?
				(AgentAddress)null, // creator
				this.agent, // new agent
				null, // agent name
				null); // initialization parameters
		assertTrue(this.kernel.getKernelContext().getAgentRepository().contains(this.agent.getAddress()));
		this.agent.killMe();
	}

	/**
	 */
	public void testIsCompound() {
		bindToKernel(this.agent);
		assertFalse(this.agent.isCompound());
	}

	/**
	 */
	public void testIsRecruitmentAllowed() {
		bindToKernel(this.agent);
		assertFalse(this.agent.isRecruitmentAllowed());
	}

	/**
	 */
	public void testGetInternalOrganizations() {
		bindToKernel(this.agent);
		Collection<GroupAddress> orgas = this.agent.getInternalOrganizations();
		assertNotNull(orgas);
		assertTrue(orgas.isEmpty());
	}

	/**
	 */
	public void testGetMergingOrganization() {
		bindToKernel(this.agent);
		assertNull(this.agent.getMergingOrganization());
	}

	/**
	 */
	public void testGetHolonicOrganization() {
		bindToKernel(this.agent);
		assertNull(this.agent.getHolonicOrganization());
	}

	/**
	 */
	public void testGetMailbox() {
		Mailbox mb1, mb2;
		
		bindToKernel(this.agent);

		mb1 = this.agent.getMailbox();
		assertNotNull(mb1);
		
		mb2 = this.agent.getMailbox();
		assertNotNull(mb2);
		
		assertSame(mb1, mb2);
	}	

	/**
	 */
	public void testSetMailbox() {
		Mailbox mb = new LinkedListMailbox();
		
		this.agent.setMailbox(mb);
		
		assertSame(mb, this.agent.getMailbox());
	}	

	/**
	 * @throws Exception
	 */
	public void testGetMessage() throws Exception {
		bindToKernel(this.agent);

		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);

		assertNull(this.agent.getMessage());
		
		this.agent.sendMessage(
				m2,
				this.agent.getAddress());
		this.agent.sendMessage(
				m1,
				this.agent.getAddress());

		Mailbox mb = this.agent.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}
		
		assertSame(m1, this.agent.getMessage());
		assertSame(m2, this.agent.getMessage());
	}
	
	/**
	 * @throws Exception
	 */
	public void testPeekMessage() throws Exception {
		bindToKernel(this.agent);

		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);
		
		assertNull(this.agent.peekMessage());
		
		this.agent.sendMessage(
				m2,
				this.agent.getAddress());
		this.agent.sendMessage(
				m1,
				this.agent.getAddress());
		
		Mailbox mb = this.agent.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		assertSame(m1, this.agent.peekMessage());
		assertSame(m1, this.agent.peekMessage());
	}
	
	/**
	 * @throws Exception
	 */
	public void testGetMessages() throws Exception {
		bindToKernel(this.agent);

		Iterator<Message> iterator;
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);
		
		iterator = this.agent.getMessages().iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
		
		this.agent.sendMessage(
				m2,
				this.agent.getAddress());
		this.agent.sendMessage(
				m1,
				this.agent.getAddress());
		
		Mailbox mb = this.agent.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		iterator = this.agent.getMessages().iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = this.agent.getMessages().iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
	}
	
	/**
	 * @throws Exception
	 */
	public void testPeekMessages() throws Exception {
		bindToKernel(this.agent);

		Iterator<Message> iterator;
		Message m1 = new StringMessage("m1"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m1, null, null, 1024f);
		Message m2 = new StringMessage("m2"); //$NON-NLS-1$
		InteractionUtilStub.updateContext(m2, null, null, 1026f);
		
		iterator = this.agent.peekMessages().iterator();
		assertNotNull(iterator);
		assertFalse(iterator.hasNext());
		
		this.agent.sendMessage(
				m2,
				this.agent.getAddress());
		this.agent.sendMessage(
				m1,
				this.agent.getAddress());
		
		Mailbox mb = this.agent.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		iterator = this.agent.peekMessages().iterator();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertSame(m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m2, iterator.next());
		assertFalse(iterator.hasNext());

		iterator = this.agent.peekMessages().iterator();
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
		bindToKernel(this.agent);

		assertFalse(this.agent.hasMessage());
		
		this.agent.sendMessage(
				new Message(),
				this.agent.getAddress());
		
		Mailbox mb = this.agent.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		assertTrue(this.agent.hasMessage());
	}
	
	/**
	 * @throws Exception
	 */
	public void testGetMailboxSize() throws Exception {
		bindToKernel(this.agent);

		assertEquals(0, this.agent.getMailboxSize());
		
		this.agent.sendMessage(
				new Message(),
				this.agent.getAddress());
		
		Mailbox mb = this.agent.getMailbox();
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		assertEquals(1, this.agent.getMailboxSize());
		
		this.agent.sendMessage(
				new Message(),
				this.agent.getAddress());
		
		if (mb instanceof BufferedMailbox) {
			((BufferedMailbox)mb).synchronizeMessages();
		}

		assertEquals(2, this.agent.getMailboxSize());
	}

	/**
	 */
	public void testReplyToMessageMessageMessage() {
		//TODO: write unit test
	}

	/**
	 */
	public void testSendMessageMessageAgentAddressArray() {
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);
		
		AgentAddress adr;
		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		adr = agent1.sendMessage(msg, agent1.getAddress(), agent2.getAddress());

		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertNotNull(adr);
		assertEquals(agent2.getAddress(), adr);
		assertTrue(agent1.getMailbox().isEmpty());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertTrue(agent3.getMailbox().isEmpty());
		assertTrue(agent4.getMailbox().isEmpty());

		msg = new StringMessage("titi"); //$NON-NLS-1$
		adr = agent1.sendMessage(msg, agent1.getAddress(), agent2.getAddress(), agent3.getAddress());

		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertNotNull(adr);
		boolean isAgent2 = (agent2.getAddress().equals(adr));
		if (isAgent2) {
			assertEquals(agent2.getAddress(), adr);
		}
		else {
			assertEquals(agent3.getAddress(), adr);
		}
		assertTrue(agent1.getMailbox().isEmpty());
		if (isAgent2) {
			assertFalse(agent2.getMailbox().isEmpty());
			assertSame(msg, agent2.getMailbox().removeFirst());
			assertTrue(agent3.getMailbox().isEmpty());
		}
		else {
			assertTrue(agent2.getMailbox().isEmpty());
			assertFalse(agent3.getMailbox().isEmpty());
			assertSame(msg, agent3.getMailbox().removeFirst());
		}
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testSendMessageMessageList() {
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		AgentAddress adr;
		Message msg = new StringMessage("toto"); //$NON-NLS-1$
				
		adr = agent1.sendMessage(msg, Arrays.asList(agent1.getAddress(), agent2.getAddress()));

		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}

		assertNotNull(adr);
		assertEquals(agent2.getAddress(), adr);
		assertTrue(agent1.getMailbox().isEmpty());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertTrue(agent3.getMailbox().isEmpty());
		assertTrue(agent4.getMailbox().isEmpty());

		msg = new StringMessage("titi"); //$NON-NLS-1$
		adr = agent1.sendMessage(msg, Arrays.asList(agent1.getAddress(), agent2.getAddress(), agent3.getAddress()));

		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertNotNull(adr);
		boolean isAgent2 = (agent2.getAddress().equals(adr));
		if (isAgent2) {
			assertEquals(agent2.getAddress(), adr);
		}
		else {
			assertEquals(agent3.getAddress(), adr);
		}
		assertTrue(agent1.getMailbox().isEmpty());
		if (isAgent2) {
			assertFalse(agent2.getMailbox().isEmpty());
			assertSame(msg, agent2.getMailbox().removeFirst());
			assertEquals(agent1.getAddress(), msg.getSender());
			assertTrue(agent3.getMailbox().isEmpty());
		}
		else {
			assertTrue(agent2.getMailbox().isEmpty());
			assertFalse(agent3.getMailbox().isEmpty());
			assertSame(msg, agent3.getMailbox().removeFirst());
			assertEquals(agent1.getAddress(), msg.getSender());
		}
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testSendMessageMessageMessageReceiverSelectionPolicy() {
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		AgentAddress adr;
		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		
		adr = agent1.sendMessage(msg, new SelectionPolicy(agent2.getAddress()));

		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		Mailbox mb4 = agent3.getMailbox();
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}
		
		assertNotNull(adr);
		assertEquals(agent2.getAddress(), adr);
		assertTrue(agent1.getMailbox().isEmpty());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertTrue(agent3.getMailbox().isEmpty());
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testBroadcastMessageMessageAgentAddressArray() {
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		
		agent1.broadcastMessage(msg);
		
		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		Mailbox mb4 = agent4.getMailbox();
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}

		assertFalse(agent1.getMailbox().isEmpty());
		assertSame(msg, agent1.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent3.getMailbox().isEmpty());
		assertSame(msg, agent3.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent4.getMailbox().isEmpty());
		assertSame(msg, agent4.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());

		msg = new StringMessage("titi"); //$NON-NLS-1$
		agent1.broadcastMessage(msg, agent1.getAddress(), agent2.getAddress(), agent3.getAddress());
		
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}

		assertFalse(agent1.getMailbox().isEmpty());
		assertSame(msg, agent1.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent3.getMailbox().isEmpty());
		assertSame(msg, agent3.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testBroadcastMessageMessageIterable() {
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		
		agent1.broadcastMessage(msg, Collections.<AgentAddress>emptyList());
		
		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		Mailbox mb4 = agent4.getMailbox();
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}

		assertFalse(agent1.getMailbox().isEmpty());
		assertSame(msg, agent1.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent3.getMailbox().isEmpty());
		assertSame(msg, agent3.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent4.getMailbox().isEmpty());
		assertSame(msg, agent4.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());

		msg = new StringMessage("titi"); //$NON-NLS-1$
		agent1.broadcastMessage(msg, Arrays.asList(agent1.getAddress(), agent2.getAddress(), agent3.getAddress()));
		
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}

		assertFalse(agent1.getMailbox().isEmpty());
		assertSame(msg, agent1.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertFalse(agent3.getMailbox().isEmpty());
		assertSame(msg, agent3.getMailbox().removeFirst());
		assertEquals(agent1.getAddress(), msg.getSender());
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testForwardMessageMessageMessageReceiverSelectionPolicy() {
		Agent agent0 = new AgentStub("agent0", false); //$NON-NLS-1$
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$
		
		bindToKernel(agent0);
		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		SelectionPolicy policy = new SelectionPolicy(agent2.getAddress());

		AgentAddress adr;
		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		agent0.sendMessage(msg, agent1.getAddress());
				
		Mailbox mb0 = agent1.getMailbox();
		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		Mailbox mb4 = agent4.getMailbox();
		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		agent1.getMailbox().clear();

		assertTrue(agent0.getMailbox().isEmpty());
		assertTrue(agent1.getMailbox().isEmpty());
		assertTrue(agent2.getMailbox().isEmpty());
		assertTrue(agent3.getMailbox().isEmpty());
		assertTrue(agent4.getMailbox().isEmpty());

		adr = agent1.forwardMessage(msg, policy);
		
		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).synchronizeMessages();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}

		assertNotNull(adr);
		assertEquals(agent2.getAddress(), adr);
		assertTrue(agent0.getMailbox().isEmpty());
		assertTrue(agent1.getMailbox().isEmpty());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertTrue(agent3.getMailbox().isEmpty());
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testForwardMessageMessageAgentAddressArray() {
		Agent agent0 = new AgentStub("agent0", false); //$NON-NLS-1$
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent0);
		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		AgentAddress adr;
		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		agent0.sendMessage(msg, agent1.getAddress());
				
		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		Mailbox mb4 = agent4.getMailbox();
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).clearBuffer();
		}
		agent1.getMailbox().clear();

		assertEquals(agent0.getAddress(), msg.getSender());
		adr = agent1.forwardMessage(msg, agent1.getAddress(), agent2.getAddress());

		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}
		
		assertNotNull(adr);
		assertEquals(agent2.getAddress(), adr);
		assertTrue(agent0.getMailbox().isEmpty());
		assertTrue(agent1.getMailbox().isEmpty());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertTrue(agent3.getMailbox().isEmpty());
		assertTrue(agent4.getMailbox().isEmpty());

		msg = new StringMessage("titi"); //$NON-NLS-1$
		agent0.sendMessage(msg, agent1.getAddress());
		
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).clearBuffer();
		}
		agent1.getMailbox().clear();
		agent2.getMailbox().clear();
		agent3.getMailbox().clear();

		assertEquals(agent0.getAddress(), msg.getSender());
		adr = agent1.forwardMessage(msg, agent1.getAddress(), agent2.getAddress(), agent3.getAddress());

		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}
		
		assertNotNull(adr);
		boolean isAgent2 = (agent2.getAddress().equals(adr));
		if (isAgent2) {
			assertEquals(agent2.getAddress(), adr);
		}
		else {
			assertEquals(agent3.getAddress(), adr);
		}
		assertTrue(agent0.getMailbox().isEmpty());
		assertTrue(agent1.getMailbox().isEmpty());
		if (isAgent2) {
			assertFalse(agent2.getMailbox().isEmpty());
			assertSame(msg, agent2.getMailbox().removeFirst());
			assertEquals(agent0.getAddress(), msg.getSender());
			assertTrue(agent3.getMailbox().isEmpty());
		}
		else {
			assertTrue(agent2.getMailbox().isEmpty());
			assertFalse(agent3.getMailbox().isEmpty());
			assertSame(msg, agent3.getMailbox().removeFirst());
			assertEquals(agent0.getAddress(), msg.getSender());
		}
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testForwardMessageMessageList() {
		Agent agent0 = new AgentStub("agent0", false); //$NON-NLS-1$
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent0);
		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		AgentAddress adr;
		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		agent0.sendMessage(msg, Arrays.asList(agent1.getAddress()));
		
		Mailbox mb0 = agent0.getMailbox();
		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		Mailbox mb4 = agent4.getMailbox();
		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).clearBuffer();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).clearBuffer();
		}
		agent1.getMailbox().clear();
				
		assertEquals(agent0.getAddress(), msg.getSender());
		adr = agent1.forwardMessage(msg, Arrays.asList(agent1.getAddress(), agent2.getAddress()));

		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).synchronizeMessages();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}

		assertNotNull(adr);
		assertEquals(agent2.getAddress(), adr);
		assertTrue(agent0.getMailbox().isEmpty());
		assertTrue(agent1.getMailbox().isEmpty());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertTrue(agent3.getMailbox().isEmpty());
		assertTrue(agent4.getMailbox().isEmpty());

		msg = new StringMessage("titi"); //$NON-NLS-1$
		agent0.sendMessage(msg, agent1.getAddress());
		
		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).clearBuffer();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).clearBuffer();
		}
		agent1.getMailbox().clear();
		
		assertEquals(agent0.getAddress(), msg.getSender());
		adr = agent1.forwardMessage(msg, Arrays.asList(agent1.getAddress(), agent2.getAddress(), agent3.getAddress()));

		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).synchronizeMessages();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}

		assertNotNull(adr);
		boolean isAgent2 = (agent2.getAddress().equals(adr));
		if (isAgent2) {
			assertEquals(agent2.getAddress(), adr);
		}
		else {
			assertEquals(agent3.getAddress(), adr);
		}
		assertTrue(agent0.getMailbox().isEmpty());
		assertTrue(agent1.getMailbox().isEmpty());
		if (isAgent2) {
			assertFalse(agent2.getMailbox().isEmpty());
			assertSame(msg, agent2.getMailbox().removeFirst());
			assertEquals(agent0.getAddress(), msg.getSender());
			assertTrue(agent3.getMailbox().isEmpty());
		}
		else {
			assertTrue(agent2.getMailbox().isEmpty());
			assertFalse(agent3.getMailbox().isEmpty());
			assertSame(msg, agent3.getMailbox().removeFirst());
			assertEquals(agent0.getAddress(), msg.getSender());
		}
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testForwardBroadcastMessageMessageAgentAddressArray() {
		Agent agent0 = new AgentStub("agent0", false); //$NON-NLS-1$
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent0);
		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		agent0.sendMessage(msg, agent1.getAddress());
		Mailbox mb0 = agent0.getMailbox();
		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		Mailbox mb4 = agent4.getMailbox();
		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).clearBuffer();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).clearBuffer();
		}
		agent1.getMailbox().clear();
				
		assertEquals(agent0.getAddress(), msg.getSender());
		agent1.forwardBroadcastMessage(msg);

		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).synchronizeMessages();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}
		
		assertFalse(agent0.getMailbox().isEmpty());
		assertSame(msg, agent0.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent1.getMailbox().isEmpty());
		assertSame(msg, agent1.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent3.getMailbox().isEmpty());
		assertSame(msg, agent3.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent4.getMailbox().isEmpty());
		assertSame(msg, agent4.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());

		msg = new StringMessage("titi"); //$NON-NLS-1$
		agent0.sendMessage(msg, agent1.getAddress());

		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).clearBuffer();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).clearBuffer();
		}
		agent1.getMailbox().clear();
				
		assertEquals(agent0.getAddress(), msg.getSender());
		agent1.forwardBroadcastMessage(msg, agent2.getAddress(), agent3.getAddress());

		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).synchronizeMessages();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}
		
		assertTrue(agent0.getMailbox().isEmpty());
		assertTrue(agent1.getMailbox().isEmpty());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent3.getMailbox().isEmpty());
		assertSame(msg, agent3.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertTrue(agent4.getMailbox().isEmpty());
	}

	/**
	 */
	public void testForwardBroadcastMessageMessageIterable() {
		Agent agent0 = new AgentStub("agent0", false); //$NON-NLS-1$
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent0);
		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);

		Message msg = new StringMessage("toto"); //$NON-NLS-1$
		agent0.sendMessage(msg, agent1.getAddress());
				
		Mailbox mb0 = agent0.getMailbox();
		Mailbox mb1 = agent1.getMailbox();
		Mailbox mb2 = agent2.getMailbox();
		Mailbox mb3 = agent3.getMailbox();
		Mailbox mb4 = agent4.getMailbox();
		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).clearBuffer();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).clearBuffer();
		}
		agent1.getMailbox().clear();

		assertEquals(agent0.getAddress(), msg.getSender());
		agent1.forwardBroadcastMessage(msg, Collections.<AgentAddress>emptyList());

		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).synchronizeMessages();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}
		
		assertFalse(agent0.getMailbox().isEmpty());
		assertSame(msg, agent0.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent1.getMailbox().isEmpty());
		assertSame(msg, agent1.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent3.getMailbox().isEmpty());
		assertSame(msg, agent3.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent4.getMailbox().isEmpty());
		assertSame(msg, agent4.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());

		msg = new StringMessage("titi"); //$NON-NLS-1$
		agent0.sendMessage(msg, agent1.getAddress());
		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).clearBuffer();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).clearBuffer();
		}
		agent1.getMailbox().clear();
				
		assertEquals(agent0.getAddress(), msg.getSender());
		agent1.forwardBroadcastMessage(msg, Arrays.asList(agent2.getAddress(), agent3.getAddress()));

		if (mb0 instanceof BufferedMailbox) {
			((BufferedMailbox)mb0).synchronizeMessages();
		}
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		if (mb4 instanceof BufferedMailbox) {
			((BufferedMailbox)mb4).synchronizeMessages();
		}
		

		assertTrue(agent0.getMailbox().isEmpty());
		assertTrue(agent1.getMailbox().isEmpty());
		assertFalse(agent2.getMailbox().isEmpty());
		assertSame(msg, agent2.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertFalse(agent3.getMailbox().isEmpty());
		assertSame(msg, agent3.getMailbox().removeFirst());
		assertEquals(agent0.getAddress(), msg.getSender());
		assertTrue(agent4.getMailbox().isEmpty());
	}
	
	/**
	 */
	public void testGetLocalAgents() {
		SizedIterator<AgentAddress> iterator;
		
		Agent agent1 = new AgentStub("agent1", false); //$NON-NLS-1$
		Agent agent2 = new AgentStub("agent2", false); //$NON-NLS-1$
		Agent agent3 = new AgentStub("agent3", false); //$NON-NLS-1$
		Agent agent4 = new AgentStub("agent4", false); //$NON-NLS-1$

		bindToKernel(agent1);
		bindToKernel(agent2);
		bindToKernel(agent3);
		bindToKernel(agent4);
		
		Collection<AgentAddress> addresses = new ArrayList<AgentAddress>();
		iterator = agent1.getLocalAgents();
		assertNotNull(iterator);
		assertEquals(4, iterator.totalSize());
		while (iterator.hasNext()) {
			addresses.add(iterator.next());
		}
		
		assertEquals(4, addresses.size());
		assertTrue(addresses.remove(agent1.getAddress()));
		assertTrue(addresses.remove(agent2.getAddress()));
		assertTrue(addresses.remove(agent3.getAddress()));
		assertTrue(addresses.remove(agent4.getAddress()));
		assertTrue(addresses.isEmpty());
	}

	/**
	 */
	public void testIsSelfKillableNow_notSuicidal() {
		AgentStub ag = new AgentStub(this.name, false);
		bindToKernel(ag);
		
		ag.proceedPrivateInitialization();

		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());
		
		for(int i=0; i<5; i++) ag.live();
		
		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());
		
		ag.requestTestRole();
		for(int i=0; i<5; i++) ag.live();
		
		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());
		
		ag.leaveTestRole();
		for(int i=0; i<5; i++) ag.live();

		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());
	}

	/**
	 */
	public void testIsSelfKillableNow_suicidal() {
		AgentStub ag = new AgentStub(this.name, true);
		bindToKernel(ag);
		
		ag.proceedPrivateInitialization();

		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());
		
		for(int i=0; i<5; i++) ag.live();
		
		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());
		
		ag.requestTestRole();
		for(int i=0; i<5; i++) ag.live();
		
		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());
		
		ag.leaveTestRole();
		for(int i=0; i<5; i++) ag.live();

		assertTrue(ag.canCommitSuicide());
		assertTrue(ag.isSelfKillableNow());
	}
	
	/**
	 */
	public void testSignalReception_MonoListener() {
		SignalAgentStub a = new SignalAgentStub();
		
		bindToKernel(a);

		a.proceedPrivateInitialization();

		// Simulate the life of the agent
		while (a.state<5) {
			Status s = a.proceedPrivateBehaviour();
			assertTrue(s.isSuccess());
		}
		
		Signal s;

		assertEquals(3, a.signals.size());
		s = a.signals.get(0);
		assertNotNull(s);
		assertEquals("SIG1", s.getName()); //$NON-NLS-1$
		s = a.signals.get(1);
		assertNotNull(s);
		assertEquals("SIG2", s.getName()); //$NON-NLS-1$
		s = a.signals.get(2);
		assertNotNull(s);
		assertEquals("SIG3", s.getName()); //$NON-NLS-1$
	}

	/**
	 */
	public void testSignalReception_MultiListener() {
		SignalAgentStub a = new SignalAgentStub();
		SignalListenerStub l1 = new SignalListenerStub();
		SignalListenerStub l2 = new SignalListenerStub();
		
		bindToKernel(a);
		a.registerSignalListener(l1);
		a.registerSignalListener(l2);

		a.proceedPrivateInitialization();

		// Simulate the life of the agent
		while (a.state<5) {
			Status s = a.proceedPrivateBehaviour();
			assertTrue(s.isSuccess());
		}
		
		Signal s;

		assertEquals(3, a.signals.size());
		s = a.signals.get(0);
		assertNotNull(s);
		assertEquals("SIG1", s.getName()); //$NON-NLS-1$
		s = a.signals.get(1);
		assertNotNull(s);
		assertEquals("SIG2", s.getName()); //$NON-NLS-1$
		s = a.signals.get(2);
		assertNotNull(s);
		assertEquals("SIG3", s.getName()); //$NON-NLS-1$

		assertEquals(3, l1.signals.size());
		s = l1.signals.get(0);
		assertNotNull(s);
		assertEquals("SIG1", s.getName()); //$NON-NLS-1$
		s = l1.signals.get(1);
		assertNotNull(s);
		assertEquals("SIG2", s.getName()); //$NON-NLS-1$
		s = l1.signals.get(2);
		assertNotNull(s);
		assertEquals("SIG3", s.getName()); //$NON-NLS-1$

		assertEquals(3, l2.signals.size());
		s = l2.signals.get(0);
		assertNotNull(s);
		assertEquals("SIG1", s.getName()); //$NON-NLS-1$
		s = l2.signals.get(1);
		assertNotNull(s);
		assertEquals("SIG2", s.getName()); //$NON-NLS-1$
		s = l2.signals.get(2);
		assertNotNull(s);
		assertEquals("SIG3", s.getName()); //$NON-NLS-1$
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AgentStub extends Agent {

		private static final long serialVersionUID = -8844208765009912688L;
		
		private GroupAddress group = null;
		
		/**
		 * @param name
		 * @param commitSuicide
		 */
		public AgentStub(String name, boolean commitSuicide) {
			super(commitSuicide);
			if (name!=null) getAddress().setName(name);
		}
		
		/**
		 */
		public void requestTestRole() {
			this.group = getOrCreateGroup(new OrganizationFactory<Organization1Stub>() {
				@Override
				public Class<Organization1Stub> getOrganizationType() {
					return Organization1Stub.class;
				}
				@Override
				public Organization1Stub newInstance(CRIOContext context)
						throws Exception {
					return new Organization1Stub(context);
				}
			});
			assertNotNull(this.group);
			assertNotNull(requestRole(RoleStub.class, this.group, new RoleFactory() {
				@Override
				public Role newInstance(Class<? extends Role> type)
						throws Exception {
					return type.newInstance();
				}
			}));
		}
		
		/**
		 */
		public void leaveTestRole() {
			assertNotNull(this.group);
			assertTrue(leaveRole(RoleStub.class, this.group));
			this.group = null;
		}
		
	} // class AgentStub

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public class SelectionPolicy implements MessageReceiverSelectionPolicy {

		private final AgentAddress ag;
		
		/**
		 * @param a
		 */
		public SelectionPolicy(AgentAddress a) {
			this.ag = a;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress selectEntity(AgentAddress sender,
				List<? extends AgentAddress> availableEntities) {
			if (availableEntities.contains(this.ag))
				return this.ag;
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress selectEntity(AgentAddress sender,
				DirectAccessCollection<? extends AgentAddress> availableEntities) {
			if (availableEntities.contains(this.ag))
				return this.ag;
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress selectEntity(AgentAddress sender,
				SizedIterator<? extends AgentAddress> availableEntities) {
			AgentAddress a;
			while (availableEntities.hasNext()) {
				a = availableEntities.next();
				if (a.equals(this.ag)) return this.ag;
			}
			return null;
		}

	} // class SelectionPolicy

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SignalAgentStub extends Agent implements SignalListener {

		private static final long serialVersionUID = 1576060174346314275L;

		/** */
		public int state = 0;
		
		/** */
		public final List<Signal> signals = new ArrayList<Signal>();

		/**
		 */
		public SignalAgentStub() {
			//
		}
		
		/**
		 * @param l
		 */
		public void registerSignalListener(SignalListener l) {
			addSignalListener(l);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status live() {
			Status s = super.live();
			if (s.isSuccess()) {
				switch(this.state) {
				case 0:
					addSignalListener(this);
					break;
				case 1:
					fireSignal(new Signal(this, "SIG1")); //$NON-NLS-1$
					break;
				case 2:
					fireSignal(new Signal(this, "SIG2")); //$NON-NLS-1$
					break;
				case 3:
					fireSignal(new Signal(this, "SIG3")); //$NON-NLS-1$
					break;
				default:
				}
				this.state++;
			}
			return s;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onSignal(Signal signal) {
			this.signals.add(signal);
		}
		
	} // class SignalAgentStub

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SignalListenerStub implements SignalListener {

		/** */
		public final List<Signal> signals = new ArrayList<Signal>();

		/**
		 */
		public SignalListenerStub() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onSignal(Signal signal) {
			this.signals.add(signal);
		}
		
	} // class SignalListenerStub

}
