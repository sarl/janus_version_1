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
package org.janusproject.kernel.bench;

/** Constants for the benchs.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BenchConstants {

	/**
	 *  Delay for the insertion of a message into the black hole mailbox.
	 */
	public static final int MAILBOX_INSERTION_DELAY = 0;
	
	/**
	 *  Delay for the read of a message from the black hole mailbox.
	 */
	public static final int MAILBOX_READ_DELAY = 0;

	/** Max memory in the virtual machine.
	 */
	public static final int MAX_MEMORY = 1024;
	
	/** Default number of tests in the same run
	 * when only one run should be executed.
	 */
	public static final int DEFAULT_TEST_NUMBER = 100000;

	/** Default number of runs.
	 */
	public static final int DEFAULT_RUN_NUMBER = 10;

	/** Number of runs when a bench must be reset
	 * between each benchmarked operation.
	 */
	public static final int RESETTING_RUN_NUMBER = 5000;

	/** Number of runs when the bench should measure
	 * number of operations per second.
	 */
	public static final int PER_SECOND_BENCH_RUN_NUMBER = 5;

	/** Sleeping duration when the bench should measure
	 * number of operations per second (in ms).
	 */
	public static final int PER_SECOND_BENCH_SLEEPING_DURATION = 2000;
	
	/** First point of the benchmarking intervals for messages.
	 */
	public static final int MESSAGE_INTERVAL_START = 0;

	/** Points of the benchmarking intervals for messages.
	 */
	public static final int[] MESSAGE_INTERVAL_SEGMENTS = new int[] {
		100, 10,				// [1; 100]
		1000, 50,				// ]100; 1000]
		10000, 500,				// ]1000; 10000]
	};

	/** First point of the benchmarking intervals for light agents.
	 */
	public static final int LIGHT_AGENT_INTERVAL_START = 0;

	/** Time to wait after initialization (in ms).
	 */
	public static final int INITIALIZATION_WAITING_TIME = 1000;

	/** Points of the benchmarking intervals for light agents.
	 */
	public static final int[] LIGHT_AGENT_INTERVAL_SEGMENTS = new int[] {
		100, 10,				// [1; 100]
		1000, 100,				// ]100; 1000]
		10000, 1000,			// ]1000; 10000]
		100000, 5000			// ]10000; 100000]
	};

	/** First point of the benchmarking intervals for heavy agents.
	 */
	public static final int HEAVY_AGENT_INTERVAL_START = 0;

	/** Points of the benchmarking intervals for heavy agents.
	 */
	public static final int[] HEAVY_AGENT_INTERVAL_SEGMENTS = new int[] {
		100, 20,				// [1; 100]
		1000, 100				// ]100; 1000]
	};
	
	/** First point of the benchmarking intervals for one heavy and many light agents.
	 */
	public static final int HEAVY_LIGHT_AGENT_INTERVAL_START = 0;

	/** Points of the benchmarking intervals for one heavy and many light agents.
	 */
	public static final int[] HEAVY_LIGHT_AGENT_INTERVAL_SEGMENTS = new int[] {
		100, 10,				// [1; 100]
		1000, 100,				// ]100; 1000]
		10000, 1000,			// ]1000; 10000]
		100000, 5000			// ]10000; 100000]
	};

}