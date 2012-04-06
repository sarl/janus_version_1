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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.janusproject.kernel.util.sizediterator.SizedIterator;


/** Safe iterator.
 * <p>
 * You may invoke {@link #release()} when you have not more usage of the iterator.
 *
 * @param <E> is the type of element in the collection.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SafeIterator<E>
implements SizedIterator<E> {

	private AsynchronousThreadSafeCollection<E> collection;
	private Iterator<E> iterator;
	private E returnedElement = null;
	private int iterated = 0;

	/**
	 * @param col
	 * @param iter
	 */
	SafeIterator(AsynchronousThreadSafeCollection<E> col, Iterator<E> iter) {
		assert(col!=null);
		assert(iter!=null);
		this.collection = col;
		this.iterator = iter;
		this.collection.allocateIterator(this);
	}

	/**
	 * @param iter
	 */
	public SafeIterator(Iterator<E> iter) {
		assert(iter!=null);
		this.collection = null;
		this.iterator = iter;
	}

	/**
	 */
	public SafeIterator() {
		this.collection = null;
		this.iterator = null;
	}
	
	/** Release this iterator.
	 * <p>
	 * Associated collection may be updated.
	 */
	public synchronized void release() {
		if (this.collection!=null) {
			this.collection.releaseIterator(this);
		}
		this.iterator = null;
	}
	
	/** Set the collection attached to this safe iterator if
	 * no collection was already attached to this.
	 * 
	 * @param collection is the collection to attach.
	 */
	public synchronized void attachCollection(AsynchronousThreadSafeCollection<E> collection) {
		assert(collection!=null);
		if (this.collection==null) {
			this.collection = collection;
			this.collection.allocateIterator(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finalize() throws Throwable {
		release();
		super.finalize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized boolean hasNext() {
		if (this.iterator==null)
			return false;
		if (!this.iterator.hasNext()) {
			release();
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized E next() {
		if (this.iterator==null) {
			release();
			throw new NoSuchElementException();
		}
		E n = this.iterator.next();
		if (!this.iterator.hasNext()) {
			release();
		}
		++this.iterated;
		return this.returnedElement = n;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void remove() {
		if (this.collection!=null && this.returnedElement!=null) {
			if (!this.collection.remove(this.returnedElement)) { // Use remove() because it is asynchronous collection
				throw new NoSuchElementException();
			}
		}
		else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized int rest() {
		if (this.collection==null)
			throw new IllegalStateException();
		return Math.max(0, this.collection.size() - this.iterated);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized int totalSize() {
		if (this.collection==null)
			throw new IllegalStateException();
		return this.collection.size();
	}

}
