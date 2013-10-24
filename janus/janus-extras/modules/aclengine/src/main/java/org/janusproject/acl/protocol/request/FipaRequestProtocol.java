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
package org.janusproject.acl.protocol.request;

import java.util.logging.Level;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLAgent;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.AbstractFipaProtocol;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.ProtocolResult;
import org.janusproject.kernel.address.AgentAddress;

/**
 * Request Interaction protocol.
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00026/SC00026H.html">FIPA Request Interaction Protocol Specification</a>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class FipaRequestProtocol extends AbstractFipaProtocol {
	
	/**
	 * Creates a new Request Protocol for a given agent.
	 * <p>
	 * The maximum number of participants is set to 2.
	 * 
	 * @param agent
	 */
	public FipaRequestProtocol(ACLAgent agent){
		super(agent);
		setMaximumParticipants((short) 2);
	}
	
	/**
	 * Send a request to the participant.
	 * This method must be called by the initiator.
	 * 
	 * @param content
	 */
	public void request(Object content) {
		
		if( isInitiator() && (getState() == RequestProtocolState.NOT_STARTED) ){
			sendMessage(content, Performative.REQUEST, getParticipants().get(0));
			setState(RequestProtocolState.WAITING_ANSWER);
		}
		else if( isParticipant() ){
			this.logger.log(Level.SEVERE, Locale.getString("FipaRequestProtocol.0")); //$NON-NLS-1$
		}
		else{
			this.logger.log(Level.SEVERE, Locale.getString("FipaRequestProtocol.1")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Looks if there is a pending request in the mailbox
	 * 
	 * @return ProtocolResult the result
	 */
	public ProtocolResult getRequest() {
		
		if( hasReachedTimeout() ){
			addError(Locale.getString("FipaRequestProtocol.2")); //$NON-NLS-1$
		}
		else{
			if (isParticipant() && (getState() == RequestProtocolState.NOT_STARTED)) {
				
				ACLMessage message = getRefAclAgent().getACLMessage(EnumFipaProtocol.FIPA_REQUEST, Performative.REQUEST);
				
				if (message != null) {
					initiate(message.getSender(), getRefAclAgent().getAddress());
					
					setConversationId(message.getConversationId());
					setState(RequestProtocolState.WAITING_REQUEST);
					
					resetStartedTime();
					
					return new ProtocolResult(message.getSender(), Performative.REQUEST, message.getContent().getContent().toString());
				}
			}
			else if (isInitiator()) {
				addError(Locale.getString("FipaRequestProtocol.3")); //$NON-NLS-1$
			}
			else {
				addError(Locale.getString("FipaRequestProtocol.4")); //$NON-NLS-1$
			}
		}
		return null;
	}
	
	/**
	 * Refuses the request made by the initiator.
	 * This method must be called by the participant.
	 * 
	 * @param content
	 * 
	 * @see #agree(Object)
	 */
	public void refuse(Object content){

		if (isParticipant() && (getState() == RequestProtocolState.WAITING_REQUEST)) {
			sendMessage(content, Performative.REFUSE, getInitiator());
			setFinalStep();
		}
		else if( isInitiator() ){
			addError(Locale.getString("FipaRequestProtocol.5")); //$NON-NLS-1$
		}
		else{
			addError(Locale.getString("FipaRequestProtocol.6")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Accepts the request made by the initiator.
	 * This method must be called by the participant.
	 * 
	 * Once the request has been agreed upon, 
	 * then the Participant must send one of the following notifications:
	 * failure(), informDone(), informResult().
	 * 
	 * @param content
	 * 
	 * @see #refuse(Object)
	 * @see #failure(Object)
	 * @see #informDone(Object)
	 * @see #informResult(Object)
	 */
	public void agree(Object content){

		if( isParticipant() && (getState() == RequestProtocolState.WAITING_REQUEST)){
			sendMessage(content, Performative.AGREE, getInitiator());
			setState(RequestProtocolState.SENDING_RESULT);
		}
		else if( isInitiator() ){
			addError(Locale.getString("FipaRequestProtocol.7")); //$NON-NLS-1$
		}
		else{
			addError(Locale.getString("FipaRequestProtocol.8")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Replies the answer of the participant to the request. (AGREE or REFUSE)
	 * 
	 * If the request has been agreed upon, 
	 * then the Participant will send one of the following notifications:
	 * failure(), informDone(), informResult().
	 * 
	 * This notification can be obtained by the initiator thanks to getNotification().
	 * 
	 * @return the answer (Performative + Content) or null if no answer.
	 * 
	 * @see #refuse(Object)
	 * @see #agree(Object)
	 */
	public ProtocolResult getAnswer(){
		
		if( hasReachedTimeout() ){
			addError(Locale.getString("FipaRequestProtocol.9")); //$NON-NLS-1$
		}
		else{
			if (isInitiator() && (getState() == RequestProtocolState.WAITING_ANSWER)) {
				
				ProtocolResult result = null;
				ACLMessage aMsg  = getRefAclAgent().getACLMessageForConversationId( getConversationId() );
				
				if( aMsg != null )
				{
					result = new ProtocolResult();
					result.setPerformative( aMsg.getPerformative() );
					result.setContent( aMsg.getContent().getContent() );
					
					if( result.getPerformative().compareTo(Performative.NOT_UNDERSTOOD) == 0 )
						setFinalStep();
					else if( result.getPerformative().compareTo(Performative.REFUSE) == 0 )
						setFinalStep();
					else if( result.getPerformative().compareTo(Performative.AGREE) == 0 )
						setState(RequestProtocolState.WAITING_RESULT);
					
					resetStartedTime();
				}
				
				return result;
			}
			else if( isParticipant() ){
				addError(Locale.getString("FipaRequestProtocol.10")); //$NON-NLS-1$
			}
			else{
				addError(Locale.getString("FipaRequestProtocol.11")); //$NON-NLS-1$
			}
		}
		return null;
	}
	
	/**
	 * Once the request has been agreed upon, 
	 * then the Participant can communicate a failure 
	 * if it fails in its attempt to fill the request
	 * 
	 * @param content
	 */
	public void failure(Object content){

		if (isParticipant() && (getState() == RequestProtocolState.SENDING_RESULT)) {
			sendMessage(content, Performative.FAILURE, getInitiator());
			setFinalStep();
		}
		else if( isInitiator() ){
			addError(Locale.getString("FipaRequestProtocol.12")); //$NON-NLS-1$
		}
		else{
			addError(Locale.getString("FipaRequestProtocol.13")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Once the request has been agreed upon, 
	 * then the Participant can communicate an inform-done 
	 * if it successfully completes the request and only wishes to indicate that it is done.
	 * 
	 * @param content
	 */
	public void informDone(Object content){

		if( isParticipant() && ( getState() == RequestProtocolState.SENDING_RESULT ) ){
			sendMessage(content, Performative.INFORM, getInitiator());
			setFinalStep();
		}
		else if( isInitiator() ){
			addError(Locale.getString("FipaRequestProtocol.14")); //$NON-NLS-1$
		}
		else{
			addError(Locale.getString("FipaRequestProtocol.15")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Once the request has been agreed upon, 
	 * then the Participant can communicate an inform-result 
	 * if it wishes to indicate both that it is done and notify the initiator of the results.
	 * 
	 * @param content
	 */
	public void informResult(Object content){

		if( isParticipant() && ( getState() == RequestProtocolState.SENDING_RESULT ) ){
			sendMessage(content, Performative.INFORM, getInitiator());
			setFinalStep();
		}
		else if( isInitiator() ){
			addError(Locale.getString("FipaRequestProtocol.16")); //$NON-NLS-1$
		}
		else{
			addError(Locale.getString("FipaRequestProtocol.17")); //$NON-NLS-1$
		}
	}
	
	/**
	 * 
	 * @return the notification (Performative + Content)
	 * 
	 * @see #failure(Object)
	 * @see #informDone(Object)
	 * @see #informResult(Object)
	 */
	public ProtocolResult getResult() {

		if( hasReachedTimeout() ){
			addError(Locale.getString("FipaRequestProtocol.18")); //$NON-NLS-1$
		}
		else{
			if( isInitiator() && (getState() == RequestProtocolState.WAITING_RESULT) || getState() == RequestProtocolState.CANCELING) {  
	
				ProtocolResult result = null;
				ACLMessage aMsg = getRefAclAgent().getACLMessageForConversationId( getConversationId() );
				
				if( aMsg != null)
				{
					result = new ProtocolResult();
					result.setPerformative( aMsg.getPerformative() );
					result.setContent( aMsg.getContent().getContent() );
					
					setFinalStep();
					
					resetStartedTime();
				}
				
				return result;
			}
			else if( isParticipant() ){
				addError(Locale.getString("FipaRequestProtocol.19")); //$NON-NLS-1$
			}
			else{
				addError(Locale.getString("FipaRequestProtocol.20")); //$NON-NLS-1$
			}
		}
		
		return null;
	}
	
	// ----------------------------
	// Exceptions to Protocol Flow
	// ----------------------------
	
	/**
	 * At any point in the IP, the initiator of the IP may cancel the interaction protocol.
	 * 
	 * The semantics of cancel should roughly be interpreted 
	 * as meaning that the initiator is no longer interested in continuing the interaction 
	 * and that it should be terminated in a manner acceptable to both the Initiator and the Participant. 
	 * 
	 * The Participant either informs the Initiator that the interaction 
	 * is done using an inform-done or indicates the failure of the cancellation using a failure.
	 * 
	 * @param content
	 */
	public void cancel(Object content){
		
		if (isInitiator() && (getState() != RequestProtocolState.DONE) && (getState() != RequestProtocolState.CANCELED)
				&& (getState() != RequestProtocolState.NOT_STARTED)) 
		{
			sendMessage(content, Performative.CANCEL, getParticipants().get(0));
			setState(RequestProtocolState.CANCELING);
		}
		else if (isParticipant()) {
			addError(Locale.getString("FipaRequestProtocol.21")); //$NON-NLS-1$
		}
		else{
			addError(Locale.getString("FipaRequestProtocol.22")); //$NON-NLS-1$
		}
	}
	
	/**
	 * At any point in the IP, the receiver of a communication 
	 * can inform the sender that he did not understand what was communicated.
	 * 
	 * The communication of a not-understood within an interaction protocol may terminate the entire IP 
	 * and termination of the interaction may imply that any commitments made during the interaction 
	 * are null and void.
	 * 
	 * @param content
	 */
	public void notUnderstood(Object content) {
		
		if( isParticipant() && (getState() != RequestProtocolState.DONE) && (getState() != RequestProtocolState.CANCELED)
				&& (getState() != RequestProtocolState.NOT_STARTED)) 
		{
			sendMessage(content, Performative.NOT_UNDERSTOOD, getInitiator());
			setFinalStep();
		}
		else if( isInitiator() ){
			addError(Locale.getString("FipaRequestProtocol.23")); //$NON-NLS-1$
		}
		else{
			addError(Locale.getString("FipaRequestProtocol.24")); //$NON-NLS-1$
		}
	}
	
	// -------
	// Helpers
	// -------
	
	/**
	 * Helper method used to send ACL Messages within this protocol.
	 * 
	 * @param content
	 * @param performative
	 * @param to
	 */
	@Override
	protected final void sendMessage(Object content, Performative performative, AgentAddress to){

		ACLMessage message = new ACLMessage(content, performative);
		
		message.setProtocol(EnumFipaProtocol.FIPA_REQUEST);
		message.setConversationId( getConversationId() );
		
		getRefAclAgent().sendACLMessage(message, to);
		
		//System.out.println("\n=> MESSAGE envoyé via REQUEST PROTOCOL : \n" + message.toString());
	}
	
	@Override
	protected void setFinalStep() {
		this.setState(RequestProtocolState.DONE);
	}
}
