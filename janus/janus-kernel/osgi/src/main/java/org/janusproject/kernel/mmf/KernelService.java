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

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.credential.Credentials;
import org.janusproject.kernel.status.Status;

/**
 * The Kernel Service enables Janus Modules to interact with Janus Kernel.
 * <br/>
 * Certain operations that are considered critical must passed to the {@link KernelAuthority}
 * for its authorization. If approved the service executes the kernel's operation.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface KernelService extends Kernel {

	/**
	 * The {@link KernelAuthority}. The authority is set by the kernel when
	 * starting the application.
	 * 
	 * @return the kernel authority
	 */
	public KernelAuthority getKernelAuthority();

	/**
	 * Requests the local kernel to be stopped. The request is evaluated by the
	 * {@link KernelAuthority} of the application.
	 * 
	 * @param credentials
	 * @return the status of the operation
	 */
	public Status requestKernelStop(Credentials credentials);
	

	/**
	 * Requests to start a Janus Module.
	 * The request must be authorized by the IKernelAuthority
	 * 
	 * @param module the module to start.
	 * @param credentials the credentials to authorize the actions
	 * @return {@link Status#isSuccess()} == true if the module correctly started. false otherwise.
	 */
	public Status startJanusModule(JanusModule module,
			Credentials credentials);
	
	/**
	 * Requests to stop a Janus Module.
	 * The request must be authorized by the IKernelAuthority
	 * 
	 * @param module the module to stop.
	 * @param credentials the credentials to authorize the actions
	 * @return {@link Status#isSuccess()} == true if the module correctly stopped. false otherwise.
	 */
	public Status stopJanusModule(JanusModule module,
			Credentials credentials);

}
