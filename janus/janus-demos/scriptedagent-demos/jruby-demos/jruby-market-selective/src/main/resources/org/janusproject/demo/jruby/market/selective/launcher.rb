require "java"

#it is not necessary to require market-selective since it was added to the Java Classpath through maven configuration

import "org.janusproject.kernel.agent.Kernel"
import "org.janusproject.kernel.agent.Kernels"
import "org.janusproject.kernel.locale.LocalizedString"
import "org.janusproject.demos.market.selective.agent.BrokerAgent"
import "org.janusproject.demos.market.selective.agent.ClientAgent"
import "org.janusproject.demos.market.selective.agent.ProviderAgent"


k=org.janusproject.kernel.agent.Kernels.get(true);

b = BrokerAgent.new(4);
p1 = ProviderAgent.new();
p2 = ProviderAgent.new();
p3 = ProviderAgent.new();
p4 = ProviderAgent.new();
c = ClientAgent.new();  


k.submitHeavyAgent(b,LocalizedString.get("Launcher.class", "BROKER")); 
k.submitHeavyAgent(p1,LocalizedString.get("Launcher.class", "PROVIDER", 1)); 
k.submitHeavyAgent(p2,LocalizedString.get("Launcher.class", "PROVIDER", 2)); 
k.submitHeavyAgent(p3,LocalizedString.get("Launcher.class", "PROVIDER", 3)); 
k.submitHeavyAgent(p4,LocalizedString.get("Launcher.class", "PROVIDER", 4)); 
k.submitHeavyAgent(c,LocalizedString.get("Launcher.class", "CLIENT")); 
k.launchDifferedExecutionAgents();

