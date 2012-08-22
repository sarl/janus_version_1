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
import org.janusproject.ecoresolution.agent.EcoAgent;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;

/**
 * A plane eco-agent.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractCubeProblemAgent extends EcoAgent {
	
	private static final long serialVersionUID = 181738597008532553L;

	/**
	 */
	public AbstractCubeProblemAgent() {
		//
	}
	
	/** Replies the identity of the table.
	 * 
	 * @return the identity of the table.
	 */
	protected abstract EcoIdentity getTableIdentity();

	/** Create an instance of downward relation for this eco-agent.
	 * 
	 * @param underEntity is the eco-entity under this eco-agent.
	 * @return the relation.
	 */
	protected DownwardRelation downwardRelation(EcoIdentity underEntity) {
		return new DownwardRelation(getEcoIdentity(), underEntity, getTableIdentity());
	}

	/** Create an instance of upward relation for this eco-agent.
	 * 
	 * @param upperEntity is the eco-entity on the top of this eco-agent.
	 * @return the relation.
	 */
	protected UpwardRelation upwardRelation(EcoIdentity upperEntity) {
		return new UpwardRelation(getEcoIdentity(), upperEntity, getTableIdentity());
	}
	
	/** Create an instance of upward relation for this eco-agent.
	 * 
	 * @param lowerEntity is the eco-entity on the bottom.
	 * @param upperEntity is the eco-entity on the top.
	 * @return the relation.
	 */
	protected UpwardRelation upwardRelation(EcoIdentity lowerEntity, EcoIdentity upperEntity) {
		return new UpwardRelation(lowerEntity, upperEntity, getTableIdentity());
	}

	/** Create an instance of downward relation for this eco-agent.
	 * 
	 * @param upperEntity is the eco-entity on the top.
	 * @param lowerEntity is the eco-entity on the bottom.
	 * @return the relation.
	 */
	protected DownwardRelation downwardRelation(EcoIdentity upperEntity, EcoIdentity lowerEntity) {
		return new DownwardRelation(upperEntity, lowerEntity, getTableIdentity());
	}

	/** Create an instance of attack relation for this eco-agent.
	 * 
	 * @param defender is the target of the attack.
	 * @param constraint is the constraint to be followed by the defender.
	 * @param parameters are additional parameters to pass to defender.
	 * @return the attack.
	 */
	protected EcoAttack attack(EcoIdentity defender, EcoRelation constraint, Object... parameters) {
		return new EcoAttack(getEcoIdentity(), defender, constraint, parameters);
	}

	/** Create an instance of attack relation for this eco-agent.
	 * 
	 * @param defender is the target of the attack.
	 * @param parameters are additional parameters to pass to defender.
	 * @return the attack.
	 */
	protected EcoAttack attack(EcoIdentity defender, Object... parameters) {
		return new EcoAttack(getEcoIdentity(), defender, parameters);
	}
	
}
