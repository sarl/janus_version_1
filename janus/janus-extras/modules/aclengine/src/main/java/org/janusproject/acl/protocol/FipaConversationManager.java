/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.acl.protocol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.cnp.ContractNetProtocolState;
import org.janusproject.acl.protocol.cnp.FipaContractNetProtocol;
import org.janusproject.acl.protocol.propose.FipaProposeProtocol;
import org.janusproject.acl.protocol.propose.ProposeProtocolState;
import org.janusproject.acl.protocol.query.FipaQueryProtocol;
import org.janusproject.acl.protocol.query.QueryProtocolState;
import org.janusproject.acl.protocol.request.FipaRequestProtocol;
import org.janusproject.acl.protocol.request.RequestProtocolState;

/**
 * Conversation Manager.
 * <p>
 * This class lets the agent manage its conversations using protocols.
 * <p>
 * Here is how to use it in an {@link ACLAgent} :
 * <dd>
 * <dt>Instantiation of a new FipaConversationManager :</dt>
 * <dl><code>protocolManager = new FipaConversationManager(this);</code></dl>
 * <dt>Creation of a new Conversation based on a given protocol type :</dt>
 * <dl><code>AbstractFipaProtocol protocol = protocolManager.createConversation(EnumFipaProtocol.FIPA_CONTRACT_NET, "1st Conversation");</code></dl>
 * <dt>Initiation of the protocol on the Initiator side :</dt>
 * <dl><code>protocol.initiate(getAddress(), participants);</code></dl>
 * <dt>Initiation of the protocol on the Participant side :</dt>
 * <dl><code>protocol.initiateAsParticipant();</code></dl>
 * </ul>
 * 
 * @see EnumFipaProtocol
 * @see AbstractFipaProtocol
 * @see FipaContractNetProtocol
 * @see FipaRequestProtocol
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class FipaConversationManager {	
	
	private ArrayList<AbstractFipaProtocol> conversations;
	private ACLAgent agent;
	
	private Logger logger;
	
	/**
	 * @param agent
	 */
	public FipaConversationManager(final ACLAgent agent) {
		this.agent = agent;
		this.conversations = new ArrayList<AbstractFipaProtocol>();
		this.logger = Logger.getLogger(this.getClass().getName());
	}
	
	/**
	 * Creates a protocol as the initiator
	 * @param protocolType
	 * 
	 * @return the protocol created
	 */
	public AbstractFipaProtocol createProtocol(EnumFipaProtocol protocolType) {	
		AbstractFipaProtocol protocol;
		if (EnumFipaProtocol.FIPA_REQUEST == protocolType) {
			protocol = new FipaRequestProtocol(this.agent);
			protocol.setState(RequestProtocolState.NOT_STARTED);
			protocol.setRefAclAgent(this.agent);
			this.conversations.add(protocol);
			return protocol;
		}
		else if (EnumFipaProtocol.FIPA_CONTRACT_NET == protocolType) {
			protocol = new FipaContractNetProtocol(this.agent);
			protocol.setState(ContractNetProtocolState.NOT_STARTED);
			protocol.setRefAclAgent(this.agent);
			this.conversations.add(protocol);
			return protocol;
		} else if (EnumFipaProtocol.FIPA_PROPOSE == protocolType) {
			protocol = new FipaProposeProtocol(this.agent);
			protocol.setState(ProposeProtocolState.NOT_STARTED);
			protocol.setRefAclAgent(this.agent);
			this.conversations.add(protocol);
			return protocol;
		}
		else if (EnumFipaProtocol.FIPA_QUERY == protocolType) {
			protocol = new FipaQueryProtocol(this.agent);
			protocol.setState(QueryProtocolState.NOT_STARTED);
			protocol.setRefAclAgent(this.agent);
			this.conversations.add(protocol);
			return protocol;
		}
		else {
			this.logger.log(Level.SEVERE, "Protocol not supported"); //$NON-NLS-1$
			return null;
		}
	}
	
	/**
	 * Creates a protocol as the initiator
	 * @param protocolType
	 * @param name 
	 * 
	 * @return the protocol created
	 */
	public AbstractFipaProtocol createConversation(EnumFipaProtocol protocolType, String name) {	
		AbstractFipaProtocol protocol = createProtocol(protocolType);
		if(protocol != null) {
			protocol.setName(name);
		}
		return protocol;
	}
	
	/**
	 * Creates a conversation 
	 * @param message
	 * 
	 * @return the protocol created
	 */
	public AbstractFipaProtocol createConversationFromMessage(ACLMessage message) {
		if (EnumFipaProtocol.FIPA_REQUEST == message.getProtocol() && Performative.REQUEST == message.getPerformative()) {
			return new FipaRequestProtocol(this.agent);
		}
		return null;
	}
	
	/**
	 * Gets a protocol with the specified conversation id
	 * @param conversationId
	 * 
	 * @return the protocol with the specified conversationId
	 */
	public AbstractFipaProtocol getConversation(UUID conversationId) {
		for (AbstractFipaProtocol protocol : this.conversations) {
			if (protocol.getConversationId().compareTo(conversationId) == 0)
				return protocol;
		}
		return null;		
	}
	
	/** 
	 * Indicates if the agent mailbox contains at least one ACL Message for the given protocol and performatives or not.
	 * 
	 * @param protocol 
	 * @param performative 
	 * 
	 * @return <code>true</code> if the mailbox contains at least one ACL Message 
	 * for the given protocol and performatives, otherwise <code>false</code>
	 */
	public boolean hasACLMessages(EnumFipaProtocol protocol, Performative... performative) {
		return this.agent.hasACLMessages(protocol, performative);
	}
	
	/**
	 * Delete current protocols in the given state
	 * 
	 * @param state
	 */
	public void removeConversations(ProtocolState state) {
		Iterator<AbstractFipaProtocol> i = this.conversations.iterator();
		
		while (i.hasNext()) {
			AbstractFipaProtocol conv = i.next();
			if (conv.getState() == state) {
				i.remove();
			}
		}
	}
	
	/**
	 * Gets the list of current conversations.
	 * @return the list of current conversations.
	 */
	public ArrayList<AbstractFipaProtocol> getConversations() {
		return this.conversations;
	}

	/**
	 * Sets the list of current conversations.
	 * @param protocols - the list of current conversations.
	 */
	public void setConversations(ArrayList<AbstractFipaProtocol> protocols) {
		this.conversations = protocols;
	}
	
	
}
