/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2011 Janus Core Developers
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
package org.janusproject.kernel.mmf;

import org.janusproject.kernel.status.Status;

/**
 * A Janus Module is a self-contained MultiAgent System that can be started upon
 * request of the {@link JanusApplication}
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JanusModule {

	/**
	 * Replies a human readable name of the module.
	 * 
	 * @return the name of the OSGi module.
	 */
	public String getName();

	/**
	 * Replies a human-readable description of what the module does. If the
	 * module has different possible parameters, this description should explain
	 * them.
	 * 
	 * @return the description of the OSGi module.
	 */
	public String getDescription();

	/**
	 * Replies whether this module is currently running.
	 * 
	 * @return <code>true</code> if the OSGi module is running,
	 * otherwise <code>false</code>.
	 */
	public boolean isRunning();
	/**
	 * Requests to start this module. Necessary agents should started here.
	 * 
	 * @param kernel
	 *            the kernel service
	 * @return {@link Status#isSuccess()}==<code>true</code> is successfully
	 *         started, <code>false</code> otherwise.
	 */
	public Status start(KernelService kernel);

	/**
	 * Requests to stop this module.
	 * 
	 * @param kernel
	 *            the kernel service
	 * @return {@link Status#isSuccess()}==<code>true</code> is successfully
	 *         stopped, <code>false</code> otherwise.
	 */
	public Status stop(KernelService kernel);
}
