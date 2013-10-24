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
package org.janusproject.kernel.configuration;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import junit.framework.TestCase;

import org.arakhne.afc.vmutil.FileSystem;
import org.janusproject.kernel.configuration.JanusProperties.PrivilegedContext;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JanusPropertiesTest extends TestCase {

	private UUID id;
	private JanusProperties properties;
	private PrivilegedJanusPropertySetter jps;
	
	/**
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.id = UUID.randomUUID();
		this.jps = null;
		this.properties = new JanusProperties(this.id, new PrivilegedContext() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void setPrivilegedJanusPropertySetter(PrivilegedJanusPropertySetter jps) {
				JanusPropertiesTest.this.jps = jps;
			}
			@SuppressWarnings("synthetic-access")
			@Override
			public PrivilegedJanusPropertySetter getPrivilegedJanusPropertySetter() {
				return JanusPropertiesTest.this.jps;
			}
		});
	}
	
	/**
	 */
	@Override
	public void tearDown() throws Exception {
		this.jps = null;
		this.id = null;
		this.properties.reset();
		this.properties = null;
		super.tearDown();
	}

	/**
	 */
	public void testGetContextId() {
		assertEquals(this.id, this.properties.getContextId());
	}

	/** 
	 */
	public static void testGetRootConfigurationDirectory() {
		File janusRoot = FileSystem.getUserConfigurationDirectoryFor("janus"); //$NON-NLS-1$
		assertEquals(janusRoot, JanusProperties.getRootConfigurationDirectory());
	}

	/** 
	 */
	public void testGetApplicationDirectory() {
		File janusRoot = FileSystem.getUserConfigurationDirectoryFor("janus"); //$NON-NLS-1$
		File applicationRoot;
		
		assertEquals(JanusProperties.DEFAULT_APPLICATION_NAME, this.properties.getProperty(JanusProperty.JANUS_APPLICATION_NAME));

		applicationRoot = FileSystem.join(janusRoot, JanusProperties.DEFAULT_APPLICATION_NAME);
		assertEquals(applicationRoot, this.properties.getApplicationDirectory());
		
		String randomName = UUID.randomUUID().toString();
		this.jps.setPrivilegedProperty(JanusProperty.JANUS_APPLICATION_NAME, randomName);

		applicationRoot = FileSystem.join(janusRoot, randomName);
		assertEquals(applicationRoot, this.properties.getApplicationDirectory());
	}

	/** 
	 */
	public void testGetKernelDirectory() {
		File janusRoot = FileSystem.getUserConfigurationDirectoryFor("janus"); //$NON-NLS-1$
		File applicationRoot;
		
		assertEquals(JanusProperties.DEFAULT_APPLICATION_NAME, this.properties.getProperty(JanusProperty.JANUS_APPLICATION_NAME));
		
		applicationRoot = FileSystem.join(janusRoot,
				JanusProperties.DEFAULT_APPLICATION_NAME,
				this.id.toString());
		assertEquals(applicationRoot, this.properties.getKernelDirectory());
		
		String randomName = UUID.randomUUID().toString();
		this.jps.setPrivilegedProperty(JanusProperty.JANUS_APPLICATION_NAME, randomName);

		applicationRoot = FileSystem.join(janusRoot, randomName, this.id.toString());
		assertEquals(applicationRoot, this.properties.getKernelDirectory());
	}

	/**
	 */
	public void testIsSystemPropertySynchronized() {
		assertTrue(this.properties.isSystemPropertySynchronized());
	}
	
	/**
	 */
	public void testSetSystemPropertySynchronized() {
		assertTrue(this.properties.isSystemPropertySynchronized());
		this.properties.setSystemPropertySynchronized(false);
		assertFalse(this.properties.isSystemPropertySynchronized());
		this.properties.setSystemPropertySynchronized(true);
		assertTrue(this.properties.isSystemPropertySynchronized());
	}

	/**
	 */
	public void testPut() {
		assertFalse(this.properties.containsKey("A")); //$NON-NLS-1$
		assertNull(this.properties.put("A", "aaa"));  //$NON-NLS-1$//$NON-NLS-2$
		assertTrue(this.properties.containsKey("A")); //$NON-NLS-1$
		assertEquals("aaa", this.properties.get("A")); //$NON-NLS-1$ //$NON-NLS-2$

		assertTrue(this.properties.containsKey("A")); //$NON-NLS-1$
		assertNotNull(this.properties.put("A", "bbb"));  //$NON-NLS-1$//$NON-NLS-2$
		assertTrue(this.properties.containsKey("A")); //$NON-NLS-1$
		assertEquals("bbb", this.properties.get("A")); //$NON-NLS-1$ //$NON-NLS-2$

		Object orig = this.properties.get(JanusProperty.JANUS_HOME.getPropertyName());
		assertTrue(this.properties.containsKey(JanusProperty.JANUS_HOME.getPropertyName()));
		assertNull(this.properties.put(JanusProperty.JANUS_HOME.getPropertyName(), "aaa"));  //$NON-NLS-1$
		assertTrue(this.properties.containsKey(JanusProperty.JANUS_HOME.getPropertyName()));
		assertEquals(orig, this.properties.get(JanusProperty.JANUS_HOME.getPropertyName()));
	}

	/**
	 */
	public void testGet() {
		assertNull(this.properties.get("A")); //$NON-NLS-1$
		assertEquals(System.getProperty("java.lang.classpath"), this.properties.get("java.lang.classpath")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(System.getenv("PATH"), this.properties.get("PATH")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 */
	public void testRemove() {
		assertNull(this.properties.put("A", "aaa"));  //$NON-NLS-1$//$NON-NLS-2$
		assertTrue(this.properties.containsKey("A")); //$NON-NLS-1$
		this.properties.remove("A"); //$NON-NLS-1$
		assertFalse(this.properties.containsKey("A")); //$NON-NLS-1$

		assertTrue(this.properties.containsKey(JanusProperty.JANUS_HOME.getPropertyName()));
		this.properties.remove(JanusProperty.JANUS_HOME);
		assertTrue(this.properties.containsKey(JanusProperty.JANUS_HOME.getPropertyName()));
	}

	/**
	 */
	public void testEntrySet() {
		Set<Entry<Object,Object>> entries = this.properties.entrySet();
		assertNotNull(entries);

		Entry<Object,Object> entry;
		
		int expectedSize = entries.size();
		int removeQueries = 0;
		
		Iterator<Entry<Object,Object>> iterator = entries.iterator();
		while (iterator.hasNext()) {
			entry = iterator.next();
			if (JanusProperty.isReadOnlyPropertyName(entry.getKey().toString())) {
				iterator.remove();
			}
			else {
				removeQueries++;
				iterator.remove();
			}
		}
		
		expectedSize -= removeQueries;
		assertEquals(expectedSize, entries.size());
	}

	/**
	 */
	public void testKeySet() {
		Set<Object> entries = this.properties.keySet();
		assertNotNull(entries);

		Object entry;
		
		int expectedSize = entries.size();
		int removeQueries = 0;
		
		Iterator<Object> iterator = entries.iterator();
		while (iterator.hasNext()) {
			entry = iterator.next();
			if (JanusProperty.isReadOnlyPropertyName(entry.toString())) {
				iterator.remove();
			}
			else {
				removeQueries++;
				iterator.remove();
			}
		}
		
		expectedSize -= removeQueries;
		assertEquals(expectedSize, entries.size());
	}

	/**
	 */
	public void testValues() {
		Collection<Object> entries = this.properties.values();
		assertNotNull(entries);

		Iterator<Object> iterator = entries.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
		
		assertTrue(entries.size()>0);
	}

	/**
	 */
	public void testIsReadOnly() {
		for(JanusProperty prop : JanusProperty.values()) {
			assertEquals(
					prop.isReadOnly(),
					this.properties.isReadOnly(prop.getPropertyName()));
		}
		assertFalse(this.properties.isReadOnly("A")); //$NON-NLS-1$
		assertFalse(this.properties.isReadOnly("B")); //$NON-NLS-1$
		assertFalse(this.properties.isReadOnly("C")); //$NON-NLS-1$
		assertFalse(this.properties.isReadOnly("D")); //$NON-NLS-1$
		assertFalse(this.properties.isReadOnly("E")); //$NON-NLS-1$
		assertFalse(this.properties.isReadOnly("F")); //$NON-NLS-1$
	}
	
	/**
	 */
	public static void testIsConstantProperty() {
		for(JanusProperty prop : JanusProperty.values()) {
			if (JanusProperties.isConstantProperty(prop))
				assertTrue(prop.getPropertyName(), prop.isReadOnly());
		}
	}

}
