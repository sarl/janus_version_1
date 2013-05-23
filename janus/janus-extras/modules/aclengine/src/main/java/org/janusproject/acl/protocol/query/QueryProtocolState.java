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
package org.janusproject.acl.protocol.query;

import org.janusproject.acl.protocol.ProtocolState;

/**
 * This enumeration describes all available states of the Query Protocol.
 * 
 * @see FipaQueryProtocol
 * @see ProtocolState
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public enum QueryProtocolState implements ProtocolState {
	/**
	 */
	NOT_STARTED,
	/**
	 */
	SENDING_QUERY,
	/**
	 */
	WAITING_QUERY,
	/**
	 */
	SENDING_ANSWER,
	/**
	 */
	WAITING_ANSWER,
	/**
	 */
	SENDING_RESULT,
	/**
	 */
	WAITING_RESULT,
	/**
	 */
	REFUSED,
	/**
	 */
	CANCELING,
	/**
	 */
	CANCELED,
	/**
	 */
	DONE;
}
