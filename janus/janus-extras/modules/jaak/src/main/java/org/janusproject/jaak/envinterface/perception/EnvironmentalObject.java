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
package org.janusproject.jaak.envinterface.perception;

import org.janusproject.jaak.envinterface.influence.EnvironmentalObjectRemovalInfluence;
import org.janusproject.jaak.envinterface.influence.Influence;

/** This class defines a situated object inside the Jaak environment
 * which is not an agent.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EnvironmentalObject extends AbstractPerceivable implements JaakObject {

	private static final long serialVersionUID = 4218782489455701467L;

	/**
	 * @param semantic is the semantic associated to this environmental object.
	 */
	public EnvironmentalObject(Object semantic) {
		super();
		this.semantic = semantic;
	}
	
	/** Replies an identifier for this object.
	 * The identifier is unique for environmental objects
	 * which are not a {@link Substance} and is common
	 * to all instances of the same <code>Substance</code> class.
	 * 
	 * @return the identifier of the environmental object.
	 */
	public String getEnvironmentalObjectIdentifier() {
		StringBuffer buf = new StringBuffer();
		buf.append(getClass().getCanonicalName());
		buf.append("-o-o-o-"); //$NON-NLS-1$
		buf.append(Integer.toHexString(System.identityHashCode(this)));
		return buf.toString();
	}
	
	/** Set the position of this object.
	 * 
	 * @param x is the new position of the object.
	 * @param y is the new position of the object.
	 */
	void setPosition(int x, int y) {
		this.position.setX(x);
		this.position.setY(y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isTurtle() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBurrow() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isObstacle() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSubstance() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		if (isTurtle()) {
			buffer.append("TURTLE("); //$NON-NLS-1$
		}
		else {
			buffer.append("OBJECT("); //$NON-NLS-1$
		}
		buffer.append(getEnvironmentalObjectIdentifier());
		buffer.append("); "); //$NON-NLS-1$
		buffer.append(super.toString());
		return buffer.toString();
	}

	/** Replies an influence which is permitting to remove this object from the environment.
	 * 
	 * @return an influence, or <code>null</code> to not allow to remove the object.
	 */
	protected Influence createRemovalInfluenceForItself() {
		return new RemoveItself();
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RemoveItself extends EnvironmentalObjectRemovalInfluence {

		/**
		 */
		public RemoveItself() {
			//
		}
		
		@Override
		public EnvironmentalObject getRemovableObject() {
			return EnvironmentalObject.this;
		}
		
	}
	
}