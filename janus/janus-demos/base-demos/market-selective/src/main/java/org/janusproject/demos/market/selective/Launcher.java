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
package org.janusproject.demos.market.selective;

import java.util.logging.Level;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.market.selective.agent.BrokerAgent;
import org.janusproject.demos.market.selective.agent.ClientAgent;
import org.janusproject.demos.market.selective.agent.ProviderAgent;
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
		LoggerUtil.setGlobalLevel(Level.ALL);
		LoggerUtil.setShortLogMessageEnable(true);
		Kernel k = Kernels.get(true);
		
		Integer providerCount = new Integer(4);
		
		BrokerAgent b = new BrokerAgent();
		ProviderAgent p1 = new ProviderAgent();
		ProviderAgent p2 = new ProviderAgent();
		ProviderAgent p3 = new ProviderAgent();
		ProviderAgent p4 = new ProviderAgent();
		ClientAgent c = new ClientAgent();	
		
		k.launchHeavyAgent(p1,Locale.getString(Launcher.class, "PROVIDER", 1)); //$NON-NLS-1$
		k.launchHeavyAgent(p2,Locale.getString(Launcher.class, "PROVIDER", 2)); //$NON-NLS-1$
		k.launchHeavyAgent(p3,Locale.getString(Launcher.class, "PROVIDER", 3)); //$NON-NLS-1$
		k.launchHeavyAgent(p4,Locale.getString(Launcher.class, "PROVIDER", 4)); //$NON-NLS-1$
		k.launchHeavyAgent(c,Locale.getString(Launcher.class, "CLIENT")); //$NON-NLS-1$
		k.launchHeavyAgent(b,Locale.getString(Launcher.class, "BROKER"),providerCount); //$NON-NLS-1$
	}
	
}
