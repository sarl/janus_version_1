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
package org.janusproject.demos.market.simple;

import java.util.logging.Level;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.simple.agent.BrokerAgent;
import org.janusproject.demos.market.simple.agent.ClientAgent;
import org.janusproject.demos.market.simple.agent.ProviderAgent;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoggerUtil.setGlobalLevel(Level.INFO);
		Kernel k = Kernels.get(true);
		
		//Creates the 3 agents: a client, a broker and a provider
		BrokerAgent b = new BrokerAgent();
		ProviderAgent p = new ProviderAgent();
		ClientAgent c = new ClientAgent();	
		
		//Launch these agents
		//Since we wants to execute these agents using a dedicated thread for each agent the method launchHeavyAgent is used in place of launchLightAgent
		k.launchHeavyAgent(p,Locale.getString(Launcher.class, "PROVIDER")); //$NON-NLS-1$
		k.launchHeavyAgent(c,Locale.getString(Launcher.class, "CLIENT")); //$NON-NLS-1$
		k.launchHeavyAgent(b,Locale.getString(Launcher.class, "BROKER")); //$NON-NLS-1$
	}
}
