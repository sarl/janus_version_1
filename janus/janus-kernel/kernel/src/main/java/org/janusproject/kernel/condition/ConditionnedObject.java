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
package org.janusproject.kernel.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This class represents an object that the access and the liberation 
 * are restricted by a set of conditions to satisfy : 
 * <ul>
 * <li> <code>obtainConditions</code> : the set of conditions to satisfy to access/assume/acquire this object
 * <li> <code>leaveConditions</code> : the set of conditions to satisfy to leave/liberate this object
 * </ul>
 * Each condition are considered with the same importance, and they have to be all satisfied to access or leave this object.
 *
 * @param <O> is the type of the objects tested in the condition.
 * @param <C> is the type of all the conditions.
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ConditionnedObject<O, C extends Condition<? super O>>
implements Conditionnable<O,C> {

	/**
	 * the set of conditions to satisfy before accessing/assuming/acquiring this object
	 */
	private Collection<C> obtainConditions = null;

	/**
	 * the set of conditions to satisfy before leaving/liberating this object
	 */
	private Collection<C> leaveConditions = null;

	/**
	 * Builds a new ConditionnedObject
	 */
	public ConditionnedObject() {
		//
	}

	/**
	 * Builds a new ConditionnedObject with the specified collection of obtain and leave conditions.
	 * 
	 * @param obtainConditions is the collection of obtain conditions.
	 * @param leaveConditions is the collection of leave conditions.
	 */
	public ConditionnedObject(Collection<? extends C> obtainConditions, Collection<? extends C> leaveConditions) {
		if (obtainConditions!=null && !obtainConditions.isEmpty()) {
			this.obtainConditions = new ArrayList<C>(obtainConditions);
		}
		if (leaveConditions!=null && !leaveConditions.isEmpty()) {
			this.leaveConditions = new ArrayList<C>(leaveConditions);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isObtainable(O parameterOwner) {
		if (this.obtainConditions!=null) {
			for (C c : this.obtainConditions) {
				if (!c.evaluate(parameterOwner)) return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLeavable(O parameterOwner) {
		if (this.leaveConditions!=null) {
			for (C c : this.leaveConditions) {
				if (!c.evaluate(parameterOwner)) return false;
			}
		}
		return true;
	}

	/**
	 * Verify if all the obtain conditions are satisfied by the specified agent
	 * @param parameterOwner - the requester object
	 * @return <code>null</code> if all conditions are met, otherwise a
	 * description of the first failed condition.
	 */
	public ConditionFailure getObtainFailure(O parameterOwner) {
		if (this.obtainConditions!=null) {
			ConditionFailure cf;
			for (C c : this.obtainConditions) {
				cf = c.evaluateFailure(parameterOwner);
				if (cf!=null) return cf;
			}
		}
		return null;
	}
	
	

	/**
	 * Verify if all the leave conditions are satisfied by the specified agent
	 * @param parameterOwner - the requester object
	 * @return <code>null</code> if all conditions are met, otherwise a
	 * description of the first failed condition.
	 */
	public ConditionFailure getLeaveFailure(O parameterOwner) {
		if (this.leaveConditions!=null) {
			ConditionFailure cf;
			for (C c : this.leaveConditions) {
				cf = c.evaluateFailure(parameterOwner);
				if (cf!=null) return cf;
			}
		}
		return null;
	}

	/**
	 * Appends the specified condition to the end of the obtain conditions
	 * @param c - the condition to add to the obtain conditions
	 * @return <tt>true</tt> if the obtain conditions changed as a result of the
	 *         call
	 */
	protected boolean addObtainCondition(C c) {
		if (c==null) return false;
		if (this.obtainConditions==null)
			this.obtainConditions = new LinkedList<C>();
		return this.obtainConditions.add(c);
	}


	/**
	 * Appends the specified condition to the end of the leave conditions
	 * @param c - the condition to add to the leave conditions
	 * @return <tt>true</tt> if the leave conditions changed as a result of the
	 *         call
	 */
	protected boolean addLeaveCondition(C c) {
		if (c==null) return false;
		if (this.leaveConditions==null)
			this.leaveConditions = new LinkedList<C>();
		return this.leaveConditions.add(c);
	}

	/**
	 * Set the leave conditions.
	 * @param c - leave conditions
	 */
	protected void setLeaveCondition(Collection<? extends C> c) {
		if (c==null || c.isEmpty()) {
			this.leaveConditions = null;		
		}
		else {
			if (this.leaveConditions==null)
				this.leaveConditions = new LinkedList<C>();
			this.leaveConditions.addAll(c);
		}
	}

	/**
	 * Set the obtains conditions.
	 * @param c - obtain conditions
	 */
	protected void setObtainCondition(Collection<? extends C> c) {
		if (c==null || c.isEmpty()) {
			this.obtainConditions = null;		
		}
		else {
			if (this.obtainConditions==null)
				this.obtainConditions = new LinkedList<C>();
			this.obtainConditions.addAll(c);
		}
	}

	/**
	 * Remove the specified condition from the obtain conditions.
	 * 
	 * @param c - the condition to remove from the obtain conditions.
	 * @return <tt>true</tt> if the obtain conditions changed as a result of the
	 *         call
	 */
	protected boolean removeObtainCondition(Condition<?> c) {
		if (c!=null && this.obtainConditions!=null) {
			if (this.obtainConditions.remove(c)) {
				if (this.obtainConditions.isEmpty())
					this.obtainConditions = null;
				return true;
			}
		}
		return false;
	}


	/**
	 * Remove the specified condition from the leave conditions
	 * @param c - the condition to remove from the leave conditions
	 * @return <tt>true</tt> if the leave conditions changed as a result of the
	 *         call
	 */
	protected boolean removeLeaveCondition(Condition<?> c) {
		if (c!=null && this.leaveConditions!=null) {
			if (this.leaveConditions.remove(c)) {
				if (this.leaveConditions.isEmpty())
					this.leaveConditions = null;
				return true;
			}
		}
		return false;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Collection<C> getObtainConditions() {
		if (this.obtainConditions==null) return Collections.emptyList();
		return Collections.unmodifiableCollection(this.obtainConditions);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Collection<C> getLeaveConditions() {
		if (this.leaveConditions==null) return Collections.emptyList();
		return Collections.unmodifiableCollection(this.leaveConditions);
	}

}
