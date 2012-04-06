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
public class BufferedSignalManagerParentChildTest extends TestCase {

	private InstantSignalManager child;
	private BufferedSignalManager adapter;
	private InstantSignalManager parent;
	private SignalListenerStub listener2;
	private SignalListenerStub listener1;
	private SignalListenerStub listener0;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.listener2 = new SignalListenerStub();
		this.listener1 = new SignalListenerStub();
		this.listener0 = new SignalListenerStub();

		this.parent = new InstantSignalManager(null, this.adapter);
		this.parent.setPolicy(SignalPolicy.FIRE_SIGNAL);
		this.parent.addSignalListener(this.listener0);

		this.adapter = new BufferedSignalManager(null, this.parent);
		this.adapter.addSignalListener(this.listener1);

		this.child = new InstantSignalManager(null, this.adapter);
		this.child.addSignalListener(this.listener2);
		this.child.setPolicy(SignalPolicy.FIRE_SIGNAL);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.child.removeSignalListener(this.listener2);
		this.adapter.removeSignalListener(this.listener1);
		this.parent.removeSignalListener(this.listener0);
		this.listener2 = null;
		this.listener1 = null;
		this.listener0 = null;
		this.parent = null;
		this.adapter = null;
		this.child = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetParent() {
		assertNull(this.parent.getParent());
		assertSame(this.parent, this.adapter.getParent());
		assertSame(this.adapter, this.child.getParent());
	}
	
	/**
	 */
	public void testGetSignal() {
		Signal s;
		
		this.adapter.setPolicy(SignalPolicy.IGNORE_ALL);
		this.adapter.onSignal(s = new Signal(this));
		assertNull(this.adapter.getSignal());
		this.adapter.sync();
		assertNull(this.adapter.getSignal());

		this.adapter.setPolicy(SignalPolicy.FIRE_SIGNAL);
		this.adapter.onSignal(s = new Signal(this));
		assertNull(this.adapter.getSignal());
		this.adapter.sync();
		assertNull(this.adapter.getSignal());

		this.adapter.setPolicy(SignalPolicy.STORE_IN_QUEUE);
		this.adapter.onSignal(s = new Signal(this));
		assertSame(s, this.adapter.getSignal());
		assertNull(this.adapter.getSignal());
		this.adapter.sync();
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
		this.adapter.sync();
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
		this.listener2.assertNull();
		this.listener1.assertNull();
		this.listener0.assertSignal(s);
		
		this.adapter.setPolicy(SignalPolicy.FIRE_SIGNAL);
		this.adapter.fireSignal(s);
		assertFalse(this.adapter.hasSignal());
		this.listener2.assertNull();
		this.listener1.assertNull();
		this.listener0.assertSignal(s);
		this.adapter.sync();
		this.listener2.assertSignal(s);
		this.listener1.assertSignal(s);
		this.listener0.assertNull();

		this.adapter.setPolicy(SignalPolicy.STORE_IN_QUEUE);
		this.adapter.fireSignal(s);
		assertTrue(this.adapter.hasSignal());
		this.listener2.assertSignal(s);
		this.listener1.assertNull();
		this.listener0.assertSignal(s);
	}

	/**
	 */
	public void testOnSignal() {
		Signal s = new Signal(this);
		
		this.adapter.setPolicy(SignalPolicy.IGNORE_ALL);
		this.adapter.onSignal(s);
		assertFalse(this.adapter.hasSignal());
		this.listener2.assertNull();
		this.listener1.assertNull();
		this.listener0.assertNull();
		
		this.adapter.setPolicy(SignalPolicy.FIRE_SIGNAL);
		this.adapter.onSignal(s);
		assertFalse(this.adapter.hasSignal());
		this.listener2.assertNull();
		this.listener1.assertNull();
		this.listener0.assertNull();
		this.adapter.sync();
		this.listener2.assertSignal(s);
		this.listener1.assertSignal(s);
		this.listener0.assertNull();

		this.adapter.setPolicy(SignalPolicy.STORE_IN_QUEUE);
		this.adapter.onSignal(s);
		assertTrue(this.adapter.hasSignal());
		this.listener2.assertSignal(s);
		this.listener1.assertNull();
		this.listener0.assertNull();
	}
	
}