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
package org.janusproject.scriptedagent;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.arakhne.afc.vmutil.FileSystem;
import org.arakhne.afc.vmutil.Resources;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class ScriptRepositoryTest extends TestCase {

	private static final URL REMOTE_TEST_URL; 
	
	static {
		try {
			REMOTE_TEST_URL = new URL("http://www.janus-project.org/scriptedAgent"); //$NON-NLS-1$
		}
		catch (MalformedURLException e) {
			throw new Error(e);
		}
	}
	
	private ScriptRepository repository;
	private URL resourceDirectory;
	private File resourceFile;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.resourceDirectory = FileSystem.dirname(Resources.getResource(ScriptRepository.class, "emptyResource.txt")); //$NON-NLS-1$
		this.resourceFile = FileSystem.convertURLToFile(this.resourceDirectory);
		this.repository = new ScriptRepository();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		this.repository = null;
		this.resourceDirectory = null;
		super.tearDown();
	}
	
	/**
	 * @throws Exception
	 */
	public void testIsEmpty() throws Exception {
		assertTrue(this.repository.isEmpty());
		this.repository.addDirectory(this.resourceDirectory);
		assertFalse(this.repository.isEmpty());
		this.repository.clear();
		assertTrue(this.repository.isEmpty());		
	}
	
	/**
	 * @throws Exception
	 */
	public void testClear() throws Exception {
		this.repository.addDirectory(this.resourceDirectory);
		assertFalse(this.repository.isEmpty());
		this.repository.clear();
		assertTrue(this.repository.isEmpty());		
	}
	
	/**
	 * @throws Exception
	 */
	public void testSize() throws Exception {
		assertEquals(0, this.repository.size());
		this.repository.addDirectory(this.resourceDirectory);
		assertEquals(1, this.repository.size());
		this.repository.clear();
		assertEquals(0, this.repository.size());		
	}
	
	private static void assertEquals(Object[] expected, Iterator<?> actual) {
		Collection<Object> col = new ArrayList<Object>();
		col.addAll(Arrays.asList(expected));
		while (actual.hasNext()) {
			Object o = actual.next();
			assertTrue(col.remove(o));
		}
		assertTrue(col.isEmpty());
	}
	
	/**
	 * @throws Exception
	 */
	public void testAddDirectoryFile() throws Exception {
		this.repository.addDirectory(this.resourceFile);
		this.repository.addDirectory(this.resourceFile.getParentFile());
		assertEquals(2, this.repository.size());
		assertEquals(
				new Object[]{
						this.resourceFile,
						this.resourceFile.getParentFile()
				},
				this.repository.getLocalDirectories());
	}

	/**
	 * @throws Exception
	 */
	public void testAddDirectoryURL() throws Exception {
		this.repository.addDirectory(this.resourceDirectory);
		this.repository.addDirectory(FileSystem.dirname(this.resourceDirectory));
		assertEquals(2, this.repository.size());
		assertEquals(new Object[]{this.resourceDirectory,
				FileSystem.dirname(this.resourceDirectory)
				},
				this.repository.getDirectories());
	}

	/**
	 * @throws Exception
	 */
	public void testRemoveDirectoryFile() throws Exception {
		this.repository.addDirectory(this.resourceFile);
		this.repository.addDirectory(this.resourceFile.getParentFile());
		assertEquals(2, this.repository.size());
		this.repository.removeDirectory(this.resourceFile.getParentFile());
		assertEquals(1, this.repository.size());
		this.repository.removeDirectory(this.resourceFile.getParentFile().getParentFile());
		assertEquals(1, this.repository.size());
		this.repository.removeDirectory(this.resourceFile);
		assertEquals(0, this.repository.size());
	}

	/**
	 * @throws Exception
	 */
	public void testRemoveDirectoryURL() throws Exception {
		this.repository.addDirectory(this.resourceDirectory);
		this.repository.addDirectory(FileSystem.dirname(this.resourceDirectory));
		assertEquals(2, this.repository.size());
		this.repository.removeDirectory(FileSystem.dirname(this.resourceDirectory));
		assertEquals(1, this.repository.size());
		this.repository.removeDirectory(FileSystem.dirname(FileSystem.dirname(this.resourceDirectory)));
		assertEquals(1, this.repository.size());
		this.repository.removeDirectory(this.resourceDirectory);
		assertEquals(0, this.repository.size());
	}

	/**
	 * @throws Exception
	 */
	public void testGetLocalDirectoriesScript() throws Exception {
		this.repository.addDirectory(this.resourceFile);
		this.repository.addDirectory(this.resourceFile.getParentFile());
		this.repository.addDirectory(REMOTE_TEST_URL);
		Iterator<File> iterator = this.repository.getLocalDirectories();
		assertEquals(
				new Object[] {
					this.resourceFile,
					this.resourceFile.getParentFile()
				},
				iterator);
	}

	/**
	 * @throws Exception
	 */
	public void testGetLocalDirectoriesScriptFileFilter() throws Exception {
		this.repository.addDirectory(this.resourceFile);
		this.repository.addDirectory(this.resourceFile.getParentFile());
		this.repository.addDirectory(REMOTE_TEST_URL);
		Iterator<File> iterator = this.repository.getLocalDirectories(new Filter());
		assertEquals(
				new Object[] {
					this.resourceFile
				},
				iterator);
	}
	
	/**
	 * @throws Exception
	 */
	public void testGetDirectoriesScript() throws Exception {
		this.repository.addDirectory(this.resourceFile);
		this.repository.addDirectory(this.resourceFile.getParentFile());
		this.repository.addDirectory(REMOTE_TEST_URL);
		Iterator<URL> iterator = this.repository.getDirectories();
		assertEquals(
				new Object[] {
					FileSystem.convertFileToURL(this.resourceFile),
					FileSystem.convertFileToURL(this.resourceFile.getParentFile()),
					REMOTE_TEST_URL
				},
				iterator);
	}

	/**
	 * @throws Exception
	 */
	public void testGetDirectoriesScriptFileFilter() throws Exception {
		this.repository.addDirectory(this.resourceFile);
		this.repository.addDirectory(this.resourceFile.getParentFile());
		this.repository.addDirectory(REMOTE_TEST_URL);
		Iterator<URL> iterator = this.repository.getDirectories(new Filter());
		assertEquals(
				new Object[] {
					FileSystem.convertFileToURL(this.resourceFile),
					REMOTE_TEST_URL
				},
				iterator);
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Filter extends ScriptFileFilter {

		/**
		 */
		public Filter() {
			super(null, false, null);
		}
		
		@Override
		protected boolean isExtension(String basename) {
			return basename.toLowerCase().endsWith("scriptedagent"); //$NON-NLS-1$
		}
		
	}

}
