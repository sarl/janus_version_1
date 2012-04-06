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
package org.janusproject.demos.network.androidchat;

import java.util.Comparator;

import org.janusproject.demos.network.januschat.ChatUtil;
import org.janusproject.kernel.address.AgentAddress;

/** Comparator of agent addresses according
 * to the chat-room member heuristic.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MemberComparator implements Comparator<AgentAddress> {

	/** Singleton of the comparator.
	 */
	public static MemberComparator SINGLETON = new MemberComparator();
	
	/**
	 */
	protected MemberComparator() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(AgentAddress o1, AgentAddress o2) {
		if (o1==o2) return 0;
		if (o1==null) return Integer.MIN_VALUE;
		if (o2==null) return Integer.MAX_VALUE;
		String n1 = ChatUtil.getChatterName(o1);
		String n2 = ChatUtil.getChatterName(o2);
		if (n1!=null && !n1.isEmpty()
			&& n2!=null && !n2.isEmpty()) {
			return n1.compareToIgnoreCase(n2);
		}
		return o1.compareTo(o2);
	}
	
}

