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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.KernelEvent.KernelEventType;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.channels.ChannelInteractableListener;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelTest extends TestCase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		Kernels.killAll();
		super.tearDown();
	}
	
	private static void assertNear(int expected, int current) {
		if (current!=expected && current!=(expected+1)) {
			fail("expecting: "+expected+"; actual: "+current); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @throws Exception 
	 */
	public static void testPause() throws Exception {
		int v1, v2, v3, v4;
		
		Kernel k = Kernels.create();
		AgentStub a1 = new AgentStub();
		AgentStub a2 = new AgentStub();
		AgentStub a3 = new AgentStub();
		AgentStub a4 = new AgentStub();
		
		k.submitHeavyAgent(a1);
		k.submitLightAgent(a2);
		k.submitHeavyAgent(a3);
		k.submitLightAgent(a4);
		
		assertNear(0, a1.value.get());
		assertNear(0, a2.value.get());
		assertNear(0, a3.value.get());
		assertNear(0, a4.value.get());
		
		k.launchDifferedExecutionAgents();
		
		Thread.sleep(1000);
		
		do {
			v1 = a1.value.get();
			v2 = a2.value.get();
			v3 = a3.value.get();
			v4 = a4.value.get();
		}
		while (v1<=0 || v2 <= 0 || v3 <= 0 || v4 <= 0);

		k.pause();
		
		v1 = a1.value.get();
		v2 = a2.value.get();
		v3 = a3.value.get();
		v4 = a4.value.get();
		
		assertTrue(v1>0);
		assertTrue(v2>0);
		assertTrue(v3>0);
		assertTrue(v4>0);

		Thread.sleep(2000);

		int v1b = a1.value.get();
		int v2b = a2.value.get();
		int v3b = a3.value.get();
		int v4b = a4.value.get();
		
		assertNear(v1, v1b);
		assertNear(v2, v2b);
		assertNear(v3, v3b);
		assertNear(v4, v4b);

		k.resume();

		Thread.sleep(1000);

		v1b = a1.value.get();
		v2b = a2.value.get();
		v3b = a3.value.get();
		v4b = a4.value.get();
		
		assertTrue(v1b>=v1);
		assertTrue(v2b>=v2);
		assertTrue(v3b>=v3);
		assertTrue(v4b>=v4);
	}
	
	private static void assertIsAlive(AgentStub a) throws Exception {
		assertNotNull(a);
		int v = a.value.get();
		int to = 0;
		while (v==a.value.get() && to<20) {
			Thread.sleep(500);
			++to;
		}
		if (v==a.value.get()) {
			fail("Expecting agent "+a+" is alive"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * @throws Exception
	 */
	public static void testSetHeavyAgent() throws Exception {
		Kernel k = Kernels.create();
		AgentStub a = new AgentStub();
		
		k.launchLightAgent(a);
		
		Thread.sleep(500);
		
		assertTrue(a.isLightAgent());
		
		assertTrue(a.setHeavyAgent());

		Thread.sleep(2000);

		assertTrue(a.isHeavyAgent());
		assertIsAlive(a);
	}
	
	/**
	 * @throws Exception
	 */
	public static void testSetLightAgent() throws Exception {
		Kernel k = Kernels.create();
		AgentStub a = new AgentStub();
		
		k.launchHeavyAgent(a);
		
		Thread.sleep(500);
		
		assertTrue(a.isHeavyAgent());
		
		a.setLightAgent();

		Thread.sleep(2000);

		assertTrue(a.isLightAgent());
		assertIsAlive(a);
	}
	
	/**
	 * @throws Exception
	 */
	public static void testChannelInteractableListener() throws Exception {
		KernelListenerStub kListener = new KernelListenerStub();
		ChannelListenerStub listener = new ChannelListenerStub();
		Kernel k = Kernels.create();
		k.getChannelManager().addChannelInteractableListener(listener);
		k.addKernelListener(kListener);
		listener.reset();
		
		AgentStub a1 = new AgentStub();
		AgentStub a2 = new AgentStub();
		AgentStub2 a3 = new AgentStub2();
		AgentStub2 a4 = new AgentStub2();
		
		List<Agent> tab = new ArrayList<Agent>();
		tab.add(a1);
		tab.add(a2);
		tab.add(a3);
		tab.add(a4);
		
		for(Agent a : tab) {
			k.submitLightAgent(a);
		}
		
		listener.assertNull();
		
		k.launchDifferedExecutionAgents();
		
		long endTime = System.currentTimeMillis() + 5000;

		while (!tab.isEmpty() && System.currentTimeMillis()<=endTime) {
			Iterator<Agent> iterator = tab.iterator();
			while (iterator.hasNext()) {
				Agent a = iterator.next();
				if (kListener.isAgentLaunched(a.getAddress()))
					iterator.remove();
			}
		}
		
		Thread.sleep(1000); // to be sure that the ChannelInteractable is invoked
		
		listener.assertLaunched(a3);
		listener.assertLaunched(a4);
		listener.assertNull();

		Thread.sleep(1000);
		
		a3.stop.set(true);
		
		Thread.sleep(2000);

		listener.assertKilled(a3);
		listener.assertNull();		
	}

	/**
	 * @throws Exception
	 */
	public static void testKernelListener() throws Exception {
		KernelListenerStub listener = new KernelListenerStub();
		Kernel k = Kernels.create();
		k.addKernelListener(listener);
		listener.assertNull();

		AgentStub a1 = new AgentStub();
		AgentStub a2 = new AgentStub();
		AgentStub2 a3 = new AgentStub2();
		AgentStub2 a4 = new AgentStub2();
		
		k.submitLightAgent(a1);
		k.submitLightAgent(a2);
		k.submitLightAgent(a3);
		k.submitLightAgent(a4);
		
		listener.assertNull();
		
		k.launchDifferedExecutionAgents();
		
		Thread.sleep(1000);
		
		listener.assertLaunchedAgent(k, a1.getAddress());
		listener.assertLaunchedAgent(k, a2.getAddress());
		listener.assertLaunchedAgent(k, a3.getAddress());
		listener.assertLaunchedAgent(k, a4.getAddress());
		listener.assertNull();

		Thread.sleep(1000);
		
		a3.stop.set(true);
		
		Thread.sleep(1000);

		listener.assertKilledAgent(k, a3.getAddress());
		listener.assertNull();		

		Kernels.killAll();

		Thread.sleep(2000);
		
		listener.assertKilledAgent(k, a1.getAddress());
		listener.assertKilledAgent(k, a2.getAddress());
		listener.assertKilledAgent(k, a4.getAddress());
		listener.assertKilledKernel(k, k.getAddress());
		listener.assertNull();
	}

	/**
	 * @throws Exception
	 */
	public static void testAgentLifeStageListener() throws Exception {
		AgentLifeListenerStub listener = new AgentLifeListenerStub();
		Kernel k = Kernels.create();
		k.addAgentLifeStateListener(listener);
		listener.assertNull();
		
		AgentStub a1 = new AgentStub();
		AgentStub a2 = new AgentStub();
		AgentStub2 a3 = new AgentStub2();
		AgentStub2 a4 = new AgentStub2();
		
		k.submitLightAgent(a1);
		k.submitLightAgent(a2);
		k.submitLightAgent(a3);
		k.submitLightAgent(a4);
		
		listener.assertNull();
		
		k.launchDifferedExecutionAgents();
		
		Thread.sleep(1000);
		
		listener.assertNull();

		Thread.sleep(1000);
		
		a3.stop.set(true);
		
		Thread.sleep(1000);

		listener.assertNull();		

		Kernels.killAll();

		Thread.sleep(2000);
		
		listener.assertLifeStateChange(k.getAddress(), AgentLifeState.BREAKING_DOWN);
		listener.assertLifeStateChange(k.getAddress(), AgentLifeState.DYING);
		listener.assertLifeStateChange(k.getAddress(), AgentLifeState.DIED);
		listener.assertNull();
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class AgentStub extends Agent {

		private static final long serialVersionUID = 4965506812126379370L;
		
		public final AtomicInteger value = new AtomicInteger(0);
		
		/**
		 */
		public AgentStub() {
			//
		}
		
		@Override
		public Status live() {
			this.value.incrementAndGet();
			return StatusFactory.ok(this);
		}
		
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class AgentStub2 extends Agent implements ChannelInteractable {

		private static final long serialVersionUID = 4965506812126379370L;
		
		public final AtomicInteger value = new AtomicInteger(0);
		public final AtomicBoolean stop = new AtomicBoolean(false);
		
		/**
		 */
		public AgentStub2() {
			//
		}
		
		@Override
		public Status live() {
			this.value.incrementAndGet();
			if (this.stop.get())
				killMe();
			return StatusFactory.ok(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<? extends Class<? extends Channel>> getSupportedChannels() {
			return Collections.emptySet();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <C extends Channel> C getChannel(Class<C> channelClass, Object... params) {
			return null;
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ChannelListenerStub implements ChannelInteractableListener {

		private List<ChannelInteractable> launched = new ArrayList<ChannelInteractable>();
		private List<ChannelInteractable> killed = new ArrayList<ChannelInteractable>();
		
		/**
		 */
		public ChannelListenerStub() {
			//
		}
		
		/** Reset the stub.
		 */
		public synchronized void reset() {
			this.launched.clear();
			this.killed.clear();
		}
		
		/**
		 */
		public synchronized void assertNull() {
			if (!this.launched.isEmpty()) {
				Assert.fail("unexpecting launched ChannelInteractable"); //$NON-NLS-1$
			}
			if (!this.killed.isEmpty()) {
				Assert.fail("unexpecting killed ChannelInteractable"); //$NON-NLS-1$
			}
		}
		
		/**
		 * @param launched
		 */
		public synchronized void assertLaunched(ChannelInteractable launched) {
			Iterator<ChannelInteractable> iterator = this.launched.iterator();
			ChannelInteractable c;
			while (iterator.hasNext()) {
				c = iterator.next();
				if (launched.getUUID().equals(c.getUUID())) {
					iterator.remove();
					return;
				}
			}
			Assert.fail("ChannelInteractable not found: "+launched); //$NON-NLS-1$
		}

		/**
		 * @param killed
		 */
		public synchronized void assertKilled(ChannelInteractable killed) {
			Iterator<ChannelInteractable> iterator = this.killed.iterator();
			ChannelInteractable c;
			while (iterator.hasNext()) {
				c = iterator.next();
				if (killed.getUUID().equals(c.getUUID())) {
					iterator.remove();
					return;
				}
			}
			Assert.fail("ChannelInteractable not found: "+killed); //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void channelIteractableLaunched(ChannelInteractable agent) {
			this.launched.add(agent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void channelIteractableKilled(ChannelInteractable agent) {
			this.killed.add(agent);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class KernelListenerStub implements KernelListener {

		private List<KernelEvent> launchedAgents = new ArrayList<KernelEvent>();
		private List<KernelEvent> killedAgents = new ArrayList<KernelEvent>();
		private List<KernelEvent> launchedKernels = new ArrayList<KernelEvent>();
		private List<KernelEvent> killedKernels = new ArrayList<KernelEvent>();
		
		/**
		 */
		public KernelListenerStub() {
			//
		}
		
		public synchronized boolean isAgentLaunched(AgentAddress adr) {
			for(KernelEvent evt : this.launchedAgents) {
				if (evt.getAgent().equals(adr)) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 */
		public synchronized void assertNull() {
			if (!this.launchedAgents.isEmpty()) {
				Assert.fail("unexpecting launched agent"); //$NON-NLS-1$
			}
			else if (!this.killedAgents.isEmpty()) {
				Assert.fail("unexpecting killed agent"); //$NON-NLS-1$
			}
			else if (!this.launchedKernels.isEmpty()) {
				Assert.fail("unexpecting launched kernel"); //$NON-NLS-1$
			}
			else if (!this.killedKernels.isEmpty()) {
				Assert.fail("unexpecting killed kernel"); //$NON-NLS-1$
			}
		}
		
		private static void assertKernelEvent(Collection<KernelEvent> list, Object source, KernelEventType type, AgentAddress address) {
			Iterator<KernelEvent> iterator = list.iterator();
			KernelEvent e;
			while (iterator.hasNext()) {
				e = iterator.next();
				if (e!=null
					&& e.getAgent().equals(address)
					&& e.getSource()==source
					&& e.getType()==type) {
					iterator.remove();
					return;
				}
			}
			Assert.fail("event not found"); //$NON-NLS-1$
		}
		
		/**
		 * @param k
		 * @param agent
		 */
		public synchronized void assertKilledAgent(Kernel k, AgentAddress agent) {
			assertKernelEvent(this.killedAgents,
					k,
					KernelEventType.AGENT_KILLING,
					agent);
		}
		
		/**
		 * @param k
		 * @param agent
		 */
		public synchronized void assertLaunchedAgent(Kernel k, AgentAddress agent) {
			assertKernelEvent(this.launchedAgents,
					k,
					KernelEventType.AGENT_LAUNCHING,
					agent);
		}

		/**
		 * @param k
		 * @param agent
		 */
		public synchronized void assertKilledKernel(Kernel k, AgentAddress agent) {
			assertKernelEvent(this.killedKernels,
					k,
					KernelEventType.AGENT_KILLING,
					agent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void agentLaunched(KernelEvent event) {
			this.launchedAgents.add(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void agentKilled(KernelEvent event) {
			this.killedAgents.add(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized boolean exceptionUncatched(Throwable error) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void kernelAgentLaunched(KernelEvent event) {
			this.launchedKernels.add(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void kernelAgentKilled(KernelEvent event) {
			this.killedKernels.add(event);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Pair<A,B> {
		
		/**
		 */
		public final A a;
		
		/**
		 */
		public final B b;

		/**
		 * @param a
		 * @param b
		 */
		public Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class AgentLifeListenerStub implements AgentLifeStateListener {

		private List<Pair<AgentAddress,AgentLifeState>> events = new ArrayList<Pair<AgentAddress,AgentLifeState>>();
		
		/**
		 */
		public AgentLifeListenerStub() {
			//
		}
		
		/**
		 */
		public void assertNull() {
			if (!this.events.isEmpty()) {
				Assert.fail("unexpecting agent life state change"); //$NON-NLS-1$
			}
		}
		
		/**
		 * @param address
		 * @param state
		 */
		public void assertLifeStateChange(AgentAddress address, AgentLifeState state) {
			Iterator<Pair<AgentAddress,AgentLifeState>> iterator = this.events.iterator();
			Pair<AgentAddress,AgentLifeState> e;
			while (iterator.hasNext()) {
				e = iterator.next();
				if (e!=null
					&& e.a.equals(address)
					&& e.b==state) {
					iterator.remove();
					return;
				}
			}
			Assert.fail("event not found"); //$NON-NLS-1$
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void agentLifeChanged(AgentAddress agent, AgentLifeState state) {
			this.events.add(new Pair<AgentAddress,AgentLifeState>(agent, state));
		}
		
	}

}
