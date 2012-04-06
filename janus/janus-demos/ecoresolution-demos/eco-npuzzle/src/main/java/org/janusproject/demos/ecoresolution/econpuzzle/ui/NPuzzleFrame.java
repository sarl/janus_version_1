/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.econpuzzle.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.janusproject.kernel.Kernel;

/**
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class NPuzzleFrame extends JFrame {

	private static final long serialVersionUID = -6707839044970314635L;
	
	/**
	 * @param k is the kernel to inspect.
	 * @param gridSize 
	 */
	public NPuzzleFrame(Kernel k, int gridSize) {
		super("Eco NPuzzle"); //$NON-NLS-1$
		
		setLayout(new BorderLayout());
		
		NPuzzlePanel panel = new NPuzzlePanel(k, gridSize);
		
		add(panel, BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(800, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		pack();
	}
}
