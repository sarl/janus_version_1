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

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.kernel.bench.BenchConstants;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** This abstract class describes a bench for the Janus kernel.
 * <p>
 * The subclasses of {@code Bench} must implements functions with
 * the prefix "bench" in their name. The function {@link #runBenchs(float, float)}
 * retreives there functions with reflection mechanism and run them.
 * The function {@link #setUpUnitaryBench(BenchRun)} and {@link #tearDownUnitaryBench(BenchRun)} are invoked
 * before and after each invocation of a "bench" function.
 * 
 * @param <R> is the type of the runs
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class Bench<R extends BenchRun> {

	private static final String PREFIX = "bench"; //$NON-NLS-1$
	
	private int nbTests = BenchConstants.DEFAULT_TEST_NUMBER;
	private int nbRuns = BenchConstants.DEFAULT_RUN_NUMBER;
	
	private final List<R> runs = new LinkedList<R>();
	
	private R currentRun = null;
	
	private final OperatingSystemMXBean osBean;
	
	/**
	 */
	protected Bench() {
		this.osBean = ManagementFactory.getOperatingSystemMXBean();
	}
	
	/**
	 * Returns the system load average for the last minute. The system load
	 * average is the sum of the number of runnable entities queued to the
	 * available processors and the number of runnable entities running on
	 * the available processors averaged over a period of time. The way in
	 * which the load average is calculated is operating system specific but
	 * is typically a damped time-dependent average.
	 * <p>
	 * If the load average is not available, a zero value is returned.
	 * <p>
	 * This method is designed to provide a hint about the system load and
	 * may be queried frequently. The load average may be unavailable on
	 * some platform where it is expensive to implement this method.
	 * 
	 * @return the system load average.
	 */
	public double getSystemLoadAverage() {
		return Math.max(0, this.osBean.getSystemLoadAverage());
	}
	
	/**
	 * Returns the number of processors available to the Java virtual machine.
     * This method is equivalent to the {@link Runtime#availableProcessors()}
     * method.
     * <p> This value may change during a particular invocation of
     * the virtual machine.
     *
     * @return  the number of processors available to the virtual
     *          machine; never smaller than one.
	 */
	public double getAvailableProcessors() {
		return this.osBean.getAvailableProcessors();
	}

	// Invoked by reflection
	@SuppressWarnings("unused")
	private void doIddle() {
		//
	}
	
	/** Replies the current run.
	 * 
	 * @return the current run.
	 */
	public R getCurrentRun() {
		return this.currentRun;
	}
	
	private void setCurrentRun(R run) {
		this.currentRun = run;
	}
	
	/** Set the number of tests to run for each benchmark.
	 * Each benchmark function is invoked the number of times
	 * given by the parameter. This enable us to obtain
	 * better time consumption computation.
	 * 
	 * @param nbTests
	 */
	protected void setNumberOfTests(int nbTests) {
		if (nbTests>0) {
			this.nbTests = nbTests;
		}
	}
	
	/** Set the number of tests to run for each benchmark.
	 * Each benchmark function is invoked the number of times
	 * given by the parameter. This enable us to obtain
	 * better time consumption computation.
	 * 
	 * @return the number of tests to run for each benchmark.
	 */
	protected int getNumberOfTests() {
		return this.nbTests;
	}

	/** Replies the number of times the bench was initialized, run and
	 * disposed.
	 * 
	 * @return the number of runs
	 */
	protected int getNumberOfRuns() {
		return this.nbRuns;
	}

	/** Set the number of times the bench was initialized, run and
	 * disposed.
	 * 
	 * @param nbRuns
	 */
	protected void setNumberOfRuns(int nbRuns) {
		if (nbRuns>0) {
			this.nbRuns = nbRuns;
		}
	}
	
	/** Invoked when the bench class is starting.
	 * @throws Exception
	 */
	public void initialize() throws Exception {
		//
	}
	
	/** Invoked when the bench class should be disposed.
	 * @throws Exception
	 */
	public void dispose() throws Exception {
		//
	}
	
	/** Invoked to initialize a group of benchs.
	 * 
	 * @param benchFunctionName is the name of the bench function
	 * @return the iterator on the description of the benchs to run.
	 * @throws Exception
	 */
	public abstract SizedIterator<R> setUpGroup(String benchFunctionName) throws Exception;

	/** Invoked to initialize one run of a bench.
	 * 
	 * @param run is the description of the run of the bench to execute. 
	 * @throws Exception
	 */
	public void setUpUnitaryBench(R run) throws Exception {
		//
	}

	/** Invoked to initialize a bench when it is multi-run-based.
	 * 
	 * @param run is the description of the run of the bench to execute. 
	 * @throws Exception
	 */
	public void setUpMultiRunBench(R run) throws Exception {
		//
	}

	/** Invoked to close one run of a bench.
	 * 
	 * @param run is the description of the bench to shut down.
	 * @throws Exception
	 */
	public void tearDownUnitaryBench(R run) throws Exception {
		this.runs.add(run);
	}
	
	/** Invoked to close a bench when it is multirun.
	 * 
	 * @param nbRuns is the number of runs.
	 * @param run is the description of the bench to shut down.
	 * @throws Exception
	 */
	public void tearDownMultiRunBench(int nbRuns, R run) throws Exception {
		this.runs.add(run);
	}

	/** Invoked to close a group of benchs.
	 * 
	 * @throws Exception
	 */
	public void tearDownGroup() throws Exception {
		//
	}
	
	/** Replies the terminated runs.
	 * 
	 * @return the terminated runs.
	 */
	protected List<R> getTerminatedRuns() {
		return this.runs;
	}
	
	private String formatPercentage(float v) {
		StringBuilder sb = new StringBuilder();
		sb.append((int)v);
		while (sb.length()<3) {
			sb.insert(0, ' ');
		}
		return sb.toString();
	}

	/** Run all the benchs of the class.
	 * 
	 * @param taskStart is the first percentage dedicated to this task.
	 * @param taskSize is the amount of the progression dedicated to this run
	 * @throws Exception
	 */
	public final void runBenchs(float taskStart, float taskSize) throws Exception {
		// Retreive the hierarchy of classes.
		List<Class<?>> classes = new LinkedList<Class<?>>();
		{
			Class<?> type = getClass();
			while (type!=null && !type.equals(Bench.class)) {
				classes.add(0, type);
				type = type.getSuperclass();
			}
		}
		
		Logger logger = Logger.getAnonymousLogger();
		
		initialize();
		
		List<Method> benchMethods = new ArrayList<Method>(); 
		for(Class<?> type : classes) {
			for(Method method : type.getDeclaredMethods()) {
				if (method.getName().startsWith(PREFIX)) {
					benchMethods.add(method);
				}
			}
		}
		
		float taskStep = taskSize / benchMethods.size();
		float taskValue = taskStart;
		String task = formatPercentage(taskValue);
		
		
		// Run the "bench" functions for each class
		for(Method method : benchMethods) {
			String groupName = method.getName().substring(PREFIX.length());
			try {
				long startTime, endTime;
				int nbTests = getNumberOfTests();
				int nbRuns = getNumberOfRuns();
				
				logger.info(Locale.getString("START_GROUP", task, getClass().getSimpleName(), groupName)); //$NON-NLS-1$
				this.runs.clear();
				SizedIterator<R> runIterator = setUpGroup(groupName);
				
				float subTaskValue = taskValue;
				float subTaskStep = taskStep / runIterator.totalSize();
				
				while (runIterator.hasNext()) {
					R run = runIterator.next();
					setCurrentRun(run);
					long runDuration = 0;
					if (nbRuns>1) {
						setUpMultiRunBench(run);
					}
					long[] measurements = new long[nbRuns];
					
					float subsubTaskValue = subTaskValue;
					float subsubTaskStep = subTaskStep / nbRuns;
					task = formatPercentage(subsubTaskValue);
					
					for(int idxRun=0; idxRun<nbRuns; ++idxRun) {
						
						logger.info(Locale.getString("PREPARE_BENCH", task, idxRun+1, nbRuns, getClass().getSimpleName(), run.toString(), BenchConstants.INITIALIZATION_WAITING_TIME/1000)); //$NON-NLS-1$
						setUpUnitaryBench(run);
						if (BenchConstants.INITIALIZATION_WAITING_TIME>0) {
							Thread.sleep(BenchConstants.INITIALIZATION_WAITING_TIME);
						}
						logger.info(Locale.getString("START_BENCH", task, idxRun+1, nbRuns, getClass().getSimpleName(), run.toString())); //$NON-NLS-1$
						startTime = System.nanoTime();
						for(int i=0; i<nbTests; ++i) {
							method.invoke(this);
						}
						endTime = System.nanoTime();
						measurements[idxRun] = Math.max(0,
								(long)(run.getTimeScalingFactor()*(endTime - startTime))
								+run.getTimeIncrement());
						runDuration += measurements[idxRun];
						measurements[idxRun] = measurements[idxRun] / nbTests;
						if (nbRuns==1) {
							run.setDurations(
									runDuration,
									measurements[idxRun],
									0);
						}
						tearDownUnitaryBench(run);
						subsubTaskValue += subsubTaskStep;
						task = formatPercentage(subsubTaskValue);
						logger.info(Locale.getString("END_BENCH", task, idxRun+1, nbRuns, getClass().getSimpleName(), run.toString())); //$NON-NLS-1$
					}
					{
						double runAverage = (double)runDuration/nbRuns;
						double testAverage = 0.;
						if (measurements.length>0) {
							for(long x : measurements) {
								testAverage += x;
							}
							testAverage /= measurements.length;
						}
						long stdDev = 0;
						if (measurements.length>0) {
							for(long x : measurements) {
								stdDev += (x-testAverage)*(x-testAverage);
							}
							stdDev /= measurements.length;
							stdDev = Math.round(Math.sqrt(stdDev));
						}
						run.setDurations(
								(long)runAverage,
								(long)testAverage,
								stdDev);
					}
					if (nbRuns>1) {
						tearDownMultiRunBench(nbRuns, run);
					}
					setCurrentRun(null);
					run = null;
					for(int i=0; i<6; ++i) {
						System.gc();
					}
					subTaskValue += subTaskStep;
				}
			}
			catch(Throwable e) {
				logError(logger, groupName, e);
			}

			tearDownGroup();
			this.runs.clear();

			taskValue += taskStep;
			task = formatPercentage(taskValue);
			logger.info(Locale.getString("END_GROUP", task, getClass().getSimpleName(), groupName)); //$NON-NLS-1$
			
		}
		
		dispose();
	}
	
	/** Log an error.
	 * 
	 * @param logger the default logger.
	 * @param benchFunctionName is the name of the group of benchs that have failed.
	 * @param e is the error to log.
	 */
	protected void logError(Logger logger, String benchFunctionName, Throwable e) {
		logger.log(Level.SEVERE, e.toString(), e);
	}
	
}