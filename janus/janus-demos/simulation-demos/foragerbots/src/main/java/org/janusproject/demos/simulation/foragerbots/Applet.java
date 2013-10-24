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

import java.awt.BorderLayout;

import javax.swing.JApplet;
import javax.swing.JScrollPane;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.foragerbots.agents.ForagerBot;
import org.janusproject.demos.simulation.foragerbots.agents.Grid;
import org.janusproject.demos.simulation.foragerbots.ui.EnvironmentPanel;
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
	
	private static final long serialVersionUID = 1323619380703961975L;

	/** Count of bases.
	 */
	public static final int BASE_COUNT = 10;
	
	/** Count of bots.
	 */
	public static final int BOT_COUNT = 400;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		super.init();
		LoggerUtil.setLoggingEnable(false);

		Kernel kernel = Kernels.get(false);
		
		// Create GUI
		EnvironmentPanel gui = new EnvironmentPanel();
		JScrollPane scroll = new JScrollPane(gui);
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		
		// Create environment
		Grid environment = new Grid(200, 150, BASE_COUNT);
		environment.addGridListener(gui);
		kernel.launchLightAgent(environment, 
				Locale.getString(Applet.class, "ENVIONMENT_NAME")); //$NON-NLS-1$
		
		//Initialize boids for the blue group.
		for(int i =0; i<BOT_COUNT; ++i) {
			ForagerBot bot = new ForagerBot();
			kernel.launchLightAgent(bot,
					Locale.getString(Applet.class, "BOT_NAME", Integer.toString(i))); //$NON-NLS-1$
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
