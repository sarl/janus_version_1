/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010, 2012 Janus Core Developers
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
package org.janusproject.demos.network.januschat.swing.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;
import org.janusproject.demos.network.januschat.ChatUtil;
import org.janusproject.demos.network.januschat.ChatterListener;
import org.janusproject.demos.network.januschat.swing.ui.ChatRoomFrame;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.KernelAgentFactory;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.mmf.KernelAuthority;
import org.janusproject.kernel.mmf.KernelService;
import org.janusproject.kernel.mmf.JanusApplication;
import org.janusproject.kernel.network.jxse.agent.JxtaJxseKernelAgentFactory;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activates and start the simple chat demo via a OSGi module.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ChatActivator implements BundleActivator, JanusApplication {

	private BundleContext context = null;

	/** {@inheritDoc}
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		LoggerUtil.setGlobalLevel(Level.SEVERE);
		Logger.getAnonymousLogger().info(Locale.getString(ChatActivator.class, "ACTIVATING_CHAT")); //$NON-NLS-1$
		this.context = context;
		context.registerService(JanusApplication.class.getName(), this, null);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		//
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status start(KernelService kernel) {
		Logger.getAnonymousLogger().info(Locale.getString(ChatActivator.class, "STARTING_CHAT")); //$NON-NLS-1$

		ChatUtil.addChatterListener(new Listener());
		ChatUtil.createChatter(kernel);
		
		return StatusFactory.ok(this);
	}

	/** {@inheritDoc}
	 */
	@Override
	public KernelAgentFactory getKernelAgentFactory() {
		return new JxtaJxseKernelAgentFactory(this.context);
	}

	/** {@inheritDoc}
	 */
	@Override
	public KernelAuthority getKernelAuthority() {
		return null;
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isAutoStartJanusModules() {
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Status stop(KernelService kernel) {
		return StatusFactory.ok(this);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public boolean isStopOsgiFramework() {
		return true;
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isKeepKernelAlive() {
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getName() {
		return Locale.getString(ChatActivator.class, "APPLICATION_NAME"); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString(ChatActivator.class, "APPLICATION_DESCRIPTION"); //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return true;
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Listener implements ChatterListener {

		/**
		 */
		public Listener() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onChatterCreated(AgentAddress chatter) {
			ChatRoomFrame frame = new ChatRoomFrame(chatter);
			frame.setVisible(true);
		}
		
	}
	
}
