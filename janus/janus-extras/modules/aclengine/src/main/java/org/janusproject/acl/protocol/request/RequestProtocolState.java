package org.janusproject.acl.protocol.request;

import org.janusproject.acl.protocol.ProtocolState;

/**
 * This enumeration describes all available states of the Request Protocol.
 * 
 * @see FipaRequestProtocol
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
public enum RequestProtocolState implements ProtocolState {
	/**
	 */
	NOT_STARTED,
	/**
	 */
	SENDING_REQUEST,
	/**
	 */
	WAITING_REQUEST,
	/**
	 */
	SENDING_ANSWER,
	/**
	 */
	WAITING_ANSWER,
	/**
	 */
	SENDING_RESULT,
	/**
	 */
	WAITING_RESULT,
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
