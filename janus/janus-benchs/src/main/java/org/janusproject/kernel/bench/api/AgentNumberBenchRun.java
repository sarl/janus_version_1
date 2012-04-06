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

/** This class describes a run of a bench
 * that is describing a number of agents to run.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class AgentNumberBenchRun extends BenchRun {

	private int nbAgents = 1;
	
	/**
	 * @param name
	 * @param nbAgents
	 */
	public AgentNumberBenchRun(String name, int nbAgents) {
		super(name);
		this.nbAgents = nbAgents;
	}
	
	/** Replies the name of the bench.
	 * 
	 * @return the name
	 */
	public int getNumberOfAgents() {
		return this.nbAgents;
	}
		
}