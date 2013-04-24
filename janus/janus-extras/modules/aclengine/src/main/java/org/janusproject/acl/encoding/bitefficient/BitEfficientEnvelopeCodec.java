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
package org.janusproject.acl.encoding.bitefficient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.ACLMessage.Envelope;
import org.janusproject.acl.encoding.ACLMessageEnvelopeEncodingService;
import org.janusproject.acl.ACLMessageEnvelope;

/**
 * This class encodes an ACLMessageEnvelope in bytes or decodes
 * an ACLMessageEnvelope encoded in bytes
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class BitEfficientEnvelopeCodec implements ACLMessageEnvelopeEncodingService
{
	/** {@inheritDoc}
	 */
	@Override
	public byte[] encode(Envelope envelope) {
		
		byte[] encodedEnvelope = null;
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // Output flow
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream( byteArrayOutputStream );
			try {
				objectOutputStream.writeObject(envelope); // Serialization of the envelope
				encodedEnvelope = byteArrayOutputStream.toByteArray(); // Envelope encoded in array of bytes
				objectOutputStream.flush();
			} 
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
			finally {
				objectOutputStream.close();
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return encodedEnvelope;
		
	}

	/** {@inheritDoc}
	 */
	@Override
	public ACLMessageEnvelope decode(byte[] encodedEnvelope) {
		
		ACLMessage.Envelope decodedEnvelope = null;
		
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encodedEnvelope); // Input flow
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			try {
				// Unserialization of the envelope
				decodedEnvelope = (ACLMessage.Envelope) objectInputStream.readObject(); 
			} 
			catch(IOException ioe) {
				ioe.printStackTrace();
			} 
			catch(ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			}
			finally {
				objectInputStream.close();
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		} 
		
		return decodedEnvelope;		
	}
}
