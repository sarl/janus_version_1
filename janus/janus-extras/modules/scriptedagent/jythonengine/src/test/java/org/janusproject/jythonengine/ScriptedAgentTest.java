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
package org.janusproject.jythonengine;

import java.io.StringWriter;
import java.net.URL;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.arakhne.afc.vmutil.Resources;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.agent.KernelTestUtil;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.status.Status;

/**
 * @author $Author: sgalland$
 */
public class ScriptedAgentTest extends TestCase {

	private static URL AGENT1_SCRIPT = Resources.getResource(ScriptedAgentTest.class, "agent1.py"); //$NON-NLS-1$
	private static URL AGENT2_SCRIPT = Resources.getResource(ScriptedAgentTest.class, "agent2.py"); //$NON-NLS-1$
	private static URL THIS_SCRIPT = Resources.getResource(ScriptedAgentTest.class, "this.py"); //$NON-NLS-1$
	
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
	public static void testLiveOnly() throws Exception {
		Kernel k = Kernels.get();
		
		ScriptedTestingAgent agent = new ScriptedTestingAgent(AGENT1_SCRIPT);
		
		k.launchHeavyAgent(agent, 1, "parameter2"); //$NON-NLS-1$
		
		k.waitUntilTermination();
		
		assertFalse(agent.scriptedActivateExecuted);
		assertTrue(agent.scriptedLiveExecuted);
		assertTrue(agent.scriptedKilledExecuted);
		assertFalse(agent.scriptedEndExecuted);
	}
			
	/**
	 * @throws Exception
	 */
	public static void testActivateLiveEnd_InvalidActivateParameter() throws Exception {
		Kernel k = Kernels.get();
		
		ScriptedTestingAgent agent = new ScriptedTestingAgent(AGENT2_SCRIPT);
		
		k.launchLightAgent(agent, 1, "parameter2"); //$NON-NLS-1$
		
		k.waitUntilTermination();
		
		assertFalse(agent.scriptedActivateExecuted);
		assertFalse(agent.scriptedLiveExecuted);
		assertFalse(agent.scriptedKilledExecuted);
		assertFalse(agent.scriptedEndExecuted);
	}

	/**
	 * @throws Exception
	 */
	public static void testActivateLiveEnd_Ok() throws Exception {
		Kernel k = Kernels.get();
		
		ScriptedTestingAgent agent = new ScriptedTestingAgent(AGENT2_SCRIPT);
		
		k.launchLightAgent(agent);
		
		k.waitUntilTermination();
		
		assertTrue(agent.scriptedActivateExecuted);
		assertTrue(agent.scriptedLiveExecuted);
		assertTrue(agent.scriptedKilledExecuted);
		assertTrue(agent.scriptedEndExecuted);
	}

	/**
	 * @throws Exception
	 */
	public static void testInstances() throws Exception {
		// Create the agent and bind it to the kernel, but no launch
		ScriptedTestingAgent agent = new ScriptedTestingAgent(THIS_SCRIPT);
		KernelTestUtil.bind(agent);
		
		// Force activation to load the script
		Status s = agent.activate();
		assertTrue(s==null || s.isSuccess());
		
		// Run the live
		String v = agent.liveAsInteractiveInterpreter();
		
		// Assertion
		assertTrue(Pattern.matches(
				"^self=\n\\Q" //$NON-NLS-1$
				+"None" //$NON-NLS-1$
				+"\\E\n; agent=\n\\Q" //$NON-NLS-1$
				+agent.toString()
				+"\\E\n$", //$NON-NLS-1$
				v));
	}

	/**
	 * @author $Author: sgalland$
	 */
	@AgentActivationPrototype(variableParameters=Object.class)
	public static class ScriptedTestingAgent extends JythonAgent {

		private static final long serialVersionUID = -8783235912158015759L;
		
		/**
		 */
		public volatile boolean scriptedActivateExecuted = false;
		/**
		 */
		public volatile boolean scriptedLiveExecuted = false;
		/**
		 */
		public volatile boolean scriptedKilledExecuted = false;
		/**
		 */
		public volatile boolean scriptedEndExecuted = false;
		
		/**
		 * @param url
		 */
		public ScriptedTestingAgent(URL url) {
			super(url);
		}
		
		/** Run {@link #live()} as interactive interpreter.
		 * 
		 * @return the output
		 */
		public String liveAsInteractiveInterpreter() {
			StringWriter buffer = new StringWriter();
			setScriptStandardOutput(buffer);
			setScriptStandardError(buffer);
			live();
			return buffer.toString();
		}
		
	}
	
}
