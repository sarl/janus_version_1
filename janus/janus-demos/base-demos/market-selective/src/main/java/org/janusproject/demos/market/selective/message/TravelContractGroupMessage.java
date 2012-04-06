/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.market.selective.message;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.message.ObjectMessage;

/**
 * Message that contains a contract group.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TravelContractGroupMessage extends ObjectMessage {

	private static final long serialVersionUID = 812286688821571799L;
	
	private final AgentAddress provider;
	
	/**
	 * @param provider
	 * @param contractGroup
	 */
	public TravelContractGroupMessage(AgentAddress provider, GroupAddress contractGroup) {
		super(contractGroup);
		this.provider = provider;
	}
	
	/** Replies the provider address.
	 * 
	 * @return the provider address.
	 */
	public AgentAddress getProvider() {
		return this.provider;
	}
		
	/** Replies the group in which travel contract may be passed.
	 * 
	 * @return the group address.
	 */
	public GroupAddress getTravelGroup() {
		return (GroupAddress)getContent();
	}

}
