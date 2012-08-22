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
package org.janusproject.demos.jaak.ants.environment;

import org.janusproject.jaak.envinterface.perception.FloatSubstance;
import org.janusproject.jaak.envinterface.perception.Substance;

/** This class defines a source of food.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Food extends FloatSubstance implements Cloneable {
	
	private static final long serialVersionUID = 472944785479015352L;

	/**
	 * @param foodQuantity is the quantity of food in this source.
	 */
	public Food(float foodQuantity) {
		super(foodQuantity, Food.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Food clone() {
		try {
			return (Food)super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Substance decrement(Substance s) {
		if (s instanceof Food) {
			float oldValue = floatValue();
			decrement(s.floatValue());
			Food c = clone();
			c.value = Math.abs(floatValue() - oldValue);
			return c;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Substance increment(Substance s) {
		return null;
	}
	
}