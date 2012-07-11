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

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AbstractContentMessageTest extends TestCase {

	private AgentAddress emitter, receiver;
	private AbstractContentMessage<Object> message;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.emitter = new AgentAddressStub();
		this.receiver = new AgentAddressStub();
		this.message = new MessageStub(1);
		this.message.sender = this.emitter;
		this.message.receiver = this.receiver;
		this.message.creationDate = 1024f;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.message = null;
		this.emitter = this.receiver = null;
		super.tearDown();
	}

	/**
	 */
	public void testContainsTypeClass() {
		assertTrue(this.message.containsType(Object.class));
		assertTrue(this.message.containsType(Number.class));
		assertTrue(this.message.containsType(Long.class));
		assertFalse(this.message.containsType(Integer.class));
		assertFalse(this.message.containsType(String.class));
	}
	
}
