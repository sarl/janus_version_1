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
package org.janusproject.kernel.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.configuration.PrivilegedJanusPropertySetter;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.PrivilegedPlayerAddressService;
import org.janusproject.kernel.crio.interaction.PrivilegedMessageTransportService;
import org.janusproject.kernel.crio.organization.PrivilegedPersistentGroupCleanerService;
import org.janusproject.kernel.time.KernelTimeManager;
import org.janusproject.kernel.util.sizediterator.SizedIterator;
import org.janusproject.kernel.util.sizediterator.UnmodifiableSizedIterator;

/**
 * This class represents an execution context for CRIO classes and Janus kernel.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelContext extends CRIOContext {

	/** Number of threads to keep in the scheduled thread pool.
	 * Note that a value of zero causes pseudo-infinite loop in the allocation
	 * and release of the threads.
	 */
	public static final int NUMBER_OF_IDDLE_THREAD_IN_SCHEDULED_THREAD_POOL = 2;
	
	private final ExecutorService executionService = new ThreadPoolExecutor(
			1, // Min number of threads in pool size, even if iddle
			Short.MAX_VALUE, // Max pool size
			60L, TimeUnit.SECONDS, // Time to keep alive the threads before destruction
			new SynchronousQueue<Runnable>()); // Blocking queue

	private ScheduledExecutorService scheduledExecutionService = null;

	private final AgentRepository agents = new AgentRepository();
	
	private final AgentAddress kernel;

	private ProbeManager probes = null;
	
	private ChannelManager channels = null;

	private final AtomicBoolean kernelPause = new AtomicBoolean(false);
	
	private final PrivilegedMessageTransportService privilegedMTS;
	private final PrivilegedJanusPropertySetter privilegedJPS;
	private final PrivilegedPersistentGroupCleanerService privilegedPGC;
	private final PrivilegedPlayerAddressService privilegedPA;

	/**
	 * @param kernelAgent
	 *            is the address of the kernel agent which is owning this
	 *            context.
	 * @param tm
	 *            is the time manager to use in this CRIO context, or
	 *            <code>null</code> to use the default one.
	 * @param distantKernelHandler
	 *            is the handler of Distant Kernels, or <code>null</code> if the
	 *            kernel will not be working in a network
	 */
	public KernelContext(
			AgentAddress kernelAgent,
			KernelTimeManager tm,
			DistantKernelHandler distantKernelHandler) {
		this(kernelAgent, tm, distantKernelHandler, new PrivilegedServices());
	}

	/** 
	 * @param kernelAgent
	 *            is the address of the kernel agent which is owning this
	 *            context.
	 * @param tm
	 *            is the time manager to use in this CRIO context, or
	 *            <code>null</code> to use the default one.
	 * @param distantKernelHandler
	 *            is the handler of Distant Kernels, or <code>null</code> if the
	 *            kernel will not be working in a network
	 * @param privilegedServiceListener is the listener to immediately notify with
	 * the privileged services.
	 * @since 0.4
	 */
	protected KernelContext(
			AgentAddress kernelAgent,
			KernelTimeManager tm,
			DistantKernelHandler distantKernelHandler,
			PrivilegedContext privilegedServiceListener) {
		super(kernelAgent.getUUID(), tm, distantKernelHandler, privilegedServiceListener);
		this.kernel = kernelAgent;

		this.privilegedMTS = privilegedServiceListener.getPrivilegedMessageTransportService();
		this.privilegedJPS = privilegedServiceListener.getPrivilegedJanusPropertySetter();
		this.privilegedPGC = privilegedServiceListener.getPrivilegedPersistentGroupCleanerService();
		this.privilegedPA = privilegedServiceListener.getPrivilegedPlayerAddressService();
	}
	
	/**
	 * Invoked to destroy this context.
	 */
	@Override
	protected synchronized void destroy() {
		try {
			this.executionService.shutdownNow();
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch (Throwable _) {
			// ignore why the execution service has
			// failed to be shutted down
		}
		if (this.scheduledExecutionService != null) {
			try {
				this.scheduledExecutionService.shutdownNow();
			}
			catch(AssertionError ae) {
				throw ae;
			}
			catch (Throwable _) {
				// ignore why the execution service has
				// failed to be shutted down
			}
			this.scheduledExecutionService = null;
		}
		this.agents.clear();
		if (this.channels!=null) {
			this.channels.release();
			this.channels = null;
		}
		if (this.probes!=null) {
			this.probes.release();
			this.probes = null;
		}
		super.destroy();
	}

	/** Replies the handler which is permitting to connect this kernel context
	 * to a distant kernel context.
	 * 
	 * @return the distant kernel context handler.
	 */
	protected DistantKernelHandler getDistantKernelHandler() {
		return (DistantKernelHandler)getDistantCRIOContextHandler();
	}

	/**
	 * Replies the executor sevice for agents.
	 * 
	 * @return the executor sevice for agents.
	 */
	final ExecutorService getExecutorService() {
		return this.executionService;
	}

	/**
	 * Replies the executor sevice that support scheduling plans.
	 * <p>
	 * The replied executor service may not be used for agent execition.
	 * 
	 * @return a scheduled executor service.
	 */
	public final ScheduledExecutorService getScheduledExecutorService() {
		if (this.scheduledExecutionService == null)
			this.scheduledExecutionService = Executors
					.newScheduledThreadPool(NUMBER_OF_IDDLE_THREAD_IN_SCHEDULED_THREAD_POOL);
		return this.scheduledExecutionService;
	}

	/**
	 * Replies the agent repository.
	 * 
	 * @return the agent repository.
	 */
	final AgentRepository getAgentRepository() {
		return this.agents;
	}

	/** Replies the agents which are currently running on the same kernel context.
	 * 
	 * @return the running agents on the current kernel context.
	 * @since 0.4
	 */
	public final SizedIterator<AgentAddress> getLocalAgents() {
		return new UnmodifiableSizedIterator<AgentAddress>(this.agents.sizedIterator());
	}

	/**
	 * Replies if the kernel was paused.
	 * <p>
	 * When the kernel is pausing, it does not responds to any stimuli.
	 * 
	 * @return <code>true</code> if the kernel was paused, otherwise <code>false</code>.
	 * @since 0.5
	 */
	public boolean isKernelPaused() {
		return this.kernelPause.get();
	}

	/**
	 * Set the flag that indicates if the kernel was paused.
	 * 
	 * @param pause is <code>true</code> if the kernel was paused, otherwise <code>false</code>.
	 * @since 0.5
	 */
	void setKernelPaused(boolean pause) {
		this.kernelPause.set(pause);
	}

	/**
	 * Replies the executor sevice for agents.
	 * 
	 * @return the executor sevice for agents.
	 */
	public final synchronized ProbeManager getProbeManager() {
		if (this.probes == null) {
			this.probes = new ProbeManager(this);
		}
		return this.probes;
	}

	/**
	 * Return the manager of channels for this kernel.
	 * 
	 * @return the manager of channels.
	 * @since 0.5
	 */
	public final synchronized ChannelManager getChannelManager() {
		if (this.channels == null) {
			this.channels = new ChannelManager(this);
		}
		return this.channels;
	}

	/**
	 * Replies the address of the kernel agent.
	 * 
	 * @return the address of the kernel agent.
	 */
	public final AgentAddress getKernelAgent() {
		return this.kernel;
	}
	
	/**
	 * Replies the kernel interface.
	 * 
	 * @return the kernel interface.
	 * @since 0.5
	 */
	public final Kernel getKernel() {
		Agent ka = this.agents.get(this.kernel);
		if (ka instanceof KernelAgent) {
			return ((KernelAgent)ka).toKernel();
		}
		return null;
	}

	/** Replies the privileged message transport service.
	 * 
	 * @return the privileged message transport service.
	 */
	PrivilegedMessageTransportService getPrivilegedMessageTransportService() {
		return this.privilegedMTS;
	}

	/** Replies the privileged player-address service.
	 * 
	 * @return the privileged player-address service.
	 */
	PrivilegedPlayerAddressService getPrivilegedPlayerAddressService() {
		return this.privilegedPA;
	}

	/** Replies the privileged Janus property setter.
	 * 
	 * @return the privileged Janus property setter.
	 */
	PrivilegedJanusPropertySetter getPrivilegedJanusPropertySetter() {
		return this.privilegedJPS;
	}

	/** Replies the privileged cleaner of persistent groups.
	 * 
	 * @return the privileged cleaner.
	 * @since 0.4
	 */
	PrivilegedPersistentGroupCleanerService getPrivilegedJanusPersistentGroupCleaner() {
		return this.privilegedPGC;
	}

	/**
	 * This class represents an execution context for CRIO classes and Janus kernel.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class PrivilegedServices implements PrivilegedContext {

		private PrivilegedMessageTransportService mts = null;
		private PrivilegedJanusPropertySetter jps = null;
		private PrivilegedPersistentGroupCleanerService pgc = null;
		private PrivilegedPlayerAddressService pa = null;

		/**
		 */
		public PrivilegedServices() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrivilegedMessageTransportService(PrivilegedMessageTransportService mts) {
			this.mts = mts;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrivilegedJanusPropertySetter(PrivilegedJanusPropertySetter jps) {
			this.jps = jps;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrivilegedPersistentGroupCleanerService(PrivilegedPersistentGroupCleanerService pgc) {
			this.pgc = pgc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PrivilegedMessageTransportService getPrivilegedMessageTransportService() {
			return this.mts;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PrivilegedPersistentGroupCleanerService getPrivilegedPersistentGroupCleanerService() {
			return this.pgc;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PrivilegedJanusPropertySetter getPrivilegedJanusPropertySetter() {
			return this.jps;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrivilegedPlayerAddressService(PrivilegedPlayerAddressService pa) {
			this.pa = pa;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public PrivilegedPlayerAddressService getPrivilegedPlayerAddressService() {
			return this.pa;
		}
		
	} // class PrivilegedServices
	
}
