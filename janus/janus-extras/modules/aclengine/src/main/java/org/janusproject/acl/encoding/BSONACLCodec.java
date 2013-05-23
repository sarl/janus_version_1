/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	 * 
	 * @return the BSON mapper.
	 */
	@Override
	protected ObjectMapper getMapper() {
		return new ObjectMapper(new BsonFactory());
	}
}
