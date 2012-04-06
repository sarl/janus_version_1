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

import java.util.Random;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EnumMessageTest extends TestCase {

	private EnumStub expected;
	private EnumMessage message;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		Random rnd = new Random();
		this.expected = EnumStub.values()[rnd.nextInt(EnumStub.values().length)];	
		this.message = new EnumMessage(this.expected);
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
		assertEquals(this.expected, this.message.getContent());
	}
	
	/**
	 */
	public void testEnumValueClass() {
		assertEquals(this.expected, this.message.enumValue(EnumStub.class));
		assertNull(this.message.enumValue(EnumStub2.class));
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum EnumStub {
		A,B,C;
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private enum EnumStub2 {
		A,B,C;
	}

}
