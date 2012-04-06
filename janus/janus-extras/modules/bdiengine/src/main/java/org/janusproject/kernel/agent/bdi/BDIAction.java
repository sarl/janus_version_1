/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011-2012 Janus Core Developers
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
package org.janusproject.kernel.agent.bdi;

import java.util.List;

/**
 * Action executed by a BDI Agent.
 * 
 * @author $Author: matthias.brigaud@gmail.com$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface BDIAction {
	/**
	 * Test if the action is executable, according to the agent's beliefs.
	 * @param beliefs : agent's beliefs
	 * @return true if the action is executable, false otherwise
	 */
	public boolean isExecutable(List<BDIBelief> beliefs);
	
	/**
	 * Execute the action.
	 * If the needed parameters are not correct, the execution failed.
	 * @param parameters : parameters necessary for the action
	 * @return a status (execution failed or successful)
	 */
	public BDIActionStatus execute(Object[] parameters);
}
