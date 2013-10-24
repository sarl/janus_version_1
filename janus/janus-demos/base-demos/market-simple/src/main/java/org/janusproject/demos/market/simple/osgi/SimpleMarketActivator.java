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
package org.janusproject.demos.market.simple.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.simple.Launcher;
import org.janusproject.demos.market.simple.agent.BrokerAgent;
import org.janusproject.demos.market.simple.agent.ClientAgent;
import org.janusproject.demos.market.simple.agent.ProviderAgent;
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
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SimpleMarketActivator implements BundleActivator, JanusApplication {

	private Logger logger;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.INFO);
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.logger.info(Locale.getString(SimpleMarketActivator.class, "ACTIVATING_MARKET")); //$NON-NLS-1$
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
		this.logger.log(Level.INFO, Locale.getString(SimpleMarketActivator.class, "MARKET_START")); //$NON-NLS-1$

		//Creates the 3 agents: a client, a broker and a provider
		BrokerAgent b = new BrokerAgent();
		ProviderAgent p = new ProviderAgent();
		ClientAgent c = new ClientAgent();	
		
		//Launch these agents
		//Since we wants to execute these agents using a dedicated thread for each agent the method launchHeavyAgent is used in place of launchLightAgent
		kernel.launchHeavyAgent(b,Locale.getString(Launcher.class, "BROKER")); //$NON-NLS-1$
		kernel.launchHeavyAgent(p,Locale.getString(Launcher.class, "PROVIDER")); //$NON-NLS-1$
		kernel.launchHeavyAgent(c,Locale.getString(Launcher.class, "CLIENT")); //$NON-NLS-1$

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
		return Locale.getString(SimpleMarketActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(SimpleMarketActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}


}
