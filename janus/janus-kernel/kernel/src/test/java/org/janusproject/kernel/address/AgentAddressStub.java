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
package org.janusproject.kernel.address;

import java.util.UUID;

/**
 * This is the address of an agent in the kernel community.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class AgentAddressStub extends AgentAddress {

	private static final long serialVersionUID = -4997759046185344825L;

	/** Create a agent address.
	 * 
	 * @param id is the identifier of the agent.
	 * @param name is the name of the address/agent.
	 */
	public AgentAddressStub(UUID id, String name) {
		super(id, name);
	}

	/** Create a agent address.
	 */
	public AgentAddressStub() {
		super(null, null);
	}

}