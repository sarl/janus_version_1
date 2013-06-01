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
package org.janusproject.kernel.crio.capacity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.janusproject.kernel.repository.RepositoryChangeListener;
import org.janusproject.kernel.repository.RepositoryOverlooker;
import org.janusproject.kernel.util.sizediterator.SizedIterator;
import org.janusproject.kernel.util.sizediterator.UnmodifiableIterator;
import org.janusproject.kernel.util.sizediterator.UnmodifiableMapKeySizedIterator;

/**
 * {@link HashMap}-based implementation of a CapacityContainer.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see Capacity
 */
public class HashCapacityContainer
extends AbstractCapacityContainer {
	
	private final Map<Class<? extends Capacity>,Collection<CapacityImplementation>> content;

	/**
	 */
	public HashCapacityContainer() {
		this.content = new HashMap<Class<? extends Capacity>,Collection<CapacityImplementation>>();
	}
	
	/**
	 * @param initialCapacity is the initial capacity of the HashMap.
	 */
	public HashCapacityContainer(int initialCapacity) {
		this.content = new HashMap<Class<? extends Capacity>,Collection<CapacityImplementation>>(initialCapacity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeCapacity(Class<? extends Capacity> capacity) {
		assert(capacity!=null);
		this.content.remove(capacity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeCapacity(CapacityImplementation capacity) {
		assert(capacity!=null);
		Collection<CapacityImplementation> implementations;
		for(Class<? extends Capacity> type : capacity.getCapacities()) {
			implementations = this.content.get(type);
			if (implementations!=null) {
				implementations.remove(capacity);
				if (implementations.isEmpty()) {
					this.content.remove(type);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCapacity(CapacityImplementation capacity) {
		assert(capacity!=null);
		Collection<CapacityImplementation> implementations;
		for(Class<? extends Capacity> type : capacity.getCapacities()) {
			implementations = this.content.get(type);
			if (implementations==null) {
				implementations = new TreeSet<CapacityImplementation>(CapacityImplementationComparator.SINGLETON);
				this.content.put(type, implementations);
			}
			implementations.add(capacity);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAll(CapacityContainer container) {
		assert(container!=null);
		Collection<CapacityImplementation> implementations;
		for(Class<? extends Capacity> capacity : container) {
			implementations = this.content.get(capacity);
			if (implementations==null) {
				implementations = new TreeSet<CapacityImplementation>(CapacityImplementationComparator.SINGLETON);
				this.content.put(capacity, implementations);
			}
			implementations.addAll(container.get(capacity));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Class<? extends Capacity> id) {
		return this.content.containsKey(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<CapacityImplementation> get(Class<? extends Capacity> id) {
		Collection<CapacityImplementation> col = this.content.get(id);
		return (col==null) ? Collections.<CapacityImplementation>emptyList() : 
			Collections.unmodifiableCollection(col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.content.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.content.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Class<? extends Capacity>> iterator() {
		return new UnmodifiableIterator<Class<? extends Capacity>>(this.content.keySet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SizedIterator<Class<? extends Capacity>> sizedIterator() {
		return new UnmodifiableMapKeySizedIterator<Class<? extends Capacity>>(this.content);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Class<? extends Capacity>> identifiers() {
		return Collections.unmodifiableSet(this.content.keySet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Collection<CapacityImplementation>> values() {
		return Collections.unmodifiableCollection(this.content.values());
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public RepositoryOverlooker<Class<? extends Capacity>> getOverlooker() {
		return new Overlooker();
	}

	/**
	 * Stores the capacities of a given entity and their associated implementation.
	 * <p>
	 * The default capacity implementation selection policy is an instance of
	 * {@link FirstCapacityImplementationSelectionPolicy}.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @see Capacity
	 */
	private class Overlooker implements RepositoryOverlooker<Class<? extends Capacity>> {
		
		/**
		 */
		public Overlooker() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addRepositoryChangeListener(RepositoryChangeListener listener) {
			HashCapacityContainer.this.addRepositoryChangeListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeRepositoryChangeListener(RepositoryChangeListener listener) {
			HashCapacityContainer.this.removeRepositoryChangeListener(listener);
		}

		/** {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public Iterator<Class<? extends Capacity>> iterator() {
			return Collections.unmodifiableMap(HashCapacityContainer.this.content).keySet().iterator();
		}
		
	}

}
