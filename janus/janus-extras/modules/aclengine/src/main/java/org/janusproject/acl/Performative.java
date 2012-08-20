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

/**
 * This enumeration describes all available performatives as defined by FIPA
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00037/SC00037J.html">FIPA Communicative Act Library Specification</a>
 * 
 * @author $Author: madeline$
 * @author $Author: kleroy$
 * @author $Author: ptalagrand$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum Performative {
	/**
	 */
	NONE, 
	/**
	 */
	ACCEPT_PROPOSAL,  
	/**
	 */
	AGREE,  
	/**
	 */
	CANCEL,  
	/**
	 */
	CFP,  
	/**
	 */
	CONFIRM,  
	/**
	 */
	DISCONFIRM,  
	/**
	 */
	FAILURE,  
	/**
	 */
	INFORM,  
	/**
	 */
	INFORM_IF,  
	/**
	 */
	INFORM_REF,  
	/**
	 */
	NOT_UNDERSTOOD,  
	/**
	 */
	PROPOSE,  
	/**
	 */
	QUERY_IF,  
	/**
	 */
	QUERY_REF,  
	/**
	 */
	REFUSE,  
	/**
	 */
	REJECT_PROPOSAL,  
	/**
	 */
	REQUEST,  
	/**
	 */
	REQUEST_WHEN,  
	/**
	 */
	REQUEST_WHENEVER,  
	/**
	 */
	SUBSCRIBE,  
	/**
	 */
	PROXY,  
	/**
	 */
	PROPAGATE
}
