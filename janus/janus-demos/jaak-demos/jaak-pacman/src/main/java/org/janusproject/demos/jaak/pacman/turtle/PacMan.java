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
package org.janusproject.demos.jaak.pacman.turtle;

import java.util.Set;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.jaak.pacman.PacManGame;
import org.janusproject.demos.jaak.pacman.channel.Player;
import org.janusproject.demos.jaak.pacman.channel.PlayerDirection;
import org.janusproject.demos.jaak.pacman.semantic.PacManSemantic;
import org.janusproject.demos.jaak.pacman.semantic.PillSemantic;
import org.janusproject.demos.jaak.pacman.semantic.SuperPacManSemantic;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.body.TurtleBodyFactory;
import org.janusproject.jaak.envinterface.frustum.CrossTurtleFrustum;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.turtle.Turtle;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.channels.Channel;

/** This class defines an interactive pacman.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype
public class PacMan extends Turtle {

	private static final long serialVersionUID = 5307824475544378243L;

	private PlayerDirection requestedDirection = null;
	private long endOfSuperPacMan = -1;
	
	/**
	 */
	public PacMan() {
		setName(Locale.getString(PacMan.class, "NAME")); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TurtleBody createTurtleBody(TurtleBodyFactory factory) {
		TurtleBody body = factory.createTurtleBody(getAddress(), new CrossTurtleFrustum(1));
		if (body!=null) {
			body.setSemantic(PacManSemantic.SEMANTIC);
		}
		return body;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<? extends Channel>> getSupportedChannels() {
		Set<Class<? extends Channel>> channels = super.getSupportedChannels();
		assert(channels!=null);
		channels.add(Player.class);
		return channels;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Channel> C getChannel(Class<C> type, Object... arguments) {
		if (Player.class.equals(type)) {
			return type.cast(new PlayerInteraction());
		}
		return super.getChannel(type, arguments);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void turtleBehavior() {
		if (hasBody()) {
			PlayerDirection direction;
			synchronized(PacMan.this) {
				direction = this.requestedDirection;
			}
			if (direction!=null) {
				move(direction.dx, direction.dy, true);
			}
			EnvironmentalObject pill = pickUpWithSemantic(PillSemantic.SEMANTIC);
			long current = System.currentTimeMillis();
			if (pill!=null) {
				setSemantic(SuperPacManSemantic.SEMANTIC);
				this.endOfSuperPacMan = current + PacManGame.SUPER_PACMAN_DURATION*1000;
			}
			else {
				if (current<=this.endOfSuperPacMan) {
					setSemantic(SuperPacManSemantic.SEMANTIC);
				}
				else {
					setSemantic(PacManSemantic.SEMANTIC);
					this.endOfSuperPacMan = -1;
				}
			}
		}
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PlayerInteraction implements Player {

		/**
		 */
		public PlayerInteraction() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Address getChannelOwner() {
			return getAddress();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void movePlayer(PlayerDirection direction) {
			synchronized(PacMan.this) {
				PacMan.this.requestedDirection = direction;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public long getRemainPowerTime() {
			long f = PacMan.this.endOfSuperPacMan - System.currentTimeMillis();
			if (f<0) return 0;
			return f;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Point2i getPosition() {
			return new Point2i(PacMan.this.getPosition());
		}

	}
	
}