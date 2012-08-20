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
package org.janusproject.jaak.environment.model;

import org.janusproject.jaak.envinterface.body.TurtleBody;
import org.janusproject.jaak.envinterface.influence.MotionInfluenceStatus;
import org.janusproject.jaak.envinterface.perception.PickedObject;
import org.janusproject.jaak.environment.solver.InfluenceSolver;

/** Abstract implementation of an influence solver which is able to access
 * to the internal data structures of the Jaak environment.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractJaakEnvironmentInfluenceSolver extends InfluenceSolver<RealTurtleBody> {

	/**
	 */
	public AbstractJaakEnvironmentInfluenceSolver() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void putBackPickingAction(TurtleBody body, PickedObject action) {
		if (body instanceof RealTurtleBody) {
			RealTurtleBody b = (RealTurtleBody)body;
			b.putBackPickingAction(action);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void putBackMotionInfluenceStatus(TurtleBody body, MotionInfluenceStatus status) {
		if (body instanceof RealTurtleBody) {
			RealTurtleBody b = (RealTurtleBody)body;
			b.putBackMotionInfluenceStatus(status);
		}
	}

}