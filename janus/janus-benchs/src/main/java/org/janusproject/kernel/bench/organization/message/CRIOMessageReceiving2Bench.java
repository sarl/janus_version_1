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
package org.janusproject.kernel.bench.organization.message;

import java.io.File;
import java.io.IOException;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.bench.api.AgentNumberBenchRun;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.CsvBench;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** Run the bench on the message API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class CRIOMessageReceiving2Bench extends CsvBench<AgentNumberBenchRun> {

	private Receiver receiver;

	/**
	 * @param directory
	 * @throws IOException
	 */
	public CRIOMessageReceiving2Bench(File directory) throws IOException {
		super(directory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<AgentNumberBenchRun> setUpGroupWithCSV(String benchFunctionName) throws Exception {
		writeHeader("Name", "Agents", "Tests", "Run (ns)", "Unit (ns)", "OS Load Average"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		return BenchUtil.makeAllLightAgentIntervals(
				AgentNumberBenchRun.class,
				benchFunctionName,
				3);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUpUnitaryBench(AgentNumberBenchRun run) throws Exception {
		super.setUpUnitaryBench(run);
		Kernels.killAll();
		LoggerUtil.setLoggingEnable(false);
		Kernel kernel = Kernels.create();

		for(int i=0; i<run.getNumberOfAgents()-2; ++i) {
			kernel.launchLightAgent(new Receiver(false));
		}

		this.receiver = new Receiver(true);
		
		kernel.launchHeavyAgent(new EmitterAgent(null, BenchOrganization.class, this.receiver.getAddress(), ReceiverRole.class));
		kernel.launchLightAgent(this.receiver);

		while (!this.receiver.getState().isAlive()) {
			Thread.yield();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownUnitaryBench(AgentNumberBenchRun run) throws Exception {
		Kernels.killAll();
		this.receiver = null;
		writeRecord(
				run.getName(),
				run.getNumberOfAgents(), 
				getNumberOfTests(),
				run.getRunDuration(),
				run.getTestAverageDuration(),
				getSystemLoadAverage());
	}

	/**
	 */
	public void benchPeekMessage() {
		this.receiver.peek();
	}

	/**
	 */
	public void benchGetMessage() {
		this.receiver.get();
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Receiver extends Agent {

		private static final long serialVersionUID = 8804935270327139156L;

		private final boolean active;
		private GroupAddress group;
		private ReceiverRole receiverRole;

		/**
		 * @param active
		 */
		public Receiver(boolean active) {
			this.active = active;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... parameters) {
			Status s = super.activate(parameters);
			this.group = getOrCreateGroup(BenchOrganization.class);
			requestRole(ReceiverRole.class, this.group, this.active);
			this.receiverRole = getRole(this.group, ReceiverRole.class); 
			return s;
		}

		/**
		 */
		public void peek() {
			this.receiverRole.peek();
		}

		/**
		 */
		public void get() {
			this.receiverRole.get();
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@RoleActivationPrototype(
			fixedParameters={Boolean.class}
	)
	public static class ReceiverRole extends Role {

		private boolean active = false;
		
		/**
		 */
		public ReceiverRole() {
			super();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... params) {
			this.active = ((Boolean)params[0]).booleanValue();
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status live() {
			if (this.active) {
				if (getMailbox().size()>5000) getMailbox().clear();
			}
			else {
				getMailbox().clear();
			}
			return null;
		}

		/**
		 */
		public void peek() {
			peekMessage();
		}

		/**
		 */
		public void get() {
			getMessage();
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class BenchOrganization extends Organization {

		/**
		 * @param context
		 */
		public BenchOrganization(CRIOContext context) {
			super(context);
			addRole(EmitterRole.class);
			addRole(IddleRole.class);
			addRole(ReceiverRole.class);
		}
		
	}
	
}