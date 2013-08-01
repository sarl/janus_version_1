/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.kernel.crio.core;

import org.janusproject.kernel.condition.AbstractCondition;
import org.janusproject.kernel.condition.ConditionFailure;
import org.janusproject.kernel.crio.core.RolePlayer;
import org.janusproject.kernel.crio.organization.GroupCondition;

/**
 * Abstract implementation of a GroupCondition that is able to access
 * to some information of the group.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class AbstractGroupCondition extends AbstractCondition<RolePlayer> implements GroupCondition {

	private static final long serialVersionUID = 4427697274827670220L;

	/**
	 * 
	 */
	public AbstractGroupCondition() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean evaluate(RolePlayer object) {
		return evaluateOnGroup(object, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConditionFailure evaluateFailure(RolePlayer object) {
		return evaluateFailureOnGroup(object, null, null);
	}	

}