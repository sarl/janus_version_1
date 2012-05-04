package org.janusproject.acl;

import org.janusproject.kernel.message.ObjectMessage;

/**
 * This class extends ObjectMessage and is used to
 * transport the encoded ACLMessage (payload) via
 * the Message Transport Service
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLTransportMessage extends ObjectMessage
{
	private static final long serialVersionUID = -296154029511090541L;

	/**
	 * Creates a new ACLTransportMessage containing
	 * the ACLMessage encoded (payload) in byte array
	 * got as parameter
	 * 
	 * @param payload the ACLMessage encoded in byte array
	 */
	public ACLTransportMessage(byte[] payload) {
		super(payload);
	}
	
	/**
	 * Getter of the ACLTransportMessage content
	 * 
	 * @return the content of the ACLTransportMessage
	 * normally the encoded ACLMessage (payload)
	 */
	public byte[] getPayload() {
		return getContent(byte[].class);
	}
}
