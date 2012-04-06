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

package org.janusproject.demos.simulation.preypredator.message;

import org.janusproject.kernel.message.AbstractContentMessage;

/** 
 * Message used to manage the prey predator game
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
public class GameMessage extends AbstractContentMessage<MoveDirection> {

	private static final long serialVersionUID = 4986318037822882125L;
	
	private final GameMessageType type;
	private final MoveDirection direction;
	
	/**
	 * @param direction is the move direction
	 */
	public GameMessage(MoveDirection direction) {
		this.type = GameMessageType.MOVE;
		this.direction = direction;
	}
	
	/**
	 * @param type is the type of action.
	 */
	public GameMessage(GameMessageType type) {
		this.type = type;
		this.direction = null;
	}

	/** Replies the type of the message.
	 * 
	 * @return the type of the message.
	 */
	public GameMessageType getType() {
		return this.type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MoveDirection getContent() {
		return getMove();
	}

	/**
	 * Replies move.
	 * 
	 * @return move.
	 */
	public MoveDirection getMove() {
		return (this.direction==null) ? MoveDirection.NONE : this.direction;
	}

	/** 
	 * Type of message.
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
	public enum GameMessageType {

		/** Presentation of a prey.
		 */
		I_AM_PREY,

		/** Presentation of a predator.
		 */
		I_AM_PREDATOR,

		/** Simulation may run.
		 */
		READY_TO_SIMULATE,

		/** Animat movement.
		 */
		MOVE,

		/** End of game message
		 */
		END_OF_GAME;
		
	}
	
}