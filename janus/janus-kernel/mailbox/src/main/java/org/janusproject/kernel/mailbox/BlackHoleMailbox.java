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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.util.comparator.GenericComparator;
import org.janusproject.kernel.util.selector.Selector;

/**
 * This class provides an implementation of a {@link Mailbox} that
 * is detroying a message then it arrive in the mailbox.
 * The consequence is that this type of mailbox is always empty.
 * <p>
 * This implementation is thread-safe.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BlackHoleMailbox extends AbstractMailbox {

	private static final long serialVersionUID = -5978873992959822330L;
	
	private final int insertionDelay;
	private final int removalDelay;
	private final int readingDelay;
	
	/**
	 * Create mailbox manager.
	 */
	public BlackHoleMailbox() {
		this(0, 0, 0);
	}

	/**
	 * Create mailbox manager.
	 * 
	 * @param insertionDelay is the delay applied at each insertion action (in nanocesonds).
	 * @param removalDelay is the delay applied at each removal action (in nanocesonds).
	 * @param readingDelay is the delay applied at each reading action (in nanocesonds).
	 */
	public BlackHoleMailbox(int insertionDelay, int removalDelay, int readingDelay) {
		this.insertionDelay = insertionDelay;
		this.removalDelay = removalDelay;
		this.readingDelay = readingDelay;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Message msg) {
		if (this.insertionDelay>0) {
			try {
				Thread.sleep(0, this.insertionDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void synchronize(Mailbox mailbox) {
		for(Message m : mailbox) {
			add(m);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Message msg) {
		if (this.removalDelay>0) {
			try {
				Thread.sleep(0, this.removalDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(Selector<? extends Message> selector) {
		if (this.removalDelay>0) {
			try {
				Thread.sleep(0, this.removalDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Message msg) {
		if (this.readingDelay>0) {
			try {
				Thread.sleep(0, this.readingDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Selector<? extends Message> selector) {
		if (this.readingDelay>0) {
			try {
				Thread.sleep(0, this.readingDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		if (this.removalDelay>0) {
			try {
				Thread.sleep(0, this.removalDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message removeFirst() {
		if (this.removalDelay>0) {
			try {
				Thread.sleep(0, this.removalDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message getFirst() {
		if (this.readingDelay>0) {
			try {
				Thread.sleep(0, this.readingDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T removeFirst(Selector<T> selector) {
		if (this.removalDelay>0) {
			try {
				Thread.sleep(0, this.removalDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> T getFirst(Selector<T> selector) {
		if (this.readingDelay>0) {
			try {
				Thread.sleep(0, this.readingDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message remove(int index) {
		if (this.removalDelay>0) {
			try {
				Thread.sleep(0, this.removalDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Message get(int index) {
		if (this.readingDelay>0) {
			try {
				Thread.sleep(0, this.readingDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends Message> Iterator<T> iterator(Selector<T> selector,
			boolean consumeMails) {
		if (this.readingDelay>0) {
			try {
				Thread.sleep(0, this.readingDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return Collections.<T>emptyList().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Message> iterator(boolean consumeMails) {
		if (this.readingDelay>0) {
			try {
				Thread.sleep(0, this.readingDelay);
			}
			catch (InterruptedException _) {
				//
			}
		}
		return Collections.<Message>emptyList().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Comparator<? super Message> comparator() {
		return GenericComparator.SINGLETON;
	}

}
