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

	/** {@inheritDoc}
	 */
	@Override
	public void setName(String iname) {
		this.name = iname;
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		StringBuilder b = new StringBuilder();
		String n = getName();
		if (n!=null && !n.isEmpty()) {
			b.append(n);
		}
		b.append("::"); //$NON-NLS-1$
		b.append(getUUID());
		return b.toString();
	}

}