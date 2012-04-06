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
package org.janusproject.kernel.message;

import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class IntMessageTest extends TestCase {

	private int expected;
	private IntMessage message;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.expected = 123;
		this.message = new IntMessage(this.expected);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.message = null;
		super.tearDown();
	}
	
	/**
	 */
	public void testGetContent() {
		assertEquals(Integer.valueOf(this.expected), this.message.getContent());
	}
	
	/**
	 */
	public void testShortValue() {
		assertEquals((short)123, this.message.shortValue());
	}
	
	/**
	 */
	public void testIntValue() {
		assertEquals(123, this.message.intValue());
	}

	/**
	 */
	public void testByteValue() {
		assertEquals((byte)123, this.message.byteValue());
	}

	/**
	 */
	public void testLongValue() {
		assertEquals(123l, this.message.longValue());
	}
	
	/**
	 */
	public void testFloatValue() {
		assertEquals(123f, this.message.floatValue());
	}

	/**
	 */
	public void testDoubleValue() {
		assertEquals(123., this.message.doubleValue());
	}

}
