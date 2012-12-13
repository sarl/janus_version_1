/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.probe.CollectiveProbe;
import org.janusproject.kernel.probe.IndividualProbe;
import org.janusproject.kernel.probe.ProbeCreationException;
import org.janusproject.kernel.probe.ProbeValueNotDefinedException;
import org.janusproject.kernel.probe.Watchable;
import org.janusproject.kernel.probe.WatchableObject;
import org.janusproject.kernel.util.comparator.GenericComparator;
import org.janusproject.kernel.util.sizediterator.EmptyIterator;
import org.janusproject.kernel.util.sizediterator.UnmodifiableCollectionSizedIterator;

/**
 * A manager of probes.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ProbeManager {
	
	private final Map<Class<?>, Collection<Field>> watchableFields = 
		new TreeMap<Class<?>, Collection<Field>>(GenericComparator.SINGLETON);
	
	private final Map<AgentAddress,Collection<IndividualProbe>> individualProbes = 
		new TreeMap<AgentAddress, Collection<IndividualProbe>>(GenericComparator.SINGLETON);
	
	private final Set<CollectiveProbe> collectiveProbes = 
			new TreeSet<CollectiveProbe>(GenericComparator.SINGLETON);

	private final WeakReference<KernelContext> context;
	
	/**
	 * @param context
	 */
	ProbeManager(KernelContext context) {
		this.context = new WeakReference<KernelContext>(context);
	}
	
	/** Release any resource owned by this manager.
	 * 
	 * @since 0.5
	 */
	synchronized void release() {
		Iterator<Entry<AgentAddress,Collection<IndividualProbe>>> iteratorI = this.individualProbes.entrySet().iterator();
		Entry<AgentAddress,Collection<IndividualProbe>> eI;
		while (iteratorI.hasNext()) {
			eI = iteratorI.next();
			iteratorI.remove();
			onIndividualProbesReleased(eI.getKey(), eI.getValue());
		}
		
		Iterator<CollectiveProbe> iteratorC = this.collectiveProbes.iterator();
		CollectiveProbe eC;
		while (iteratorC.hasNext()) {
			eC = iteratorC.next();
			iteratorC.remove();
			onCollectiveProbeReleased(eC);
		}

		this.watchableFields.clear();
	}
	
	private void onIndividualProbesReleased(AgentAddress probedObject, Collection<IndividualProbe> probes) {
		if (probes!=null && !probes.isEmpty()) {
			Agent a = getAgent(probedObject);
			WatchableObject wo;
			if (a instanceof WatchableObject) {
				wo = (WatchableObject)a;
			}
			else {
				wo = null;
			}
			for(IndividualProbe p : probes) {
				if (wo!=null) wo.releaseProbe(p);
				if (p.isAlive()) p.releaseProbe();
			}
		}
	}

	private static void onCollectiveProbeReleased(CollectiveProbe probe) {
		if (probe!=null && probe.isAlive()) {
			probe.releaseProbe();
		}
	}

	/** Replies the list of probable attributes for the specified object.
	 */
	private static Collection<Field> extractWatchableAttributesFrom(Class<?> probedObjectType) {
		assert(probedObjectType!=null);
		
		Map<String,Field> attributes = new TreeMap<String, Field>();
		String attrName;
		
		Class<?> type = probedObjectType;
		boolean finalLevel = true;

		while (type!=null) {
			
			for(Field field : type.getDeclaredFields()) {
			
				attrName = field.getName();
				
				// If marked as watchable, save this field  
				Watchable annotation = field.getAnnotation(Watchable.class);
				if (annotation!=null) {
					if (finalLevel || !annotation.isFinal()) {
						attributes.put(attrName, field);
					}
				}
			}
			
			type = type.getSuperclass();
			finalLevel = false;
		}
		
		List<Field> fields = new ArrayList<Field>(attributes.size());
		fields.addAll(attributes.values());
		attributes.clear();
		return fields;
	}
	
	/** Replies the watchable attributes for the specified object.
	 */
	private Collection<Field> getWatchableAttributes(Class<?> probedObjectType) {
		assert(probedObjectType!=null);
		Collection<Field> fields = this.watchableFields.get(probedObjectType);
		if (fields==null) {
			fields = extractWatchableAttributesFrom(probedObjectType);
			this.watchableFields.put(probedObjectType, fields);
		}
		return fields;
	}

	/** Return the value of the specified field inside the specified object.
	 * 
	 * @param probeName is the name of the attribute to watch
	 * @param probedObject is the object from which the attribute value must be extracted.
	 * @return the attribute's value extracted from the given object.
	 * @throws ProbeValueNotDefinedException if the value is undefined  
	 */
	synchronized Object getProbeValue(String probeName, Object probedObject) throws ProbeValueNotDefinedException {
		if (probedObject!=null) {
			Collection<Field> collection = getWatchableAttributes(probedObject.getClass());
			Iterator<Field> iterator = collection.iterator();
			Field field;
			while (iterator.hasNext()) {
				field = iterator.next();
				if (field.getName().equals(probeName)) {
					// JANUS-156: Enable the probe manager to access to
					// the fields that are not public.
					// Change the accessibility during the reading of
					// the value to limit the time during which
					// the field accessibility is overridden.
					boolean isAccessible = field.isAccessible();
					field.setAccessible(true);
					try {
						return field.get(probedObject);
					}
					catch(AssertionError ae) {
						throw ae;
					}
					catch (IllegalArgumentException e) {
						iterator.remove();
					}
					catch (IllegalAccessException e) {
						iterator.remove();
					}
					finally {
						field.setAccessible(isAccessible);
					}
					
					// Remove the cache entry when it is no more required
					if (collection.isEmpty())
						this.watchableFields.remove(probedObject.getClass());
					
					break; // Stop loop and thrown an exception
				}
			}
		}
		throw new ProbeValueNotDefinedException(probeName);
	}
	
	/** Invoked to retreive the instance of an agent.
	 * 
	 * @param address is the address of the agent to retreive.
	 * @return the agent instance or <code>null</code>.
	 * @since 0.5
	 */
	Agent getAgent(AgentAddress address) {
		KernelContext context = this.context.get();
		if (context!=null) {
			return context.getAgentRepository().get(address);
		}
		return null;
	}

	/** Invoked to retreive the instance of a role.
	 * 
	 * @param player is the address of the role player.
	 * @param group is the address of the group.
	 * @param role is the name of the role.
	 * @return the agent instance or <code>null</code>.
	 * @since 0.5
	 */
	Role getRole(AgentAddress player, GroupAddress group, Class<? extends Role> role) {
		Agent agent = getAgent(player);
		if (agent!=null) {
			return agent.getRoleInstance(group, role);
		}
		return null;
	}

	/** Release the cache for the given object.
	 * 
	 * @param probedObject is the object for which all the probes msut be released.
	 */
	void release(Agent probedObject) {
		release(probedObject.getAddress());
	}

	/** Release the cache for the given object.
	 * 
	 * @param probedObject is the object for which all the probes msut be released.
	 */
	public synchronized void release(AgentAddress probedObject) {
		assert(probedObject!=null);
		onIndividualProbesReleased(probedObject, this.individualProbes.remove(probedObject));
	}

	/** Release the cache for the given object.
	 * 
	 * @param probe is the probe that must be released.
	 */
	public synchronized void release(IndividualProbe probe) {
		assert(probe!=null);
		AgentAddress adr = probe.getWatchedObject();
		if (adr!=null) {
			Collection<IndividualProbe> col = this.individualProbes.get(adr);
			if (col!=null) {
				if (col.remove(probe)) {
					if (col.isEmpty()) this.individualProbes.remove(adr);
					Agent a = getAgent(adr);
					if (a instanceof WatchableObject) {
						((WatchableObject)a).releaseProbe(probe);
					}
					if (probe.isAlive()) probe.releaseProbe();
				}
			}
		}
	}

	/** Release the cache for the given object.
	 * 
	 * @param probe is the probe that must be released.
	 */
	public synchronized void release(CollectiveProbe probe) {
		assert(probe!=null);
		this.collectiveProbes.remove(probe);
		onCollectiveProbeReleased(probe);
	}
	
	/** Return the names of supported probes.
	 * 
	 * @param probedObjectType is the type of the objects for which all names of probed attributes mustbe replied.
	 * @return the list of the probed attributes. 
	 */
	public synchronized Set<String> getWatchableNames(Class<?> probedObjectType) {	
		Set<String> list = new TreeSet<String>();
		if (probedObjectType!=null) {
			Collection<Field> collection = getWatchableAttributes(probedObjectType);
			Iterator<Field> iterator = collection.iterator();
			Field field;
			while (iterator.hasNext()) {
				field = iterator.next();
				list.add(field.getName());
			}
		}
		return list;
	}
	
	/** Create a new probe.
	 * <p>
	 * If the agent is implementing {@link WatchableObject},
	 * the creation of the probe is delegated to the agent itself.
	 * Otherwise this function tries to use the following constructors
	 * in the given order (where <code>Type</code>
	 * is the classname of your probe):<ol>
	 * <li>{@code Type(ProbeManager, AgentAddress)}</li>
	 * <li>{@code Type(KernelContext, AgentAddress)}</li>
	 * <li>{@code Type(AgentAddress)}</li>
	 * </ol>
	 *
	 * @param <T> is the type of the probe to instance.
	 * @param probe is the type of the probe to instance.
	 * @param probedAgent is the address of the probed agent.
	 * @return a probe, never <code>null</code>.
	 * @throws ProbeCreationException when something wrong append during the creation of the probe.
	 */
	public synchronized <T extends IndividualProbe> T createProbe(Class<T> probe, AgentAddress probedAgent) {
		T probeInstance = null;
		Agent a = getAgent(probedAgent);
		if (a instanceof WatchableObject) {
			probeInstance = ((WatchableObject)a).createProbe(probe); 
		}
		if (probeInstance==null) {
			try {
				Constructor<T> cons = probe.getConstructor(ProbeManager.class, AgentAddress.class);
				probeInstance = cons.newInstance(this, probedAgent);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch (Throwable e) {
				//
			}
		}
		if (probeInstance==null) {
			try {
				Constructor<T> cons = probe.getConstructor(KernelContext.class, AgentAddress.class);
				probeInstance = cons.newInstance(this.context.get(), probedAgent);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch (Throwable e) {
				//
			}
		}
		if (probeInstance==null) {
			try {
				Constructor<T> cons = probe.getConstructor(ProbeManager.class, AgentAddress.class);
				probeInstance = cons.newInstance(this, probedAgent);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch (Throwable e) {
				throw new ProbeCreationException(probe, e);
			}
		}
		assert(probeInstance!=null);
		
		Collection<IndividualProbe> agentProbes = this.individualProbes.get(probedAgent);
		if (agentProbes==null) {
			agentProbes = new ArrayList<IndividualProbe>();
			this.individualProbes.put(probedAgent, agentProbes);
		}
		agentProbes.add(probeInstance);
		
		return probeInstance;
	}
	
	/** Create a new probe.
	 * <p>
	 * This function does not support {@link WatchableObject} interface.
	 * This function tries to use the following constructors
	 * in the given order (where <code>Type</code>
	 * is the classname of your probe):<ol>
	 * <li>{@code Type(ProbeManager, AgentAddress, GroupAddress, Class&lt;? extends Role&gt;)}</li>
	 * <li>{@code Type(KernelContext, AgentAddress, GroupAddress, Class&lt;? extends Role&gt;)}</li>
	 * <li>{@code Type(AgentAddress, GroupAddress, Class&lt;? extends Role&gt;)}</li>
	 * </ol>
	 *
	 * @param <T> is the type of the probe to instance.
	 * @param probe is the type of the probe to instance.
	 * @param probedAgent is the address of the probed agent.
	 * @param group is the address of the organizational group.
	 * @param role is the role to watch.
	 * @return a probe, never <code>null</code>.
	 * @throws ProbeCreationException when something wrong append during the creation of the probe.
	 */
	public synchronized <T extends RoleProbe> T createProbe(Class<T> probe, AgentAddress probedAgent, GroupAddress group, Class<? extends Role> role) {
		T probeInstance = null;
		try {
			Constructor<T> cons = probe.getConstructor(ProbeManager.class, AgentAddress.class, GroupAddress.class, Class.class);
			probeInstance = cons.newInstance(this, probedAgent, group, role);
		}
		catch(AssertionError e) {
			throw e;
		}
		catch (Throwable e) {
			//
		}
		if (probeInstance==null) {
			try {
				Constructor<T> cons = probe.getConstructor(KernelContext.class, AgentAddress.class, GroupAddress.class, Class.class);
				probeInstance = cons.newInstance(this.context.get(), probedAgent, group, role);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch (Throwable e) {
				//
			}
		}
		if (probeInstance==null) {
			try {
				Constructor<T> cons = probe.getConstructor(ProbeManager.class, AgentAddress.class, GroupAddress.class, Class.class);
				probeInstance = cons.newInstance(this, probedAgent, group, role);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch (Throwable e) {
				throw new ProbeCreationException(probe, e);
			}
		}
		assert(probeInstance!=null);
		
		Collection<IndividualProbe> agentProbes = this.individualProbes.get(probedAgent);
		if (agentProbes==null) {
			agentProbes = new ArrayList<IndividualProbe>();
			this.individualProbes.put(probedAgent, agentProbes);
		}
		agentProbes.add(probeInstance);
		
		return probeInstance;
	}

	/** Create a new probe.
	 * <p>
	 * This function does not support the {@link WatchableObject} interface.
	 * This function tries to use the following constructors
	 * in the given order (where <code>Type</code>
	 * is the classname of your probe):<ol>
	 * <li>{@code Type(ProbeManager, AgentAddress[])}</li>
	 * <li>{@code Type(KernelContext, AgentAddress[])}</li>
	 * <li>{@code Type(AgentAddress[])}</li>
	 * </ol>
	 *
	 * @param <T> is the type of the probe to instance.
	 * @param probe is the type of the probe to instance.
	 * @param probedAgents are the addresses of the probed agents.
	 * @return a probe, never <code>null</code>.
	 * @throws ProbeCreationException when something wrong append during the creation of the probe.
	 */
	public synchronized <T extends CollectiveProbe> T createProbe(Class<T> probe, AgentAddress... probedAgents) {
		T probeInstance = null;
		try {
			Constructor<T> cons = probe.getConstructor(ProbeManager.class, AgentAddress[].class);
			probeInstance = cons.newInstance(this, probedAgents);
		}
		catch(AssertionError e) {
			throw e;
		}
		catch (Throwable e) {
			//
		}
		if (probeInstance==null) {
			try {
				Constructor<T> cons = probe.getConstructor(KernelContext.class, AgentAddress[].class);
				probeInstance = cons.newInstance(this.context.get(), probedAgents);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch (Throwable e) {
				//
			}
		}
		if (probeInstance==null) {
			try {
				Constructor<T> cons = probe.getConstructor(ProbeManager.class, AgentAddress[].class);
				probeInstance = cons.newInstance(this, probedAgents);
			}
			catch(AssertionError e) {
				throw e;
			}
			catch (Throwable e) {
				throw new ProbeCreationException(probe, e);
			}
		}
		assert(probeInstance!=null);

		this.collectiveProbes.add(probeInstance);
		
		return probeInstance;
	}

	/** Create a new probe.
	 * <p>
	 * If the agent is implementing {@link WatchableObject},
	 * the creation of the probe is delegated to the agent itself.
	 * Otherwise an {@link AgentProbe} is created.
	 *
	 * @param probedAgent is the address of the probed agent.
	 * @return a probe, never <code>null</code>.
	 * @throws ProbeCreationException when something wrong append during the creation of the probe.
	 * @since 0.5
	 */
	public synchronized IndividualProbe createProbe(AgentAddress probedAgent) {
		assert(probedAgent!=null);
		try {
			IndividualProbe probeInstance = null;
			Agent a = getAgent(probedAgent);
			if (a instanceof WatchableObject) {
				probeInstance = ((WatchableObject)a).createProbe(IndividualProbe.class);
			}
			if (probeInstance==null) {
				probeInstance = new AgentProbe(this, probedAgent);
			}
			Collection<IndividualProbe> agentProbes = this.individualProbes.get(probedAgent);
			if (agentProbes==null) {
				agentProbes = new ArrayList<IndividualProbe>();
				this.individualProbes.put(probedAgent, agentProbes);
			}
			agentProbes.add(probeInstance);
			return probeInstance;
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(ProbeCreationException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new ProbeCreationException(AgentProbe.class, e);
		}
	}

	/** Replies all the probes on the given entity.
	 * 
	 * @param entity is the address of the entity.
	 * @return probes.
	 */
	public synchronized Iterator<IndividualProbe> getProbes(AgentAddress entity) {
		Collection<IndividualProbe> declaredProbes = this.individualProbes.get(entity);
		if (declaredProbes!=null)
			return new UnmodifiableCollectionSizedIterator<IndividualProbe>(declaredProbes);
		return EmptyIterator.singleton();
	}

}
