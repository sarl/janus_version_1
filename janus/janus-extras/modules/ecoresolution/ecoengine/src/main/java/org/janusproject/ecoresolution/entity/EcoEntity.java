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

import java.util.Set;

import org.janusproject.ecoresolution.event.EcoEntityListener;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;

/** Describes an entity in eco-resolution problem solving.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface EcoEntity {
	
	/** Replies the identity of this eco-entity.
	 * 
	 * @return the identity of this eco-entity.
	 */
	public EcoIdentity getIdentity();
	
	/** Replies a string-representation of this entity.
	 * 
	 * @return a string-representation of this entity.
	 */
	@Override
	public String toString();
	
	/** Replies if the eco-entity has been integrated inside a eco-problem and is
	 * ready to solve the problem.
	 * 
	 * @return <code>true</code> if the entity is inside the collection of
	 * eco-entities to solve a problem and ready to proceed, otherwise <code>false</code>.
	 */
	public boolean isProblemSolvingParticipantReady();

	/** Replies if the eco-entity has been notified to solve the problem.
	 * 
	 * @return <code>true</code> if the problem is under solving, otherwise <code>false</code>.
	 */
	public boolean isProblemSolvingStarted();

	/** Invoked when the eco-entity should proceed several actions
	 * when it is just been initialized.
	 */
	public void doInitializationCommitment();

	/** Invoked by the state machine to update the goal for the eco-agent.
	 * This function permits to eco-agent to change its goal.
	 * 
	 * @return <code>true</code> if something has changed in the eco-entity,
	 * <code>false</code> if nothing change in the eco-entity.
	 */
	public boolean updateGoal();

	/** Invoked by the state machine to update the knownledge of the eco-agent.
	 * This function permits to eco-agent to check its environment and
	 * detect changes.
	 * 
	 * @return <code>true</code> if something has changed in the eco-entity
	 * knownledge, <code>false</code> if nothing change in the eco-entity knownledge.
	 */
	public boolean updateKnowledge();
	
	/** Replies the goal of this eco-entity.
	 * A goal is a relationship to obtain against an
	 * other eco-entity.
	 * 
	 * @return the goal of this eco-entity.
	 */
	public EcoRelation getGoal();

	/** Replies the acquaintances of this eco-entity.
	 * An acquaintance in eco-resolution problem solving
	 * is described through a relationship between this
	 * eco-entity (as master) and another eco-entity (as slave). 
	 * 
	 * @return the acquaintances of this eco-entity.
	 */
	public Set<EcoRelation> getAcquaintances();

	/** Replies the dependencies of this eco-entity.
	 * Dependencies are the eco-entities which depend on
	 * the current eco-entity on there goals. 
	 * 
	 * @return the dependencies of this eco-entity.
	 */
	public Set<EcoIdentity> getDependencies();

	/** Replies the attacks received by this eco-entity.
	 * 
	 * @return the attacks received by this eco-entity.
	 */
	public Set<EcoAttack> getAttacks();
	
	/** Select and replies an intruder eco-entity which is
	 * currently avoiding to satisfy the current eco-entity.
	 * 
	 * @return the attack to send to the intruder or <code>null</code> if no intruder.
	 */
	public EcoAttack selectSatisfactionIntruder();

	/** Select and replies an intruder eco-entity which is
	 * currently avoiding to escape.
	 * 
	 * @param attacks are the current attacks on this eco-entity, which are causing to detect
	 * an escaping intruder. 
	 * @return the attack to send to the intruder or <code>null</code> if no intruder.
	 */
	public Set<EcoAttack> selectEscapingIntruder(Set<EcoAttack> attacks);

	/** Invoked to send and attack to an other eco-entity.
	 * 
	 * @param attack describes the attack.
	 */
	public void doAttack(EcoAttack attack);
	
	/** Invoked to move to increase the satisfaction.
	 */
	public void doSatisfactionIncreasing();

	/** Invoked when satisfied.
	 */
	public void doSatisfied();

	/** Invoked to escape.
	 */
	public void doEscaping();
	
	/** Add listener on eco-entity events.
	 * 
	 * @param listener
	 */
	public void addEcoEntityListener(EcoEntityListener listener);

	/** Removelistener on eco-entity events.
	 * 
	 * @param listener
	 */
	public void removeEcoEntityListener(EcoEntityListener listener);

}