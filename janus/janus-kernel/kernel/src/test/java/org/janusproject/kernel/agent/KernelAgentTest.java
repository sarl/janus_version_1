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

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.role.RoleFactory;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mailbox.BufferedMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelAgentTest extends TestCase {

	private static final long TIMEOUT = 10000;
	
	private KernelAgent agent;
	private AgentListener listener;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.listener = new AgentListener(AgentLifeState.UNBORN);
		this.agent = new KernelAgent(new AgentActivator(), false, null, this.listener);
		this.agent.addAgentLifeStateListener(this.listener);
		assertEquals(1, Kernels.getKernelCount());
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.agent.removeAgentLifeStateListener(this.listener);
		this.agent = null;
		this.listener = null;
		Kernels.shutdownNow();
		assertEquals(0, Kernels.getKernelCount());
		super.tearDown();
	}

	private static void assertNotAlive(AgentListener listener) throws TimeoutException {
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis()-start)<=TIMEOUT) {
			if (listener.state==AgentLifeState.UNBORN) {
				return;
			}
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException _) {
				//
			}
		}
		throw new TimeoutException(listener.state.toString());
	}

	private static void assertAlive(AgentListener listener) throws TimeoutException {
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis()-start)<=TIMEOUT) {
			if (listener.state==AgentLifeState.ALIVE) {
				return;
			}
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException _) {
				//
			}
		}
		throw new TimeoutException(listener.state.toString());
	}
	
	private static void assertDead(AgentListener listener) throws TimeoutException {
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis()-start)<=TIMEOUT) {
			if (listener.state==AgentLifeState.DIED) {
				return;
			}
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException _) {
				//
			}
		}
		throw new TimeoutException(listener.state.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testShutdownNow() throws Exception {
		String appName = this.agent.getKernelContext().getProperties().getProperty(JanusProperty.JANUS_APPLICATION_NAME.getPropertyName(), null);
		String baseString;
		if (appName!=null && !"".equals(appName) && !JanusProperties.DEFAULT_APPLICATION_NAME.equals(appName)) { //$NON-NLS-1$
			baseString = Locale.getString(KernelAgent.class, "KERNEL_AGENT_NAME_WITH_APP", appName); //$NON-NLS-1$
		}
		else {
			baseString = Locale.getString(KernelAgent.class, "KERNEL_AGENT_NAME"); //$NON-NLS-1$
		}
		assertNotNull(baseString);
		Thread[] threads = new Thread[100];
		int count, kernelAgentCount;

		assertAlive(this.listener);
		
		assertEquals(1, Kernels.getKernelCount());
		count = Thread.enumerate(threads);
		kernelAgentCount = 0;
		String tName;
		for(int i=0; i<count; ++i) {
			tName = threads[i].getName();
			if (baseString.equals(tName)) {
				++kernelAgentCount;
			}
		}
		assertEquals(1, kernelAgentCount);
		
		this.agent.shutdownNow();
		
		assertDead(this.listener);

		assertEquals(0, Kernels.getKernelCount());
		count = Thread.enumerate(threads);
		kernelAgentCount = 0;
		for(int i=0; i<count; ++i) {
			tName = threads[i].getName();
			if (baseString.equals(tName)) {
				++kernelAgentCount;
			}
		}
		assertEquals(0, kernelAgentCount);
	}

	/**
	 */
	public void testToKernel() {
		Kernel k = this.agent.toKernel();
		assertNotNull(k);
		assertNotSame(this.agent, k);
	}

	/** 
	 */
	public void testKernelAgent() {
		assertTrue(Kernels.contains(this.agent.getAddress()));
	}

	/**
	 */
	public void testGetCRIOContext() {
		CRIOContext c;
		assertNotNull(c = this.agent.getCRIOContext());
		assertSame(c, this.agent.getCRIOContext());
		assertSame(c, this.agent.getKernelContext());
	}

	/**
	 */
	public void testGetKernelContext() {
		CRIOContext c;
		assertNotNull(c = this.agent.getKernelContext());
		assertSame(c, this.agent.getKernelContext());
		assertSame(c, this.agent.getCRIOContext());
	}

	/**
	 * @throws TimeoutException 
	 */
	public void testKillAgentAddress_self() throws TimeoutException {
		assertAlive(this.listener);
		this.agent.kill(this.agent.getAddress());
		assertDead(this.listener);
	}
	
	/**
	 */
	public void testWakeUpIfSleeping() {
		assertFalse(this.agent.wakeUpIfSleeping());
	}

	/**
	 */
	public void testSleepFloat() {
		assertFalse(this.agent.wakeUpIfSleeping());
		assertFalse(this.agent.sleep(50));
		assertFalse(this.agent.wakeUpIfSleeping());
	}

	/**
	 * @throws Exception
	 */
	public void testLaunchLightAgentAgentAddressAgentStringAgentActivator_immediate() throws Exception {
		AgentAddress adr = new AgentAddressStub();
		AgentStub h = new AgentStub(false);
		
		assertSame(AgentLifeState.UNBORN, h.getState());

		this.agent.launchLightAgent(
				false, // differed execution?
				adr, // creator
				h, // new agent
				null, // agent name
				null, // activator
				null); // initialization parameters
		
		while (!h.isInit.get()) {
			Thread.yield();
		}
		
		Thread.sleep(500);
		
		assertEquals(adr, h.creator);
		assertSame(AgentLifeState.ALIVE, h.getState());
		assertFalse(h.isHeavyAgent());
		assertTrue(h.isLightAgent());
		assertSame(this.agent, h.kernel.get());
	}

	/**
	 * @throws Exception
	 */
	public void testLaunchLightAgentAgentAddressAgentStringAgentActivator_differed() throws Exception {
		AgentAddress adr = new AgentAddressStub();
		AgentStub h = new AgentStub(false);
		
		assertSame(AgentLifeState.UNBORN, h.getState());

		this.agent.launchLightAgent(
				true, // differed execution?
				adr, // creator
				h, // new agent
				null, // agent name
				null, // activator
				null); // initialization parameters
		
		for(int i=0; i<200; ++i) {
			assertFalse(h.isInit.get());
			Thread.yield();
		}
		
		this.agent.launchDifferedExecutionAgents();
		
		while (!h.isInit.get()) {
			Thread.yield();
		}
		
		Thread.sleep(500);
		
		assertEquals(adr, h.creator);
		assertSame(AgentLifeState.ALIVE, h.getState());
		assertFalse(h.isHeavyAgent());
		assertTrue(h.isLightAgent());
		assertSame(this.agent, h.kernel.get());
	}

	/**
	 * @throws TimeoutException 
	 */
	public void testLaunchHeavyAgentAgentAddressAgentString_immediate() throws TimeoutException {
		AgentAddress adr = new AgentAddressStub();
		Agent h = new Agent(false);
		
		AgentListener l = new AgentListener(h.getState());
		h.addAgentLifeStateListener(l);
		
		this.agent.launchHeavyAgent(
				false, // differed execution?
				adr, // creator
				h, // new agent
				null, // agent name
				null); // initialization parameters
		
		assertAlive(l);
		
		assertEquals(adr, h.creator);
		assertTrue(h.isHeavyAgent());
		assertFalse(h.isLightAgent());
		assertSame(this.agent, h.kernel.get());
		
		h.killMe();
		
		assertDead(l);
		h.removeAgentLifeStateListener(l);		
	}
	
	/**
	 * @throws TimeoutException 
	 */
	public void testLaunchHeavyAgentAgentAddressAgentString_differed() throws TimeoutException {
		AgentAddress adr = new AgentAddressStub();
		Agent h = new Agent(false);
		
		AgentListener l = new AgentListener(h.getState());
		h.addAgentLifeStateListener(l);
		
		this.agent.launchHeavyAgent(
				true, // differed execution?
				adr, // creator
				h, // new agent
				null, // agent name
				null); // initialization parameters
		
		assertNotAlive(l);
		
		this.agent.launchDifferedExecutionAgents();
		
		assertAlive(l);
		
		assertEquals(adr, h.creator);
		assertTrue(h.isHeavyAgent());
		assertFalse(h.isLightAgent());
		assertSame(this.agent, h.kernel.get());
		
		h.killMe();
		
		assertDead(l);
		h.removeAgentLifeStateListener(l);		
	}
	
	/**
	 * @throws Exception
	 */
	public void testKernelSuicide() throws Exception {
		Kernel kernel = Kernels.get(false);
		for (int i=1;i<5;++i){
			kernel.submitLightAgent(new AgentStub(false));
		}
		kernel.submitLightAgent(new AgentStub(false));
		kernel.submitLightAgent(new AgentStub(false));
		
		Thread.sleep(1000);
		
		kernel.launchDifferedExecutionAgents();
		
		kernel.kill();
	}

	/**
	 */
	public void testForwardMessageMessage_inPrivilegedCRIOContext() {
		CrioAgentStub player1 = new CrioAgentStub(false);
		CrioAgentStub player2 = new CrioAgentStub(false);
		CrioAgentStub player3 = new CrioAgentStub(false);
	
		this.agent.launchLightAgent(player1);
		this.agent.launchLightAgent(player2);
		this.agent.launchLightAgent(player3);

		GroupAddress ga = player1.dbgGetGroup();
		
		assertTrue(player1.dbgRequestRole(ga, RoleStub.class));
		assertTrue(player2.dbgRequestRole(ga, RoleStub.class));
		assertTrue(player3.dbgRequestRole(ga, Role3Stub.class));

		Mailbox mb1 = player1.dbgGetMailbox(ga, RoleStub.class);
		Mailbox mb2 = player2.dbgGetMailbox(ga, RoleStub.class);
		Mailbox mb3 = player3.dbgGetMailbox(ga, Role3Stub.class);
		
		Message message = new Message();
		
		// Send the message to be sure the message's fields are correctly set
		// and clear all the mail boxes.
		player2.dbgSendMessage(ga, RoleStub.class, RoleStub.class, player1.getAddress(), message);
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		mb1.clear();
		mb2.clear();
		mb3.clear();

		assertTrue(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());

		Address adr = this.agent.forwardMessage(message);
		assertNotNull(adr);
		assertTrue(adr instanceof RoleAddress);
		assertEquals(ga, ((RoleAddress)adr).getGroup());
		assertEquals(RoleStub.class, ((RoleAddress)adr).getRole());
		assertEquals(player1.getAddress(), ((RoleAddress)adr).getPlayer());
	
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertFalse(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());
		
		assertSame(message, mb1.removeFirst());
	}
	
	/**
	 */
	public void testForwardBroadcastMessageMessage_inPrivilegedCRIOContext() {
		CrioAgentStub player1 = new CrioAgentStub(false);
		CrioAgentStub player2 = new CrioAgentStub(false);
		CrioAgentStub player3 = new CrioAgentStub(false);
	
		this.agent.launchLightAgent(player1);
		this.agent.launchLightAgent(player2);
		this.agent.launchLightAgent(player3);

		GroupAddress ga = player1.dbgGetGroup();
		
		assertTrue(player1.dbgRequestRole(ga, RoleStub.class));
		assertTrue(player2.dbgRequestRole(ga, RoleStub.class));
		assertTrue(player3.dbgRequestRole(ga, Role3Stub.class));

		Mailbox mb1 = player1.dbgGetMailbox(ga, RoleStub.class);
		Mailbox mb2 = player2.dbgGetMailbox(ga, RoleStub.class);
		Mailbox mb3 = player3.dbgGetMailbox(ga, Role3Stub.class);
		
		Message message = new Message();
		
		// Send the message to be sure the message's fields are correctly set
		// and clear all the mail boxes.
		player2.dbgBroadcastMessage(ga, RoleStub.class, RoleStub.class, message);
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		mb1.clear();
		mb2.clear();
		mb3.clear();

		assertTrue(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());

		this.agent.forwardBroadcastMessage(message);
	
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertFalse(mb1.isEmpty());
		assertFalse(mb2.isEmpty());
		assertTrue(mb3.isEmpty());
		
		assertSame(message, mb1.removeFirst());
		assertSame(message, mb2.removeFirst());
	}

	/**
	 */
	public void testForwardMessageMessage_inPrivilegedAgentContext() {
		AgentStub player1 = new AgentStub(false);
		AgentStub player2 = new AgentStub(false);
		AgentStub player3 = new AgentStub(false);
	
		this.agent.launchLightAgent(player1);
		this.agent.launchLightAgent(player2);
		this.agent.launchLightAgent(player3);

		Mailbox mb1 = player1.getMailbox();
		Mailbox mb2 = player2.getMailbox();
		Mailbox mb3 = player3.getMailbox();
		
		Message message = new Message();
		
		// Send the message to be sure the message's fields are correctly set
		// and clear all the mail boxes.
		player2.sendMessage(message, player1.getAddress());
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		mb1.clear();
		mb2.clear();
		mb3.clear();

		assertTrue(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());

		assertEquals(player1.getAddress(), this.agent.forwardMessage(message));
	
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertFalse(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());
		
		assertSame(message, mb1.removeFirst());
	}

	/**
	 */
	public void testForwardBroadcastMessageMessage_inPrivilegedAgentContext() {
		AgentStub player1 = new AgentStub(false);
		AgentStub player2 = new AgentStub(false);
		AgentStub player3 = new AgentStub(false);
	
		this.agent.launchLightAgent(player1);
		this.agent.launchLightAgent(player2);
		this.agent.launchLightAgent(player3);

		Mailbox mb1 = player1.getMailbox();
		Mailbox mb2 = player2.getMailbox();
		Mailbox mb3 = player3.getMailbox();
		
		Message message = new Message();
		
		// Send the message to be sure the message's fields are correctly set
		// and clear all the mail boxes.
		player2.broadcastMessage(message);
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).clearBuffer();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).clearBuffer();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).clearBuffer();
		}
		mb1.clear();
		mb2.clear();
		mb3.clear();

		assertTrue(mb1.isEmpty());
		assertTrue(mb2.isEmpty());
		assertTrue(mb3.isEmpty());

		this.agent.forwardBroadcastMessage(message);
	
		if (mb1 instanceof BufferedMailbox) {
			((BufferedMailbox)mb1).synchronizeMessages();
		}
		if (mb2 instanceof BufferedMailbox) {
			((BufferedMailbox)mb2).synchronizeMessages();
		}
		if (mb3 instanceof BufferedMailbox) {
			((BufferedMailbox)mb3).synchronizeMessages();
		}
		
		assertFalse(mb1.isEmpty());
		assertFalse(mb2.isEmpty());
		assertFalse(mb3.isEmpty());
		
		assertSame(message, mb1.removeFirst());
		assertSame(message, mb2.removeFirst());
		assertSame(message, mb3.removeFirst());
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public class AgentStub extends Agent {

		private static final long serialVersionUID = 6186707084746117100L;
		
		/**
		 */
		public final AtomicBoolean isInit = new AtomicBoolean(false);
		/**
		 */
		public final AtomicBoolean isRun = new AtomicBoolean(false);
		/**
		 */
		public final AtomicBoolean isDestroy = new AtomicBoolean(false);
		
		/**
		 * @param commitSuicide
		 */
		public AgentStub(boolean commitSuicide) {
			super(commitSuicide);
		}
		
		@Override
		public Status activate(Object... parameters) {
			this.isInit.set(true);
			return super.activate(parameters);
		}

		@Override
		public Status live() {
			this.isRun.set(true);
			return super.live();
		}

		@Override
		public Status end() {
			this.isDestroy.set(true);
			return super.end();
		}

	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class CrioOrgaFactory implements OrganizationFactory<Organization1Stub> {
		
		public CrioOrgaFactory() {
			//
		}

		@Override
		public Class<Organization1Stub> getOrganizationType() {
			return Organization1Stub.class;
		}

		@Override
		public Organization1Stub newInstance(CRIOContext context) throws Exception {
			return new Organization1Stub(context);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class CrioRoleFactory implements RoleFactory {
		
		public CrioRoleFactory() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Role newInstance(Class<? extends Role> type) throws Exception {
			return type.newInstance();
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class CrioAgentStub extends Agent {

		private static final long serialVersionUID = 2127561440418907830L;

		public CrioAgentStub(boolean commitSuicide) {
			super(commitSuicide);
		}
		
		public GroupAddress dbgGetGroup() {
			return getOrCreateGroup(new CrioOrgaFactory());
		}
		
		public boolean dbgRequestRole(GroupAddress ga, Class<? extends Role> role) {
			return requestRole(role, ga, new CrioRoleFactory())!=null;
		}

		public Mailbox dbgGetMailbox(GroupAddress ga, Class<? extends Role> role) {
			return getMailbox(ga, role);
		}
		
		public void dbgSendMessage(
				GroupAddress ga,
				Class<? extends Role> emitter,
				Class<? extends Role> receiver,
				AgentAddress receiverAdr,
				Message message) {
			sendMessage(ga, emitter, receiver, receiverAdr, message);
		}

		public void dbgBroadcastMessage(
				GroupAddress ga,
				Class<? extends Role> emitter,
				Class<? extends Role> receiver,
				Message message) {
			broadcastMessage(ga, emitter, receiver, message);
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AgentListener
	implements AgentLifeStateListener {

		/**
		 * Last state of the agent.
		 */
		public volatile AgentLifeState state;
		
		/**
		 * @param st
		 */
		public AgentListener(AgentLifeState st) {
			this.state = st;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void agentLifeChanged(AgentAddress agent, AgentLifeState state) {
			this.state = state;
		}
		
	}
	
}
