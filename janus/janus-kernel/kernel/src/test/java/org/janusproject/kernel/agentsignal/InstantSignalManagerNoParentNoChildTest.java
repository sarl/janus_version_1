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

import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class InstantSignalManagerNoParentNoChildTest extends TestCase {

	private InstantSignalManager adapter;
	private SignalListenerStub listener;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.listener = new SignalListenerStub();
		this.adapter = new InstantSignalManager(null);
		this.adapter.addSignalListener(this.listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.adapter.removeSignalListener(this.listener);
		this.listener = null;
		this.adapter = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetParent() {
		assertNull(this.adapter.getParent());
	}
	
	/**
	 */
	public void testGetSignal() {
		Signal s;
		
		this.adapter.setPolicy(SignalPolicy.IGNORE_ALL);
		this.adapter.onSignal(s = new Signal(this));
		assertNull(this.adapter.getSignal());

		this.adapter.setPolicy(SignalPolicy.FIRE_SIGNAL);
		this.adapter.onSignal(s = new Signal(this));
		assertNull(this.adapter.getSignal());

		this.adapter.setPolicy(SignalPolicy.STORE_IN_QUEUE);
		this.adapter.onSignal(s = new Signal(this));
		assertSame(s, this.adapter.getSignal());
		assertNull(this.adapter.getSignal());
	}

	/**
	 */
	public void testHasSignal() {
		this.adapter.setPolicy(SignalPolicy.IGNORE_ALL);
		this.adapter.onSignal(new Signal(this));
		assertFalse(this.adapter.hasSignal());

		this.adapter.setPolicy(SignalPolicy.FIRE_SIGNAL);
		this.adapter.onSignal(new Signal(this));
		assertFalse(this.adapter.hasSignal());

		this.adapter.setPolicy(SignalPolicy.STORE_IN_QUEUE);
		this.adapter.onSignal(new Signal(this));
		assertTrue(this.adapter.hasSignal());
		assertTrue(this.adapter.hasSignal());
		this.adapter.getSignal();
		assertFalse(this.adapter.hasSignal());
	}

	/**
	 */
	public void testFireSignal() {
		Signal s = new Signal(this);
		
		this.adapter.setPolicy(SignalPolicy.IGNORE_ALL);
		this.adapter.fireSignal(s);
		assertFalse(this.adapter.hasSignal());
		this.listener.assertNull();
		
		this.adapter.setPolicy(SignalPolicy.FIRE_SIGNAL);
		this.adapter.fireSignal(s);
		assertFalse(this.adapter.hasSignal());
		this.listener.assertSignal(s);

		this.adapter.setPolicy(SignalPolicy.STORE_IN_QUEUE);
		this.adapter.fireSignal(s);
		assertTrue(this.adapter.hasSignal());
		this.listener.assertNull();
	}

	/**
	 */
	public void testOnSignal() {
		Signal s = new Signal(this);
		
		this.adapter.setPolicy(SignalPolicy.IGNORE_ALL);
		this.adapter.onSignal(s);
		assertFalse(this.adapter.hasSignal());
		this.listener.assertNull();
		
		this.adapter.setPolicy(SignalPolicy.FIRE_SIGNAL);
		this.adapter.onSignal(s);
		assertFalse(this.adapter.hasSignal());
		this.listener.assertSignal(s);

		this.adapter.setPolicy(SignalPolicy.STORE_IN_QUEUE);
		this.adapter.onSignal(s);
		assertTrue(this.adapter.hasSignal());
		this.listener.assertNull();
	}
	
}