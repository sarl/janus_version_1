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
package org.janusproject.kernel.util.multicollection;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * Iterator on elements that is iterate on two given collections.
 * <p>
 * Removal is not supported.
 * 
 * @param <M> is the type of element.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DoubleSizedIterator<M>
implements SizedIterator<M> {

	private Collection<? extends M> collection1;
	private Collection<? extends M> collection2;
	private Iterator<? extends M> original;
	private M next;
	private final int total;
	private int rest;

	/**
	 * @param collection1
	 * @param collection2
	 */
	public DoubleSizedIterator(Collection<? extends M> collection1, Collection<? extends M> collection2) {
		assert(collection1!=null);
		assert(collection2!=null);
		this.collection1 = collection1;
		this.collection2 = collection2;
		this.total = this.rest = this.collection1.size() + this.collection2.size();
		this.original = this.collection1.iterator();
		searchNext();
	}
	
	private void searchNext() {
		this.next = null;
		M m;
		if (this.collection1!=null) {
			while (this.next==null && this.original.hasNext()) {
				m = this.original.next();
				this.next = m;
			}
		}
		if (this.next==null) {
			if (this.collection1!=null) {
				this.collection1 = null;
				this.original = this.collection2.iterator();
			}
			while (this.next==null && this.original.hasNext()) {
				m = this.original.next();
				this.next = m;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return this.next!=null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public M next() {
		M n = this.next;
		if (n==null) throw new NoSuchElementException();
		searchNext();
		this.rest --;
		return n;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
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

}
