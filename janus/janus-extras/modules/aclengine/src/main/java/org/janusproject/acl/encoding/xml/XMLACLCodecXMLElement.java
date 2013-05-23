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

/**
 * This enumeration describes all available message param (name of the xml tags) as defined by FIPA for XML encoding, 
 * and their setter (used for decoding process - java reflection tips)
 * 
 * @see <a href="http://www.fipa.org/specs/fipa00071/SC00071E.html">FIPA ACL Message Representation in XML Specification</a> 
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum XMLACLCodecXMLElement {
	/**
	 */
	RECEIVER("receiver"), //$NON-NLS-1$
	/**
	 */
	SENDER("sender"), //$NON-NLS-1$
	/**
	 */
	CONTENT("content"), //$NON-NLS-1$
	/**
	 */
	LANGUAGE("language"), //$NON-NLS-1$
	/**
	 */
	ENCODING("encoding"), //$NON-NLS-1$
	/**
	 */
	ONTOLOGY("ontology"), //$NON-NLS-1$
	/**
	 */
	PROTOCOL("protocol"), //$NON-NLS-1$
	/**
	 */
	REPLY_WITH("reply-with"), //$NON-NLS-1$
	/**
	 */
	IN_REPLY_TO("in-reply-to"), //$NON-NLS-1$
	/**
	 */
	REPLY_BY("reply-by"), //$NON-NLS-1$
	/**
	 */
	REPLY_TO("reply-to"), //$NON-NLS-1$
	/**
	 */
	CONVERSATION_ID("conversation-id"), //$NON-NLS-1$
	/**
	 */
	PERFORMATIVE("performative"); //$NON-NLS-1$

	private final String tag;
	
	XMLACLCodecXMLElement(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the name tag of the msg param
	 */
	public String getTag() { 
		return this.tag; 
	} 
	
}
