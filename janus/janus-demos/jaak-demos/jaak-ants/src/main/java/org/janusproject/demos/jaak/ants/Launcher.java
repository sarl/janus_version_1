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
package org.janusproject.demos.jaak.ants;

import java.util.logging.Level;

import javax.swing.JFrame;

import org.janusproject.demos.jaak.ants.ui.AntFrame;
import org.janusproject.demos.jaak.ants.ui.AntPanel;
import org.janusproject.jaak.environment.model.JaakEnvironment;
import org.janusproject.jaak.kernel.JaakKernel;
import org.janusproject.jaak.kernel.JaakKernelController;
import org.janusproject.jaak.spawner.JaakSpawner;
import org.janusproject.kernel.logger.LoggerUtil;

/** Launcher for the Ant Colony Demo.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoggerUtil.setGlobalLevel(Level.WARNING);
		JaakEnvironment environment = AntColonySystem.createEnvironment();
		JaakSpawner[] spawners = AntColonySystem.createColonies(environment);
		JaakKernelController controller = JaakKernel.initializeKernel(environment, spawners);
		AntPanel panel = AntColonySystem.createPanel(controller.getKernelAddress());
		JFrame frame = new AntFrame(panel);
		frame.setVisible(true);
	}
	
}