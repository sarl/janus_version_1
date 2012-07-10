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
package org.janusproject.kernel.mailbox;

import java.util.Iterator;

import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.util.selector.Selector;
import org.janusproject.kernel.util.selector.TypeSelector;

/**
 * Abstract implementation of a {@link Mailbox}.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see LinkedListMailbox
 * @see TreeSetMailbox
 */
public abstract class AbstractMailbox implements Mailbox {

	private static final long serialVersionUID = -5713042336297958425L;

	/**
	 * Create mailbox manager.
	 */
	public AbstractMailbox() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Message getFirst(long timeout) {
		Message msg = null;

		long timeoutDate = System.currentTimeMillis() + timeout; 
		
		msg = getFirst();
			
		while (msg==null && System.currentTimeMillis() < timeoutDate) {
			Thread.yield();
			msg = getFirst();
		}
		
		return msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T extends Message> T getFirst(Selector<T> selector, long timeout) {
		T msg = null;

		long timeoutDate = System.currentTimeMillis() + timeout; 
		
		msg = getFirst(selector);
			
		while (msg==null && System.currentTimeMillis() < timeoutDate) {
			Thread.yield();
			msg = getFirst(selector);
		}
		
		return msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Message removeFirst(long timeout) {
		Message msg = null;

		long timeoutDate = System.currentTimeMillis() + timeout; 
		
		msg = removeFirst();
			
		while (msg==null && System.currentTimeMillis() < timeoutDate) {
			Thread.yield();
			msg = removeFirst();
		}
		
		return msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T extends Message> T removeFirst(Selector<T> selector, long timeout) {
		T msg = null;

		long timeoutDate = System.currentTimeMillis() + timeout; 
		
		msg = removeFirst(selector);
			
		while (msg==null && System.currentTimeMillis() < timeoutDate) {
			Thread.yield();
			msg = removeFirst(selector);
		}
		
		return msg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<Message> iterator() {
		return iterator(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T extends Message> Iterator<T> iterator(Selector<T> selector) {
		return iterator(selector, true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T extends Message> Iterator<T> iterator(Class<T> type) {
		return iterator(new TypeSelector<T>(type));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T extends Message> Iterable<T> iterable(Class<T> type) {
		return iterable(new TypeSelector<T>(type));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T extends Message> Iterable<T> iterable(final Selector<T> selector) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return AbstractMailbox.this.iterator(selector);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T extends Message> Iterable<T> iterable(final Selector<T> selector,
			final boolean consumeMails) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return AbstractMailbox.this.iterator(selector, consumeMails);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterable<Message> iterable(final boolean consumeMails) {
		return new Iterable<Message>() {
			@Override
			public Iterator<Message> iterator() {
				return AbstractMailbox.this.iterator(consumeMails);
			}
		};
	}
}
