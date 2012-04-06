/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, 2012 Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.demos.bdi.market.simple;

import java.util.logging.Level;

import org.arakhne.vmutil.locale.Locale;
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
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.agent.bdi.BDIPlanRepository;
import org.janusproject.kernel.logger.LoggerUtil;


/**
 *
 * @author $Author: mbrigaud$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoggerUtil.setGlobalLevel(Level.ALL);
		LoggerUtil.setShortLogMessageEnable(true);
		Kernel k = Kernels.get(true);
		
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
		
		k.submitHeavyAgent(b,Locale.getString(Launcher.class, "BROKER"),nbofprovider); //$NON-NLS-1$
		k.submitHeavyAgent(p1,Locale.getString(Launcher.class, "PROVIDER", 1),environment); //$NON-NLS-1$
		k.submitHeavyAgent(p2,Locale.getString(Launcher.class, "PROVIDER", 2),environment); //$NON-NLS-1$
		k.submitHeavyAgent(p3,Locale.getString(Launcher.class, "PROVIDER", 3),environment); //$NON-NLS-1$
		k.submitHeavyAgent(p4,Locale.getString(Launcher.class, "PROVIDER", 4),environment); //$NON-NLS-1$
		k.submitHeavyAgent(c,Locale.getString(Launcher.class, "CLIENT"),environment); //$NON-NLS-1$
		k.launchDifferedExecutionAgents();
	}
	
}
