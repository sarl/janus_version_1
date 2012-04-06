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
import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.bench.api.AbstractRoleOperationPerSecondCsvBench;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * (n-2) agents are playing IddleRole
 * 1 agent is playing the EmitterRole
 * 1 agent is playing the benchmarked role (GetRole or PeekRole)
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractCRIOAgentMessageReceivingBench extends AbstractRoleOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractCRIOAgentMessageReceivingBench(File directory) throws IOException {
		super(directory,
				"Players", //$NON-NLS-1$
				"Messages per Second"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SizedIterator<OperationAgentNumberBenchRun> createIntervals(
			String benchFunctionName) throws Exception {
		return BenchUtil.makeAllLightAgentIntervals(
				OperationAgentNumberBenchRun.class,
				benchFunctionName,
				3);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected org.janusproject.kernel.bench.api.AbstractRoleOperationPerSecondCsvBench.BenchMarkedAgent launchAgents(
			OperationAgentNumberBenchRun run, Kernel kernel,
			AtomicInteger launchFlag) {
		for(int i=0; i<run.getNumberOfAgents()-2; ++i) {
			kernel.submitLightAgent(new IddleAgent(launchFlag, BenchOrganization.class, false));
		}

		BenchMarkedAgent receiver;
		EmitterAgent emitter;
		
		if (run.getName().startsWith("PeekMessage")) { //$NON-NLS-1$
			receiver = new BenchMarkedAgent(launchFlag, BenchOrganization.class, PeekRole.class, false);
			emitter = new EmitterAgent(launchFlag, BenchOrganization.class, receiver.getAddress(), PeekRole.class);
		}
		else if (run.getName().startsWith("GetMessage")) { //$NON-NLS-1$
			receiver = new BenchMarkedAgent(launchFlag, BenchOrganization.class, GetRole.class, false);
			emitter = new EmitterAgent(launchFlag, BenchOrganization.class, receiver.getAddress(), GetRole.class);
		}
		else {
			throw new IllegalStateException();
		}
		
		kernel.submitHeavyAgent(emitter);
		launchReceiver(kernel, receiver);

		return receiver;
	}
	
	/** Launch the receiver of the messages.
	 * 
	 * @param kernel
	 * @param agent
	 */
	protected abstract void launchReceiver(Kernel kernel, Agent agent);
	
	/**
	 * @throws Exception
	 */
	public void benchPeekMessage() throws Exception {
		runBenchFor(getCurrentRun(), getBenchMarkedRole());
	}
	
	/**
	 * @throws Exception
	 */
	public void benchGetMessage() throws Exception {
		runBenchFor(getCurrentRun(), getBenchMarkedRole());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class PeekRole extends BenchMarkedRole {

		/**
		 */
		public PeekRole() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doBench() {
			Message m = peekMessage();
			return m!=null;
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
			addRole(PeekRole.class);
			addRole(GetRole.class);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class GetRole extends BenchMarkedRole {
	
		/**
		 */
		public GetRole() {
			super();
		}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doBench() {
			Message m = getMessage();
			return m!=null;
		}
		
	}

}