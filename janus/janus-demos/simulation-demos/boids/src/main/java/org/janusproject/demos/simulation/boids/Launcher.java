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
package org.janusproject.demos.simulation.boids;

import java.awt.Color;
import java.util.logging.Level;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.boids.agent.BoidAgent;
import org.janusproject.demos.simulation.boids.agent.EnvironmentAgent;
import org.janusproject.demos.simulation.boids.util.EnvironmentFrame;
import org.janusproject.demos.simulation.boids.util.Population;
import org.janusproject.demos.simulation.boids.util.Settings;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	private static AgentAddress addBoid(Kernel kernel, Population p, String name) {
		Vector2f initialPosition = new Vector2f((Math.random() - 0.5)*Settings.ENVIRONMENT_DEMI_WIDTH, (Math.random() - 0.5)*Settings.ENVIRONMENT_DEMI_HEIGHT);
		Vector2f initialSpeed = new Vector2f(Math.random() - 0.5, Math.random() - 0.5);
		
		BoidAgent boid = new BoidAgent(p, initialPosition, initialSpeed);
		
		return kernel.launchLightAgent(boid, name);
	}
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		LoggerUtil.setGlobalLevel(Level.WARNING);
		
		Kernel kernel = Kernels.get(false);
		
		//3 populations of boids are created (Red, green and blue)		
		Population pRed = new Population(Color.RED, Locale.getString(Launcher.class, "RED")); //$NON-NLS-1$
		Population pGreen = new Population(Color.GREEN, Locale.getString(Launcher.class, "GREEN")); //$NON-NLS-1$
		Population pBlue = new Population(Color.BLUE, Locale.getString(Launcher.class, "BLUE")); //$NON-NLS-1$
		
		int numberOfBoidPerPopulation = 20;
		
		// Create environment
		EnvironmentAgent environment = new EnvironmentAgent(
				pRed,
				pGreen,
				pBlue);
		EnvironmentFrame frame = new EnvironmentFrame(environment.getGUI());
		kernel.launchLightAgent(environment, "Environment"); //$NON-NLS-1$
		
		//Initialize boids for the red group.
		for(int i =0; i<numberOfBoidPerPopulation;++i){
			addBoid(kernel, pRed, "Red #"+(i+1)); //$NON-NLS-1$
		}
		
		//Initialize boids for the green group.
		for(int i =0; i<numberOfBoidPerPopulation;++i){
			addBoid(kernel, pGreen, "Green #"+(i+1)); //$NON-NLS-1$
		}
		
		//Initialize boids for the blue group.
		for(int i =0; i<numberOfBoidPerPopulation;++i){
			addBoid(kernel, pBlue, "Blue #"+(i+1)); //$NON-NLS-1$
		}
		
		synchronized(environment) {
			environment.wait();
		}
		
		Kernels.killAll();

		frame.dispose();
	}
}
