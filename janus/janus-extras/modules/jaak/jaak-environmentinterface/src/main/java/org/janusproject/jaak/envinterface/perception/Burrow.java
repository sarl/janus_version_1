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

import org.janusproject.jaak.envinterface.influence.Influence;

/** This class defines a location on the grid where turtles are living.
 * When a cell contains a burrow, it has no more restriction about
 * the number of turtles to be on the cell.
 * Moreover, all turtles inside the burrow are not perceivable by the
 * other turtles.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Burrow extends EnvironmentalObject {

	private static final long serialVersionUID = 7690399061756387663L;
	
	/** Default Burrow semantic.
	 */
	public static final Object BURROW_SEMANTIC = new Object();
	
	/**
	 * @param semantic is the semantic associated to this environmental object.
	 */
	public Burrow(Object semantic) {
		super(semantic);
	}
	
	/**
	 */
	public Burrow() {
		this(BURROW_SEMANTIC);
	}

	/** {@inheritDoc}
	 */
	@Override
	protected final Influence createRemovalInfluenceForItself() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isBurrow() {
		return true;
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