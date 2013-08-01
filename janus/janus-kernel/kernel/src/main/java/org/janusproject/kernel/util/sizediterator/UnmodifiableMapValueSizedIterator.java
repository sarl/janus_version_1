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
package org.janusproject.kernel.util.sizediterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;


/**
 * Class that map the values of a Map to an unmodifiable sized iterator.
 * <p>
 * A sized iterator is an Iterator that is able to
 * reply the size of the iterated collection and
 * the number of elements that may be encountered
 * in the next iterations.
 * <p>
 * This class provides features closed to {@link Collections#unmodifiableMap(Map)}
 * but with a sized iterators on the values of the map.
 * To obtain similar features on the map keys, see {@link UnmodifiableMapKeySizedIterator}.
 * <p>
 * This iterator disables the use of the function {@link #remove()}.
 * 
 * @param <V> is the type of values.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see UnmodifiableMapKeySizedIterator
 */
public class UnmodifiableMapValueSizedIterator<V>
implements SizedIterator<V> {

	private final int total;
	private int rest;
	private final Iterator<V> iterator;
	
	/**
	 * @param map
	 */
	public UnmodifiableMapValueSizedIterator(Map<?,V> map) {
		assert(map!=null);
		this.total = this.rest = map.size();
		this.iterator = map.values().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int rest() {
		return this.rest;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int totalSize() {
		return this.total;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V next() {
		V elt = this.iterator.next();
		--this.rest;
		return elt;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
