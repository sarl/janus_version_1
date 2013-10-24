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
package org.janusproject.demos.bdi.market.simple;

import java.util.logging.Level;

import org.arakhne.afc.vmutil.locale.Locale;
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
 * @version $FullVersion$
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
