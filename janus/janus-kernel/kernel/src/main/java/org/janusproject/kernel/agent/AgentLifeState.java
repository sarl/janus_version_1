/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.agent;

import org.janusproject.kernel.schedule.Activable;

/**
 * This enumeration lists the different states of an
 * autonomous entity.
 * <p>
 * Agent Life Cycle is defined by the sequence of states.
 * Figure 1 illustrates a standard agent's life-cyle.
 * Agent starts "unborn" until there {@link Activable#activate(Object...)}
 * function is invoked and terminated.
 * During the "alive" state, the function {@link Activable#live()} is
 * continuously invoked. This loop is breaking down when the agent is
 * killed (by invoking {@link Agent#killMe()} or
 * {@link Agent#kill(org.janusproject.kernel.address.AgentAddress)}).
 * Then agent reachs the "died" state and run 
 * its {@link Activable#end()}.
 * <center>
 *   <img src="./doc-files/Standard_Agent_LifeCycle.png">
 *   <small>Figure 1: Standard Agent's Life-Cycle</small>
 * </center>
 * <p>
 * Figure 2 illustrates the finer agent's life-cyle, which is
 * currently implemented in this AgentLifeState enumeration.
 * <center>
 *   <img src="./doc-files/Finer_Agent_LifeCycle.png">
 *   <small>Figure 2: Implemented Agent's Life-Cycle</small>
 * </center>
 * The main difference between the standard life-cycle and the
 * finer life-cycle is that additional states are introduced
 * for transitions:<ul>
 * <li>{@link #BORN}: <code>activate()</code> is under running;</li>
 * <li>{@link #DYING}: <code>killMe()</code> or <code>kill()</code> was invoked but agent's destruction is not yet proceeded;</li>
 * <li>{@link #BREAKING_DOWN}: <code>end()</code> is under running.</li>
 * </ul>
 * <p>
 * The table below describes the relationship between the agent life-cycle
 * and {@link Activable}:
 * <table border="1">
 * <tbody>
 * <tr>
 * <th rowspan=2>State</th>
 * <th rowspan=2>Object in memory</th>
 * <th colspan=2>{@link Activable#activate(Object...)}</th>
 * <th colspan=2>{@link Activable#live()}</th>
 * <th colspan=2>{@link Activable#end()}</th>
 * </tr>
 * <tr>
 * <th>started</th>
 * <th>finished</th>
 * <th>started</th>
 * <th>finished</th>
 * <th>started</th>
 * <th>finished</th>
 * </tr>
 * </tbody>
 * <tr>
 * 		<td>{@link #UNBORN}</td>
 * 		<td>yes</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * </tr>
 * <tr>
 * 		<td>{@link #BORN}</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * </tr>
 * <tr>
 * 		<td>{@link #ALIVE}</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>-</td>
 * 		<td>-</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * </tr>
 * <tr>
 * 		<td>{@link #DYING}</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>-</td>
 * 		<td>no</td>
 * 		<td>no</td>
 * </tr>
 * <tr>
 * 		<td>{@link #BREAKING_DOWN}</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>no</td>
 * </tr>
 * <tr>
 * 		<td>{@link #DIED}</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * 		<td>yes</td>
 * </tr>
 * </table>
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum AgentLifeState {

	/** This state indicated that the entity exists in memory
	 * but it was never initialized.
	 */
	UNBORN {
		@Override
		public AgentLifeState next() { return BORN; }
		@Override
		public AgentLifeState previous() { return UNBORN; }
		@Override
		public boolean isAlive() { return false; }
		@Override
		public boolean isLifeless() { return true; }
		@Override
		public boolean isPrenatal() { return true; }
		@Override
		public boolean isMortuary() { return false; }
	},
	
	/** This start indicated that the entity is currently
	 * under initialization.
	 */
	BORN {
		@Override
		public AgentLifeState next() { return ALIVE; }
		@Override
		public AgentLifeState previous() { return UNBORN; }
		@Override
		public boolean isAlive() { return false; }
		@Override
		public boolean isLifeless() { return true; }
		@Override
		public boolean isPrenatal() { return true; }
		@Override
		public boolean isMortuary() { return false; }
	},

	/** This standard state durig which the entity
	 * is able to do something.
	 */
	ALIVE {
		@Override
		public AgentLifeState next() { return DYING; }
		@Override
		public AgentLifeState previous() { return BORN; }
		@Override
		public boolean isAlive() { return true; }
		@Override
		public boolean isLifeless() { return false; }
		@Override
		public boolean isPrenatal() { return false; }
		@Override
		public boolean isMortuary() { return false; }
	},
	
	/** At the end of its life, an entity may
	 * pass by this intermediate state. The entity
	 * is able to do something but it is informed
	 * that it will disappear very soon.
	 */
	DYING {
		@Override
		public AgentLifeState next() { return BREAKING_DOWN; }
		@Override
		public AgentLifeState previous() { return ALIVE; }
		@Override
		public boolean isAlive() { return true; }
		@Override
		public boolean isLifeless() { return false; }
		@Override
		public boolean isPrenatal() { return false; }
		@Override
		public boolean isMortuary() { return false; }
	},

	/** The entity was not more alive and currently inside
	 * a destruction process.
	 */
	BREAKING_DOWN {
		@Override
		public AgentLifeState next() { return DIED; }
		@Override
		public AgentLifeState previous() { return DYING; }
		@Override
		public boolean isAlive() { return false; }
		@Override
		public boolean isLifeless() { return true; }
		@Override
		public boolean isPrenatal() { return false; }
		@Override
		public boolean isMortuary() { return true; }
	},

	/** The entity was dead, ie destroyed, but is still present is memory.
	 */
	DIED {
		@Override
		public AgentLifeState next() { return DIED; }
		@Override
		public AgentLifeState previous() { return BREAKING_DOWN; }
		@Override
		public boolean isAlive() { return false; }
		@Override
		public boolean isLifeless() { return true; }
		@Override
		public boolean isPrenatal() { return false; }
		@Override
		public boolean isMortuary() { return true; }
	};

	/** Replies the next state.
	 *
	 * @return the next state.
	 */
	public abstract AgentLifeState next(); 
	
	/** Replies the previous state.
	 *
	 * @return the previous state.
	 */
	public abstract AgentLifeState previous();
	
	/** Replies if the state permits to the entity to do something.
	 * <p>
	 * Basically {@link #ALIVE} and {@link #DYING} are both alive states.
	 * 
	 * @return <code>true</code> if the entity is able to do something,
	 * otherwise <code>false</code>.
	 */
	public abstract boolean isAlive();

	/** Replies if the state does not permit to the entity to do something.
	 * <p>
	 * Basically {@link #UNBORN}, {@link #BORN}, {@link #DIED}, and {@link #BREAKING_DOWN} 
	 * are all lifeless states.
	 * 
	 * @return <code>true</code> if the entity is not able to do something,
	 * otherwise <code>false</code>.
	 */
	public abstract boolean isLifeless();

	/** Replies if the state corresponds to a young lifeless entity state.
	 * <p>
	 * Basically {@link #UNBORN}, are {@link #BORN} are 
	 * all prenatal states.
	 * 
	 * @return <code>true</code> if the entity is not already alive,
	 * otherwise <code>false</code>.
	 */
	public abstract boolean isPrenatal();

	/** Replies if the state corresponds to a old lifeless entity state.
	 * <p>
	 * Basically {@link #BREAKING_DOWN}, are {@link #DIED} are 
	 * all mortuary states.
	 * 
	 * @return <code>true</code> if the entity is no more alive,
	 * otherwise <code>false</code>.
	 */
	public abstract boolean isMortuary();

}
