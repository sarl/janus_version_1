require "java"

#it is not necessary to require market-selective since it was added to the Java Classpath through maven configuration

import "org.arakhne.vmutil.locale.Locale"
import "org.janusproject.demos.market.selective.agent.BrokerAgent"
import "org.janusproject.demos.market.selective.agent.ClientAgent"
import "org.janusproject.demos.market.selective.agent.ProviderAgent"
import "org.janusproject.kernel.Kernel"
import "org.janusproject.kernel.agent.Kernels"


k=org.janusproject.kernel.agent.Kernels.get(true);

providerCount = java.lang.Integer.new(4)
b = BrokerAgent.new();
p1 = ProviderAgent.new();
p2 = ProviderAgent.new();
p3 = ProviderAgent.new();
p4 = ProviderAgent.new();
c = ClientAgent.new();  


k.submitHeavyAgent(b,Locale.getString("Launcher.class", "BROKER"),providerCount); 
k.submitHeavyAgent(p1,Locale.getString("Launcher.class", "PROVIDER", 1)); 
k.submitHeavyAgent(p2,Locale.getString("Launcher.class", "PROVIDER", 2)); 
k.submitHeavyAgent(p3,Locale.getString("Launcher.class", "PROVIDER", 3)); 
k.submitHeavyAgent(p4,Locale.getString("Launcher.class", "PROVIDER", 4)); 
k.submitHeavyAgent(c,Locale.getString("Launcher.class", "CLIENT")); 
k.launchDifferedExecutionAgents();
