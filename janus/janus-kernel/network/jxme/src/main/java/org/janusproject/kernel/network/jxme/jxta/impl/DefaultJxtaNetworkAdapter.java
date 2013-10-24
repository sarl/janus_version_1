/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.kernel.network.jxme.jxta.impl;

import java.io.File;
import java.util.Random;

import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.platform.ConfigurationFactory;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousListener;

import org.arakhne.afc.vmutil.FileSystem;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.util.sizediterator.EmptyIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * Default implementation of the network adapter.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DefaultJxtaNetworkAdapter extends AbstractJxtaNetworkAdapter implements RendezvousListener {

	private static int getNetworkManagerConfig(JanusProperties props) {
		return props.getProperty(JanusProperty.JXTA_MODE. getPropertyName()).equalsIgnoreCase("EDGE") //$NON-NLS-1$
				? NetworkManager.EDGE : NetworkManager.ADHOC;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void initializeNetwork(AgentAddress kernelAddress,
			JanusProperties properties) throws Exception {
		setKernelAddress(kernelAddress);
		File home = new File(properties.getProperty(JanusProperty.JXTA_HOME));
				
		if (properties.getBoolean(JanusProperty.JXTA_CLEAN)) {
			FileSystem.delete(new File(home, "cm")); //$NON-NLS-1$
		}
		
		boolean jxtaLogging = properties.getBoolean(JanusProperty.JXTA_LOGGING);
		System.getProperties().put(JanusProperty.JXTA_LOGGING.getPropertyName(), jxtaLogging);
		
		String name = kernelAddress.toString();
		NetworkManager networkManager = new NetworkManager(
				getNetworkManagerConfig(properties), name, home.toURI());
		//networkManager.setUseDefaultSeeds(true);
		networkManager.setConfigPersistent(true);
		//networkManager.startNetwork();
		
		NetworkConfigurator configurator = networkManager.getConfigurator();
		configurator.setHome(home);
		configurator.setPrincipal(kernelAddress.getUUID().toString());
		configurator.setDescription(""); //$NON-NLS-1$
		configurator.setPassword(""); //$NON-NLS-1$
				
		//Can't be possible to use the dynamic port allocation
		//if two JXTA applications are running on the same computer a "java.net.BindException: Adress already used" is thrown
		//FIXME potential bug, we may obtain the same port, the probability is low but it is still possible
		//I see any simple solution except a shared file in janus config directory since the two applications are running on different JVMs
		Random randomGen = new Random();
		int port = randomGen.nextInt(65535);
		// On Unix system, we need to use ephemeral ports that don't require root permission
		while ((port < 1024) && (port>65534)) {
			port = randomGen.nextInt(65534);
		}		
		configurator.setTcpStartPort(port);
		configurator.setTcpEndPort(port + 50);
		configurator.setTcpPort(port);
		configurator.save();
		
		ConfigurationFactory.setHome(home);
		ConfigurationFactory.setName("JXME.Janus"); //$NON-NLS-1$
		Advertisement config = ConfigurationFactory.newPlatformConfig();
		ConfigurationFactory.save(config, false);

		PeerGroup netPeerGroup = PeerGroupFactory.newNetPeerGroup();
		JxtaGroup netJG = new JxtaGroup(this,netPeerGroup, null);

		RendezVousService rdv = netPeerGroup.getRendezVousService();
		rdv.addListener(this);
		setRendezVous(rdv);
		waitForRendezvousConnection(5000);
		setApplicationGroup(createApplicationGroup(properties, netJG));
	}

	/** {@inheritDoc}
	 */
	@Override
	public void shutdownNetwork() throws Exception {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<AgentAddress> getRemoteKernels() {
		return EmptyIterator.singleton();
	}

	/** {@inheritDoc}
	 */
	@Override
	public void informLocalAgentAdded(AgentAddress agentAdress) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc}
	 */
	@Override
	public void informLocalAgentRemoved(AgentAddress agentAddress) {
		// TODO Auto-generated method stub
		
	}

}
