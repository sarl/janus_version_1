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

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.janusproject.kernel.address.AgentAddress;

/**
 * This interface describes the Envelope of a given ACL Message as defined by FIPA.
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00085/SC00085J.html">FIPA Agent Message Transport Envelope Representation</a>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface ACLMessageEnvelope
{
	/**
	 * Gets the list of recipients of the ACL Message. 
	 * @return the list of recipients of the ACL Message. 
	 */
	public Collection<AgentAddress> getTo();
	
	/**
	 * Sets the list of recipients of the ACL Message. 
	 * @param addresses is a collection of {@link AgentAddress}
	 */
	public void setTo(Collection<AgentAddress> addresses);
	
	/**
	 * Gets the agent address of the sender of the ACL Message. 
	 * @return the agent address of the sender of the ACL Message. 
	 */
	public AgentAddress getFrom();
	
	/**
	 * Sets the sender of the ACL Message. 
	 * @param address is an {@link AgentAddress}
	 */
	public void setFrom(AgentAddress address);
	
	/**
	 * Gets the ACL Representation of the content of the ACL Message. 
	 * @return the ACL Representation of the content of the ACL Message. 
	 */
	public String getAclRepresentation();
	
	/**
	 * Sets the ACL Representation of the content of the ACL Message.
	 * @param aclRepresentation - the ACL Representation of the content of the ACL Message.
	 */
	public void setAclRepresentation(String aclRepresentation);
	
	/**
	 * Gets the date of the ACL Message. 
	 * @return the date of the ACL Message. 
	 */
	public Date getDate();
	
	/**
	 * Sets the date of the ACL Message.
	 * @param date
	 */
	public void setDate(Date date);
	
	/**
	 * Gets the comments of the ACL Message.
	 * @return the comments
	 */
	public String getComments();
	
	/**
	 * Sets the comments of the ACL Message.
	 * @param comments
	 */
	public void setComments(String comments);
	
	/**
	 * Gets the length of the payload of the ACL Message. 
	 * @return the length of the payload of the ACL Message
	 */
	public Long getPayloadLength();
	
	/**
	 * Sets the length of the payload of the ACL Message.
	 * @param payloadLength
	 */
	public void setPayloadLength(Long payloadLength);
	
	/**
	 * Gets the encoding of the payload of the ACL Message. 
	 * @return the encoding of the payload of the ACL Message. 
	 */
	public String getPayloadEncoding();

	/**
	 * Gets the encoding of the payload of the ACL Message.
	 * @param payloadEncoding
	 */
	public void setPayloadEncoding(String payloadEncoding);
	
	/**
	 * Gets the list of intended receivers of the ACL Message. 
	 * @return the list of intended receivers of the ACL Message. 
	 */
	public ArrayList<AgentAddress> getIntendedReceiver();
	
	/**
	 * Sets the list of intended receivers of the ACL Message.
	 * @param intendedReceivers is a list of {@link AgentAddress}
	 */
	public void setIntendedReceiver(ArrayList<AgentAddress> intendedReceivers);
	
	/**
	 * Gets the transport behaviour used to send and receive this ACL Message. 
	 * @return the transport behaviour used to send and receive this ACL Message. 
	 */
	public Properties getTransportBehaviour();
	
	/**
	 * Sets the transport behaviour used to send and receive this ACL Message.
	 * @param transportBehaviour
	 */
	public void setTransportBehaviour(Properties transportBehaviour);
    
    
}
