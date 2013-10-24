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

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.ACLMessageContent;
import org.janusproject.acl.ACLMessageEnvelope;
import org.janusproject.acl.ACLRepresentation;
import org.janusproject.acl.ACLTransportMessage;
import org.janusproject.acl.encoding.bitefficient.BitEfficientACLCodec;
import org.janusproject.acl.encoding.bitefficient.BitEfficientEnvelopeCodec;
import org.janusproject.acl.encoding.xml.XMLACLCodec;
import org.janusproject.acl.exception.UnspecifiedACLMessageRepresentationException;

/**
 * This class manages all the encoding/decoding repartition tasks.
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ACLEncodingService
{
	private Logger logger; 
	
	/**
	 * the ACLMessageContentEncodingService used to encode the ACLMessageContent
	 */
	private ACLMessageContentEncodingService contentEncodingService;
	
	/**
	 * the ACLMessageEnvelopeEncodingService used to encode the ACLMessageEnvelope
	 */
	private ACLMessageEnvelopeEncodingService envelopeEncodingService;
	
	/**
	 * The normal size of the envelope length
	 */
	private static int ENVELOPE_LENGTH = 2;
	
	/**
	 * The big size of the envelope length
	 */
	private static int BIG_ENVELOPE_LENGTH = 6;
	
	/**
	 * Creates a new ACLEncodingService.
	 */
	public ACLEncodingService() {
		this.contentEncodingService = null;
		this.envelopeEncodingService = null;
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
	}
    
    /**
     * Encodes a given ACL Message into an array of bytes 
     * (which is commonly called the payload).
     * 
     * @param aMsg is the ACL Message to encode
     * @param parameters
     * @return the ACLMessage encoded in an array of bytes
     * 
     */
	public byte[] encode(ACLMessage aMsg, Object... parameters)
    {
    	try {
			initEncodingServices(aMsg.getEnvelope());
		} 
    	catch (UnspecifiedACLMessageRepresentationException e) {
			e.printStackTrace();
		}
		
    	return buildEncodedACLMessage(this.contentEncodingService.encode(aMsg), this.envelopeEncodingService.encode(aMsg.getEnvelope()));
    }

	/**
	 * Gets the payload 
	 * for a given content encoded in an array of bytes 
	 * and for a given envelope encoded in an array of bytes.
	 * <p>
	 * To be able to easily get back the envelope and the content 
	 * from the payload when decoding, the payload must respect 
	 * the following representation as defined by Fipa :
	 * <ul>
	 * <li>Overview of the returned payload : [ [Envelope Length] | [Envelope] | [Content] ]</li>
	 * </ul>
	 * <p>
	 * Where the envelope length respects the following :
	 * <ul>
	 * <li>2 bytes are reserved for envelope size (cf: {@link #ENVELOPE_LENGTH})</li>
	 * <li>If more space is needed, the 2 first bytes are set to zero 
	 * and 4 bytes (BIG_ENVELOPE_LENGTH - ENVELOPE_LENGTH) are added to contain the envelope length (cf: {@link #BIG_ENVELOPE_LENGTH})</li>
	 * </ul>
	 * 
	 * @see <a href=":">Fipa Representation</a>
	 * 
	 * @param encodedContent is the ACLMessageContent encoded in an array of bytes
	 * @param encodedEnvelope is the ACLMessageEnvelope encode in an array of bytes
	 * @return the payload
	 */
	private static byte[] buildEncodedACLMessage(byte[] encodedContent, byte[] encodedEnvelope) {
	
		int envelopeLength = encodedEnvelope.length;
		int contentLength = encodedContent.length;
		
		int envelopeSizeLength = envelopeLength < Short.MAX_VALUE ? ENVELOPE_LENGTH : BIG_ENVELOPE_LENGTH;
		
		int totalLength = envelopeLength + contentLength + envelopeSizeLength;
		ByteBuffer completePayload = ByteBuffer.allocate(totalLength);
		
		if ( envelopeSizeLength == ENVELOPE_LENGTH ) {
			completePayload.putShort( (short) envelopeLength );
		} 
		else {
			completePayload.putShort( (short) 0 );
			completePayload.putInt( envelopeSizeLength );
		}
		
		completePayload.put( encodedEnvelope );
		completePayload.put( encodedContent );	
		
		return completePayload.array();
	}

	/**
	 * Gets the ACL Message from an ACL Transport Message.
	 * <ul>
	 * <li>The ACL Transport Message corresponds to the payload</p>
	 * <li>The payload has been previously encoded by {@link #encode(ACLMessage, Object...)} 
	 * as defined by FIPA : [ [Envelope Length] | [Envelope] | [Content] ]</li>
	 * </ul>
	 * <p>
	 * Workflow :
	 * <ol>
	 * <li>Get the envelope length</li>
	 * <li>Get back the envelope in an array bytes according to the previously recovered envelope length</li>
	 * <li>Get back the corresponding {@link ACLMessageEnvelope}</li>
	 * <li>Get the content length according to the global length and the envelope length</li>
	 * <li>Get back the content in an array of bytes according to the previously recovered content length</li>
	 * <li>Get back the corresponding {@link ACLMessageContent}</li>
	 * <li>Create a new ACL Message from the recovered envelope and content</li>
	 * </ol>
	 * 	
	 * @param tMsg the ACLTransportMessage containing the encoded ACLMessage (payload)
	 * @param parameters
	 * @return the new ACLMessage decoded and rebuilt
	 */
    public ACLMessage decode(ACLTransportMessage tMsg, Object... parameters)
    {		
    	this.envelopeEncodingService = new BitEfficientEnvelopeCodec();
		
		ByteBuffer payloadBuffer = ByteBuffer.wrap(tMsg.getPayload());
		
		// GET ENVELOPE :
		int envelopeLength = payloadBuffer.getShort(0);
		int envelopeSizeLength = ENVELOPE_LENGTH;
		
		if (envelopeLength == 0) { // case of big envelope
			envelopeLength = payloadBuffer.getInt(ENVELOPE_LENGTH);
			envelopeSizeLength = BIG_ENVELOPE_LENGTH;
		}

		byte[] encodedEnvelope = new byte[envelopeLength];
		payloadBuffer.position(envelopeSizeLength);
		payloadBuffer.get(encodedEnvelope, 0, envelopeLength);
		ACLMessageEnvelope envelope = this.envelopeEncodingService.decode(encodedEnvelope);
		
		try {
			initContentEncodingService(envelope);
		} catch (UnspecifiedACLMessageRepresentationException e) {
			e.printStackTrace();
		}
		
		// GET CONTENT :
		int contentLength = payloadBuffer.capacity() - envelopeLength - envelopeSizeLength;
		byte[] encodedContent = new byte[contentLength];
		payloadBuffer.position(envelopeLength + envelopeSizeLength);
		payloadBuffer.get(encodedContent, 0, contentLength);
		ACLMessageContent content = this.contentEncodingService.decode(encodedContent);
		
		return new ACLMessage(envelope, content);
    }
    
    /**
     * This method gets the encoding type of the payload (encoded ACLMessage)
     * 
     * @param envelope the ACLMessageEnvelope containing the encoding type we want
     * @return encoding type of the payload
     * 
     * @see PayloadEncoding
     */
    @SuppressWarnings("unused")
	private PayloadEncoding getPayloadEncoding(ACLMessageEnvelope envelope) {

    	try {
    		return PayloadEncoding.valueOf(envelope.getPayloadEncoding());
    	}
    	catch (Exception e) {
			this.logger.log(Level.WARNING, Locale.getString(ACLEncodingService.class, "UNSPECIFIEDENCODING")); //$NON-NLS-1$
			return PayloadEncoding.UTF8;
		} 	
    }
    
    /**
     * Instantiates the {@link #contentEncodingService} to use 
     * to decode the encode/decode the content of a given ACL Message
     * according to the ACL representation defined in the envelope.
     * 
     * @param envelope
     * @throws UnspecifiedACLMessageRepresentationException
     * 
     * @see ACLRepresentation
     */
    private void initContentEncodingService(ACLMessageEnvelope envelope) throws UnspecifiedACLMessageRepresentationException {
    	
    	String aclRepresentation = envelope.getAclRepresentation();
    	
    	if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.BIT_EFFICIENT.getValue())) {
    		this.contentEncodingService = new BitEfficientACLCodec();
		} 
    	else if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.STRING.getValue())) {
    		this.contentEncodingService = new StringACLCodec();
		} 
    	else if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.XML.getValue())) {
    		this.contentEncodingService = new XMLACLCodec();
    	}
    	else if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.JSON.getValue())) {
    		this.contentEncodingService = new JSONACLCodec();
    	}
    	else if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.BSON.getValue())) {
    		this.contentEncodingService = new BSONACLCodec();
		} else {
			throw new UnspecifiedACLMessageRepresentationException();
		}
    }
    
    /**
     * Instantiates the {@link #contentEncodingService} and the {@link #envelopeEncodingService} to use 
     * to decode the encode/decode the content and the envelope of a given ACL Message
     * according to the ACL representation defined in the envelope.
     * 
     * @param envelope
     * @throws UnspecifiedACLMessageRepresentationException
     * 
     * @see ACLRepresentation
     */
    private void initEncodingServices(ACLMessageEnvelope envelope) throws UnspecifiedACLMessageRepresentationException {
    	
    	String aclRepresentation = envelope.getAclRepresentation();
    	
    	if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.BIT_EFFICIENT.getValue())) {
    		this.contentEncodingService = new BitEfficientACLCodec();
			this.envelopeEncodingService = new BitEfficientEnvelopeCodec();
		} 
    	else if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.STRING.getValue())) {
    		this.contentEncodingService = new StringACLCodec();
			this.envelopeEncodingService = new BitEfficientEnvelopeCodec();
		} 
    	else if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.XML.getValue())) {
    		this.contentEncodingService = new XMLACLCodec();
    		this.envelopeEncodingService = new XMLEnvelopeCodec();
    	}
    	else if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.JSON.getValue())) {
    		this.contentEncodingService = new JSONACLCodec();
    		this.envelopeEncodingService = new BitEfficientEnvelopeCodec();
		}
    	else if (aclRepresentation.equalsIgnoreCase(ACLRepresentation.BSON.getValue())) {
    		this.contentEncodingService = new BSONACLCodec();
    		this.envelopeEncodingService = new BitEfficientEnvelopeCodec();
    	}
    	else {
			throw new UnspecifiedACLMessageRepresentationException();
		}
    }
    
	/**
	 * Getter of contentEncodingService
	 * 
	 * @return the contentEncodingService used to encode the ACLMessageContent
	 */
	public ACLMessageContentEncodingService getContentEncodingService() {
		return this.contentEncodingService;
	}

	/**
	 * Setter of contentEncodingService used to encode the ACLMessageContent
	 * 
	 * @param contentEncodingService to be set
	 */
	public void setContentEncodingService(ACLMessageContentEncodingService contentEncodingService) {
		this.contentEncodingService = contentEncodingService;
	}

	/**
	 * Getter of envelopeEncodingService
	 * 
	 * @return envelopeEncodingService used to encode the envelope
	 */
	public ACLMessageEnvelopeEncodingService getEnvelopeEncodingService() {
		return this.envelopeEncodingService;
	}

	/**
	 * Setter of envelopeEncodingService used to encode the envelope
	 * 
	 * @param envelopeEncodingService to be set
	 */
	public void setEnvelopeEncodingService(ACLMessageEnvelopeEncodingService envelopeEncodingService) {
		this.envelopeEncodingService = envelopeEncodingService;
	}
}
