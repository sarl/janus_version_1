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

import java.util.ArrayList;
import java.util.List;

/** This class describes a run of a bench
 * that is describing a number of agents to run.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class OperationAgentNumberBenchRun extends AgentNumberBenchRun {

	private List<Long> nbOperations = new ArrayList<Long>();
	private long standardDeviation = -1;
	private long means = -1;
	
	/**
	 * @param name
	 * @param nbAgents
	 */
	public OperationAgentNumberBenchRun(String name, int nbAgents) {
		super(name, nbAgents);
	}
	
	/** Replies the number of operations of the bench.
	 * 
	 * @return the number of operations
	 */
	public long getNumberOfOperations() {
		if (this.means==-1 && !this.nbOperations.isEmpty()) {
			this.means = 0;
			for(Long l : this.nbOperations) {
				this.means += l;
			}
			this.means /= this.nbOperations.size();
		}
		return this.means;
	}
		
	/** Set the number of operations of the bench.
	 * 
	 * @param nb is the number of operations
	 */
	public void addNumberOfOperations(long nb) {
		this.nbOperations.add(nb);
		this.means = -1;
		this.standardDeviation = -1;
	}
	
	/** Clear the number of operations of the bench.
	 */
	public void clearNumberOfOperations() {
		this.nbOperations.clear();
		this.means = -1;
		this.standardDeviation = -1;
	}
		
	/** Replies the standard deviation for the number of operations of the bench.
	 * 
	 * @return the number of operations
	 */
	public long getNumberOfOperationStandardDeviation() {
		if (this.standardDeviation==-1) {
			this.standardDeviation = 0;
			long m = getNumberOfOperations();
			if (!this.nbOperations.isEmpty()) {
				for(long x : this.nbOperations) {
					this.standardDeviation += (x-m)*(x-m);
				}
				this.standardDeviation /= this.nbOperations.size();
				this.standardDeviation = Math.round(Math.sqrt(this.standardDeviation));
			}
		}
		return this.standardDeviation;
	}
		
	/** Set the standard deviation for the number of operations of the bench.
	 * 
	 * @param stdDev is the standard deviation for the number of operations
	 */
	public void setNumberOfOperationsStandardDeviation(long stdDev) {
		this.standardDeviation = stdDev;
	}

}