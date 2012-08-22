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
package org.janusproject.ecoresolution.event;

import org.janusproject.ecoresolution.entity.EcoEntity;
import org.janusproject.ecoresolution.relation.EcoRelation;


/** Event describing a change of goal.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GoalChangeEvent extends AbstractEcoEntityEvent {
	
	private static final long serialVersionUID = -1809527317433699783L;
	
	private final EcoRelation oldGoal;
	private final EcoRelation newGoal;
	
	/**
	 * @param entity is the entity that has changed its goal.
	 * @param oldGoal is the old goal of the entity.
	 * @param newGoal is the new goal of the entity.
	 */
	public GoalChangeEvent(EcoEntity entity, EcoRelation oldGoal, EcoRelation newGoal) {
		super(entity);
		this.oldGoal = oldGoal;
		this.newGoal = newGoal;
	}
	
	/** Replies the previous goal of the entity.
	 * 
	 * @return the previous goal.
	 */
	public EcoRelation getPreviousGoal() {
		return this.oldGoal;
	}
	
	/** Replies the current goal of the entity.
	 * 
	 * @return the current goal.
	 */
	public EcoRelation getCurrentGoal() {
		return this.newGoal;
	}

}