/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.agent;

import java.util.Comparator;

/**
 * Compare to agents.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentComparator implements Comparator<Agent> {

	/** Singleton.
	 */
	public static final AgentComparator SINGLETON = new AgentComparator();
	
	/** 
	 */
	private AgentComparator() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Agent o1, Agent o2) {
		if (o1==o2) return 0;
		if (o1==null) return -1;
		if (o2==null) return 1;
		return o1.getAddress().compareTo(o2.getAddress());
	}

}
