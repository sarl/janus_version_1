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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.PrivilegedPlayerAddressService;
import org.janusproject.kernel.crio.core.RolePlayer;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageFactory;
import org.janusproject.kernel.message.MessageReceiverSelectionPolicy;
import org.janusproject.kernel.message.UnspecifiedReceiverAddressMessageException;
import org.janusproject.kernel.message.UnspecifiedSelectionPolicyMessageException;
import org.janusproject.kernel.message.UnspecifiedSenderAddressMessageException;
import org.janusproject.kernel.util.random.RandomNumber;


/**
 * Private utilities to enable interactions between agents.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class InteractionUtil extends MessageFactory {

	private static boolean sendLocalMessage(
			float creationDate,
			Agent emitter, 
			Agent receiver, 
			AgentAddress receiverAddress,
			Message message, 
			boolean forceEmitter,
			boolean includeSender) {
		assert(message!=null);
		assert(emitter!=null);
		if (receiver==null)
			throw new UnspecifiedReceiverAddressMessageException(message);
		
		float date = message.getCreationDate();
		if (Float.isNaN(date)) date = creationDate;

		// Make sure message's fields are correctly set
		AgentAddress emitterAddress;
		if (forceEmitter) {
			emitterAddress = emitter.getAddress();
		}
		else {
			Address sender = message.getSender();
			if (sender instanceof AgentAddress) {
				emitterAddress = (AgentAddress)sender;
			}
			else {
				emitterAddress = emitter.getAddress();
			}
		}
		
		if (includeSender
			||
			(receiverAddress==null)
			||
			(!receiver.getAddress().equals(emitterAddress))) {
			
			setCreationDate(message, date);
			setSender(message, emitterAddress);
			setReceiver(message, receiverAddress);
	
			// Put message in mail box
			return receiver.getMailbox().add(message);
		}
		return false;
	}
	
	private static boolean sendDistantMessage(
			float creationDate,
			Agent emitter, 
			AgentAddress receiverAddress,
			Message message) {
		assert(message!=null);
		assert(emitter!=null);
		assert(receiverAddress!=null);
		
		KernelContext context = emitter.getKernelContext();
		DistantKernelHandler distantKernel = context.getDistantKernelHandler();
		if (distantKernel!=null) {
			setCreationDate(message, creationDate);
			setSender(message, emitter.getAddress());
			setReceiver(message, receiverAddress);
			
			distantKernel.sendMessage(message);
			return true;
		}		
		
		return false;
	}

	private static boolean broadcastDistantMessage(Agent emitter, Message message) {
		assert(message!=null);
		assert(emitter!=null);
		assert(emitter.getKernelContext()!=null);
		
		DistantKernelHandler distantKernel = emitter.getKernelContext().getDistantKernelHandler();
		if (distantKernel!=null) {
			distantKernel.broadcastMessage(message);
			return true;
		}
		
		return false;
	}
	
	private static Agent retreiveReceiver(AgentAddress address, KernelContext context) {
		Agent ag = null;
		PrivilegedPlayerAddressService pa = context.getPrivilegedPlayerAddressService();
		if (pa!=null) {
			RolePlayer rp = pa.getBindedPlayer(address);
			if (rp instanceof Agent) ag = (Agent)rp;
		}
		if (ag==null || ag.getState().isMortuary() || ag.hasMigrated()) {
			if (pa!=null) {
				pa.unbind(address);
			}
			AgentRepository agRepository = context.getAgentRepository();
			assert(agRepository!=null);
			ag = agRepository.get(address);
			if (ag!=null && pa!=null) {
				pa.bind(address, ag);
			}
		}
		return ag;
	}

	/**
	 * Send the specified <code>Message</code> to one randomly selected agent.
	 * <p>
	 * This function force the emitter of the message to be this agent.
	 * 
	 * @param creationDate is the date at which the message is sent.
	 * @param emitter is the emitter of the message.
	 * @param message is the message to send
	 * @param agents is the collection of receivers.
	 * @param forceSenderAddress is <code>true</code> to force the message to
	 * have a sender address equal to the emitter address. It is
	 * <code>false</code> to leave the message's address unchanged.
	 * @param includeSender indicates if the message sender may also receive
	 * the message.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 */
	public static AgentAddress sendMessage(
			float creationDate,
			Agent emitter,
			Message message, 
			List<? extends AgentAddress> agents,
			boolean forceSenderAddress,
			boolean includeSender) {
		if (emitter==null)
			throw new UnspecifiedSenderAddressMessageException(message);
		if (agents==null || agents.isEmpty())
			throw new UnspecifiedReceiverAddressMessageException(message);
		
		KernelContext context = emitter.getKernelContext();
		assert(context!=null);
		
		AgentAddress emitterAddress = emitter.getAddress();
		
		Agent receiver;
		AgentAddress receiverAddress;
		
		if (agents.size()>1) {
			// Random selection of the receiver
			int n;
			Set<Integer> invalidValues = new TreeSet<Integer>();
			
			do {
				invalidValues.clear();
				receiverAddress = null;
				while (invalidValues.size()<agents.size() && receiverAddress==null) {
					n = RandomNumber.nextInt(agents.size());
					receiverAddress = agents.get(n);
					if (emitterAddress.equals(receiverAddress)) {
						invalidValues.add(n);
						receiverAddress = null;
					}
				}
				if (receiverAddress==null) return null;
				receiver = retreiveReceiver(receiverAddress, context);
				
				// The receiver could be on a distant kernel
				if (receiver==null &&
					sendDistantMessage(creationDate, emitter, receiverAddress, message)) {
					return receiverAddress;
				}
			}
			while (receiver==null);
		}
		else {
			receiverAddress = agents.get(0);
			receiver = retreiveReceiver(receiverAddress, context);
			if (receiver==null) {
				// The receiver could be on a distant kernel
				if (sendDistantMessage(creationDate, emitter, receiverAddress, message))
					return receiverAddress;
				return null;
			}
		}
		
		if (sendLocalMessage(
				creationDate, 
				emitter, 
				receiver, 
				receiver.getAddress(),
				message, 
				forceSenderAddress, 
				includeSender)) {
			return receiverAddress;
		}
		
		return null;
	}

	/**
	 * Send the specified <code>Message</code> to one arbitrary selected agent.
	 * <p>
	 * This function force the emitter of the message to be this agent.
	 * 
	 * @param creationDate is the date at which the message is sent.
	 * @param emitter is the emitter of the message.
	 * @param message is the message to send
	 * @param policy permits to select an agent.
	 * @param forceSenderAddress is <code>true</code> to force the message to
	 * have a sender address equal to the emitter address. It is
	 * <code>false</code> to leave the message's address unchanged.
	 * @param includeSender indicates if the message sender may also receive
	 * the message.
	 * @return the address of the receiver of the freshly sended message if it
	 *         was found, <code>null</code> else.
	 */
	public static AgentAddress sendMessage(
			float creationDate,
			Agent emitter,
			Message message, 
			MessageReceiverSelectionPolicy policy,
			boolean forceSenderAddress,
			boolean includeSender) {
		if (emitter==null)
			throw new UnspecifiedSenderAddressMessageException(message);
		if (policy==null)
			throw new UnspecifiedSelectionPolicyMessageException(message);
		
		KernelContext context = emitter.getKernelContext();
		assert(context!=null);
		AgentRepository agRepository = context.getAgentRepository();
		assert(agRepository!=null);
		
		AgentAddress emitterAddress = emitter.getAddress();
		
		Agent receiver;
		AgentAddress receiverAddress;
		
		do {
			receiverAddress = policy.selectEntity(
					emitterAddress,
					agRepository.sizedIterator());
			if (receiverAddress==null) return null;
			receiver = retreiveReceiver(receiverAddress, context);
			
			// Try to find the agent on a remote kernel
			if (receiver==null
				&& sendDistantMessage(creationDate, emitter, receiverAddress, message)) {
				return receiverAddress;
			}
		}
		while (receiver==null);
		
		if (sendLocalMessage(
				creationDate, 
				emitter, 
				receiver,
				receiver.getAddress(),
				message, 
				forceSenderAddress, 
				includeSender)) {
			return receiverAddress;
		}
		
		return null;
	}

	/**
	 * Send the specified <code>Message</code> to all given agents.
	 * <p>
	 * If the list of agent address is empty, all the agents currently
	 * registered inside the kernel will receive the message.
	 *
	 * @param creationDate is the date at which the message is broadcasted.
	 * @param emitter is the emitter of the message.
	 * @param message is the message to send
	 * @param agents is the list of the receivers.
	 * @param forceSenderAddress is <code>true</code> to force the message to
	 * have a sender address equal to the emitter address. It is
	 * <code>false</code> to leave the message's address unchanged.
	 * @param includeSender indicates if the message sender may also receive
	 * the message.
	 */
	public static void broadcastMessage(
			float creationDate,
			Agent emitter,
			Message message,
			Collection<? extends AgentAddress> agents,
			boolean forceSenderAddress,
			boolean includeSender) {
		if (emitter==null)
			throw new UnspecifiedSenderAddressMessageException(message);
		
		KernelContext context = emitter.getKernelContext();
		assert(context!=null);
		AgentRepository agRepository = context.getAgentRepository();
		assert(agRepository!=null);
		
		Agent receiver;
		boolean distantBroadcast = false;
		Iterator<? extends AgentAddress> selectedAgents;
		
		if (agents==null || agents.isEmpty()) {
			// Broadcast all the agents in the system
			selectedAgents = agRepository.iterator();
			distantBroadcast = true;
		}
		else {
			// Broadcast only the given agents
			selectedAgents = agents.iterator();
		}
		
		assert(selectedAgents!=null);
		AgentAddress receiverAddress;
		
		while (selectedAgents.hasNext()) {
			receiverAddress = selectedAgents.next();
			receiver = retreiveReceiver(receiverAddress, context);
			if (receiver!=null) {
				sendLocalMessage(creationDate, 
						emitter,
						receiver,
						null,
						message, 
						forceSenderAddress, 
						includeSender);
			}
			else if (!distantBroadcast) {
				// Receiver should be on a distant kernel
				sendDistantMessage(creationDate, emitter, receiverAddress, message);
			}
		}

		if (distantBroadcast) {
			broadcastDistantMessage(emitter, message);
		}
	}
	
}