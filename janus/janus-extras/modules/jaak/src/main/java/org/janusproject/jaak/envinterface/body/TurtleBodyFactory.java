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
package org.janusproject.jaak.envinterface.body;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.frustum.TurtleFrustum;
import org.janusproject.kernel.address.AgentAddress;

/** Factory of bodies for turtles.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface TurtleBodyFactory {

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given position could be discarted by the environment
	 * model if some internal rules is broken. In this case, the
	 * real position of the turtle is selected by the environment
	 * itself.
	 * <p>
	 * The given orientation angle could be discarted by the
	 * environment model according to internal rules. The
	 * orientation is then selected by the environment itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredPosition is the position desired by the turtle.
	 * @param desiredAngle is the orientation angle desired by the turtle.
	 * @param semantic is the semantic to associated to the body.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Point2i desiredPosition,
			float desiredAngle,
			Object semantic);
		
	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given position could be discarted by the environment
	 * model if some internal rules is broken. In this case, the
	 * real position of the turtle is selected by the environment
	 * itself.
	 * <p>
	 * The given orientation angle could be discarted by the
	 * environment model according to internal rules. The
	 * orientation is then selected by the environment itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredPosition is the position desired by the turtle.
	 * @param desiredAngle is the orientation angle desired by the turtle.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Point2i desiredPosition,
			float desiredAngle);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given position could be discarted by the environment
	 * model if some internal rules is broken. In this case, the
	 * real position of the turtle is selected by the environment
	 * itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredPosition is the position desired by the turtle.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Point2i desiredPosition);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given orientation angle could be discarted by the
	 * environment model according to internal rules. The
	 * orientation is then selected by the environment itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredAngle is the orientation angle desired by the turtle.
	 * @param semantic is the semantic to associated to the body.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			float desiredAngle,
			Object semantic);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param semantic is the semantic to associated to the body.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Object semantic);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given position could be discarted by the environment
	 * model if some internal rules is broken. In this case, the
	 * real position of the turtle is selected by the environment
	 * itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredPosition is the position desired by the turtle.
	 * @param semantic is the semantic to associated to the body.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Point2i desiredPosition,
			Object semantic);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given position could be discarted by the environment
	 * model if some internal rules is broken. In this case, the
	 * real position of the turtle is selected by the environment
	 * itself.
	 * <p>
	 * The given orientation angle could be discarted by the
	 * environment model according to internal rules. The
	 * orientation is then selected by the environment itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredPosition is the position desired by the turtle.
	 * @param desiredAngle is the orientation angle desired by the turtle.
	 * @param semantic is the semantic to associated to the body.
	 * @param frustum is the perception frustum to use.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Point2i desiredPosition,
			float desiredAngle,
			Object semantic,
			TurtleFrustum frustum);
		
	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given position could be discarted by the environment
	 * model if some internal rules is broken. In this case, the
	 * real position of the turtle is selected by the environment
	 * itself.
	 * <p>
	 * The given orientation angle could be discarted by the
	 * environment model according to internal rules. The
	 * orientation is then selected by the environment itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredPosition is the position desired by the turtle.
	 * @param desiredAngle is the orientation angle desired by the turtle.
	 * @param frustum is the perception frustum to use.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Point2i desiredPosition,
			float desiredAngle,
			TurtleFrustum frustum);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given position could be discarted by the environment
	 * model if some internal rules is broken. In this case, the
	 * real position of the turtle is selected by the environment
	 * itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredPosition is the position desired by the turtle.
	 * @param frustum is the perception frustum to use.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Point2i desiredPosition,
			TurtleFrustum frustum);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param frustum is the perception frustum to use.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			TurtleFrustum frustum);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given orientation angle could be discarted by the
	 * environment model according to internal rules. The
	 * orientation is then selected by the environment itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredAngle is the orientation angle desired by the turtle.
	 * @param semantic is the semantic to associated to the body.
	 * @param frustum is the perception frustum to use.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			float desiredAngle,
			Object semantic,
			TurtleFrustum frustum);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param semantic is the semantic to associated to the body.
	 * @param frustum is the perception frustum to use.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Object semantic,
			TurtleFrustum frustum);

	/** Create an instance of a turtle body
	 * with to the given attributes.
	 * <p>
	 * The body is created by a factory implementation
	 * which is relevant to the current environment model. 
	 * <p>
	 * The given position could be discarted by the environment
	 * model if some internal rules is broken. In this case, the
	 * real position of the turtle is selected by the environment
	 * itself.
	 * 
	 * @param turtleId is the identifier of the turtle.
	 * @param desiredPosition is the position desired by the turtle.
	 * @param semantic is the semantic to associated to the body.
	 * @param frustum is the perception frustum to use.
	 * @return the created body, never <code>null</code>.
	 */
	public TurtleBody createTurtleBody(
			AgentAddress turtleId,
			Point2i desiredPosition,
			Object semantic,
			TurtleFrustum frustum);

	/** Replies if the cell at the given position is able to
	 * receive the new turtle body.
	 *
	 * @param position
	 * @return <code>true</code> if the new body could be put on the
	 * cell, otherwise <code>false</code>.
	 */
	public boolean isFreeCell(Point2i position);
	
}