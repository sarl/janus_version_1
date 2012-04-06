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
package org.janusproject.kernel.bench.memory;

import java.io.File;
import java.io.IOException;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.CsvBench;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** Run the bench on the execution API.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class HeavyAgentMemoryUsageBench extends CsvBench<MemoryUsageBenchRun> {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public HeavyAgentMemoryUsageBench(File directory) throws IOException {
		super(directory);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<MemoryUsageBenchRun> setUpGroupWithCSV(String benchFunctionName) throws Exception {
		writeHeader("Name", "Agents", "Tests", "Run (ns)", "Unit (ns)", "Allocated Memory (Bytes)", "Free Memory (Bytes)", "OS Load Average"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		return BenchUtil.makeAllHeavyAgentIntervals(
				MemoryUsageBenchRun.class,
				benchFunctionName,
				1);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUpUnitaryBench(MemoryUsageBenchRun run) throws Exception {
		super.setUpUnitaryBench(run);
		Kernels.killAll();
		LoggerUtil.setLoggingEnable(false);
		Kernel kernel = Kernels.create();
		
		for(int i=0; i<getCurrentRun().getNumberOfAgents(); ++i) {
			kernel.launchHeavyAgent(new IddleAgent());
		}

		Thread.sleep(1000);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDownUnitaryBench(MemoryUsageBenchRun run) throws Exception {
		Kernels.killAll();
		writeRecord(
				run.getName(),
				run.getNumberOfAgents(), 
				getNumberOfTests(),
				run.getRunDuration(),
				run.getTestAverageDuration(),
				run.getAllocatedMemory(),
				run.getFreeMemory(),
				getSystemLoadAverage());
	}
	
	/**
	 * @throws Exception
	 */
	public void benchUsage() throws Exception {
		Runtime runtime = Runtime.getRuntime();
		for(int i=0; i<6; ++i) {
			runtime.gc();
		}
		getCurrentRun().setFreeMemory(runtime.freeMemory());
		getCurrentRun().setAllocatedMemory(runtime.totalMemory() - runtime.freeMemory());
	}

	/** Replies the memory use by Java objects
	 * in bytes.
	 * <p>
	 * used memory <= allocated memory <= max memory.<br>
	 * free memory <= allocated memory <= max memory.
	 * 
	 * @return the count of used bytes.
	 */
	public static long getMemoryUse() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();    	
	}
	
	/** Replies the total amount of free memory in bytes.
	 * <p>
	 * used memory <= allocated memory <= max memory.<br>
	 * free memory <= allocated memory <= max memory.
	 * 
	 * @return the amount of free memory.
	 */
	public static long getTotalFreeMemory() {
		return  Runtime.getRuntime().freeMemory()
				+Runtime.getRuntime().maxMemory()
				-Runtime.getRuntime().totalMemory();    	
	}
	
}