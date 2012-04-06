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
package org.janusproject.kernel.organization.integration;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * This organization manage the recruitment of new members for an existing compound agent (super-agent)
 * Two roles are defnied on it :
 * <ul>
 * <li> <code>MHead</code> : The recruter, the head of the holonic organization of the super-agent
 * <li> <code>StandAlone</code> : the canditate to the integration of the super-agent
 * </ul>
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class IntegrationOrganization extends Organization {

	/**
	 * Create an integration organization.
	 * 
	 * @param context is the CRIO execution context.
	 */
	public IntegrationOrganization(CRIOContext context) {
		super(context);
		addRole(MHead.class);
		addRole(StandAlone.class);
	}

}