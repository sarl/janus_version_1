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

/** This class describes a run of a bench.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BenchRun {

	private String name;
	private long durationPerTest = -1;
	private long runDuration = -1;
	private long testStandardDeviation = 0;
	private float timeScalingFactor = 1f;
	private long timeIncrement = 0;
	
	/**
	 * @param name
	 */
	public BenchRun(String name) {
		this.name = name;
	}
	
	/** Replies the name of the bench.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/** Set the name of the bench.
	 * 
	 * @param name is the name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** Replies the duration of the bench run with
	 * all the tests.
	 * @return the nano time, or {@code -1} if the
	 * duration is unknown.
	 */
	public long getRunDuration() {
		return this.runDuration;
	}
	
	/** Replies the average duration of one test of 
	 * the bench run.
	 * @return the nano time, or {@code -1} if the
	 * duration is unknown.
	 */
	public long getTestAverageDuration() {
		return this.durationPerTest;
	}
	
	/** Replies the standard deviation of one unit operation.
	 * 
	 * @return the standard deviation.
	 */
	public long getTestStandardDeviation() {
		return this.testStandardDeviation;
	}

	/** Set the duration of the bench.
	 * 
	 * @param runDuration is the duration of the complete run.
	 * @param testDuration is the average duration of one test in the run.
	 * @param testStandardDeviation is the standard deviation of the complete run.
	 */
	void setDurations(long runDuration, long testDuration, long testStandardDeviation) {
		this.runDuration = runDuration;
		this.durationPerTest = testDuration;
		this.testStandardDeviation = testStandardDeviation;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return (this.name==null || this.name.isEmpty()) ? super.toString() : this.name;
	}
	
	/** Replies the factor to apply to the measured time after all the tests
	 * were run.
	 * 
	 * @return the time scaling factor.
	 */
	public float getTimeScalingFactor() {
		return this.timeScalingFactor;
	}
	
	/** Set the factor to apply to the measured time after all the tests
	 * were run.
	 * 
	 * @param factor is the time scaling factor.
	 */
	public void setTimeScalingFactor(float factor) {
		this.timeScalingFactor = factor;
	}

	/** Replies the a time to add to the measured duration, after its scaling.
	 * 
	 * @return the time scaling factor.
	 */
	public long getTimeIncrement() {
		return this.timeIncrement;
	}
	
	/** Set the a time to add to the measured duration, after its scaling.
	 * 
	 * @param increment is the time scaling factor.
	 */
	public void setTimeIncrement(long increment) {
		this.timeIncrement = increment;
	}

}