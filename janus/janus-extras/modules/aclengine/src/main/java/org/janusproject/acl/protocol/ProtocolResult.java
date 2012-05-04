package org.janusproject.acl.protocol;

import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.request.FipaRequestProtocol;
import org.janusproject.kernel.address.AgentAddress;

/**
 * This class is used to simplify ACL Messages when working with protocols.
 * <p>
 * For example, if you're calling getRequest() from {@link FipaRequestProtocol} 
 * you won't get back the whole corresponding ACL Message but only a summarize of it : 
 * the performative and the content.
 * </p>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ProtocolResult {
	
	private Performative performative;
	private Object content;
	private AgentAddress author;
	
	/**
	 * 
	 */
	public ProtocolResult() {}
	
	/**
	 * Creates a new ProtocoleResult.
	 * @param author 
	 * 
	 * @param performative
	 * @param content
	 */
	public ProtocolResult(AgentAddress author, Performative performative, Object content) {
		this.author = author;
		this.performative = performative;
		this.content = content;
	}
	
	/**
	 * Gets the performative.
	 * @return the performative associated to an ACL message
	 */
	public Performative getPerformative() {
		return this.performative;
	}
	
	/**
	 * Sets the performative.
	 * 
	 * @param performative
	 */
	public void setPerformative(Performative performative) {
		this.performative = performative;
	}
	
	/**
	 * Gets the content.
	 * @return the content of an ACL message
	 */
	public Object getContent() {
		return this.content;
	}
	
	/**
	 * Sets the content.
	 * 
	 * @param content
	 */
	public void setContent(Object content) {
		this.content = content;
	}

	/**
	 * @return the author
	 */
	public AgentAddress getAuthor() {
		return this.author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(AgentAddress author) {
		this.author = author;
	}
}
