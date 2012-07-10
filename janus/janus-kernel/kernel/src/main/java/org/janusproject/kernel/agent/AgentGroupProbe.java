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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.probe.AbstractCollectiveProbe;
import org.janusproject.kernel.probe.ProbeValueNotDefinedException;


/**
 * This class defines a watcher probe allowing the observation of a collection of agents.
 * <p>
 * This probe implementation is automatically checking the public attributes
 * of the agent with the <code>@Watchable</code> annotation.
 * <p>
 * You may override the functions of this class to provide another way to access
 * to the probed values. By default, all the job is delegated to the {@link ProbeManager}.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentGroupProbe
extends AbstractCollectiveProbe {

	private WeakReference<ProbeManager> manager;
	
	private List<WeakReference<Agent>> probedAgents = null;
	
	/**
	 * @param manager is the probe manager that has instanced this probe.
	 * @param watchedAgents are the addresses of the watched agents. 
	 */
	public AgentGroupProbe(ProbeManager manager, AgentAddress[] watchedAgents) {
		super(watchedAgents);
		this.manager = new WeakReference<ProbeManager>(manager);
	}
	
	private List<WeakReference<Agent>> getAgents() {
		if (this.probedAgents==null) {
			ProbeManager pm = (this.manager==null) ? null : this.manager.get();
			if (pm!=null) {
				AgentAddress[] adrs = getWatchedObjects();
				this.probedAgents = new ArrayList<WeakReference<Agent>>(adrs.length);
				for(AgentAddress adr : adrs) {
					Agent a = pm.getAgent(adr);
					if (a!=null && a.isAlive()) {
						this.probedAgents.add(new WeakReference<Agent>(a));
					}
				}
			}
		}
		return this.probedAgents;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Map<AgentAddress,T> getProbeValue(String probeName, Class<T> clazz) {
		Map<AgentAddress,T> map = new TreeMap<AgentAddress,T>();
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				List<WeakReference<Agent>> agents = getAgents();
				if (agents!=null) {
					for(WeakReference<Agent> wagent : agents) {
						Agent agent = wagent.get();
						if (agent!=null) {
							Object v;
							try {
								v = pm.getProbeValue(probeName, agent);
								if (v!=null) {
									map.put(agent.getAddress(), clazz.cast(v));
								}
							}
							catch (ProbeValueNotDefinedException _) {
								//
							}
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<AgentAddress,Object> getProbeValue(String probeName) {
		Map<AgentAddress,Object> map = new TreeMap<AgentAddress, Object>();
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				List<WeakReference<Agent>> agents = getAgents();
				if (agents!=null) {
					for(WeakReference<Agent> wagent : agents) {
						Agent agent = wagent.get();
						if (agent!=null) {
							Object v;
							try {
								v = pm.getProbeValue(probeName, agent);
								if (v!=null) {
									map.put(agent.getAddress(), v);
								}
							}
							catch (ProbeValueNotDefinedException _) {
								//
							}
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<AgentAddress,T[]> getProbeArray(String probeName, Class<T> clazz) {
		Map<AgentAddress,T[]> map = new TreeMap<AgentAddress, T[]>();
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				List<WeakReference<Agent>> agents = getAgents();
				if (agents!=null) {
					for(WeakReference<Agent> wagent : agents) {
						Agent agent = wagent.get();
						if (agent!=null) {
							Object v;
							try {
								v = pm.getProbeValue(probeName, agent);
								if (v!=null) {
									int count = 0;
									T[] t = (T[])(Array.newInstance(clazz,count));
									for(int i=0; i<count; ++i) {
										Object lo = Array.get(v,i);
										if ((lo!=null)&&(clazz.isInstance(lo))) {
											t[i] = clazz.cast(lo);
										}
									}
									map.put(agent.getAddress(), t);
								}
							}
							catch (ProbeValueNotDefinedException _) {
								//
							}
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getProbedValueNames() {
		Set<String> theSet = new TreeSet<String>();
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				List<WeakReference<Agent>> agents = getAgents();
				if (agents!=null) {
					for(WeakReference<Agent> wagent : agents) {
						Agent agent = wagent.get();
						if (agent!=null) {
							theSet.addAll(pm.getWatchableNames(agent.getClass()));
						}
					}
				}
			}
		}
		return theSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasProbeValues() {
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				List<WeakReference<Agent>> agents = getAgents();
				if (agents!=null) {
					for(WeakReference<Agent> wagent : agents) {
						Agent agent = wagent.get();
						if (agent!=null) {
							Set<String> theSet = pm.getWatchableNames(agent.getClass());
							if (theSet!=null && !theSet.isEmpty())
								return true;
						}
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
				List<WeakReference<Agent>> agents = getAgents();
				if (agents!=null) {
					for(WeakReference<Agent> wagent : agents) {
						Agent agent = wagent.get();
						if (agent!=null) {
							Set<String> theSet = pm.getWatchableNames(agent.getClass());
							if (theSet!=null && theSet.contains(probeValueName))
								return true;
						}
					}
				}
			}
		}
		return false;
	}
		
}
