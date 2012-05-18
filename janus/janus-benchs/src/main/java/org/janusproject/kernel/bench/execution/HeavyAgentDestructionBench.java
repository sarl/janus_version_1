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
import org.janusproject.kernel.agent.AgentLifeState;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.bench.BenchConstants;
import org.janusproject.kernel.bench.api.AgentNumberBenchRun;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.CsvBench;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class HeavyAgentDestructionBench extends CsvBench<AgentNumberBenchRun> {

	private KillableAgent agent;
	
	/**
	 * @param directory
	 * @throws IOException
	 */
	public HeavyAgentDestructionBench(File directory) throws IOException {
		super(directory);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() throws Exception {
		super.initialize();
		setNumberOfTests(1);
		setNumberOfRuns(BenchConstants.RESETTING_RUN_NUMBER);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<AgentNumberBenchRun> setUpGroupWithCSV(String benchFunctionName) throws Exception {
		writeHeader("Name", "Agents", "Tests", "Run (ns)", "Unit (ns)", "OS Load Average"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
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
		
		AtomicInteger launchedAgents = new AtomicInteger();
		
		this.agent = new KillableAgent(launchedAgents);
		
		if (run.getName().startsWith("LaunchHeavy")) { //$NON-NLS-1$
			kernel.submitHeavyAgent(this.agent);
		}
		else if (run.getName().startsWith("LaunchLight")) { //$NON-NLS-1$
			kernel.submitLightAgent(this.agent);
		}
		else {
			throw new IllegalStateException();
		}
		
		for(int i=0; i<getCurrentRun().getNumberOfAgents()-1; ++i) {
			kernel.submitHeavyAgent(new IddleAgent(launchedAgents));
		}
		
		kernel.launchDifferedExecutionAgents();

		while (launchedAgents.get()<run.getNumberOfAgents()) {
			Thread.yield();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownUnitaryBench(AgentNumberBenchRun run) throws Exception {
		this.agent = null;
		Kernels.killAll();
		writeRecord(
				run.getName(),
				run.getNumberOfAgents(), 
				getNumberOfTests(),
				run.getRunDuration(),
				run.getTestAverageDuration(),
				getSystemLoadAverage());
	}
	
	/**
	 * @throws Exception
	 */
	public void benchLaunchHeavy() throws Exception {
		this.agent.requestKill();
		while (this.agent.getState()!=AgentLifeState.DIED) {
			Thread.yield();
		}
	}

	/**
	 * @throws Exception
	 */
	public void benchLaunchLight() throws Exception {
		this.agent.requestKill();
		while (this.agent.getState()!=AgentLifeState.DIED) {
			Thread.yield();
		}
	}
	
	/**
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private static  class KillableAgent extends Agent {
	
		private static final long serialVersionUID = 3034430040363375633L;
		
		private AtomicInteger flag;
		private boolean requestKill = false;
		
		/**
		 * @param flag
		 */
		public KillableAgent(AtomicInteger flag) {
			this.flag = flag;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized Status live() {
			if (this.flag==null) {
				if (this.requestKill) {
					killMe();
				}
			}
			else {
				this.flag.incrementAndGet();
				this.flag = null;
			}
			return null;
		}

		/**
		 */
		public synchronized void requestKill() {
			this.requestKill = true;
		}
		
	}
		
}