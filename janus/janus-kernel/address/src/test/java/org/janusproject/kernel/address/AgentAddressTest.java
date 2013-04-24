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
package org.janusproject.kernel.address;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AgentAddressTest extends TestCase {

	private UUID uid;
	private String name;

	/**
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.uid = UUID.randomUUID();
		this.name = UUID.randomUUID().toString();
	}

	/**
	 */
	@Override
	protected void tearDown() throws Exception {
		this.name = null;
		this.uid = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetName() {
		AgentAddress hAdr1 = new AgentAddressStub(this.uid, null);
		assertEquals(AgentAddress.NO_NAME, hAdr1.getName());

		AgentAddress hAdr2 = new AgentAddressStub(this.uid, AgentAddress.NO_NAME);
		assertEquals(AgentAddress.NO_NAME, hAdr2.getName());

		AgentAddress hAdr3 = new AgentAddressStub(this.uid, this.name);
		assertEquals(this.name, hAdr3.getName());
	}


	/**
	 */
	public void testSetNameString() {
		AgentAddress hAdr1 = new AgentAddressStub(this.uid, null);
		hAdr1.setName(this.name);
		assertEquals(this.name, hAdr1.getName());

		AgentAddress hAdr2 = new AgentAddressStub(this.uid, AgentAddress.NO_NAME);
		hAdr2.setName(this.name);
		assertEquals(this.name, hAdr2.getName());

		AgentAddress hAdr3 = new AgentAddressStub(this.uid, this.name);
		hAdr3.setName(this.name);
		assertEquals(this.name, hAdr3.getName());
	}

	/**
	 */
	public void testToString() {
		AgentAddress hAdr1 = new AgentAddressStub(this.uid, null);
		assertEquals("::"+this.uid.toString(), //$NON-NLS-1$
				hAdr1.toString());

		AgentAddress hAdr2 = new AgentAddressStub(this.uid, AgentAddress.NO_NAME);
		assertEquals("::"+this.uid.toString(), //$NON-NLS-1$
				hAdr2.toString());

		AgentAddress hAdr3 = new AgentAddressStub(this.uid, this.name);
		assertEquals(this.name+"::"+this.uid.toString(), //$NON-NLS-1$
				hAdr3.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testSerialization() throws Exception {
		AgentAddress address = new AgentAddressStub(this.uid, this.name);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(address);
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
		
		assertTrue(unserializedObject instanceof AgentAddress);
		assertNotSame(address, unserializedObject);
		
		AgentAddress uAdr = (AgentAddress)unserializedObject;
		assertEquals(this.uid, uAdr.getUUID());
		assertEquals(this.name, uAdr.getName());
		assertEquals(address, uAdr);
	}

}
