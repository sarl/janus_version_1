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
package org.janusproject.kernel.crio.core;

import java.security.AccessControlContext;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityCallException;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.Role.MessageTransportService;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.organization.OrganizationFactory;
import org.janusproject.kernel.crio.role.RoleFactory;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * This class provide the minimal definition of a implementation of capacity
 * which is usable from a role.
 * <p>
 * It permits to receive and send messages as if it a the invocating role
 * which receive and send these messages.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class RoleCapacityImplementation
extends CapacityImplementation {

	/**
	 * @param type is the type of implementation.
	 */
	public RoleCapacityImplementation(CapacityImplementationType type) {
		super(type);
	}
	
	/**
	 * Defines an atomic capacity implementation.
	 */
	public RoleCapacityImplementation() {
		super();
	}

	private static GroupCapacityContext castContext(CapacityContext context) {
		assert(context!=null);
		try {
			return (GroupCapacityContext)context;
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable e) {
			throw new CapacityCallException(e);
		}
	}
		
	/**
	 * Send the specified <code>Message</code> to a role of one agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which may receive the message.
	 * @param receiver is the agent which may receive the message.
	 * @param message is the message to send
	 * @return the address of the receiver.
	 * @see #call(CapacityContext)
	 * @MESSAGEAPI
	 */
	protected static final RoleAddress sendMessage(
			CapacityContext callContext,
			Class<? extends Role> role,
			AgentAddress receiver,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				new RoleAddress(context.getGroupAddress(), role, receiver),
				message,
				true,
				getMessageTransportService(callContext).isSendMessageFeedBack());
	}

	/**
	 * Send the specified <code>Message</code> to the specified role.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param receiver is the receiver of the message.
	 * @param message is the message to send
	 * @return the address of the receiver.
	 * @see #call(CapacityContext)
	 * @MESSAGEAPI
	 * @since 0.5
	 */
	protected static final RoleAddress sendMessage(
			CapacityContext callContext,
			RoleAddress receiver,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				receiver,
				message,
				true,
				getMessageTransportService(callContext).isSendMessageFeedBack());
	}

	/**
	 * Forward the specified <code>Message</code> to the role and agent
	 * in the message.
	 * <p>
	 * This function does not change the emitter and receiver of the message. 
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param message is the message to send
	 * @return the address of the receiver.
	 * @MESSAGEAPI
	 */
	protected static final RoleAddress forwardMessage(
			CapacityContext callContext,
			Message message) {
		assert(message!=null);
		GroupCapacityContext context = castContext(callContext);
		assert(message.getReceiver() instanceof RoleAddress);
		RoleAddress receiver = (RoleAddress)message.getReceiver();
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				receiver,
				message,
				false,
				getMessageTransportService(callContext).isForwardMessageFeedBack());
	}

	/**
	 * Send the specified <code>Message</code> to a role of one agent.
	 * <p>
	 * This function does not change the emitter of the message. 
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which may receive the message.
	 * @param receiver is the agent which may receive the message.
	 * @param message is the message to send
	 * @return the address of the receiver.
	 * @MESSAGEAPI
	 */
	protected static final RoleAddress forwardMessage(
			CapacityContext callContext,
			Class<? extends Role> role,
			AgentAddress receiver,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				new RoleAddress(context.getGroupAddress(), role, receiver),
				message,
				false,
				getMessageTransportService(callContext).isForwardMessageFeedBack());
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary selected agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which may receive the message.
	 * @param message is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected static final RoleAddress sendMessage(
			CapacityContext callContext,
			Class<? extends Role> role,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				new RoleAddress(context.getGroupAddress(), role, null),
				message,
				true,
				getMessageTransportService(callContext).isSendMessageFeedBack());
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary selected agent.
	 * <p>
	 * This function does not change the emitter of the message. 
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which may receive the message.
	 * @param message is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected static final RoleAddress forwardMessage(
			CapacityContext callContext,
			Class<? extends Role> role,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				new RoleAddress(context.getGroupAddress(), role, null),
				message,
				false,
				getMessageTransportService(callContext).isForwardMessageFeedBack());
	}

	/**
	 * Send the specified <code>Message</code> to the specified role.
	 * <p>
	 * This function does not change the emitter of the message. 
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param receiver is the role which may receive the message.
	 * @param message is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected static final RoleAddress forwardMessage(
			CapacityContext callContext,
			RoleAddress receiver,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				receiver,
				message,
				false,
				getMessageTransportService(callContext).isForwardMessageFeedBack());
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary selected agent.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which may receive the message.
	 * @param policy is the receiver selection policy.
	 * @param message is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected static final RoleAddress sendMessage(
			CapacityContext callContext,
			Class<? extends Role> role,
			MessageReceiverSelectionPolicy policy,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				new RoleAddress(context.getGroupAddress(), role, null),
				policy,
				message,
				true,
				getMessageTransportService(callContext).isSendMessageFeedBack());
	}

	/**
	 * Send the specified <code>Message</code> to a role of one arbitrary selected agent.
	 * <p>
	 * This function does not change the emitter of the message. 
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which may receive the message.
	 * @param policy is the receiver selection policy.
	 * @param message is the message to send
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 * @MESSAGEAPI
	 */
	protected static final RoleAddress forwardMessage(
			CapacityContext callContext,
			Class<? extends Role> role,
			MessageReceiverSelectionPolicy policy,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		return InteractionUtil.sendMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				new RoleAddress(context.getGroupAddress(), role, null),
				policy,
				message,
				false,
				getMessageTransportService(callContext).isForwardMessageFeedBack());
	}

	/**
	 * Send the specified <code>Message</code> to all the players of the given role,
	 * except the sender if it is playing the role.
	 * <p>
	 * This function force the emitter of the message to be this role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which may receive the message.
	 * @param message is the message to send
	 * @MESSAGEAPI
	 */
	protected static final void broadcastMessage(
			CapacityContext callContext,
			Class<? extends Role> role,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		InteractionUtil.broadcastMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				role,
				message,
				true,
				getMessageTransportService(callContext).isBroadcastMessageFeedBack());
	}

	/**
	 * Forward the specified <code>Message</code> to all the players of the given role,
	 * except the sender if it is playing the role.
	 * <p>
	 * This function does not change the emitter of the message. 
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which may receive the message.
	 * @param message is the message to send
	 * @MESSAGEAPI
	 */
	protected static final void forwardBroadcastMessage(
			CapacityContext callContext,
			Class<? extends Role> role,
			Message message) {
		GroupCapacityContext context = castContext(callContext);
		MessageTransportService mts = getMessageTransportService(callContext);
		InteractionUtil.broadcastMessage(
				context.getTimeManager().getCurrentTime(),
				context.getRoleAddress(),
				role,
				message,
				false,
				mts.isForwardMessageFeedBack() && mts.isBroadcastMessageFeedBack());
	}

	/**
	 * Replies the mailbox for the calling role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the mailbox of the given role, or <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected static final Mailbox getMailbox(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		return context.getRole().getMailbox();
	}

	/**
	 * Replies the message transport service for the calling role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the mts of the given role, or <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected static final MessageTransportService getMessageTransportService(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		return context.getRole().getMessageTransportService();
	}

	/**
	 * Replies the first available message in the calling-role mail box
	 * and remove it from the mailbox.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the first available message, or <code>null</code> if
	 * the mailbox is empty.
	 * @MESSAGEAPI
	 */
	protected static final Message getMessage(CapacityContext callContext) {
		return getMailbox(callContext).removeFirst();
	}
	
	/**
	 * Replies the first available message in the calling-role mail box
	 * and leave it inside the mailbox.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the first available message, or <code>null</code> if
	 * the mailbox is empty.
	 * @MESSAGEAPI
	 */
	protected static final Message peekMessage(CapacityContext callContext) {
		return getMailbox(callContext).getFirst();
	}
	
	/**
	 * Replies the messages in the calling-role mailbox.
	 * Each time an message is consumed
	 * from the replied iterable object,
	 * the corresponding message is removed
	 * from the mailbox.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return all the messages, never <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected static final Iterator<Message> getMessages(CapacityContext callContext) {
		return getMailbox(callContext).iterator(true);
	}
	
	/**
	 * Replies the messages in the calling-role mailbox.
	 * Each time an message is consumed
	 * from the replied iterable object,
	 * the corresponding message is NOT removed
	 * from the mailbox.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return all the messages, never <code>null</code>.
	 * @MESSAGEAPI
	 */
	protected static final Iterator<Message> peekMessages(CapacityContext callContext) {
		return getMailbox(callContext).iterator(false);
	}

	/** Indicates if the calling-role mailbox contains a message or not.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return <code>true</code> if the message contains at least one message,
	 * otherwise <code>false</code>
	 * @MESSAGEAPI
	 */
	protected static final boolean hasMessage(CapacityContext callContext) {
		return !getMailbox(callContext).isEmpty();
	}
	
	/**
	 * Replies the number of messages in the calling-role mailbox.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the number of messages in the mailbox.
	 * @MESSAGEAPI
	 */
	protected static final long getMailboxSize(CapacityContext callContext) {
		return getMailbox(callContext).size();
	}
	
	/**
	 * Replies the instance for the given organization.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param organization is the organization that must be instanced.
	 * @return organization instance in the current context.
	 * @GROUPAPI
	 */
	protected static final Organization getOrganization(
			CapacityContext callContext,
			Class<? extends Organization> organization) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.getOrganization(organization);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param organization is the organization that must be instanced
	 * @param obtainConditions is the list of conditions to respect to enter in the group
	 * @param leaveConditions is the list of conditions to respect to leave out of the group 
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected static final GroupAddress createGroup(
			CapacityContext callContext,
			Class<? extends Organization> organization,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.createGroup(organization, obtainConditions, leaveConditions);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param factory is the organization factory to use to create an organization instance
	 * when required by the CRIO context.
	 * @param obtainConditions is the list of conditions to respect to enter in the group
	 * @param leaveConditions is the list of conditions to respect to leave out of the group 
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected static final GroupAddress createGroup(
			CapacityContext callContext,
			OrganizationFactory<? extends Organization> factory,
			Collection<? extends GroupCondition> obtainConditions,
			Collection<? extends GroupCondition> leaveConditions) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.createGroup(factory, obtainConditions, leaveConditions);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param organization is the organization that must be instanced
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected static final GroupAddress createGroup(
			CapacityContext callContext,
			Class<? extends Organization> organization) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.createGroup(organization);
	}

	/**
	 * Creates a new group implementing the specified organization with its
	 * associated GroupManager.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param factory is the organization factory to use to create an organization instance
	 * when required by the CRIO context.
	 * @return The address of the group freshly created
	 * @GROUPAPI
	 */
	protected static final GroupAddress createGroup(
			CapacityContext callContext,
			OrganizationFactory<? extends Organization> factory) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.createGroup(factory);
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param organization -
	 *            the organization that the group have to implement
	 * @return the address of the group
	 * @GROUPAPI
	 */
	protected static final GroupAddress getOrCreateGroup(
			CapacityContext callContext,
			Class<? extends Organization> organization) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.getOrCreateGroup(organization);
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, or create a new one.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param factory is the organization factory to use to create an organization instance
	 * when required by the CRIO context.
	 * @return the address of the group
	 * @GROUPAPI
	 */
	protected static final GroupAddress getOrCreateGroup(
			CapacityContext callContext,
			OrganizationFactory<? extends Organization> factory) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.getOrCreateGroup(factory);
	}

	/**
	 * Replies the address of the group where this role is defined.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the address of the group where this role is defined.
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected static final GroupAddress getGroupAddress(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.getGroupAddress();
	}

	/**
	 * Replies the name of the role invoking this capacity.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the role of the invoker.
	 * @GROUPAPI
	 */
	protected static final Class<? extends Role> getRole(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.getClass();
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, do not create a new one.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param organization -
	 *            the organization that the group have to implement
	 * @return the address of the group, or <code>null</code>
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected static final GroupAddress getExistingGroup(
			CapacityContext callContext,
			Class<? extends Organization> organization) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.getExistingGroup(organization);
	}

	/**
	 * Get the address of an already existing group implementing the specified
	 * organization if any, do not create a new one.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param factory is the organization factory to use to create an organization instance
	 * when required by the CRIO context.
	 * @return the address of the group, or <code>null</code>
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected static final GroupAddress getExistingGroup(
			CapacityContext callContext,
			OrganizationFactory<? extends Organization> factory) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.getExistingGroup(factory);
	}

	/**
	 * Returns the list of the addresses of the players currently playing the
	 * calling role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the addresses of the entities currently playing the
	 *         current role.
	 * @GROUPAPI
	 */
	protected static final SizedIterator<AgentAddress> getPlayers(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role role = context.getRole();
		assert(role!=null);
		return role.getPlayers();
	}

	/**
	 * Returns the list of the addresses of the players currently playing the
	 * calling role in the specified group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role of which obtain the number of players.
	 * @return the addresses of the entities currently playing the
	 *         specified role.
	 * @GROUPAPI
	 */
	protected static final SizedIterator<AgentAddress> getPlayers(
			CapacityContext callContext,
			Class<? extends Role> role) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getPlayers(role);
	}

	/**
	 * Returns the list of the addresses of the entities currently playing the
	 * specified role defined on the specified group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role of which obtain the number of players
	 * @param group is the group where the role is defined
	 * @return the addresses of the entity currently playing the
	 *         specified role
	 * @GROUPAPI
	 */
	protected static final SizedIterator<AgentAddress> getPlayers(
			CapacityContext callContext, 
			Class<? extends Role> role,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getPlayers(role, group);
	}
	
	/** Replies the player's capacities.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the player's capacities.
	 * @CAPACITYAPI
	 */
	protected static final Collection<Class<? extends Capacity>> getPlayerCapacities(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getPlayerCapacities();
	}
	
	/**
	 * Replies the player's roles in all the groups.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the player's roles
	 * @ROLEAPI
	 */
	protected static final Collection<Class<? extends Role>> getPlayerRoles(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getPlayerRoles();
	}

	/**
	 * Replies the player's roles in the given group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param group is the address of the group from which roles may be extracted.
	 * @return the player's roles in the given group.
	 * @ROLEAPI
	 */
	protected static final Collection<Class<? extends Role>> getPlayerRoles(
			CapacityContext callContext,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getPlayerRoles(group);
	}

	/** Replies the buffered player's memory.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the buffered player's memory.
	 * @MINDAPI
	 */
	protected static final Memory getMemory(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getMemory();
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * specified group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the class of the requested role.
	 * @param group is the group where the requested role is defined
	 * @param initParameters is the set of parameters to pass to {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected static final RoleAddress requestRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			GroupAddress group,
			Object... initParameters) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.requestRole(role, group, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * specified group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the class of the requested role.
	 * @param group is the group where the requested role is defined
	 * @param accessContext is the context of access control to use when instanciating the role.
	 * If <code>null</code>, the default context will be used.
	 * @param initParameters is the set of parameters to pass to {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected static final RoleAddress requestRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			GroupAddress group,
			AccessControlContext accessContext,
			Object... initParameters) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.requestRole(role, group, accessContext, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * specified group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the class of the requested role.
	 * @param group is the group where the requested role is defined
	 * @param factory is the factory to invoke to create a new instance of the role.
	 * If <code>null</code> the default factory will be used.
	 * @param initParameters is the set of parameters to pass to {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected static final RoleAddress requestRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			GroupAddress group,
			RoleFactory factory,
			Object... initParameters) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.requestRole(role, group, factory, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * specified group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the class of the requested role.
	 * @param group is the group where the requested role is defined
	 * @param factory is the factory to invoke to create a new instance of the role.
	 * If <code>null</code> the default factory will be used.
	 * @param accessContext is the context of access control to use when instanciating the role.
	 * If <code>null</code>, the default context will be used.
	 * @param initParameters is the set of parameters to pass to {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected static final RoleAddress requestRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			GroupAddress group,
			RoleFactory factory,
			AccessControlContext accessContext,
			Object... initParameters) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.requestRole(role, group, factory, accessContext, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * current group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the class of the requested role.
	 * @param initParameters is the set of parameters to pass to {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected static final RoleAddress requestRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			Object... initParameters) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.requestRole(role,  initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * current group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the class of the requested role.
	 * @param accessContext is the context of access control to use when instanciating the role.
	 * If <code>null</code>, the default context will be used.
	 * @param initParameters is the set of parameters to pass to {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected static final RoleAddress requestRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			AccessControlContext accessContext,
			Object... initParameters) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.requestRole(role, accessContext, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * current group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the class of the requested role.
	 * @param factory is the factory to invoke to create a new instance of the role.
	 * If <code>null</code> the default factory will be used.
	 * @param initParameters is the set of parameters to pass to {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected static final RoleAddress requestRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			RoleFactory factory,
			Object... initParameters) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.requestRole(role, factory, initParameters);
	}

	/**
	 * Function allowing the request of the obtention of a given role on the
	 * current group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the class of the requested role.
	 * @param factory is the factory to invoke to create a new instance of the role.
	 * If <code>null</code> the default factory will be used.
	 * @param accessContext is the context of access control to use when instanciating the role.
	 * If <code>null</code>, the default context will be used.
	 * @param initParameters is the set of parameters to pass to {@link Role#activate(Object...)}.
	 * @return the address of the role if the role was taken, <code>null</code>
	 * if not.
	 * @GROUPAPI
	 */
	protected static final RoleAddress requestRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			RoleFactory factory,
			AccessControlContext accessContext,
			Object... initParameters) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.requestRole(role, factory, accessContext, initParameters);
	}

	/**
	 * Function allowing the request of the liberation of this role on the
	 * corresponding group.
	 * <p>
	 * This function assumes that a role could be played only one time
	 * by a entity inside a one group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @GROUPAPI
	 */
	protected static final void leaveRole(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		r.leaveMe();
	}

	/**
	 * Function allowing the request of the liberation of a role on the
	 * current group.
	 * <p>
	 * This function assumes that a role could be played only one time
	 * by a entity inside a one group.
	 *
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role to leave.
	 * @return <code>true</code> if the request was accepted, <code>false</code> else
	 * @GROUPAPI
	 */
	protected static final boolean leaveRole(
			CapacityContext callContext,
			Class<? extends Role> role) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.leaveRole(role);
	}

	/**
	 * Function allowing the request of the liberation of a role on the
	 * given group.
	 * <p>
	 * This function assumes that a role could be played only one time
	 * by a entity inside a one group.
	 *
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role to leave.
	 * @param group is the address of the group of the role to leave.
	 * @return <code>true</code> if the request was accepted, <code>false</code> else
	 * @GROUPAPI
	 */
	protected static final boolean leaveRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.leaveRole(role, group);
	}

	/**
	 * Returns the address of an entity currently playing the given role
	 * in the current organization.
	 * <p>
	 * The replied address is randomly selected.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which must be played by the replied address.
	 * @return the address of the entity currently playing the given role,
	 * or <code>null</code> if no entity is playing such role.
	 * @GROUPAPI
	 */
	protected static final AgentAddress getPlayer(
			CapacityContext callContext,
			Class<? extends Role> role) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getPlayer(role);
	}

	/**
	 * Returns the address of an entity currently playing this roles.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return the address of the entity currently playing this roles.
	 * @GROUPAPI
	 */
	protected static final AgentAddress getPlayer(CapacityContext callContext) {
		assert(callContext!=null);
		return callContext.getCaller().getAddress();
	}

	/**
	 * Returns the address of an entity currently playing the given role
	 * in the given organization.
	 * <p>
	 * The replied address is randomly selected.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role which must be played by the replied address.
	 * @param group is the address of the group in which an player may be selected.
	 * @return the address of the entity currently playing the given role,
	 * or <code>null</code> if no entity is playing such role.
	 * @GROUPAPI
	 */
	protected static final AgentAddress getPlayer(
			CapacityContext callContext,
			Class<? extends Role> role,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getPlayer(role, group);
	}

	/**
	 * Return all known groups of an organization.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param organization
	 * 			the organization that the group have to implement
	 * @return all known groups
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected static final List<GroupAddress> getExistingGroups(
			CapacityContext callContext,
			Class<? extends Organization> organization) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getExistingGroups(organization);
	}

	/** Replies the played roles in the whole system.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return played roles.
	 * @GROUPAPI
	 */
	protected static final Collection<Class<? extends Role>> getExistingRoles(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getExistingRoles();
	}

	/** Replies the played roles in the given group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param group is the address of the group from which played roles may be replied.
	 * @return played roles in the given group.
	 * @GROUPAPI
	 */
	protected static final SizedIterator<Class<? extends Role>> getExistingRoles(
			CapacityContext callContext,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getExistingRoles(group);
	}

	/**
	 * Return all the groups in which this calling-role's player is playing a role.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @return all the groups in which this role player is playing a role.
	 * @GROUPAPI
	 * @since 0.5
	 */
	protected static final Collection<GroupAddress> getPlayerGroups(CapacityContext callContext) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.getPlayerGroups();
	}

	/** Replies if the calling-role's player is currently playing the given 
	 * role in the current group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role to search for.
	 * @return <code>true</code> if a role is played, otherwise
	 * <code>false</code> 
	 */
	protected static final boolean isPlayingRole(CapacityContext callContext, Class<? extends Role> role) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.isPlayingRole(role);
	}

	/** Replies if the calling-role's player is currently playing the given role 
	 * in the given group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role to search for.
	 * @param group is the group of the role.
	 * @return <code>true</code> if a role is played, otherwise
	 * <code>false</code> 
	 */
	protected static final boolean isPlayingRole(
			CapacityContext callContext, 
			Class<? extends Role> role,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.isPlayingRole(role, group);
	}

	/**
	 * Replies if the given group address corresponds to an existing group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param group
	 * @return <code>true</code> if the given group address corresponds to
	 * an existing group, otherwise <code>false</code>
	 * @GROUPAPI
	 */
	protected static final boolean isGroup(
			CapacityContext callContext, 
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.isGroup(group);
	}

	/** Replies if the given role is played by any player in the current group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role to search for.
	 * @return <code>true</code> if a role is played, otherwise
	 * <code>false</code> 
	 */
	protected static final boolean isPlayedRole(
			CapacityContext callContext,
			Class<? extends Role> role) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.isPlayedRole(role);
	}

	/** Replies if the given role is played by any player in the given group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param role is the role to search for.
	 * @param group is the group of the role.
	 * @return <code>true</code> if a role is played, otherwise
	 * <code>false</code> 
	 */
	protected static final boolean isPlayedRole(
			CapacityContext callContext,
			Class<? extends Role> role,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.isPlayedRole(role, group);
	}

	/** Replies if this calling-role's player is member of the given group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param group is the group to test.
	 * @return <code>true</code> if the player in memeber of the group, otherwise
	 * <code>false</code> 
	 */
	protected static final boolean isMemberOf(
			CapacityContext callContext,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.isMemberOf(group);
	}

	/** Replies if the given player is member of the given group.
	 * 
	 * @param callContext is the context of invocation of this capacity.
	 * @param entity
	 * @param group is the group to test.
	 * @return <code>true</code> if the given player in memeber of the group, otherwise
	 * <code>false</code> 
	 */
	protected static final boolean isMemberOf(
			CapacityContext callContext,
			AgentAddress entity,
			GroupAddress group) {
		GroupCapacityContext context = castContext(callContext);
		Role r = context.getRole();
		assert(r!=null);
		return r.isMemberOf(entity, group);
	}

}