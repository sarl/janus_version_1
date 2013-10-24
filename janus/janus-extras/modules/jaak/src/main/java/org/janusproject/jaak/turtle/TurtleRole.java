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
import java.util.Collections;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.Perceivable;
import org.janusproject.jaak.envinterface.perception.PerceivedTurtle;
import org.janusproject.jaak.envinterface.time.JaakTimeManager;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.core.Role;

/** This class permits to define a Turtle in Jaak.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class TurtleRole extends Role {

	/**
	 * Create a new turtle role.
	 */
	public TurtleRole() {
		super();
		addObtainCondition(new HasAllRequiredCapacitiesCondition(TurtleBodyCapacity.class));
	}
	
	/**
	 * Replies the time manager used by Jaak.
	 * 
	 * @return the time manager used by Jaak.
	 */
	protected final JaakTimeManager getJaakTimeManager() {
		return (JaakTimeManager)getTimeManager();
	}
	
	/** Replies if this turtle is binded to a body.
	 * 
	 * @return <code>true</code> if this turtle is binded to a body,
	 * otherwise <code>false</code>.
	 */
	protected final boolean hasBody() {
		try {
			CapacityContext cc = executeCapacityCall(TurtleBodyCapacity.class, TurtleBodyCapacityMethodName.HAS_BODY);
			return cc.getOutputValueAt(0, Boolean.class).booleanValue();
		}
		catch (Exception _) {
			return false;
		}
	}
	
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
	protected final void move(Vector2f direction, boolean changeHeading) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.MOVE,
					direction,
					Boolean.valueOf(changeHeading));
		}
		catch (Exception _) {
			//
		}
	}
	
	/** Move the turtle along the given direction and
	 * change the heading orientation if necessary.
	 * The norm of the <var>direction</var> is the number
	 * of cells to traverse.
	 * 
	 * @param dx is the X-component of the motion vector. 
	 * @param dy is the Y-component of the motion vector. 
	 * @param changeHeading is <code>true</code> to force
	 * the head to see at the same direction as the motion,
	 * otherwise <code>false</code>.
	 */
	protected final void move(float dx, float dy, boolean changeHeading) {
		move(new Vector2f(dx, dy), changeHeading);
	}

	/** Move the turtle along the given direction and
	 * change the heading orientation if necessary.
	 * The norm of the <var>direction</var> is the number
	 * of cells to traverse.
	 * 
	 * @param dx is the X-component of the motion vector. 
	 * @param dy is the Y-component of the motion vector. 
	 * @param changeHeading is <code>true</code> to force
	 * the head to see at the same direction as the motion,
	 * otherwise <code>false</code>.
	 */
	protected final void move(int dx, int dy, boolean changeHeading) {
		move(new Vector2f(dx, dy), changeHeading);
	}

	/** Move the turtle straight ahead about the given number 
	 * of cells.
	 * 
	 * @param cells is the count of cells to traverse.
	 */
	protected final void moveForward(int cells) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.MOVE_FORWARD,
					Integer.valueOf(cells));
		}
		catch (Exception _) {
			//
		}
	}
	
	/** Move the turtle backward about the given number 
	 * of cells.
	 * 
	 * @param cells is the count of cells to traverse.
	 */
	protected final void moveBackward(int cells) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.MOVE_BACKWARD,
					Integer.valueOf(cells));
		}
		catch (Exception _) {
			//
		}
	}

	/** Turn the head on the left of the turtle about the given
	 * number of radians.
	 * 
	 * @param radians is the rotation angle.
	 */
	protected final void turnLeft(float radians) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.TURN_LEFT,
					Float.valueOf(radians));
		}
		catch (Exception _) {
			//
		}
	}

	/** Turn the head on the right of the turtle about the given
	 * number of radians.
	 * 
	 * @param radians is the rotation angle.
	 */
	protected final void turnRight(float radians) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.TURN_RIGHT,
					Float.valueOf(radians));
		}
		catch (Exception _) {
			//
		}
	}

	/** Set the orientation of the turtle head 
	 * to the given angle according to the trigonometric
	 * circle.
	 * 
	 * @param radians is the orientation angle.
	 */
	protected final void setHeading(float radians) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.SET_HEADDING_ANGLE,
					Float.valueOf(radians));
		}
		catch (Exception _) {
			//
		}
	}
	
	/** Set the orientation of the turtle head 
	 * to the given direction.
	 * 
	 * @param direction is the new direction of the head.
	 */
	protected final void setHeading(Vector2f direction) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.SET_HEADING_DIRECTION,
					direction);
		}
		catch (Exception _) {
			//
		}
	}

	/** Replies the orientation of the turtle head
	 * in radians according to a trigonometric circle.
	 * 
	 * @return the orientation of the head in radians.
	 */
	protected final float getHeadingAngle() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_HEADING_ANGLE);
			return cc.getOutputValueAt(0, Float.class).floatValue();
		}
		catch (Exception _) {
			return Float.NaN;
		}
	}
	
	/** Replies the orientation of the turtle head.
	 * 
	 * @return the orientation of the head in radians.
	 */
	protected final Vector2f getHeadingVector() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_HEADING_DIRECTION);
			return cc.getOutputValueAt(0, Vector2f.class);
		}
		catch (Exception _) {
			return null;
		}
	}

	/** Notify the body that is should do nothing.
	 * <p>
	 * This method should be invoked when no other kind of
	 * influence is sent by the turtle.
	 */
	protected final void beIddle() {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.BE_IDDLE);
		}
		catch (Exception _) {
			//
		}
	}

	/** Put an object on the current cell of the environment.
	 * 
	 * @param object is the object to drop off.
	 * @see #pickUp(Class)
	 * @see #touchUp(Class)
	 */
	protected final void dropOff(EnvironmentalObject object) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.DROP_OFF,
					object);
		}
		catch (Exception _) {
			//
		}
	}

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
	protected final <T extends Perceivable> T pickUp(Class<T> type) {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.PICK_UP_FROM_TYPE,
					type);
			return type.cast(cc.getOutputValueAt(0, Perceivable.class));
		}
		catch (Exception _) {
			return null;
		}
	}
	
	/** Remove an object from the current environment cell.
	 * <p>
	 * Caution: the object is not immediately removed from the environment
	 * according to the influence mechanism.
	 * 
	 * @param object is the object to pick up.
	 * @see #dropOff(EnvironmentalObject)
	 * @see #touchUp(Class)
	 */
	protected final void pickUp(EnvironmentalObject object) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.PICK_UP_OBJECT,
					object);
		}
		catch (Exception _) {
			//
		}
	}
	
	/** Get an object from the current environment cell but do not
	 * remove it from the cell.
	 * 
	 * @param <T> is the type of the object to touch up.
	 * @param type is the type of the object to touch up.
	 * @return the touched up object.
	 * @see #pickUp(Class)
	 * @see #dropOff(EnvironmentalObject)
	 */
	protected final <T extends EnvironmentalObject> T touchUp(Class<T> type) {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.TOUCH_UP,
					type);
			return type.cast(cc.getOutputValueAt(0, EnvironmentalObject.class));
		}
		catch (Exception _) {
			return null;
		}
	}

	/** Replies x-coordinate of the position of the body.
	 * 
	 * @return the x-coordinate of the body.
	 */
	protected final int getX() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_X);
			return cc.getOutputValueAt(0, Integer.class).intValue();
		}
		catch (Exception _) {
			return -1;
		}
	}
	
	/** Replies y-coordinate of the position of the body.
	 * 
	 * @return the y-coordinate of the body.
	 */
	protected final int getY() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_Y);
			return cc.getOutputValueAt(0, Integer.class).intValue();
		}
		catch (Exception _) {
			return -1;
		}
	}
	
	/** Replies the position of the body.
	 * 
	 * @return the position of the body.
	 */
	protected final Point2i getPosition() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_POSITION);
			return cc.getOutputValueAt(0, Point2i.class);
		}
		catch (Exception _) {
			return null;
		}
	}
	
	/** Replies the all the perceptions of the body.
	 * 
	 * @return the collection of perceived objects.
	 */
	@SuppressWarnings("unchecked")
	protected final Collection<Perceivable> getPerception() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_PERCEPTION);
			return cc.getOutputValueAt(0, Collection.class);
		}
		catch (Exception _) {
			return Collections.emptyList();
		}
	}

	/** Replies the all the perceptions of the body of a given type.
	 * 
	 * @param <T> is the type of the objects to perceived.
	 * @param type is the type of the objects to perceived.
	 * @return the collection of perceived objects.
	 */
	@SuppressWarnings("unchecked")
	protected final <T extends Perceivable> Collection<T> getPerception(Class<T> type) {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_PERCEPTION_BY_TYPE,
					type);
			return cc.getOutputValueAt(0, Collection.class);
		}
		catch (Exception _) {
			return Collections.emptyList();
		}
	}

	/** Replies the first perception of the body of a given type.
	 * 
	 * @param <T> is the type of the objects to perceived.
	 * @param type is the type of the objects to perceived.
	 * @return the collection of perceived objects.
	 */
	protected final <T extends Perceivable> T getFirstPerception(Class<T> type) {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_FIRST_PERCEPTION_BY_TYPE,
					type);
			return cc.getOutputValueAt(0, type);
		}
		catch (Exception _) {
			return null;
		}
	}

	/** Replies the all the environmental objects perceived by the body.
	 * 
	 * @return the collection of perceived environmental objects.
	 */
	@SuppressWarnings("unchecked")
	protected final Collection<EnvironmentalObject> getPerceivedObjects() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_PERCEIVED_OBJECTS);
			return cc.getOutputValueAt(0, Collection.class);
		}
		catch (Exception _) {
			return Collections.emptyList();
		}
	}
	
	/** Replies the all the environmental objects perceived by the body.
	 * 
	 * @param <T> is the type of the objects to reply.
	 * @param type is the type of the objects to reply.
	 * @return the collection of perceived environmental objects.
	 */
	@SuppressWarnings("unchecked")
	protected final <T extends EnvironmentalObject> Collection<T> getPerceivedObjects(Class<T> type) {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_PERCEIVED_OBJECTS_OF_TYPE,
					type);
			return cc.getOutputValueAt(0, Collection.class);
		}
		catch (Exception _) {
			return Collections.emptyList();
		}
	}

	/** Replies the first environmental objects perceived by the body.
	 * 
	 * @param <T> is the type of the objects to reply.
	 * @param type is the type of the objects to reply.
	 * @return the first perceived object of the given type or <code>null</code>.
	 */
	protected final <T extends EnvironmentalObject> T getPerceivedObject(Class<T> type) {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_PERCEIVED_OBJECT_OF_TYPE,
					type);
			return cc.getOutputValueAt(0, type);
		}
		catch (Exception _) {
			return null;
		}
	}

	/** Replies all the environmental objects perceived by the body and which have the given
	 * semantic.
	 * 
	 * @param semantic is the semantic.
	 * @return the collection of perceived environmental objects.
	 */
	@SuppressWarnings("unchecked")
	protected final Collection<EnvironmentalObject> getPerceivedObjectsWithSemantic(Class<?> semantic) {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_PERCEIVED_OBJECTS_WITH_SEMANTIC,
					semantic);
			return cc.getOutputValueAt(0, Collection.class);
		}
		catch (Exception _) {
			return Collections.emptyList();
		}
	}

	/** Replies the first environmental objects perceived by the body and which has the given
	 * semantic.
	 * 
	 * @param semantic is the semantic. 
	 * @return the first perceived object of the given type or <code>null</code>.
	 */
	protected final EnvironmentalObject getPerceivedObjectWithSemantic(Class<?> semantic) {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_PERCEIVED_OBJECT_WITH_SEMANTIC,
					semantic);
			return cc.getOutputValueAt(0, EnvironmentalObject.class);
		}
		catch (Exception _) {
			return null;
		}
	}

	/** Replies the all the turtles perceived by the body.
	 * 
	 * @return the collection of perceived turtles.
	 */
	@SuppressWarnings("unchecked")
	protected final Collection<PerceivedTurtle> getPerceivedTurtles() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_PERCEIVED_TURTLES);
			return cc.getOutputValueAt(0, Collection.class);
		}
		catch (Exception _) {
			return Collections.emptyList();
		}
	}

	/** Replies if this body has perceived something.
	 * 
	 * @return <code>true</code> if something is perceived,
	 * otherwise <code>false</code>.
	 */
	protected final boolean hasPerception() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.HAS_PERCEPTION);
			return cc.getOutputValueAt(0, Boolean.class).booleanValue();
		}
		catch (Exception _) {
			return false;
		}
	}

	/** Replies if this body has perceived environmental objects.
	 * 
	 * @return <code>true</code> if an environmental
	 * object is perceived, otherwise <code>false</code>.
	 */
	protected final boolean hasPerceivedObject() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.HAS_PERCEIVED_OBJECT);
			return cc.getOutputValueAt(0, Boolean.class).booleanValue();
		}
		catch (Exception _) {
			return false;
		}
	}

	/** Replies if this body has perceived turtles.
	 * 
	 * @return <code>true</code> if a turtle
	 * is perceived, otherwise <code>false</code>.
	 */
	protected final boolean hasPerceivedTurtle() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.HAS_PERCEIVED_TURTLE);
			return cc.getOutputValueAt(0, Boolean.class).booleanValue();
		}
		catch (Exception _) {
			return false;
		}
	}
	
	/** Replies the instant speed of the turtle.
	 * 
	 * @return the instant speed of the turtle in cells per second.
	 */
	protected final float getSpeed() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_SPEED);
			return cc.getOutputValueAt(0, Number.class).floatValue();
		}
		catch (Exception _) {
			return 0f;
		}
	}
	
	/** Notifies the body that perceptions should be enabled or not.
	 * 
	 * @param enable is <code>true</code> to enable perception from the body,
	 * <code>false</code> to disable perceptions.
	 */
	protected final void setPerceptionEnable(boolean enable) {
		try {
			executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.SET_PERCEPTION_ENABLE,
					Boolean.valueOf(enable));
		}
		catch (Exception _) {
			//
		}
	}

	/** Replies if the perceptions are computed or not.
	 * 
	 * @return <code>true</code> if perceptions are enable from the body,
	 * <code>false</code> if they are disable.
	 */
	protected final boolean isPerceptionEnable() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.IS_PERCEPTION_ENABLE);
			return cc.getOutputValueAt(0, Boolean.class).booleanValue();
		}
		catch (Exception _) {
			return true;
		}
	}

	/** Replies the status of the application of the last motion influence
	 * sent by via this turtle body.
	 * 
	 * @return the application status of the last motion influence.
	 */
	public MotionInfluenceStatus getLastMotionInfluenceStatus() {
		try {
			CapacityContext cc = executeCapacityCall(
					TurtleBodyCapacity.class,
					TurtleBodyCapacityMethodName.GET_LAST_MOTION_INFLUENCE_STATUS);
			return cc.getOutputValueAt(0, MotionInfluenceStatus.class);
		}
		catch (Exception _) {
			return MotionInfluenceStatus.NOT_AVAILABLE;
		}
	}

}