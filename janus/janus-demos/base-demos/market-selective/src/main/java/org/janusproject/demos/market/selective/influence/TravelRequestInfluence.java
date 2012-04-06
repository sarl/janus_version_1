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
package org.janusproject.demos.market.selective.influence;

import org.janusproject.demos.market.selective.travel.TravelDestination;
import org.janusproject.demos.market.selective.travel.TravelSelectionCritera;
import org.janusproject.kernel.agentsignal.Signal;

/**
 * Signal from client broker to provider broker.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TravelRequestInfluence extends Signal {
	
	private static final long serialVersionUID = 6196549042332964651L;

	/**
	 * @param source
	 * @param destination
	 * @param critera
	 */
	public TravelRequestInfluence(Object source, TravelDestination destination, TravelSelectionCritera critera) {
		super(source, TravelRequestInfluence.class.getName(), destination, critera);
	}
	
	/** Replies the destination.
	 * 
	 * @return the destination.
	 */
	public TravelDestination getDestination() {
		return (TravelDestination)getValues()[0];
	}

	/** Replies the selection critera.
	 * 
	 * @return the selection critera.
	 */
	public TravelSelectionCritera getCritera() {
		return (TravelSelectionCritera)getValues()[1];
	}

}
