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
package org.janusproject.kernel.mailbox;

import java.util.Iterator;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.CreationDateMessageComparator;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see ThreadSafeMailbox
 */
public class ThreadSafeMailboxTest extends TestCase {

	private ThreadSafeMailbox mailbox;
	private MessageStub m1;
	private MessageStub m2;
	private MessageStub m3;
	private MessageStub2 m4;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.mailbox = new ThreadSafeMailbox();
		this.m1 = new MessageStub(2047f, "m1"); //$NON-NLS-1$
		this.m2 = new MessageStub(1024f, "m2"); //$NON-NLS-1$
		this.m3 = new MessageStub(4096f, "m3"); //$NON-NLS-1$
		this.m4 = new MessageStub2(4096f, "m4"); //$NON-NLS-1$
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.mailbox = null;
		this.m1 = this.m2 = this.m3 = null;
		this.m4 = null;
		super.tearDown();
	}
	
	private void initMailbox() {
		this.mailbox.add(this.m1);
		this.mailbox.add(this.m2);
		this.mailbox.synchronizeMessages();
	}

	/**
	 */
	public void testClear_withoutIterator() {
		initMailbox();
		assertFalse(this.mailbox.isEmpty());
		this.mailbox.clear();
		assertTrue(this.mailbox.isEmpty()); // true because no iterator on
		this.mailbox.synchronizeMessages();
		assertTrue(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testClear_withIterator() {
		initMailbox();
		assertFalse(this.mailbox.isEmpty());
		Iterator<Message> iterator = this.mailbox.iterator();
		this.mailbox.clear();
		assertFalse(this.mailbox.isEmpty()); // false because iterator on
		iterator.hasNext();
		this.mailbox.synchronizeMessages();
		assertTrue(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testContainsMail() {
		initMailbox();
		assertTrue(this.mailbox.contains(this.m1));
		assertTrue(this.mailbox.contains(this.m2));
		assertFalse(this.mailbox.contains(this.m3));
		assertFalse(this.mailbox.contains(this.m4));
	}

	/**
	 */
	public void testContainsSelector() {
		initMailbox();
		assertTrue(this.mailbox.contains(new MailTypeSelector(MessageStub.class)));
		assertFalse(this.mailbox.contains(new MailTypeSelector(MessageStub2.class)));
	}

	/**
	 */
	public void testGetInt() {
		initMailbox();
		assertNull(this.mailbox.get(-1));
		assertSame(this.m2, this.mailbox.get(0));
		assertSame(this.m1, this.mailbox.get(1));
		assertNull(this.mailbox.get(2));
	}

	/**
	 */
	public void testGetFirst() {
		initMailbox();
		assertSame(this.m2, this.mailbox.getFirst());
	}

	/**
	 */
	public void testGetFirstSelector() {
		initMailbox();
		assertSame(this.m2, this.mailbox.getFirst(new MailTypeSelector(MessageStub.class)));
		assertNull(this.mailbox.getFirst(new MailTypeSelector(MessageStub2.class)));
	}

	/**
	 */
	public void testIsEmpty() {
		initMailbox();
		assertFalse(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testSize() {
		initMailbox();
		assertEquals(2, this.mailbox.size());
	}

	/**
	 */
	public void testIterator_withoutParallelIterator() {
		initMailbox();
		Iterator<Message> iterator = this.mailbox.iterator();
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertTrue(this.mailbox.isEmpty()); // true because auto apply is enable on internal mailbox list
	}

	/**
	 */
	public void testIterator_withParallelIterator() {
		initMailbox();
		Iterator<Message> iterator = this.mailbox.iterator();
		Iterator<Message> iterator2 = this.mailbox.iterator();
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertTrue(iterator2.hasNext());
		
		assertFalse(this.mailbox.isEmpty());
		this.mailbox.synchronizeMessages();
		assertTrue(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testIteratorBoolean_true_withoutParallelIterator() {
		initMailbox();
		Iterator<Message> iterator = this.mailbox.iterator(true);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertTrue(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testIteratorBoolean_true_withParallelIterator() {
		initMailbox();
		Iterator<Message> iterator = this.mailbox.iterator(true);
		this.mailbox.iterator();
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertFalse(this.mailbox.isEmpty());
		this.mailbox.synchronizeMessages();
		assertTrue(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testIteratorBoolean_false() {
		initMailbox();
		Iterator<Message> iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());
		this.mailbox.synchronizeMessages();
		assertEquals(2, this.mailbox.size());
	}

	/**
	 */
	public void testIteratorSelector() {
		initMailbox();
		Iterator<Message> iterator;
		
		iterator = this.mailbox.iterator(new MailTypeSelector(MessageStub2.class));
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());

		iterator = this.mailbox.iterator(new OddDateSelector());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(1, this.mailbox.size());
		assertFalse(this.mailbox.contains(this.m1));
		assertTrue(this.mailbox.contains(this.m2));
	}

	/**
	 */
	public void testIteratorSelectorBoolean_true() {
		initMailbox();
		Iterator<Message> iterator;
		
		iterator = this.mailbox.iterator(new MailTypeSelector(MessageStub2.class), true);
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());

		iterator = this.mailbox.iterator(new OddDateSelector(), true);
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(1, this.mailbox.size());
		assertTrue(this.mailbox.contains(this.m2));
		assertFalse(this.mailbox.contains(this.m1));
	}

	/**
	 */
	public void testIteratorSelectorBoolean_false() {
		initMailbox();
		Iterator<Message> iterator;
		
		iterator = this.mailbox.iterator(new MailTypeSelector(MessageStub2.class), false);
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());

		iterator = this.mailbox.iterator(new OddDateSelector(), false);
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());
		assertTrue(this.mailbox.contains(this.m1));
		assertTrue(this.mailbox.contains(this.m2));
		
		this.mailbox.synchronizeMessages();
		
		assertEquals(2, this.mailbox.size());
		assertTrue(this.mailbox.contains(this.m1));
		assertTrue(this.mailbox.contains(this.m2));
	}

	/**
	 */
	public void testAddMail() {
		initMailbox();
		Iterator<Message> iterator;
		
		MessageStub m5 = new MessageStub(1f, "m5"); //$NON-NLS-1$
		assertTrue(this.mailbox.add(m5));
		this.mailbox.synchronizeMessages();
		
		iterator = this.mailbox.iterator(false);

		assertTrue(iterator.hasNext());
		assertSame(m5, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());

		MessageStub m6 = new MessageStub(2000f, "m6"); //$NON-NLS-1$
		assertTrue(this.mailbox.add(m6));
		MessageStub m7 = new MessageStub(2000f, "m7"); //$NON-NLS-1$
		assertTrue(this.mailbox.add(m7));
		this.mailbox.synchronizeMessages();
		
		iterator = this.mailbox.iterator(false);
		
		Message m67_a, m67_b;
		if (CreationDateMessageComparator.SINGLETON.compare(m6, m7)<=0) {
			m67_a = m6;
			m67_b = m7;
		}
		else {
			m67_a = m7;
			m67_b = m6;
		}
		
		assertTrue(iterator.hasNext());
		assertSame(m5, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m67_a, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(m67_b, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	/**
	 */
	public void testRemoveMail() {
		initMailbox();
		Iterator<Message> iterator;
		
		assertTrue(this.mailbox.remove(this.m1));

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertFalse(this.mailbox.remove(this.m3));

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());

		assertFalse(this.mailbox.remove(this.m4));

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());

		assertTrue(this.mailbox.remove(this.m2));

		iterator = this.mailbox.iterator(false);
		
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testRemoveInt() {
		initMailbox();
		Iterator<Message> iterator;
		
		assertSame(this.m1, this.mailbox.remove(1));
		this.mailbox.synchronizeMessages();

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertSame(this.m2, this.mailbox.remove(0));
		this.mailbox.synchronizeMessages();

		iterator = this.mailbox.iterator(false);
		
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testRemoveAllSelector() {
		initMailbox();
		Iterator<Message> iterator;

		assertFalse(this.mailbox.removeAll(new MailTypeSelector(MessageStub2.class)));
		this.mailbox.synchronizeMessages();

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());

		assertTrue(this.mailbox.removeAll(new MailTypeSelector(MessageStub.class)));
		this.mailbox.synchronizeMessages();

		iterator = this.mailbox.iterator(false);
		
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testRemoveFirst() {
		initMailbox();
		Iterator<Message> iterator;

		assertSame(this.m2, this.mailbox.removeFirst());
		this.mailbox.synchronizeMessages();

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());

		assertSame(this.m1, this.mailbox.removeFirst());
		this.mailbox.synchronizeMessages();

		iterator = this.mailbox.iterator(false);
		
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testRemoveFirstSelector() {
		initMailbox();
		Iterator<Message> iterator;

		assertNull(this.mailbox.removeFirst(new MailTypeSelector(MessageStub2.class)));
		this.mailbox.synchronizeMessages();

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());

		assertSame(this.m1, this.mailbox.removeFirst(new OddDateSelector()));
		this.mailbox.synchronizeMessages();

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testGetBufferSize() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		
		this.mailbox.add(this.m1);
		assertEquals(1, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.add(this.m2);
		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		
		this.mailbox.synchronizeMessages();
		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
	}

	/**
	 */
	public void testIsBufferEmpty_withoutParallelIterator() {
		assertTrue(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());
		
		this.mailbox.add(this.m1);
		assertFalse(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());

		this.mailbox.add(this.m2);
		assertFalse(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());
		
		this.mailbox.synchronizeMessages();
		assertFalse(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());
	}

	/**
	 */
	public void testIsBufferEmpty_withParallelIterator() {
		Iterator<Message> iterator = this.mailbox.iterator();
		
		assertTrue(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());
		
		this.mailbox.add(this.m1);
		assertFalse(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());

		this.mailbox.add(this.m2);
		assertFalse(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());
		
		while (iterator.hasNext()) {
			iterator.next();
		}
		
		this.mailbox.synchronizeMessages();
		assertFalse(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());
	}

	/**
	 */
	public void testClearBuffer_withoutParallelIterator() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.add(this.m1);
		this.mailbox.add(this.m2);
		this.mailbox.add(this.m3);
		
		assertEquals(3, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.clearBuffer();

		assertEquals(3, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());		

		this.mailbox.synchronizeMessages();

		assertEquals(3, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());		
	}

	/**
	 */
	public void testClearBuffer_withParallelIterator() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.add(this.m1);
		this.mailbox.add(this.m2);

		Iterator<Message> iterator = this.mailbox.iterator();
		iterator.hasNext();
		
		this.mailbox.add(this.m3);
		this.mailbox.add(this.m4);
		
		assertEquals(2, this.mailbox.size());
		assertEquals(2, this.mailbox.getBufferSize());

		this.mailbox.clearBuffer();

		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		
		this.mailbox.synchronizeMessages();

		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());		
	}

	/**
	 */
	public void testAdd_withoutParallelIterator() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.add(this.m1);
		assertEquals(1, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		
		this.mailbox.add(this.m2);
		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.add(this.m3);
		assertEquals(3, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
	}

	/**
	 */
	public void testAdd_withParallelIterator() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.add(this.m3);
		this.mailbox.add(this.m3);

		Iterator<Message> iterator = this.mailbox.iterator();
		iterator.hasNext();
		
		this.mailbox.add(this.m1);
		assertEquals(2, this.mailbox.size());
		assertEquals(1, this.mailbox.getBufferSize());
		
		this.mailbox.add(this.m2);
		assertEquals(2, this.mailbox.size());
		assertEquals(2, this.mailbox.getBufferSize());
		
		while (iterator.hasNext()) {
			iterator.next();
		}
		
		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());		
	}

	/**
	 */
	public void testSynchronizedMessages() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		this.mailbox.add(this.m1);
		this.mailbox.add(this.m2);
		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		
		assertTrue(this.mailbox.contains(this.m1));
		assertTrue(this.mailbox.contains(this.m2));
		assertFalse(this.mailbox.contains(this.m3));
		assertFalse(this.mailbox.contains(this.m4));

		this.mailbox.synchronizeMessages();

		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		
		assertTrue(this.mailbox.contains(this.m1));
		assertTrue(this.mailbox.contains(this.m2));
		assertFalse(this.mailbox.contains(this.m3));
		assertFalse(this.mailbox.contains(this.m4));
	}

}
