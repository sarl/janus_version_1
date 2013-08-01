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

import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.message.MessageStub;
import org.janusproject.kernel.message.MessageStub2;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see BufferedTreeSetMailbox
 */
public class BufferedPerSenderMailboxTest extends TestCase {

	private BufferedPerSenderMailbox mailbox;
	private MessageStub m1;
	private MessageStub m2;
	private MessageStub m3;
	private MessageStub2 m4;
	private AgentAddress emitter1;
	private AgentAddress emitter2;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.emitter1 = new AgentAddressStub(UUID.randomUUID(), "emitter1"); //$NON-NLS-1$
		this.emitter2 = new AgentAddressStub(UUID.randomUUID(), "emitter2"); //$NON-NLS-1$
		this.mailbox = new BufferedPerSenderMailbox();
		this.m1 = new MessageStub(1024f, "m1", this.emitter1); //$NON-NLS-1$
		this.m2 = new MessageStub(2047f, "m2", this.emitter2); //$NON-NLS-1$
		this.m3 = new MessageStub(4096f, "m3", this.emitter1); //$NON-NLS-1$
		this.m4 = new MessageStub2(4096f, "m4", this.emitter2); //$NON-NLS-1$
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.mailbox = null;
		this.m1 = this.m2 = this.m3 = null;
		this.m4 = null;
		this.emitter1 = this.emitter2 = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetBufferSize() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		
		this.mailbox.add(this.m1);
		assertEquals(0, this.mailbox.size());
		assertEquals(1, this.mailbox.getBufferSize());

		this.mailbox.add(this.m2);
		assertEquals(0, this.mailbox.size());
		assertEquals(2, this.mailbox.getBufferSize());
		
		this.mailbox.synchronizeMessages();
		assertEquals(2, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
	}

	/**
	 */
	public void testIsBufferEmpty() {
		assertTrue(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());
		
		this.mailbox.add(this.m1);
		assertTrue(this.mailbox.isEmpty());
		assertFalse(this.mailbox.isBufferEmpty());

		this.mailbox.add(this.m2);
		assertTrue(this.mailbox.isEmpty());
		assertFalse(this.mailbox.isBufferEmpty());
		
		this.mailbox.synchronizeMessages();
		assertFalse(this.mailbox.isEmpty());
		assertTrue(this.mailbox.isBufferEmpty());
	}

	/**
	 */
	public void testClearBuffer() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.add(this.m1);
		this.mailbox.add(this.m2);
		this.mailbox.add(this.m3);
		
		assertEquals(0, this.mailbox.size());
		assertEquals(3, this.mailbox.getBufferSize());

		this.mailbox.clearBuffer();

		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());		
	}

	/**
	 */
	public void testAdd() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());

		this.mailbox.add(this.m1);
		assertEquals(0, this.mailbox.size());
		assertEquals(1, this.mailbox.getBufferSize());
		
		this.mailbox.add(this.m2);
		assertEquals(0, this.mailbox.size());
		assertEquals(2, this.mailbox.getBufferSize());

		this.mailbox.add(this.m3);
		assertEquals(0, this.mailbox.size());
		assertEquals(3, this.mailbox.getBufferSize());
	}

	/**
	 */
	public void testSynchronizedMessages() {
		assertEquals(0, this.mailbox.size());
		assertEquals(0, this.mailbox.getBufferSize());
		this.mailbox.add(this.m1);
		this.mailbox.add(this.m2);
		assertEquals(0, this.mailbox.size());
		assertEquals(2, this.mailbox.getBufferSize());
		
		assertFalse(this.mailbox.contains(this.m1));
		assertFalse(this.mailbox.contains(this.m2));
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
