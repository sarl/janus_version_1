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
import java.util.Iterator;
import java.util.Set;


/**
 * Make unmodifiable a DirectAccessCollection from a set.
 * 
 * @param <M> is the type of element.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class UnmodifiableDirectAccessSet<M>
implements DirectAccessCollection<M> {

	private final Set<M> originalCollection;

	/**
	 * @param collection
	 */
	public UnmodifiableDirectAccessSet(Set<M> collection) {
		assert(collection!=null);
		this.originalCollection = collection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public M get(int position) {
		if (position<0 || position>=this.originalCollection.size())
			throw new IndexOutOfBoundsException();
		Iterator<M> iterator = this.originalCollection.iterator();
		int i=0;
		while (iterator.hasNext() && i<position) {
			iterator.next();
			++i;
		}
		if (iterator.hasNext())
			return iterator.next();
		throw new IndexOutOfBoundsException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object o) {
		return this.originalCollection.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.originalCollection.containsAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.originalCollection.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SafeIterator<M> iterator() {
		return new SafeIterator<M>(this.originalCollection.iterator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.originalCollection.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return this.originalCollection.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return this.originalCollection.toArray(a);
	}
	
}
