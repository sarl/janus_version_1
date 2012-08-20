/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.ecoresolution.problem;

import org.janusproject.ecoresolution.entity.InitializableEcoEntity;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoRelation;

/** This class describes an eco-problem.
 * An eco-problem (see also eco-resolution problem solving) is solved
 * by a set of eco-entities. The eco-problem instance is
 * starting the solving when all the eco-entities have been initialized.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class EcoProblem {

	/** Initialize the given eco-entity.
	 * <p>
	 * If goal is not <code>null</code>, the goal's master entity must be the given <var>entity</var>.
	 * For each acquaintance, the relation master must be the given <var>entity</var>.
	 * <p>
	 * The given master agent is registered as an agent to launch.
	 * 
	 * @param entity is the agent which is participating to the given relation.
	 * @param goal is the goald of the entity.
	 * @param aquaintances is the knowledge to insert at startup.
	 */
	protected final static void init(InitializableEcoEntity entity, EcoRelation goal, EcoRelation... aquaintances) {
		EcoIdentity m;
		if (goal!=null) {
			m = goal.getMaster();
			assert(entity.getIdentity().equals(m));
			entity.setGoal(goal);
		}
		if (aquaintances!=null) {
			for(EcoRelation r : aquaintances) {
				m = r.getMaster();
				assert(entity.getIdentity().equals(m));
				entity.addAcquaintance(r);
			}
		}
	}
	
	/** Launch eco-resolution problem solving.
	 * 
	 * @param monitor is the monitor that checks if the problem is solved.
	 */
	public abstract void solve(EcoProblemMonitor monitor);
		
}