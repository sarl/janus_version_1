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
package org.janusproject.demos.ecoresolution.ecocube.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.ecoresolution.ecocube.agent.CubeAgent;
import org.janusproject.demos.ecoresolution.ecocube.agent.CubeEcoProblem;
import org.janusproject.demos.ecoresolution.ecocube.agent.GroundAgent;
import org.janusproject.demos.ecoresolution.ecocube.ui.CubeWorldFrame;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.kernel.agent.KernelAgentFactory;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mmf.JanusApplication;
import org.janusproject.kernel.mmf.KernelAuthority;
import org.janusproject.kernel.mmf.KernelService;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi activator for the EcoCube demo.
 * 
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EcoCubeActivator implements BundleActivator, JanusApplication {

	private Logger logger;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.INFO);
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.logger.info(Locale.getString(EcoCubeActivator.class, "ACTIVATING_ECOCUBE")); //$NON-NLS-1$
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
		this.logger.log(Level.INFO, Locale.getString(EcoCubeActivator.class, "ECOCUBE_START")); //$NON-NLS-1$
		
		GroundAgent planeAgent = new GroundAgent();
		EcoIdentity table = planeAgent.getEcoIdentity();
		
		CubeAgent cubeA = new CubeAgent("A", table, table); //$NON-NLS-1$
		CubeAgent cubeB = new CubeAgent("B", cubeA.getEcoIdentity(), table);  //$NON-NLS-1$
		CubeAgent cubeC = new CubeAgent("C", cubeB.getEcoIdentity(), table);  //$NON-NLS-1$
		
		// Initialize the problem
		CubeEcoProblem problem = new CubeEcoProblem(); // 3 cubes + 1 table are in the problem
		problem.addUpDownRelation(cubeA, planeAgent);
		problem.addUpDownRelation(cubeC, cubeA);
		problem.addUpDownRelation(cubeB, cubeC);

		// Window
		CubeWorldFrame frame = new CubeWorldFrame(kernel, planeAgent.getEcoIdentity(), 3);
		frame.setVisible(true);
		
		// Start solving
		problem.solve(null);

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
		return Locale.getString(EcoCubeActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(EcoCubeActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}
}
