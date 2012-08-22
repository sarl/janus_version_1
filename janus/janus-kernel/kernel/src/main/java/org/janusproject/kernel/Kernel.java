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

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agent.AgentActivator;
import org.janusproject.kernel.agent.AgentLifeState;
import org.janusproject.kernel.agent.AgentLifeStateListener;
import org.janusproject.kernel.agent.ChannelManager;
import org.janusproject.kernel.agent.KernelContext;
import org.janusproject.kernel.agent.ProbeManager;
import org.janusproject.kernel.configuration.JanusProperty;
import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.GroupListener;
import org.janusproject.kernel.crio.organization.MembershipService;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
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
     * Causes the current thread to wait until the kernel has terminated
     * its execution or a thread has invoked {@link Object#notify()}
     * or {@link Object#notifyAll()} on the instance of this object.
     * In other words, this function extends the behavior of 
     * {@link Object#wait()} by adding the termination of the kernel
     * as a critera to wake up.
     * <p>
     * In opposite to {@link Object#wait()}, this function does not
     * requires to explicitly get ownership of this object's monitor. 
	 * 
	 * @throws InterruptedException
	 * @since 0.5
	 */
	public void waitUntilTermination() throws InterruptedException;

	/**
     * Causes the current thread to wait until the kernel has terminated
     * its execution or a thread has invoked {@link Object#notify()}
     * or {@link Object#notifyAll()} on the instance of this object.
     * In other words, this function extends the behavior of 
     * {@link Object#wait()} by adding the termination of the kernel
     * as a critera to wake up.
     * <p>
     * In opposite to {@link Object#wait()}, this function does not
     * requires to explicitly get ownership of this object's monitor. 
	 * 
	 * @param timeout is the maximal time to wait for the termination in milliseconds.
	 * @throws InterruptedException
	 * @since 0.5
	 */
	public void waitUntilTermination(long timeout) throws InterruptedException;

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

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param organization
	 *            is the organization that must be instanced
	 * @param obtainConditions
	 *            is the list of conditions to respect to enter in the group
	 * @param leaveConditions
	 *            is the list of conditions to respect to leave out of the group
	 * @return The address of the group freshly created
	 * @since 0.5
	 */
	public GroupAddress createGroup(
			Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions);

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param organization
	 *            is the organization that must be instanced
	 * @param obtainConditions
	 *            is the list of conditions to respect to enter in the group
	 * @param leaveConditions
	 *            is the list of conditions to respect to leave out of the group
	 * @param groupName is the name of the group.
	 * @return The address of the group freshly created
	 * @since 0.5
	 */
	public GroupAddress createGroup(
			Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			String groupName);

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @param obtainConditions
	 *            is the list of conditions to respect to enter in the group
	 * @param leaveConditions
	 *            is the list of conditions to respect to leave out of the group
	 * @return The address of the group freshly created
	 * @since 0.5
	 */
	public GroupAddress createGroup(
			OrganizationFactory<? extends Organization> factory,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions);

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @param obtainConditions
	 *            is the list of conditions to respect to enter in the group
	 * @param leaveConditions
	 *            is the list of conditions to respect to leave out of the group
	 * @param groupName is the name of the group.
	 * @return The address of the group freshly created
	 * @since 0.5
	 */
	public GroupAddress createGroup(
			OrganizationFactory<? extends Organization> factory,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			String groupName);

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param organization
	 *            is the organization that must be instanced
	 * @return The address of the group freshly created
	 * @since 0.5
	 */
	public GroupAddress createGroup(Class<? extends Organization> organization);

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param organization
	 *            is the organization that must be instanced
	 * @param groupName is the name of the group.
	 * @return The address of the group freshly created
	 * @since 0.5
	 */
	public GroupAddress createGroup(Class<? extends Organization> organization, String groupName);

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager
	 * 
	 * @param factory
	 *            is the organization factory which may be used to create the
	 *            organization instance.
	 * @return The address of the group freshly created
	 * @since 0.5
	 */
	public GroupAddress createGroup(OrganizationFactory<?> factory);

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, do not create a new one.
	 * 
	 * @param organization
	 *            - the organization that the group have to implement
	 * @return the address of the group, or <code>null</code>
	 * @since 0.5
	 */
	public GroupAddress getExistingGroup(Class<? extends Organization> organization);

	/**
	 * Return all known groups of an organization.
	 * 
	 * @param organization
	 *            the organization that the group have to implement
	 * @return all known groups
	 * @since 0.5
	 */
	public List<GroupAddress> getExistingGroups(Class<? extends Organization> organization);

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, do not create a new one.
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @return the address of the group, or <code>null</code>
	 * @since 0.5
	 */
	public GroupAddress getExistingGroup(OrganizationFactory<? extends Organization> factory);

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param organization
	 *            - the organization that the group have to implement
	 * @return the address of the group
	 * @since 0.5
	 */
	public GroupAddress getOrCreateGroup(Class<? extends Organization> organization);

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param organization
	 *            - the organization that the group have to implement
	 * @param groupName is the name of the group, used only when creating a new group.
	 * @return the address of the group
	 * @since 0.5
	 */
	public GroupAddress getOrCreateGroup(
			Class<? extends Organization> organization,
			String groupName);

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @return the address of the group
	 * @since 0.5
	 */
	public GroupAddress getOrCreateGroup(
			OrganizationFactory<? extends Organization> factory);

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one
	 * 
	 * @param factory
	 *            is the organization factory which may be used to instance
	 *            organization.
	 * @param groupName is the name of the group, used only when creating a new group.
	 * @return the address of the group
	 * @since 0.5
	 */
	public GroupAddress getOrCreateGroup(
			OrganizationFactory<? extends Organization> factory,
			String groupName);

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization with the specified ID if any, or create a new one with the specified id.
	 * 
	 * @param id is the desired ID for the group.
	 * @param organization is the organization implemented by the group.
	 * @param obtainConditions are the obtain conditions to pass to the newly created group.
	 * @param leaveConditions are the leave conditions to pass to the newly created group.
	 * @param membership is the membership descriptor to pass to the newly created group.
	 * @param distributed indicates if the newly created group is marked as distributed or not. 
	 * @param persistent indicates if the newly created group is marked as persistent or not. 
	 * @param groupName is the name associated to the newly created group. 
	 * @return the address of the group, never <code>null</code>.
	 * @since 0.5
	 */
	public GroupAddress getOrCreateGroup(UUID id,
			Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions,
			MembershipService membership,
			boolean distributed,
			boolean persistent,
			String groupName);

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization with the specified ID if any, or create a new one
	 * 
	 * @param id
	 *            The desired ID for the group
	 * @param organization
	 *            - the organization that the group have to implement
	 * @param groupName is the name of the group, used only when creating a new group.
	 * @return the address of the group
	 * @since 0.5
	 */
	public GroupAddress getOrCreateGroup(UUID id,
			Class<? extends Organization> organization,
			String groupName);

}
