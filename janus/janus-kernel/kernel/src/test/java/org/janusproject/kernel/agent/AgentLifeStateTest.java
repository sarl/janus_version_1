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

import java.util.logging.Level;

import org.janusproject.kernel.agent.AgentLifeState;
import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * This enumeration lists the different states of an
 * autonomous entity.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentLifeStateTest extends TestCase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		Kernels.shutdownNow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		Kernels.shutdownNow();
		super.tearDown();
	}
	
	/**
	 */
	public static void testNext() {
		assertSame(AgentLifeState.BORN, AgentLifeState.UNBORN.next());
		assertSame(AgentLifeState.ALIVE, AgentLifeState.BORN.next());
		assertSame(AgentLifeState.DYING, AgentLifeState.ALIVE.next());
		assertSame(AgentLifeState.BREAKING_DOWN, AgentLifeState.DYING.next());
		assertSame(AgentLifeState.DIED, AgentLifeState.BREAKING_DOWN.next());
		assertSame(AgentLifeState.DIED, AgentLifeState.DIED.next());
	}
	
	/**
	 */
	public static void testPrevious() {
		assertSame(AgentLifeState.UNBORN, AgentLifeState.UNBORN.previous());
		assertSame(AgentLifeState.UNBORN, AgentLifeState.BORN.previous());
		assertSame(AgentLifeState.BORN, AgentLifeState.ALIVE.previous());
		assertSame(AgentLifeState.ALIVE, AgentLifeState.DYING.previous());
		assertSame(AgentLifeState.DYING, AgentLifeState.BREAKING_DOWN.previous());
		assertSame(AgentLifeState.BREAKING_DOWN, AgentLifeState.DIED.previous());
	}
	
	/**
	 */
	public static void testIsAlive() {
		assertFalse(AgentLifeState.UNBORN.isAlive());
		assertFalse(AgentLifeState.BORN.isAlive());
		assertTrue(AgentLifeState.ALIVE.isAlive());
		assertTrue(AgentLifeState.DYING.isAlive());
		assertFalse(AgentLifeState.BREAKING_DOWN.isAlive());
		assertFalse(AgentLifeState.DIED.isAlive());
	}

	/**
	 */
	public static void testIsLifless() {
		assertTrue(AgentLifeState.UNBORN.isLifeless());
		assertTrue(AgentLifeState.BORN.isLifeless());
		assertFalse(AgentLifeState.ALIVE.isLifeless());
		assertFalse(AgentLifeState.DYING.isLifeless());
		assertTrue(AgentLifeState.BREAKING_DOWN.isLifeless());
		assertTrue(AgentLifeState.DIED.isLifeless());
	}

	/**
	 */
	public static void testIsPrenatal() {
		assertTrue(AgentLifeState.UNBORN.isPrenatal());
		assertTrue(AgentLifeState.BORN.isPrenatal());
		assertFalse(AgentLifeState.ALIVE.isPrenatal());
		assertFalse(AgentLifeState.DYING.isPrenatal());
		assertFalse(AgentLifeState.BREAKING_DOWN.isPrenatal());
		assertFalse(AgentLifeState.DIED.isPrenatal());
	}

	/**
	 */
	public static void testIsMortuary() {
		assertFalse(AgentLifeState.UNBORN.isMortuary());
		assertFalse(AgentLifeState.BORN.isMortuary());
		assertFalse(AgentLifeState.ALIVE.isMortuary());
		assertFalse(AgentLifeState.DYING.isMortuary());
		assertTrue(AgentLifeState.BREAKING_DOWN.isMortuary());
		assertTrue(AgentLifeState.DIED.isMortuary());
	}

	/**
	 */
	public static void testIsAliveLifless() {
		for(AgentLifeState state : AgentLifeState.values()) {
			assertEquals(!state.isAlive(), state.isLifeless());
			assertEquals(!state.isLifeless(), state.isAlive());
		}
	}

}
