package org.janusproject.acl.protocol;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.cnp.ContractNetProtocolState;
import org.janusproject.acl.protocol.cnp.FipaContractNetProtocol;
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
 * @version $Name$ $Revision$ $Date$
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
		protocol.setName(name);
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
