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

import java.math.BigDecimal;
import java.math.BigInteger;

/** This class defines a substance with a single precision
 * floating point value as the internal substance value.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class FloatSubstance extends AbstractNumberSubstance {

	private static final long serialVersionUID = -6809995815683796406L;
	
	/** Value of the substance.
	 */
	protected float value;
	
	/**
	 * @param semantic is the semantic associated to this environmental object.
	 */
	public FloatSubstance(Object semantic) {
		this(0f, semantic);
	}
	
	/**
	 * @param initialValue is the initial value of this substance.
	 * @param semantic is the semantic associated to this environmental object.
	 */
	public FloatSubstance(float initialValue, Object semantic) {
		super(semantic);
		this.value = initialValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void increment(float a) {
		this.value += a;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void decrement(float a) {
		this.value -= a;
		if (this.value<0f) this.value = 0f;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isDisappeared() {
		return this.value<=0f;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Float getAmount() {
		return Float.valueOf(this.value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BigDecimal bigDecimalValue() {
		return new BigDecimal(Float.toString(this.value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BigInteger bigIntegerValue() {
		return new BigInteger(Float.toString(this.value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final byte byteValue() {
		return (byte)this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double doubleValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final float floatValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int intValue() {
		return (int)this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long longValue() {
		return (long)this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final short shortValue() {
		return (short)this.value;
	}

}