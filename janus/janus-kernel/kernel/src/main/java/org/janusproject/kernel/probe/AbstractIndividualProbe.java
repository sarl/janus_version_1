/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.probe;

import java.util.concurrent.atomic.AtomicBoolean;

import org.janusproject.kernel.address.AgentAddress;

/** A probe permits to obtain values from one agent.
 * <p>
 * A probe must not be invasive and the agent is
 * able to ignore them (ie. to not provide information).
 * <p>
 * This interface is strongly inspirated by the <code>Probe</code>
 * interface from the
 * <a href="http://www.arakhne.org/tinyMAS/">tinyMAS project</a>.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractIndividualProbe implements IndividualProbe {

	private final AgentAddress watchedObject;
	private final AtomicBoolean isAlive = new AtomicBoolean(true);
	
	/**
	 * @param address is the address of the probed agent.
	 */
	public AbstractIndividualProbe(AgentAddress address) {
		this.watchedObject = address;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final AgentAddress getWatchedObject() {
		return this.watchedObject;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public final int getProbeInt(String probeName) throws ProbeException {
		Number value = getProbeValue(probeName, Number.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value.intValue();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final long getProbeLong(String probeName) throws ProbeException {
		Number value = getProbeValue(probeName, Number.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value.intValue();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final float getProbeFloat(String probeName) throws ProbeException {
		Number value = getProbeValue(probeName, Number.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value.floatValue();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final double getProbeDouble(String probeName) throws ProbeException {
		Number value = getProbeValue(probeName, Number.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value.doubleValue();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final boolean getProbeBool(String probeName) throws ProbeException {
		Boolean value = getProbeValue(probeName, Boolean.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value.booleanValue();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final char getProbeChar(String probeName) throws ProbeException {
		Character value = getProbeValue(probeName, Character.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value.charValue();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final byte getProbeByte(String probeName) throws ProbeException {
		Number value = getProbeValue(probeName, Number.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value.byteValue();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final short getProbeShort(String probeName) throws ProbeException {
		Number value = getProbeValue(probeName, Number.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value.shortValue();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final String getProbeString(String probeName) throws ProbeException {
		String value = getProbeValue(probeName, String.class);
		if (value==null)
			throw new ProbeValueNotDefinedException(probeName);
		return value;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void releaseProbe() {
		this.isAlive.set(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAlive() {
		return this.isAlive.get();
	}

}