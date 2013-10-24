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

import java.util.Collection;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.Perceivable;
import org.janusproject.jaak.envinterface.perception.PerceivedTurtle;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.capacity.CapacityPrototype;

/** This class defines a capacity to access to turtle body.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@CapacityPrototype(
		fixedParameters={TurtleBodyCapacityMethodName.class,Object.class,Object.class},
		optionalParameterAt=1,
		variableOutput = Object.class
)
public interface TurtleBodyCapacity extends Capacity {

	/** Replies if this turtle is binded to a body.
	 * 
	 * @return <code>true</code> if this turtle is binded to a body,
	 * otherwise <code>false</code>.
	 */
	public boolean hasBody();
	
	/** Move the turtle along the given direction and
	 * change the heading orientation if necessary.
	 * The norm of the <var>direction</var> is the number
	 * of cells to traverse.
	 * 
	 * @param direction is the motion direction.
	 * @param changeHeading is <code>true</code> to force
	 * the head to see at the same direction as the motion,
	 * otherwise <code>false</code>.
	 */
	public void move(Vector2f direction, boolean changeHeading);
	
	/** Move the turtle straight ahead about the given number 
	 * of cells.
	 * 
	 * @param cells is the count of cells to traverse.
	 */
	public void moveForward(int cells);
	
	/** Move the turtle backward about the given number 
	 * of cells.
	 * 
	 * @param cells is the count of cells to traverse.
	 */
	public void moveBackward(int cells);

	/** Turn the head on the left of the turtle about the given
	 * number of radians.
	 * 
	 * @param radians is the rotation angle.
	 */
	public void turnLeft(float radians);

	/** Turn the head on the right of the turtle about the given
	 * number of radians.
	 * 
	 * @param radians is the rotation angle.
	 */
	public void turnRight(float radians);

	/** Set the orientation of the turtle head 
	 * to the given angle according to the trigonometric
	 * circle.
	 * 
	 * @param radians is the orientation angle.
	 */
	public void setHeading(float radians);
	
	/** Set the orientation of the turtle head 
	 * to the given direction.
	 * 
	 * @param direction is the new direction of the head.
	 */
	public void setHeading(Vector2f direction);

	/** Replies the orientation of the turtle head
	 * in radians according to a trigonometric circle.
	 * 
	 * @return the orientation of the head in radians.
	 */
	public float getHeadingAngle();
	
	/** Replies the orientation of the turtle head.
	 * 
	 * @return the orientation of the head in radians.
	 */
	public Vector2f getHeadingVector();

	/** Notify the body that is should do nothing.
	 * <p>
	 * This method should be invoked when no other kind of
	 * influence is sent by the turtle.
	 */
	public void beIddle();

	/** Put an object on the current cell of the environment.
	 * 
	 * @param object is the object to drop off.
	 * @see #pickUp(Class)
	 * @see #touchUp(Class)
	 */
	public void dropOff(EnvironmentalObject object);

	/** Remove an object from the current environment cell.
	 * <p>
	 * Caution: the object is not immediately removed from the environment
	 * according to the influence mechanism.
	 * 
	 * @param <T> is the type of the object to pick up.
	 * @param type is the type of the object to pick up.
	 * @return the picked up object.
	 * @see #dropOff(EnvironmentalObject)
	 * @see #touchUp(Class)
	 */
	public <T extends Perceivable> T pickUp(Class<T> type);
	
	/** Remove an object from the current environment cell.
	 * <p>
	 * Caution: the object is not immediately removed from the environment
	 * according to the influence mechanism.
	 * 
	 * @param object is object to pick up.
	 * @see #dropOff(EnvironmentalObject)
	 * @see #touchUp(Class)
	 */
	public void pickUp(EnvironmentalObject object);

	/** Get an object from the current environment cell but do not
	 * remove it from the cell.
	 * 
	 * @param <T> is the type of the object to touch up.
	 * @param type is the type of the object to touch up.
	 * @return the touched up object.
	 * @see #pickUp(Class)
	 * @see #dropOff(EnvironmentalObject)
	 */
	public <T extends EnvironmentalObject> T touchUp(Class<T> type);

	/** Replies x-coordinate of the position of the body.
	 * 
	 * @return the x-coordinate of the body.
	 */
	public int getX();
	
	/** Replies y-coordinate of the position of the body.
	 * 
	 * @return the y-coordinate of the body.
	 */
	public int getY();
	
	/** Replies the position of the body.
	 * 
	 * @return the position of the body.
	 */
	public Point2i getPosition();
	
	/** Replies the all the perceptions of the body.
	 * 
	 * @return the collection of perceived objects.
	 */
	public Collection<Perceivable> getPerception();

	/** Replies the all the perceptions of the body of a given type.
	 * 
	 * @param <T> is the type of the objects to perceived.
	 * @param type is the type of the objects to perceived.
	 * @return the collection of perceived objects.
	 */
	public <T extends Perceivable> Collection<T> getPerception(Class<T> type);

	/** Replies the first perception of the body of a given type.
	 * 
	 * @param <T> is the type of the objects to perceived.
	 * @param type is the type of the objects to perceived.
	 * @return the collection of perceived objects.
	 */
	public <T extends Perceivable> T getFirstPerception(Class<T> type);

	/** Replies the all the environmental objects perceived by the body.
	 * 
	 * @return the collection of perceived environmental objects.
	 */
	public Collection<EnvironmentalObject> getPerceivedObjects();
	
	/** Replies the all the environmental objects perceived by the body.
	 * 
	 * @param <T> is the type of the objects to reply.
	 * @param type is the type of the objects to reply.
	 * @return the collection of perceived environmental objects.
	 */
	public <T extends EnvironmentalObject> Collection<T> getPerceivedObjects(Class<T> type);

	/** Replies the first environmental objects perceived by the body.
	 * 
	 * @param <T> is the type of the objects to reply.
	 * @param type is the type of the objects to reply.
	 * @return the perceived environmental object of the given type or <code>null</code> if none.
	 */
	public <T extends EnvironmentalObject> T getPerceivedObject(Class<T> type);

	/** Replies all the environmental objects perceived by the body and which have the given
	 * semantic.
	 * 
	 * @param semantic is the semantic.
	 * @return the collection of perceived environmental objects.
	 */
	public Collection<EnvironmentalObject> getPerceivedObjectsWithSemantic(Class<?> semantic);

	/** Replies the first environmental objects perceived by the body and which has the given
	 * semantic.
	 * 
	 * @param semantic is the semantic. 
	 * @return the first perceived object of the given type or <code>null</code>.
	 */
	public EnvironmentalObject getPerceivedObjectWithSemantic(Class<?> semantic);

	/** Replies the all the turtles perceived by the body.
	 * 
	 * @return the collection of perceived turtles.
	 */
	public Collection<PerceivedTurtle> getPerceivedTurtles();

	/** Replies if this body has perceived something.
	 * 
	 * @return <code>true</code> if something is perceived,
	 * otherwise <code>false</code>.
	 */
	public boolean hasPerception();

	/** Replies if this body has perceived environmental objects.
	 * 
	 * @return <code>true</code> if an environmental
	 * object is perceived, otherwise <code>false</code>.
	 */
	public boolean hasPerceivedObject();

	/** Replies if this body has perceived turtles.
	 * 
	 * @return <code>true</code> if a turtle
	 * is perceived, otherwise <code>false</code>.
	 */
	public boolean hasPerceivedTurtle();
	
	/** Replies the instant speed of the turtle.
	 * 
	 * @return the instant speed of the turtle in cells per second.
	 */
	public float getSpeed();

	/** Notifies the body that perceptions should be enabled or not.
	 * 
	 * @param enable is <code>true</code> to enable perception from the body,
	 * <code>false</code> to disable perceptions.
	 */
	public void setPerceptionEnable(boolean enable);

	/** Replies if the perceptions are computed or not.
	 * 
	 * @return <code>true</code> if perceptions are enable from the body,
	 * <code>false</code> if they are disable.
	 */
	public boolean isPerceptionEnable();

	/** Replies the status of the application of the last motion influence
	 * sent by via this turtle body.
	 * 
	 * @return the application status of the last motion influence.
	 */
	public MotionInfluenceStatus getLastMotionInfluenceStatus();

}