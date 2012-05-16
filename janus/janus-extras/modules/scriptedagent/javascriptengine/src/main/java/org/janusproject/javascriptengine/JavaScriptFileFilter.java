package org.janusproject.javascriptengine;
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


import org.arakhne.vmutil.locale.Locale;
import org.janusproject.scriptedagent.ScriptFileFilter;


/**
 * Annotation to specify the default path to the directory containing JavaScript scripts.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class JavaScriptFileFilter extends ScriptFileFilter {

	/**
	 * File Extension of JavaScript script files
	 */
	public static final String JAVASCRIPT_FILE_EXTENSION = ".js"; //$NON-NLS-1$
	
	/**
	 * Create a file filter that is enabling directories. 
	 */
	public JavaScriptFileFilter() {
		super(true);
	}
	
	/**
	 * @param enableDirectories indicates if the accepting function is accepting
	 * the directories or not. 
	 */
	public JavaScriptFileFilter(boolean enableDirectories) {
		super(enableDirectories);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return Locale.getString("NAME"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isExtension(String basename) {
		return matchExtensions(basename, JAVASCRIPT_FILE_EXTENSION);
	}
	
}
