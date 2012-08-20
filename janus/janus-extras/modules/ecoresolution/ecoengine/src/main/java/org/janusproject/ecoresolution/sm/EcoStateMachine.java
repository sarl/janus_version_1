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
package org.janusproject.ecoresolution.sm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.janusproject.ecoresolution.entity.EcoEntity;
import org.janusproject.ecoresolution.event.EcoStateMachineListener;
import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.ecoresolution.relation.EcoRelation;

/**
 * State machine for eco-resolution problem solving.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 * @see EcoState
 */
public class EcoStateMachine {

	private final EcoEntity entity;
	private final AtomicBoolean isNewState = new AtomicBoolean();

	private Collection<EcoStateMachineListener> listeners = null;

	private EcoState state;

	/**
	 * @param ecoentity
	 *            is the eco-entity which is running the state machine.
	 */
	public EcoStateMachine(EcoEntity ecoentity) {
		this.state = EcoState.INITIALIZING;
		this.isNewState.set(true);
		this.entity = ecoentity;
	}

	/**
	 * Add listener on state machine events.
	 * 
	 * @param listener
	 */
	public void addEcoStateMachineListener(EcoStateMachineListener listener) {
		if (listener != null) {
			if (this.listeners == null)
				this.listeners = new ArrayList<EcoStateMachineListener>(1);
			this.listeners.add(listener);
		}
	}

	/**
	 * Add listener on state machine events.
	 * 
	 * @param listener
	 */
	public void removeEcoStateMachineListener(EcoStateMachineListener listener) {
		if (listener != null && this.listeners != null) {
			this.listeners.remove(listener);
			if (this.listeners.isEmpty())
				this.listeners = null;
		}
	}

	/**
	 * Notify listeners about state change.
	 * 
	 * @param oldState
	 *            is the old state.
	 * @param newState
	 *            is the new state.
	 */
	protected void fireStateChange(EcoState oldState, EcoState newState) {
		if (this.listeners != null) {
			EcoStateMachineListener[] list = new EcoStateMachineListener[this.listeners.size()];
			this.listeners.toArray(list);
			for (EcoStateMachineListener listener : list) {
				listener.stateChanged(oldState, newState);
			}
		}
	}

	/**
	 * Replies the entity which is using the state machine.
	 * 
	 * @return the eco-entity.
	 */
	public EcoEntity getEntity() {
		return this.entity;
	}

	/**
	 * Replies the current state of the machine.
	 * 
	 * @return the current state of the machine.
	 */
	public EcoState getState() {
		return this.state;
	}

	/**
	 * Replies if the state machine is in a terminal state.
	 * 
	 * @return <code>true</code> if the state machine is in a terminal state, otherwise <code>false</code>
	 */
	public boolean isTerminalState() {
		return this.state.isTerminalState();
	}

	/**
	 * Run the eco-resolution state machine.
	 * 
	 * @return <code>true</code> if the state machine has run something, <code>false</code> if the state machine has do nothing.
	 */
	public boolean run() {
		boolean isRun = false;
		EcoEntity e = getEntity();
		if (e != null) {
			boolean goalChanged = e.updateGoal();
			boolean knowledgeChanged = e.updateKnowledge();
			if (this.isNewState.getAndSet(false) || knowledgeChanged || goalChanged) {
				switch (this.state) {
				case INITIALIZING: {
					// Do nothing here, see below
					break;
				}
				case INITIALIZED: {
					if (e.isProblemSolvingStarted()) {
						updateState(EcoState.SATISFACTING);
						isRun = true;
					}
					break;
				}
				case SATISFACTING: {
					Set<EcoAttack> attacks = e.getAttacks();
					if (attacks.isEmpty()) {
						EcoRelation goal = e.getGoal();
						Set<EcoRelation> acquaintances = e.getAcquaintances();
						boolean isSatisfied = (goal == null) || acquaintances.contains(goal);
						if (isSatisfied) {
							updateState(EcoState.SATISFACTED);
							e.doSatisfied();
						} else {
							EcoAttack intruder = e.selectSatisfactionIntruder();
							if (intruder != null) {
								e.doAttack(intruder);
							} else {
								e.doSatisfactionIncreasing();
							}
						}
					} else {
						updateState(EcoState.ESCAPING);
					}
					isRun = true;
					break;
				}
				case SATISFACTED: {
					Set<EcoAttack> attacks = e.getAttacks();
					if (!attacks.isEmpty()) {
						updateState(EcoState.ESCAPING);
					}
					isRun = true;
					break;
				}
				case ESCAPING: {
					Set<EcoAttack> attacks = e.getAttacks();
					Set<EcoAttack> intruders = e.selectEscapingIntruder(attacks);
					if (intruders != null && intruders.size() > 0) {
						for (EcoAttack intruder : intruders) {
							e.doAttack(intruder);
						}
					} else {
						e.doEscaping();
						updateState(EcoState.ESCAPED);
					}
					isRun = true;
					break;
				}
				case ESCAPED: {
					Set<EcoAttack> attacks = e.getAttacks();
					if (attacks.isEmpty()) {
						updateState(EcoState.SATISFACTING);
					} else {
						updateState(EcoState.ESCAPING);
					}
					isRun = true;
					break;
				}
				default:
					throw new IllegalStateException();
				}
			} else if (this.state == EcoState.INITIALIZING && !goalChanged && e.isProblemSolvingParticipantReady()) {
				updateState(EcoState.INITIALIZED);
				e.doInitializationCommitment();
				isRun = true;
			}
		}
		return isRun;
	}

	private void updateState(EcoState newState) {
		EcoState oldState = this.state;
		this.state = newState;
		this.isNewState.set(true);
		fireStateChange(oldState, newState);
	}

}