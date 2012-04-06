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
package org.janusproject.kernel.organization.holonic;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * This class describes one the major organization in an compound agent (super-agent).
 * It precises the status of each members and provides communication means between the super-agent and his heads.
 * 
 * 4 roles are defined on this orgnaization :
 * <ul>
 * <li> <code>Super</code> : this role is reserved to the super-agent
 * <li> <code>Head</code> : representative of Part members, he 's the interface of the super-agent. he take the decision and assure the tasks distribution.
 * <li> <code>Part</code> : A classical member without particular authority inside the super-agent.
 * <li> <code>MultiPart</code> : A classical member simultaneously shared between various super-agent. This role is not automatically authorized. Certain agent doesn't accept this particular status 
 * </ul>
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see Super
 * @see Head
 * @see Part
 * @see MultiPart
 */
public class HolonicOrganization extends Organization {

	/**
	 * Create a holonic organization.
	 * @param context is the CRIO execution context.
	 */
	public HolonicOrganization(CRIOContext context) {
		super(context);
		addRole(Super.class);
		addRole(Head.class);
		addRole(Part.class);
		addRole(MultiPart.class);
	}

}
