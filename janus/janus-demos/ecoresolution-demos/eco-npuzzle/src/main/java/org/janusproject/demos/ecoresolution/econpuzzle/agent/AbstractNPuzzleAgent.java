/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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
package org.janusproject.demos.ecoresolution.econpuzzle.agent;

import org.janusproject.ecoresolution.agent.EcoAgent;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;

/**
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractNPuzzleAgent extends EcoAgent {
		
	private static final long serialVersionUID = -5574441964147076745L;

	/**
	 */
	public AbstractNPuzzleAgent() {
		super();
	}

	/**
	 * @param commitSuicide
	 */
	public AbstractNPuzzleAgent(Boolean commitSuicide) {
		super(commitSuicide);
	}
	
	@Override
	public String toString() {
		return getAddress().getName();
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
