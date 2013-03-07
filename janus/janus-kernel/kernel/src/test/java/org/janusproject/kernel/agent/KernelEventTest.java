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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.KernelEvent;
import org.janusproject.kernel.KernelEvent.KernelEventType;
import org.janusproject.kernel.KernelListener;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.sizediterator.EmptyIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelEventTest extends TestCase {

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
	public void testGetSource() {
		for(KernelEventType type : KernelEventType.values()) {
			for(boolean isKernel : Arrays.asList(Boolean.TRUE, Boolean.FALSE)) {
				Kernel source = new KernelStub();
				AgentAddress adr = new AgentAddressStub();
				KernelEvent event = new KernelEvent(
						type,
						source,
						adr,
						isKernel);
				assertSame(source, event.getSource());
			}
		}
	}
	
	/**
	 */
	public void testGetType() {
		for(KernelEventType type : KernelEventType.values()) {
			for(boolean isKernel : Arrays.asList(Boolean.TRUE, Boolean.FALSE)) {
				Kernel source = new KernelStub();
				AgentAddress adr = new AgentAddressStub();
				KernelEvent event = new KernelEvent(
						type,
						source,
						adr,
						isKernel);
				assertEquals(type, event.getType());
			}
		}
	}
	
	/**
	 */
	public void testGetAgent() {
		for(KernelEventType type : KernelEventType.values()) {
			for(boolean isKernel : Arrays.asList(Boolean.TRUE, Boolean.FALSE)) {
				Kernel source = new KernelStub();
				AgentAddress adr = new AgentAddressStub();
				KernelEvent event = new KernelEvent(
						type,
						source,
						adr,
						isKernel);
				assertEquals(adr, event.getAgent());
			}
		}
	}

	/**
	 */
	public void testIsKernelAgent() {
		for(KernelEventType type : KernelEventType.values()) {
			for(boolean isKernel : Arrays.asList(Boolean.TRUE, Boolean.FALSE)) {
				Kernel source = new KernelStub();
				AgentAddress adr = new AgentAddressStub();
				KernelEvent event = new KernelEvent(
						type,
						source,
						adr,
						isKernel);
				assertEquals(isKernel, event.isKernelAgent());
			}
		}
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class KernelStub implements Kernel {

		/**
		 */
		public KernelStub() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addKernelListener(KernelListener listener) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean canCommitSuicide() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress getAddress() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CRIOContext getCRIOContext() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public KernelContext getKernelContext() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentLifeState getState() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isAlive() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status kill() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchHeavyAgent(Agent agent, Object... initParams) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchHeavyAgent(Agent agent, String name, Object... initParams) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchLightAgent(Agent agent, String name, Object... initParams) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchLightAgent(Agent agent, Object... initParams) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchLightAgent(Agent agent,
				AgentActivator activator, Object... initParams) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress launchLightAgent(Agent agent,
				String name, AgentActivator activator, Object... initParams) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeKernelListener(KernelListener listener) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addAgentLifeStateListener(AgentLifeStateListener listener) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAgentLifeStateListener(AgentLifeStateListener listener) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void launchDifferedExecutionAgents() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitHeavyAgent(Agent agent,
				Object... initParameters) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitHeavyAgent(Agent agent, String name,
				Object... initParameters) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitLightAgent(Agent agent, String name,
				Object... initParameters) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitLightAgent(Agent agent,
				Object... initParameters) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitLightAgent(Agent agent,
				AgentActivator activator, Object... initParameters) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AgentAddress submitLightAgent(Agent agent,
				String name, AgentActivator activator,
				Object... initParameters) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProbeManager getProbeManager() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChannelManager getChannelManager() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SizedIterator<AgentAddress> getAgents() {
			return EmptyIterator.singleton();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Logger getLogger() {
			return Logger.getAnonymousLogger();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pause() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isPaused() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void resume() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addGroupListener(GroupListener listener) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeGroupListener(GroupListener listener) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress createGroup(
				Class<? extends Organization> organization,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress createGroup(
				Class<? extends Organization> organization,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions,
				String groupName) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress createGroup(
				OrganizationFactory<? extends Organization> factory,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress createGroup(
				OrganizationFactory<? extends Organization> factory,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions,
				String groupName) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress createGroup(
				Class<? extends Organization> organization) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress createGroup(
				Class<? extends Organization> organization, String groupName) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress createGroup(OrganizationFactory<?> factory) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getExistingGroup(
				Class<? extends Organization> organization) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GroupAddress> getExistingGroups(
				Class<? extends Organization> organization) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getExistingGroup(
				OrganizationFactory<? extends Organization> factory) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getOrCreateGroup(
				Class<? extends Organization> organization) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getOrCreateGroup(
				Class<? extends Organization> organization, String groupName) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getOrCreateGroup(
				OrganizationFactory<? extends Organization> factory) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getOrCreateGroup(
				OrganizationFactory<? extends Organization> factory,
				String groupName) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getOrCreateGroup(UUID id,
				Class<? extends Organization> organization,
				Collection<? extends GroupCondition> obtainConditions,
				Collection<? extends GroupCondition> leaveConditions,
				MembershipService membership, boolean distributed,
				boolean persistent, String groupName) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GroupAddress getOrCreateGroup(UUID id,
				Class<? extends Organization> organization, String groupName) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void waitUntilTermination() throws InterruptedException {
			wait();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void waitUntilTermination(long timeout) throws InterruptedException {
			wait(timeout);
		}

		/** {@inheritDoc}
		 */
		@Override
		public void createCheckPoint(OutputStream stream) throws IOException {
			throw new UnsupportedOperationException();
		}

	}
	
}
