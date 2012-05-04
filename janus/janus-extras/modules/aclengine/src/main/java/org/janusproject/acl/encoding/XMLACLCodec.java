package org.janusproject.acl.encoding;

import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.ACLMessageContent;

/**
 * TODO : class supposed to permit to encode/decode the content of an ACL Message in XML.
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00071/SC00071E.html">FIPA ACL Message Representation in XML Specification</a>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class XMLACLCodec implements ACLMessageContentEncodingService
{
	/**
	 * TODO
	 */
	@Override
	public byte[] encode(ACLMessage aMsg) {
		return null;
	}

	/**
	 * TODO
	 */
	@Override
	public ACLMessageContent decode(byte[] byteMsg, Object... parameters) {
		return null;
	}
}
