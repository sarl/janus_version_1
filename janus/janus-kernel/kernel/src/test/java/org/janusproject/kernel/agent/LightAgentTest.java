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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelAdapter;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/** This set of test is dedicated to light agents only.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class LightAgentTest extends TestCase {

	private static final long TIMEOUT = 5000;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.SEVERE);
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
	
	/**
	 * @throws Throwable
	 */
	public static void testReleaseAllRoles() throws Throwable {
		PlayerAgent agent = new PlayerAgent();
		Kernel k = Kernels.get();

		KernelEventListener listener = new KernelEventListener();
		k.addKernelListener(listener);

		k.launchLightAgent(agent);
		k.waitUntilTermination(TIMEOUT);
		
		listener.throwsErrors();		
	}

	/** This set of test is dedicated to light agents only.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	@AgentActivationPrototype
	private static class PlayerAgent extends Agent {

		private static final long serialVersionUID = 133767903021367950L;
		
		private boolean run = false;

		/**
		 */
		public PlayerAgent() {
			super(true);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... parameters) {
			GroupAddress grp = getOrCreateGroup(Organization1Stub.class);
			if (requestRole(RoleStub.class, grp)==null) {
				throw new RuntimeException("unable to request RoleStub"); //$NON-NLS-1$
			}
			if (requestRole(Role3Stub.class, grp)==null) {
				throw new RuntimeException("unable to request Role3Stub"); //$NON-NLS-1$
			}
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status live() {
			Status s = super.live();
			if (s!=null && s.isFailure()) {
				throw new RuntimeException(s.toString());
			}
			if (!this.run) {
				leaveAllRoles();
			}
			else {
				throw new RuntimeException("I should be killed"); //$NON-NLS-1$
			}
			return null;
		}
		
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class KernelEventListener extends KernelAdapter {
		
		/** Errors.
		 */
		public final List<Throwable> errors = new LinkedList<Throwable>();
		
		/**
		 */
		public KernelEventListener() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean exceptionUncatched(Throwable error) {
			this.errors.add(error);
			return true;
		}
		
		/** Print the uncautched errors. 
		 * 
		 * @throws Throwable
		 */
		public void throwsErrors() throws Throwable {
			for(Throwable e: this.errors) {
				throw e;
			}
		}
		
	}

}
