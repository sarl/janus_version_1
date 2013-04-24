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

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import org.janusproject.acl.encoding.StringACLCodec;
import org.janusproject.acl.encoding.xml.XMLACLCodec;
import org.janusproject.acl.encoding.xml.XMLACLCodecHelper;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.kernel.address.AgentAddress;

/**
 * This class precises the minimal set of attributes required by an ACL Message to be sent and received.
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
public class ACLMessage
{
	private Envelope envelope;
	private Content content;
	
	/**
	 * Creates an ACL Message with the given performative.
	 * 
	 * @param performative
	 */
	public ACLMessage(Performative performative){
		this.setPerformative(performative);
	}

	/**
	 * Creates an ACL Message with the given content (any {@link Object}) and the given performative.
	 * 
	 * @param content 
	 * @param performative
	 */
	public ACLMessage(Object content, Performative performative) {
		getContent().setContent(new StringBuffer(content.toString()));
		setPerformative(performative);
	}
	
	/**
	 * Creates an ACL Message with the given {@link ACLMessageEnvelope} and the given {@link ACLMessageContent}.
	 * 
	 * @param _envelope
	 * @param _content
	 */
	public ACLMessage(ACLMessageEnvelope _envelope, ACLMessageContent _content) {
		this.envelope = (Envelope) _envelope;
		this.content = (Content) _content;
	}
	
	/**
	 * Returns a string representation of the ACL Message.
	 */
	@Override
	public String toString() {
		return StringACLCodec.toString(this);
	}
	
	/**
	 * Returns a string representation of the ACL Message.
	 * @return a string representing this ACL Message.
	 */
	public String toXML() {
		return XMLACLCodecHelper.format(XMLACLCodec.toXML(this));
	}
	
	/**
	 * Gets the envelope of the ACL Message.
	 * 
	 * @return the envelope
	 */
	public Envelope getEnvelope() {
		if (this.envelope == null) {
			this.envelope = new Envelope();
		}
		return this.envelope;
	}
	
	/**
	 * Gets the content of the ACL Message.
	 * 
	 * @return the content
	 */
	public Content getContent() {
		if (this.content == null) {
			this.content = new Content();
		}
		return this.content;
	}	

	/**
	 * Gets the performative of the ACL Message.
	 * @return the performative of the ACL Message.
	 */
	public final Performative getPerformative() {
		return Performative.values()[getContent().getPerformative()];
	}
	
	/**
	 * Sets the performative of the ACL Message.
	 * @param performative
	 * @see Content
	 */
	public final void setPerformative(Performative performative) {
		getContent().setPerformative(performative.ordinal());
	}
	
	/**
	 * Gets the agent address of the sender of the ACL Message.
	 * @return the agent address of the sender of the ACL Message.
	 */
	public final AgentAddress getSender() {
		return getContent().getSender();
	}
	
	/**
	 * Sets the agent address of the sender of the ACL Message.
	 * 
	 * @param address is the agent address of the agent sending the ACL Message.
	 */
	public final void setSender(AgentAddress address) {
		getContent().setSender(address);
		getEnvelope().setFrom(address);
	}
	
	/**
	 * Gets the list of the receivers of the ACL Message.
	 * 
	 * @return a collection of {@link AgentAddress}
	 */
	public final Collection<AgentAddress> getReceiver() {
		return getContent().getReceiver();
	}
	
	/**
	 * Sets the list of the receivers of the ACL Message.
	 * 
	 * @param addresses is a collection of {@link AgentAddress}
	 */
	public final void setReceiver(Collection<AgentAddress> addresses) {
		getContent().setReceiver(addresses);
		getEnvelope().setTo(addresses);
	}
	
	/**
	 * Gets the ACL Representation of the ACL Message.
	 * This information will then permit to encode and decode the corresponding content.
	 * 
	 * @return the ACL Representation
	 */
	public final String getAclRepresentation() {
		return getEnvelope().getAclRepresentation();
	}
	
	/**
	 * Sets the ACL Representation of the ACL Message. (example : fipa.acl.rep.xml.std)
	 * 
	 * @param aclRepresentation
	 */
	public final void setAclRepresentation(String aclRepresentation) {
		getEnvelope().setAclRepresentation(aclRepresentation);		
	}
	
	/**
	 * Gets the ontology of the ACL Message.
	 * @return the ontology of the ACL Message.
	 */
	public final String getOntology() {
		return getContent().getOntology();
	}
	
	/**
	 * Sets the ontology of the ACL Message.
	 * <p>
	 * In many situations, the ontology parameter 
	 * will be commonly understood by the agent community 
	 * and so this message parameter may be omitted.
	 * 
	 * @param ontology
	 */
	public final void setOntology(String ontology) {
		getContent().setOntology(ontology);
	}
	
	/**
	 * Gets the encoding of the ACL Message.
	 * @return the encoding of the ACL Message.
	 */
	public final String getEncoding() {
		return getContent().getEncoding();
	}
	
	/**
	 * Sets the encoding of the ACL Message.
	 * @param encoding
	 */
	public final void setEncoding(String encoding) {
		getContent().setEncoding(encoding);
	}
    
	/**
	 * Gets the language of the ACL Message.
	 * @return the language of the ACL Message.
	 */
	public final String getLanguage() {
		return getContent().getLanguage();
	}
	
	/**
	 * Sets the language of the ACL Message.
	 * 
	 * @param language
	 */
	public final void setLanguage(String language) {
		getContent().setLanguage(language);
	}
	
	/**
	 * Gets the protocol used to mangage the ACL Message.
	 * @return the protocol used to mangage the ACL Message.
	 * 
	 * @see EnumFipaProtocol
	 */
	public EnumFipaProtocol getProtocol() {
		return EnumFipaProtocol.valueOfByName(getContent().getProtocol());
	}
	
	/**
	 * Sets the protocol used to manage the ACL Message.
	 * @param protocol
	 * @see EnumFipaProtocol
	 */
	public void setProtocol(EnumFipaProtocol protocol) {
		getContent().setProtocol(protocol.getName());
	}
	
	/**
	 * Gets the conversation Id of the ACL Message.
	 * @return the conversation Id of the ACL Message.
	 */
	public final UUID getConversationId() {
		return getContent().getConversationId();
	}
	
	/**
	 * Sets the conversation Id of the ACL Message.
	 * 
	 * @param conversationId is an {@link UUID}
	 */
	public final void setConversationId(UUID conversationId) {
		getContent().setConversationId(conversationId);
	}
	
	/**
	 * This inner-class represents the envelope of the ACL Message.
	 * 
	 * @see <a href="http://www.fipa.org/specs/fipa00085/SC00085J.html">FIPA Agent Message Transport Envelope Representation in XML Specification</a>
	 */
	public static class Envelope implements ACLMessageEnvelope, Serializable {

		private static final long serialVersionUID = 7549961619925954406L;
		
		private Collection<AgentAddress> to = null;
		private AgentAddress from = null;
		private String aclRepresentation = null;
		private Date date = null;
		private String comments = null;
		private Long payloadLength = null;
		private String payloadEncoding = null;
		private ArrayList<AgentAddress> intendedReceiver = null;
		private Properties transportBehaviour = null;
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public Collection<AgentAddress> getTo() {
			return this.to;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setTo(Collection<AgentAddress> addresses) {
			this.to = addresses;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public AgentAddress getFrom() {
			return this.from;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setFrom(AgentAddress address) {
			this.from = address;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getAclRepresentation() {
			return this.aclRepresentation;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setAclRepresentation(String aclRepresentation) {
			this.aclRepresentation = aclRepresentation;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public Date getDate() {
			return this.date;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setDate(Date date) {
			this.date = date;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getComments() {
			return this.comments;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setComments(String comments) {
			this.comments = comments;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public Long getPayloadLength() {
			return this.payloadLength;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setPayloadLength(Long payloadLength) {
			this.payloadLength = payloadLength;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getPayloadEncoding() {
			return this.payloadEncoding;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setPayloadEncoding(String payloadEncoding) {
			this.payloadEncoding = payloadEncoding;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public ArrayList<AgentAddress> getIntendedReceiver() {
			return this.intendedReceiver;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setIntendedReceiver(ArrayList<AgentAddress> intendedReceivers) {
			this.intendedReceiver = intendedReceivers;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public Properties getTransportBehaviour() {
			return this.transportBehaviour;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setTransportBehaviour(Properties transportBehaviour) {
			this.transportBehaviour = transportBehaviour;
		}
    	
    }
	
	/**
	 * This inner-class represents the content of the ACL Message.
	 * 
	 * @see <a href="http://www.fipa.org/specs/fipa00061/SC00061G.html">FIPA ACL Message Structure Specification</a>
	 */
    public static class Content implements ACLMessageContent{
    	
		private int performative; 
    	private AgentAddress sender = null; 
    	private Collection<AgentAddress> receiver = null; 
    	private Collection<AgentAddress> replyTo = null; 
    	private StringBuffer content = null; 
    	private String language = null; 
    	private String encoding = null; 
    	private String ontology = null; 
    	private String protocol = null; 
    	private UUID conversationId = null; 
    	private String replyWith = null; 
    	private String inReplyTo = null; 
    	private Date replyBy = null; 
    	
    	/**
    	 * {@inheritDoc}
    	 */
		@Override
    	public int getPerformative() {
			return this.performative;
		}
    	
    	/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setPerformative(int performative) {
			this.performative = performative;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public AgentAddress getSender() {
			return this.sender;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setSender(AgentAddress address) {
			this.sender = address;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public Collection<AgentAddress> getReceiver() {
			return this.receiver;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setReceiver(Collection<AgentAddress> receivers) {
			this.receiver = receivers;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public Collection<AgentAddress> getReplyTo() {
			return this.replyTo;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setReplyTo(Collection<AgentAddress> replyTo) {
			this.replyTo = replyTo;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public StringBuffer getContent() {
			return this.content;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setContent(StringBuffer content) {
			this.content = content;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getLanguage() {
			return this.language;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setLanguage(String language) {
			this.language = language;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getEncoding() {
			return this.encoding;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getOntology() {
			return this.ontology;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setOntology(String ontology) {
			this.ontology = ontology;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getProtocol() {
			return this.protocol;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public UUID getConversationId() {
			return this.conversationId;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setConversationId(UUID conversationId) {
			this.conversationId = conversationId;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getReplyWith() {
			return this.replyWith;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setReplyWith(String replyWith) {
			this.replyWith = replyWith;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public String getInReplyTo() {
			return this.inReplyTo;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setInReplyTo(String inReplyTo) {
			this.inReplyTo = inReplyTo;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public Date getReplyBy() {
			return this.replyBy;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		@Override
		public void setReplyBy(Date replyBy) {
			this.replyBy = replyBy;
		}
    }

}