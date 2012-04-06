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
import org.janusproject.kernel.bench.api.AbstractOperationPerSecondCsvBench;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractAgentExecutionBench extends AbstractOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractAgentExecutionBench(File directory) throws IOException {
		super(directory,
				"Agents", //$NON-NLS-1$
				"Lives per Second"); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected org.janusproject.kernel.bench.api.AbstractOperationPerSecondCsvBench.BenchMarkedAgent launchAgents(
			OperationAgentNumberBenchRun run, Kernel kernel,
			AtomicInteger launchFlag) {
		for(int i=0; i<run.getNumberOfAgents()-1; ++i) {
			kernel.submitLightAgent(new IddleAgent(launchFlag));
		}
		
		Referee ag = new Referee(launchFlag);
		
		launchRefereeAgent(kernel, ag);
		
		return ag;
	}
	
	/** Launch the given agent.
	 * 
	 * @param kernel
	 * @param agent
	 */
	protected abstract void launchRefereeAgent(Kernel kernel, Agent agent);
	
	/**
	 * @throws Exception
	 */
	public void benchCountExecutions() throws Exception {
		runBenchFor(getCurrentRun(), getBenchMarkedAgent());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private class Referee extends BenchMarkedAgent {

		private static final long serialVersionUID = 357677645079944787L;

		/**
		 * @param flag
		 */
		public Referee(AtomicInteger flag) {
			super(flag);
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