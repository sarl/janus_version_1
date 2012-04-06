#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* 
 * ${symbol_dollar}Id${symbol_dollar}
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package ${package};

import ${package}.agent.ProviderAgent;
import ${package}.agent.RequestingAgent;

import org.janusproject.kernel.agent.KernelAgentFactory;
import org.janusproject.kernel.mmf.IKernelAuthority;
import org.janusproject.kernel.mmf.IKernelService;
import org.janusproject.kernel.mmf.JanusApplication;
import org.janusproject.kernel.network.agent.NetworkingKernelAgentFactory;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.osgi.framework.BundleContext;

/**
 * A basic application implementation.
 * 
 * @author Sebastian Rodriguez &lt;sebastian@sebastianrodriguez.com.ar&gt;
 * @version ${symbol_dollar}Name${symbol_dollar} ${symbol_dollar}Revision${symbol_dollar} ${symbol_dollar}Date${symbol_dollar}
 * @mavengroupid org.janus-project.kernel
 * @mavenartifactid osgi-basic-archetype
 * 
 */
public class Application implements JanusApplication {

	private BundleContext context = null;
	private ProviderAgent providerAgent = null;
	private RequestingAgent requestingAgent = null;
	
	public Application(BundleContext context) {
		this.context = context;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.janusproject.kernel.mmf.JanusModule#getName()
	 */
	@Override
	public String getName() {
		return "Date Requesting Application";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.janusproject.kernel.mmf.JanusModule#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Starts a Requester agent that asks for the current time 5 times to a Provider using the DateProviderOrganization";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.janusproject.kernel.mmf.JanusModule${symbol_pound}start(org.janusproject.kernel
	 * .mmf.IKernelService)
	 */
	@Override
	public Status start(IKernelService kernel) {
		providerAgent = new ProviderAgent();
		requestingAgent = new RequestingAgent();
		kernel.launchHeavyAgent(providerAgent);
		kernel.launchHeavyAgent(requestingAgent);
		return StatusFactory.ok(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.janusproject.kernel.mmf.JanusApplication${symbol_pound}getKernelAgentFactory()
	 */
	@Override
	public KernelAgentFactory getKernelAgentFactory() {
		//Use this if you want to enable networking.
		//return new NetworkingKernelAgentFactory(context);
		
		//Use this if you want a stand-alone kernel.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.janusproject.kernel.mmf.JanusApplication${symbol_pound}getKernelAuthority()
	 */
	@Override
	public IKernelAuthority getKernelAuthority() {
		// All operations are approved.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.janusproject.kernel.mmf.JanusApplication${symbol_pound}isAutoStartJanusModules()
	 */
	@Override
	public boolean isAutoStartJanusModules() {
		// All modules registered will be started automatically.
		return false;
	}

	/* (non-Javadoc)
	 * @see org.janusproject.kernel.mmf.JanusApplication#isStopOsgiFramework()
	 */
	@Override
	public boolean isStopOsgiFramework() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.janusproject.kernel.mmf.JanusApplication#isKeepKernelAlive()
	 */
	@Override
	public boolean isKeepKernelAlive() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.janusproject.kernel.mmf.JanusModule#isRunning()
	 */
	@Override
	public boolean isRunning() {
		if(providerAgent != null && providerAgent.isAlive() ){
			return true;
		}
		
		if( requestingAgent != null && requestingAgent.isAlive()){
			return true;
		}
		providerAgent = null;
		requestingAgent = null;
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.janusproject.kernel.mmf.JanusModule${symbol_pound}stop(org.janusproject.kernel.mmf.IKernelService)
	 */
	@Override
	public Status stop(IKernelService kernel) {
		return StatusFactory.ok(this);
	}

}
