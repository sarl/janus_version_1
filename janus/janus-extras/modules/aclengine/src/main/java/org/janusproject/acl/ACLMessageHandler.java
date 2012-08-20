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
package org.janusproject.acl;

import java.util.Arrays;

import org.janusproject.acl.encoding.ACLEncodingService;
import org.janusproject.kernel.address.AgentAddress;

/**
 * This class handles the transmission of the ACLMessage before/after encoding and decoding
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLMessageHandler
{
	/**
	 * The encoding service of the class needed to encode
	 * or decode the ACLMessage
	 */
	private ACLEncodingService encodingService;
	
	/**
	 * Creates an ACLMessageHandler with a new 
	 * ACLEncodingService ready to encode/decode
	 */
	public ACLMessageHandler() {
		this.encodingService = new ACLEncodingService(); 
	}
	
	/**
	 * This method sets the receivers of the ACLMessage
	 * and calls the method encode() of the {@link #getEncodingService}
	 * to encode the ACLMessage in an array of bytes and return it.
	 * 
	 * @param aMsg is the ACLMessage to be encoded
	 * @param agents is the list of receivers of the ACLMessage
	 * @return a new ACLTransportMessage containing the encoded ACLMessage (payload)
	 */
	public ACLTransportMessage prepareOutgoingACLMessage(ACLMessage aMsg, AgentAddress... agents)
    {
		aMsg.setReceiver(Arrays.asList(agents));
		byte[] payload = this.encodingService.encode(aMsg);
    	return new ACLTransportMessage(payload);
    }
    
    /**
     * This method calls the method decode() of the {@link #getEncodingService}
     * to decode the ACLMessage encoded inside the ACLTransportMessage
     * and then returns it.
     * 
     * @param tMsg is the ACLTransportMessage containing the encoded ACLMessage
     * @param parameters
     * @return the corresponding ACL Message
     */
    public ACLMessage prepareIncomingMessage(ACLTransportMessage tMsg, Object... parameters)
    {
    	return this.encodingService.decode(tMsg, parameters);
    }

    /**
     * 
     * @return the encoding service associated to this message handler
     */
	public ACLEncodingService getEncodingService() {
		return this.encodingService;
	}
    
    
}
