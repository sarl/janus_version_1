/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2009 Stephane GALLAND
 * Copyright (C) 2010 Janus Core Developers
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

package org.janusproject.demos.simulation.preypredator.organization;

import org.janusproject.demos.simulation.preypredator.message.GameMessage;
import org.janusproject.demos.simulation.preypredator.message.MoveDirection;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.util.random.RandomNumber;

/** 
 * Abstract implementation of an animat role.
 * <p>
 * Copied from <a href="http://www.arakhne.org/tinymas/index.html">TinyMAS Platform Demos</a>
 * and adapted for Janus platform.
 * <p>
 * Thanks to Julia Nikolaeva, aka. <a href="mailto:flameia@zerobias.com">Flameia</a>, for the icons.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AnimatRole extends Role {

	/**
	 */
	public AnimatRole() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		AgentAddress rp = getPlayer();
		if (rp!=null) return rp.getName();
		return null;
	}

	/** Move the agent on the given direction
	 * 
	 * @param direction  
	 * @return <code>true</code> if the mouvement was
	 * sent, otherwise <code>false</code>
	 */	
	protected boolean moveTo(MoveDirection direction) {
		try {
			sendMessage(Terrain.class, new GameMessage(direction));
			//setProbe("LAST_MOVE", direction); //$NON-NLS-1$
			return true;
		}
		catch(Throwable e) {
			error(e.getLocalizedMessage());
			return false;
		}
	}
	
	/** Random selection of a movement direction.
	 * 
	 * @param allowNoMove indicates if {@link MoveDirection#NONE} is allowed.
	 * @return a random movement direction.
	 */
	protected static MoveDirection computeMove(boolean allowNoMove) {
		int tries = 0;
		MoveDirection[] values = MoveDirection.values();
		MoveDirection selected;
		do {
			int direction = RandomNumber.nextInt(values.length);
			selected = values[direction];
			++tries;
		}
		while ((!allowNoMove)&&(selected==MoveDirection.NONE)&&(tries<10));
		return selected;
	}

}