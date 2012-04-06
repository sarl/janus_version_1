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
package org.janusproject.kernel.bench.mailbox;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.UUID;

import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.bench.api.AgentNumberBenchRun;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.CsvBench;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.CreationDateMessageComparator;
import org.janusproject.kernel.message.FirstArrivedMessageComparator;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** Run the bench on the mailbox API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractMailboxInsertionBench extends CsvBench<AgentNumberBenchRun> {

	private Mailbox mailbox = null;
	
	/**
	 * @param directory
	 * @throws IOException
	 */
	public AbstractMailboxInsertionBench(File directory) throws IOException {
		super(directory);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<AgentNumberBenchRun> setUpGroupWithCSV(String benchFunctionName) throws Exception {
		writeHeader("Name", "Messages", "Tests", "Run (ns)", "Operation Duration (ns)", "Standard Deviation", "OS Load Average"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		return BenchUtil.makeMessageIntervals(
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

		run.setTimeScalingFactor(.5f);

		Comparator<Message> comparator;
		if (run.getName().startsWith("DateInsertion")) { //$NON-NLS-1$
			comparator = CreationDateMessageComparator.SINGLETON;
		}
		else if (run.getName().startsWith("LastInsertion")) { //$NON-NLS-1$
			comparator = FirstArrivedMessageComparator.SINGLETON;
		}
		else {
			throw new IllegalStateException();
		}
		
		this.mailbox = createMailbox(comparator);

		for(int i=0; i<run.getNumberOfAgents(); ++i) {
			this.mailbox.add(new StringMessage(UUID.randomUUID().toString()));
		}
		
	}
	
	/** Create a mailbox to bench.
	 * 
	 * @param comparator
	 * @return a mailbox.
	 */
	protected abstract Mailbox createMailbox(Comparator<? super Message> comparator);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownUnitaryBench(AgentNumberBenchRun run) throws Exception {
		Kernels.killAll();
		this.mailbox.clear();
		this.mailbox = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownMultiRunBench(int nbRuns, AgentNumberBenchRun run)
			throws Exception {
		writeRecord(
				run.getName(),
				run.getNumberOfAgents(), 
				getNumberOfTests(),
				run.getRunDuration(),
				run.getTestAverageDuration(),
				run.getTestStandardDeviation(),
				getSystemLoadAverage());
		super.tearDownMultiRunBench(nbRuns, run);
	}
	
	/**
	 */
	public void benchDateInsertion() {
		StringMessage m = new StringMessage(UUID.randomUUID().toString());
		this.mailbox.add(m);
		this.mailbox.remove(m);
	}
	
	/**
	 */
	public void benchLastInsertion() {
		StringMessage m = new StringMessage(UUID.randomUUID().toString());
		this.mailbox.add(m);
		this.mailbox.remove(m);
	}

}