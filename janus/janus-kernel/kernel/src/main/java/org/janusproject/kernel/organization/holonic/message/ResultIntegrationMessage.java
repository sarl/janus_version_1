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
package org.janusproject.kernel.organization.holonic.message;

import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.message.AbstractContentMessage;

/** Message send to notify on the result of an integration.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ResultIntegrationMessage
extends AbstractContentMessage<Boolean> {

	private static final long serialVersionUID = -4540216615885221289L;

	private final boolean isAccepted;

	private final GroupAddress holonicGA;

	/**
	 * @param holonicGroup is the group address to integrate.
	 */
	public ResultIntegrationMessage(GroupAddress holonicGroup) {
		if (holonicGroup != null) {
			this.isAccepted = true;
			this.holonicGA = holonicGroup;
		} else {
			this.isAccepted = false;
			this.holonicGA = null;
		}
	}

	/**
	 * @return the group address to integrate.
	 */
	public GroupAddress getHolonicGroupAddress() {
		return this.holonicGA;
	}

	/**
	 * 
	 * @return <code>true</code> if the integration of the agent was accepted,
	 * otherwise <code>false</code>
	 */
	public boolean isAccepted() {
		return this.isAccepted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getContent() {
		return Boolean.valueOf(isAccepted());
	}

}
