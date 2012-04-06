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
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.message.StringMessage;
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
public class CRIOBroadcastMessageSendingBench extends CsvBench<AgentNumberBenchRun> {

	private Emitter emitter;

	/**
	 * @param directory
	 * @throws IOException
	 */
	public CRIOBroadcastMessageSendingBench(File directory) throws IOException {
		super(directory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<AgentNumberBenchRun> setUpGroupWithCSV(String benchFunctionName) throws Exception {
		writeHeader("Name", "Agents", "Tests", "Run (ns)", "Operation Duration (ns)", "Standard Deviation", "OS Load Average"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		return BenchUtil.makeAllHeavyAgentIntervals(
				AgentNumberBenchRun.class,
				benchFunctionName,
				2);
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

		for(int i=0; i<run.getNumberOfAgents()-1; ++i) {
			kernel.launchLightAgent(new IddleAgent(null, BenchOrganization.class, true));
		}

		this.emitter = new Emitter();
		kernel.launchLightAgent(this.emitter);

		while (!this.emitter.getState().isAlive()) {
			Thread.yield();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownUnitaryBench(AgentNumberBenchRun run) throws Exception {
		Kernels.killAll();
		this.emitter = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownMultiRunBench(int nbRuns, AgentNumberBenchRun run)
			throws Exception {
		writeRecord(
				run.getName(),
				run.getNumberOfAgents(), 
				getNumberOfTests(),
				run.getRunDuration(),
				run.getTestAverageDuration(),
				run.getTestStandardDeviation(),
				getSystemLoadAverage());
		super.tearDownMultiRunBench(nbRuns, run);
	}

	/**
	 */
	public void benchBroadcastMessage() {
		this.emitter.broadcast();
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Emitter extends Agent {

		private static final long serialVersionUID = 8804935270327139156L;

		private GroupAddress group;
		private EmitterRole emitterRole;

		/**
		 */
		public Emitter() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... parameters) {
			Status s = super.activate(parameters);
			setMailbox(BenchUtil.createMailboxForSendingBenchs());
			this.group = getOrCreateGroup(BenchOrganization.class);
			requestRole(EmitterRole.class, this.group);
			this.emitterRole = getRole(this.group, EmitterRole.class); 
			return s;
		}

		/**
		 */
		public void broadcast() {
			this.emitterRole.broadcast();
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class EmitterRole extends Role {

		/**
		 */
		public EmitterRole() {
			super();
			setMailbox(BenchUtil.createMailboxForSendingBenchs());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status live() {
			return null;
		}

		/**
		 */
		public void broadcast() {
			broadcastMessage(IddleRole.class, new StringMessage("")); //$NON-NLS-1$
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
		}
		
	}
	
}