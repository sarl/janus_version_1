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
package org.janusproject.kernel.agent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Set;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.probe.AbstractIndividualProbe;
import org.janusproject.kernel.probe.ProbeValueNotDefinedException;


/**
 * This class defines a watcher probe allowing the observation of a given agent.
 * <p>
 * This probe implementation is automatically checking the public attributes
 * of the agent with the <code>@Watchable</code> annotation.
 * <p>
 * You may override the functions of this class to provide another way to access
 * to the probed values. By default, all the job is delegated to the {@link ProbeManager}.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentProbe
extends AbstractIndividualProbe {

	private WeakReference<ProbeManager> manager;
	
	private WeakReference<Agent> probedAgent = null;
	
	/**
	 * @param manager is the probe manager that has instanced this probe.
	 * @param watchedAgent is the address of the watched agent 
	 */
	public AgentProbe(ProbeManager manager, AgentAddress watchedAgent) {
		super(watchedAgent);
		this.manager = new WeakReference<ProbeManager>(manager);
	}

	private Agent getAgent() {
		Agent a = (this.probedAgent==null) ? null : this.probedAgent.get();
		if (a==null) {
			ProbeManager pm = (this.manager==null) ? null : this.manager.get();
			if (pm!=null) {
				a = pm.getAgent(getWatchedObject());
				if (a!=null && !a.isAlive()) a = null;
				if (a!=null) this.probedAgent = new WeakReference<Agent>(a);
			}
		}
		return a;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getProbeValue(String probeName, Class<T> clazz) {
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				Agent agent = getAgent();
				if (agent!=null) {
					Object v;
					try {
						v = pm.getProbeValue(probeName, agent);
						if (v!=null) {
							return clazz.cast(v);
						}
					}
					catch (ProbeValueNotDefinedException _) {
						//
					}
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getProbeValue(String probeName) {
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				Agent agent = getAgent();
				if (agent!=null) {
					try {
						return pm.getProbeValue(probeName, agent);
					}
					catch (ProbeValueNotDefinedException _) {
						//
					}
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] getProbeArray(String probeName, Class<T> clazz) {
		Object v = null;
		int count = 0;
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				Agent agent = getAgent();
				if (agent!=null) {
					try {
						v = pm.getProbeValue(probeName, agent);
						if (v!=null) {
							count = Array.getLength(v);
						}
					}
					catch (ProbeValueNotDefinedException _) {
						//
					}
				}
			}
		}
		T[] t = (T[])(Array.newInstance(clazz,count));
		for(int i=0; i<count; ++i) {
			Object lo = Array.get(v,i);
			if ((lo!=null)&&(clazz.isInstance(lo))) {
				t[i] = clazz.cast(lo);
			}
		}
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getProbedValueNames() {
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				Agent agent = getAgent();
				if (agent!=null) {
					try {
						return pm.getWatchableNames(agent.getClass());
					}
					catch (Throwable _) {
						//
					}
				}
			}
		}
		return Collections.emptySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasProbeValues() {
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				Agent agent = getAgent();
				if (agent!=null) {
					try {
						Set<String> v = pm.getWatchableNames(agent.getClass());
						return v!=null && !v.isEmpty();
					}
					catch (Throwable _) {
						//
					}
				}
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasProbeValue(String probeValueName) {
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				Agent agent = getAgent();
				if (agent!=null) {
					try {
						Set<String> v = pm.getWatchableNames(agent.getClass());
						return v!=null && v.contains(probeValueName);
					}
					catch (Throwable _) {
						//
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void releaseProbe() {
		super.releaseProbe();
		// Be sure that the probe manager has released this probe.
		ProbeManager pm = this.manager.get();
		if (pm!=null) pm.release(this);
	}
		
}
