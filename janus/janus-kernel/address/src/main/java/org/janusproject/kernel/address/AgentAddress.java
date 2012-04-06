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
package org.janusproject.kernel.address;

import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * This is the address of an agent in the kernel community.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AgentAddress extends AbstractAddress {

	private static final long serialVersionUID = 1231281906219059565L;
	
	/**
	 * Constant which is used to unset AgentAddress name.
	 */
	public static final String NO_NAME = ""; //$NON-NLS-1$
	
	/**
	 * Agent's intelligeble name. This name does not influence in any way the
	 * agents are addressed.
	 */
	private String name;

	/** Replies a AgentAddress which corresponds to the specified string.
	 * <p>
	 * The parameter must be result of the {@link #toString()} function.
	 * 
	 * @param address is the name of the address 
	 * @return the address or <code>null</code> if it could be created.
	 * @throws UnknownHostException if the host name in the given address is unknown (from DNS point of view).
	 * @throws IllegalArgumentException if the given address has invalid format or value.
	 * @deprecated See <code>AddressUtil.createAgentAddress(String)</code>
	 * @see "{@code AddressUtil.createAgentAddress(String)}"
	 */
	@Deprecated
	public static AgentAddress valueOf(String address)
	throws UnknownHostException, IllegalArgumentException {
		if (address==null)
			throw new IllegalArgumentException();
		StringTokenizer tok = new StringTokenizer(address, "::"); //$NON-NLS-1$
		if (tok.countTokens() != 2)
			throw new IllegalArgumentException();

		String name = tok.nextToken();
		String ids = tok.nextToken();

		tok = new StringTokenizer(ids, "@"); //$NON-NLS-1$

		UUID id = UUID.fromString(tok.nextToken());
		assert(id!=null);

		return new AgentAddress(id,name) {
			private static final long serialVersionUID = 2052897212713875693L;
		};
	}

	/** Create a agent address.
	 * 
	 * @param id is the identifier of the agent.
	 * @param name is the name of the address/agent.
	 */
	public AgentAddress(UUID id, String name) {
		super(id);
		this.name = (name==null) ? NO_NAME : name;
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/** Set the name of this address.
	 * 
	 * @param iname
	 */
	public void setName(String iname) {
		this.name = iname;
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName() + "::" + getUUID(); //$NON-NLS-1$
	}

}