/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
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
package org.janusproject.demos.simulation.foragerbots;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.foragerbots.agents.ForagerBot;
import org.janusproject.demos.simulation.foragerbots.agents.Grid;
import org.janusproject.demos.simulation.foragerbots.ui.EnvironmentFrame;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * Launcher for the forager bot demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	/** Count of bases.
	 */
	public static final int BASE_COUNT = 10;
	
	/** Count of bots.
	 */
	public static final int BOT_COUNT = 400;

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		LoggerUtil.setGlobalLevel(Level.WARNING);

		Logger logger = Logger.getAnonymousLogger();
		
		logger.info(Locale.getString(Launcher.class,
				"CONSOLE_TITLE", //$NON-NLS-1$
				Integer.toString(BOT_COUNT),
				Integer.toString(BASE_COUNT)));

		Kernel kernel = Kernels.get(false);
		
		// Create GUI
		EnvironmentFrame gui = new EnvironmentFrame(kernel);
		
		// Create environment
		Grid environment = new Grid(200, 150, BASE_COUNT);
		environment.addGridListener(gui.getGridListener());
		kernel.launchLightAgent(environment, 
				Locale.getString(Launcher.class, "ENVIONMENT_NAME")); //$NON-NLS-1$
		
		//Initialize boids for the blue group.
		for(int i =0; i<BOT_COUNT; ++i) {
			ForagerBot bot = new ForagerBot();
			kernel.launchLightAgent(bot,
					Locale.getString(Launcher.class, "BOT_NAME", Integer.toString(i))); //$NON-NLS-1$
		}
		
		gui.setVisible(true);

		synchronized(environment) {
			environment.wait();
		}
		
		Kernels.killAll();
	}
}
