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
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.probe.AbstractIndividualProbe;
import org.janusproject.kernel.probe.ProbeValueNotDefinedException;


/**
 * This class defines a watcher probe allowing the observation of a given role.
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
public class RoleProbe
extends AbstractIndividualProbe {

	private WeakReference<ProbeManager> manager;
	
	private final GroupAddress groupAddress;
	private final Class<? extends Role> role;
	
	private WeakReference<Role> probedRole = null;
	
	/**
	 * @param manager is the probe manager that has instanced this probe.
	 * @param watchedAgent is the address of the watched agent.
	 * @param group is the group in which the role lies. 
	 * @param role is the role to watch.
	 */
	public RoleProbe(ProbeManager manager, AgentAddress watchedAgent, GroupAddress group, Class<? extends Role> role) {
		super(watchedAgent);
		this.manager = new WeakReference<ProbeManager>(manager);
		this.groupAddress = group;
		this.role = role;
	}

	private Role getRole() {
		Role r = (this.probedRole==null) ? null : this.probedRole.get();
		if (r==null) {
			ProbeManager pm = (this.manager==null) ? null : this.manager.get();
			if (pm!=null) {
				r = pm.getRole(getWatchedObject(), this.groupAddress, this.role);
				if (r!=null) this.probedRole = new WeakReference<Role>(r);
			}
		}
		return r;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getProbeValue(String probeName, Class<T> clazz) {
		if (isAlive()) {
			ProbeManager pm = this.manager.get();
			if (pm!=null) {
				Role role = getRole();
				if (role!=null) {
					Object v;
					try {
						v = pm.getProbeValue(probeName, role);
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
				Role role = getRole();
				if (role!=null) {
					try {
						return pm.getProbeValue(probeName, role);
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
				Role role = getRole();
				if (role!=null) {
					try {
						v = pm.getProbeValue(probeName, role);
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
				Role role = getRole();
				if (role!=null) {
					try {
						return pm.getWatchableNames(role.getClass());
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
				Role role = getRole();
				if (role!=null) {
					try {
						Set<String> v = pm.getWatchableNames(role.getClass());
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
				Role role = getRole();
				if (role!=null) {
					try {
						Set<String> v = pm.getWatchableNames(role.getClass());
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
