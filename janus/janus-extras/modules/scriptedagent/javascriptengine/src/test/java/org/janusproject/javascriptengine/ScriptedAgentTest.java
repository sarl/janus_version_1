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
package org.janusproject.javascriptengine;

import java.net.URL;

import junit.framework.TestCase;

import org.arakhne.afc.vmutil.Resources;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 */
public class ScriptedAgentTest extends TestCase {

	private static URL AGENT1_SCRIPT = Resources.getResource(ScriptedAgentTest.class, "agent1.js"); //$NON-NLS-1$
	private static URL AGENT2_SCRIPT = Resources.getResource(ScriptedAgentTest.class, "agent2.js"); //$NON-NLS-1$
	
	/**
	 * @throws java.lang.Exception
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setLoggingEnable(false);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		Kernels.killAll();
		super.tearDown();
	}
	
	/**
	 * @throws Exception
	 */
	@SuppressWarnings("synthetic-access")
	public static void testLiveOnly() throws Exception {
		Kernel k = Kernels.create();
		
		ScriptedTestingAgent agent = new ScriptedTestingAgent(AGENT1_SCRIPT);
		
		k.launchLightAgent(agent, "AgLiveOnly", 1, "parameter2"); //$NON-NLS-1$ //$NON-NLS-2$
		
		k.waitUntilTermination();
		
		assertFalse(agent.scriptedActivateExecuted);
		assertTrue(agent.scriptedLiveExecuted);
		assertTrue(agent.scriptedKilledExecuted);
		assertFalse(agent.scriptedEndExecuted);
	}
			
	/**
	 * @throws Exception
	 */
	@SuppressWarnings("synthetic-access")
	public static void testActivateLiveEnd_Ok() throws Exception {
		Kernel k = Kernels.get();
		
		ScriptedTestingAgent agent = new ScriptedTestingAgent(AGENT2_SCRIPT);
		
		k.launchLightAgent(agent, "AgLiveEndOk"); //$NON-NLS-1$
		
		k.waitUntilTermination();
		
		assertTrue(agent.scriptedActivateExecuted);
		assertTrue(agent.scriptedLiveExecuted);
		assertTrue(agent.scriptedKilledExecuted);
		assertTrue(agent.scriptedEndExecuted);
	}

	/**
	 * @author $Author: sgalland$
	 */
	@AgentActivationPrototype(variableParameters=Object.class)
	public static class ScriptedTestingAgent extends JavascriptAgent {

		private static final long serialVersionUID = 6588116598418914793L;
		
		private volatile boolean scriptedActivateExecuted = false;
		private volatile boolean scriptedLiveExecuted = false;
		private volatile boolean scriptedKilledExecuted = false;
		private volatile boolean scriptedEndExecuted = false;
		
		/**
		 * @param url
		 */
		public ScriptedTestingAgent(URL url) {
			super(url);
		}
				
	}
	
}
