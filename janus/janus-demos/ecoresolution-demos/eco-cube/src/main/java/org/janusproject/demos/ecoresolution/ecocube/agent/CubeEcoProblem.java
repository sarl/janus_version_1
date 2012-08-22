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
package org.janusproject.demos.ecoresolution.ecocube.agent;

import org.janusproject.demos.ecoresolution.ecocube.relation.DownwardRelation;
import org.janusproject.demos.ecoresolution.ecocube.relation.UpwardRelation;
import org.janusproject.ecoresolution.agent.AgentBasedEcoProblem;
import org.janusproject.ecoresolution.agent.EcoAgent;

/** General utilities to control eco-resolution problem solving.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class CubeEcoProblem extends AgentBasedEcoProblem {

	/**
	 */
	public CubeEcoProblem() {
		//
	}
	
	/** Add initial eco-resolution relationship.
	 * 
	 * @param upAgent is the up agent.
	 * @param downAgent is the down agent.
	 */
	public void addUpDownRelation(AbstractCubeProblemAgent upAgent, EcoAgent downAgent) {
		init(upAgent, null, new DownwardRelation(upAgent.getEcoIdentity(), downAgent.getEcoIdentity(), upAgent.getTableIdentity()));
		init(downAgent, null, new UpwardRelation(downAgent.getEcoIdentity(), upAgent.getEcoIdentity(), upAgent.getTableIdentity()));
	}
		
}