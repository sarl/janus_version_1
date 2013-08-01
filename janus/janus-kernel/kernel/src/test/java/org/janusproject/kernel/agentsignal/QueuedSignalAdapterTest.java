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
package org.janusproject.kernel.agentsignal;

import java.util.Iterator;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class QueuedSignalAdapterTest extends TestCase {

	private QueuedSignalAdapter<Signal> adapter;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.adapter = new QueuedSignalAdapter<Signal>(Signal.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.adapter = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetFirstAvailableSignal() {
		assertNull(this.adapter.getFirstAvailableSignal());
		Signal s = new Signal(this);
		this.adapter.onSignal(s);
		assertSame(s, this.adapter.getFirstAvailableSignal());
	}
	
	/**
	 */
	public void testGetQueueSize() {
		assertEquals(0, this.adapter.getQueueSize());
		Signal s = new Signal(this);
		this.adapter.onSignal(s);
		assertEquals(1, this.adapter.getQueueSize());
		s = new Signal(this);
		this.adapter.onSignal(s);
		assertEquals(2, this.adapter.getQueueSize());
	}

	/**
	 */
	public void testClear() {
		Signal s = new Signal(this);
		this.adapter.onSignal(s);

		this.adapter.clear();
		
		assertEquals(0, this.adapter.getQueueSize());
	}
	
	/**
	 */
	public void testIterator() {
		Signal s0 = new Signal(this);
		Signal s1 = new Signal(this);
		Signal s2 = new Signal(this);
		Signal s3 = new Signal(this);
		Signal s4 = new Signal(this);
		Signal s5 = new Signal(this);
		this.adapter.onSignal(s0);
		this.adapter.onSignal(s1);
		this.adapter.onSignal(s2);
		this.adapter.onSignal(s3);
		this.adapter.onSignal(s4);
		this.adapter.onSignal(s5);
		
		assertEquals(6, this.adapter.getQueueSize());

		Iterator<Signal> iterator = this.adapter.iterator();
		
		assertTrue(iterator.hasNext());
		assertSame(s0, iterator.next());
		assertEquals(5, this.adapter.getQueueSize());

		assertTrue(iterator.hasNext());
		assertSame(s1, iterator.next());
		assertEquals(4, this.adapter.getQueueSize());

		assertTrue(iterator.hasNext());
		assertSame(s2, iterator.next());
		assertEquals(3, this.adapter.getQueueSize());

		assertTrue(iterator.hasNext());
		assertSame(s3, iterator.next());
		assertEquals(2, this.adapter.getQueueSize());

		assertTrue(iterator.hasNext());
		assertSame(s4, iterator.next());
		assertEquals(1, this.adapter.getQueueSize());

		assertTrue(iterator.hasNext());
		assertSame(s5, iterator.next());
		assertEquals(0, this.adapter.getQueueSize());

		assertFalse(iterator.hasNext());
	}

}
