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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.UUID;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.ACLMessageContent;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.AddressUtil;

/**
 * This class encodes an ACLMessageContent in String or decodes
 * an ACLMessageContent encoded in bytes to String
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class StringACLCodec implements ACLMessageContentEncodingService
{
	/**
	 * Encodes a given ACL Message into an array of bytes.
	 */
	@Override
	public byte[] encode(ACLMessage aMsg) {
		byte[] payload;
		try {
			payload = aMsg.toString().getBytes(aMsg.getEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new byte[0];
		}
		return payload;
	}

	/**
	 * Decodes a given array of bytes supposed to correspond to the ACLMessageContent.
	 */
	@Override
	public ACLMessageContent decode(byte[] byteMsg, Object... parameters) {
		
		ACLMessage.Content content = new ACLMessage.Content();
		
		// Get charset parameter
		String charset = PayloadEncoding.UTF8.getValue();
		for (Object parameter : parameters) {
			if (parameter instanceof PayloadEncoding) {
				charset = ((PayloadEncoding)parameter).getValue();
			}
		}
		
		// Try decoding with provided charset
		String message;
		try {
			message = new String(byteMsg, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} 
		StringTokenizer st = new StringTokenizer(message,"\n",false); //$NON-NLS-1$
		
		String str;
		
		// Loop through string tokenizer...
		
		while (st.hasMoreTokens())
		{	
			str = st.nextToken();
			
			// PERFORMATIVE
			if( StringACLCodecHelper.isPerformative( str ) ){
				content.setPerformative( StringACLCodecHelper.getPerformative( str ) );				
			}
			// SENDER
			else if( StringACLCodecHelper.isSender( str ) ){
				content.setSender( AddressUtil.createAgentAddress( UUID.fromString( StringACLCodecHelper.getSender( str ) )) );
			}
			// RECEIVERS
			else if( StringACLCodecHelper.isReceiver( str ) ){
				Collection<AgentAddress> receivers = new ArrayList<AgentAddress>();
				
				for(String agentId : StringACLCodecHelper.getReceiversList( str ) ){
					receivers.add( AddressUtil.createAgentAddress( UUID.fromString( agentId )) );
				}
				
				content.setReceiver(receivers);
			}
			// CONTENT
			else if( StringACLCodecHelper.isContent( str ) ){
				content.setContent( new StringBuffer( StringACLCodecHelper.getContent(str) ) );
			}
			// ENCODING
			else if( StringACLCodecHelper.isEncoding( str ) ){
				content.setEncoding( StringACLCodecHelper.getEncoding(str) );
			}
			// LANGUAGE
			else if( StringACLCodecHelper.isLanguage( str ) ){
				content.setLanguage( StringACLCodecHelper.getLanguage(str) );
			}
			// ONTOLOGY
			else if( StringACLCodecHelper.isOntology( str ) ){
				content.setOntology( StringACLCodecHelper.getOntology(str) );
			}
			// PROTOCOL
			else if( StringACLCodecHelper.isProtocol( str ) ){
				content.setProtocol( StringACLCodecHelper.getProtocol(str) );
			}
			// CONVERSATION ID
			else if( StringACLCodecHelper.isConversationId( str ) ){
				String uuid = StringACLCodecHelper.getConversationId(str);
				
				if( StringACLCodecHelper.isUUID( uuid ) )
					content.setConversationId( UUID.fromString( uuid ) );
				else
					content.setConversationId( null );
			}
		}
		
		return content;
	}
	
	/**
	 * Adapted from JADE.
	 * <p>
     * If a user-defined parameter contain a blank char inside, then it is skipped for FIPA-compatibility.
	 * @param msg 
     * 
     * @return a String encoded message
     * @see ACLMessage#toString()
     **/
    public static String toString(ACLMessage msg) {
      
      StringBuffer str = new StringBuffer("("); //$NON-NLS-1$
      
      // Display PERFORMATIVE
      str.append(msg.getPerformative());
      str.append("\n"); //$NON-NLS-1$
      
      // Display SENDER
      AgentAddress sender = msg.getSender();
      if (sender != null){ 
    	  str.append(Locale.getString(StringACLCodec.class, "SENDER")); //$NON-NLS-1$
    	  str.append(" "); //$NON-NLS-1$
    	  str.append(toString(sender));
    	  str.append("\n"); //$NON-NLS-1$
      }
      
      // Display RECEIVERS
      Collection<AgentAddress> receivers = msg.getReceiver();    
      
      if(receivers != null){
    	  boolean isFirst = true;
      	for (AgentAddress receiver : receivers) {
    	  	if (isFirst) 
    		str.append(Locale.getString(StringACLCodec.class, "RECEIVER")); //$NON-NLS-1$
    		str.append(" ( set "); //$NON-NLS-1$
    	  	str.append(toString(receiver));
    	  	str.append(" "); //$NON-NLS-1$
      	}
      	str.append(")\n"); //$NON-NLS-1$
      }

      // Display CONTENT
	  String content = msg.getContent().getContent().toString();
	  str.append(Locale.getString(StringACLCodec.class, "CONTENT")); //$NON-NLS-1$
	  str.append(" \""); //$NON-NLS-1$
	  if (content != null && content.length() > 0){
		  str.append(content.trim());
	  }
	  str.append("\" \n"); //$NON-NLS-1$
	  
	  // Display ENCODING
	  str.append(Locale.getString(StringACLCodec.class, "ENCODING")); //$NON-NLS-1$
	  str.append(" "); //$NON-NLS-1$
	  str.append(msg.getEncoding());
	  str.append("\n"); //$NON-NLS-1$
	  
	  // Display LANGUAGE
	  str.append(Locale.getString(StringACLCodec.class, "LANGUAGE")); //$NON-NLS-1$
	  str.append(" "); //$NON-NLS-1$
	  str.append(msg.getLanguage());
	  str.append("\n"); //$NON-NLS-1$
	  
	  // Display ONTOLOGY
	  str.append(Locale.getString(StringACLCodec.class, "ONTOLOGY")); //$NON-NLS-1$
	  str.append(" "); //$NON-NLS-1$
	  str.append(msg.getOntology());
	  str.append("\n"); //$NON-NLS-1$

	  // Display PROTOCOL
	  str.append(Locale.getString(StringACLCodec.class, "PROTOCOL")); //$NON-NLS-1$
	  str.append(" "); //$NON-NLS-1$
	  str.append(msg.getProtocol().getName());
	  str.append("\n"); //$NON-NLS-1$
	  
	  // Display CONVERSATION ID
	  str.append(Locale.getString(StringACLCodec.class, "CONVERSATIONID")); //$NON-NLS-1$
	  str.append(" "); //$NON-NLS-1$
	  str.append(msg.getConversationId());
	  str.append("\n"); //$NON-NLS-1$
	  
      str.append(")"); //$NON-NLS-1$

      return str.toString();
    }
    
    /**
     * This methods converts to String an agentAddress
     * 
     * @param agentAddress to be converted
     * @return agentAdress converted to String
     */
    static String toString(AgentAddress agentAddress) {
    	StringBuffer identifier = new StringBuffer();
		identifier.append("( agent-identifier"); //$NON-NLS-1$
		identifier.append(" "); //$NON-NLS-1$
		identifier.append(Locale.getString(StringACLCodec.class, "NAME")); //$NON-NLS-1$
		identifier.append(" "); //$NON-NLS-1$
		identifier.append(agentAddress.getUUID());
		identifier.append(" )"); //$NON-NLS-1$
		return identifier.toString();
    }
}
