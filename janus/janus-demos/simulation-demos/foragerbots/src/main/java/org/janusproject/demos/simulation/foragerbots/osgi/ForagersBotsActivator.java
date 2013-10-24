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
package org.janusproject.demos.simulation.foragerbots.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.foragerbots.Launcher;
import org.janusproject.demos.simulation.foragerbots.agents.ForagerBot;
import org.janusproject.demos.simulation.foragerbots.agents.Grid;
import org.janusproject.demos.simulation.foragerbots.ui.EnvironmentFrame;
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
public class ForagersBotsActivator implements BundleActivator, JanusApplication {

	private Logger logger;
	
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
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.INFO);
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.logger.info(Locale.getString(ForagersBotsActivator.class, "ACTIVATING_FORAGERBOTS")); //$NON-NLS-1$
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
		this.logger.log(Level.INFO, Locale.getString(ForagersBotsActivator.class, "FORAGERBOTS_START")); //$NON-NLS-1$
		
		
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
			kernel.launchLightAgent(bot, Locale.getString(Launcher.class, "BOT_NAME", Integer.toString(i))); //$NON-NLS-1$
		}
		
		gui.setVisible(true);
		
		synchronized(environment) {
			try {
				environment.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}				

		return StatusFactory.ok(this);
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
		return Locale.getString(ForagersBotsActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(ForagersBotsActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}
}

