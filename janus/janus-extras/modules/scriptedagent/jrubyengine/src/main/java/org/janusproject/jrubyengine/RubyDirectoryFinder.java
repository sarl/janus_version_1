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
package org.janusproject.jrubyengine;

import java.io.File;

import org.janusproject.jrubyengine.exceptions.NotANormalizedDirectoryException;

/**
 * This class allows - to check the validity of the folder which should
 * containing scripts - to get some informations about the folder content
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gvinson$
 * @author $Author: rbuecher$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class RubyDirectoryFinder {
		
	private static final String RubyIndexFile = "index.rb"; //$NON-NLS-1$
	
	/**
	 * The folder to check, that should normally contained JRuby script
	 */
	private File directory;

	/**
	 * 
	 * @param a_directoryPath - the path to the directory containing JRuby Scripts
	 * @throws NotANormalizedDirectoryException
	 * @throws NullPointerException
	 */
	public RubyDirectoryFinder(String a_directoryPath) throws NotANormalizedDirectoryException, NullPointerException {
		this.directory = new File(a_directoryPath);
		if (!isDirectoryContainingOnlyRubyFiles()) {
			throw new NotANormalizedDirectoryException();
		}
	}

	// Functions

	/**
	 * Check if all the files in the directory are .rb files
	 * 
	 * @return if all files found were .rb
	 */
	public boolean isDirectoryContainingOnlyRubyFiles() {
		for (File f : this.directory.listFiles())
			if (!f.isDirectory()) {
				if (!f.getName().endsWith(RubyExecutionScriptContext.RubyFileExtension)) {
					return false;
				}
			}
		return true;
	}

	/**
	 * Check if there is a file index.rb
	 * 
	 * @return if the file was found
	 */
	public boolean isDirectoryContainingRubyIndexFile() {
		for (File f : this.directory.listFiles())
			if (f.getName().compareTo(RubyIndexFile) == 0) {
				return true;
			}
		return false;
	}

	/**
	 * Return files in the directory
	 * 
	 * @return all the files in the ruby directory
	 */
	public File[] getFileList() {
		return this.directory.listFiles();
	}
	
	
	// Accessors
	/**
	 * @return the directory
	 */
	public File getDirectory() {
		return this.directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(File directory) {
		this.directory = directory;
	}




}
