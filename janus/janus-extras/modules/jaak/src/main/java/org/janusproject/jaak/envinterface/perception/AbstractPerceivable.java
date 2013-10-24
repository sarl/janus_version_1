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

/** This class defines a perceived turtle.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractPerceivable implements Perceivable, Serializable {

	private static final long serialVersionUID = -7970321275288727456L;
	
	/** Position of the perceived object.
	 */
	final Point2i position = new Point2i();

	/** Is the semantic associated to this perceived object.
	 */
	Object semantic = null;

	/**
	 */
	public AbstractPerceivable() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Point2i getPosition() {
		return this.position;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2i getRelativePosition(TurtleBody body) {
		Point2i p = this.position;
		if (body==null) return p;
		Point2i bp = body.getPosition();
		return new Point2i(bp.x() - p.x(), bp.y() - p.y());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getSemantic() {
		return this.semantic;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		Point2i position = getPosition();
		buffer.append('(');
		buffer.append(position.getX());
		buffer.append(';');
		buffer.append(position.getY());
		buffer.append("); semantic="); //$NON-NLS-1$
		Object semantic = getSemantic();
		if (semantic==null) buffer.append((String)null);
		else buffer.append(semantic.toString());
		return buffer.toString();
	}
	
}