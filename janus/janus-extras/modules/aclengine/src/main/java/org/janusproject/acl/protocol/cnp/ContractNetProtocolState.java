package org.janusproject.acl.protocol.cnp;

import org.janusproject.acl.protocol.ProtocolState;

/**
 * This enumeration describes all available states of the Contract Net Protocol (CNP).
 * 
 * @see FipaContractNetProtocol
 * @see ProtocolState
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum ContractNetProtocolState implements ProtocolState {
	/**
	 */
	NOT_STARTED,
	/**
	 */
	SENDING_CALL_FOR_PROPOSAL,
	/**
	 */
	WAITING_CALL_FOR_PROPOSAL,
	/**
	 */
	SENDING_PROPOSAL,
	/**
	 */
	WAITING_ALL_PROPOSALS,
	/**
	 */
	SENDING_ALL_PROPOSAL_ANSWERS,
	/**
	 */
	WAITING_PROPOSAL_ANSWER,
	/**
	 */
	SENDING_RESULT,
	/**
	 */
	WAITING_ALL_RESULTS,
	/**
	 */
	CANCELING,
	/**
	 */
	CANCELED,
	/**
	 */
	DONE;
}
