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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.demos.jaak.pacman.PacManGame;
import org.janusproject.demos.jaak.pacman.channel.PlayerDirection;
import org.janusproject.demos.jaak.pacman.semantic.EvadeGhostSemantic;
import org.janusproject.demos.jaak.pacman.semantic.PacManSemantic;
import org.janusproject.demos.jaak.pacman.semantic.PursueGhostSemantic;
import org.janusproject.demos.jaak.pacman.semantic.SearchGhostSemantic;
import org.janusproject.demos.jaak.pacman.semantic.SuperPacManSemantic;
import org.janusproject.demos.jaak.pacman.semantic.WallSemantic;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.body.TurtleBodyFactory;
import org.janusproject.jaak.envinterface.frustum.CrossTurtleFrustum;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.envinterface.perception.Perceivable;
import org.janusproject.jaak.turtle.Turtle;
import org.janusproject.kernel.agent.AgentActivationPrototype;
import org.janusproject.kernel.util.random.RandomNumber;

/** This class defines a ghost.
 * <p>
 * The ghost move randomly until it does not perceive the pacman.
 * When it is perceiving the pacman, it pursue it.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@AgentActivationPrototype
public class Ghost extends Turtle {

	private static final long serialVersionUID = 2685829907892602342L;

	private PlayerDirection lastDirection = null;
	
	/**
	 * @param ghostNumber is the number of the ghost.
	 */
	public Ghost(int ghostNumber) {
		setName(Locale.getString(Ghost.class, "NAME", ghostNumber)); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TurtleBody createTurtleBody(TurtleBodyFactory factory) {
		TurtleBody body = factory.createTurtleBody(getAddress(), new CrossTurtleFrustum(PacManGame.GHOST_VISION_LENGTH));
		if (body!=null) {
			body.setSemantic(SearchGhostSemantic.SEMANTIC);
		}
		return body;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void turtleBehavior() {
		if (hasBody()) {
			Collection<Perceivable> perceptions = getPerception();
	
			MotionInfluenceStatus mis = getLastMotionInfluenceStatus();
			if (mis!=null && mis==MotionInfluenceStatus.NO_MOTION) {
				this.lastDirection = null;
			}
			
			List<PlayerDirection> availableDirections = new LinkedList<PlayerDirection>();
			for(PlayerDirection d : PlayerDirection.values()) {
				availableDirections.add(d);
			}
			
			Object sem;
			PlayerDirection d;
			PlayerDirection pacman = null;
			PlayerDirection superPacman = null;
			
			for(Perceivable p : perceptions) {
				sem = p.getSemantic();
				d = PlayerDirection.makeDirection(getPosition(), p.getPosition());
				if (sem instanceof PacManSemantic) {
					pacman = d;
					superPacman = null;
				}
				else if (sem instanceof SuperPacManSemantic) {
					pacman = null;
					superPacman = d;
				}
				else if (sem instanceof WallSemantic && getPosition().distanceL1(p.getPosition())<=1) {
					availableDirections.remove(d);
				}
			}
	
			if (superPacman!=null) {
				superPacman = superPacman.opposite();
				move(superPacman.dx, superPacman.dy, true);
				setSemantic(EvadeGhostSemantic.SEMANTIC);
			}
			else if (pacman!=null) {
				move(pacman.dx, pacman.dy, true);
				setSemantic(PursueGhostSemantic.SEMANTIC);
			}
			else if (!availableDirections.isEmpty()) {
				if (this.lastDirection!=null
				 && availableDirections.contains(this.lastDirection)) {
					if (availableDirections.size()>1
					 && RandomNumber.nextFloat()<.3f) {
						d = availableDirections.get(RandomNumber.nextInt(availableDirections.size()));
					}
					else {
						d = this.lastDirection;
					}
				}
				else {
					d = availableDirections.get(RandomNumber.nextInt(availableDirections.size()));
				}
				this.lastDirection = d;
				move(d.dx, d.dy, true);
				setSemantic(SearchGhostSemantic.SEMANTIC);
			}
			else {
				setSemantic(SearchGhostSemantic.SEMANTIC);
				beIddle();
			}
		}
	}
	
}