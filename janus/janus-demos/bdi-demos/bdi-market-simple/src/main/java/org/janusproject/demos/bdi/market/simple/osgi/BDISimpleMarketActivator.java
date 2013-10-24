/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.demos.bdi.market.simple.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.bdi.market.simple.Environment;
import org.janusproject.demos.bdi.market.simple.agent.BrokerAgent;
import org.janusproject.demos.bdi.market.simple.agent.ClientAgent;
import org.janusproject.demos.bdi.market.simple.agent.ProviderAgent;
import org.janusproject.demos.bdi.market.simple.goal.BrokerGoal;
import org.janusproject.demos.bdi.market.simple.goal.ClientGoal;
import org.janusproject.demos.bdi.market.simple.goal.ProviderGoal;
import org.janusproject.demos.bdi.market.simple.goal.SellerGoal;
import org.janusproject.demos.bdi.market.simple.plan.BrokerPlan;
import org.janusproject.demos.bdi.market.simple.plan.ClientPlan;
import org.janusproject.demos.bdi.market.simple.plan.ProviderPlan;
import org.janusproject.demos.bdi.market.simple.plan.SellerPlan;
import org.janusproject.kernel.agent.KernelAgentFactory;
import org.janusproject.kernel.agent.bdi.BDIPlanRepository;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mmf.JanusApplication;
import org.janusproject.kernel.mmf.KernelAuthority;
import org.janusproject.kernel.mmf.KernelService;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BDISimpleMarketActivator implements BundleActivator, JanusApplication {

	private Logger logger;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.INFO);
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.logger.info(Locale.getString(BDISimpleMarketActivator.class, "ACTIVATING_MARKET")); //$NON-NLS-1$
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
		this.logger.log(Level.INFO, Locale.getString(BDISimpleMarketActivator.class, "MARKET_START")); //$NON-NLS-1$
		
		Environment environment = new Environment();
		
		Integer nbofprovider = new Integer(4);
		
		BrokerAgent b = new BrokerAgent();
		ProviderAgent p1 = new ProviderAgent();
		ProviderAgent p2 = new ProviderAgent();
		ProviderAgent p3 = new ProviderAgent();
		ProviderAgent p4 = new ProviderAgent();
		ClientAgent c = new ClientAgent();	
		
		environment.addAgent(b);
		environment.addAgent(p1);
		environment.addAgent(p2);
		environment.addAgent(p3);
		environment.addAgent(p4);
		
		BDIPlanRepository.getInstance().addPlan(ClientGoal.class, ClientPlan.class);
		BDIPlanRepository.getInstance().addPlan(BrokerGoal.class, BrokerPlan.class);
		BDIPlanRepository.getInstance().addPlan(ProviderGoal.class, ProviderPlan.class);
		BDIPlanRepository.getInstance().addPlan(SellerGoal.class, SellerPlan.class);
		
		kernel.submitHeavyAgent(b,Locale.getString(BDISimpleMarketActivator.class, "BROKER"),nbofprovider); //$NON-NLS-1$
		kernel.submitHeavyAgent(p1,Locale.getString(BDISimpleMarketActivator.class, "PROVIDER", 1),environment); //$NON-NLS-1$
		kernel.submitHeavyAgent(p2,Locale.getString(BDISimpleMarketActivator.class, "PROVIDER", 2),environment); //$NON-NLS-1$
		kernel.submitHeavyAgent(p3,Locale.getString(BDISimpleMarketActivator.class, "PROVIDER", 3),environment); //$NON-NLS-1$
		kernel.submitHeavyAgent(p4,Locale.getString(BDISimpleMarketActivator.class, "PROVIDER", 4),environment); //$NON-NLS-1$
		kernel.submitHeavyAgent(c,Locale.getString(BDISimpleMarketActivator.class, "CLIENT"),environment); //$NON-NLS-1$
		kernel.launchDifferedExecutionAgents();

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
		return Locale.getString(BDISimpleMarketActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(BDISimpleMarketActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}
}
