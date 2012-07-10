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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.bench.api.AbstractOperationPerSecondCsvBench;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.util.random.RandomNumber;

/** Run the bench on the message API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractAgentMessageSendingBench extends AbstractOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractAgentMessageSendingBench(File directory) throws IOException {
		super(directory,
				"Agents", //$NON-NLS-1$
				"Messages per Second"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected org.janusproject.kernel.bench.api.AbstractOperationPerSecondCsvBench.BenchMarkedAgent launchAgents(
			OperationAgentNumberBenchRun run, Kernel kernel, AtomicInteger launchFlag) {
		List<AgentAddress> addr = new ArrayList<AgentAddress>();
		
		for(int i=0; i<run.getNumberOfAgents()-1; ++i) {
			addr.add(kernel.submitLightAgent(new IddleAgent(launchFlag, true)));
		}
		
		AgentAddress[] addresses = new AgentAddress[addr.size()];
		addr.toArray(addresses);
		addr.clear();
		
		int selectedReceiver = RandomNumber.nextInt(addresses.length);

		BenchMarkedAgent emitter;

		if (run.getName().startsWith("SendMessageToRandomReceiver")) { //$NON-NLS-1$
			emitter = new RandomEmitter(launchFlag, addresses);
		}
		else if (run.getName().startsWith("SendMessageToOneReceiver")) { //$NON-NLS-1$
			emitter = new OneEmitter(launchFlag, addresses[selectedReceiver]);
		}
		else {
			throw new IllegalStateException();
		}
				
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
	public void benchSendMessageToRandomReceiver() throws Exception {
		runBenchFor(getCurrentRun(), getBenchMarkedAgent());
	}
	
	/**
	 * @throws Exception
	 */
	public void benchSendMessageToOneReceiver() throws Exception {
		runBenchFor(getCurrentRun(), getBenchMarkedAgent());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class RandomEmitter extends BenchMarkedAgent {
		
		private static final long serialVersionUID = 8804935270327139156L;

		private AgentAddress[] addresses;
		
		/**
		 * @param flag
		 * @param addresses
		 */
		public RandomEmitter(AtomicInteger flag, AgentAddress[] addresses) {
			super(flag);
			this.addresses = addresses;
			setMailbox(BenchUtil.createMailboxForSendingBenchs());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean doBench() {
			sendMessage(new StringMessage(""), this.addresses); //$NON-NLS-1$
			return true;
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class OneEmitter extends BenchMarkedAgent {
		
		private static final long serialVersionUID = 8804935270327139156L;

		private AgentAddress[] address;
		
		/**
		 * @param flag
		 * @param address
		 */
		public OneEmitter(AtomicInteger flag, AgentAddress address) {
			super(flag);
			this.address = new AgentAddress[] { address };
			setMailbox(BenchUtil.createMailboxForSendingBenchs());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean doBench() {
			sendMessage(new StringMessage(""), this.address); //$NON-NLS-1$
			return true;
		}
		
	}

}