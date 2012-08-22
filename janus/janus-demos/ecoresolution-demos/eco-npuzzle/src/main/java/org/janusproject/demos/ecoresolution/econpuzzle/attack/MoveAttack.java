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
 * Represents movement of a Tile to this new position
 * Each time an EcoAgent receives a MoveAttack, it answers with a MoveAttackReturn
 * This attack constains as constraint the set of the new acquaintances for the Tile that receives it
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class MoveAttack extends EcoAttack {

	private static final long serialVersionUID = -3928722841773320903L;

	/**
	 * 
	 * @param assailant is the source of the attack.
	 * @param defender is the target of the attack.
	 * @param eConstraints - the set of the new acquaintances for the Defender.
	 * @param parameters are additional parameters to pass to defender.
	 */	
	public MoveAttack(EcoIdentity assailant, EcoIdentity defender, Collection<EcoRelation> eConstraints, Object... parameters) {
		super(assailant, defender, eConstraints, parameters);

		assert(eConstraints!=null && eConstraints.size()>=5);// 0 or 5 constraints: up down left right hosting
		
	}	

	
	
}
