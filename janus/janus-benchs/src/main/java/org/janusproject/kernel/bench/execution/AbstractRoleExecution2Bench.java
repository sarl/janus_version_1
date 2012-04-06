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
package org.janusproject.kernel.bench.execution;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.bench.api.AbstractRoleOperationPerSecondCsvBench;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.status.Status;

/** 
 * n groups.
 * 1 player.
 * 1 role per group.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractRoleExecution2Bench extends AbstractRoleOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractRoleExecution2Bench(File directory) throws IOException {
		super(directory,
				"Groups", //$NON-NLS-1$
				"Lives per Second"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void waitAgentLaunching(OperationAgentNumberBenchRun run,
			AtomicInteger flag) {
		while (flag.get()<1) {
			Thread.yield();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final org.janusproject.kernel.bench.api.AbstractRoleOperationPerSecondCsvBench.BenchMarkedAgent launchAgents(
			OperationAgentNumberBenchRun run, Kernel kernel,
			AtomicInteger launchFlag) {
		
		RefereeAgent referee = new RefereeAgent(launchFlag);
		
		launchRefereeAgent(kernel, referee, run.getNumberOfAgents()); // stands for getNumberOfGroups
		
		return referee;
	}
	
	/** Launch the referee agent.
	 * 
	 * @param kernel
	 * @param agent
	 * @param nbGroups
	 */
	protected abstract void launchRefereeAgent(Kernel kernel, Agent agent, int nbGroups);
	
	/**
	 * @throws Exception
	 */
	public void benchCountExecutions() throws Exception {
		runBenchFor(getCurrentRun(), getBenchMarkedRole());
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class RefereeAgent extends BenchMarkedAgent {
		
		private static final long serialVersionUID = 2377461826680471054L;

		/**
		 * @param flag
		 */
		public RefereeAgent(AtomicInteger flag) {
			super(flag,
					BenchOrganization.class,
					RefereeRole.class,
					true);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... parameters) {
			int nbGroups = ((Number)parameters[0]).intValue();
			super.activate(parameters);
			nbGroups--;
			for(int i=0; i<nbGroups; ++i) {
				GroupAddress nGroup = createGroup(getOrganization());
				if (requestRole(IddleRole.class, nGroup)==null) {
					throw new RuntimeException("no role: "+IddleRole.class); //$NON-NLS-1$
				}
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
	public static class BenchOrganization extends Organization {

		/**
		 * @param context
		 */
		public BenchOrganization(CRIOContext context) {
			super(context);
			addRole(IddleRole.class);
			addRole(RefereeRole.class);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class RefereeRole extends BenchMarkedRole {
		
		/**
		 */
		public RefereeRole() {
			super();
		}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doBench() {
			getMailbox().clear();
			return true;
		}
		
	}
	
}