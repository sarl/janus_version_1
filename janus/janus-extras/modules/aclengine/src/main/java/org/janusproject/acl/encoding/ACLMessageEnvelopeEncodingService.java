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
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface ACLMessageEnvelopeEncodingService
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
