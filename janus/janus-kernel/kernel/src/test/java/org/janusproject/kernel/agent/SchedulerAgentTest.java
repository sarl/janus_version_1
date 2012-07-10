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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.agent.AgentActivator;
import org.janusproject.kernel.agent.KernelAgent;
import org.janusproject.kernel.agent.SchedulerAgent;
import org.janusproject.kernel.schedule.DefaultScheduler;
import org.janusproject.kernel.schedule.Scheduler;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SchedulerAgentTest extends TestCase {

	private KernelAgent kernel;
	private SchedulerAgent<AgentActivator> agent;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		Kernels.shutdownNow();
		this.kernel = new KernelAgent(new AgentActivator(), true, null, null);
		this.agent = new SchedulerAgent<AgentActivator>(
				new DefaultScheduler<AgentActivator>(AgentActivator.class));
		this.agent.kernel = new WeakReference<KernelAgent>(this.kernel);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.kernel.killMe();
		this.kernel = null;
		this.agent = null;
		Kernels.shutdownNow();
		super.tearDown();
	}
	
	private static void assertEquals(Collection<?> expected, Collection<?> actual) {
		if (expected==actual) return;
		if (expected!=null && actual!=null && expected.size()==actual.size()) {
			try {
				ArrayList<Object> obj = new ArrayList<Object>(actual);
				Iterator<?> iterator = expected.iterator();
				boolean failure = false;
				Object o1;
				while (iterator.hasNext() && !failure) {
					o1 = iterator.next();
					failure = !obj.remove(o1);
				}
				if (!failure && obj.isEmpty()) return;
			}
			catch(Throwable _) {
				//
			}
		}
		fail("collections are not equal. Expected: " //$NON-NLS-1$
				+((expected==null)?null:expected.toString())
				+"; Actual: " //$NON-NLS-1$
				+((actual==null)?null:actual.toString()));
	}

	/**
	 */
	public void testGetActivator() {
		Scheduler<AgentActivator> s;
		assertNotNull(s = this.agent.getActivator());
		assertSame(s, this.agent.getActivator());
	}
	
	/**
	 */
	public void testGetInitializationParameters() {
		this.agent.proceedPrivateInitialization(1, 2, 3);
		assertEquals(
				Arrays.asList(1, 2, 3),
				Arrays.asList(this.agent.getInitializationParameters()));
	}
	
}
