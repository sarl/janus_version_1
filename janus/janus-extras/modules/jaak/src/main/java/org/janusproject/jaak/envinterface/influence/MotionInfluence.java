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
package org.janusproject.jaak.envinterface.influence;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.JaakObject;

/** This class defines a motion influence.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MotionInfluence extends Influence {

	private final Vector2f linearMotion;
	private float angularMotion;
	private final JaakObject moveObject;

	/**
	 * @param emitter is the identifier of the influence emitter.
	 * @param linearMotion is the linear motion to apply. The vector
	 * describes the motion direction and the length of the vector
	 * is the number of cells to traverse.
	 * @param angularMotion is the rotational motion to apply.
	 */
	public MotionInfluence(TurtleBody emitter, Vector2f linearMotion, float angularMotion) {
		super(emitter);
		assert(linearMotion!=null);
		assert(emitter!=null);
		this.moveObject = emitter;
		this.linearMotion = linearMotion;
		this.angularMotion = angularMotion;
	}
	
	/**
	 * @param emitter is the identifier of the influence emitter.
	 * @param angularMotion is the rotational motion to apply.
	 */
	public MotionInfluence(TurtleBody emitter, float angularMotion) {
		super(emitter);
		assert(emitter!=null);
		this.moveObject = emitter;
		this.linearMotion = new Vector2f();
		this.angularMotion = angularMotion;
	}

	/**
	 * @param emitter is the identifier of the influence emitter.
	 * @param linearMotion is the linear motion to apply. The vector
	 * describes the motion direction and the length of the vector
	 * is the number of cells to traverse.
	 */
	public MotionInfluence(TurtleBody emitter, Vector2f linearMotion) {
		super(emitter);
		assert(linearMotion!=null);
		assert(emitter!=null);
		this.moveObject = emitter;
		this.linearMotion = linearMotion;
		this.angularMotion = 0f;
	}

	/**
	 * @param emitter is the identifier of the influence emitter.
	 */
	public MotionInfluence(TurtleBody emitter) {
		super(emitter);
		assert(emitter!=null);
		this.moveObject = emitter;
		this.linearMotion = new Vector2f();
		this.angularMotion = 0f;
	}

	/**
	 * @param emitter is the identifier of the influence emitter.
	 * @param object is the object to move.
	 * @param linearMotion is the linear motion to apply. The vector
	 * describes the motion direction and the length of the vector
	 * is the number of cells to traverse.
	 * @param angularMotion is the rotational motion to apply.
	 */
	public MotionInfluence(TurtleBody emitter, EnvironmentalObject object, Vector2f linearMotion, float angularMotion) {
		super(emitter);
		assert(linearMotion!=null);
		assert(object!=null);
		this.moveObject = object;
		this.linearMotion = linearMotion;
		this.angularMotion = angularMotion;
	}
	
	/**
	 * @param emitter is the identifier of the influence emitter.
	 * @param object is the object to move.
	 * @param angularMotion is the rotational motion to apply.
	 */
	public MotionInfluence(TurtleBody emitter, EnvironmentalObject object, float angularMotion) {
		super(emitter);
		assert(object!=null);
		this.moveObject = object;
		this.linearMotion = new Vector2f();
		this.angularMotion = angularMotion;
	}

	/**
	 * @param emitter is the identifier of the influence emitter.
	 * @param object is the object to move.
	 * @param linearMotion is the linear motion to apply. The vector
	 * describes the motion direction and the length of the vector
	 * is the number of cells to traverse.
	 */
	public MotionInfluence(TurtleBody emitter, EnvironmentalObject object, Vector2f linearMotion) {
		super(emitter);
		assert(linearMotion!=null);
		assert(object!=null);
		this.moveObject = object;
		this.linearMotion = linearMotion;
		this.angularMotion = 0f;
	}

	/**
	 * @param emitter is the identifier of the influence emitter.
	 * @param object is the object to move.
	 */
	public MotionInfluence(TurtleBody emitter, EnvironmentalObject object) {
		super(emitter);
		assert(object!=null);
		this.moveObject = object;
		this.linearMotion = new Vector2f();
		this.angularMotion = 0f;
	}

	/** Set the linear motion to apply. The vector
	 * describes the motion direction and the length of the vector
	 * is the number of cells to traverse.
	 * 
	 * @param x is the x-component of the motion vector.
	 * @param y is the y-component of the motion vector.
	 */
	public void setLinearMotion(float x, float y) {
		this.linearMotion.set(x,y);
	}

	/** Set the rotational motion to apply.
	 * 
	 * @param angularMotion is the rotational motion to apply.
	 */
	public void setAngularMotion(float angularMotion) {
		this.angularMotion = angularMotion;
	}

	/** Fill the given vector with the linear motion vector
	 * to apply.
	 * 
	 * @param motion is the vector which is filled.
	 */
	public void getLinearMotion(Vector2f motion) {
		assert(motion!=null);
		motion.set(this.linearMotion);
	}
	
	/** Replies the linear motion
	 * vector to apply (not a copy).
	 * 
	 * @return the linear motion vector to apply.
	 */
	public Vector2f getLinearMotion() {
		return this.linearMotion;
	}

	/** Replies the x-component of the linear motion
	 * vector to apply.
	 * 
	 * @return the x-component of the linear motion
	 * vector to apply.
	 */
	public float getLinearMotionX() {
		return this.linearMotion.getX();
	}
		
	/** Replies the y-component of the linear motion
	 * vector to apply.
	 * 
	 * @return the y-component of the linear motion
	 * vector to apply.
	 */
	public float getLinearMotionY() {
		return this.linearMotion.getY();
	}
	
	/** Replies the angular motion angle to apply.
	 * 
	 * @return the angular motion angle to apply.
	 */
	public float getAngularMotion() {
		return this.angularMotion;
	}
	
	/** Replies the moved object.
	 * 
	 * @return the moved object.
	 */
	public JaakObject getMovedObject() {
		return this.moveObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(getEmitter().getTurtleId().toString());
		buffer.append(": linear=("); //$NON-NLS-1$
		buffer.append(getLinearMotionX());
		buffer.append(';');
		buffer.append(getLinearMotionY());
		buffer.append("); angular="); //$NON-NLS-1$
		buffer.append(getAngularMotion());
		return buffer.toString();
	}
	
}