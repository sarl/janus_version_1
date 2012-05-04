package org.janusproject.acl;

/**
 * This enumeration describes all available performatives as defined by FIPA
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00037/SC00037J.html">FIPA Communicative Act Library Specification</a>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum Performative {
	/**
	 */
	NONE, 
	/**
	 */
	ACCEPT_PROPOSAL,  
	/**
	 */
	AGREE,  
	/**
	 */
	CANCEL,  
	/**
	 */
	CFP,  
	/**
	 */
	CONFIRM,  
	/**
	 */
	DISCONFIRM,  
	/**
	 */
	FAILURE,  
	/**
	 */
	INFORM,  
	/**
	 */
	INFORM_IF,  
	/**
	 */
	INFORM_REF,  
	/**
	 */
	NOT_UNDERSTOOD,  
	/**
	 */
	PROPOSE,  
	/**
	 */
	QUERY_IF,  
	/**
	 */
	QUERY_REF,  
	/**
	 */
	REFUSE,  
	/**
	 */
	REJECT_PROPOSAL,  
	/**
	 */
	REQUEST,  
	/**
	 */
	REQUEST_WHEN,  
	/**
	 */
	REQUEST_WHENEVER,  
	/**
	 */
	SUBSCRIBE,  
	/**
	 */
	PROXY,  
	/**
	 */
	PROPAGATE
}
