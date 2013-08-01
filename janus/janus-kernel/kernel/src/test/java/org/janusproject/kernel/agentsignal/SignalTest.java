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
package org.janusproject.kernel.agentsignal;

import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SignalTest extends TestCase {

	private Object source;
	private String name;
	private Signal signal;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.source = new Object();
		this.name = UUID.randomUUID().toString();
		this.signal = new Signal(this.source, this.name, 1, 2, 3, 4);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.signal = null;
		this.source = null;
		this.name = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetSource() {
		assertSame(this.source, this.signal.getSource());
	}

	/**
	 */
	public void testGetName() {
		assertEquals(this.name, this.signal.getName());
	}
	
	/**
	 */
	public void testGetValues() {
		Object[] values = this.signal.getValues();
		assertNotNull(values);
		assertEquals(4, values.length);
		assertEquals(1, values[0]);
		assertEquals(2, values[1]);
		assertEquals(3, values[2]);
		assertEquals(4, values[3]);
	}

}
