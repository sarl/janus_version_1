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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.janusproject.kernel.bench.BenchConstants;
import org.janusproject.kernel.mailbox.BlackHoleMailbox;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.util.sizediterator.SizedIterator;
import org.janusproject.kernel.util.sizediterator.UnmodifiableCollectionSizedIterator;

/** Utilities for benchmarking.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BenchUtil {

	/** Create a collection of bench run in which the number of agents
	 * is evolving.
	 * 
	 * @param name is the name of the runs.
	 * @param start is the minimum number of agents.
	 * @param end is the maximum number of agents.
	 * @param step is the number of agents between two consecutive runs.
	 * @return the runs.
	 */
	public static SizedIterator<AgentNumberBenchRun> makeRuns(String name, int start, int end, int step) {
		List<AgentNumberBenchRun> list = new ArrayList<AgentNumberBenchRun>();
		for(int i=start; i<=end; i+=step) {
			list.add(new AgentNumberBenchRun(name+"#"+i, i)); //$NON-NLS-1$
		}
		return new UnmodifiableCollectionSizedIterator<AgentNumberBenchRun>(list);
	}

	/** Create a collection of bench run in which the number of agents
	 * is evolving.
	 * 
	 * @param name is the name of the runs.
	 * @param start is the minimum number of agents.
	 * @param intervals are the interval definition composed of the max number of agents in the interval
	 * and the step in this interval.
	 * @return the runs.
	 * @throws Exception
	 */
	public static SizedIterator<AgentNumberBenchRun> makeRunsPerInterval(String name, int start, int... intervals) throws Exception {
		return makeRunsPerInterval(AgentNumberBenchRun.class, name, start, intervals);
	}

	/** Create a collection of bench run in which the number of agents
	 * is evolving.
	 * 
	 * @param type is the type of the run to create.
	 * @param name is the name of the runs.
	 * @param start is the minimum number of agents.
	 * @param intervals are the interval definition composed of the max number of agents in the interval
	 * and the step in this interval.
	 * @return the runs.
	 * @throws Exception
	 */
	public static <RR extends AgentNumberBenchRun> SizedIterator<RR> makeRunsPerInterval(Class<RR> type, String name, int start, int... intervals) throws Exception {
		List<RR> list = new ArrayList<RR>();
		int s = start;
		Constructor<RR> cons = type.getConstructor(String.class, int.class);
		for(int i=0; i<intervals.length; i+=2) {
			int e = intervals[i];
			int step = intervals[i+1];
			if (i>0) s += step;
			for(int j=s; j<=e; j+=step) {
				if (j>=1) {
					list.add(type.cast(cons.newInstance(name+"#"+j, j))); //$NON-NLS-1$
				}
				else if (j+step>1) {
					list.add(type.cast(cons.newInstance(name+"#"+1, 1))); //$NON-NLS-1$
				}
			}
			s = e;
		}
		return new UnmodifiableCollectionSizedIterator<RR>(list);
	}
	
	/** Create a collection of bench run in which the number of agents
	 * is evolving when these agents are light agents.
	 * 
	 * @param type is the type of the run to create.
	 * @param name is the name of the runs.
	 * @param minNumber is the minium number of agents.
	 * @return the runs.
	 * @throws Exception
	 */
	public static <RR extends AgentNumberBenchRun> SizedIterator<RR> makeAllLightAgentIntervals(Class<RR> type, String name, int minNumber) throws Exception {
		return makeRunsPerInterval(
				type,
				name,
				start(
						BenchConstants.LIGHT_AGENT_INTERVAL_START,
						BenchConstants.LIGHT_AGENT_INTERVAL_SEGMENTS,
						minNumber),
				BenchConstants.LIGHT_AGENT_INTERVAL_SEGMENTS);
	}

	/** Create a collection of bench run in which the number of agents
	 * is evolving when these agents are heavy agents.
	 * 
	 * @param type is the type of the run to create.
	 * @param name is the name of the runs.
	 * @param minNumber is the minium number of agents.
	 * @return the runs.
	 * @throws Exception
	 */
	public static <RR extends AgentNumberBenchRun> SizedIterator<RR> makeAllHeavyAgentIntervals(Class<RR> type, String name, int minNumber) throws Exception {
		return makeRunsPerInterval(
				type,
				name,
				start(
						BenchConstants.HEAVY_AGENT_INTERVAL_START,
						BenchConstants.HEAVY_AGENT_INTERVAL_SEGMENTS,
						minNumber),
				BenchConstants.HEAVY_AGENT_INTERVAL_SEGMENTS);
	}

	/** Create a collection of bench run in which the number of agents
	 * is evolving when these agents are heavy agents.
	 * 
	 * @param type is the type of the run to create.
	 * @param name is the name of the runs.
	 * @param minNumber is the minium number of agents.
	 * @return the runs.
	 * @throws Exception
	 */
	public static <RR extends AgentNumberBenchRun> SizedIterator<RR> makeOneHeavyManyLightAgentIntervals(Class<RR> type, String name, int minNumber) throws Exception {
		return makeRunsPerInterval(
				type,
				name,
				start(
						BenchConstants.HEAVY_LIGHT_AGENT_INTERVAL_START,
						BenchConstants.HEAVY_LIGHT_AGENT_INTERVAL_SEGMENTS,
						minNumber),
				BenchConstants.HEAVY_LIGHT_AGENT_INTERVAL_SEGMENTS);
	}
	
	private static int start(int start, int[] segments, int minNumber) {
		if (start<minNumber) {
			return start + segments[1];
		}
		return start;
	}
	
	/** 
	 * Create a mailbox dedicated to the sending benchmarks.
	 * 
	 * @return the mailbox.
	 */
	public static Mailbox createMailboxForSendingBenchs() {
		return new BlackHoleMailbox(BenchConstants.MAILBOX_INSERTION_DELAY, 0, 0);
	}

	/** 
	 * Create a mailbox dedicated to the sending benchmarks.
	 * 
	 * @return the mailbox.
	 */
	public static Mailbox createMailboxForReadingBenchs() {
		return new BlackHoleMailbox(0, 0, BenchConstants.MAILBOX_READ_DELAY);
	}

	/** Create a collection of bench run in which the number of messages
	 * is evolving.
	 * 
	 * @param type is the type of the run to create.
	 * @param name is the name of the runs.
	 * @param minNumber is the minium number of agents.
	 * @return the runs.
	 * @throws Exception
	 */
	public static <RR extends AgentNumberBenchRun> SizedIterator<RR> makeMessageIntervals(Class<RR> type, String name, int minNumber) throws Exception {
		return makeRunsPerInterval(
				type,
				name,
				start(
						BenchConstants.MESSAGE_INTERVAL_START,
						BenchConstants.MESSAGE_INTERVAL_SEGMENTS,
						minNumber),
				BenchConstants.MESSAGE_INTERVAL_SEGMENTS);
	}

}