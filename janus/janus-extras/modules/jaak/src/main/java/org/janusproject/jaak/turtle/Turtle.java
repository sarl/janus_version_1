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
package org.janusproject.jaak.turtle;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.body.TurtleBodyFactory;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.Perceivable;
import org.janusproject.jaak.envinterface.perception.PerceivedTurtle;
import org.janusproject.jaak.envinterface.time.JaakTimeManager;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.channels.Channel;
import org.janusproject.kernel.channels.ChannelInteractable;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

/** This class permits to define a Turtle in Jaak.
 * <p>
 * One of the major difference between a turtle and an agent is
 * the name of the function where the entity behavior must be
 * written. For agents, {@link #live()} should be implemented.
 * But for turtles, {@link #turtleBehavior()} is replacing
 * the agent's <code>live</code> function. This difference
 * is due to the fact that if a turtle does not produce anyu
 * influence in its behavior, it must be marked as "iddle"
 * by invoking {@link #beIddle()}. To avoid dead-lock problem
 * in simulation loop, turtle is implemented according to the following
 * rules:
 * <ol>
 * <li>{@link #live()} is the living function invoked by the Janus kernel. It is
 * implemented and not overidable to ensure that the turtles are marked as "iddle"
 * when they do not generate any influence. <code>live()</code> invokes the abstract
 * function named {@link #turtleBehavior()} to proceed the turtle's behavior.</li>
 * <li>{@link #turtleBehavior()} is introduced as an abstract function. Semantically,
 * it partly replaces the {@link #live()} from <code>Agent</code> which is no more
 * overridable by turtle subclasses.</li>
 * </ol>
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class Turtle extends Agent implements ChannelInteractable {

	private static final long serialVersionUID = -4839953888164321347L;
	
	private TurtleBody body = null;
	
	/**
	 * Create a new non-compound turtle
	 * 
	 * @param commitSuicide indicates if this turtle is able to commit suicide or not
	 */
	protected Turtle(Boolean commitSuicide) {
		super(commitSuicide);
		getCapacityContainer().addCapacity(new TurtleBodyCapacityImplementation());
	}

	/**
	 * Create a new non-compound turtle
	 */
	protected Turtle() {
		super();
		getCapacityContainer().addCapacity(new TurtleBodyCapacityImplementation());
	}

	/**
	 * Create a new non-compound turtle.
	 * 
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this turtle is able to commit suicide or not
	 */
	protected Turtle(CapacityContainer capacityContainer, Boolean commitSuicide) {
		super(capacityContainer, commitSuicide);
		getCapacityContainer().addCapacity(new TurtleBodyCapacityImplementation());
	}

	/**
	 * Create a new non-compound turtle.
	 * 
	 * @param capacityContainer is the container of capacities.
	 */
	protected Turtle(CapacityContainer capacityContainer) {
		super(capacityContainer);
		getCapacityContainer().addCapacity(new TurtleBodyCapacityImplementation());
	}

	/**
	 * Create a new non-compound turtle
	 * 
	 * @param address is a precomputed address to give to this turtle.
	 */
	protected Turtle(AgentAddress address) {
		super(address);
		getCapacityContainer().addCapacity(new TurtleBodyCapacityImplementation());
	}

	/**
	 * Create a new non-compound turtle
	 * 
	 * @param address is a precomputed address to give to this turtle.
	 * @param commitSuicide indicates if this turtle is able to commit suicide or not
	 */
	protected Turtle(AgentAddress address, Boolean commitSuicide) {
		super(address, commitSuicide);
		getCapacityContainer().addCapacity(new TurtleBodyCapacityImplementation());
	}

	/**
	 * Create a new non-compound turtle.
	 * 
	 * @param address is a precomputed address to give to this turtle.
	 * @param capacityContainer is the container of capacities.
	 * @param commitSuicide indicates if this turtle is able to commit suicide or not
	 */
	protected Turtle(AgentAddress address, CapacityContainer capacityContainer, Boolean commitSuicide) {
		super(address, capacityContainer, commitSuicide);
		getCapacityContainer().addCapacity(new TurtleBodyCapacityImplementation());
	}

	/**
	 * Create a new non-compound turtle.
	 * 
	 * @param address is a precomputed address to give to this turtle.
	 * @param capacityContainer is the container of capacities.
	 */
	protected Turtle(AgentAddress address, CapacityContainer capacityContainer) {
		super(address, capacityContainer);
		getCapacityContainer().addCapacity(new TurtleBodyCapacityImplementation());
	}
	
	/** Invoked to bind a body to a turtle.
	 * 
	 * @param turtle is the turtle for which a body should be created.
	 * @param factory is the factory to use to create the body was never set before.
	 * @return the body, never <code>null</code>.
	 */
	public static TurtleBody bindTurtleBody(Turtle turtle, TurtleBodyFactory factory) {
		assert(turtle!=null && factory!=null);
		synchronized(turtle) {
			if (turtle.body==null) {
				turtle.body = turtle.createTurtleBody(factory);
			}
			return turtle.body;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Status live() {
		Status s = super.live();
		if (s.isSuccess()) {
			turtleBehavior();
			if (this.body!=null && !this.body.hasInfluences()) {
				beIddle();
			}
			return StatusFactory.ok(this);
		}
		return s;
	}
	
	/**
	 * Run one step of the turtle behavior.
	 * Basically, the turtle is assumed to
	 * perceive, decide the next action and
	 * generate a corresponding influence.
	 * If the turtle's behavior does not generate
	 * any influence, {@link #beIddle()} is
	 * automatically invoked.
	 */
	protected void turtleBehavior() {
		//
	}

	/** Replies the body associated to this turtle.
	 * 
	 * @return the body, or <code>null</code> if no body was associated to this turtle.
	 */
	protected synchronized TurtleBody getTurtleBody() {
		return this.body;
	}

	/** Replies the time manager used by Jaak.
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
	public final synchronized boolean hasBody() {
		return this.body!=null;
	}
	
	/** Invoked when a body may be created for this turtle.
	 * 
	 * @param factory is the factory to use to instance the body.
	 * @return the body, never <code>null</code>.
	 */
	protected abstract TurtleBody createTurtleBody(TurtleBodyFactory factory);
	
	/** Replies the status of the application of the last motion influence
	 * sent by via this turtle body.
	 * 
	 * @return the application status of the last motion influence.
	 */
	protected final MotionInfluenceStatus getLastMotionInfluenceStatus() {
		assert(this.body!=null);
		return this.body.getLastMotionInfluenceStatus();
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
		assert(this.body!=null);
		this.body.move(direction, changeHeading);
	}
	
	/** Move the turtle along the given direction and
	 * change the heading orientation if necessary.
	 * The norm of the <var>direction</var> is the number
	 * of cells to traverse.
	 * 
	 * @param dx is X-component of the the motion vector
	 * @param dy is Y-component of the the motion vector
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
	 * @param dx is X-component of the the motion vector
	 * @param dy is Y-component of the the motion vector
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
		assert(this.body!=null);
		this.body.moveForward(cells);
	}
	
	/** Move the turtle backward about the given number 
	 * of cells.
	 * 
	 * @param cells is the count of cells to traverse.
	 */
	protected final void moveBackward(int cells) {
		assert(this.body!=null);
		this.body.moveForward(cells);
	}

	/** Turn the head on the left of the turtle about the given
	 * number of radians.
	 * 
	 * @param radians is the rotation angle.
	 */
	protected final void turnLeft(float radians) {
		assert(this.body!=null);
		this.body.turnLeft(radians);
	}

	/** Turn the head on the right of the turtle about the given
	 * number of radians.
	 * 
	 * @param radians is the rotation angle.
	 */
	protected final void turnRight(float radians) {
		assert(this.body!=null);
		this.body.turnRight(radians);
	}

	/** Set the orientation of the turtle head 
	 * to the given angle according to the trigonometric
	 * circle.
	 * 
	 * @param radians is the orientation angle.
	 */
	protected final void setHeading(float radians) {
		assert(this.body!=null);
		this.body.setHeading(radians);
	}
	
	/** Set the orientation of the turtle head 
	 * to the given direction.
	 * 
	 * @param direction is the new direction of the head.
	 */
	protected final void setHeading(Vector2f direction) {
		assert(this.body!=null);
		this.body.setHeading(direction);
	}

	/** Replies the orientation of the turtle head
	 * in radians according to a trigonometric circle.
	 * 
	 * @return the orientation of the head in radians.
	 */
	public final float getHeadingAngle() {
		assert(this.body!=null);
		return this.body.getHeadingAngle();
	}
	
	/** Replies the orientation of the turtle head.
	 * 
	 * @return the orientation of the head in radians.
	 */
	public final Vector2f getHeadingVector() {
		assert(this.body!=null);
		return this.body.getHeadingVector();
	}

	/** Notify the body that is should do nothing.
	 * <p>
	 * This method should be invoked when no other kind of
	 * influence is sent by the turtle.
	 */
	protected final void beIddle() {
		assert(this.body!=null);
		this.body.beIddle();
	}

	/** Put an object on the current cell of the environment.
	 * 
	 * @param object is the object to drop off.
	 * @see #pickUp(Class)
	 * @see #touchUp(Class)
	 */
	protected final void dropOff(EnvironmentalObject object) {
		assert(this.body!=null);
		this.body.dropOff(object);
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
		assert(this.body!=null);
		return this.body.pickUp(type);
	}
	
	/** Remove an object with the given semantic from the current environment cell.
	 * <p>
	 * Caution: the object is not immediately removed from the environment
	 * according to the influence mechanism.
	 * 
	 * @param semantic is the searched semantic.
	 * @return the picked up object or <code>null</code>.
	 * @see #dropOff(EnvironmentalObject)
	 * @see #touchUp(Class)
	 */
	protected final EnvironmentalObject pickUpWithSemantic(Object semantic) {
		assert(this.body!=null);
		return this.body.pickUpWithSemantic(semantic);
	}

	/** Remove an object from the current environment cell.
	 * <p>
	 * Caution: the object is not immediately removed from the environment
	 * according to the influence mechanism.
	 * 
	 * @param object is the object to remove from the cell.
	 */
	protected final void pickUp(EnvironmentalObject object) {
		assert(this.body!=null);
		this.body.pickUp(object);
	}

	/** Get an object from the current environment cell but do not
	 * remove it from the cell.
	 * 
	 * @param <T> is the type of the object to touch up.
	 * @param type is the type of the object to touch up.
	 * @return the touched up object.
	 * @see #dropOff(EnvironmentalObject)
	 * @see #pickUp(Class)
	 */
	protected final <T extends EnvironmentalObject> T touchUp(Class<T> type) {
		assert(this.body!=null);
		return this.body.touchUp(type);
	}

	/** Get an object with the given semantic from the current environment cell but do not
	 * remove it from the cell.
	 * 
	 * @param semantic is the searched semantic.
	 * @return the touched up object or <code>null</code>.
	 * @see #dropOff(EnvironmentalObject)
	 * @see #pickUp(Class)
	 */
	protected final EnvironmentalObject touchUpWithSemantic(Object semantic) {
		assert(this.body!=null);
		return this.body.touchUpWithSemantic(semantic);
	}

	/** Replies x-coordinate of the position of the body.
	 * 
	 * @return the x-coordinate of the body.
	 */
	public final int getX() {
		assert(this.body!=null);
		return this.body.getX();
	}
	
	/** Replies y-coordinate of the position of the body.
	 * 
	 * @return the y-coordinate of the body.
	 */
	public final int getY() {
		assert(this.body!=null);
		return this.body.getY();
	}
	
	/** Replies the position of the body.
	 * 
	 * @return the position of the body.
	 */
	public final Point2i getPosition() {
		assert(this.body!=null);
		return this.body.getPosition();
	}
	
	/** Replies the all the perceptions of the body.
	 * 
	 * @return the collection of perceived objects.
	 */
	protected final Collection<Perceivable> getPerception() {
		assert(this.body!=null);
		return this.body.getPerception();
	}

	/** Replies the all the perceptions of the body of a given type.
	 * 
	 * @param <T> is the type of the objects to perceived.
	 * @param type is the type of the objects to perceived.
	 * @return the collection of perceived objects.
	 */
	protected final <T extends Perceivable> Collection<T> getPerception(Class<T> type) {
		assert(this.body!=null);
		return this.body.getPerception(type);
	}

	/** Replies the first perception of the body of a given type.
	 * 
	 * @param <T> is the type of the objects to perceived.
	 * @param type is the type of the objects to perceived.
	 * @return the collection of perceived objects.
	 */
	protected final <T extends Perceivable> T getFirstPerception(Class<T> type) {
		assert(this.body!=null);
		return this.body.getFirstPerception(type);
	}

	/** Replies the all the environmental objects perceived by the body.
	 * 
	 * @return the collection of perceived environmental objects.
	 */
	protected final Collection<EnvironmentalObject> getPerceivedObjects() {
		assert(this.body!=null);
		return this.body.getPerceivedObjects();
	}
	
	/** Replies the all the environmental objects perceived by the body.
	 * 
	 * @param <T> is the type of the objects to reply.
	 * @param type is the type of the objects to reply.
	 * @return the collection of perceived environmental objects.
	 */
	protected final <T extends EnvironmentalObject> Collection<T> getPerceivedObjects(Class<T> type) {
		assert(this.body!=null);
		assert(type!=null);
		List<T> list = new LinkedList<T>();
		for(EnvironmentalObject o : this.body.getPerceivedObjects()) {
			if (o!=null && type.isInstance(o)) {
				list.add(type.cast(o));
			}
		}
		return list;
	}

	/** Replies all the environmental objects perceived by the body and which have the given
	 * semantic.
	 * 
	 * @param semantic is the semantic.
	 * @return the collection of perceived environmental objects.
	 */
	protected final Collection<EnvironmentalObject> getPerceivedObjectsWithSemantic(Class<?> semantic) {
		assert(this.body!=null);
		assert(semantic!=null);
		List<EnvironmentalObject> list = new LinkedList<EnvironmentalObject>();
		for(EnvironmentalObject o : this.body.getPerceivedObjects()) {
			if (o!=null && o.getSemantic()!=null && semantic.isInstance(o.getSemantic())) {
				list.add(o);
			}
		}
		return list;
	}

	/** Replies the first environmental objects perceived by the body and which has the given
	 * semantic.
	 * 
	 * @param semantic is the semantic. 
	 * @return the first perceived object of the given type or <code>null</code>.
	 */
	protected final EnvironmentalObject getPerceivedObjectWithSemantic(Class<?> semantic) {
		assert(this.body!=null);
		assert(semantic!=null);
		for(EnvironmentalObject o : this.body.getPerceivedObjects()) {
			if (o!=null && o.getSemantic()!=null && semantic.isInstance(o.getSemantic())) {
				return o;
			}
		}
		return null;
	}

	/** Replies the all the turtles perceived by the body.
	 * 
	 * @return the collection of perceived turtles.
	 */
	protected final Collection<PerceivedTurtle> getPerceivedTurtles() {
		assert(this.body!=null);
		return this.body.getPerceivedTurtles();
	}

	/** Replies if this body has perceived something.
	 * 
	 * @return <code>true</code> if something is perceived,
	 * otherwise <code>false</code>.
	 */
	protected final boolean hasPerception() {
		assert(this.body!=null);
		return this.body.hasPerception();
	}

	/** Replies if this body has perceived environmental objects.
	 * 
	 * @return <code>true</code> if an environmental
	 * object is perceived, otherwise <code>false</code>.
	 */
	protected final boolean hasPerceivedObject() {
		assert(this.body!=null);
		return this.body.hasPerceivedObject();
	}

	/** Replies if this body has perceived turtles.
	 * 
	 * @return <code>true</code> if a turtle
	 * is perceived, otherwise <code>false</code>.
	 */
	protected final boolean hasPerceivedTurtle() {
		assert(this.body!=null);
		return this.body.hasPerceivedTurtle();
	}

	/** Replies the semantic of the body.
	 * 
	 * @return the semantic of the body.
	 */
	protected final Object getSemantic() {
		assert(this.body!=null);
		return this.body.getSemantic();
	}

	/** Set the semantic of the body.
	 * 
	 * @param semantic is the semantic of the body.
	 */
	protected final void setSemantic(Object semantic) {
		assert(this.body!=null);
		if (semantic!=null) {
			this.body.setSemantic(semantic);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Channel> C getChannel(Class<C> type, Object... arguments) {
		if (TurtleBindingChannel.class.equals(type) && !hasBody()) {
			return type.cast(new BindingChannel(this));
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<? extends Channel>> getSupportedChannels() {
		Set<Class<? extends Channel>> channels = new HashSet<Class<? extends Channel>>();
		if (!hasBody()) {
			channels.add(TurtleBindingChannel.class);
		}
		return channels;
	}

	/** Channel to allow binding 
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class BindingChannel implements TurtleBindingChannel {

		private Turtle turtle;
		
		/**
		 * @param turtle is the turtle to bind to.
		 */
		public BindingChannel(Turtle turtle) {
			this.turtle = turtle;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TurtleBody bindBody(TurtleBodyFactory bodyFactory) {
			Turtle t = this.turtle;
			this.turtle = null;
			if (t!=null) {
				return Turtle.bindTurtleBody(t, bodyFactory);
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Address getChannelOwner() {
			if (this.turtle==null) return null;
			return this.turtle.getAddress();
		}
		
	}
	
	/** This class permits to define a Turtle in Jaak.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class TurtleBodyCapacityImplementation
	extends CapacityImplementation
	implements TurtleBodyCapacity {

		/**
		 */
		public TurtleBodyCapacityImplementation() {
			super();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void call(CapacityContext call) throws Exception {
			TurtleBodyCapacityMethodName methodName = call.getInputValueAt(0, TurtleBodyCapacityMethodName.class);
			switch(methodName) {
			case BE_IDDLE:
				beIddle();
				return;
			case DROP_OFF:
				dropOff(call.getInputValueAt(1, EnvironmentalObject.class));
				return;
			case GET_HEADING_ANGLE:
				call.setOutputValues(Float.valueOf(getHeadingAngle()));
				return;
			case GET_HEADING_DIRECTION:
				call.setOutputValues(getHeadingVector());
				return;
			case GET_PERCEPTION_BY_TYPE:
				call.setOutputValues(getPerception(call.getInputValueAt(1,Class.class)));
				return;
			case GET_FIRST_PERCEPTION_BY_TYPE:
				call.setOutputValues(getFirstPerception(call.getInputValueAt(1,Class.class)));
				return;
			case GET_PERCEIVED_OBJECTS:
				call.setOutputValues(getPerceivedObjects());
				return;
			case GET_PERCEIVED_OBJECTS_OF_TYPE:
				call.setOutputValues(getPerceivedObjects(call.getInputValueAt(1,Class.class)));
				return;
			case GET_PERCEIVED_OBJECT_OF_TYPE:
				call.setOutputValues(getPerceivedObject(call.getInputValueAt(1,Class.class)));
				return;
			case GET_PERCEIVED_OBJECT_WITH_SEMANTIC:
				call.setOutputValues(getPerceivedObjectWithSemantic(call.getInputValueAt(1,Class.class)));
				return;
			case GET_PERCEIVED_OBJECTS_WITH_SEMANTIC:
				call.setOutputValues(getPerceivedObjectsWithSemantic(call.getInputValueAt(1,Class.class)));
				return;
			case GET_PERCEIVED_TURTLES:
				call.setOutputValues(getPerceivedTurtles());
				return;
			case GET_PERCEPTION:
				call.setOutputValues(getPerception());
				return;
			case GET_POSITION:
				call.setOutputValues(getPosition());
				return;
			case GET_X:
				call.setOutputValues(Integer.valueOf(getX()));
				return;
			case GET_Y:
				call.setOutputValues(Integer.valueOf(getY()));
				return;
			case HAS_BODY:
				call.setOutputValues(hasBody());
				return;
			case HAS_PERCEIVED_OBJECT:
				call.setOutputValues(Boolean.valueOf(hasPerceivedObject()));
				return;
			case HAS_PERCEIVED_TURTLE:
				call.setOutputValues(Boolean.valueOf(hasPerceivedTurtle()));
				return;
			case HAS_PERCEPTION:
				call.setOutputValues(Boolean.valueOf(hasPerception()));
				return;
			case GET_SPEED:
				call.setOutputValues(Float.valueOf(getSpeed()));
				return;
			case MOVE:
				move(
						call.getInputValueAt(1,Vector2f.class),
						call.getInputValueAt(2, Boolean.class).booleanValue());
				return;
			case MOVE_BACKWARD:
				moveBackward(call.getInputValueAt(1,Number.class).intValue());
				return;
			case MOVE_FORWARD:
				moveForward(call.getInputValueAt(1, Number.class).intValue());
				return;
			case PICK_UP_FROM_TYPE:
				call.setOutputValues(pickUp(call.getInputValueAt(1,Class.class)));
				return;
			case PICK_UP_OBJECT:
				pickUp(call.getInputValueAt(1,EnvironmentalObject.class));
				return;
			case SET_HEADDING_ANGLE:
				setHeading(call.getInputValueAt(1,Number.class).floatValue());
				return;
			case SET_HEADING_DIRECTION:
				setHeading(call.getInputValueAt(1,Vector2f.class));
				return;
			case TOUCH_UP:
				call.setOutputValues(touchUp(call.getInputValueAt(1,Class.class)));
				return;
			case TURN_LEFT:
				turnLeft(call.getInputValueAt(1,Number.class).floatValue());
				return;
			case TURN_RIGHT:
				turnRight(call.getInputValueAt(1,Number.class).floatValue());
				return;
			case IS_PERCEPTION_ENABLE:
				call.setOutputValues(Boolean.valueOf(isPerceptionEnable()));
				return;
			case SET_PERCEPTION_ENABLE:
				setPerceptionEnable(call.getInputValueAt(1,Boolean.class).booleanValue());
				return;
			case GET_LAST_MOTION_INFLUENCE_STATUS:
				call.setOutputValues(getLastMotionInfluenceStatus());
				return;
			default:
				//
			}
			
			call.fail();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasBody() {
			TurtleBody body = Turtle.this.getTurtleBody();
			return (body!=null);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void move(Vector2f direction, boolean changeHeading) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.move(direction, changeHeading);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void moveForward(int cells) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.moveForward(cells);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void moveBackward(int cells) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.moveBackward(cells);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void turnLeft(float radians) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.turnLeft(radians);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void turnRight(float radians) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.turnRight(radians);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setHeading(float radians) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.setHeading(radians);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setHeading(Vector2f direction) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.setHeading(direction);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getHeadingAngle() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getHeadingAngle();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Vector2f getHeadingVector() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getHeadingVector();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void beIddle() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.beIddle();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void dropOff(EnvironmentalObject object) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.dropOff(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T extends Perceivable> T pickUp(Class<T> type) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.pickUp(type);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pickUp(EnvironmentalObject object) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.pickUp(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T extends EnvironmentalObject> T touchUp(Class<T> type) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.touchUp(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getX() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getX();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getY() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getY();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Point2i getPosition() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getPosition();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<Perceivable> getPerception() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getPerception();
		}

		/** {@inheritDoc}
		 */
		@Override
		public <T extends Perceivable> Collection<T> getPerception(Class<T> type) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getPerception(type);
		}

		/** {@inheritDoc}
		 */
		@Override
		public <T extends Perceivable> T getFirstPerception(Class<T> type) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getFirstPerception(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<EnvironmentalObject> getPerceivedObjects() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getPerceivedObjects();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T extends EnvironmentalObject> Collection<T> getPerceivedObjects(Class<T> type) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			List<T> list = new LinkedList<T>();
			for(EnvironmentalObject o : body.getPerceivedObjects()) {
				if (o!=null && type.isInstance(o)) {
					list.add(type.cast(o));
				}
			}
			return list;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T extends EnvironmentalObject> T getPerceivedObject(Class<T> type) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			for(EnvironmentalObject o : body.getPerceivedObjects()) {
				if (o!=null && type.isInstance(o)) {
					return type.cast(o);
				}
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EnvironmentalObject getPerceivedObjectWithSemantic(Class<?> semantic) {
			assert(semantic!=null);
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			for(EnvironmentalObject o : body.getPerceivedObjects()) {
				if (o!=null && o.getSemantic()!=null && semantic.isInstance(o.getSemantic())) {
					return o;
				}
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<EnvironmentalObject> getPerceivedObjectsWithSemantic(Class<?> semantic) {
			assert(semantic!=null);
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			List<EnvironmentalObject> list = new LinkedList<EnvironmentalObject>();
			for(EnvironmentalObject o : body.getPerceivedObjects()) {
				if (o!=null && o.getSemantic()!=null && semantic.isInstance(o.getSemantic())) {
					list.add(o);
				}
			}
			return list;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<PerceivedTurtle> getPerceivedTurtles() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getPerceivedTurtles();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasPerception() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.hasPerception();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasPerceivedObject() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.hasPerceivedObject();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasPerceivedTurtle() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.hasPerceivedTurtle();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getSpeed() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getSpeed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isPerceptionEnable() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.isPerceptionEnable();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPerceptionEnable(boolean enable) {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			body.setPerceptionEnable(enable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MotionInfluenceStatus getLastMotionInfluenceStatus() {
			TurtleBody body = Turtle.this.getTurtleBody();
			if (body==null) throw new IllegalStateException();
			return body.getLastMotionInfluenceStatus();
		}

	} // class TurtleBodyCapacityImplementation
	
}