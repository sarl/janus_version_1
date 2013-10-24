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
import java.io.FilenameFilter;
import java.net.URL;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import org.arakhne.afc.vmutil.FileSystem;


/**
 * File filter that is matching script files.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class ScriptFileFilter extends FileFilter implements FilenameFilter {

	private final String description;
	private final boolean allowDirectories;
	private final List<String> extensions;
	
	/**
	 * @param description is the description of the files.
	 * @param enableDirectories indicates if the accepting function is accepting
	 * the directories or not. 
	 * @param extensions are the extensions associated to the interpreter.
	 */
	public ScriptFileFilter(String description, boolean enableDirectories, List<String> extensions) {
		this.description = description;
		this.allowDirectories = enableDirectories;
		this.extensions = extensions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(File dir, String name) {
		return accept(new File(dir, name));
	}
	
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param   filename is the name to test.
     * @return  <code>true</code> if and only if the name should be
     * included in the file list; <code>false</code> otherwise.
     */
	@Override
    public boolean accept(File filename) {
    	if (this.allowDirectories && filename.isDirectory()) return true;
		return isExtension(filename.getName());
    }
    
    /**
     * Tests if a specified URL should be included in a file list.
     *
     * @param  url is the name to test.
     * @return  <code>true</code> if and only if the name should be
     * included in the file list; <code>false</code> otherwise.
     */
    public boolean accept(URL url) {
    	return isExtension(FileSystem.basename(url));
    }

    /** Test if a specified basename should be included in a file list.
     * 
     * @param basename is the basename to test.
     * @return <code>true</code> if and only if the basename should be
     * included in the file list; <code>false</code> otherwise.
     */
    protected boolean isExtension(String basename) {
   		for(String ext : this.extensions) {
   			if (FileSystem.hasExtension(basename, ext))
   				return true;
    	}
		return false;
    }
	
    /**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.description;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return this.description;
	}
		
}
