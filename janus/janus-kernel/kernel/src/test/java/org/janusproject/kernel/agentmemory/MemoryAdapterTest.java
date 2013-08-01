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

import java.util.logging.Level;

import org.janusproject.kernel.agentmemory.Memory;
import org.janusproject.kernel.agentmemory.MemoryAdapter;
import org.janusproject.kernel.agentmemory.MemoryEvent;
import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MemoryAdapterTest extends TestCase {

	private static final String A1 = "A1"; //$NON-NLS-1$
	private static final String A2 = "A2"; //$NON-NLS-1$
	private static final String A3 = "A3"; //$NON-NLS-1$
	
	private MemoryAdapter adapter;
	private Memory memory;
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
		this.oldValue = new Object();
		this.newValue = new Object();
		this.adapter = new MemoryAdapter();
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.memory = null;
		this.oldValue = null;
		this.newValue = null;
		this.adapter = null;
		super.tearDown();
	}

	/**
	 */
	public void testHasKnowledgeChangedString() {
		assertFalse(this.adapter.hasKnowledgeChanged(A1));
		assertFalse(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));
		
		this.adapter.onKnownledgeChanged(new MemoryEvent(this.memory, A1, this.oldValue, this.newValue));

		assertTrue(this.adapter.hasKnowledgeChanged(A1));
		assertFalse(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));

		this.adapter.onKnownledgeChanged(new MemoryEvent(this.memory, A2, this.oldValue, this.newValue));

		assertTrue(this.adapter.hasKnowledgeChanged(A1));
		assertTrue(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));

		this.adapter.onKnownledgeChanged(new MemoryEvent(this.memory, A1, this.oldValue, this.newValue));

		assertTrue(this.adapter.hasKnowledgeChanged(A1));
		assertTrue(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));
	}
	
	/**
	 */
	public void testMarkAsNotChangedString() {
		assertFalse(this.adapter.hasKnowledgeChanged(A1));
		assertFalse(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));
		
		this.adapter.onKnownledgeChanged(new MemoryEvent(this.memory, A1, this.oldValue, this.newValue));
		this.adapter.onKnownledgeChanged(new MemoryEvent(this.memory, A2, this.oldValue, this.newValue));

		this.adapter.markAsNotChanged(A1);
		
		assertFalse(this.adapter.hasKnowledgeChanged(A1));
		assertTrue(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));

		this.adapter.markAsNotChanged(A3);
		
		assertFalse(this.adapter.hasKnowledgeChanged(A1));
		assertTrue(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));
	}

	/** Mark all the knowledge as not-changed.
	 */
	public void markAsNotChanged() {
		assertFalse(this.adapter.hasKnowledgeChanged(A1));
		assertFalse(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));
		
		this.adapter.onKnownledgeChanged(new MemoryEvent(this.memory, A1, this.oldValue, this.newValue));
		this.adapter.onKnownledgeChanged(new MemoryEvent(this.memory, A2, this.oldValue, this.newValue));

		this.adapter.markAsNotChanged();
		
		assertFalse(this.adapter.hasKnowledgeChanged(A1));
		assertFalse(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));

		this.adapter.markAsNotChanged();
		
		assertFalse(this.adapter.hasKnowledgeChanged(A1));
		assertFalse(this.adapter.hasKnowledgeChanged(A2));
		assertFalse(this.adapter.hasKnowledgeChanged(A3));
	}

}
