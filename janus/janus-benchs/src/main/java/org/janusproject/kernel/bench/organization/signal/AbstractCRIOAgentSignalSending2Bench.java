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
package org.janusproject.kernel.bench.organization.signal;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.agentsignal.SignalListener;
import org.janusproject.kernel.bench.api.AbstractRoleOperationPerSecondCsvBench;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * 0 listener is registered in the role.
 * n listeners are registered in the player.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractCRIOAgentSignalSending2Bench extends AbstractRoleOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractCRIOAgentSignalSending2Bench(File directory) throws IOException {
		super(directory,
				"Listeners", //$NON-NLS-1$
				"Signals per Second"); //$NON-NLS-1$
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
				2);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected org.janusproject.kernel.bench.api.AbstractRoleOperationPerSecondCsvBench.BenchMarkedAgent launchAgents(
			OperationAgentNumberBenchRun run, Kernel kernel,
			AtomicInteger launchFlag) {
		EmitterAgent emitter = new EmitterAgent(launchFlag);
		
		launchEmitter(kernel, emitter, run.getNumberOfAgents()); // stands for getNumberOfSignals
		
		return emitter;
	}
		
	/** Launch the given signal emitter agent.
	 * 
	 * @param kernel
	 * @param agent
	 * @param nbListeners
	 */
	protected abstract void launchEmitter(Kernel kernel, Agent agent, int nbListeners);
	
	/**
	 * @throws Exception
	 */
	public void benchFireSignal() throws Exception {
		runBenchFor(getCurrentRun(), getBenchMarkedRole());
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class EmitterAgent extends BenchMarkedAgent {
		
		private static final long serialVersionUID = 3984926646392360946L;

		/**
		 * @param flag
		 */
		public EmitterAgent(AtomicInteger flag) {
			super(flag, 
					BenchOrganization.class, 
					EmitterRole.class,
					true);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... params) {
			int nb = ((Integer)params[0]).intValue();
			SignalListener listener = new IddleSignalListener();
			for(int i=0; i<nb; ++i) {
				addSignalListener(listener);
			}
			return super.activate(params);
		}
		
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class EmitterRole extends BenchMarkedRole {

		/**
		 */
		public EmitterRole() {
			super();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doBench() {
			fireSignal(new Signal("MYSIGNAL")); //$NON-NLS-1$
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
			addRole(EmitterRole.class);
		}
		
	}

}