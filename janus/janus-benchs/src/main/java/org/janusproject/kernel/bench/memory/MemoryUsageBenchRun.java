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

import org.janusproject.kernel.bench.api.AgentNumberBenchRun;

/** This class describes a run of a bench
 * that is describing a number of agents to run.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class MemoryUsageBenchRun extends AgentNumberBenchRun {

	private long allocatedMemory = 0;
	private long freeMemory = 0;
	
	/**
	 * @param name
	 * @param nbAgents
	 */
	public MemoryUsageBenchRun(String name, int nbAgents) {
		super(name, nbAgents);
	}
	
	/** Replies the allocated memory.
	 * 
	 * @return the allocated memory
	 */
	public long getAllocatedMemory() {
		return this.allocatedMemory;
	}
		
	/** Set the allocated memory.
	 * 
	 * @param mem is the allocated memory
	 */
	public void setAllocatedMemory(long mem) {
		this.allocatedMemory = mem;
	}

	/** Replies the free memory.
	 * 
	 * @return the free memory
	 */
	public long getFreeMemory() {
		return this.freeMemory;
	}
		
	/** Set the free memory.
	 * 
	 * @param mem is the free memory
	 */
	public void setFreeMemory(long mem) {
		this.freeMemory = mem;
	}

}