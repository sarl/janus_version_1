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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.janusproject.ecoresolution.event.AcquaintanceEvent;
import org.janusproject.ecoresolution.event.AttackEvent;
import org.janusproject.ecoresolution.event.DependencyEvent;
import org.janusproject.ecoresolution.event.EcoEntityListener;
import org.janusproject.ecoresolution.event.EventType;
import org.janusproject.ecoresolution.event.GoalChangeEvent;
import org.janusproject.ecoresolution.identity.AgentIdentity;
import org.janusproject.ecoresolution.identity.AnyIdentity;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.identity.EcoIdentityComparator;
import org.janusproject.ecoresolution.identity.NoIdentity;
import org.janusproject.ecoresolution.message.EcoAcquaintanceMessage;
import org.janusproject.ecoresolution.message.EcoAttackMessage;
import org.janusproject.ecoresolution.message.EcoDependencyMessage;
import org.janusproject.ecoresolution.message.EcoInitializationDoneMessage;
import org.janusproject.ecoresolution.message.EcoProblemSolvedMessage;
import org.janusproject.ecoresolution.message.EcoProblemSolverPresentationMessage;
import org.janusproject.ecoresolution.message.EcoProblemSolvingStartMessage;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.message.Message;

/** Default implementation of an eco-entity in eco-resolution problem solving.
 * <p>
 * TODO: Internal HashSet should be replaced by TreeSet (if an complete-order relationship is applicable).
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractEcoEntity implements InitializableEcoEntity {

	private Collection<EcoEntityListener> listeners = null;
	
	private final EcoIdentity identity;
	
	private boolean isSolvingStarted = false;
	private EcoIdentity monitor = null;
	
	private EcoRelation goal = null;
	private final Set<EcoIdentity> dependencies = new TreeSet<EcoIdentity>(EcoIdentityComparator.SINGLETON);
	private Set<EcoAttack> attacks = new HashSet<EcoAttack>();
	private final Set<EcoRelation> acquaintances = new HashSet<EcoRelation>();
	
	private EcoRelation bufferedGoal = null;
	private final Set<EcoRelation> bufferedAddedAcquaintances = new HashSet<EcoRelation>();
	private final Set<EcoRelation> bufferedRemovedAcquaintances = new HashSet<EcoRelation>();
	
	/**
	 * @param identity is the identity of the entity.
	 */
	public AbstractEcoEntity(EcoIdentity identity) {
		assert(identity!=null);
		this.identity = identity;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isProblemSolvingStarted() {
		return this.isSolvingStarted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isProblemSolvingParticipantReady() {
		return this.monitor!=null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void doInitializationCommitment() {
		sendMessage(new EcoInitializationDoneMessage(), this.monitor);
	}

	/** Add listener on eco-entity events.
	 * 
	 * @param listener
	 */
	@Override
	public final synchronized void addEcoEntityListener(EcoEntityListener listener) {
		if (this.listeners==null)
			this.listeners = new ArrayList<EcoEntityListener>();
		this.listeners.add(listener);
	}

	/** Remove listener on eco-entity events.
	 * 
	 * @param listener
	 */
	@Override
	public final synchronized void removeEcoEntityListener(EcoEntityListener listener) {
		if (this.listeners!=null) {
			if (this.listeners.remove(listener)) {
				if (this.listeners.isEmpty())
					this.listeners = null;
			}
		}
	}
	
	/** Fire the goal change event.
	 * 
	 * @param oldGoal is the previous goal of the entity.
	 * @param newGoal is the current goal of the entity.
	 */
	protected final synchronized void fireGoalChange(EcoRelation oldGoal, EcoRelation newGoal) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			GoalChangeEvent event = new GoalChangeEvent(this, oldGoal, newGoal);
			for(EcoEntityListener listener : list) {
				listener.goalChanged(event);
			}
		}
	}

	/** Fire the event to notify on the start of problem solving.
	 */
	protected final synchronized void fireProblemSolvingStarted() {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			for(EcoEntityListener listener : list) {
				listener.problemSolvingStarted();
			}
		}
	}

	/** Fire the event to notify on the solving of the problem.
	 */
	protected final synchronized void fireProblemSolved() {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			for(EcoEntityListener listener : list) {
				listener.problemSolved();
			}
		}
	}

	/** Fire the acquaintance addition event.
	 * 
	 * @param acquaintances are the added acquaintances.
	 */
	protected final synchronized void fireAcquaintanceAddition(Collection<EcoRelation> acquaintances) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			AcquaintanceEvent event = new AcquaintanceEvent(this, EventType.ADDITION, acquaintances);
			for(EcoEntityListener listener : list) {
				listener.acquaintanceChanged(event);
			}
		}
	}

	/** Fire the acquaintance addition event.
	 * 
	 * @param acquaintance is the added acquaintance.
	 */
	protected final synchronized void fireAcquaintanceAddition(EcoRelation acquaintance) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			AcquaintanceEvent event = new AcquaintanceEvent(this, EventType.ADDITION, acquaintance);
			for(EcoEntityListener listener : list) {
				listener.acquaintanceChanged(event);
			}
		}
	}

	/** Fire the acquaintance removal event.
	 * 
	 * @param acquaintances are the removed acquaintances.
	 */
	protected final synchronized void fireAcquaintanceRemoval(Collection<EcoRelation> acquaintances) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			AcquaintanceEvent event = new AcquaintanceEvent(this, EventType.REMOVAL, acquaintances);
			for(EcoEntityListener listener : list) {
				listener.acquaintanceChanged(event);
			}
		}
	}

	/** Fire the acquaintance removal event.
	 * 
	 * @param acquaintance is the removed acquaintance.
	 */
	protected final synchronized void fireAcquaintanceRemoval(EcoRelation acquaintance) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			AcquaintanceEvent event = new AcquaintanceEvent(this, EventType.REMOVAL, acquaintance);
			for(EcoEntityListener listener : list) {
				listener.acquaintanceChanged(event);
			}
		}
	}

	/** Fire the attack addition event.
	 * 
	 * @param attack is the added attack.
	 */
	protected final synchronized void fireAttackAddition(EcoAttack attack) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			AttackEvent event = new AttackEvent(this, EventType.ADDITION, attack);
			for(EcoEntityListener listener : list) {
				listener.attackChanged(event);
			}
		}
	}

	/** Fire the attack removal event.
	 * 
	 * @param attacks are the removed attacks.
	 */
	protected final synchronized void fireAttackRemoval(Collection<EcoAttack> attacks) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			AttackEvent event = new AttackEvent(this, EventType.REMOVAL, attacks);
			for(EcoEntityListener listener : list) {
				listener.attackChanged(event);
			}
		}
	}

	/** Fire the dependency addition event.
	 * 
	 * @param dependency is the added dependency.
	 */
	protected final synchronized void fireDependencyAddition(EcoIdentity dependency) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			DependencyEvent event = new DependencyEvent(this, EventType.ADDITION, dependency);
			for(EcoEntityListener listener : list) {
				listener.dependencyChanged(event);
			}
		}
	}

	/** Fire the dependency removal event.
	 * 
	 * @param dependency is the removed dependency.
	 */
	protected final synchronized void fireDependencyRemoval(EcoIdentity dependency) {
		if (this.listeners!=null) {
			EcoEntityListener[] list = new EcoEntityListener[this.listeners.size()];
			this.listeners.toArray(list);
			DependencyEvent event = new DependencyEvent(this, EventType.REMOVAL, dependency);
			for(EcoEntityListener listener : list) {
				listener.dependencyChanged(event);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EcoIdentity getIdentity() {
		return this.identity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<EcoAttack> getAttacks() {
		return Collections.unmodifiableSet(this.attacks);
	}
	
	/** Clear the attacks.
	 */
	protected void clearAttacks() {
		if (!this.attacks.isEmpty()) {
			Collection<EcoAttack> removedAttacks = this.attacks;
			this.attacks = new HashSet<EcoAttack>();
			fireAttackRemoval(removedAttacks);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<EcoRelation> getAcquaintances() {
		return Collections.unmodifiableSet(this.acquaintances);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<EcoIdentity> getDependencies() {
		return Collections.unmodifiableSet(this.dependencies);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EcoRelation getGoal() {
		return this.goal;
	}

	/** Add an acquaintance. This acquaintance change will be apply on the
	 * eco-entity knowledge later.
	 * 
	 * @param relation
	 */
	@Override
	public void addAcquaintance(EcoRelation relation) {
		assert(relation!=null);
		if (!this.bufferedRemovedAcquaintances.isEmpty()) {
			Iterator<EcoRelation> iterator = this.bufferedRemovedAcquaintances.iterator();
			while (iterator.hasNext()) {
				EcoRelation oldRelation = iterator.next();
				if (oldRelation.isConflict(relation)) {
					iterator.remove();
				}
			}
		}
		this.bufferedAddedAcquaintances.add(relation);
	}	
	
	/** Remove an acquaintance. This acquaintance change will be apply on the
	 * eco-entity knowledge later.
	 * 
	 * @param relation
	 */
	public void removeAcquaintance(EcoRelation relation) {
		assert(relation!=null);
		if (!this.bufferedAddedAcquaintances.isEmpty()) {
			Iterator<EcoRelation> iterator = this.bufferedAddedAcquaintances.iterator();
			while (iterator.hasNext()) {
				EcoRelation oldRelation = iterator.next();
				if (oldRelation.isConflict(relation)) {
					iterator.remove();
				}
			}
		}
		this.bufferedRemovedAcquaintances.add(relation);
	}	

	/**
	 * Notify the other eco-entity which is participating to the relation
	 * that the acquaintance is no more known by this party.
	 */
	private void notifyOtherPartyAboutAcquaintanceChange(EcoRelation relation, boolean isAddition) {		
		EcoIdentity otherParty = relation.getOtherParticipant(getIdentity());
		
		EcoAcquaintanceMessage message = new EcoAcquaintanceMessage(relation, isAddition);
		
		if (otherParty!=null && !(otherParty instanceof NoIdentity) && !(otherParty instanceof AnyIdentity)) sendMessage(message, otherParty);

		for(EcoIdentity dependency : this.dependencies) {
			sendMessage(message, dependency);
		}
	}
	
	private boolean removeConflictingAcquaintances(Collection<EcoRelation> acquaintances) {
		boolean changed = false;
		for(EcoRelation newRelation : acquaintances) {
			Iterator<EcoRelation> iterator = this.acquaintances.iterator();
			while (iterator.hasNext()) {
				EcoRelation oldRelation = iterator.next();
				if (newRelation.isConflict(oldRelation)) {
					iterator.remove();
					changed = true;
					notifyOtherPartyAboutAcquaintanceChange(oldRelation, false);
					fireAcquaintanceRemoval(oldRelation);
				}
			}
		}
		return changed;
	}

	private boolean updateKnownledgeFromBufferedAcquaintanceChanges() {
		boolean changed = false;

		if (!this.bufferedAddedAcquaintances.isEmpty()) {
			// Remove conflicting relations
			changed = removeConflictingAcquaintances(this.bufferedAddedAcquaintances);
			
			// Add new relations
			Collection<EcoRelation> addedAquaintances = new LinkedList<EcoRelation>();
			for(EcoRelation newAcquaintance : this.bufferedAddedAcquaintances) {
				if (this.acquaintances.add(newAcquaintance)) {
					changed = true;
					addedAquaintances.add(newAcquaintance);
					notifyOtherPartyAboutAcquaintanceChange(newAcquaintance, true);
				}
			}
			this.bufferedAddedAcquaintances.clear();
			if (!addedAquaintances.isEmpty())
				fireAcquaintanceAddition(addedAquaintances);
		}

		if (!this.bufferedRemovedAcquaintances.isEmpty()) {
			// Remove relations
			Collection<EcoRelation> removedAquaintances = new LinkedList<EcoRelation>();
			for(EcoRelation oldAcquaintance : this.bufferedRemovedAcquaintances) {
				if (this.acquaintances.remove(oldAcquaintance)) {
					changed = true;
					removedAquaintances.add(oldAcquaintance);
					notifyOtherPartyAboutAcquaintanceChange(oldAcquaintance, false);
				}
			}
			this.bufferedRemovedAcquaintances.clear();
			if (!removedAquaintances.isEmpty())
				fireAcquaintanceAddition(removedAquaintances);
		}

		return changed;
	}
	
	private boolean updateKnowledgeFromMailBox() {
		boolean changed = false;
		for(Message msg : getMessages()) {
			if (msg instanceof EcoProblemSolverPresentationMessage) {
				if (this.monitor==null) {
					this.monitor = new AgentIdentity((AgentAddress)msg.getSender());
					changed = true;
				}
			}
			else if (msg instanceof EcoProblemSolvingStartMessage) {
				if (this.monitor!=null && this.monitor.equals(msg.getSender())) {
					this.isSolvingStarted = true;
					changed = true;
					fireProblemSolvingStarted();
				}
			}
			else if (msg instanceof EcoAttackMessage) {
				EcoAttack attack = ((EcoAttackMessage)msg).getContent();
				if (attack!=null && this.attacks.add(attack)) {
					changed = true;
					fireAttackAddition(attack);
				}
			}
			else if (msg instanceof EcoAcquaintanceMessage) {
				EcoAcquaintanceMessage m = (EcoAcquaintanceMessage)msg;
				EcoRelation acquaintance = m.getContent();
				if (acquaintance!=null) {
					if (m.isKnowledgeAddition()) {
						changed = removeConflictingAcquaintances(Collections.singleton(acquaintance));
						if (this.acquaintances.add(acquaintance)) {
							changed = true;
							EcoAcquaintanceMessage message = new EcoAcquaintanceMessage(acquaintance, true);
							for(EcoIdentity dependency : this.dependencies) {
								sendMessage(message, dependency);
							}
							fireAcquaintanceAddition(acquaintance);
						}
					}
					else {
						if (this.acquaintances.remove(acquaintance)) {
							changed = true;
							EcoAcquaintanceMessage message = new EcoAcquaintanceMessage(acquaintance, false);
							for(EcoIdentity dependency : this.dependencies) {
								sendMessage(message, dependency);
							}
							fireAcquaintanceRemoval(acquaintance);
						}
					}
				}
			}
			else if (msg instanceof EcoDependencyMessage) {
				EcoDependencyMessage dMsg = (EcoDependencyMessage)msg;
				EcoIdentity dependency = dMsg.getContent();
				if (dependency!=null) {
					if (dMsg.isDependencyCreation()) {
						if (this.dependencies.add(dependency)) {
							changed = true;
							fireDependencyAddition(dependency);
							
							// Provide a feedback.
							EcoRelation feedbackPattern = dMsg.getFeedBackPattern();
							assert(feedbackPattern!=null);
							for(EcoRelation acquaintance : this.acquaintances) {
								if (feedbackPattern.equals(acquaintance)) {
									sendMessage(new EcoAcquaintanceMessage(acquaintance, true), dependency);
								}
							}
						}				
					}
					else {
						if (this.dependencies.remove(dependency)) {
							changed = true;
							fireDependencyRemoval(dependency);
						}
					}
				}
			}
			else if (msg instanceof EcoProblemSolvedMessage) {
				fireProblemSolved();
				doKill();
			}
		}
		return changed;
	}
	
	/** Invoked when the eco-entity should be destroyed.
	 */
	protected abstract void doKill();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateGoal() {
		boolean changed = false;
		if (this.bufferedGoal!=null) {
			if (this.goal==null || !this.goal.equals(this.bufferedGoal)) {
				EcoRelation oldGoal = this.goal;
				this.goal = this.bufferedGoal;
				
				EcoIdentity id = getIdentity();
				
				if (oldGoal!=null) {
					sendMessage(new EcoDependencyMessage(id), oldGoal.getSlave());
				}
				if (this.goal!=null) {
					sendMessage(
							new EcoDependencyMessage(
									id,
									this.goal.toPattern(id)),
							this.goal.getSlave());
				}
				
				changed = true;
				fireGoalChange(oldGoal, this.goal);
			}
			this.bufferedGoal = null;
		}
		return changed;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateKnowledge() {
		boolean changed = updateKnownledgeFromBufferedAcquaintanceChanges();
		changed = updateKnowledgeFromMailBox() || changed;
		return changed;
	}
	
	/** Set the goal of this eco-entity.
	 * A goal is a relationship to obtain against an
	 * other eco-entity.
	 * The master of the given relation <strong>MUST BE</strong>
	 * this eco-entity.
	 * 
	 * @param goal is the goal of this eco-entity.
	 */
	@Override
	public void setGoal(EcoRelation goal) {
		assert(goal.getMaster().equals(getIdentity()));
		this.bufferedGoal = goal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doAttack(EcoAttack attack) {
		sendMessage(new EcoAttackMessage(attack), attack.getDefender());
	}


	/** Replies the messages received by the eco-entity.
	 * 
	 * @return the messages.
	 */
	protected abstract Iterable<Message> getMessages();

	/** Send the message to the eco-entity.
	 * 
	 * @param message is the message to send.
	 * @param receiver is the receiver of the message.
	 */
	protected abstract void sendMessage(Message message, EcoIdentity receiver);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.identity.toString();
	}
}