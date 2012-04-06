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
package org.janusproject.kernel.mailbox;

import java.util.Comparator;
import java.util.Iterator;

import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.CreationDateMessageComparator;
import org.janusproject.kernel.util.autoremove.AutoremoveIterator;
import org.janusproject.kernel.util.directaccess.AsynchronousThreadSafeCollection;
import org.janusproject.kernel.util.directaccess.SafeIterator;
import org.janusproject.kernel.util.selector.AutoremoveSelectorIterator;
import org.janusproject.kernel.util.selector.Selector;
import org.janusproject.kernel.util.selector.SelectorIterator;


/**
 * This class provides a THREAD-SAFE {@link AsynchronousThreadSafeCollection}-based implementation
 * of a {@link BufferedMailbox}.
 * <p>
 * Caution: this mail box is a {@link BufferedMailbox}. It implies that
 * the added messages will be available only after the function {@link #synchronizeMessages()}
 * is invoked.
 * <p>
 * This implementation is thread-safe.
 * <p>
 * All the mailbox implementations must ensure that, when a message should be added,
 * the comparator is invoked as: <code>comparator(newMessage, messageAlreadyInMailbox)</code>.
 * It means that the first parameter given to the comparator is always the
 * parameter of the addition function.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ThreadSafeMailbox extends AbstractMailbox implements BufferedMailbox {

	private static final long serialVersionUID = 4172699639389621190L;
	
	/** Inbox.
	 */
	protected final AsynchronousThreadSafeCollection<Message> inbox;

	/**
	 * Create mailbox manager that sorts the message by their creation date.
	 */
	public ThreadSafeMailbox() {
		this(null);
	}

	/**
	 * Create mailbox manager.
	 * 
	 * @param comparator is the comparator that is used to sort the messages in the mailbox.
	 * @since 0.5
	 */
	public ThreadSafeMailbox(Comparator<? super Message> comparator) {
		Comparator<? super Message> c;
		if (comparator==null) c = CreationDateMessageComparator.SINGLETON;
		else c = comparator;
		this.inbox = new AsynchronousThreadSafeCollection<Message>(
				Message.class,
				c);
		this.inbox.setAutoApplyEnabled(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Comparator<? super Message> comparator() {
		return this.inbox.comparator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Message msg) {
		return this.inbox.add(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronize(Mailbox mailbox) {
		assert(mailbox!=null);
		this.inbox.clear();
		for(Message msg : mailbox) {
			this.inbox.add(msg);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.inbox.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Message msg) {
		assert(msg!=null);
		return this.inbox.contains(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Selector<? super Message> selector) {
		assert(selector!=null);
		Iterator<Message> itr = this.inbox.iterator();
		Message msg;
		while(itr.hasNext()) {
			msg = itr.next();
			if (selector.isSelected(msg)) return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message get(int index) {
		if (index<0 || index>=this.inbox.size()) return null;
		return this.inbox.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message getFirst() {
		if (this.inbox.isEmpty()) return null;
		return this.inbox.get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message getFirst(Selector<? super Message> selector) {
		assert(selector!=null);
		if (this.inbox.isEmpty()) return null;
		
		Iterator<Message> itr = this.inbox.iterator();
		Message msg;
		while(itr.hasNext()) {
			msg = itr.next();
			if (selector.isSelected(msg)) return msg;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.inbox.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Message msg) {
		return this.inbox.remove(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message remove(int index) {
		return this.inbox.remove(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Selector<? super Message> selector) {
		assert(selector!=null);
		Iterator<Message> iterator = this.inbox.iterator();
		boolean changed = false;
		Message m;
		while (iterator.hasNext()) {
			m = iterator.next();
			if (selector.isSelected(m)) {
				iterator.remove();
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message removeFirst() {
		SafeIterator<Message> iterator = this.inbox.iterator();
		try {
			if (!iterator.hasNext()) return null;
			Message firstMessage = iterator.next();
			iterator.remove();
			return firstMessage;
		}
		finally {
			iterator.release();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message removeFirst(Selector<? super Message> selector) {
		assert(selector!=null);
		if (this.inbox.isEmpty()) return null;
		Iterator<Message> iterator = this.inbox.iterator();
		Message m;
		while (iterator.hasNext()) {
			m = iterator.next();
			if (selector.isSelected(m)) {
				iterator.remove();
				return m;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.inbox.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Message> iterator(boolean consumeMails) {
		if (consumeMails)
			return new AutoremoveIterator<Message>(this.inbox.iterator());
		return this.inbox.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Message> iterator(Selector<? super Message> selector, boolean consumeMails) {
		if (consumeMails)
			return new AutoremoveSelectorIterator<Message>(selector, this.inbox.iterator());
		return new SelectorIterator<Message>(selector, this.inbox.iterator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearBuffer() {
		this.inbox.removePendingElements();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBufferSize() {
		return this.inbox.getPendingElementCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBufferEmpty() {
		return !this.inbox.hasPendingElement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronizeMessages() {
		this.inbox.applyChanges(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.inbox.toString();
	}

}