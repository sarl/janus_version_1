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

import org.janusproject.kernel.credential.Credentials;
import org.janusproject.kernel.status.Status;

/**
 * The Kernel authority evaluates if different kernel operations should be
 * performed or not.
 * 
 * <b>IMPORTANT:</b> If the application does not provide a kernel authority
 * <b>ALL</b> operations will be approved.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface KernelAuthority {

	/**
	 * Checks the kernel operation should be authorized a request using the
	 * {@link Credentials}
	 * @param operation The requested operation
	 * @param credentials
	 * @param param Optional parameters for the operation.
	 * @return the status of the operation
	 */
	public Status authorizeKernelOperation(
			KernelOperation operation,
			Credentials credentials,
			Object param);

}
