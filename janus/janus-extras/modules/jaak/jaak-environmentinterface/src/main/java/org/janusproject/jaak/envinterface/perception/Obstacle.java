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

/** This class defines a location on the grid where no turtle nor other
 * environment object stay.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Obstacle extends EnvironmentalObject {

	private static final long serialVersionUID = 7180672612817853149L;
	
	/** Default Obstacle semantic.
	 */
	public static final Object OBSTACLE_SEMANTIC = new Object();
	
	/**
	 * @param semantic is the semantic associated to this environmental object.
	 */
	public Obstacle(Object semantic) {
		super(semantic);
	}
	
	/**
	 */
	public Obstacle() {
		this(OBSTACLE_SEMANTIC);
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
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSubstance() {
		return false;
	}

}