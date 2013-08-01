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
package org.janusproject.kernel.agentmemory;

import java.util.EventListener;

/**
 * Listener on memory events.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface MemoryListener extends EventListener {

	/** Invoked when a knownledge has appeared or changed in the memory.
	 * <p>
	 * This function is invoked in three cases:
	 * <ol>
	 * <li>on the creation of a knowledge: the event has not an old value;</li>
	 * <li>on the update of a knowledge: the event has both an old value and a new value;</li>
	 * <li>on the removal of a knowledge: the event has not a new value.</li>
	 * </ol>
	 * 
	 * @param event describes the event.
	 */
	public void onKnownledgeChanged(MemoryEvent event);
	
}
