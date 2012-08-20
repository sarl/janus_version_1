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
package org.janusproject.acl.protocol;

import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.request.FipaRequestProtocol;
import org.janusproject.kernel.address.AgentAddress;

/**
 * This class is used to simplify ACL Messages when working with protocols.
 * <p>
 * For example, if you're calling getRequest() from {@link FipaRequestProtocol} 
 * you won't get back the whole corresponding ACL Message but only a summarize of it : 
 * the performative and the content.
 * </p>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class ProtocolResult {
	
	private Performative performative;
	private Object content;
	private AgentAddress author;
	
	/**
	 * 
	 */
	public ProtocolResult() {}
	
	/**
	 * Creates a new ProtocoleResult.
	 * @param author 
	 * 
	 * @param performative
	 * @param content
	 */
	public ProtocolResult(AgentAddress author, Performative performative, Object content) {
		this.author = author;
		this.performative = performative;
		this.content = content;
	}
	
	/**
	 * Gets the performative.
	 * @return the performative associated to an ACL message
	 */
	public Performative getPerformative() {
		return this.performative;
	}
	
	/**
	 * Sets the performative.
	 * 
	 * @param performative
	 */
	public void setPerformative(Performative performative) {
		this.performative = performative;
	}
	
	/**
	 * Gets the content.
	 * @return the content of an ACL message
	 */
	public Object getContent() {
		return this.content;
	}
	
	/**
	 * Sets the content.
	 * 
	 * @param content
	 */
	public void setContent(Object content) {
		this.content = content;
	}

	/**
	 * @return the author
	 */
	public AgentAddress getAuthor() {
		return this.author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(AgentAddress author) {
		this.author = author;
	}
}
