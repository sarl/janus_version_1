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

import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.organization.holonic.message.RequestIntegrationMessage;
import org.janusproject.kernel.organization.holonic.message.ResultIntegrationMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Representative Role that permits to have an entry point for organization
 * integration.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MHead extends Role {

	private Message m;

	private int current = 1; // the current state

	private GroupAddress holonicGroup;

	/**
	 */
	public MHead() {
		super();
	}
	
	/**
	 * Initialize this role with :
	 * <ul>
	 * <li> The address of the holonic group which the access is managed by the merging orgnaization where this role is defined
	 * </ul>
	 * <p>
	 * This function will be automatically invoked by {@link Role} class.
	 * 
	 * @param adr is the gorup address.
	 * @throws IllegalArgumentException
	 */
	public void init(GroupAddress adr) {
		this.holonicGroup = adr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		this.current = Run();
		return StatusFactory.ok(this);
	}

	private int Run() {
		switch (this.current) {
		case 1:
			this.m = getMailbox().removeFirst();
			if ((this.m != null)
					&& (this.m instanceof RequestIntegrationMessage)) {
				return 2;
			}
			return 1;

			//Automatically accepted
		case 2:
			debug("MHead got a integration request"); //$NON-NLS-1$
			RoleAddress adr = (RoleAddress)this.m.getSender();
			sendMessage(StandAlone.class, adr.getPlayer(),
						new ResultIntegrationMessage(this.holonicGroup));
			return 0;
		default:
			return 0;
		}

	}

}
