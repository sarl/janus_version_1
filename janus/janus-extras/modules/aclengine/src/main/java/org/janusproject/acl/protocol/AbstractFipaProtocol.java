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
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.Performative;
import org.janusproject.kernel.address.AgentAddress;

/**
 * This abstract class describes all the necessary information common to every protocols.
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractFipaProtocol {
	
	/**
	 * Maximum number of participants in a protocol (including initiator)
	 */
	public static short maximumParticipants;
	
	/**
	 * Unique identifier used to identify the conversation and transmitted into the ACL Messages
	 */
	private UUID conversationId;
	
	/**
	 * Friendly name for the conversation
	 */
	private String name;

	/**
	 * Reference to the agent that is running the protocol
	 */
	private ACLAgent refAclAgent;
	
	/**
	 * Address of the agent that initiated the protocol
	 */
	private AgentAddress initiator;
	
	/**
	 * Addresses of the participants
	 */
	private List<AgentAddress> participants;

	/**
	 * State of the agent for the current state
	 */
	private ProtocolState state;
	
	/**
	 * Number of errors.
	 */
	private int nbErrors = 0;

	/**
	 * Started time is used to check if timeout is reached or not.
	 */
	private long startedTime = 0;
	
	/**
	 * Logger
	 */
	protected Logger logger;

	/**
	 * Timeout in milliseconds.
	 */
	protected static int TIMEOUT = 10000; // 10 seconds
	
	/**
	 * 
	 */
	protected static int CANCEL_STEP = -1;
	
	/**
	 * 
	 */
	protected static int FINAL_STEP = Integer.MAX_VALUE;
	
	
	/**
	 * Creates a new Abstract Fipa Protocol.
	 * In order to let the protocol managing messages, a reference to the agent must be given.
	 * 
	 * @param agent
	 */
	public AbstractFipaProtocol(ACLAgent agent) {
		this.logger = Logger.getAnonymousLogger();
		setRefAclAgent(agent);
		this.participants = new ArrayList<AgentAddress>();
	}
	
	/**
	 * Initializes the participants and the initiator of the current protocol.
	 * 
	 * @param initiator
	 * @param participants
	 */
	public void initiate(AgentAddress initiator, AgentAddress... participants){
		this.initiator = initiator;	
		if (participants.length > maximumParticipants-1) {
			this.logger.log(Level.SEVERE, Locale.getString("AbstractFipaProtocol.0") + (maximumParticipants-1)); //$NON-NLS-1$
		}
		for (AgentAddress participant : participants) {
			if (participant.compareTo(initiator) != 0) {
				this.participants.add(participant);
			} else {
				this.logger.log(Level.WARNING, Locale.getString("AbstractFipaProtocol.1")); //$NON-NLS-1$
			}
		}
		this.conversationId = generateConversationId();
	}
	
	/**
	 * Initializes the participants and the initiator of the current protocol.
	 * 
	 * @param initiator
	 * @param participants
	 */
	public void initiate(AgentAddress initiator, List<AgentAddress> participants){
		this.initiator = initiator;	
		if (participants.size() > maximumParticipants-1) {
			this.logger.log(Level.SEVERE, Locale.getString("AbstractFipaProtocol.2") + (maximumParticipants-1)); //$NON-NLS-1$
		}
		for (AgentAddress participant : participants) {
			if (participant.compareTo(initiator) != 0) {
				this.participants.add(participant);
			} else {
				this.logger.log(Level.WARNING, Locale.getString("AbstractFipaProtocol.3")); //$NON-NLS-1$
			}
		}
		this.conversationId = generateConversationId();
	}
	
	/**
	 * Initializes the protocol as a participant.
	 */
	public void initiateAsParticipant() {
		this.participants.clear();
		this.participants.add(this.refAclAgent.getAddress());
	}
	
	/**
	 * Gets the agent used as a reference.
	 * @return the agent used as a reference.
	 */
	protected final ACLAgent getRefAclAgent() {
		return this.refAclAgent;
	}

	/**
	 * Sets the agent used as a reference.
	 * 
	 * @param refAclAgent
	 */
	protected final void setRefAclAgent(ACLAgent refAclAgent) {
		this.refAclAgent = refAclAgent;
	}

	/**
	 * Gets the agent address of the initiator of the protocol.
	 * 
	 * @return the agent address of the initiator
	 */
	protected final AgentAddress getInitiator() {
		return this.initiator;
	}

	/**
	 * Sets the agent address of the initiator of the protocol.
	 * 
	 * @param initiator
	 */
	protected final void setInitiator(AgentAddress initiator) {
		this.initiator = initiator;
	}
	
	/**
	 * Ends the protocol execution by setting the {@link #getState} to DONE.
	 * 
	 * @see ProtocolState
	 */
	protected abstract void setFinalStep();
	
	/**
	 * Gets the current conversation id.
	 * 
	 * @see UUID
	 * 
	 * @return the current conversation id
	 */
	public UUID getConversationId() {
		return this.conversationId;
	}

	/**
	 * Sets the current conversation id to a given {@link UUID}.
	 * 
	 * @param conversationId
	 */
	public void setConversationId(UUID conversationId) {
		this.conversationId = conversationId;
	}

	/**
	 * Generates a random conversation id.
	 * 
	 * @see UUID
	 * 
	 * @return a new unique UUID
	 */
	protected static final UUID generateConversationId(){
		return UUID.randomUUID();
	}
	
	/**
	 * Tests if the currently referenced agent is the initiator of the protocol.
	 * 
	 * @return <code>true</code> if the referenced agent is the initiator or <code>false</code> otherwise.
	 */
	public final Boolean isInitiator(){
		return ( getRefAclAgent().getAddress() == this.initiator );
	}
	
	/**
	 * Tests if the currently referenced agent is a simple participant of the protocol.
	 * 
	 * @return <code>true</code> if the referenced agent is a participant or <code>false</code> otherwise.
	 */
	public final Boolean isParticipant() {
		for (AgentAddress participant : this.participants) {
			if (participant.compareTo(this.refAclAgent.getAddress()) == 0) 
				return true;
		}
		return false;
	}

	/**
	 * Gets the maximum number of participants.
	 * @return the maximum number of participants.
	 */
	public static short getMaximumParticipants() {
		return maximumParticipants;
	}

	/**
	 * Sets the maximum number of participants.
	 * @param maximumParticipants
	 */
	public static void setMaximumParticipants(short maximumParticipants) {
		AbstractFipaProtocol.maximumParticipants = maximumParticipants;
	}

	/**
	 * Gets the list of participants involved in the conversation.
	 * 
	 * @return the list of participants
	 */
	public List<AgentAddress> getParticipants() {
		return this.participants;
	}

	/**
	 * Sets the participants involved in the conversation.
	 * 
	 * @param participants is a list of agent addresses
	 */
	public void setParticipants(ArrayList<AgentAddress> participants) {
		this.participants = participants;
	}
	
	/**
	 * Gets the friendly name of the current conversation.
	 * @return  the friendly name of the current conversation.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the friendly name of the current conversation.
	 * @param name - the friendly name of the current conversation.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the current state.
	 * @return the current state of the protocol
	 * @see ProtocolState
	 */
	public ProtocolState getState() {
		return this.state;
	}

	/**
	 * Sets the current state.
	 * @param state
	 */
	public void setState(ProtocolState state) {
		this.state = state;
	}
	
	/**
	 * Generates an error and terminates the conversation.
	 * @param msg 
	 */
	public void addError(String msg){
		setFinalStep();
		this.nbErrors++;
		
		// Display error :
		StringBuffer str = new StringBuffer();
		
		str.append("[agent:"); //$NON-NLS-1$
		str.append(getRefAclAgent().getName());
		str.append("] "); //$NON-NLS-1$
		str.append(msg);
		
		this.logger.log(Level.SEVERE, str.toString());
	}
	
	/** Print the specified message on the logging output.
	 * 
	 * @param msg is the message to output.
	 */
	protected void print(String msg) {
		this.logger.log(Level.SEVERE, msg);
	}
	
	/**
	 * Tests if there is at least one error.
	 * @return true if there is at least one error in the protocol execution, false otherwise
	 */
	public boolean hasFailed(){
		return (this.nbErrors > 0);
	}
	
	/**
	 * Gets started time.
	 * @return the time at which the protocol was started
	 */
	public long getStartedTime(){
		return this.startedTime;
	}
	
	/**
	 * Sets started time.
	 */
	public void setStartedTime(){
		this.startedTime = getCurrentTime();
	}
	
	/**
	 * Resets started time.
	 */
	public void resetStartedTime(){
		this.startedTime = 0;
	}
	
	/**
	 * Gets current time in milliseconds
	 * @return the current time in milliseconds
	 */
	public static long getCurrentTime(){
		//FIXME: Must be linked with the Janus time manager
		return (System.nanoTime() / (1000 * 1000) );
	}
	
	/**
	 * Tests if timeout has been reached.
	 * <p>
	 * If this method is called whereas {@link #getStartedTime} hasn't been set, 
	 * {@link #getStartedTime} will be automatically set to the current time.
	 * </p>
	 * @return true the timeout has ellapsed, false otherwise
	 */
	public boolean hasReachedTimeout(){
		if( getStartedTime() == 0 ){
			setStartedTime();
		}
		
		if( getCurrentTime() > ( getStartedTime() + TIMEOUT ) ){
			resetStartedTime();
			return true;
		}
		return false;
	}
	
	/**
	 * @param content
	 * @param performative
	 * @param to
	 */
	protected abstract void sendMessage(Object content, Performative performative, AgentAddress to);
}
