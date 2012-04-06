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
package org.janusproject.kernel.agent;

import java.util.EventListener;

/**
 * This interface is used by <code>Kernels</code>
 * to create an instance of a kernel agent.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface KernelAgentFactory {

	/** Invoked to obtain a new instance of a kernel agent.
	 * 
	 * @param commitSuicide indicates if the kernel agent may kill itself.
	 * when no more agent is inside the kernel. If this parameter is <code>null</code>,
	 * the default value will be used for this flag.
	 * @param activator is the activator to be used by the kernel agent.
	 * @param startUpListener is a listener on kernel events which may be added at startup.
	 * @param applicationName is the name of the application supported by the new kernel.
	 * @return the new kernel agent instance.
	 * @throws Exception if something wrong append
	 */
	public KernelAgent newInstance(Boolean commitSuicide, AgentActivator activator, EventListener startUpListener, String applicationName) throws Exception;

}
