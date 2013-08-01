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
import org.janusproject.demos.simulation.preypredator.message.GameMessage.GameMessageType;
import org.janusproject.kernel.crio.role.RoleActivationPrototype;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** 
 * Predator in a wild world.
 * Predator tries to catch preys.
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
@RoleActivationPrototype(
		fixedParameters={}
)
public class Predator extends AnimatRole {

	private State state;
	
	/**
	 */
	public Predator() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		this.state = State.PRESENTATION;
		return StatusFactory.ok(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		switch(this.state) {
		case PRESENTATION:
			broadcastMessage(Terrain.class, new GameMessage(GameMessageType.I_AM_PREDATOR));
			this.state = State.WAIT_START;
			break;
		case WAIT_START:
			for(Message message : getMailbox()) {
				if (message instanceof GameMessage) {
					GameMessage gmsg = (GameMessage)message;
					switch(gmsg.getType()) {
					case END_OF_GAME:
						leaveMe();
						break;
					case READY_TO_SIMULATE:
						this.state = State.PURSUE;
						break;
					case I_AM_PREDATOR:
					case I_AM_PREY:
					case MOVE:
					default:
						// ignore message
					}
				}
			}
			break;
		case PURSUE:
		default:
			boolean eog = false;
			
			// Search for END-OF-GAME message
			for(Message message : getMailbox()) {
				if (message instanceof GameMessage) {
					GameMessage gmsg = (GameMessage)message;
					if (gmsg.getType()==GameMessageType.END_OF_GAME) {
						eog = true;
					}
				}
			}
	
			if (eog) {
				leaveMe();
			}
			else {
				moveTo(computeMove(true));
			}
		}
			
		return StatusFactory.ok(this);
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum State {

		PRESENTATION,
		
		WAIT_START,
		
		PURSUE;
	}

}