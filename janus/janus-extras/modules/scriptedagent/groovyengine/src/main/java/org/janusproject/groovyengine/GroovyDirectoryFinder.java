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
package org.janusproject.groovyengine;

import java.io.File;

import org.janusproject.groovyengine.exceptions.NotANormalizedDirectoryException;

/**
 * This class allows - to check the validity of the folder which should
 * containing scripts - to get some informations about the folder content
 * 
 * @author $Author: lcabasson$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class GroovyDirectoryFinder {

	private static final String GROOVY_INDEX_FILE = "index.gy"; //$NON-NLS-1$

	/**
	 * The folder to check, that should normally contained Groovy script
	 */
	private File directory;

	/**
	 * Creates a new directory finder
	 * @param a_directoryPath - the path to the directory containing Groovy Scripts
	 * @throws NotANormalizedDirectoryException
	 * @throws NullPointerException
	 */
	public GroovyDirectoryFinder(String a_directoryPath) throws NotANormalizedDirectoryException, NullPointerException {
		this.directory = new File(a_directoryPath);
		if (!isDirectoryContainingOnlyGroovyFiles()) {
			throw new NotANormalizedDirectoryException();
		}
	}

	// Functions

	/**
	 * Check if all the files in the directory are .gy files
	 * 
	 * @return if all files found were .gy
	 */
	public boolean isDirectoryContainingOnlyGroovyFiles() {
		for (File f : this.directory.listFiles())
			if (!f.isDirectory()) {
				if (!f.getName().endsWith(GroovyExecutionScriptContext.GROOVY_FILE_EXTENSION)) {
					return false;
				}
			}
		return true;
	}

	/**
	 * Check if there is a file index.gy
	 * 
	 * @return if the file was found
	 */
	public boolean isDirectoryContainingGroovyIndexFile() {
		for (File f : this.directory.listFiles())
			if (f.getName().compareTo(GROOVY_INDEX_FILE) == 0) {
				return true;
			}
		return false;
	}

	/**
	 * Return files in the directory
	 * 
	 * @return all the files in the Groovy directory
	 */
	public File[] getFileList() {
		return this.directory.listFiles();
	}


	// Accessors
	/**
	 * Returns the underlying File instance of this directory 
	 * @return the directory
	 */
	public File getDirectory() {
		return this.directory;
	}

	/**
	 * Set the directory where scripts files are located
	 * @param directory the directory to set
	 */
	public void setDirectory(File directory) {
		this.directory = directory;
	}
}
