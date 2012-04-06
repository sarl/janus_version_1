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
package org.janusproject.demos.ecoresolution.econpuzzle.relation;

import org.janusproject.ecoresolution.identity.AnyIdentity;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.AbstractEcoRelation;
import org.janusproject.ecoresolution.relation.EcoRelation;

/**
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PlaceGoal extends AbstractEcoRelation<PlaceGoal,TileGoal> {

	private static final long serialVersionUID = -5483328341748999178L;

	/**
	 * @param place
	 * @param tile
	 */
	public PlaceGoal(EcoIdentity place, EcoIdentity tile) {
		super(place, tile);
	}

	@Override
	public boolean isConflict(EcoRelation relation) {
		return NPuzzleRelationUtil.isConflict(this,relation);
	}

	@Override
	public EcoRelation toPattern(EcoIdentity participantToRemove) {
		assert(participantToRemove!=null);
		EcoIdentity m = getMaster();
		EcoIdentity s = getSlave();
		if (m.equals(participantToRemove)) {
			return new PlaceGoal(AnyIdentity.SINGLETON, s);
		}
		if (s.equals(participantToRemove)) {
			return new PlaceGoal(m, AnyIdentity.SINGLETON);
		}
		return this;
	}

	@Override
	public int hashCode() {
		return NPuzzleRelationUtil.hashCode(this);
	}

	@Override
	public boolean equals(EcoRelation relation) {
		return NPuzzleRelationUtil.isTileGoalEqual(getSlave(),getMaster(), relation);
	}

	@Override
	public TileGoal invert() {
		return new TileGoal(getSlave(), getMaster());
	}

}
