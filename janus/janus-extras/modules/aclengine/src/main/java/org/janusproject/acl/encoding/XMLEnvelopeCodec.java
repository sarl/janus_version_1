package org.janusproject.acl.encoding;

import org.janusproject.acl.ACLMessage.Envelope;
import org.janusproject.acl.ACLMessageEnvelope;

/**
 * TODO : class supposed to permit to encode/decode the envelope of an ACL Message in XML.
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00085/SC00085J.html">FIPA Agent Message Transport Envelope Representation in XML Specification</a>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class XMLEnvelopeCodec implements ACLMessageEnvelopeEncodingService
{
	/**
	 * TODO
	 */
	@Override
	public byte[] encode(Envelope env) {
		return null;
	}

	/**
	 * TODO
	 */
	@Override
	public ACLMessageEnvelope decode(byte[] encodedEnvelope) {
		return null;
	}
}
