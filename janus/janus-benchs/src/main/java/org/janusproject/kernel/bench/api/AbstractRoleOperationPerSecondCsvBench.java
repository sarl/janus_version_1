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
package org.janusproject.kernel.bench.api;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.bench.BenchConstants;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** This abstract class describes a bench for the Janus kernel with
 * all the results stored in a CSV file.
 * 
 * @param <R> is the type of the runs
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractRoleOperationPerSecondCsvBench<R extends OperationAgentNumberBenchRun> extends CsvBench<R> {

	private BenchMarkedAgent benchmarkedAgent = null;
	private BenchMarkedRole benchmarkedRole = null;
	private final String xName;
	private final String yName;
	
	/**
	 * @param directory
	 * @param xName
	 * @param yName
	 * @throws IOException
	 */
	public AbstractRoleOperationPerSecondCsvBench(File directory,
			String xName, String yName) throws IOException {
		super(directory);
		this.xName = xName;
		this.yName = yName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() throws Exception {
		super.initialize();
		setNumberOfTests(1);
		setNumberOfRuns(BenchConstants.PER_SECOND_BENCH_RUN_NUMBER);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<R> setUpGroupWithCSV(String benchFunctionName) throws Exception {
		writeHeader("Name", this.xName, "Tests", "Run (ns)", "Test (ns)", this.yName, "Standard Deviation", "OS Load Average"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		return createIntervals(benchFunctionName);
	}
	
	@Override
	public void setUpMultiRunBench(R run) throws Exception {
		run.clearNumberOfOperations();
	}
	
	
	/** Create the intervals.
	 * 
	 * @param benchFunctionName
	 * @return the intervals
	 * @throws Exception
	 */
	protected abstract SizedIterator<R> createIntervals(String benchFunctionName) throws Exception;
	
	/** Replies the agent to bench.
	 * 
	 * @return the agent to bench.
	 */
	protected final BenchMarkedAgent getBenchMarkedAgent() {
		return this.benchmarkedAgent;
	}
	
	/** Replies the role to bench.
	 * 
	 * @return the role to bench.
	 */
	protected final BenchMarkedRole getBenchMarkedRole() {
		return this.benchmarkedRole;
	}

	@Override
	public final void setUpUnitaryBench(R run) throws Exception {
		super.setUpUnitaryBench(run);
		Kernels.killAll();
		LoggerUtil.setLoggingEnable(false);
		Kernel kernel = Kernels.create();
		AtomicInteger nbLaunchedAgents = new AtomicInteger(0);
		
		this.benchmarkedAgent = launchAgents(run, kernel, nbLaunchedAgents);

		kernel.launchDifferedExecutionAgents();
		
		waitAgentLaunching(run, nbLaunchedAgents);

		this.benchmarkedRole = this.benchmarkedAgent.getBenchMarkedRole();
		while (this.benchmarkedRole==null) {
			Thread.yield();
			this.benchmarkedRole = this.benchmarkedAgent.getBenchMarkedRole();
		}
	}
	
	/** Wait for agent launching.
	 * 
	 * @param run
	 * @param flag
	 */
	protected void waitAgentLaunching(R run, AtomicInteger flag) {
		while (flag.get()<run.getNumberOfAgents()) {
			Thread.yield();
		}
	}
	
	/** Launch the agents to benchmark.
	 * 
	 * @param run
	 * @param kernel
	 * @param launchFlag
	 * @return the benchmarked agent.
	 */
	protected abstract BenchMarkedAgent launchAgents(R run, Kernel kernel, AtomicInteger launchFlag);
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownUnitaryBench(R run) throws Exception {
		Kernels.killAll();
		this.benchmarkedAgent = null;
		this.benchmarkedRole = null;
	}
	
	@Override
	public void tearDownMultiRunBench(int nbRuns, R run) throws Exception {
		writeRecord(
				run.getName(),
				run.getNumberOfAgents(), 
				getNumberOfTests(),
				run.getRunDuration(),
				run.getTestAverageDuration(),
				run.getNumberOfOperations(),
				run.getNumberOfOperationStandardDeviation(),
				getSystemLoadAverage());
	}
	
	/** Wait for the end of the time-based benchmark.
	 * 
	 * @param role
	 * @return the number of operations.
	 * @throws Exception
	 */
	protected static long waitForNbOperationsPerSecond(BenchMarkedRole role) throws Exception {
		long s = System.currentTimeMillis();
		Thread.sleep(BenchConstants.PER_SECOND_BENCH_SLEEPING_DURATION);
		long e = System.currentTimeMillis();
		long nb = role.stopBench();
		return (long)(1000. * nb / (e-s));
	}
	
	/** Run the bench for the given role.
	 * 
	 * @param run
	 * @param role
	 * @throws Exception
	 */
	protected void runBenchFor(R run, BenchMarkedRole role) throws Exception {
		role.startBench();
		long nb = waitForNbOperationsPerSecond(role);
		run.addNumberOfOperations(nb);
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected static abstract class BenchMarkedRole extends Role {
		
		private BenchType n = BenchType.NONE;
		private long nb = 0;
		
		/**
		 */
		public BenchMarkedRole() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized final Status live() {
			if (this.n==BenchType.RUN) {
				if (doBench()) {
					++this.nb;
				}
			}
			return null;
		}
		
		/** Do the bench.
		 * @return <code>true</code> if increment the nb of operations, <code>false</code>
		 * to not increment the nb of operations.
		 */
		protected abstract boolean doBench();
		
		/**
		 */
		public synchronized void startBench() {
			this.nb = 0;
			this.n = BenchType.RUN;
		}

		/**
		 * @return the number of operations of the given type.
		 */
		public synchronized long stopBench() {
			this.n = BenchType.NONE;
			return this.nb;
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected static class BenchMarkedAgent extends Agent {
		
		private static final long serialVersionUID = 8804935270327139156L;

		private AtomicInteger flag;
		private final Class<? extends Organization> organization;
		private final Class<? extends Role> role;
		private final boolean newGroup;
		private GroupAddress group = null;
		private Object[] initParameters;
		
		/**
		 * @param flag
		 * @param organization
		 * @param role
		 * @param newGroup
		 * @param initParameters
		 */
		public BenchMarkedAgent(AtomicInteger flag,
				Class<? extends Organization> organization,
				Class<? extends Role> role,
				boolean newGroup,
				Object... initParameters) {
			this.flag = flag;
			this.organization = organization;
			this.role = role;
			this.newGroup = newGroup;
			this.initParameters = initParameters;
		}
		
		/** Replies the organization to join.
		 * 
		 * @return the organization to join.
		 */
		protected Class<? extends Organization> getOrganization() {
			return this.organization;
		}

		/** Replies the group to join.
		 * 
		 * @return the group to join.
		 */
		protected GroupAddress getGroup() {
			return this.group;
		}

		/** Replies the role to play.
		 * 
		 * @return the role to play.
		 */
		protected Class<? extends Role> getRole() {
			return this.role;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... parameters) {
			if (this.newGroup) {
				this.group = createGroup(this.organization);
			}
			else {
				this.group = getOrCreateGroup(this.organization);
			}
			if (requestRole(this.role, this.group, this.initParameters)==null) {
				throw new RuntimeException("no role: "+this.role); //$NON-NLS-1$
			}
			this.initParameters = null;
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized final Status live() {
			if (this.flag==null) {
				return super.live();
			}
			this.flag.incrementAndGet();
			this.flag = null;
			return StatusFactory.ok(this);
		}
		
		/** Replies the role taken by the agent.
		 * 
		 * @return the role taken by the agent.
		 */
		public BenchMarkedRole getBenchMarkedRole() {
			Role r = getRole(this.group, this.role);
			if (r==null || r instanceof BenchMarkedRole)
				return (BenchMarkedRole)r;
			throw new ClassCastException();
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static enum BenchType {
		/** */
		NONE,
		/** */
		RUN,
	}

}