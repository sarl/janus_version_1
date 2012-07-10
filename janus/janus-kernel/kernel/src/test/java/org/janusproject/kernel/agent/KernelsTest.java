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
import java.util.Arrays;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.AgentLifeState;
import org.janusproject.kernel.agent.KernelAgent;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelsTest extends TestCase {

	private static final long TIMEOUT = 10000;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
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
	@Override
	public void tearDown() throws Exception {
		Kernels.shutdownNow();
		super.tearDown();
	}
	
	private static void assertContains(Iterator<Kernel> iterator, AgentAddress... objects) {
		ArrayList<AgentAddress> objs = new ArrayList<AgentAddress>(Arrays.asList(objects));
		assertNotNull(iterator);
		while(iterator.hasNext()) {
			Object obj = iterator.next();
			assertTrue(objs.remove(obj));
		}
		assertTrue(objs.isEmpty());		
	}
	
	/**
	 */
	public static void testGetKernelCount() {
		assertEquals(0, Kernels.getKernelCount());
	}
	
	/**
	 * @throws InterruptedException 
	 */
	public void testCreateBoolean_true() throws InterruptedException {
		long startTime;
		boolean timeout;
		ListenerStub listener = new ListenerStub();
		
		assertEquals(0, Kernels.getKernelCount());
		
		Kernel k1 = Kernels.create(true, listener);
		assertEquals(1, Kernels.getKernelCount());
		
		Kernel k2 = Kernels.create(true, listener);
		assertEquals(2, Kernels.getKernelCount());
		
		assertNotNull(k1);
		assertNotNull(k2);
		assertNotSame(k1, k2);

		startTime = System.currentTimeMillis();
		timeout = false;
		while ((listener.states.get(k1.getAddress())!=AgentLifeState.ALIVE
				||listener.states.get(k2.getAddress())!=AgentLifeState.ALIVE)
				&& !timeout) {
			Thread.yield();
			timeout = ((System.currentTimeMillis()-startTime)>TIMEOUT);
		}
		assertFalse(timeout);

		assertTrue(k1.isAlive());
		assertTrue(k2.isAlive());
		assertEquals(2, Kernels.getKernelCount());
		
		assertTrue(k1.kill().isSuccess());
		assertTrue(k2.kill().isSuccess());

		startTime = System.currentTimeMillis();
		timeout = false;
		while ((listener.states.get(k1.getAddress())!=AgentLifeState.DIED
				||listener.states.get(k2.getAddress())!=AgentLifeState.DIED)
				&& !timeout) {
			Thread.yield();
			timeout = ((System.currentTimeMillis()-startTime)>TIMEOUT);
		}
		assertFalse(timeout);

		assertFalse(k1.isAlive());
		assertFalse(k2.isAlive());
	}

	/**
	 * @throws InterruptedException 
	 */
	public void testCreateBoolean_false() throws InterruptedException {
		long startTime;
		boolean timeout;
		ListenerStub listener = new ListenerStub();
		
		assertEquals(0, Kernels.getKernelCount());
		
		Kernel k1 = Kernels.create(false, listener);
		assertEquals(1, Kernels.getKernelCount());
		
		Kernel k2 = Kernels.create(false, listener);
		assertEquals(2, Kernels.getKernelCount());
		
		assertNotNull(k1);
		assertNotNull(k2);
		assertNotSame(k1, k2);

		startTime = System.currentTimeMillis();
		timeout = false;
		while ((listener.states.get(k1.getAddress())!=AgentLifeState.ALIVE
				||listener.states.get(k2.getAddress())!=AgentLifeState.ALIVE)
				&& !timeout) {
			Thread.yield();
			timeout = ((System.currentTimeMillis()-startTime)>TIMEOUT);
		}
		assertFalse(timeout);

		assertTrue(k1.isAlive());
		assertTrue(k2.isAlive());
		assertEquals(2, Kernels.getKernelCount());
		
		assertTrue(k1.kill().isSuccess());
		assertTrue(k2.kill().isSuccess());

		startTime = System.currentTimeMillis();
		timeout = false;
		while ((listener.states.get(k1.getAddress())!=AgentLifeState.DIED
				||listener.states.get(k2.getAddress())!=AgentLifeState.DIED)
				&& !timeout) {
			Thread.yield();
			timeout = ((System.currentTimeMillis()-startTime)>TIMEOUT);
		}
		assertFalse(timeout);

		assertFalse(k1.isAlive());
		assertFalse(k2.isAlive());
	}

	/**
	 * @throws InterruptedException 
	 */
	public void testKillAll() throws InterruptedException {
		long startTime;
		boolean timeout;
		ListenerStub listener = new ListenerStub();
		
		assertEquals(0, Kernels.getKernelCount());
		
		Kernel k1 = Kernels.create(false, listener);
		assertEquals(1, Kernels.getKernelCount());
		
		Kernel k2 = Kernels.create(false, listener);
		assertEquals(2, Kernels.getKernelCount());
		
		assertNotNull(k1);
		assertNotNull(k2);
		assertNotSame(k1, k2);

		startTime = System.currentTimeMillis();
		timeout = false;
		while ((listener.states.get(k1.getAddress())!=AgentLifeState.ALIVE
				||listener.states.get(k2.getAddress())!=AgentLifeState.ALIVE)
				&& !timeout) {
			Thread.yield();
			timeout = ((System.currentTimeMillis()-startTime)>TIMEOUT);
		}
		assertFalse(timeout);

		assertTrue(k1.isAlive());
		assertTrue(k2.isAlive());
		assertEquals(2, Kernels.getKernelCount());
		
		Kernels.killAll();

		startTime = System.currentTimeMillis();
		timeout = false;
		while ((listener.states.get(k1.getAddress())!=AgentLifeState.DIED
				||listener.states.get(k2.getAddress())!=AgentLifeState.DIED)
				&& !timeout) {
			Thread.yield();
			timeout = ((System.currentTimeMillis()-startTime)>TIMEOUT);
		}
		assertFalse(timeout);

		assertFalse(k1.isAlive());
		assertFalse(k2.isAlive());
	}

	/**
	 * @throws Exception
	 */
	public void testContainsAgentAddress() throws Exception {
		KernelAgent h = new KernelAgent(new AgentActivator(), false, null, null);
		
		AgentListener l = new AgentListener(h.getState());
		h.addAgentLifeStateListener(l);
		assertAlive(l);
		
		assertTrue(Kernels.contains(h.getAddress()));
		
		h.killMe();
		
		assertDead(l);
		assertFalse(Kernels.contains(h.getAddress()));

		h.removeAgentLifeStateListener(l);
	}

	/**
	 */
	public static void testGetAgentAddress() {
		KernelAgent h = new KernelAgent(new AgentActivator(), false, null, null);
		assertSame(h.toKernel(), Kernels.get(h.getAddress()));
		h.killMe();
	}

	/**
	 * @throws Exception
	 */
	public void testIterator() throws Exception {
		Iterator<Kernel> iterator;

		iterator = Kernels.iterator();
		assertFalse(iterator.hasNext());

		AgentListener l1 = new AgentListener(AgentLifeState.UNBORN);
		KernelAgent h1 = new KernelAgent(new AgentActivator(), false, null, l1);
		assertAlive(l1);
		
		iterator = Kernels.iterator();
		assertContains(iterator, h1.getAddress());
		
		AgentListener l2 = new AgentListener(AgentLifeState.UNBORN);
		KernelAgent h2 = new KernelAgent(new AgentActivator(), false, null, l2);
		assertAlive(l2);

		iterator = Kernels.iterator();
		assertContains(iterator, h1.getAddress(), h2.getAddress());

		h1.killMe();
		h2.killMe();
		
		assertDead(l1);
		assertDead(l2);
		
		iterator = Kernels.iterator();
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testDefaultKernelType() {
		Kernels.setPreferredKernelFactory(new KernelAgentFactoryStub());
		Kernel k = Kernels.create(true);
		assertNotNull(k);
		assertTrue(k.kill().isSuccess());
		Kernels.setPreferredKernelFactory(null);
	} 

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class KernelAgentFactoryStub implements KernelAgentFactory {
		
		/**
		 */
		public KernelAgentFactoryStub() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public KernelAgent newInstance(
				Boolean commitSuicide,
				AgentActivator activator,
				EventListener startUpListener,
				String applicationName) throws Exception {
			return new KernelAgent(activator, commitSuicide, null, startUpListener, applicationName);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class ListenerStub implements AgentLifeStateListener {

		/**
		 */
		public final Map<AgentAddress,AgentLifeState> states = new ConcurrentHashMap<AgentAddress,AgentLifeState>();
		
		/**
		 */
		public ListenerStub() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void agentLifeChanged(AgentAddress agent, AgentLifeState state) {
			this.states.put(agent,state);
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
