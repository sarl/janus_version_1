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

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.janusproject.kernel.address.AgentAddress;

/** A probe permits to obtain values from a collection of agents.
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
public abstract class AbstractCollectiveProbe implements CollectiveProbe {

	private final AgentAddress[] watchedObjects;
	private final AtomicBoolean isAlive = new AtomicBoolean(true);

	/**
	 * @param watchedObjects is the list of the objects to watch.
	 */
	public AbstractCollectiveProbe(AgentAddress[] watchedObjects) {
		this.watchedObjects = watchedObjects;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAlive() {
		return this.isAlive.get();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AgentAddress[] getWatchedObjects() {
		return this.watchedObjects;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,Integer> getProbeInt(String probeName) throws ProbeException {
		return getProbeValue(probeName, Integer.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,Long> getProbeLong(String probeName) throws ProbeException {
		return getProbeValue(probeName, Long.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,Float> getProbeFloat(String probeName) throws ProbeException {
		return getProbeValue(probeName, Float.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,Double> getProbeDouble(String probeName) throws ProbeException {
		return getProbeValue(probeName, Double.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,Boolean> getProbeBool(String probeName) throws ProbeException {
		return getProbeValue(probeName, Boolean.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,Character> getProbeChar(String probeName) throws ProbeException {
		return getProbeValue(probeName, Character.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,Byte> getProbeByte(String probeName) throws ProbeException {
		return getProbeValue(probeName, Byte.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,Short> getProbeShort(String probeName) throws ProbeException {
		return getProbeValue(probeName, Short.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Map<AgentAddress,String> getProbeString(String probeName) throws ProbeException {
		return getProbeValue(probeName, String.class);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void releaseProbe() {
		this.isAlive.set(false);
	}
	
}