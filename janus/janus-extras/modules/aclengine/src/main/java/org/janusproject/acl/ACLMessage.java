package org.janusproject.acl;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import org.janusproject.acl.encoding.StringACLCodec;
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
 * @version $Name$ $Revision$ $Date$
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
		public Collection<AgentAddress> getTo() {
			return this.to;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setTo(Collection<AgentAddress> addresses) {
			this.to = addresses;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public AgentAddress getFrom() {
			return this.from;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setFrom(AgentAddress address) {
			this.from = address;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getAclRepresentation() {
			return this.aclRepresentation;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setAclRepresentation(String aclRepresentation) {
			this.aclRepresentation = aclRepresentation;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public Date getDate() {
			return this.date;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setDate(Date date) {
			this.date = date;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getComments() {
			return this.comments;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setComments(String comments) {
			this.comments = comments;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public Long getPayloadLength() {
			return this.payloadLength;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setPayloadLength(Long payloadLength) {
			this.payloadLength = payloadLength;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getPayloadEncoding() {
			return this.payloadEncoding;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setPayloadEncoding(String payloadEncoding) {
			this.payloadEncoding = payloadEncoding;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public ArrayList<AgentAddress> getIntendedReceiver() {
			return this.intendedReceiver;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setIntendedReceiver(ArrayList<AgentAddress> intendedReceivers) {
			this.intendedReceiver = intendedReceivers;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public Properties getTransportBehaviour() {
			return this.transportBehaviour;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
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
    	private String replyBy = null; 
    	
    	/**
    	 * {@inheritDoc}
    	 */
    	public int getPerformative() {
			return this.performative;
		}
    	
    	/**
    	 * {@inheritDoc}
    	 */
		public void setPerformative(int performative) {
			this.performative = performative;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public AgentAddress getSender() {
			return this.sender;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setSender(AgentAddress address) {
			this.sender = address;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public Collection<AgentAddress> getReceiver() {
			return this.receiver;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setReceiver(Collection<AgentAddress> receivers) {
			this.receiver = receivers;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public Collection<AgentAddress> getReplyTo() {
			return this.replyTo;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setReplyTo(Collection<AgentAddress> replyTo) {
			this.replyTo = replyTo;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public StringBuffer getContent() {
			return this.content;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setContent(StringBuffer content) {
			this.content = content;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getLanguage() {
			return this.language;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setLanguage(String language) {
			this.language = language;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getEncoding() {
			return this.encoding;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setEncoding(String encoding) {
			this.encoding = encoding;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getOntology() {
			return this.ontology;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setOntology(String ontology) {
			this.ontology = ontology;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getProtocol() {
			return this.protocol;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public UUID getConversationId() {
			return this.conversationId;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setConversationId(UUID conversationId) {
			this.conversationId = conversationId;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getReplyWith() {
			return this.replyWith;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setReplyWith(String replyWith) {
			this.replyWith = replyWith;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getInReplyTo() {
			return this.inReplyTo;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setInReplyTo(String inReplyTo) {
			this.inReplyTo = inReplyTo;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public String getReplyBy() {
			return this.replyBy;
		}
		
		/**
    	 * {@inheritDoc}
    	 */
		public void setReplyBy(String replyBy) {
			this.replyBy = replyBy;
		}
		
    }
}