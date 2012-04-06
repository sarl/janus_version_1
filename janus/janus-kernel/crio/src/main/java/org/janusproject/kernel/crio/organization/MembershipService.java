/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.crio.organization;

import java.io.Serializable;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.credential.Credentials;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;

/**
 * The membership service verifies if an agent can take a given role inside a
 * group.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface MembershipService extends Serializable {

	/**
	 * Invoked to validate if an agent is able to take a role.
	 * 
	 * @param agent indicates the agents allowed to get a role.
	 * @param role is the role allowed to be get by the agent.
	 * @param agentCredentials is the credentials associated to the agent.
	 * @return the status of the validation.
	 */
	public Status validateRoleTaker(
			AgentAddress agent,
			Class<? extends Role> role,
			Credentials agentCredentials);

}
