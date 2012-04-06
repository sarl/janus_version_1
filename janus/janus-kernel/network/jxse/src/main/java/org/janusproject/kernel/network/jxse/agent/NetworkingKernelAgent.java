/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.kernel.network.jxse.agent;

/**
 * Agent that represents and run the kernel of the Janus platform.
 * <p>
 * If the kernel agent is suicidable, it means that it will stop its execution
 * if no more other agent exists. If the kernel agent is not suicidable, it
 * will persist even if no more other agent is registered.
 * <p>
 * This kernel agent supports networking.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated see {@link JxtaJxseKernelAgent}
 */
@Deprecated
public class NetworkingKernelAgent extends JxtaJxseKernelAgent {

	private static final long serialVersionUID = -230244525219678609L;

	/**
	 */
	private NetworkingKernelAgent() {
		super(null, null, null, null, null, null);
	}

}