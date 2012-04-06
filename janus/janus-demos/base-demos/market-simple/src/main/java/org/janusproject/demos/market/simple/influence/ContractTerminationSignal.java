/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010 Janus Core Developers
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
package org.janusproject.demos.market.simple.influence;

import org.janusproject.kernel.agentsignal.Signal;

/**
 * Signal that is used to notifies about the acceptant of a contract.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ContractTerminationSignal extends Signal {

	private static final long serialVersionUID = 5524530567384961541L;

	/**
	 * @param source
	 * @param contractDescription
	 */
	public ContractTerminationSignal(Object source, Object contractDescription) {
		super(source, ContractTerminationSignal.class.getName(), contractDescription);
	}
	
	/** Replies the description of the contract.
	 * 
	 * @return the description of the contract.
	 */
	public Object getContractDescription() {
		return getValues()[0];
	}
	
}
