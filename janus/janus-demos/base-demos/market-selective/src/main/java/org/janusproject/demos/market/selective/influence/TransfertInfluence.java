/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.market.selective.influence;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.crio.core.GroupAddress;

/**
 * Influence used to wake up another role.
 * It is embedding the targeted group address.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TransfertInfluence extends Signal {
	
	private static final long serialVersionUID = 4076440033831374510L;
	
	/**
	 * @param source
	 * @param provider
	 * @param iga
	 */
	public TransfertInfluence(Object source, AgentAddress provider, GroupAddress iga) {
		super(source, TransfertInfluence.class.getName(), provider, iga);
	}
	
}
