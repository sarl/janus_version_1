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

import java.util.Iterator;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.MessageStub;
import org.janusproject.kernel.message.MessageStub2;
import org.janusproject.kernel.util.selector.TypeSelector;


/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see TreeSetMailbox
 */
public class LinkedListMailboxTest extends TestCase {

	private LinkedListMailbox mailbox;
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
		this.mailbox = new LinkedListMailbox();
		this.m1 = new MessageStub(1024f, "m1"); //$NON-NLS-1$
		this.m2 = new MessageStub(2047f, "m2"); //$NON-NLS-1$
		this.m3 = new MessageStub(4096f, "m3"); //$NON-NLS-1$
		this.m4 = new MessageStub2(4096f, "m4"); //$NON-NLS-1$
		this.mailbox.add(this.m1);
		this.mailbox.add(this.m2);
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

	/**
	 */
	public void testClear() {
		assertFalse(this.mailbox.inbox.isEmpty());
		this.mailbox.clear();
		assertTrue(this.mailbox.inbox.isEmpty());
	}

	/**
	 */
	public void testContainsMail() {
		assertTrue(this.mailbox.contains(this.m1));
		assertTrue(this.mailbox.contains(this.m2));
		assertFalse(this.mailbox.contains(this.m3));
		assertFalse(this.mailbox.contains(this.m4));
	}

	/**
	 */
	public void testContainsSelector() {
		assertTrue(this.mailbox.contains(new TypeSelector<MessageStub>(MessageStub.class)));
		assertFalse(this.mailbox.contains(new TypeSelector<MessageStub2>(MessageStub2.class)));
	}

	/**
	 */
	public void testGetInt() {
		assertNull(this.mailbox.get(-1));
		assertSame(this.m1, this.mailbox.get(0));
		assertSame(this.m2, this.mailbox.get(1));
		assertNull(this.mailbox.get(2));
	}

	/**
	 */
	public void testGetFirst() {
		assertSame(this.m1, this.mailbox.getFirst());
	}

	/**
	 */
	public void testGetFirstSelector() {
		assertSame(this.m1, this.mailbox.getFirst(new TypeSelector<MessageStub>(MessageStub.class)));
		assertNull(this.mailbox.getFirst(new TypeSelector<MessageStub2>(MessageStub2.class)));
	}

	/**
	 */
	public void testIsEmpty() {
		assertFalse(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testSize() {
		assertEquals(2, this.mailbox.size());
	}

	/**
	 */
	public void testIterator() {
		Iterator<Message> iterator = this.mailbox.iterator();
		
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertTrue(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testIteratorBoolean_true() {
		Iterator<Message> iterator = this.mailbox.iterator(true);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertTrue(this.mailbox.isEmpty());
	}

	/**
	 */
	public void testIteratorBoolean_false() {
		Iterator<Message> iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());
	}

	/**
	 */
	public void testIteratorSelector() {
		Iterator<? extends Message> iterator;
		
		iterator = this.mailbox.iterator(new TypeSelector<MessageStub2>(MessageStub2.class));
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());

		iterator = this.mailbox.iterator(new OddDateSelector());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(1, this.mailbox.size());
		assertTrue(this.mailbox.contains(this.m1));
	}

	/**
	 */
	public void testIteratorSelectorBoolean_true() {
		Iterator<? extends Message> iterator;
		
		iterator = this.mailbox.iterator(new TypeSelector<MessageStub2>(MessageStub2.class), true);
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());

		iterator = this.mailbox.iterator(new OddDateSelector(), true);
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(1, this.mailbox.size());
		assertTrue(this.mailbox.contains(this.m1));
	}

	/**
	 */
	public void testIteratorSelectorBoolean_false() {
		Iterator<? extends Message> iterator;
		
		iterator = this.mailbox.iterator(new TypeSelector<MessageStub2>(MessageStub2.class), false);
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());

		iterator = this.mailbox.iterator(new OddDateSelector(), false);
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(2, this.mailbox.size());
	}

	/**
	 */
	public void testAddMail() {
		Iterator<Message> iterator;
		
		MessageStub m5 = new MessageStub(1f, "m5"); //$NON-NLS-1$
		assertTrue(this.mailbox.add(m5));
		
		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(m5, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());

		MessageStub m6 = new MessageStub(2000f, "m6"); //$NON-NLS-1$
		assertTrue(this.mailbox.add(m6));
		MessageStub m7 = new MessageStub(2000f, "m7"); //$NON-NLS-1$
		assertTrue(this.mailbox.add(m7));
		
		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(m5, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		if (m6.getIdentifier().compareTo(m7.getIdentifier())<0) {
			assertSame(m6, iterator.next());
			assertTrue(iterator.hasNext());
			assertSame(m7, iterator.next());
			assertTrue(iterator.hasNext());
		}
		else {
			assertSame(m7, iterator.next());
			assertTrue(iterator.hasNext());
			assertSame(m6, iterator.next());
			assertTrue(iterator.hasNext());
		}
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	/**
	 */
	public void testRemoveMail() {
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
		Iterator<Message> iterator;
		
		assertSame(this.m2, this.mailbox.remove(1));

		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
		
		assertSame(this.m1, this.mailbox.remove(0));

		iterator = this.mailbox.iterator(false);
		
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testRemoveAllSelector() {
		Iterator<Message> iterator;

		assertFalse(this.mailbox.removeAll(new TypeSelector<MessageStub2>(MessageStub2.class)));
		
		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());

		assertTrue(this.mailbox.removeAll(new TypeSelector<MessageStub>(MessageStub.class)));
		
		iterator = this.mailbox.iterator(false);
		
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testRemoveFirst() {
		Iterator<Message> iterator;

		assertSame(this.m1, this.mailbox.removeFirst());
		
		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());

		assertSame(this.m2, this.mailbox.removeFirst());
		
		iterator = this.mailbox.iterator(false);
		
		assertFalse(iterator.hasNext());
	}

	/**
	 */
	public void testRemoveFirstSelector() {
		Iterator<Message> iterator;

		assertNull(this.mailbox.removeFirst(new TypeSelector<MessageStub2>(MessageStub2.class)));
		
		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertTrue(iterator.hasNext());
		assertSame(this.m2, iterator.next());
		assertFalse(iterator.hasNext());

		assertSame(this.m2, this.mailbox.removeFirst(new OddDateSelector()));
		
		iterator = this.mailbox.iterator(false);
		
		assertTrue(iterator.hasNext());
		assertSame(this.m1, iterator.next());
		assertFalse(iterator.hasNext());
	}

}
