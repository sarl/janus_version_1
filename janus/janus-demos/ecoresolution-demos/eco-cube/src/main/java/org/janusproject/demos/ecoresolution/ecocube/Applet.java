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

import java.awt.BorderLayout;
import java.util.logging.Level;

import javax.swing.JApplet;

import org.janusproject.demos.ecoresolution.ecocube.agent.CubeAgent;
import org.janusproject.demos.ecoresolution.ecocube.agent.CubeEcoProblem;
import org.janusproject.demos.ecoresolution.ecocube.agent.GroundAgent;
import org.janusproject.demos.ecoresolution.ecocube.ui.CubeWorldPanel;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Applet extends JApplet {
	
	private static final long serialVersionUID = 5684223161212781274L;

	private CubeEcoProblem problem = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		super.init();

		LoggerUtil.setGlobalLevel(Level.OFF);
		
		Kernel kernel = Kernels.get();
		
		GroundAgent planeAgent = new GroundAgent();
		EcoIdentity table = planeAgent.getEcoIdentity();
		
		CubeAgent cubeA = new CubeAgent("A", table, table); //$NON-NLS-1$
		CubeAgent cubeB = new CubeAgent("B", cubeA.getEcoIdentity(), table); //$NON-NLS-1$
		CubeAgent cubeC = new CubeAgent("C", cubeB.getEcoIdentity(), table); //$NON-NLS-1$
		
		// Initialize the problem
		this.problem = new CubeEcoProblem(); // 3 cubes + 1 table are in the problem
		this.problem.addUpDownRelation(cubeA, planeAgent);
		this.problem.addUpDownRelation(cubeC, cubeA);
		this.problem.addUpDownRelation(cubeB, cubeC);

		// Window Content
		CubeWorldPanel panel = new CubeWorldPanel(kernel, planeAgent.getEcoIdentity(), 3);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		// Start solving
		if (this.problem!=null) {
			this.problem.solve(null);
			this.problem = null;
		}
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		Kernels.killAll();
		super.destroy();
	}
	
}