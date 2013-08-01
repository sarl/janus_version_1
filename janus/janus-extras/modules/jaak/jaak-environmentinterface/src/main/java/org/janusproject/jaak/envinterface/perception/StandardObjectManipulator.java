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

import org.janusproject.jaak.envinterface.perception.EnvironmentalObject;
import org.janusproject.jaak.envinterface.perception.ObjectManipulator;
import org.janusproject.jaak.envinterface.perception.Substance;

/** This class permits to manipulate the environmental objects.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class StandardObjectManipulator implements ObjectManipulator {

	/**
	 * @throw {@link SecurityException} if the object which is instanciating
	 * this class in not a Jaak environment.
	 */
	public StandardObjectManipulator() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPosition(EnvironmentalObject object, int x, int y) {
		assert(object!=null);
		object.setPosition(x,y);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Substance combine(Substance s1, Substance s2, boolean additionOperation) {
		if (additionOperation)
			return s1.increment(s2);
		return s1.decrement(s2);
	}
		
}