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
package org.janusproject.kernel.message;

import java.util.logging.Level;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MessageContextTest extends TestCase {

	private float initial;
	private AgentAddress emitter, receiver;
	private MessageContext context;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.initial = 1024f;
		this.emitter = new AgentAddressStub();
		this.receiver = new AgentAddressStub();
		this.context = new MessageContext(this.emitter, this.receiver, this.initial);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.context = null;
		this.emitter = this.receiver = null;
		super.tearDown();
	}
	
	/**
	 */
	public void testGetReceiver() {
		assertEquals(this.receiver, this.context.getReceiver());
	}

	/**
	 */
	public void testGetSender() {
		assertEquals(this.emitter, this.context.getSender());
	}


	/**
	 */
	public void testGetCreationDate() {
		assertEquals(this.initial, this.context.getCreationDate());
	}

	/**
	 */
	public void testHashCode() {
		assertTrue(new MessageContext(this.emitter, this.receiver, this.initial).hashCode()==this.context.hashCode());
		assertFalse(new MessageContext(new AgentAddressStub(), this.receiver, this.initial).hashCode()==this.context.hashCode());
		assertFalse(new MessageContext(this.emitter, new AgentAddressStub(), this.initial).hashCode()==this.context.hashCode());
		assertFalse(new MessageContext(new AgentAddressStub(), new AgentAddressStub(), this.initial).hashCode()==this.context.hashCode());
	}

	/**
	 */
	public void testEquals() {
		assertTrue(new MessageContext(this.emitter, this.receiver, this.initial).equals(this.context));
		assertTrue(this.context.equals(new MessageContext(this.emitter, this.receiver, this.initial)));

		assertFalse(new MessageContext(new AgentAddressStub(), this.receiver, this.initial).equals(this.context));
		assertFalse(this.context.equals(new MessageContext(new AgentAddressStub(), this.receiver, this.initial)));
		
		assertFalse(new MessageContext(this.emitter, new AgentAddressStub(), this.initial).equals(this.context));
		assertFalse(this.context.equals(new MessageContext(this.emitter, new AgentAddressStub(), this.initial)));
		
		assertFalse(new MessageContext(new AgentAddressStub(), new AgentAddressStub(), this.initial).equals(this.context));
		assertFalse(this.context.equals(new MessageContext(new AgentAddressStub(), new AgentAddressStub(), this.initial)));
	}

}
