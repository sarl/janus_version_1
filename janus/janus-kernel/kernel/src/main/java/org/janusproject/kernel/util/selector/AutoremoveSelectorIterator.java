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
package org.janusproject.kernel.util.selector;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator on messages that are matching the given selector.
 * The replied messages are automatically removed
 * from the original collection.
 * 
 * @param <M> is the type of mail in the iterator.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AutoremoveSelectorIterator<M>
implements Iterator<M> {

	private final Selector<? extends M> selector;
	private final Iterator<?> original;
	private M next;

	/**
	 * @param selector
	 * @param iterator an iterator which is implementing the <code>remove</code> function.
	 */
	public AutoremoveSelectorIterator(Selector<? extends M> selector, Iterator<?> iterator) {
		assert(selector!=null);
		assert(iterator!=null);
		this.selector = selector;
		this.original = iterator;
		searchNext();
	}
	
	private void searchNext() {
		this.next = null;
		Object obj;
		while (this.next==null && this.original.hasNext()) {
			obj = this.original.next();
			if (this.selector.isSelected(obj)) {
				this.next = this.selector.getSupportedClass().cast(obj);
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
		// Consume the message
		// assuming that the internal iterator has stopped
		// its iteration when found the next element
		this.original.remove();
		searchNext();
		return n;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {
		//
	}

}
