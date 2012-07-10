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
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.role.RoleFactory;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ActivatorAgentTest extends TestCase {

	private KernelAgent kernel;
	private ActivatorAgent<AgentActivator> agent;

	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		Kernels.shutdownNow();
		this.kernel = new KernelAgent(new AgentActivator(), true, null, null);
		this.agent = new ActivatorAgent<AgentActivator>(new AgentActivator());
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

	private void bindToKernel(Agent h) {
		h.kernel = new WeakReference<KernelAgent>(this.kernel);
		this.kernel.getKernelContext().getAgentRepository().add(h.getAddress(), h);
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
		AgentActivator s;
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

	/**
	 */
	public void testIsSelfKillableNow_notSuicidal() {
		AgentStub ag = new AgentStub(false);
		bindToKernel(ag);

		ag.proceedPrivateInitialization();

		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		for(int i=0; i<5; i++) ag.live();

		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		ag.requestTestRole();
		for(int i=0; i<5; i++) ag.live();

		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		Agent ag2 = new Agent();
		bindToKernel(ag2);
		ag.activator.addAgent(ag2);
		for(int i=0; i<5; i++) ag.live();

		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		ag.leaveTestRole();
		for(int i=0; i<5; i++) ag.live();

		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		ag.activator.removeAgent(ag2);
		for(int i=0; i<5; i++) ag.live();

		assertFalse(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());
	}

	/**
	 */
	public void testIsSelfKillableNow_suicidal() {
		AgentStub ag = new AgentStub(true);
		bindToKernel(ag);

		ag.proceedPrivateInitialization();

		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		for(int i=0; i<5; i++) ag.live();

		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		ag.requestTestRole();
		for(int i=0; i<5; i++) ag.live();

		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		Agent ag2 = new Agent();
		bindToKernel(ag2);
		ag.activator.addAgent(ag2);
		for(int i=0; i<5; i++) ag.live();

		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		ag.leaveTestRole();
		for(int i=0; i<5; i++) ag.live();

		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		ag.activator.removeAgent(ag2);
		for(int i=0; i<5; i++) ag.live();

		assertTrue(ag.canCommitSuicide());
		assertTrue(ag.isSelfKillableNow());

		ag.requestTestRole();
		for(int i=0; i<5; i++) ag.live();

		assertTrue(ag.canCommitSuicide());
		assertFalse(ag.isSelfKillableNow());

		ag.leaveTestRole();
		for(int i=0; i<5; i++) ag.live();

		assertTrue(ag.canCommitSuicide());
		assertTrue(ag.isSelfKillableNow());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AgentStub extends ActivatorAgent<AgentActivator> {

		private static final long serialVersionUID = 4510410418205108880L;

		private GroupAddress group = null;

		/** Activator. */
		public final AgentActivator activator;

		/**
		 * @param commitSuicide
		 */
		public AgentStub(boolean commitSuicide) {
			this(new AgentActivator(), commitSuicide);
		}

		/**
		 * @param activator
		 * @param commitSuicide
		 */
		private AgentStub(AgentActivator act, boolean commitSuicide) {
			super(act, commitSuicide);
			this.activator = act;
		}

		/**
		 */
		public void requestTestRole() {
			this.group = getOrCreateGroup(new OrganizationFactory<Organization1Stub>() {
				@Override
				public Class<Organization1Stub> getOrganizationType() {
					return Organization1Stub.class;
				}
				@Override
				public Organization1Stub newInstance(CRIOContext context)
						throws Exception {
					return new Organization1Stub(context);
				}
			});
			assertNotNull(this.group);
			assertNotNull(requestRole(RoleStub.class, this.group, new RoleFactory() {
				@Override
				public Role newInstance(Class<? extends Role> type)
						throws Exception {
					return type.newInstance();
				}
			}));
		}

		/**
		 */
		public void leaveTestRole() {
			assertNotNull(this.group);
			assertTrue(leaveRole(RoleStub.class, this.group));
			this.group = null;
		}

	}

}
