/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivator;
import org.janusproject.kernel.agent.AgentLifeState;
import org.janusproject.kernel.agent.AgentLifeStateListener;
import org.janusproject.kernel.agent.ChannelManager;
import org.janusproject.kernel.agent.KernelContext;
import org.janusproject.kernel.agent.ProbeManager;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.channels.ChannelInteractableListener;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.logger.LoggerProvider;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * Interface which is providing services of a Janus kernel.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Kernel extends LoggerProvider {
	
	/**
	 * Replies the address of the kernel agent.
	 * 
	 * @return the address of the kernel agent.
	 */
	public AgentAddress getAddress();

	/**
	 * Replies the CRIO context managed by the kernel agent.
	 * 
	 * @return the CRIO context managed by the kernel agent.
	 */
	public CRIOContext getCRIOContext();

	/**
	 * Replies the Kernel context managed by the kernel agent.
	 * 
	 * @return the Kernel context managed by the kernel agent.
	 */
	public KernelContext getKernelContext();

	/**
	 * Replies if the kernel agent is alive.
	 * 
	 * @return <code>true</code> if the kernel agent is alive,
	 * otherwise <code>false</code>
	 */
	public boolean isAlive();

	/** Replies if this kernel agent is able to commit suicide.
	 * <p>
	 * When an kernel agent is able to commit suicide is will automatically
	 * kill itself when its has no more role to play nor holon to schedule.
	 * By default kernel agent is able to commit suicide according to
	 * the value of the variable {@link JanusProperty#JANUS_KERNEL_KEEP_ALIVE}.
	 * 
	 * @return <code>true</code> if the kernel agent wants to commit a suicide at least,
	 * otherwise <code>false</code>
	 */
	public boolean canCommitSuicide();

	/**
	 * Replies the state of the kernel agent.
	 * 
	 * @return the state of the kernel agent.
	 */
	public AgentLifeState getState();

	/** Launch immediately the given agent as a light agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 */
	public AgentAddress launchLightAgent(
			Agent agent, 
			String name,
			Object... initParameters);

	/** Launch immediately the given agent as a light agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 */
	public AgentAddress launchLightAgent(
			Agent agent,
			Object... initParameters);

	/** Launch immediately the given agent as a light agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param activator is the activator to use, never <code>null</code>.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 */
	public AgentAddress launchLightAgent(
			Agent agent, 
			AgentActivator activator,
			Object... initParameters);

	/** Launch immediately the given agent as a light agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param activator is the activator to use, never <code>null</code>.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 */
	public AgentAddress launchLightAgent(
			Agent agent, 
			String name,
			AgentActivator activator,
			Object... initParameters);
	
	/** Launch immediately the given agent as a heavy agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the kernel agent initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 */
	public AgentAddress launchHeavyAgent(
			Agent agent,
			Object... initParameters);

	/** Launch immediately the given agent as a heavy agent.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the kernel agent initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 */
	public AgentAddress launchHeavyAgent(
			Agent agent, 
			String name,
			Object... initParameters);

	/** Submit the given agent to be launched
	 * as a light agent at the next invocation
	 * of {@link #launchDifferedExecutionAgents()}.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @see #launchDifferedExecutionAgents()
	 */
	public AgentAddress submitLightAgent(
			Agent agent, 
			String name,
			Object... initParameters);

	/** Submit the given agent to be launched
	 * as a light agent at the next invocation
	 * of {@link #launchDifferedExecutionAgents()}.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @see #launchDifferedExecutionAgents()
	 */
	public AgentAddress submitLightAgent(
			Agent agent,
			Object... initParameters);

	/** Submit the given agent to be launched
	 * as a light agent at the next invocation
	 * of {@link #launchDifferedExecutionAgents()}.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param activator is the activator to use, never <code>null</code>.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @see #launchDifferedExecutionAgents()
	 */
	public AgentAddress submitLightAgent(
			Agent agent, 
			AgentActivator activator,
			Object... initParameters);

	/** Submit the given agent to be launched
	 * as a light agent at the next invocation
	 * of {@link #launchDifferedExecutionAgents()}.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the activator's initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param activator is the activator to use, never <code>null</code>.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @see #launchDifferedExecutionAgents()
	 */
	public AgentAddress submitLightAgent(
			Agent agent, 
			String name,
			AgentActivator activator,
			Object... initParameters);
	
	/** Submit the given agent to be launched
	 * as a heavy agent at the next invocation
	 * of {@link #launchDifferedExecutionAgents()}.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the kernel agent initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @see #launchDifferedExecutionAgents()
	 */
	public AgentAddress submitHeavyAgent(
			Agent agent,
			Object... initParameters);

	/** Submit the given agent to be launched
	 * as a heavy agent at the next invocation
	 * of {@link #launchDifferedExecutionAgents()}.
	 * <p>
	 * If initialization parameters are given,
	 * they are passed to <code>activate()</code>,
	 * otherwise the kernel agent initialization
	 * parameters will be passed.
	 * 
	 * @param agent is the agent to initialize and launch
	 * @param name is the name of the agent.
	 * @param initParameters are the parameters to pass to activate.
	 * @return the address of the launched agent on the kernel.
	 * @see #launchDifferedExecutionAgents()
	 */
	public AgentAddress submitHeavyAgent(
			Agent agent, 
			String name,
			Object... initParameters);
	
	/** Launch all the agents which has been
	 * previously submitted as differed execution agents.
	 */
	public void launchDifferedExecutionAgents();

	/** Kill this kernel agent.
	 * 
	 * @return the status of the operation
	 */
	public Status kill();
	
	/** Add listener on kernel events.
	 * 
	 * @param listener
	 */
	public void addKernelListener(KernelListener listener);

	/** Remove listener on kernel events.
	 * 
	 * @param listener
	 */
	public void removeKernelListener(KernelListener listener);

	/** Add listener on life state changes.
	 * 
	 * @param listener
	 * @deprecated see {@link #addAgentLifeStateListener(AgentLifeStateListener)}
	 */
	@Deprecated
	public void addAgentLifeStageListener(AgentLifeStateListener listener);

	/** Remove listener on life state changes.
	 * 
	 * @param listener
	 * @deprecated see {@link #removeAgentLifeStateListener(AgentLifeStateListener)}
	 */
	@Deprecated
	public void removeAgentLifeStageListener(AgentLifeStateListener listener);
	
	/** Add listener on life state changes.
	 * 
	 * @param listener
	 */
	public void addAgentLifeStateListener(AgentLifeStateListener listener);

	/** Remove listener on life state changes.
	 * 
	 * @param listener
	 */
	public void removeAgentLifeStateListener(AgentLifeStateListener listener);

	/** Add listener on group events.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	public void addGroupListener(GroupListener listener);

	/** Add listener on group events.
	 * 
	 * @param listener
	 * @since 0.5
	 */
	public void removeGroupListener(GroupListener listener);

	/**
	 * Adds a {@link ChannelInteractableListener}
	 * @param listener
	 * @deprecated see {@link #getChannelManager()} and {@link ChannelManager#addChannelIteractableListener(ChannelInteractableListener)}.
	 */
	@Deprecated
	public void addChannelIteractableListener(ChannelInteractableListener listener);
	
	/**
	 * Removes a {@link ChannelInteractableListener}
	 * @param listener
	 * @deprecated see {@link #getChannelManager()} and {@link ChannelManager#removeChannelIteractableListener(ChannelInteractableListener)}.
	 */
	@Deprecated
	public void removeChannelIteractableListener(ChannelInteractableListener listener);
	
	/**
	 * Return the {@link ChannelInteractable} Interface for the agent.
	 * Only local agents can be reached this way.
	 * 
	 * @param address
	 * @return the {@link ChannelInteractable} interface for the agent if the
	 *         agent is local and if it implements the interface, <code>null</code> otherwise.
	 * @deprecated see {@link #getChannelManager()} and {@link ChannelManager#getChannelInteractable(AgentAddress)}.
	 */
	@Deprecated
	public ChannelInteractable getChannelInteractable(AgentAddress address);

	/**
	 * Return the manager of channels for this kernel.
	 * 
	 * @return the manager of channels.
	 * @since 0.5
	 */
	public ChannelManager getChannelManager();

	/**
	 * Replies the probe manager used in the current kernel context.
	 * 
	 * @return the probe manager in the current context.
	 * @since 0.4
	 */
	public ProbeManager getProbeManager();
	
	/** Replies the agents which are running on the current kernel.
	 * The replied collection includes the kernel agent itself.
	 * 
	 * @return the agents which are running on the current kernel.
	 * @since 0.4
	 */
	public SizedIterator<AgentAddress> getAgents();

	/**
	 * Pause the kernel and all the agents supported by the kernel.
	 * <p>
	 * When the kernel is pausing, it does not responds to any stimuli.
	 * 
	 * @see #isPaused()
	 * @see #resume()
	 * @since 0.5
	 */
	public void pause();

	/**
	 * Replies if the kernel was paused.
	 * <p>
	 * When the kernel is pausing, it does not responds to any stimuli.
	 * 
	 * @return <code>true</code> if the kernel was paused, otherwise <code>false</code>.
	 * @see #pause()
	 * @see #resume()
	 * @since 0.5
	 */
	public boolean isPaused();

	/**
	 * Resume from a pause of the kernel and all the agents supported by the kernel.
	 * <p>
	 * When the kernel is pausing, it does not responds to any stimuli.
	 * 
	 * @see #pause()
	 * @see #isPaused()
	 * @since 0.5
	 */
	public void resume();

}
