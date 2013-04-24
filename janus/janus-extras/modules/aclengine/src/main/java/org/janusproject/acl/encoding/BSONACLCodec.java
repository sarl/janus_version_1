package org.janusproject.acl.encoding;

import org.codehaus.jackson.map.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;

/**
 * This class encodes an ACLMessageContent in BSON or decodes
 * an ACLMessageContent encoded in bytes to BSON
 * 
 * @author $Author: ngrenie$
 * @author $Author: bfeld$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BSONACLCodec extends JSONACLCodec {

	/**
	 * Return BSON ObjectMapper, used to convert Map to BSON String
	 */
	protected ObjectMapper getMapper() {
		return new ObjectMapper(new BsonFactory());
	}
}
