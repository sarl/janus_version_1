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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivator;
import org.janusproject.kernel.agent.KernelAgent;
import org.janusproject.kernel.schedule.Activable;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentActivatorTest extends TestCase {

	private KernelAgent kernel;
	private Agent a1;
	private Agent a2;
	private AgentActivator activator;
	private List<Agent> stubs;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		Kernels.shutdownNow();
		this.kernel = new KernelAgent(new AgentActivator(), true, null, null);
		this.activator = new AgentActivator();
		this.a1 = new Agent();
		this.a2 = new Agent();
		this.stubs = Arrays.asList(this.a1, this.a2);
		this.kernel.launchLightAgent(this.a1, this.activator);
		this.kernel.launchLightAgent(this.a2, this.activator);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.activator = null;
		this.a1 = null;
		this.a2 = null;
		this.stubs = null;
		this.kernel.killMe();
		this.kernel = null;
		Kernels.shutdownNow();
		super.tearDown();
	}
	
	private Agent[] toArray() {
		Agent[] tab = new Agent[this.activator.size()];
		this.activator.toArray(tab);
		return tab;
	}

	private static void assertEquals(Collection<?> c1, Agent[] c2) {
		if (c1!=null && c2!=null) {
			try {
				ArrayList<Object> obj = new ArrayList<Object>(c1);
				boolean failure = false;
				Object o1;
				for(int i=0; i<c2.length&&!failure; ++i) {
					o1 = c2[i];
					failure = !obj.remove(o1);
				}
				if (!failure && obj.isEmpty()) return;
			}
			catch(Throwable _) {
				//
			}
		}
		fail("collections are not equal. Expected: " //$NON-NLS-1$
				+((c1==null)?null:c1.toString())
				+"; Actual: " //$NON-NLS-1$
				+((c2==null)?null:c2.toString()));
	}

	/**
	 */
	public void testCanActivateClass() {
		assertTrue(this.activator.canActivate(Agent.class));
		assertFalse(this.activator.canActivate(Activable.class));
	}

	/**
	 */
	public void testInitBehaviourDestroy() {
		assertFalse(this.a1.isAlive());
		assertFalse(this.a2.isAlive());

		this.activator.sync();
		assertNotNull(this.activator.activate());

		assertTrue(this.a1.isAlive());
		assertTrue(this.a2.isAlive());

		this.activator.sync();
		assertNotNull(this.activator.live());
		
		assertTrue(this.a1.isAlive());
		assertTrue(this.a2.isAlive());

		this.activator.sync();
		assertNotNull(this.activator.end());
		
		assertFalse(this.a1.isAlive());
		assertFalse(this.a2.isAlive());
	}

	/** 
	 */
	public void testAddActivableObject() {
		Agent a3 = new Agent();
		this.activator.sync();
		this.activator.activate();
		assertEquals(this.stubs, toArray());
		this.activator.addAgent(a3);
		this.activator.sync();
		assertEquals(Arrays.asList(this.a1, this.a2, a3),
				toArray());
	}

	/** 
	 */
	public void testAddAllActivableObjects() {
		Agent a3 = new Agent();
		Agent a4 = new Agent();
		this.activator.sync();
		this.activator.activate();
		assertEquals(this.stubs, toArray());
		this.activator.addAgent(a3);
		this.activator.addAgent(a4);
		this.activator.sync();
		assertEquals(Arrays.asList(this.a1, this.a2, a3, a4),
				toArray());
	}

	/** 
	 */
	public void testGetAllActivableObjects() {
		this.activator.sync();
		assertEquals(this.stubs, toArray());
	}

	/** 
	 */
	public void testRemoveActivableObject() {
		this.activator.removeAgent(this.a2);
		this.activator.sync();
		assertEquals(
				Collections.singleton(this.a1),
				toArray());
		this.activator.removeAgent(this.a1);
		this.activator.sync();
		assertEquals(
				Collections.emptyList(),
				toArray());
	}

	/**
	 */
	public void testHasActivable() {
		assertTrue(this.activator.hasActivable());

		this.activator.removeAllAgents();
		assertFalse(this.activator.hasActivable());
		
		this.activator.addAgent(this.a1);
		assertTrue(this.activator.hasActivable());
		
		this.activator.removeAllAgents();
		assertFalse(this.activator.hasActivable());
		
		this.activator.activate();
		assertFalse(this.activator.hasActivable());
		
		this.activator.addAgent(this.a2);
		assertTrue(this.activator.hasActivable());
		
		this.activator.removeAllAgents();
		assertFalse(this.activator.hasActivable());
	}

	/**
	 */
	public void testIsUsed() {
		assertTrue(this.activator.isUsed());

		this.activator.removeAllAgents();
		assertTrue(this.activator.isUsed());
		
		this.activator.addAgent(this.a1);
		assertTrue(this.activator.isUsed());
		
		this.activator.removeAllAgents();
		assertTrue(this.activator.isUsed());
		
		this.activator.activate();
		assertTrue(this.activator.isUsed());
		
		this.activator.addAgent(this.a2);
		assertTrue(this.activator.isUsed());
		
		this.activator.removeAllAgents();
		assertTrue(this.activator.isUsed());
	}

}
