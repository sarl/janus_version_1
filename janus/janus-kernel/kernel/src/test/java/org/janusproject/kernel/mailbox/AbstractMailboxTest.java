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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.message.CreationDateMessageComparator;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageStub;
import org.janusproject.kernel.message.MessageStub2;
import org.janusproject.kernel.util.selector.Selector;
import org.janusproject.kernel.util.selector.TypeSelector;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AbstractMailboxTest extends TestCase {

	private AbstractMailbox mailbox;
	private MessageStub m1;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.mailbox = new AbstractMailboxTestStub();
		this.m1 = new MessageStub(1024f, "m1"); //$NON-NLS-1$
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.mailbox = null;
		this.m1 = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetFirstLong() {
		assertSame(this.m1, this.mailbox.getFirst(500));
	}

	/**
	 */
	public void testGetFirstSelectorLong() {
		assertSame(this.m1, this.mailbox.getFirst(new TypeSelector<MessageStub>(MessageStub.class), 500));
		assertNull(this.mailbox.getFirst(new TypeSelector<MessageStub2>(MessageStub2.class), 500));
	}

	/**
	 */
	public void testRemoveFirstLong() {
		assertSame(this.m1, this.mailbox.removeFirst(500));
	}

	/**
	 */
	public void testRemoveFirstSelectorLong() {
		assertNull(this.mailbox.removeFirst(new TypeSelector<MessageStub2>(MessageStub2.class), 500));
		assertSame(this.m1, this.mailbox.removeFirst(new TypeSelector<MessageStub>(MessageStub.class), 500));
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AbstractMailboxTestStub extends AbstractMailbox {
		
		private static final long serialVersionUID = -5931657486275068644L;

		private boolean removed = false;
		
		/**
		 */
		public AbstractMailboxTestStub() {
			//
		}

		@Override
		public boolean add(Message msg) {
			return false;
		}

		@Override
		public void synchronize(Mailbox mailbox) {
			//
		}

		@Override
		public void clear() {
			this.removed = true;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public boolean contains(Message msg) {
			if (this.removed) return false;
			return AbstractMailboxTest.this.m1.equals(msg);
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public boolean contains(Selector<? extends Message> selector) {
			if (this.removed) return false;
			return selector.isSelected(AbstractMailboxTest.this.m1);
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public MessageStub get(int index) {
			if (this.removed) return null;
			return (index==0) ? AbstractMailboxTest.this.m1 : null;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public MessageStub getFirst() {
			if (this.removed) return null;
			return AbstractMailboxTest.this.m1;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public <T extends Message> T getFirst(Selector<T> selector) {
			if (this.removed) return null;
			return selector.isSelected(AbstractMailboxTest.this.m1) 
					? 
						selector.getSupportedClass().cast(AbstractMailboxTest.this.m1)
					:	null;
		}

		@Override
		public boolean isEmpty() {
			return this.removed;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public Iterator<Message> iterator(boolean ignored) {
			if (this.removed)
				return Collections.<Message>emptyList().iterator();
			return Arrays.<Message>asList(AbstractMailboxTest.this.m1).iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings({ "synthetic-access", "unchecked" })
		@Override
		public <T extends Message> Iterator<T> iterator(Selector<T> selector, boolean ignored) {
			if (this.removed)
				return Collections.<T>emptyList().iterator();
			return Arrays.<T>asList(
					selector.getSupportedClass().cast(AbstractMailboxTest.this.m1)).iterator();
		}

		@Override
		public boolean remove(Message msg) {
			return false;
		}

		@Override
		public MessageStub remove(int index) {
			MessageStub f = getFirst();
			this.removed = true;
			return f;
		}

		@Override
		public boolean removeAll(Selector<? extends Message> selector) {
			boolean r = this.removed;
			this.removed = true;
			return !r;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public MessageStub removeFirst() {
			if (this.removed) return null;
			this.removed = true;
			return AbstractMailboxTest.this.m1;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public <T extends Message> T removeFirst(Selector<T> selector) {
			if (this.removed) return null;
			if (selector.isSelected(AbstractMailboxTest.this.m1)) {
				this.removed = true;
				return selector.getSupportedClass().cast(AbstractMailboxTest.this.m1);
			}
			return null;
		}

		@Override
		public int size() {
			return this.removed ? 0 : 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Comparator<? super Message> comparator() {
			return CreationDateMessageComparator.SINGLETON;
		}

	}
	
}
