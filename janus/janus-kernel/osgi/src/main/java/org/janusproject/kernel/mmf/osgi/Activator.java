/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2012 Janus Core Developers
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
package org.janusproject.kernel.mmf.osgi;

import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.mmf.KernelOperation;
import org.janusproject.kernel.mmf.KernelServiceEvent;
import org.janusproject.kernel.mmf.KernelServiceEvent.KernelServiceEventType;
import org.janusproject.kernel.mmf.KernelServiceListener;
import org.janusproject.kernel.mmf.impl.OSGiKernelService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * OSGi activator for Janus kernel service.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Activator implements BundleActivator, KernelServiceListener {

	private OSGiKernelService service;

	private BundleContext context;
	private Bundle janusRemoteApplicationBundle;
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	/** {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		this.logger.info(Locale.getString(Activator.class, "KERNEL_STARTED")); //$NON-NLS-1$
		this.service = new OSGiKernelService(context);
		this.service.addKernelServiceListener(this);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		if (this.janusRemoteApplicationBundle != null) {
			this.janusRemoteApplicationBundle.stop();
		}

		// this.service.kill();
		// NOTE: The service should be automatically unregistered by OSGI
		this.logger.info(Locale.getString(Activator.class, "KERNEL_STOPPED")); //$NON-NLS-1$
		
		if (this.service!= null && this.service.getApplication()!= null
				&& !this.service.getApplication().isKeepKernelAlive()
				&& this.service.getApplication().isStopOsgiFramework()) {
			this.logger.info("Stopping OSGi Framework."); //$NON-NLS-1$
			context.getBundle(0).stop();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void kernelServiceEvent(KernelServiceEvent event) {
		if (KernelOperation.KERNEL_STOP == event.getOperation()
				&& KernelServiceEventType.OPERATION_EXECUTED == event.getType()) {
			if (this.context.getBundle().getState() == Bundle.STOPPING) {
				return;
			}
			try {
				this.context.getBundle().stop();
			}
			catch(AssertionError ae) {
				throw ae;
			}
			catch (BundleException e) {
				throw new RuntimeException("UNABLE_STOP_KERNEL", e); //$NON-NLS-1$
			}
		}

	}
	
}
