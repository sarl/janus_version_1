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
package org.janusproject.kernel.bench.api;

import java.util.Comparator;
import java.util.Iterator;

import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.util.comparator.GenericComparator;
import org.janusproject.kernel.util.selector.Selector;

/** A mailbox that contains an infinite number of messages.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class InfiniteMailBox implements Mailbox, Iterator<Message> {

	private static final long serialVersionUID = 2520023104303100755L;

	/**
	 */
	public InfiniteMailBox() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Message msg) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronize(Mailbox mailbox) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Message msg) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Selector<? extends Message> selector) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Message msg) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Selector<? extends Message> selector) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message removeFirst() {
		return new StringMessage(""); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message getFirst() {
		return new StringMessage(""); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T removeFirst(Selector<T> selector) {
		return selector.getSupportedClass().cast(new StringMessage("")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T getFirst(Selector<T> selector) {
		return selector.getSupportedClass().cast(new StringMessage("")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message remove(int index) {
		return new StringMessage(""); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message get(int index) {
		return new StringMessage(""); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message removeFirst(long timeout) {
		return new StringMessage(""); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message getFirst(long timeout) {
		return new StringMessage(""); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T removeFirst(Selector<T> selector,
			long timeout) {
		return selector.getSupportedClass().cast(new StringMessage("")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T getFirst(Selector<T> selector, long timeout) {
		return selector.getSupportedClass().cast(new StringMessage("")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Message> Iterator<T> iterator(Selector<T> selector) {
		return (Iterator<T>)this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Message> iterator() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Message> Iterator<T> iterator(Selector<T> selector,
			boolean consumeMails) {
		return (Iterator<T>)this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Message> iterator(boolean consumeMails) {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Comparator<? super Message> comparator() {
		return GenericComparator.SINGLETON;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message next() {
		return new StringMessage(""); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Message> Iterator<T> iterator(Class<T> type) {
		return (Iterator<T>)this;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Message> Iterable<T> iterable(Class<T> type) {
		return (Iterable<T>)this;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Message> Iterable<T> iterable(Selector<T> selector) {
		return (Iterable<T>)this;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Message> Iterable<T> iterable(Selector<T> selector,
			boolean consumeMails) {
		return (Iterable<T>)this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<Message> iterable(boolean consumeMails) {
		return this;
	}

}
