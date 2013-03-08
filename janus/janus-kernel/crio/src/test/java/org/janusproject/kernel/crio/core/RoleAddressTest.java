/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.kernel.crio.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.logger.LoggerUtil;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoleAddressTest extends TestCase {

	private CRIOContext context;
	private Organization organization;
	private GroupAddress groupAddress;
	private AgentAddress agentAddress;
	private RoleAddress address;
	private String description;
	private String name;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.context = new CRIOContext(null);
		this.organization = new Organization1Stub(this.context);
		this.description = UUID.randomUUID().toString();
		this.name = UUID.randomUUID().toString();
		this.agentAddress = AddressUtil.createAgentAddress(
				UUID.randomUUID(),
				this.name);
		this.groupAddress = new GroupAddress(
				UUID.randomUUID(),
				this.organization.getClass(),
				null,
				this.name,
				this.description);
		this.address = new RoleAddress(
				this.groupAddress,
				Role3Stub.class,
				this.agentAddress,
				this.name);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.address = null;
		this.groupAddress = null;
		this.agentAddress = null;
		this.organization = null;
		this.name = null;
		this.description = null;
		this.context = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetDescription() {
		assertNull(this.address.getDescription());
	}

	/**
	 */
	public void testGetName() {
		assertEquals(this.name, this.address.getName());
	}

	/**
	 */
	public void testGetGroup() {
		assertEquals(this.groupAddress, this.address.getGroup());
	}

	/**
	 */
	public void testGetGroupObject() {
		assertNull(this.address.getGroupObject());
	}

	/**
	 */
	public void testGetPlayer() {
		assertEquals(this.agentAddress, this.address.getPlayer());
	}

	/**
	 */
	public void testGetRole() {
		assertEquals(Role3Stub.class, this.address.getRole());
	}

	/**
	 */
	public void testGetRoleObject() {
		assertNull(this.address.getRoleObject());
	}

	/**
	 */
	public void testGetUUID() {
		assertNotNull(this.address.getUUID());
	}

	/**
	 * @throws Exception
	 */
	public void testSerialization() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this.address);
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
		
		assertTrue(unserializedObject instanceof RoleAddress);
		assertNotSame(this.address, unserializedObject);
		
		RoleAddress rAdr = (RoleAddress)unserializedObject;
		assertEquals(this.address.getUUID(), rAdr.getUUID());
		assertEquals(this.address.getName(), rAdr.getName());
		assertEquals(this.address.getDescription(), rAdr.getDescription());
		assertEquals(this.address.getGroup(), rAdr.getGroup());
		assertNull(rAdr.getGroupObject());
		assertEquals(this.address.getPlayer(), rAdr.getPlayer());
		assertEquals(this.address.getRole(), rAdr.getRole());
		assertNull(rAdr.getRoleObject());
		assertEquals(this.address, rAdr);
	}

}