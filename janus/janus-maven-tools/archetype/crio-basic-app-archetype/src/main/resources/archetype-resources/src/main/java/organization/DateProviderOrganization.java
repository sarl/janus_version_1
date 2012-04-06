#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* 
 * ${symbol_dollar}Id${symbol_dollar}
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package ${package}.organization;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * An organization that defines how to request the current date. The
 * organization can be reused in any project that might need to ask for the
 * current date.
 * 
 * @author ${symbol_dollar}Author: srodriguez${symbol_dollar}
 * @version ${symbol_dollar}Name${symbol_dollar} ${symbol_dollar}Revision${symbol_dollar} ${symbol_dollar}Date${symbol_dollar}
 * @mavengroupid ${symbol_dollar}GroupId${symbol_dollar}
 * @mavenartifactid ${symbol_dollar}ArtifactId${symbol_dollar}
 */
public class DateProviderOrganization extends Organization {

	/**
	 * @param crioContext
	 */
	public DateProviderOrganization(CRIOContext crioContext) {
		super(crioContext);
		//we add the roles that participate in this organization
		//this way we define the organization's structure
		addRole(RequesterRole.class);
		addRole(DateProviderRole.class);
	}

}
