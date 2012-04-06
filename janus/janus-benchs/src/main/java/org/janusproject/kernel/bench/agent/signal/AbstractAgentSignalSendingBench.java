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
package org.janusproject.kernel.bench.agent.signal;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.agentsignal.SignalListener;
import org.janusproject.kernel.bench.api.AbstractOperationPerSecondCsvBench;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;
import org.janusproject.kernel.status.Status;

/** Run the bench on the signal API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractAgentSignalSendingBench extends AbstractOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractAgentSignalSendingBench(File directory) throws IOException {
		super(directory,
				"Listeners", //$NON-NLS-1$
				"Signals per Second"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected org.janusproject.kernel.bench.api.AbstractOperationPerSecondCsvBench.BenchMarkedAgent launchAgents(
			OperationAgentNumberBenchRun run, Kernel kernel,
			AtomicInteger launchFlag) {
		Emitter emitter = new Emitter(launchFlag);
		launchEmitter(kernel, emitter, run.getNumberOfAgents());
		return emitter;
	}
	
	/**
	 * @param run
	 * @param flag
	 */
	@Override
	protected final void waitAgentLaunching(OperationAgentNumberBenchRun run, AtomicInteger flag) {
		while (flag.get()<1) {
			Thread.yield();
		}
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
		runBenchFor(getCurrentRun(), getBenchMarkedAgent());
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Emitter extends BenchMarkedAgent {
		
		private static final long serialVersionUID = 8804935270327139156L;

		/**
		 * @param flag
		 */
		public Emitter(AtomicInteger flag) {
			super(flag);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... parameters) {
			int nb = ((Integer)parameters[0]).intValue();
			SignalListener listener = new IddleSignalListener();
			for(int i=0; i<nb; ++i) {
				addSignalListener(listener);
			}
			return super.activate(parameters);
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

}