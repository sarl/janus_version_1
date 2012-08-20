/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.ecocube;

import java.util.logging.Level;

import org.janusproject.demos.ecoresolution.ecocube.agent.CubeAgent;
import org.janusproject.demos.ecoresolution.ecocube.agent.CubeEcoProblem;
import org.janusproject.demos.ecoresolution.ecocube.agent.GroundAgent;
import org.janusproject.demos.ecoresolution.ecocube.ui.CubeWorldFrame;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/** CUBE PROBLEM WITH ECO-RESOLUTION.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {

	/**
	 * @param argv
	 * @throws Exception
	 */	
	public static void main(String[] argv) throws Exception {
		LoggerUtil.setGlobalLevel(Level.WARNING);
		
		Kernel kernel = Kernels.get();
		
		GroundAgent planeAgent = new GroundAgent();
		EcoIdentity table = planeAgent.getEcoIdentity();
		
		CubeAgent cubeA = new CubeAgent("A", table, table); //$NON-NLS-1$
		CubeAgent cubeB = new CubeAgent("B", cubeA.getEcoIdentity(), table); //$NON-NLS-1$
		CubeAgent cubeC = new CubeAgent("C", cubeB.getEcoIdentity(), table); //$NON-NLS-1$
		
		// Initialize the problem
		CubeEcoProblem problem = new CubeEcoProblem(); // 3 cubes + 1 table are in the problem
		problem.addUpDownRelation(cubeA, planeAgent);
		problem.addUpDownRelation(cubeC, cubeA);
		problem.addUpDownRelation(cubeB, cubeC);

		// Window
		CubeWorldFrame frame = new CubeWorldFrame(kernel, planeAgent.getEcoIdentity(), 3);
		frame.setVisible(true);
		
		// Start solving
		problem.solve(null);
	}

}