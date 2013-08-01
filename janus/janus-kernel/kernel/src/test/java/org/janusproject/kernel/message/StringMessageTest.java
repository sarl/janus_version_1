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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class StringMessageTest extends TestCase {

	private String expected;
	private StringMessage message;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.expected = "123"; //$NON-NLS-1$
		this.message = new StringMessage(this.expected);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.message = null;
		this.expected = null;
		super.tearDown();
	}
	
	/**
	 */
	public void testGetContent() {
		assertEquals(this.expected, this.message.getContent());
	}
	
	/**
	 * @throws Exception
	 */
	public void testSerialization() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this.message);
		}
		finally {
			baos.close();
		}
		
		byte[] data = baos.toByteArray();
		
		Object unserializedObject = null;
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			unserializedObject = ois.readObject();
		}
		finally {
			bais.close();
		}
		
		assertTrue(unserializedObject instanceof StringMessage);
		assertNotSame(this.message, unserializedObject);
		
		StringMessage msg = (StringMessage)unserializedObject;
		assertEquals(this.message.getCreationDate(), msg.getCreationDate());
		assertEquals(this.message.getIdentifier(), msg.getIdentifier());
		assertEquals(this.message.getReceiver(), msg.getReceiver());
		assertEquals(this.message.getSender(), msg.getSender());
		assertEquals(this.expected, msg.getContent());
	}
	
}
