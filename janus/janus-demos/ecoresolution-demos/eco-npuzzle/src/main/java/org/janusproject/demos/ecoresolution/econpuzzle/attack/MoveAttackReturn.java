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

import java.util.Collection;

import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;


/**
 * Represents the answer  to a MoveAttack specifying the new acquaintances of the Tile that has moved.
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class MoveAttackReturn extends EcoAttack {

	private static final long serialVersionUID = -2678271814179990021L;

	/**
	 * 
	 * @param assailant is the source of the attack.
	 * @param defender is the target of the attack.
	 * @param eConstraints - the new acquaintances of the Defender that has moved and initially sent the MoveAttack.
	 * @param parameters are additional parameters to pass to defender.
	 */
	public MoveAttackReturn(EcoIdentity assailant, EcoIdentity defender, Collection<EcoRelation> eConstraints, Object... parameters) {
		super(assailant, defender, eConstraints, parameters);
		assert(eConstraints!=null && eConstraints.size()>=5);// 5 constraints: up down left right hosting		
	}

}
