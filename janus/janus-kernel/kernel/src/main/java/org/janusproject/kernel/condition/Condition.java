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

import java.io.Serializable;

/** Represents a generic condition. 
 *
 * @param <O> is the type of object to evaluate.
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Condition<O> extends Serializable {
	
	/**
	 * Verify if the specified agent satisfy this condition
	 * 
	 * @param object - the object to test
	 * @return <tt>true</tt> if this condition is satisfied
	 */
	public boolean evaluate(O object);	

	/**
	 * Verify if the specified agent satisfy this condition.
	 * 
	 * @param object - the object to test
	 * @return <code>null</code> if condition is satisfied,
	 * otherwise the failure condition (this condition or a sub-condition).
	 */
	public ConditionFailure evaluateFailure(O object);	

}
