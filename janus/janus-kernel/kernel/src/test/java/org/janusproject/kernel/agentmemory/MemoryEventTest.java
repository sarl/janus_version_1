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
package org.janusproject.kernel.agentmemory;

import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.agentmemory.MemoryEvent;
import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MemoryEventTest extends TestCase {

	private Memory memory;
	private MemoryEvent event;
	private String id;
	private Object oldValue;
	private Object newValue;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.memory = new MemoryStub();
		this.id = UUID.randomUUID().toString();
		this.oldValue = new Object();
		this.newValue = new Object();
		this.event = new MemoryEvent(this.memory, this.id, this.oldValue, this.newValue);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.memory = null;
		this.event = null;
		this.id = null;
		this.oldValue = null;
		this.newValue = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetMemory() {
		assertSame(this.memory, this.event.getMemory());
	}
	
	/**
	 */
	public void testGetKnowledgeIdentifier() {
		assertEquals(this.id, this.event.getKnowledgeIdentifier());
	}
	
	/**
	 */
	public void testGetOldValue() {
		assertSame(this.oldValue, this.event.getOldValue());
	}

	/**
	 */
	public void testGetOldValueClass() {
		assertSame(this.oldValue, this.event.getOldValue(Object.class));
	}

	/**
	 */
	public void testGetNewValue() {
		assertSame(this.newValue, this.event.getNewValue());
	}

	/**
	 */
	public void testGetNewValueClass() {
		assertSame(this.newValue, this.event.getNewValue(Object.class));
	}
	
}
