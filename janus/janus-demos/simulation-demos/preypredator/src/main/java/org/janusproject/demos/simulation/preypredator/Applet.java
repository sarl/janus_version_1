/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-12 Janus Core Developers
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
package org.janusproject.demos.simulation.preypredator;

import java.awt.BorderLayout;

import javax.swing.JApplet;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.preypredator.activator.PreyPredatorAgentActivator;
import org.janusproject.demos.simulation.preypredator.agent.Animat;
import org.janusproject.demos.simulation.preypredator.agent.GroundAgent;
import org.janusproject.demos.simulation.preypredator.gui.GUI;
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
	
	private static final long serialVersionUID = -3200439874274470803L;

	private static final int PREDATOR_COUNT = 10;
	
	private GroundAgent terrain = null;
	private GUI gui = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		super.init();
		LoggerUtil.setLoggingEnable(false);
		Kernel kernel = Kernels.get(true, new PreyPredatorAgentActivator());
		
		this.terrain = new GroundAgent(10, 10, PREDATOR_COUNT+1);
		
		this.gui = new GUI(this.terrain, false);
		setLayout(new BorderLayout());
		add(this.gui, BorderLayout.CENTER);
		
		Animat prey = new Animat(true);
		Animat[] predators = new Animat[PREDATOR_COUNT];
		for(int i=0; i<PREDATOR_COUNT; ++i) {
			predators[i] = new Animat(false);
		}
		
		kernel.launchLightAgent(this.terrain, Locale.getString(Launcher.class, "TERRAIN_NAME")); //$NON-NLS-1$

		kernel.launchLightAgent(prey, Locale.getString(Launcher.class, "PREY_NAME")); //$NON-NLS-1$
		for(int i=0; i<PREDATOR_COUNT; ++i) {
			kernel.launchLightAgent(predators[i],
					Locale.getString(Launcher.class, "PREDATOR_NAME", i)); //$NON-NLS-1$
		}
	}
	
	@Override
	public void start() {
		if (this.gui!=null)
			this.gui.launchRefresher();
	}

	@Override
	public void stop() {
		if (this.gui!=null)
			this.gui.stopRefresher();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		if (this.gui!=null)
			this.gui.stopRefresher();
		if (this.terrain!=null)
			this.terrain.stopGame();
		Kernels.killAll();
		super.destroy();
	}
	
}
