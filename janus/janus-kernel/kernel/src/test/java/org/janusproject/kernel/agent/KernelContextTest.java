/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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

import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.AgentRepository;
import org.janusproject.kernel.agent.KernelContext;
import org.janusproject.kernel.agent.ProbeManager;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelContextTest extends TestCase {

	private AgentAddress kernel;
	private KernelContext context;

	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		Kernels.shutdownNow();
		this.kernel = new AgentAddressStub();
		this.context = new KernelContext(this.kernel, null, null);
	}

	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.context = null;
		this.kernel = null;
		Kernels.shutdownNow();
		super.tearDown();
	}

	/**
	 */
	public void testGetKernelAgent() {
		assertSame(this.kernel, this.context.getKernelAgent());
	}

	/**
	 */
	public void testDestroy() {
		this.context.destroy();
	}

	/**
	 */
	public void testGetAgentExecutorService() {
		ExecutorService r;
		assertNotNull(r = this.context.getExecutorService());
		assertSame(r, this.context.getExecutorService());
	}

	/**
	 */
	public void testGetAgentRepository() {
		AgentRepository r;
		assertNotNull(r = this.context.getAgentRepository());
		assertSame(r, this.context.getAgentRepository());
	}

	/**
	 */
	public void testGetProbeManager() {
		ProbeManager m;
		assertNotNull(m = this.context.getProbeManager());
		assertSame(m, this.context.getProbeManager());
	}

}
