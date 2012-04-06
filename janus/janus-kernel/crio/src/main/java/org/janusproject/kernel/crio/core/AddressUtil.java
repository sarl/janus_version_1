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

import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.UUID;

import org.arakhne.vmutil.locale.Locale;
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
	 * The parameter must be result of the {@link #toString()} function.
	 * 
	 * @param address is the name of the address 
	 * @return the address or <code>null</code> if it could be created.
	 * @throws UnknownHostException if the host name in the given address is unknown (from DNS point of view).
	 * @throws IllegalArgumentException if the given address has invalid format or value.
	 */
	public static AgentAddress createAgentAddress(String address)
	throws UnknownHostException, IllegalArgumentException {
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

}