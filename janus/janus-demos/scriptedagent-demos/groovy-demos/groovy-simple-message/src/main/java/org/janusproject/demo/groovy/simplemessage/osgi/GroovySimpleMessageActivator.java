/*
 * $Id$
 *
 * Copyright (c) 2011, Multiagent Team,
 * Laboratoire Systemes et Transport of the
 * Universite of Technologie de Belfort-Montbeliard.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the Laboratoire Systemes et Transport of the
 * Universite of Technologie de Belfort-Montbeliard ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the SeT.
 *
 * http://www.multiagent.fr
 */
package org.janusproject.demo.groovy.simplemessage.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demo.groovy.simplemessage.agent.GroovyAgentReceiver;
import org.janusproject.demo.groovy.simplemessage.agent.GroovyAgentSender;
import org.janusproject.kernel.address.AgentAddress;
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
 * OSGI Activator.
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroovySimpleMessageActivator  implements BundleActivator, JanusApplication {

	private Logger logger;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.INFO);
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.logger.info(Locale.getString(GroovySimpleMessageActivator.class, "ACTIVATING_GROOVYSIMPLEMESSAGE")); //$NON-NLS-1$
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
		this.logger.log(Level.INFO, Locale.getString(GroovySimpleMessageActivator.class, "GROOVYSIMPLEMESSAGE_START")); //$NON-NLS-1$

		
		GroovyAgentReceiver receiver = new GroovyAgentReceiver();
		AgentAddress arec = kernel.launchLightAgent(receiver, "receiver"); //$NON-NLS-1$
		
		GroovyAgentSender sender = new GroovyAgentSender(arec);
		kernel.launchLightAgent(sender, "sender"); //$NON-NLS-1$		

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
		return Locale.getString(GroovySimpleMessageActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(GroovySimpleMessageActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}
}
