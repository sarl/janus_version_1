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

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.DistantCRIOContextHandler;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * Handles relations with known distant kernels.
 * <p>
 * The Kernel strongly relays on this implementation to deliver messages to
 * agents hosted by other kernels.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.4
 */
public interface DistantKernelHandler extends DistantCRIOContextHandler {

	/**
	 * Replies all the known remote kernels.
	 * 
	 * @return the remote kernels.
	 */
	public SizedIterator<AgentAddress> getRemoteKernels();

}
