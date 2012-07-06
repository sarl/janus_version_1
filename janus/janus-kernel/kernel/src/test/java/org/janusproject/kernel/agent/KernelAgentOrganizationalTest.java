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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelAgentOrganizationalTest extends TestCase {

	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setHandlerType(HandlerStub.class);
		LoggerUtil.setGlobalLevel(Level.WARNING);
		LoggerUtil.setLoggingEnable(true);
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
	
	private static HandlerStub getHandler(Logger logger) {
		for(Handler h : logger.getHandlers()) {
			if (h instanceof HandlerStub) {
				HandlerStub hs = (HandlerStub)h;
				return hs;
			}
		}
		throw new IllegalStateException("HandlerStub instance not found"); //$NON-NLS-1$
	}

	/**
	 */
	public static void testProtectedOrganizationConstructor() {
		AgentStub agent = new AgentStub(0);
		
		Kernel k = Kernels.get();
		
		HandlerStub kh = getHandler(k.getLogger());
		kh.resetLogged();
		agent.resetTestTermination();		
		
		k.launchLightAgent(agent);
		
		agent.waitForTestTermination();
		kh.assertLogged();
		HandlerStub ah = getHandler(agent.getLogger());
		assertNotNull(ah);
		ah.assertNotLogged();
	}
		
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@AgentActivationPrototype
	private static class AgentStub extends Agent {

		private static final long serialVersionUID = 8258773717125151191L;

		private final int failureState;
		private final AtomicBoolean terminate = new AtomicBoolean(false);
		
		public AgentStub(int failureState) {
			this.failureState = failureState;
		}
		
		/**
		 */
		public void resetTestTermination() {
			this.terminate.set(false);
		}

		/**
		 */
		public void waitForTestTermination() {
			while (!this.terminate.get()) {
				Thread.yield();
			}
			try {
				Thread.sleep(2000);
			}
			catch(Throwable _) {
				//
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... params) {
			if (this.failureState==0) {
				try {
					@SuppressWarnings("synthetic-access")
					HandlerStub ah = getHandler(getLogger());
					ah.resetLogged();
					getOrCreateGroup(Organization2Stub.class);
					fail("expecting error on the constructor access for Organization2Stub"); //$NON-NLS-1$
				}
				finally {
					this.terminate.set(true);
				}
			}
			return StatusFactory.ok(this);
		}
				
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class HandlerStub extends Handler {

		private final StringBuilder logged = new StringBuilder();
		
		/**
		 */
		public HandlerStub() {
			//
		}

		/**
		 */
		public void resetLogged() {
			this.logged.setLength(0);
		}

		/**
		 */
		public void assertLogged() {
			if (this.logged.length()<=0) {
				fail("a log message is expected"); //$NON-NLS-1$
			}
		}

		/**
		 */
		public void assertNotLogged() {
			if (this.logged.length()>0) {
				fail("unexpecting log message: "+this.logged.toString()); //$NON-NLS-1$
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void publish(LogRecord record) {
			this.logged.append(record.getLoggerName());
			this.logged.append(record.getMessage());
			this.logged.append(record.getThrown());
			this.logged.append("\n#######################################\n"); //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void flush() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws SecurityException {
			//
		}
		
	}
	
}
