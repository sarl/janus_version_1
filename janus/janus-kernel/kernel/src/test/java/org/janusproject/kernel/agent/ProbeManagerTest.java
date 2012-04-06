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

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.AgentProbe;
import org.janusproject.kernel.agent.KernelContext;
import org.janusproject.kernel.agent.ProbeManager;
import org.janusproject.kernel.probe.ProbeException;
import org.janusproject.kernel.probe.Watchable;
import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ProbeManagerTest extends TestCase {

	private KernelContext context;
	private ProbeManager manager;

	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		Kernels.shutdownNow();
		this.context = new KernelContext(new AgentAddressStub(), null, null);
		this.manager = new ProbeManager(this.context);
	}

	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.manager = null;
		this.context = null;
		Kernels.shutdownNow();
		super.tearDown();
	}

	/**
	 * @throws Exception
	 */
	public void testGetProbeValueStringObject() throws Exception {
		ProbeB p = new ProbeB(this.manager, new AgentAddressStub());
		try {
			this.manager.getProbeValue("notwatchable1", p); //$NON-NLS-1$
			fail("exception was expected"); //$NON-NLS-1$
		} catch (ProbeException _) {
			// Expected exception
		}

		assertEquals(2, this.manager.getProbeValue("watchable1", p)); //$NON-NLS-1$

		try {
			this.manager.getProbeValue("notwatchable2", p); //$NON-NLS-1$
			fail("exception was expected"); //$NON-NLS-1$
		} catch (ProbeException _) {
			// Expected exception
		}

		assertEquals(4, this.manager.getProbeValue("watchable2", p)); //$NON-NLS-1$

		try {
			this.manager.getProbeValue("something", p); //$NON-NLS-1$
			fail("exception was expected"); //$NON-NLS-1$
		} catch (ProbeException _) {
			// Expected exception
		}
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	class ProbeA extends AgentProbe {

		/**
		 * @param manager
		 * @param agent
		 */
		public ProbeA(ProbeManager manager, AgentAddress agent) {
			super(manager, agent);
		}

		/**
		 */
		public int notwatchable1 = 1;

		/**
		 */
		@Watchable
		public int watchable1 = 2;

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	class ProbeB extends ProbeA {

		/**
		 * @param manager
		 * @param agent
		 */
		public ProbeB(ProbeManager manager, AgentAddress agent) {
			super(manager, agent);
		}

		/**
		 */
		public int notwatchable2 = 3;

		/**
		 */
		@Watchable
		public int watchable2 = 4;

	}

}
