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
package org.janusproject.kernel.util.directaccess;

import java.util.Collection;
import java.util.List;


/**
 * Make unmodifiable a DirectAccessCollection
 * 
 * @param <M> is the type of element.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class UnmodifiableDirectAccessCollection<M>
implements DirectAccessCollection<M> {

	/** Singleton for an unmodifiable direct access collection.
	 */
	private static final UnmodifiableDirectAccessCollection<Object> SINGLETON = new UnmodifiableDirectAccessCollection<Object>();
	
	/** Singleton for an unmodifiable direct access collection.
	 * 
	 * @param <E> is the type of the elements in the empty collection.
	 * @return an empty collection
	 */
	@SuppressWarnings("unchecked")
	public static <E> UnmodifiableDirectAccessCollection<E> empty() {
		return (UnmodifiableDirectAccessCollection<E>)SINGLETON;
	}
	
	private final DirectAccessCollection<M> originalCollection;
	private final List<M> originalList;

	/**
	 * @param collection
	 */
	public UnmodifiableDirectAccessCollection(DirectAccessCollection<M> collection) {
		assert(collection!=null);
		this.originalCollection = collection;
		this.originalList = null;
	}

	/**
	 * @param collection
	 */
	public UnmodifiableDirectAccessCollection(List<M> collection) {
		assert(collection!=null);
		this.originalCollection = null;
		this.originalList = collection;
	}

	/**
	 */
	private UnmodifiableDirectAccessCollection() {
		this.originalCollection = null;
		this.originalList = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public M get(int position) {
		if (this.originalCollection!=null) 
			return this.originalCollection.get(position);
		if (this.originalList!=null)
			return this.originalList.get(position);
		throw new IndexOutOfBoundsException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object o) {
		if (this.originalCollection!=null)
			return this.originalCollection.contains(o);
		if (this.originalList!=null)
			return this.originalList.contains(o);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		if (this.originalCollection!=null)
			return this.originalCollection.containsAll(c);
		if (this.originalList!=null)
			return this.originalList.containsAll(c);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		if (this.originalCollection!=null)
			return this.originalCollection.isEmpty();
		if (this.originalList!=null)
			return this.originalList.isEmpty();
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SafeIterator<M> iterator() {
		if (this.originalCollection!=null)
			return this.originalCollection.iterator();
		if (this.originalList!=null)
			return new SafeIterator<M>(this.originalList.iterator());
		return new SafeIterator<M>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		if (this.originalCollection!=null)
			return this.originalCollection.size();
		if (this.originalList!=null)
			return this.originalList.size();
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		if (this.originalCollection!=null)
			return this.originalCollection.toArray();
		if (this.originalList!=null)
			return this.originalList.toArray();
		return new Object[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		if (this.originalCollection!=null)
			return this.originalCollection.toArray(a);
		if (this.originalList!=null)
			return this.originalList.toArray(a);
		return a;
	}
	
}
