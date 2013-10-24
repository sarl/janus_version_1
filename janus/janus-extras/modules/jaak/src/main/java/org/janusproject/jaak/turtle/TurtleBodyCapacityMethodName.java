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
package org.janusproject.jaak.turtle;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;

/** Supported method supported by the {@link TurtleBodyCapacityMethodName}.
 * Each member of this enumeration corresponds to a function name
 * in {@link TurtleBodyCapacityMethodName}.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum TurtleBodyCapacityMethodName {

	/** Replies if this turtle is binded to a body.
	 * @see TurtleBodyCapacity#hasBody()
	 */
	HAS_BODY,
	
	/** Move the turtle along the given direction and
	 * change the heading orientation if necessary.
	 * The norm of the <var>direction</var> is the number
	 * of cells to traverse.
	 * @see TurtleBodyCapacity#move(Vector2f, boolean)
	 */
	MOVE,
	
	/** Move the turtle straight ahead about the given number 
	 * of cells.
	 * @see TurtleBodyCapacity#moveForward(int)
	 */
	MOVE_FORWARD,
	
	/** Move the turtle backward about the given number 
	 * of cells.
	 * @see TurtleBodyCapacity#moveBackward(int)
	 */
	MOVE_BACKWARD,

	/** Turn the head on the left of the turtle about the given
	 * number of radians.
	 * @see TurtleBodyCapacity#turnLeft(float)
	 */
	TURN_LEFT,

	/** Turn the head on the right of the turtle about the given
	 * number of radians.
	 * @see TurtleBodyCapacity#turnRight(float)
	 */
	TURN_RIGHT,

	/** Set the orientation of the turtle head 
	 * to the given angle according to the trigonometric
	 * circle.
	 * @see TurtleBodyCapacity#setHeading(float)
	 */
	SET_HEADDING_ANGLE,
	
	/** Set the orientation of the turtle head 
	 * to the given direction.
	 * @see TurtleBodyCapacity#setHeading(Vector2f)
	 */
	SET_HEADING_DIRECTION,

	/** Replies the orientation of the turtle head
	 * in radians according to a trigonometric circle.
	 * @see TurtleBodyCapacity#getHeadingAngle()
	 */
	GET_HEADING_ANGLE,
	
	/** Replies the orientation of the turtle head.
	 * @see TurtleBodyCapacity#getHeadingVector()
	 */
	GET_HEADING_DIRECTION,

	/** Notify the body that is should do nothing.
	 * @see TurtleBodyCapacity#beIddle()
	 */
	BE_IDDLE,

	/** Put an object on the current cell of the environment.
	 * @see TurtleBodyCapacity#dropOff(EnvironmentalObject)
	 */
	DROP_OFF,

	/** Get an object from the current of the environment
	 * and remote it from the cell.
	 * @see TurtleBodyCapacity#pickUp(Class)
	 */
	PICK_UP_FROM_TYPE,
	
	/** Get an object from the current of the environment
	 * and remote it from the cell.
	 * @see TurtleBodyCapacity#pickUp(EnvironmentalObject)
	 */
	PICK_UP_OBJECT,

	/** Get an object from the current of the environment
	 * but do not remote it from the cell.
	 * @see TurtleBodyCapacity#touchUp(Class)
	 */
	TOUCH_UP,

	/** Replies x-coordinate of the position of the body.
	 * @see TurtleBodyCapacity#getX()
	 */
	GET_X,
	
	/** Replies y-coordinate of the position of the body.
	 * @see TurtleBodyCapacity#getY()
	 */
	GET_Y,
	
	/** Replies the position of the body.
	 * @see TurtleBodyCapacity#getPosition()
	 */
	GET_POSITION,
	
	/** Replies the all the perceptions of the body.
	 * @see TurtleBodyCapacity#getPerception()
	 */
	GET_PERCEPTION,

	/** Replies the all the perceptions of the body of the given type.
	 * @see TurtleBodyCapacity#getPerception(Class)
	 */
	GET_PERCEPTION_BY_TYPE,

	/** Replies the all the perceptions of the body of the given type.
	 * @see TurtleBodyCapacity#getFirstPerception(Class)
	 */
	GET_FIRST_PERCEPTION_BY_TYPE,

	/** Replies the all the environmental objects perceived by the body.
	 * @see TurtleBodyCapacity#getPerceivedObjects()
	 */
	GET_PERCEIVED_OBJECTS,
	
	/** Replies the all the environmental objects perceived by the body.
	 * @see TurtleBodyCapacity#getPerceivedObjects(Class)
	 */
	GET_PERCEIVED_OBJECTS_OF_TYPE,

	/** Replies the first environmental objects perceived by the body.
	 * @see TurtleBodyCapacity#getPerceivedObjects(Class)
	 */
	GET_PERCEIVED_OBJECT_OF_TYPE,

	/** Replies the first environmental object perceived by the body and with the given semantic.
	 * @see TurtleBodyCapacity#getPerceivedObjectWithSemantic(Class)
	 */
	GET_PERCEIVED_OBJECT_WITH_SEMANTIC,

	/** Replies all the environmental objects perceived by the body and with the given semantic.
	 * @see TurtleBodyCapacity#getPerceivedObjectsWithSemantic(Class)
	 */
	GET_PERCEIVED_OBJECTS_WITH_SEMANTIC,

	/** Replies the all the turtles perceived by the body.
	 * @see TurtleBodyCapacity#getPerceivedTurtles()
	 */
	GET_PERCEIVED_TURTLES,

	/** Replies if this body has perceived something.
	 * @see TurtleBodyCapacity#hasPerception()
	 */
	HAS_PERCEPTION,

	/** Replies if this body has perceived environmental objects.
	 * @see TurtleBodyCapacity#hasPerceivedObject()
	 */
	HAS_PERCEIVED_OBJECT,

	/** Replies if this body has perceived turtles.
	 * @see TurtleBodyCapacity#hasPerceivedTurtle()
	 */
	HAS_PERCEIVED_TURTLE,

	/** Replies the instant speed of the turtle.
	 * @see TurtleBodyCapacity#getSpeed()
	 */
	GET_SPEED,
	
	/** Replies if the perceptions are computed or not.
	 * @see TurtleBodyCapacity#isPerceptionEnable()
	 */
	IS_PERCEPTION_ENABLE,
	
	/** Notifies the body that perceptions should be enabled or not.
	 * @see TurtleBodyCapacity#setPerceptionEnable(boolean)
	 */
	SET_PERCEPTION_ENABLE,
	
	/** Replies the status of the last motion influence.
	 * @see TurtleBodyCapacity#getLastMotionInfluenceStatus()
	 */
	GET_LAST_MOTION_INFLUENCE_STATUS;

}