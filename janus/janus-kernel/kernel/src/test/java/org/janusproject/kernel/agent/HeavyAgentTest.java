/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelAdapter;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.StringMessage;

import junit.framework.TestCase;

/** This set of test is dedicated to heavy agents only.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class HeavyAgentTest extends TestCase {

	/** Sleeping duration to wait for test.
	 */
	private static final int SLEEP_DURATION = 2000;
	
	private static final long TIMEOUT = 5000;

	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.SEVERE);
		Kernels.shutdownNow();
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		Kernels.shutdownNow();
		super.tearDown();
	}
	
	/**
	 * @throws Throwable
	 */
	public static void testMessageSending() throws Throwable {
		SendingAgent sAgent = new SendingAgent();
		ReceivingAgent rAgent = new ReceivingAgent();
		
		sAgent.getAddress().setName("Sending Agent"); //$NON-NLS-1$
		rAgent.getAddress().setName("Receiving Agent"); //$NON-NLS-1$
		
		Kernel k = Kernels.get();
		
		KernelEventListener listener = new KernelEventListener();
		k.addKernelListener(listener);
		
		k.launchHeavyAgent(rAgent);
		k.launchHeavyAgent(sAgent, rAgent.getAddress());
		
		Thread.sleep(SLEEP_DURATION);
		
		sAgent.stopTest();
		rAgent.stopTest();
		
		sAgent.waitUntilTermination();
		rAgent.waitUntilTermination();
		k.waitUntilTermination();
		
		listener.throwsErrors();
		
		assertTrue(listener.errors.isEmpty());
		
		assertEquals(sAgent.getSentMessages(), rAgent.getReceivedMessages());
	}

	/**
	 * @throws Throwable
	 */
	public static void testReleaseAllRoles() throws Throwable {
		PlayerAgent agent = new PlayerAgent();
		Kernel k = Kernels.get();

		KernelEventListener listener = new KernelEventListener();
		k.addKernelListener(listener);

		k.launchLightAgent(agent);
		k.waitUntilTermination(TIMEOUT);
		
		listener.throwsErrors();		
	}

	/** This set of test is dedicated to light agents only.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	@AgentActivationPrototype
	private static class PlayerAgent extends Agent {

		private static final long serialVersionUID = 133767903021367950L;
		
		private boolean run = false;

		/**
		 */
		public PlayerAgent() {
			super(true);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... parameters) {
			GroupAddress grp = getOrCreateGroup(Organization1Stub.class);
			if (requestRole(RoleStub.class, grp)==null) {
				throw new RuntimeException("unable to request RoleStub"); //$NON-NLS-1$
			}
			if (requestRole(Role3Stub.class, grp)==null) {
				throw new RuntimeException("unable to request Role3Stub"); //$NON-NLS-1$
			}
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status live() {
			Status s = super.live();
			if (s!=null && s.isFailure()) {
				throw new RuntimeException(s.toString());
			}
			if (!this.run) {
				leaveAllRoles();
			}
			else {
				throw new RuntimeException("I should be killed"); //$NON-NLS-1$
			}
			return null;
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@AgentActivationPrototype(
			fixedParameters = {AgentAddress.class}
	)
	private static class SendingAgent extends Agent {
		
		private static final long serialVersionUID = 8604814095333750322L;
		
		private final AtomicBoolean stop = new AtomicBoolean(false);
		private final AtomicInteger nbMessages = new AtomicInteger(0);
		private AgentAddress receiver = null;
		
		/**
		 */
		public SendingAgent() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... parameters) {
			this.receiver = (AgentAddress)parameters[0];
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status live() {
			if (this.stop.get()) {
				killMe();
			}
			else {
				sendMessage(new StringMessage("Message #"+this.nbMessages), this.receiver); //$NON-NLS-1$
				this.nbMessages.incrementAndGet();
			}
			return null;
		}
		
		/**
		 */
		public void stopTest() {
			this.stop.set(true);
		}
		
		/**
		 * @return the number of messages that has been sent.
		 */
		public int getSentMessages() {
			return this.nbMessages.get();
		}
		
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ReceivingAgent extends Agent {

		private static final long serialVersionUID = -3413468714219936152L;
		
		private final AtomicBoolean stop = new AtomicBoolean(false);
		private final AtomicInteger nbMessages = new AtomicInteger(0);
		
		/**
		 */
		public ReceivingAgent() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status live() {
			Message m = getMessage();
			if (m!=null) {
				this.nbMessages.incrementAndGet();
			}
			if (this.stop.get() && m==null) {
				killMe();
			}
			return null;
		}
		
		/**
		 */
		public void stopTest() {
			this.stop.set(true);
		}
		
		/**
		 * @return the number of messages that has been received.
		 */
		public int getReceivedMessages() {
			return this.nbMessages.get();
		}

	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class KernelEventListener extends KernelAdapter {
		
		/** Errors.
		 */
		public final List<Throwable> errors = new LinkedList<Throwable>();
		
		/**
		 */
		public KernelEventListener() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean exceptionUncatched(Throwable error) {
			this.errors.add(error);
			return true;
		}
		
		/** Print the uncautched errors. 
		 * 
		 * @throws Throwable
		 */
		public void throwsErrors() throws Throwable {
			for(Throwable e: this.errors) {
				throw e;
			}
		}
		
	}

}
