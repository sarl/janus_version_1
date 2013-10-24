/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.ecocube.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.kernel.Kernel;

/** Frame to display the cube world.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class CubeWorldFrame extends JFrame {

	private static final long serialVersionUID = 2084465788358672099L;

	/**
	 * @param k is the kernel to inspect.
	 * @param planeEntity is the entity which is the plane.
	 * @param cubeCount is the number of cubes in the problem.
	 */
	public CubeWorldFrame(Kernel k, EcoIdentity planeEntity, int cubeCount) {
		super(Locale.getString(CubeWorldFrame.class, "TITLE")); //$NON-NLS-1$
		
		setLayout(new BorderLayout());
		
		CubeWorldPanel panel = new CubeWorldPanel(k, planeEntity, cubeCount);
		
		add(panel, BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(800, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		pack();
	}
	
}