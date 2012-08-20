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
package org.janusproject.ecoresolution.entity;

import org.janusproject.ecoresolution.problem.EcoProblem;
import org.janusproject.ecoresolution.relation.EcoRelation;

/** Describes an entity in eco-resolution problem solving with may 
 * be initialized by the {@link EcoProblem eco-problem instance}.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface InitializableEcoEntity extends EcoEntity {

	/** Set the goal of this eco-entity.
	 * A goal is a relationship to obtain against an
	 * other eco-entity.
	 * The master of the given relation <strong>MUST BE</strong>
	 * this eco-entity.
	 * 
	 * @param goal is the goal of this eco-entity.
	 */
	public void setGoal(EcoRelation goal);

	/** Add an acquaintance. This acquaintance change will be apply on the
	 * eco-entity knowledge later.
	 * 
	 * @param relation is the relation to add in the acquaintances of the entities.
	 */
	public void addAcquaintance(EcoRelation relation);

}