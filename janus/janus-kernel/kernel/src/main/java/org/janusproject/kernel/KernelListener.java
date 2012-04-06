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
package org.janusproject.kernel;

import java.util.EventListener;

/**
 * Listener on kernel events.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface KernelListener extends EventListener {
	
	/** Invoked when an agent was launched.
	 * 
	 * @param event is describing the event.
	 * @see #kernelAgentLaunched(KernelEvent)
	 */
	public void agentLaunched(KernelEvent event);
	
	/** Invoked when an agent was killed, excluding the kernel agent.
	 * 
	 * @param event is describing the event.
	 * @see #kernelAgentKilled(KernelEvent)
	 */
	public void agentKilled(KernelEvent event);

	/** Invoked when an uncatched exception has been thrown.
	 * 
	 * @param error is the uncatched error.
	 * @return <code>true</code> if the given error may be sent to logger,
	 * <code>false</code> otherwise.
	 */
	public boolean exceptionUncatched(Throwable error);

	/** Invoked when a kernel agent was launched.
	 * 
	 * @param event is describing the event.
	 * @see #agentLaunched(KernelEvent)
	 */
	public void kernelAgentLaunched(KernelEvent event);

	/** Invoked when a kernel agent was killed.
	 * 
	 * @param event is describing the event.
	 * @see #agentKilled(KernelEvent)
	 */
	public void kernelAgentKilled(KernelEvent event);

}
