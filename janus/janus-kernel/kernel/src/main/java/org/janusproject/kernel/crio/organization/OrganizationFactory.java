/* 
 * $Id$
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
package org.janusproject.kernel.crio.organization;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * This interface is used by <code>OrganizationRepository</code>
 * to create an instance of an organization.
 * 
 * @param <O> is the type of the organization to instance.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface OrganizationFactory<O extends Organization> {

	/** Invoked to obtain a new instance of the the organization.
	 * 
	 * @param context is the CRIO context to use.
	 * @return the new organization instance.
	 * @throws Exception if something wrong append
	 */
	public O newInstance(CRIOContext context) throws Exception;

	/** Replies the type of the organization instanced by this factory.
	 * 
	 * @return the new organization type.
	 */
	public Class<O> getOrganizationType();

}
