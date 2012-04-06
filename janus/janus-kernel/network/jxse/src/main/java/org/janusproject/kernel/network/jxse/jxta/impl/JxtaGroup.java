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
package org.janusproject.kernel.network.jxse.jxta.impl;

import net.jxta.peergroup.PeerGroup;

import org.janusproject.kernel.network.jxse.jxta.JXTANetworkHandler;

/** Describes a JXTA group.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class JxtaGroup {

	private final PeerGroup peerGroup;
	private final JxtaGroup parentGroup;
	
	/** Network handler dedicated to JXTA peer network.
	 */
	protected final JXTANetworkHandler networkHandler;
	
	/**
	 * @param adapter
	 * @param peerGroup
	 * @param parent
	 */
	public JxtaGroup(JXTANetworkHandler adapter, PeerGroup peerGroup, JxtaGroup parent) {
		this.networkHandler = adapter;
		this.peerGroup = peerGroup;
		this.parentGroup = parent;
	}

	/**
	 * @return the peerGroup
	 */
	public PeerGroup getPeerGroup() {
		return this.peerGroup;
	}

	/**
	 * @return the parentGroup
	 */
	public JxtaGroup getParentGroup() {
		return this.parentGroup;
	}

}
