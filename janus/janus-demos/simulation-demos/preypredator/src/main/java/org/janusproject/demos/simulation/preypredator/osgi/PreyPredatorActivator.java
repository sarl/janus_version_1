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
package org.janusproject.demos.simulation.preypredator.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.simulation.preypredator.Launcher;
import org.janusproject.demos.simulation.preypredator.agent.Animat;
import org.janusproject.demos.simulation.preypredator.agent.GroundAgent;
import org.janusproject.demos.simulation.preypredator.gui.GUIWindow;
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
public class PreyPredatorActivator implements BundleActivator, JanusApplication {

	private Logger logger;

	private static final int PREDATOR_COUNT = 10;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.INFO);
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.logger.info(Locale.getString(PreyPredatorActivator.class, "ACTIVATING_MARKET")); //$NON-NLS-1$
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
		this.logger.log(Level.INFO, Locale.getString(Launcher.class, "CONSOLE_TITLE")); //$NON-NLS-1$
		GroundAgent terrain = new GroundAgent(10, 10, PREDATOR_COUNT + 1);

		GUIWindow window = new GUIWindow(terrain);
		Animat prey = new Animat(true);
		Animat[] predators = new Animat[PREDATOR_COUNT];
		for (int i = 0; i < PREDATOR_COUNT; ++i) {
			predators[i] = new Animat(false);
		}

		kernel.launchLightAgent(terrain, Locale.getString(Launcher.class, "TERRAIN_NAME")); //$NON-NLS-1$

		kernel.launchLightAgent(prey, Locale.getString(Launcher.class, "PREY_NAME")); //$NON-NLS-1$
		for (int i = 0; i < PREDATOR_COUNT; ++i) {
			kernel.launchLightAgent(predators[i], Locale.getString(Launcher.class, "PREDATOR_NAME", i)); //$NON-NLS-1$
		}

		window.setVisible(true);

		try {
			synchronized (terrain) {
				terrain.wait();
			}
		} catch (Throwable _) {
			//
		}

		if (terrain.isPreyCatched()) {
			JOptionPane.showMessageDialog(window, Locale.getString(Launcher.class, "PREY_CATCHED"), //$NON-NLS-1$
					Locale.getString(Launcher.class, "PREY_CATCHED"), //$NON-NLS-1$
					JOptionPane.INFORMATION_MESSAGE);
		}

		window.setVisible(false);
		window.dispose();

		System.out.println(Locale.getString(PreyPredatorActivator.class, "GAME_END")); //$NON-NLS-1$

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return Locale.getString(PreyPredatorActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(PreyPredatorActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}
}
