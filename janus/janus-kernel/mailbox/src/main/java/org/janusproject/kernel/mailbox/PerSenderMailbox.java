/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
import java.util.Map.Entry;
import java.util.TreeMap;

import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.message.CreationDateMessageComparator;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.util.autoremove.AutoremoveIterator;
import org.janusproject.kernel.util.selector.AutoremoveSelectorIterator;
import org.janusproject.kernel.util.selector.Selector;
import org.janusproject.kernel.util.selector.SelectorIterator;

/**
 * This class provides a {@link TreeMap}-based implementation of a {@link Mailbox}
 * that is storing the last message for all the senders.
 * <p>
 * This implementation is not thread-safe.
 * <p>
 * All the mailbox implementations must ensure that, when a message should be added,
 * the comparator is invoked as: <code>comparator(newMessage, messageAlreadyInMailbox)</code>.
 * It means that the first parameter given to the comparator is always the
 * parameter of the addition function.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class PerSenderMailbox extends AbstractMailbox {

	private static final long serialVersionUID = 6831855797011126547L;
	
	/** Tree Set.
	 */
	protected final TreeMap<Address,Message> inbox;
	
	private final Comparator<? super Message> comparator;

	/**
	 * Create mailbox manager that sorts the message by their creation date.
	 */
	public PerSenderMailbox() {
		this(CreationDateMessageComparator.SINGLETON);
	}
	
	/**
	 * Create mailbox manager that sorts the message by their creation date.
	 * 
	 * @param comparator is the comparator to use to compare the messages for a sender.
	 */
	public PerSenderMailbox(Comparator<? super Message> comparator) {
		assert(comparator!=null);
		this.inbox = new TreeMap<Address,Message>();
		this.comparator = comparator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Comparator<? super Message> comparator() {
		return this.comparator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Message msg) {
		assert(msg!=null);
		synchronized(this.inbox) {
			Address sender = msg.getSender();
			assert(sender!=null);
			Message old = this.inbox.get(sender);
			if (old==null || this.comparator.compare(old, msg)<0) {
				this.inbox.put(sender,msg);
				return true;
			}
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronize(Mailbox mailbox) {
		assert(mailbox!=null);
		this.inbox.clear();
		for(Message msg : mailbox) {
			Address sender = msg.getSender();
			assert(sender!=null);
			Message old = this.inbox.get(sender);
			if (old==null || this.comparator.compare(old, msg)<0) {
				this.inbox.put(sender,msg);
			}
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
		return this.inbox.containsValue(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Selector<? extends Message> selector) {
		assert(selector!=null);
		for(Message msg : this.inbox.values()) {
			if (selector.isSelected(msg)) return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message get(int index) {
		try {
			int i=0;
			for(Message msg : this.inbox.values()) {
				if (i==index) return msg;
				++i;
			}
		}
		catch(Throwable _) {
			//
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message getFirst() {
		try {
			return this.inbox.firstEntry().getValue();
		}
		catch(Throwable _) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T getFirst(Selector<T> selector) {
		assert(selector!=null);
		try {
			for(Message msg : this.inbox.values()) {
				if (selector.isSelected(msg))
					return selector.getSupportedClass().cast(msg);
			}
		}
		catch(Throwable _) {
			//
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
		synchronized(this.inbox) {
			Message old = this.inbox.get(msg.getSender());
			if (old!=null && old.equals(msg)) {
				this.inbox.remove(msg.getSender());
				return true;
			}
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message remove(int index) {
		if (index<0 || index>=this.inbox.size()) return null;
		int i=0;
		Iterator<Entry<Address,Message>> iterator = this.inbox.entrySet().iterator();
		Entry<Address,Message> m;
		while (iterator.hasNext()) {
			m = iterator.next();
			if (i==index) {
				iterator.remove();
				return m.getValue();
			}
			++i;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Selector<? extends Message> selector) {
		assert(selector!=null);
		Iterator<Entry<Address,Message>> iterator = this.inbox.entrySet().iterator();
		boolean changed = false;
		Entry<Address,Message> m;
		while (iterator.hasNext()) {
			m = iterator.next();
			if (selector.isSelected(m.getValue())) {
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
		Iterator<Entry<Address,Message>> iterator = this.inbox.entrySet().iterator();
		if (!iterator.hasNext()) return null;
		Entry<Address,Message> firstMail = iterator.next();
		iterator.remove();
		return firstMail.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T removeFirst(Selector<T> selector) {
		assert(selector!=null);
		try {
			Iterator<Entry<Address,Message>> iterator = this.inbox.entrySet().iterator();
			Entry<Address,Message> m;
			while (iterator.hasNext()) {
				m = iterator.next();
				if (selector.isSelected(m.getValue())) {
					iterator.remove();
					return selector.getSupportedClass().cast(m.getValue());
				}
			}
		}
		catch(Throwable _) {
			//
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
			return new AutoremoveIterator<Message>(this.inbox.values().iterator());
		return this.inbox.values().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> Iterator<T> iterator(Selector<T> selector, boolean consumeMails) {
		if (consumeMails)
			return new AutoremoveSelectorIterator<T>(
					selector, this.inbox.values().iterator());
		return new SelectorIterator<T>(
				selector, this.inbox.values().iterator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.inbox.toString();
	}

}
