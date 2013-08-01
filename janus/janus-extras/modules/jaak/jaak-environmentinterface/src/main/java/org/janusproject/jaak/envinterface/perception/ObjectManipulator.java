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

/** This interface permits to manipulate the environmental objects.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ObjectManipulator {

	/** Set the position of the object.
	 * 
	 * @param object is the object to set.
	 * @param x is the position of the object.
	 * @param y is the position of the object.
	 */
	public void setPosition(EnvironmentalObject object, int x, int y);
	
	/**
	 * Combine the given substance <var>s</var> with the current substance.
	 * <p>
	 * The concept of substance combination depends on the semantic of the substance.
	 * 
	 * @param s1 is the substance to combine and which may receive the result.
	 * @param s2 is the substance to combine with.
	 * @param additionOperation indicates if the combination is an addition
	 * if <code>true</code>, or a substraction if <code>false</code>.
	 * @return the change amount in <var>s1</var>. It is a substance lower or equal to <var>s2</var>,
	 * or <code>null</code> if the operation is not possible.
	 */
	public Substance combine(Substance s1, Substance s2, boolean additionOperation);
		
}