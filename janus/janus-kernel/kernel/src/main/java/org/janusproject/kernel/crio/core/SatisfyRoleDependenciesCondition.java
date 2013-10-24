/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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

import java.util.ArrayList;
import java.util.Collection;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.condition.AbstractCondition;
import org.janusproject.kernel.condition.ConditionFailure;
import org.janusproject.kernel.crio.organization.Group;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.role.RoleCondition;

/**
 *  This condition verify if a given role player owns all required role dependencies to play a given role
 * <p> 
 * Parameter : 1
 * The list of the class of role (<code>List<Class<? extends Role>></code>) that the requester object plays.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SatisfyRoleDependenciesCondition
extends AbstractCondition<RolePlayer>
implements RoleCondition, GroupCondition {

	private static final long serialVersionUID = -3543587326124243015L;
	
	private final Collection<Class<? extends Role>> requiredRoles;

	/**
	 * @param required is the list of required roles.
	 */
	public SatisfyRoleDependenciesCondition(Collection<Class<? extends Role>> required) {
		this.requiredRoles = required;
	}

	/**
	 * @param required is the capacities.
	 */
	public SatisfyRoleDependenciesCondition(Class<? extends Role> required) {
		this.requiredRoles = new ArrayList<Class<? extends Role>>();
		this.requiredRoles.add(required);
	}

	/**
	 * Verify if the specified agent is playing 
	 * all the roles previously registered in this condition.
	 * 
	 * @param playedRoles is the collection of roles played by the agent. 
	 * @return <tt>true</tt> if the agent is playing all required roles.
	 */
	public boolean evaluate(Collection<Class<? extends Role>> playedRoles) {
		assert(playedRoles!=null);
		for (Class<? extends Role> c : this.requiredRoles) {
			if (!playedRoles.contains(c)) {
				return false;
			}				
		}
		return true;		
	}

	/**
	 * Verify if the specified agent is playing 
	 * all the roles previously registered in this condition.
	 * 
	 * @param playedRoles is the collection of roles played by the agent. 
	 * @return <code>null</code> if condition does not met,
	 * otherwise the failed condition (this condition or a sub-condition).
	 */
	public ConditionFailure evaluateFailure(Collection<Class<? extends Role>> playedRoles) {
		assert(playedRoles!=null);
		for (Class<? extends Role> c : this.requiredRoles) {
			if (c!=null && !playedRoles.contains(c)) {
				return new RoleMissedConditionFailure(c);
			}				
		}
		return null;		
	}

	/** {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "HasAllRequiredCapacitiesCondition"; //$NON-NLS-1$
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean evaluate(RolePlayer object) {
		return evaluate(object.getRoles());
	}

	/** {@inheritDoc}
	 */
	@Override
	public ConditionFailure evaluateFailure(RolePlayer object) {
		return evaluateFailure(object.getRoles());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class RoleMissedConditionFailure
	implements ConditionFailure {

		private static final long serialVersionUID = -2582834464424545616L;
		
		private final Class<? extends Role> role;
		
		/**
		 * @param role is the missed role.
		 */
		public RoleMissedConditionFailure(Class<? extends Role> role) {
			this.role = role;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Locale.getString(
					SatisfyRoleDependenciesCondition.class,
					"ROLE_MISSED", //$NON-NLS-1$
					this.role.getCanonicalName());
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean evaluateOnGroup(RolePlayer object, Class<? extends Role> roleToTake, Group group) {
		return evaluate(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConditionFailure evaluateFailureOnGroup(RolePlayer object, Class<? extends Role> roleToTake, Group group) {
		return evaluateFailure(object);
	}

}
