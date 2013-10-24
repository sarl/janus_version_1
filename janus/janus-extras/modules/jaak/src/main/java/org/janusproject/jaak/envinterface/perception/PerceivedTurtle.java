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
package org.janusproject.jaak.envinterface.perception;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.kernel.address.AgentAddress;

/** This class defines a perceived turtle.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PerceivedTurtle extends AbstractPerceivable {

	private static final long serialVersionUID = -758584526816638042L;
	
	private final AgentAddress turtle;
	private final float speed;
	private final float angle;
	
	/**
	 * @param perceivedTurtle is the identifier of the perceived turtle.
	 * @param observer is the position of the observer.
	 * @param observed is the position of the perceived turtle.
	 * @param observedSpeed is the speed of the observed turtle.
	 * @param observedOrientation is the orientation angle of the observed turtle.
	 * @param semantic is the semantic associated to the turtle body.
	 */
	public PerceivedTurtle(AgentAddress perceivedTurtle, Point2i observer, Point2i observed, 
			float observedSpeed, float observedOrientation, Object semantic) {
		super();
		assert(perceivedTurtle!=null);
		assert(observer!=null);
		assert(observed!=null);
		this.turtle = perceivedTurtle;
		this.position.set(observed);
		this.speed = observedSpeed;
		this.angle = observedOrientation;
		this.semantic = semantic;
	}

	/** Replies the identifier of this perceived object.
	 * 
	 * @return the identifier of this perceived object, or <code>null</code>
	 * if the perceived object has no address.
	 */
	public AgentAddress getIdentity() {
		return this.turtle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isTurtle() {
		return true;
	}
	
	/**
	 * Replies the instant speed of the perceived turtle.
	 * 
	 * @return the instant speed in cell per second.
	 */
	public float getSpeed() {
		return this.speed;
	}

	/**
	 * Replies the current orientation of the perceived turtle.
	 * 
	 * @return the current orientation angle of the perceived turtle.
	 */
	public float getHeadingAngle() {
		return this.angle;
	}

	/**
	 * Replies the current orientation of the perceived turtle.
	 * 
	 * @return the current orientation vector of the perceived turtle.
	 */
	public Vector2f getHeadingVector() {
		return Vector2f.toOrientationVector(this.angle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isBurrow() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isObstacle() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSubstance() {
		return false;
	}

}