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

import org.janusproject.demos.ecoresolution.econpuzzle.relation.Blank;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Down;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Hosted;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Hosting;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Left;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.PlaceGoal;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Right;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.TileGoal;
import org.janusproject.demos.ecoresolution.econpuzzle.relation.Up;
import org.janusproject.ecoresolution.agent.AgentBasedEcoProblem;
import org.janusproject.ecoresolution.agent.EcoAgent;
import org.janusproject.ecoresolution.identity.NoIdentity;

/**
 * General utilities to control N-Puzzle problem eco-solving.
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class NPuzzleProblem extends AgentBasedEcoProblem {

	/**
	 */
	public NPuzzleProblem() {
		super();
	}

	/**
	 * @param right
	 */
	public void addRightNothingRelation(EcoAgent right) {
		init(right, null, new Right(right.getEcoIdentity(),NoIdentity.SINGLETON));
	}

	/**
	 * @param left
	 */
	public void addLeftNothingRelation(EcoAgent left) {
		init(left, null, new Left(left.getEcoIdentity(),NoIdentity.SINGLETON));
	}

	/**
	 * @param up
	 */
	public void addUpNothingRelation(EcoAgent up) {
		init(up, null, new Up(up.getEcoIdentity(),NoIdentity.SINGLETON));
	}

	/**
	 * @param down
	 */
	public void addDownNothingRelation(EcoAgent down) {
		init(down, null, new Down(down.getEcoIdentity(),NoIdentity.SINGLETON));
	}

	/**
	 * @param left
	 * @param right
	 */
	public void addLeftRightRelation(EcoAgent left, EcoAgent right) {
		init(left, null, new Left(left.getEcoIdentity(),right.getEcoIdentity()));
		init(right, null, new Right(right.getEcoIdentity(),left.getEcoIdentity()));
	}

	/**
	 * @param up
	 * @param down
	 */
	public void addUpDownRelation(EcoAgent up, EcoAgent down) {
		init(up, null, new Up(up.getEcoIdentity(),down.getEcoIdentity()));
		init(down, null, new Down(down.getEcoIdentity(),up.getEcoIdentity()));
	}

	/**
	 * @param place
	 * @param tile
	 */
	public void addHostedHostingRelation(EcoPlace place, EcoTile tile) {
		init(place, null, new Hosting(place.getEcoIdentity(),tile.getEcoIdentity()));
		init(tile, null, new Hosted(tile.getEcoIdentity(),place.getEcoIdentity()));
	}

	/**
	 * @param place
	 * @param tile
	 */
	public void addGoalRelation(EcoPlace place, EcoTile tile) {
		init(place, new PlaceGoal(place.getEcoIdentity(),tile.getEcoIdentity()));
		init(tile, new TileGoal(tile.getEcoIdentity(),place.getEcoIdentity()));
	}

	/**
	 * @param blank
	 * @param otherTile
	 */
	public void addBlankRelation(EcoBlank blank, EcoTile otherTile) {
		init(blank, null, new Blank(blank.getEcoIdentity(),otherTile.getEcoIdentity()));
		//init(otherTile, null, new Blank(blank.getEcoIdentity(),otherTile.getEcoIdentity()));
	}
	
	
}
