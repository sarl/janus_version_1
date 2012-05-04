package org.janusproject.acl.encoding;

import org.janusproject.acl.ACLMessage.Envelope;
import org.janusproject.acl.ACLMessageEnvelope;

/**
 * This interface describes the EncodingService which encodes the ACLMessageEnvelope
 * of an ACLMessage
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
interface ACLMessageEnvelopeEncodingService
{
    /**
     * This method encodes the ACLMessageContent of an ACLMessage
     * 
     * @param env the ACLMessageEnvelope to encode
     * @return the ACLMessageEnvelope encoded
     */
    public byte[] encode(Envelope env);
    
    /**
     * This method decodes the ACLMessageEnvelope of an ACLMessage encoded
     * 
     * @param encodedEnvelope the ACLMessageEnvelope to decode
     * @return the ACLMessageEnvelope decoded
     */
    public ACLMessageEnvelope decode(byte[] encodedEnvelope);
}
