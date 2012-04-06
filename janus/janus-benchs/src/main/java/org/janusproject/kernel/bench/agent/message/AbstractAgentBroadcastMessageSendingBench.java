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
package org.janusproject.kernel.bench.agent.message;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.bench.api.AbstractOperationPerSecondCsvBench;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** Run the bench on the message API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractAgentBroadcastMessageSendingBench extends AbstractOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractAgentBroadcastMessageSendingBench(File directory) throws IOException {
		super(directory,
				"Agents", //$NON-NLS-1$
				"Messages per Second"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SizedIterator<OperationAgentNumberBenchRun> createIntervals(
			String benchFunctionName) throws Exception {
		return BenchUtil.makeAllHeavyAgentIntervals(
				OperationAgentNumberBenchRun.class,
				benchFunctionName,
				2);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected org.janusproject.kernel.bench.api.AbstractOperationPerSecondCsvBench.BenchMarkedAgent launchAgents(
			OperationAgentNumberBenchRun run, Kernel kernel,
			AtomicInteger launchFlag) {
		for(int i=0; i<run.getNumberOfAgents()-1; ++i) {
			kernel.submitLightAgent(new IddleAgent(launchFlag, true));
		}
		
		Emitter emitter = new Emitter(launchFlag);
		launchEmitter(kernel, emitter);
		
		return emitter;
	}
	
	/** Launch the emitter of the messages.
	 * 
	 * @param kernel
	 * @param agent
	 */
	protected abstract void launchEmitter(Kernel kernel, Agent agent);
		
	/**
	 * @throws Exception
	 */
	public void benchBroadcastMessage() throws Exception {
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
			setMailbox(BenchUtil.createMailboxForSendingBenchs());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean doBench() {
			broadcastMessage(new StringMessage("")); //$NON-NLS-1$
			return true;
		}
		
	}

}