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

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.interaction.UnspecifiedGroupMessageException;
import org.janusproject.kernel.crio.interaction.UnspecifiedReceiverRoleMessageException;
import org.janusproject.kernel.crio.interaction.UnspecifiedSenderRoleMessageException;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageContext;
import org.janusproject.kernel.message.MessageContextFactory;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;


/**
 * Private utilities to enable interactions between roles.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class InteractionUtil extends MessageContextFactory {

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
		MessageContext mc = message.getContext();
		CRIOMessageContext cmc = (mc instanceof CRIOMessageContext) ? (CRIOMessageContext)mc : null;
		float date = (mc!=null) ? mc.getCreationDate() : creationDate; 
		if (Float.isNaN(date)) date = creationDate;
		
		RoleAddress selectedSenderAddress = (cmc==null) ? null : cmc.getSendingRoleAddress();
		if (changeSender || selectedSenderAddress==null) {
			selectedSenderAddress = sender; 
		}
		
		updateContext(
				message,
				selectedSenderAddress,
				null,
				receiverRole,
				date);

		sender.getGroupObject().broadcastMessage(message, includeSender);
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

		MessageContext mc = message.getContext();
		CRIOMessageContext cmc = (mc instanceof CRIOMessageContext) ? (CRIOMessageContext)mc : null;
		float date = (mc!=null) ? mc.getCreationDate() : creationDate; 
		if (Float.isNaN(date)) date = creationDate;
		
		RoleAddress selectedSenderAddress = (cmc==null) ? null : cmc.getSendingRoleAddress();
		if (changeSender || selectedSenderAddress==null) {
			selectedSenderAddress = sender; 
		}
		
		updateContext(
				message,
				selectedSenderAddress,
				receiver,
				date);

		return sender.getGroupObject().sendMessage(message, includeSender);
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

		MessageContext mc = message.getContext();
		CRIOMessageContext cmc = (mc instanceof CRIOMessageContext) ? (CRIOMessageContext)mc : null;
		float date = (mc!=null) ? mc.getCreationDate() : creationDate; 
		if (Float.isNaN(date)) date = creationDate;

		MessageReceiverSelectionPolicy pol = policy;
		if (pol==null) pol = MessageReceiverSelectionPolicy.RANDOM_SELECTION;
		
		KernelScopeGroup group = sender.getGroupObject();
		assert(group!=null);
		
		
		AgentAddress receiverAddress = pol.selectEntity(
				sender.getPlayer(),
				group.getRolePlayers(receiver.getRole()));
		if (receiverAddress==null) return null;
		
		RoleAddress selectedSenderAddress = (cmc==null) ? null : cmc.getSendingRoleAddress();
		if (changeSender || selectedSenderAddress==null) {
			selectedSenderAddress = sender; 
		}
		
		receiver.setPlayer(receiverAddress);
		updateContext(
				message,
				selectedSenderAddress,
				receiver,
				date);

		return sender.getGroupObject().sendMessage(message, includeSender);
	}

	/**
	 * Change emitter and receiver addresses for the given message.
	 * 
	 * @param message is the message to change.
	 * @param emitter is the emitter address to put inside the message.
	 * @param receiver is the receiver address to put inside the message.
	 * @param receiverRole is the role of the receiver.
	 * @param creationDate is the creation date for the message.
	 */
	static void updateContext(
			Message message,
			RoleAddress emitter,
			AgentAddress receiver,
			Class<? extends Role> receiverRole,
			float creationDate) {
		CRIOMessageContext mc = new CRIOMessageContext(
				emitter.getPlayer(),
				receiver,
				creationDate);
		mc.setEmitterReceiver(emitter, receiverRole, receiver);
		setContext(message, mc);
	}
	
	/**
	 * Change emitter and receiver addresses for the given message.
	 * 
	 * @param message is the message to change.
	 * @param emitter is the emitter address to put inside the message.
	 * @param receiver is the receiver address to put inside the message.
	 * @param creationDate is the creation date for the message.
	 */
	static void updateContext(
			Message message,
			RoleAddress emitter,
			RoleAddress receiver,
			float creationDate) {
		CRIOMessageContext mc = new CRIOMessageContext(
				emitter.getPlayer(),
				receiver.getPlayer(),
				creationDate);
		mc.setEmitterReceiver(emitter, receiver);
		setContext(message, mc);
	}
	
}