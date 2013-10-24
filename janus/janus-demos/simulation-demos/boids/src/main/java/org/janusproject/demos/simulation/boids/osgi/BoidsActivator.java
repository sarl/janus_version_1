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
package org.janusproject.demos.simulation.boids.osgi;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.boids.Launcher;
import org.janusproject.demos.simulation.boids.agent.BoidAgent;
import org.janusproject.demos.simulation.boids.agent.EnvironmentAgent;
import org.janusproject.demos.simulation.boids.util.EnvironmentFrame;
import org.janusproject.demos.simulation.boids.util.Population;
import org.janusproject.demos.simulation.boids.util.Settings;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.KernelAgentFactory;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mmf.JanusApplication;
import org.janusproject.kernel.mmf.KernelAuthority;
import org.janusproject.kernel.mmf.KernelService;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGI Activator.
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BoidsActivator  implements BundleActivator, JanusApplication {

	private Logger logger;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.INFO);
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.logger.info(Locale.getString(BoidsActivator.class, "ACTIVATING_BOIDS")); //$NON-NLS-1$
		context.registerService(JanusApplication.class.getName(), this, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status start(KernelService kernel) {
		this.logger.log(Level.INFO, Locale.getString(BoidsActivator.class, "BOIDS_START")); //$NON-NLS-1$
		
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
			try {
				environment.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Kernels.killAll();

		frame.dispose();



		return StatusFactory.ok(this);
	}


	
	private static AgentAddress addBoid(Kernel kernel, Population p, String name) {
		Vector2f initialPosition = new Vector2f((Math.random() - 0.5)*Settings.ENVIRONMENT_DEMI_WIDTH, (Math.random() - 0.5)*Settings.ENVIRONMENT_DEMI_HEIGHT);
		Vector2f initialSpeed = new Vector2f(Math.random() - 0.5, Math.random() - 0.5);
		
		BoidAgent boid = new BoidAgent(p, initialPosition, initialSpeed);
		
		return kernel.launchLightAgent(boid, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KernelAgentFactory getKernelAgentFactory() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KernelAuthority getKernelAuthority() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAutoStartJanusModules() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status stop(KernelService kernel) {
		return StatusFactory.ok(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStopOsgiFramework() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isKeepKernelAlive() {
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getName() {
		return Locale.getString(BoidsActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(BoidsActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}
	
}
