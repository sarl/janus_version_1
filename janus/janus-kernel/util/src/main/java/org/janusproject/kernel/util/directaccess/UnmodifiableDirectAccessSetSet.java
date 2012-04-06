/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.janusproject.kernel.util.multicollection.DoubleSizedIterator;


/**
 * Make unmodifiable the result of the fusion of two sets.
 * 
 * @param <M> is the type of element.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.4
 */
public class UnmodifiableDirectAccessSetSet<M>
implements DirectAccessCollection<M> {

	private final Set<M> originalSet1;
	private final Set<M> originalSet2;

	/**
	 * @param set1
	 * @param set2
	 */
	public UnmodifiableDirectAccessSetSet(Set<M> set1, Set<M> set2) {
		if (set1==null)
			this.originalSet1 = Collections.emptySet();
		else
			this.originalSet1 = set1;
		if (set2==null)
			this.originalSet2 = Collections.emptySet();
		else
			this.originalSet2 = set2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public M get(int position) {
		int index = position;
		if (index<0) throw new IndexOutOfBoundsException();
		if (index<this.originalSet1.size()) {
			Iterator<M> iterator = this.originalSet1.iterator();
			int i=0;
			while (iterator.hasNext() && i<index) {
				iterator.next();
				++i;
			}
			if (iterator.hasNext())
				return iterator.next();
		}
		index -= this.originalSet1.size();
		if (index<this.originalSet2.size()) {
			Iterator<M> iterator = this.originalSet2.iterator();
			int i=0;
			while (iterator.hasNext() && i<index) {
				iterator.next();
				++i;
			}
			if (iterator.hasNext())
				return iterator.next();
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object o) {
		return this.originalSet1.contains(o) || this.originalSet2.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		assert(c!=null);
		for(Object o : c) {
			if (!this.originalSet1.contains(o) && !this.originalSet2.contains(o))
				return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.originalSet1.isEmpty() && this.originalSet2.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SafeIterator<M> iterator() {
		return new SafeIterator<M>(new DoubleSizedIterator<M>(this.originalSet1, this.originalSet2));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.originalSet1.size() + this.originalSet2.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		Object[] tab = new Object[size()];
		this.originalSet1.toArray(tab);
		Object[] t = this.originalSet2.toArray();
		System.arraycopy(t, 0, tab, this.originalSet1.size(), t.length);
		return tab;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		assert(a!=null);
		T[] t;
		if (a.length<size()) {
			t = (T[])Array.newInstance(a.getClass().getComponentType(), size());
		}
		else {
			t = a;
		}
		this.originalSet1.toArray(t);
		Object[] tt = this.originalSet2.toArray();
		System.arraycopy(tt, 0, t, this.originalSet1.size(), tt.length);
		return t;
	}
	
}
