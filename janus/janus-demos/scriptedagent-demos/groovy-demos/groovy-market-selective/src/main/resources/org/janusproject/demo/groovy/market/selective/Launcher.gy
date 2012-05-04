package org.janusproject.demo.groovy.market.selective

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.demos.market.selective.agent.BrokerAgent;
import org.janusproject.demos.market.selective.agent.ClientAgent;
import org.janusproject.demos.market.selective.agent.ProviderAgent;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;

k = Kernels.get(true);

b = new BrokerAgent();
p1 = new ProviderAgent();
p2 = new ProviderAgent();
p3 = new ProviderAgent();
p4 = new ProviderAgent();
c = new ClientAgent();  


k.submitHeavyAgent(b,Locale.getString("Launcher.class", "BROKER"), 4); 
k.submitHeavyAgent(p1,Locale.getString("Launcher.class", "PROVIDER", 1)); 
k.submitHeavyAgent(p2,Locale.getString("Launcher.class", "PROVIDER", 2)); 
k.submitHeavyAgent(p3,Locale.getString("Launcher.class", "PROVIDER", 3)); 
k.submitHeavyAgent(p4,Locale.getString("Launcher.class", "PROVIDER", 4)); 
k.submitHeavyAgent(c,Locale.getString("Launcher.class", "CLIENT")); 
k.launchDifferedExecutionAgents();