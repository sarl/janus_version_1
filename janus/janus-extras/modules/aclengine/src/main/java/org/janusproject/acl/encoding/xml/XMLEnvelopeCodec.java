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
package org.janusproject.acl.encoding.xml;

import org.janusproject.acl.ACLMessage.Envelope;
import org.janusproject.acl.encoding.ACLMessageEnvelopeEncodingService;
import org.janusproject.acl.ACLMessageEnvelope;

/**
 * TODO : class supposed to permit to encode/decode the envelope of an ACL Message in XML.
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00085/SC00085J.html">FIPA Agent Message Transport Envelope Representation in XML Specification</a>
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
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
