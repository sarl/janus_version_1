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
import java.util.LinkedList;
import java.util.List;

import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.CreationDateMessageComparator;
import org.janusproject.kernel.util.autoremove.AutoremoveIterator;
import org.janusproject.kernel.util.directaccess.ListUtil;
import org.janusproject.kernel.util.selector.AutoremoveSelectorIterator;
import org.janusproject.kernel.util.selector.Selector;
import org.janusproject.kernel.util.selector.SelectorIterator;

/**
 * This class provides a {@link LinkedList}-based implementation of a {@link Mailbox}.
 * <p>
 * The list of mails is sorted according to the creation date of the mails.
 * <p>
 * This implementation is not thread-safe.
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
 * @see TreeSetMailbox
 */
public class LinkedListMailbox extends AbstractMailbox {

	private static final long serialVersionUID = -587305548270297219L;
	
	/** Linked list.
	 */
	protected final List<Message> inbox;
	
	private final Comparator<? super Message> messageComparator;

	/**
	 * Create mailbox manager that sorts the messages by their creation date.
	 */
	public LinkedListMailbox() {
		this(null);
	}

	/**
	 * Create mailbox manager that sorts the messages by their creation date.
	 * 
	 * @param comparator is the comparator that is used to sort the messages in the mailbox.
	 * @since 0.5
	 */
	public LinkedListMailbox(Comparator<? super Message> comparator) {
		if (comparator==null) this.messageComparator = CreationDateMessageComparator.SINGLETON;
		else this.messageComparator = comparator;
		this.inbox = new LinkedList<Message>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Comparator<? super Message> comparator() {
		return this.messageComparator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Message msg) {
		return ListUtil.dichotomicAdd(this.inbox, this.messageComparator, msg, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronize(Mailbox mailbox) {
		assert(mailbox!=null);
		this.inbox.clear();
		for(Message msg : mailbox) {
			ListUtil.dichotomicAdd(this.inbox, this.messageComparator, msg, true);
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
	public boolean contains(Selector<? extends Message> selector) {
		assert(selector!=null);
		for(Message msg : this.inbox) {
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
	public <T extends Message> T getFirst(Selector<T> selector) {
		assert(selector!=null);
		if (this.inbox.isEmpty()) return null;
		for(Message msg : this.inbox) {
			if (selector.isSelected(msg))
				return selector.getSupportedClass().cast(msg);
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
		if (index<0 || index>=this.inbox.size()) return null;
		return this.inbox.remove(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Selector<? extends Message> selector) {
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
		Iterator<Message> iterator = this.inbox.iterator();
		if (!iterator.hasNext()) return null;
		Message firstMessage = iterator.next();
		iterator.remove();
		return firstMessage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T removeFirst(Selector<T> selector) {
		assert(selector!=null);
		if (this.inbox.isEmpty()) return null;
		Iterator<Message> iterator = this.inbox.iterator();
		Message m;
		while (iterator.hasNext()) {
			m = iterator.next();
			if (selector.isSelected(m)) {
				iterator.remove();
				return selector.getSupportedClass().cast(m);
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
	public <T extends Message> Iterator<T> iterator(Selector<T> selector, boolean consumeMails) {
		if (consumeMails)
			return new AutoremoveSelectorIterator<T>(
					selector, this.inbox.iterator());
		return new SelectorIterator<T>(
				selector, this.inbox.iterator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.inbox.toString();
	}

}
