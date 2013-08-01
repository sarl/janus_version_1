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

import java.util.Collection;

/**
 * This interface represents an object that the access and the liberation 
 * are restricted by a set of conditions to satisfy : 
 * <ul>
 * <li> <code>obtainConditions</code> : the set of conditions to satisfy to access/assume/acquire this object
 * <li> <code>leaveConditions</code> : the set of conditions to satisfy to leave/liberate this object
 * </ul>
 * Each condition are considered with the same importance, and they have to be all satisfied to access or leave this object.
 *
 * @param <O> is the type of object to test in the condition.
 * @param <C> is the type of all the conditions.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Conditionnable<O, C extends Condition<? super O>> {

	/**
	 * Verify if all the obtain conditions are satisfied by the specified agent
	 * @param parameterOwner - the requester object
	 * @return <tt>true</tt> if the specified agent satisfy all obtain condition of this object.
	 */
	public boolean isObtainable(O parameterOwner);

	/**
	 * Verify if all the leave conditions are satisfied by the specified agent
	 * @param parameterOwner - the requester object
	 * @return <tt>true</tt> if the specified agent satisfy all leave condition of this object.
	 */
	public boolean isLeavable(O parameterOwner);

	/** Replies all the obtain conditions.
	 * 
	 * @return the obtain conditions.
	 */
	public Collection<C> getObtainConditions();
	
	/** Replies all the leave conditions.
	 * 
	 * @return the leave conditions.
	 */
	public Collection<C> getLeaveConditions();

}
