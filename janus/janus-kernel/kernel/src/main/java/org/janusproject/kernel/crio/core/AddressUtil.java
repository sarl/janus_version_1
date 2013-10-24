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

import java.util.StringTokenizer;
import java.util.UUID;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.AgentAddress;

/**
 * This class provides several utilities relatecd to addresses
 * and requiring to access to the Janus kernel.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class AddressUtil {

	/** Create the address for an agent from the given informations.
	 * 
	 * @param id is the identifier in the address.
	 * @return the agent address.
	 */
	public static AgentAddress createAgentAddress(UUID id) {
		return new PlayerAddress(null, id);
	}

	/** Create the address for an agent from the given informations.
	 * 
	 * @param id is the identifier in the address.
	 * @param name is the name of the agent.
	 * @return the agent address.
	 */
	public static AgentAddress createAgentAddress(UUID id, String name) {
		return new PlayerAddress(null, id, name);
	}

	/** Replies a AgentAddress which corresponds to the specified string.
	 * <p>
	 * The parameter must be result of the {@link AgentAddress#toString()} function.
	 * 
	 * @param address is the name of the address 
	 * @return the address or <code>null</code> if it could be created.
	 * @throws IllegalArgumentException if the given address has invalid format or value.
	 */
	public static AgentAddress createAgentAddress(String address)
	throws IllegalArgumentException {
		if (address==null)
			throw new IllegalArgumentException(
					Locale.getString(
							AddressUtil.class, "NULL_ADDRESS")); //$NON-NLS-1$
		StringTokenizer tok = new StringTokenizer(address, "::"); //$NON-NLS-1$
		if (tok.countTokens() != 2)
			throw new IllegalArgumentException(
					Locale.getString(
							AddressUtil.class, "INVALID_FORMAT")); //$NON-NLS-1$

		String name = tok.nextToken();
		String ids = tok.nextToken();

		tok = new StringTokenizer(ids, "@"); //$NON-NLS-1$

		UUID id = UUID.fromString(tok.nextToken());
		assert(id!=null);

		return new AgentAddress(id,name) {
			private static final long serialVersionUID = 2052897212713875693L;
		};
	}

	/** Create the address for a group from the given informations.
	 * 
	 * @param id is the identifier in the address.
	 * @param organization is the organization instancied by the group. 
	 * @return the group address.
	 * @since 1.0
	 */
	public static GroupAddress createGroupAddress(UUID id, Class<? extends Organization> organization) {
		return new GroupAddress(id, organization);
	}

	/** Create the address for a group from the given informations.
	 * 
	 * @param id is the identifier in the address.
	 * @param organization is the organization instancied by the group. 
	 * @param name is the name of the group.
	 * @return the group address.
	 * @since 1.0
	 */
	public static GroupAddress createGroupAddress(UUID id, Class<? extends Organization> organization, String name) {
		return new GroupAddress(id, organization, name);
	}

	/** Replies a GroupAddress which corresponds to the specified string.
	 * <p>
	 * The parameter must be result of the {@link GroupAddress#toString()} function.
	 * 
	 * @param address is the name of the address 
	 * @return the address or <code>null</code> if it could be created.
	 * @throws IllegalArgumentException if the given address has invalid format or value.
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	public static GroupAddress createGroupAddress(String address)
	throws IllegalArgumentException {
		if (address==null)
			throw new IllegalArgumentException(
					Locale.getString(
							AddressUtil.class, "NULL_ADDRESS")); //$NON-NLS-1$
		StringTokenizer tok = new StringTokenizer(address, "::"); //$NON-NLS-1$
		if (tok.countTokens() == 3) {
			String name = tok.nextToken();
			String ids = tok.nextToken();
			String orga = tok.nextToken();
	
			tok = new StringTokenizer(ids, "@"); //$NON-NLS-1$
	
			try {
				Class<?> orgaType = Class.forName(orga);
				
				if (Organization.class.isAssignableFrom(orgaType)) {
				
					UUID id = UUID.fromString(tok.nextToken());
					assert(id!=null);
					
					return new GroupAddress(id, (Class<? extends Organization>)orgaType, name);
				}
			}
			catch(Throwable _) {
				//
			}
		}
		
		throw new IllegalArgumentException(
				Locale.getString(
						AddressUtil.class, "INVALID_FORMAT")); //$NON-NLS-1$
	}

	/** Create the address for a role from the given informations.
	 * 
	 * @param group is the identifier in the group in which the role is played.
	 * @param role is the type of the role.
	 * @param player is the identifier of the player of the role. 
	 * @return the role address.
	 * @since 1.0
	 */
	public static RoleAddress createRoleAddress(GroupAddress group, Class<? extends Role> role, AgentAddress player) {
		return new RoleAddress(group, role, player);
	}

	/** Create the address for a role from the given informations.
	 * 
	 * @param group is the identifier in the group in which the role is played.
	 * @param role is the type of the role.
	 * @param player is the identifier of the player of the role. 
	 * @param name is the name of the group.
	 * @return the role address.
	 * @since 1.0
	 */
	public static RoleAddress createRoleAddress(GroupAddress group, Class<? extends Role> role, AgentAddress player, String name) {
		return new RoleAddress(group, role, player, name);
	}

	/** Replies a RoleAddress which corresponds to the specified string.
	 * <p>
	 * The parameter must be result of the {@link RoleAddress#toString()} function.
	 * 
	 * @param address is the name of the address 
	 * @return the address or <code>null</code> if it could be created.
	 * @throws IllegalArgumentException if the given address has invalid format or value.
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	public static RoleAddress createRoleAddress(String address)
	throws IllegalArgumentException {
		if (address==null)
			throw new IllegalArgumentException(
					Locale.getString(
							AddressUtil.class, "NULL_ADDRESS")); //$NON-NLS-1$
		StringTokenizer tok = new StringTokenizer(address, "||"); //$NON-NLS-1$
		if (tok.countTokens() == 4) {
			String name = tok.nextToken();
			String group = tok.nextToken();
			String role = tok.nextToken();
			String player = tok.nextToken();
	
			GroupAddress groupId = createGroupAddress(group);
			AgentAddress playerId = createAgentAddress(player);
			
			try {
				Class<?> roleType = Class.forName(role);
				
				if (Role.class.isAssignableFrom(roleType)) {
					return new RoleAddress(groupId, (Class<? extends Role>)roleType, playerId, name);
				}
			}
			catch(Throwable _) {
				//
			}
		}
		
		throw new IllegalArgumentException(
				Locale.getString(
						AddressUtil.class, "INVALID_FORMAT")); //$NON-NLS-1$
	}

}