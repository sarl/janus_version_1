/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2009 Stephane GALLAND
 * Copyright (C) 2010, 2012 Janus Core Developers
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.preypredator.activator.PreyPredatorAgentActivator;
import org.janusproject.demos.simulation.preypredator.agent.Animat;
import org.janusproject.demos.simulation.preypredator.agent.GroundAgent;
import org.janusproject.demos.simulation.preypredator.gui.GUIWindow;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/** PREY PREDATOR GAME.
 * <p>
 * Copied from <a href="http://www.arakhne.org/tinymas/index.html">TinyMAS Platform Demos</a>
 * and adapted for Janus platform.
 * <p>
 * Thanks to Julia Nikolaeva, aka. <a href="mailto:flameia@zerobias.com">Flameia</a>, for the icons.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {

	private static final int PREDATOR_COUNT = 10;
	
	/**
	 * @param argv
	 * @throws Exception
	 */	
	public static void main(String[] argv) throws Exception {
		LoggerUtil.setGlobalLevel(Level.WARNING);
		
		Logger logger = Logger.getAnonymousLogger();
		logger.info(Locale.getString(Launcher.class, "CONSOLE_TITLE")); //$NON-NLS-1$

		Kernel kernel = Kernels.get(true, new PreyPredatorAgentActivator());
		
		GroundAgent terrain = new GroundAgent(10, 10, PREDATOR_COUNT+1);
		
		GUIWindow window = new GUIWindow(terrain);
		Animat prey = new Animat(true);
		Animat[] predators = new Animat[PREDATOR_COUNT];
		for(int i=0; i<PREDATOR_COUNT; ++i) {
			predators[i] = new Animat(false);
		}
		
		kernel.launchLightAgent(terrain, Locale.getString(Launcher.class, "TERRAIN_NAME")); //$NON-NLS-1$

		kernel.launchLightAgent(prey, Locale.getString(Launcher.class, "PREY_NAME")); //$NON-NLS-1$
		for(int i=0; i<PREDATOR_COUNT; ++i) {
			kernel.launchLightAgent(predators[i],
					Locale.getString(Launcher.class, "PREDATOR_NAME", i)); //$NON-NLS-1$
		}

		window.setVisible(true);

		try {
			synchronized(terrain) {
				terrain.wait();
			}
		}
		catch(Throwable _) {
			//
		}
		
		if (terrain.isPreyCatched()) {
			JOptionPane.showMessageDialog(window,
					Locale.getString(Launcher.class, "PREY_CATCHED"), //$NON-NLS-1$
					Locale.getString(Launcher.class, "PREY_CATCHED"),  //$NON-NLS-1$
					JOptionPane.INFORMATION_MESSAGE);
		}
		
		window.setVisible(false);
		window.dispose();
		
		logger.info(Locale.getString(Launcher.class, "GAME_END")); //$NON-NLS-1$
		System.exit(0);
	}

}