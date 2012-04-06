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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.janusproject.kernel.util.multicollection.DoubleSizedIterator;


/**
 * Make unmodifiable the result of the fusion of a set and a list.
 * 
 * @param <M> is the type of element.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class UnmodifiableDirectAccessSetList<M>
implements DirectAccessCollection<M> {

	private final Set<M> originalSet;
	private final List<M> originalList;

	/**
	 * @param set
	 * @param list
	 */
	public UnmodifiableDirectAccessSetList(Set<M> set, List<M> list) {
		assert(set!=null);
		assert(list!=null);
		this.originalSet = set;
		this.originalList = list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public M get(int position) {
		int index = position;
		if (index<0) throw new IndexOutOfBoundsException();
		if (index<this.originalSet.size()) {
			Iterator<M> iterator = this.originalSet.iterator();
			int i=0;
			while (iterator.hasNext() && i<index) {
				iterator.next();
				++i;
			}
			if (iterator.hasNext())
				return iterator.next();
		}
		index -= this.originalSet.size();
		return this.originalList.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object o) {
		return this.originalSet.contains(o) || this.originalList.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		assert(c!=null);
		for(Object o : c) {
			if (!this.originalSet.contains(o) && !this.originalList.contains(o))
				return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.originalSet.isEmpty() && this.originalList.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SafeIterator<M> iterator() {
		return new SafeIterator<M>(new DoubleSizedIterator<M>(this.originalSet, this.originalList));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.originalSet.size() + this.originalList.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		Object[] tab = new Object[size()];
		this.originalSet.toArray(tab);
		Object[] t = this.originalList.toArray();
		System.arraycopy(t, 0, tab, this.originalSet.size(), t.length);
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
		this.originalSet.toArray(t);
		Object[] tt = this.originalList.toArray();
		System.arraycopy(tt, 0, t, this.originalSet.size(), tt.length);
		return t;
	}
	
}
