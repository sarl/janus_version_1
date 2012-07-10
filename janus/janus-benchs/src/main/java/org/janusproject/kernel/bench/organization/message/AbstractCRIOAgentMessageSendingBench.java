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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.bench.api.AbstractRoleOperationPerSecondCsvBench;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.random.RandomNumber;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** Run the bench on the message API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractCRIOAgentMessageSendingBench extends AbstractRoleOperationPerSecondCsvBench<OperationAgentNumberBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractCRIOAgentMessageSendingBench(File directory) throws IOException {
		super(directory,
				"Players", //$NON-NLS-1$
				"Messages per Second"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SizedIterator<OperationAgentNumberBenchRun> createIntervals(String benchFunctionName) throws Exception {
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
		List<AgentAddress> addr = new ArrayList<AgentAddress>();
		
		for(int i=0; i<run.getNumberOfAgents()-1; ++i) {
			addr.add(kernel.submitLightAgent(new IddleAgent(launchFlag, BenchOrganization.class, true)));
		}
		
		AgentAddress[] addresses = new AgentAddress[addr.size()];
		addr.toArray(addresses);
		addr.clear();
		
		BenchMarkedAgent emitter;
		
		if (run.getName().startsWith("SendMessageToRandomReceiver")) { //$NON-NLS-1$
			emitter = new BenchMarkedAgent(launchFlag, BenchOrganization.class, RandomEmitter.class, false);
		}
		else if (run.getName().startsWith("SendMessageToOneReceiver")) { //$NON-NLS-1$
			int selectedReceiver = RandomNumber.nextInt(addresses.length);
			emitter = new BenchMarkedAgent(launchFlag, BenchOrganization.class, OneEmitter.class, false, addresses[selectedReceiver]);
		}
		else {
			throw new IllegalStateException();
		}
				
		addresses = null;

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
		runBenchFor(getCurrentRun(), getBenchMarkedRole());
	}
	
	/**
	 * @throws Exception
	 */
	public void benchSendMessageToOneReceiver() throws Exception {
		runBenchFor(getCurrentRun(), getBenchMarkedRole());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class RandomEmitter extends BenchMarkedRole {

		/**
		 */
		public RandomEmitter() {
			super();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... params) {
			super.activate(params);
			setMailbox(BenchUtil.createMailboxForSendingBenchs());
			return null;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doBench() {
			sendMessage(IddleRole.class, new StringMessage("")); //$NON-NLS-1$
			return true;
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class OneEmitter extends BenchMarkedRole {

		private AgentAddress address = null;
		private RoleAddress roleAddress = null;

		/**
		 */
		public OneEmitter() {
			super();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Status activate(Object... params) {
			setMailbox(BenchUtil.createMailboxForSendingBenchs());
			this.address = (AgentAddress)params[0];
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean doBench() {
			if (this.roleAddress==null)
				this.roleAddress = sendMessage(IddleRole.class, this.address, new StringMessage("")); //$NON-NLS-1$
			else
				sendMessage(this.roleAddress, new StringMessage("")); //$NON-NLS-1$
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
			addRole(RandomEmitter.class);
			addRole(OneEmitter.class);
			addRole(IddleRole.class);
		}
		
	}

}