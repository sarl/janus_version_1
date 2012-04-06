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
import org.janusproject.kernel.crio.core.Organization;

/** 
 * 1 group.
 * n players.
 * 1 role per player.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractRoleExecutionBench extends AbstractRoleOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractRoleExecutionBench(File directory) throws IOException {
		super(directory,
				"Agents", //$NON-NLS-1$
				"Lives per Second"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected org.janusproject.kernel.bench.api.AbstractRoleOperationPerSecondCsvBench.BenchMarkedAgent launchAgents(
			OperationAgentNumberBenchRun run, Kernel kernel,
			AtomicInteger launchFlag) {
		
		for(int i=0; i<run.getNumberOfAgents()-1; ++i) {
			kernel.submitLightAgent(new IddleRoleAgent(launchFlag, BenchOrganization.class));
		}
		
		BenchMarkedAgent referee = new BenchMarkedAgent(
				launchFlag,
				BenchOrganization.class,
				RefereeRole.class,
				false);
		
		launchRefereeAgent(kernel, referee);
		
		return referee;
	}
	
	/** Launch the referee agent.
	 * 
	 * @param kernel
	 * @param agent
	 */
	protected abstract void launchRefereeAgent(Kernel kernel, Agent agent);
	
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
	
}