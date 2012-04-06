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
package org.janusproject.demos.ecoresolution.econpuzzle.attack;

import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;

/**
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ManhattanDistanceAttack extends EcoAttack {

	private static final long serialVersionUID = -6915527630027418430L;
	
	/**
	 * Distance from the place that have initially launched this attack
	 * This integer corresponds to the Manhattan Distance to this place
	 */
	private Integer distanceFromAttackOrigin;
	
	/**
	 * The identity of the place that have initially launched this attack
	 */
	private EcoIdentity attackOrigin;
	
	/**
	 * @param assailant
	 * @param defender
	 * @param parameters
	 */
	public ManhattanDistanceAttack(EcoIdentity assailant, EcoIdentity defender, Object[] parameters) {
		super(assailant, defender, parameters);		
		
		assert (parameters.length > 1 && parameters[0] instanceof Integer  && parameters[1] instanceof EcoIdentity);
		
		this.distanceFromAttackOrigin = (Integer)parameters[0];
		this.attackOrigin = (EcoIdentity)parameters[1];
	}

	/**
	 * @return the manhattan distance.
	 */
	public int getManhattanDistance() {
		return this.distanceFromAttackOrigin.intValue();
	}

	/**
	 * @return the origin of the attack.
	 */
	public EcoIdentity getAttackOrigin() {
		return this.attackOrigin;
	}
	
	

}
