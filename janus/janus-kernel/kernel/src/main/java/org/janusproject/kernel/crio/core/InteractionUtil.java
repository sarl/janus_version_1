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

import java.util.logging.Level;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.interaction.UnspecifiedGroupMessageException;
import org.janusproject.kernel.crio.interaction.UnspecifiedReceiverRoleMessageException;
import org.janusproject.kernel.crio.interaction.UnspecifiedSenderRoleMessageException;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageException;
import org.janusproject.kernel.message.MessageFactory;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;


/**
 * Private utilities to enable interactions between roles.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class InteractionUtil extends MessageFactory {

	/**
	 * Send the specified <code>Message</code> to all the players of the given role,
	 * except the sender if it is playing the role.
	 * 
	 * @param creationDate is the date of creation of the message.
	 * @param sender is the role that is sending the element.
	 * @param receiverRole is the role which may receive the message.
	 * @param message is the message to send
	 * @param changeSender indicates if the sender in the message may be changed.
	 * @param includeSender indicates if the message sender may also receive
	 * the message.
	 */
	public static void broadcastMessage(
			float creationDate,
			RoleAddress sender,
			Class<? extends Role> receiverRole,
			Message message,
			boolean changeSender,
			boolean includeSender) {
		if (sender==null) throw new UnspecifiedSenderRoleMessageException(message);
		if (receiverRole==null) throw new UnspecifiedReceiverRoleMessageException(message);

		assert(message!=null);
		float date = message.getCreationDate();
		if (Float.isNaN(date)) date = creationDate;
		
		Address msgSender = message.getSender();
		RoleAddress selectedSenderAddress = (msgSender instanceof RoleAddress) ? (RoleAddress)msgSender : null;
		if (changeSender || selectedSenderAddress==null) {
			selectedSenderAddress = sender; 
		}
		
		KernelScopeGroup groupInstance = sender.getGroupObject();
		assert(groupInstance!=null);
		
		setCreationDate(message, date);
		setSender(message, selectedSenderAddress);
		setReceiver(message, new RoleAddress(groupInstance.getAddress(), receiverRole, null));

		KernelScopeGroup groupObject = sender.getGroupObject();
		try {
			groupInstance.broadcastMessage(message, includeSender);
		}
		catch(MessageException e) {
			/*if (InteractionUtil.class.desiredAssertionStatus()) {
				throw e;
			}*/
			groupObject.getLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Send the specified <code>Message</code> to a role of one agent.
	 * 
	 * @param creationDate is the date of creation of the message.
	 * @param sender is the role that is sending the element.
	 * @param receiver is the role that is receiving the element.
	 * @param message is the message to send
	 * @param changeSender indicates if the sender in the message may be changed.
	 * @param includeSender indicates if the message sender may also receive
	 * the message.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 */
	public static RoleAddress sendMessage(
			float creationDate,
			RoleAddress sender,
			RoleAddress receiver,
			Message message,
			boolean changeSender,
			boolean includeSender) {
		if (sender==null) throw new UnspecifiedSenderRoleMessageException(message);
		if (receiver==null) throw new UnspecifiedReceiverRoleMessageException(message);

		if (!sender.getGroup().equals(receiver.getGroup()))
			throw new UnspecifiedGroupMessageException(message);
		
		if (receiver.getPlayer()==null) {
			return sendMessage(creationDate, sender, receiver, null, message, changeSender, includeSender);
		}
		
		assert(message!=null);

		float date = message.getCreationDate(); 
		if (Float.isNaN(date)) date = creationDate;
		
		Address msgSender = message.getSender();
		RoleAddress selectedSenderAddress = (msgSender instanceof RoleAddress) ? (RoleAddress)msgSender : null;
		if (changeSender || selectedSenderAddress==null) {
			selectedSenderAddress = sender; 
		}
		
		setCreationDate(message, date);
		setSender(message, selectedSenderAddress);
		setReceiver(message, receiver);

		KernelScopeGroup groupObject = sender.getGroupObject();
		try {
			return groupObject.sendMessage(message, includeSender);
		}
		catch(MessageException e) {
			/*if (InteractionUtil.class.desiredAssertionStatus()) {
				throw e;
			}*/
			groupObject.getLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * Send the specified <code>Message</code> to a role of one agent.
	 * 
	 * @param creationDate is the date of creation of the message.
	 * @param sender is the role that is sending the element.
	 * @param receiver is the role that is receiving the element.
	 * @param policy is the policy to select the receiver.
	 * @param message is the message to send
	 * @param changeSender indicates if the sender in the message may be changed.
	 * @param includeSender indicates if the message sender may also receive
	 * the message.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 */
	public static RoleAddress sendMessage(
			float creationDate,
			RoleAddress sender,
			RoleAddress receiver,
			MessageReceiverSelectionPolicy policy,
			Message message,
			boolean changeSender,
			boolean includeSender) {
		if (sender==null) throw new UnspecifiedSenderRoleMessageException(message);
		if (receiver==null) throw new UnspecifiedReceiverRoleMessageException(message);

		assert(message!=null);

		float date = message.getCreationDate(); 
		if (Float.isNaN(date)) date = creationDate;

		MessageReceiverSelectionPolicy pol = policy;
		if (pol==null) pol = MessageReceiverSelectionPolicy.RANDOM_SELECTION;
		
		KernelScopeGroup group = sender.getGroupObject();
		assert(group!=null);
		
		
		AgentAddress receiverAddress = pol.selectEntity(
				sender.getPlayer(),
				group.getRolePlayers(receiver.getRole()));
		if (receiverAddress==null) return null;
		
		Address msgSender = message.getSender();
		RoleAddress selectedSenderAddress = (msgSender instanceof RoleAddress) ? (RoleAddress)msgSender : null;
		if (changeSender || selectedSenderAddress==null) {
			selectedSenderAddress = sender; 
		}
		
		receiver.setPlayer(receiverAddress);
		setCreationDate(message, date);
		setSender(message, selectedSenderAddress);
		setReceiver(message, receiver);

		KernelScopeGroup groupObject = sender.getGroupObject();
		try {
			return groupObject.sendMessage(message, includeSender);
		}
		catch(MessageException e) {
			/*if (InteractionUtil.class.desiredAssertionStatus()) {
				throw e;
			}*/
			groupObject.getLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
			return null;
		}
	}
	
}