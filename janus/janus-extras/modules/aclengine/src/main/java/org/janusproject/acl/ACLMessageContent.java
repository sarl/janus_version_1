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
package org.janusproject.acl;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.janusproject.kernel.address.AgentAddress;

/**
 * This interface describes the Content of a given ACL Message as defined by FIPA.
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00061/SC00061G.html">FIPA ACL Message Structure Specification</a>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface ACLMessageContent
{
	/**
	 * Gets the performative of the ACL Message.
	 * <p>
	 * The performative denotes the type of the communicative act of the ACL message.
	 * @return the performative of the ACL Message.
	 */
	public int getPerformative();
	
	/**
	 * Sets the performative of the ACL Message.
	 * <p>
	 * The performative denotes the type of the communicative act of the ACL message.
	 * 
	 * @param performative
	 */
	public void setPerformative(int performative);
	
	/**
	 * Gets the agent address of the sender of the ACL Message.
	 * <p>
	 * The sender denotes the identity of the sender of the message, 
	 * that is, the agent address of the agent of the communicative act.
	 * @return the agent address of the sender of the ACL Message.
	 */
	public AgentAddress getSender();
	
	/**
	 * Sets the agent address of the sender of the ACL Message.
	 * <p>
	 * The sender denotes the identity of the sender of the message, 
	 * that is, the agent address of the agent of the communicative act.
	 * 
	 * @param address
	 */
	public void setSender(AgentAddress address);
	
	/**
	 * Gets the list of receivers of the ACL Message.
	 * <p>
	 * The receiver denotes the identity of the intended recipients of the message.
	 * @return the list of receivers of the ACL Message.
	 */
	public Collection<AgentAddress> getReceiver();
	
	/**
	 * Sets the list of receivers of the ACL Message.
	 * <p>
	 * The receiver denotes the identity of the intended recipients of the message.
	 * @param receivers - the list of receivers of the ACL Message.
	 */
	public void setReceiver(Collection<AgentAddress> receivers);
	
	/**
	 * Gets the list of agent address to reply to.
	 * <p>
	 * This parameter indicates that subsequent messages 
	 * in this conversation thread are to be directed to the agent named in the reply-to parameter, 
	 * instead of to the agent named in the sender parameter.
	 * @return the list of agent address to reply to.
	 */
	public Collection<AgentAddress> getReplyTo();
	
	/**
	 * Sets the list of agent address to reply to.
	 * <p>
	 * This parameter indicates that subsequent messages 
	 * in this conversation thread are to be directed to the agent named in the reply-to parameter, 
	 * instead of to the agent named in the sender parameter.
	 * @param replyTo - the list of agent address to reply to.
	 */
	public void setReplyTo(Collection<AgentAddress> replyTo);
	
	/**
	 * Gets the content of the ACL Message.
	 * <p>
	 * The content denotes the content of the message; 
	 * equivalently denotes the object of the action. 
	 * @return the content of the ACL Message.
	 */
	public StringBuffer getContent();
	
	/**
	 * Sets the content of the ACL Message.
	 * <p>
	 * The content denotes the content of the message; 
	 * equivalently denotes the object of the action. 
	 * <p>
	 * The meaning of the content of any ACL message is intended 
	 * to be interpreted by the receiver of the message. 
	 * This is particularly relevant for instance when referring 
	 * to referential expressions, 
	 * whose interpretation might be different for the sender and the receiver.
	 * @param content - the content of the ACL Message.
	 */
	public void setContent(StringBuffer content);
	
	/**
	 * Gets the language of the content of the ACL Message.
	 * <p>
	 * The language denotes the language in which the content parameter is expressed.
	 * <p>
	 * This field may be omitted 
	 * if the agent receiving the message can be assumed 
	 * to know the language of the content expression.
	 * @return the language of the content of the ACL Message.
	 */
	public String getLanguage();
	
	/**
	 * Sets the language of the content of the ACL Message.
	 * <p>
	 * The language denotes the language in which the content parameter is expressed.
	 * <p>
	 * This field may be omitted 
	 * if the agent receiving the message can be assumed 
	 * to know the language of the content expression.
	 * @param language - the language of the content of the ACL Message.
	 */
	public void setLanguage(String language);
	
	/**
	 * Gets the encoding of the content of the ACL Message.
	 * <p>
	 * The encoding denotes the specific encoding of the content language expression.
	 * <p>
	 * If the encoding parameter is not present, 
	 * the encoding will be specified in the message envelope 
	 * that encloses the ACL message.
	 * @return the encoding of the content of the ACL Message.
	 */
	public String getEncoding();
	
	/**
	 * Sets the encoding of the content of the ACL Message.
	 * <p>
	 * The encoding denotes the specific encoding of the content language expression.
	 * <p>
	 * If the encoding parameter is not present, 
	 * the encoding will be specified in the message envelope 
	 * that encloses the ACL message.
	 * @param encoding - the encoding of the content of the ACL Message.
	 */
	public void setEncoding(String encoding);
	
	/**
	 * Gets the ontology of the content of the ACL Message.
	 * <p>
	 * The ontology denotes the ontology(s) used to give a meaning to the symbols in the content expression.
	 * <p>
	 * In many situations, the ontology parameter will be commonly understood
	 * by the agent community and so this message parameter may be omitted.
	 * @return the ontology of the content of the ACL Message.
	 */
	public String getOntology();
	
	/**
	 * Sets the ontology of the content of the ACL Message.
	 * <p>
	 * The ontology denotes the ontology(s) used to give a meaning to the symbols in the content expression.
	 * <p>
	 * In many situations, the ontology parameter will be commonly understood
	 * by the agent community and so this message parameter may be omitted.
	 * @param ontology - the ontology of the content of the ACL Message.
	 */
	public void setOntology(String ontology);
	
	/**
	 * Gets the protocol used to manage the ACL Message.
	 * <p>
	 * The protocol denotes the interaction protocol 
	 * that the sending agent is employing with this ACL message.
	 * @return the protocol used to manage the ACL Message.
	 * 
	 * @see <a href="http://www.fipa.org/specs/fipa00061/SC00061G.html#_Toc26669714">FIPA Protocol Specification</a>
	 */
	public String getProtocol();
	
	/**
	 * Sets the protocol used to manage the ACL Message.
	 * <p>
	 * The protocol denotes the interaction protocol 
	 * that the sending agent is employing with this ACL message.
	 * @param protocol - the protocol used to manage the ACL Message.
	 * 
	 * @see <a href="http://www.fipa.org/specs/fipa00061/SC00061G.html#_Toc26669714">FIPA Protocol Specification</a>
	 */
	public void setProtocol(String protocol);
	
	
	/**
	 * Gets the conversation Id which is associated to the ACL Message.
	 * <p>
	 * The conversation Id introduces an expression (a conversation identifier) 
	 * which is used to identify the ongoing sequence of communicative acts that together form a conversation.
	 * @return the conversation Id which is associated to the ACL Message.
	 */
	public UUID getConversationId();
	
	/**
	 * Sets the conversation Id which is associated to the ACL Message.
	 * <p>
	 * The conversation Id introduces an expression (a conversation identifier) 
	 * which is used to identify the ongoing sequence of communicative acts that together form a conversation.
	 * @param conversationId - the conversation Id which is associated to the ACL Message.
	 */
	public void setConversationId(UUID conversationId);
	
	/**
	 * Gets the reply-with parameter of the ACL Message.
	 * <p>
	 * The reply-with parameter introduces an expression 
	 * that will be used by the responding agent to identify this message.
	 * @return the reply-with parameter of the ACL Message.
	 */
	public String getReplyWith();
	
	/**
	 * Sets the reply-with parameter of the ACL Message.
	 * <p>
	 * The reply-with parameter introduces an expression 
	 * that will be used by the responding agent to identify this message.
	 * @param replyWith - the reply-with parameter of the ACL Message.
	 */
	public void setReplyWith(String replyWith);
	
	/**
	 * Gets the in-reply-to parameter of the ACL Message.
	 * <p>
	 * The in-reply-to parameter denotes an expression that references 
	 * an earlier action to which this message is a reply.
	 * @return the in-reply-to parameter of the ACL Message.
	 */
	public String getInReplyTo();
	
	/**
	 * Sets the in-reply-to parameter of the ACL Message.
	 * <p>
	 * The in-reply-to parameter denotes an expression that references 
	 * an earlier action to which this message is a reply.
	 * @param inReplyTo - the in-reply-to parameter of the ACL Message.
	 */
	public void setInReplyTo(String inReplyTo);
	
	/**
	 * Gets the reply-by parameter of the ACL Message.
	 * <p>
	 * The reply-by parameter denotes a time and/or date expression which indicates 
	 * the latest time by which the sending agent would like to receive a reply.
	 * @return the reply-by parameter of the ACL Message.
	 */
	public Date getReplyBy();
	
	/**
	 * Sets the reply-by parameter of the ACL Message.
	 * <p>
	 * The reply-by parameter denotes a time and/or date expression which indicates 
	 * the latest time by which the sending agent would like to receive a reply.
	 * @param replyBy - the reply-by parameter of the ACL Message.
	 */
	public void setReplyBy(Date replyBy);    
    
}
