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
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.organization.Group;
import org.janusproject.kernel.crio.organization.GroupCondition;
import org.janusproject.kernel.crio.role.RoleCondition;

/**
 * This condition verify if a given agent owns all required capacities to play a given role
 * <p>
 * Parameter : 1<br>
 * The list of the class of capacity (<code>List<Class<? extends Capacity>></code>) that the requester agent owns.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class HasAllRequiredCapacitiesCondition
extends AbstractCondition<RolePlayer>
implements RoleCondition, GroupCondition {

	private static final long serialVersionUID = -1159755448527733475L;
	
	private final Collection<Class<? extends Capacity>> requiredCapacities;

	/**
	 * @param required is the list of required capacities.
	 */
	public HasAllRequiredCapacitiesCondition(Collection<Class<? extends Capacity>> required) {
		this.requiredCapacities = required;
	}

	/**
	 * @param required is the capacities.
	 */
	public HasAllRequiredCapacitiesCondition(Class<? extends Capacity> required) {
		this.requiredCapacities = new ArrayList<Class<? extends Capacity>>();
		this.requiredCapacities.add(required);
	}

	/**
	 * Verify if the specified agent owns an implementation for
	 * all the capacities previously registered in this condition.
	 * 
	 * @param ownedCapacities is the collection of capacities owned by the agent. 
	 * @return <tt>true</tt> if the agent owns all required capacities.
	 */
	public boolean evaluate(Collection<? extends Class<? extends Capacity>> ownedCapacities) {
		assert(ownedCapacities!=null);
		for (Class<? extends Capacity> c : this.requiredCapacities) {
			if (!ownedCapacities.contains(c)) {
				return false;
			}				
		}
		return true;		
	}

	/**
	 * Verify if the specified agent owns an implementation for
	 * all the capacities previously registered in this condition.
	 * 
	 * @param ownedCapacities is the collection of capacities owned by the agent. 
	 * @return <code>null</code> if condition does not met,
	 * otherwise the failed condition (this condition or a sub-condition).
	 */
	public ConditionFailure evaluateFailure(Collection<? extends Class<? extends Capacity>> ownedCapacities) {
		assert(ownedCapacities!=null);
		for (Class<? extends Capacity> c : this.requiredCapacities) {
			if (c!=null && !ownedCapacities.contains(c)) {
				return new CapacityMissedConditionFailure(c);
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
		return evaluate(object.getCapacityContainer().identifiers());
	}

	/** {@inheritDoc}
	 */
	@Override
	public ConditionFailure evaluateFailure(RolePlayer object) {
		return evaluateFailure(object.getCapacityContainer().identifiers());
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class CapacityMissedConditionFailure
	implements ConditionFailure {

		private static final long serialVersionUID = -899620550045083980L;

		private final Class<? extends Capacity> capacity;
		
		/**
		 * @param capacity is the missed capacity.
		 */
		public CapacityMissedConditionFailure(Class<? extends Capacity> capacity) {
			this.capacity = capacity;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Locale.getString(
					HasAllRequiredCapacitiesCondition.class,
					"CAPACITY_MISSED", //$NON-NLS-1$
					this.capacity.getCanonicalName());
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
