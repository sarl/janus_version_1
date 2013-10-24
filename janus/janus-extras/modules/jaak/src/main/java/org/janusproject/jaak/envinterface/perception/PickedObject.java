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

import java.io.Serializable;

import org.arakhne.afc.math.discrete.object2d.Point2i;
import org.janusproject.jaak.envinterface.body.TurtleBody;

/** This class defines a object which was picked up from the cell
 * according to a previous picking-up influence.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PickedObject implements Perceivable, Serializable {

	private static final long serialVersionUID = -5408636984133012977L;
	
	private final EnvironmentalObject pickedObject;
	
	/**
	 * @param pickedUpObject is the picked-up object.
	 */
	public PickedObject(EnvironmentalObject pickedUpObject) {
		this.pickedObject = pickedUpObject;
	}
	
	/** Replies the picked-up object.
	 * 
	 * @return the picked-up object.
	 */
	public EnvironmentalObject getPickedUpObject() {
		return this.pickedObject;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("PICKED("); //$NON-NLS-1$
		buffer.append(this.pickedObject.getEnvironmentalObjectIdentifier());
		buffer.append(")@("); //$NON-NLS-1$
		Point2i position = getPosition();
		buffer.append(position.getX());
		buffer.append(';');
		buffer.append(position.getY());
		buffer.append(")"); //$NON-NLS-1$
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2i getPosition() {
		return this.pickedObject.getPosition();
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2i getRelativePosition(TurtleBody body) {
		Point2i p = this.pickedObject.getPosition();
		if (body==null) return p;
		Point2i bp = body.getPosition();
		return new Point2i(bp.x() - p.x(), bp.y() - p.y());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getSemantic() {
		return this.pickedObject.getSemantic();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTurtle() {
		return this.pickedObject.isTurtle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBurrow() {
		return this.pickedObject.isBurrow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isObstacle() {
		return this.pickedObject.isObstacle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSubstance() {
		return this.pickedObject.isSubstance();
	}

}