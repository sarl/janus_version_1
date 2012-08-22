/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.ecoresolution.agent;


import java.util.Set;

import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoRelation;
import org.janusproject.ecoresolution.sm.EcoState;
import org.janusproject.kernel.channels.Channel;

/** Channel dedicated to eco-agents.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface EcoChannel extends Channel {

	/** Add listener on this channel.
	 * 
	 * @param listener
	 */
	public void addEcoChannelListener(EcoChannelListener listener);
	
	/** Remove listener on this channel.
	 * 
	 * @param listener
	 */
	public void removeEcoChannelListener(EcoChannelListener listener);

	/** Replies the knowledge of the associated eco-agent.
	 * 
	 * @return the knowledge of the associated eco-agent.
	 */
	public Set<EcoRelation> getAcquaintances();
	
	/** Replies the state of the associated eco-agent.
	 * 
	 * @return the state of the associated eco-agent.
	 */
	public EcoState getEcoState();

	/** Replies the identity of the associated eco-agent.
	 * 
	 * @return the identity of the associated eco-agent.
	 */
	public EcoIdentity getEcoEntity();

}