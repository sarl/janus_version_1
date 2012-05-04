package org.janusproject.acl.encoding;

import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.ACLMessageContent;

/**
 * This interface describes the EncodingService which encodes the ACLMessageContent
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
interface ACLMessageContentEncodingService
{
    /**
     * This method encodes the ACLMessageContent of an ACLMessage in the matching type
     * 
     * @param aMsg the ACLMessage containing the ACLMessageContent
     * @return the ACLMessageContent encoded
     */
    public byte[] encode(ACLMessage aMsg);
    
    /**
     * This method decodes the ACLMessageContent of an ACLMessage encoded in the matching type
     * 
     * @param byteMsg the ACLMessage encoded in byte array (payload)
     * @param parameters
     * @return the ACLMessageContent decoded
     */
    public ACLMessageContent decode(byte[] byteMsg, Object... parameters);
}
