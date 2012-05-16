/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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

import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.time.KernelTimeManager;

/**
 * Utility methods to access to kernel agents.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public final class Kernels {

	private static final Map<AgentAddress,KernelAgent> kernelAgents = new TreeMap<>();
	
	private static KernelAgentFactory defaultKernelFactory = null;
	
	/**
	 */
	private Kernels() {
		//
	}
	
	/** Add the given kernel inside the list of available kernels.
	 * 
	 * @param kernel
	 */
	static void add(KernelAgent kernel) {
		if (kernel!=null) {
			synchronized(kernelAgents) {
				kernelAgents.put(kernel.getAddress(), kernel);
			}
		}
	}

	/** Remove the given kernel from the list of available kernels.
	 * 
	 * @param kernel
	 */
	static void remove(KernelAgent kernel) {
		if (kernel!=null) {
			synchronized(kernelAgents) {
				kernelAgents.remove(kernel.getAddress());
			}
		}
	}

	/** Clear the list of kernels but do not stop them.
	 */
	static void clear() {
		synchronized(kernelAgents) {
			kernelAgents.clear();
		}
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @return a kernel agent.
	 */
	public static Kernel get() {
		return get((Boolean)null, null, null, null, defaultKernelFactory);
	}
	
	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(String applicationName) {
		return get((Boolean)null, null, null, applicationName, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a kernel agent.
	 */
	public static Kernel get(Boolean commitSuicide, EventListener startUpListener) {
		return get(commitSuicide, null, startUpListener, null, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(Boolean commitSuicide, EventListener startUpListener, String applicationName) {
		return get(commitSuicide, null, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * when no more agent is inside the kernel.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a kernel agent.
	 */
	public static Kernel get(EventListener startUpListener) {
		return get((Boolean)null, null, startUpListener, null, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * when no more agent is inside the kernel.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(EventListener startUpListener, String applicationName) {
		return get((Boolean)null, null, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @return a kernel agent.
	 */
	public static Kernel get(Boolean commitSuicide) {
		return get(commitSuicide, null, null, null, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(Boolean commitSuicide, String applicationName) {
		return get(commitSuicide, null, null, applicationName, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param activator is the activator to be used by the kernel agent.
	 * @return a kernel agent.
	 */
	public static Kernel get(AgentActivator activator) {
		return get((Boolean)null, activator, null, null, defaultKernelFactory);
	}
	
	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param activator is the activator to be used by the kernel agent.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(AgentActivator activator, String applicationName) {
		return get((Boolean)null, activator, null, applicationName, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a kernel agent.
	 */
	public static Kernel get(Boolean commitSuicide, AgentActivator activator, EventListener startUpListener) {
		return get(commitSuicide, activator, startUpListener, null, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(Boolean commitSuicide, AgentActivator activator, EventListener startUpListener, String applicationName) {
		return get(commitSuicide, activator, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @param factory is the kernel agent factory to use when creating the agent.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(Boolean commitSuicide, AgentActivator activator, EventListener startUpListener, String applicationName, KernelAgentFactory factory) {
		KernelAgent first = getKernelAgent();
		return (first==null)
			? create(commitSuicide, activator, null, startUpListener, applicationName, factory)
			: first.toKernel();
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a kernel agent.
	 */
	public static Kernel get(AgentActivator activator, EventListener startUpListener) {
		return get((Boolean)null, activator, startUpListener, null, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(AgentActivator activator, EventListener startUpListener, String applicationName) {
		return get((Boolean)null, activator, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @return a kernel agent.
	 */
	public static Kernel get(Boolean commitSuicide, AgentActivator activator) {
		return get(commitSuicide, activator, null, null, defaultKernelFactory);
	}

	/** Replies the first kernel agent. If no kernel agent exists,
	 * create one and reply it.
	 * 
	 * @param commitSuicide indicates if the new kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel get(Boolean commitSuicide, AgentActivator activator, String applicationName) {
		return get(commitSuicide, activator, null, applicationName, defaultKernelFactory);
	}

	/** Replies the first available kernel agent. Do not create one
	 * if none.
	 * 
	 * @return a kernel agent.
	 */
	static KernelAgent getKernelAgent() {
		synchronized(kernelAgents) {
			if (!kernelAgents.isEmpty()) {
				return kernelAgents.values().iterator().next();
			}
		}
		return null;
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static Kernel create() {
		return create((Boolean)null, null, null, null, null, defaultKernelFactory);
	}
	
	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 * @since 0.4
	 */
	public static Kernel create(String applicationName) {
		return create((Boolean)null, null, null, null, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static Kernel create(KernelTimeManager timeManager) {
		return create((Boolean)null, null, timeManager, null, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 * @since 0.4
	 */
	public static Kernel create(KernelTimeManager timeManager, String applicationName) {
		return create((Boolean)null, null, timeManager, null, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @return a new kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide) {
		return create(commitSuicide, null, null, null, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(Boolean commitSuicide, String applicationName) {
		return create(commitSuicide, null, null, null, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @return a new kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide, KernelTimeManager timeManager) {
		return create(commitSuicide, null, timeManager, null, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(Boolean commitSuicide, KernelTimeManager timeManager, String applicationName) {
		return create(commitSuicide, null, timeManager, null, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static Kernel create(EventListener startUpListener) {
		return create((Boolean)null, null, null, startUpListener, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 * @since 0.4
	 */
	public static Kernel create(EventListener startUpListener, String applicationName) {
		return create((Boolean)null, null, null, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static Kernel create(KernelTimeManager timeManager, EventListener startUpListener) {
		return create((Boolean)null, null, timeManager, startUpListener, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 * @since 0.4
	 */
	public static Kernel create(KernelTimeManager timeManager, EventListener startUpListener, String applicationName) {
		return create((Boolean)null, null, timeManager, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a new kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide, EventListener startUpListener) {
		return create(commitSuicide, null, null, startUpListener, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(Boolean commitSuicide, EventListener startUpListener, String applicationName) {
		return create(commitSuicide, null, null, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a new kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide, KernelTimeManager timeManager, EventListener startUpListener) {
		return create(commitSuicide, null, timeManager, startUpListener, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(Boolean commitSuicide, KernelTimeManager timeManager, EventListener startUpListener, String applicationName) {
		return create(commitSuicide, null, timeManager, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param activator is the activator to be used by the kernel agent.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static Kernel create(AgentActivator activator) {
		return create((Boolean)null, activator, null, null, null, defaultKernelFactory);
	}
	
	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param activator is the activator to be used by the kernel agent.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 * @since 0.4
	 */
	public static Kernel create(AgentActivator activator, String applicationName) {
		return create((Boolean)null, activator, null, null, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static Kernel create(AgentActivator activator, KernelTimeManager timeManager) {
		return create((Boolean)null, activator, timeManager, null, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 * @since 0.4
	 */
	public static Kernel create(AgentActivator activator, KernelTimeManager timeManager, String applicationName) {
		return create((Boolean)null, activator, timeManager, null, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @return a new kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide, AgentActivator activator) {
		return create(commitSuicide, activator, null, null, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(Boolean commitSuicide, AgentActivator activator, String applicationName) {
		return create(commitSuicide, activator, null, null, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @return a new kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide, AgentActivator activator, KernelTimeManager timeManager) {
		return create(commitSuicide, activator, null, null, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide, AgentActivator activator, KernelTimeManager timeManager, String applicationName) {
		return create(commitSuicide, activator, null, null, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param activator is the activator to be used by the kernel agent.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static Kernel create(AgentActivator activator, EventListener startUpListener) {
		return create((Boolean)null, activator, null, startUpListener, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 * @since 0.4
	 */
	public static Kernel create(AgentActivator activator, EventListener startUpListener, String applicationName) {
		return create((Boolean)null, activator, null, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 */
	public static Kernel create(AgentActivator activator, KernelTimeManager timeManager, EventListener startUpListener) {
		return create((Boolean)null, activator, timeManager, startUpListener, null, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * <p>
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @see JanusProperty#JANUS_KERNEL_KEEP_ALIVE
	 * @since 0.4
	 */
	public static Kernel create(AgentActivator activator, KernelTimeManager timeManager, EventListener startUpListener, String applicationName) {
		return create((Boolean)null, activator, timeManager, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a new kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide, AgentActivator activator, EventListener startUpListener) {
		return create(commitSuicide, activator, null, startUpListener, null, defaultKernelFactory);
	}
	
	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(Boolean commitSuicide, AgentActivator activator, EventListener startUpListener, String applicationName) {
		return create(commitSuicide, activator, null, startUpListener, applicationName, defaultKernelFactory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a new kernel agent.
	 */
	public static Kernel create(
			Boolean commitSuicide, 
			AgentActivator activator,
			KernelTimeManager timeManager,
			EventListener startUpListener) {
		KernelAgentFactory factory = defaultKernelFactory;

		return create(commitSuicide, activator, timeManager, startUpListener,
				null, factory);
	}
	
	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a new kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(
			Boolean commitSuicide, 
			AgentActivator activator,
			KernelTimeManager timeManager,
			EventListener startUpListener,
			String applicationName) {
		return create(commitSuicide, activator, timeManager, startUpListener,
				applicationName, defaultKernelFactory);
	}

	/**
	 * Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param factory is the kernel agent factory to use.
	 * @return a kernel agent.
	 */
	public static Kernel create(KernelAgentFactory factory) {
		return create((Boolean)null, null, null, null, null, factory);
	}
	
	/**
	 * Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param factory is the kernel agent factory to use.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a kernel agent.
	 * @since 0.5
	 */
	public static Kernel create(KernelAgentFactory factory, EventListener startUpListener) {
		return create((Boolean)null, null, null, startUpListener, null, factory);
	}

	 /**
	 * Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param factory is the kernel agent factory to use.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(KernelAgentFactory factory, String applicationName) {
		return create((Boolean)null, null, null, null, applicationName, factory);
	}
	
	 /**
	 * Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param factory is the kernel agent factory to use.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @return a kernel agent.
	 * @since 0.5
	 */
	public static Kernel create(KernelAgentFactory factory, String applicationName, EventListener startUpListener) {
		return create((Boolean)null, null, null, startUpListener, applicationName, factory);
	}

	/**
	 * Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param factory is the kernel agent factory to use.
	 * @return a kernel agent.
	 */
	public static Kernel create(Boolean commitSuicide, KernelAgentFactory factory) {
		return create(commitSuicide, null, null, null, null, factory);
	}

	/**
	 * Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * The created kernel agent is able to commit suicide
	 * depending on {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @param factory is the kernel agent factory to use.
	 * @return a kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(Boolean commitSuicide, String applicationName, KernelAgentFactory factory) {
		return create(commitSuicide, null, null, null, applicationName, factory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param factory is the kernel agent factory to use when creating the agent.
	 * @return a new kernel agent.
	 */
	public static Kernel create(
			Boolean commitSuicide,
			AgentActivator activator, KernelTimeManager timeManager,
			EventListener startUpListener,
			KernelAgentFactory factory) {
		return create(commitSuicide, activator, timeManager, startUpListener, null, factory);
	}

	/** Create a new kernel agent even if another one
	 * already exists.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param timeManager is the time manager to use, or <code>null</code> to use the default one.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the kernel.
	 * @param factory is the kernel agent factory to use when creating the agent.
	 * @return a new kernel agent.
	 * @since 0.4
	 */
	public static Kernel create(
			Boolean commitSuicide,
			AgentActivator activator,
			KernelTimeManager timeManager,
			EventListener startUpListener,
			String applicationName,
			KernelAgentFactory factory) {
		AgentActivator act = activator;
		if (act==null)
			act = new AgentActivator();
		
		if (factory==null) {
			KernelAgent k = new KernelAgent(
					act, commitSuicide, timeManager, 
					startUpListener, applicationName);
			return k.toKernel();
		}

		try {
			KernelAgent k = factory.newInstance(commitSuicide, act, startUpListener, applicationName);
			if (k==null) throw new Error();
			return k.toKernel();
		}
		catch(AssertionError ae) {
			throw ae;
		}
		catch(Error e) {
			throw e;
		}
		catch(Throwable e) {
			throw new Error(e);
		}
	}
	
	/** Send a termination request to all the kernel agents.
	 * <p>
	 * This function invokes {@link Kernel#kill()} on
	 * all existing and registered kernels.
	 * <p>
	 * The kernel agents may not die immediately after the
	 * invocation of this function.
	 */
	public static void killAll() {
		synchronized(kernelAgents) {
			for(KernelAgent k : kernelAgents.values()) {
				k.killMe();
			}
		}
	}
	
	/** Kill all the kernel agent threads.
	 * <p>
	 * This function does not invokes {@link Kernel#kill()} on
	 * all existing and registered kernels.
	 * It directly shutting down all the execution resources
	 * (ie. threads) of the kernel agents.
	 * <p>
	 * Caution: Invoking this function may put your virtual machine
	 * inside an invalid state.
	 */
	static void shutdownNow() {
		synchronized(kernelAgents) {
			Collection<KernelAgent> agents = kernelAgents.values();
			KernelAgent[] tab = new KernelAgent[agents.size()];
			agents.toArray(tab);
			for(KernelAgent agent : tab) {
				agent.shutdownNow();
			}
			kernelAgents.clear();
			// Collect memory
			System.gc();
			System.gc();
			System.gc();
		}
	}

	/** Replies if the given address is corresponding by one
	 * of the kernels.
	 * 
	 * @param adr
	 * @return <code>true</code> if one kernel has the given
	 * address, otherwise <code>false</code>
	 */
	public static boolean contains(AgentAddress adr) {
		assert(adr!=null);
		synchronized(kernelAgents) {
			return kernelAgents.containsKey(adr);
		}
	}

	/** Replies the kernel with the given address.
	 * 
	 * @param adr
	 * @return the kernel or <code>null</code> if not found.
	 */
	public static Kernel get(AgentAddress adr) {
		assert(adr!=null);
		KernelAgent k; 
		synchronized(kernelAgents) {
			k = kernelAgents.get(adr);
		}
		return (k==null) ? null : k.toKernel();
	}
	
	/** Replies the count of kernels active or not.
	 * 
	 * @return the count of active kernels.
	 */
	public static int getKernelCount() {
		synchronized(kernelAgents) {
			return kernelAgents.size();
		}
	}

	/** Replies if the given address is corresponding by one
	 * of the kernels.
	 * 
	 * @return <code>true</code> if one kernel has the given
	 * address, otherwise <code>false</code>
	 */
	public static Iterator<Kernel> iterator() {
		Iterator<KernelAgent> iterator;
		synchronized(kernelAgents) {
			iterator = kernelAgents.values().iterator();
		}
		return new KernelIterator(iterator);
	}

	/** Set the preferred factory for new instances of kernel agents.
	 * 
	 * @param factory is the preferred factory. If <code>null</code> uses
	 * default one.
	 */
	public static void setPreferredKernelFactory(KernelAgentFactory factory) {
		defaultKernelFactory = factory;
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class KernelIterator implements Iterator<Kernel> {

		private final Iterator<KernelAgent> iterator;
		
		/**
		 * @param i
		 */
		public KernelIterator(Iterator<KernelAgent> i) {
			this.iterator = i;
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public Kernel next() {
			return this.iterator.next().toKernel();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
		
}
