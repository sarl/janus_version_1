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
package org.janusproject.kernel.bench.execution;

import java.io.File;
import java.io.IOException;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.bench.api.OperationAgentNumberBenchRun;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class HeavyAgentRoleExecutionBench extends AbstractRoleExecutionBench {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public HeavyAgentRoleExecutionBench(File directory) throws IOException {
		super(directory);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SizedIterator<OperationAgentNumberBenchRun> createIntervals(
			String benchFunctionName) throws Exception {
		return BenchUtil.makeOneHeavyManyLightAgentIntervals(
				OperationAgentNumberBenchRun.class,
				benchFunctionName,
				2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void launchRefereeAgent(Kernel kernel, Agent agent) {
		kernel.submitHeavyAgent(agent);
	}
	
}