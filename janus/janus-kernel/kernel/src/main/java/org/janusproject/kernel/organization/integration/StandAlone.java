/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.organization.holonic.Part;
import org.janusproject.kernel.organization.holonic.message.RequestIntegrationMessage;
import org.janusproject.kernel.organization.holonic.message.ResultIntegrationMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/**
 * Role taken by a agent outside an organization which it whant to
 * integrate.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class StandAlone extends Role {

	private Message m;

	private GroupAddress holonicGroup;

	private int current = 1; // the current state

	/**
	 */
	public StandAlone() {
		super();
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
			sendMessage(MHead.class, new RequestIntegrationMessage(
						getPlayerCapacities()));
			return 2;

		case 2:
			this.m = getMailbox().removeFirst();
			if ((this.m != null)
					&& (this.m instanceof ResultIntegrationMessage)
					&& ((ResultIntegrationMessage) this.m).isAccepted()) {
				return 3;
			}
			if ((this.m != null)
					&& (this.m instanceof ResultIntegrationMessage)
					&& (!((ResultIntegrationMessage) this.m).isAccepted())) {
				return 4;
			}
			return 1;

		case 3:
			debug("StandAlone got a integration request"); //$NON-NLS-1$
			this.holonicGroup = ((ResultIntegrationMessage) this.m)
					.getHolonicGroupAddress(); // Integration Accepted	
			if (requestRole(Part.class, this.holonicGroup)!=null) {
				debug("Integration accepted"); //$NON-NLS-1$
				leaveRole(StandAlone.class);
				return 0;
			}
			return 4;

		case 4:
			debug("Integration not accepted"); //$NON-NLS-1$
			return 0;
		default:
			return 0;
		}

	}

}
