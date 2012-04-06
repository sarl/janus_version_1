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

import java.util.Collection;

import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.message.AbstractContentMessage;

/** Message invoked when an agent wan to integrate a group.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RequestIntegrationMessage
extends AbstractContentMessage<Collection<Class<? extends Capacity>>> {

	private static final long serialVersionUID = 5300503890144874899L;

	private final Collection<Class<? extends Capacity>> capacities;

	/**
	 * @param icapacities is the list of capacities exibited by the sending agent.
	 */
	public RequestIntegrationMessage(Collection<Class<? extends Capacity>> icapacities) {
		this.capacities = icapacities;
	}

	/**
	 * @return the list of capacities exibited by the sending agent.
	 */
	public Collection<Class<? extends Capacity>> getCapacities() {
		return this.capacities;
	}

	@Override
	public Collection<Class<? extends Capacity>> getContent() {
		return getCapacities();
	}

}
