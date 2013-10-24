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

import java.util.Map;
import java.util.TreeMap;

import org.arakhne.afc.vmutil.ClassComparator;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.condition.ConditionFailure;
import org.janusproject.kernel.crio.organization.Group;

/**
 * This condition verify if a maximal number of players for a role is not reached.
 * <p>
 * Parameter : 1<br>
 * The list of the class of capacity (<code>List<Class<? extends Capacity>></code>) that the requester agent owns.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class RoleCardinalityCondition
extends AbstractGroupCondition {

	private static final long serialVersionUID = -3323377025687556467L;
	
	private final Map<Class<? extends Role>,Integer> restrictions;
	
	/**
	 */
	public RoleCardinalityCondition() {
		this.restrictions = new TreeMap<Class<? extends Role>,Integer>(ClassComparator.SINGLETON);
	}

	/**
	 * @param restrictions are the max numbers of players per role.
	 */
	public RoleCardinalityCondition(Map<Class<? extends Role>,Integer> restrictions) {
		this.restrictions = restrictions;
	}
	
	/** Add a restriction on the number of players for the given role.
	 * 
	 * @param role is the role to restrict.
	 * @param maxNumber is the maximum number of players allowed in the group.
	 */
	public void addRectriction(Class<? extends Role> role, int maxNumber) {
		this.restrictions.put(role, maxNumber);
	}

	/** {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "RoleCardinalityCondition"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean evaluateOnGroup(RolePlayer object, Class<? extends Role> roleToTake, Group group) {
		assert(object!=null);
		if (group==null || roleToTake==null) return false;
		Integer maxNumber = this.restrictions.get(roleToTake);
		if (maxNumber!=null) {
			return group.getPlayerCount(roleToTake) < maxNumber.intValue();
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConditionFailure evaluateFailureOnGroup(RolePlayer object, Class<? extends Role> roleToTake, Group group) {
		assert(object!=null);
		if (group==null) return new NoGroupFailure(object.toString());
		if (roleToTake==null) return new NoRoleFailure(object.toString());
		Integer maxNumber = this.restrictions.get(roleToTake);
		if (maxNumber!=null && group.getPlayerCount(roleToTake) >= maxNumber.intValue()) {
			return new TooManyRoleFailure(object.toString(), roleToTake);
		}
		return null;
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class NoGroupFailure
	implements ConditionFailure {

		private static final long serialVersionUID = -7168473571632170389L;

		private final String player;
		
		/**
		 * @param player
		 */
		public NoGroupFailure(String player) {
			this.player = player;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Locale.getString(
					RoleCardinalityCondition.class,
					"NO_GROUP", //$NON-NLS-1$
					this.player);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class NoRoleFailure
	implements ConditionFailure {

		private static final long serialVersionUID = 4828911182616181362L;
		
		private final String player;
		
		/**
		 * @param player
		 */
		public NoRoleFailure(String player) {
			this.player = player;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Locale.getString(
					RoleCardinalityCondition.class,
					"NO_ROLE", //$NON-NLS-1$
					this.player);
		}
		
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class TooManyRoleFailure
	implements ConditionFailure {

		private static final long serialVersionUID = 1580323013559999592L;
		
		private final String player;
		private final Class<? extends Role> role;
		
		/**
		 * @param player
		 * @param role
		 */
		public TooManyRoleFailure(String player, Class<? extends Role> role) {
			this.player = player;
			this.role = role;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Locale.getString(
					RoleCardinalityCondition.class,
					"TOO_MANY_ROLE", //$NON-NLS-1$
					this.player,
					this.role);
		}
		
	}

}
