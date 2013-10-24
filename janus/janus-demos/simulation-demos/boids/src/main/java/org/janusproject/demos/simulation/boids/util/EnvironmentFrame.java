/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010, 2012 Janus Core Developers
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
package org.janusproject.demos.simulation.boids.util;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.agent.Kernels;

/**
 * Graphic User Interface for the Boid demo.
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EnvironmentFrame extends JFrame {
	
	private static final long serialVersionUID = -2684990348513390396L;
	
	private final EnvironmentGUI internalPanel;
	
	/**
	 * 
	 * @param width of the world
	 * @param height of the world
	 * @param provider is providing boid states.
	 */
	public EnvironmentFrame(int width, int height, BodyStateProvider provider) {
		super();

		this.internalPanel = new EnvironmentGUI(width, height, provider);
		
		setTitle(Locale.getString(EnvironmentFrame.class, "TITLE")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(this.internalPanel, BorderLayout.CENTER);

		addWindowListener(new Closer());
		
		pack();
	}
	
	/**
	 * 
	 * @param gui is the panel to display.
	 */
	public EnvironmentFrame(EnvironmentGUI gui) {
		super();

		this.internalPanel = gui;
		
		setTitle(Locale.getString(EnvironmentFrame.class, "TITLE")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(this.internalPanel, BorderLayout.CENTER);

		addWindowListener(new Closer());
		
		pack();
	}

	/**
	 * @author $Author: ngaud$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Closer extends WindowAdapter {
		/**
		 */
		public Closer() {
			//
		}
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowClosing(WindowEvent event) {
			Kernels.killAll();
		}
	}

}
