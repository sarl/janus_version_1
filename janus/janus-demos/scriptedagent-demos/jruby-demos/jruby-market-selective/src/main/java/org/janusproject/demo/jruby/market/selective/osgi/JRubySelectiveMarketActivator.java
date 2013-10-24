/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.demo.jruby.market.selective.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demo.jruby.market.selective.Launcher;
import org.janusproject.jrubyengine.RubyExecutionContext;
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
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gui.vinson@gmail.com$
 * @author $Author: renaud.buecher@utbm.fr$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JRubySelectiveMarketActivator implements BundleActivator, JanusApplication {

	private Logger logger;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.INFO);
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.logger.info(Locale.getString(JRubySelectiveMarketActivator.class, "ACTIVATING_MARKET")); //$NON-NLS-1$
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
		this.logger.log(Level.INFO, Locale.getString(JRubySelectiveMarketActivator.class, "MARKET_START")); //$NON-NLS-1$

		RubyExecutionContext resc = new RubyExecutionContext();
		resc.runScript(Launcher.LAUNCHING_SCRIPT); 

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return Locale.getString(JRubySelectiveMarketActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(JRubySelectiveMarketActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}
}
