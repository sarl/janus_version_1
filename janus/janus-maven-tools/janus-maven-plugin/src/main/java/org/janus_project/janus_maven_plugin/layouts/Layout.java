/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2011 Janus Core Developers
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
package org.janus_project.janus_maven_plugin.layouts;

/** Abstract implementation of an OSGi layout.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class Layout {

	private String configurationDir = null;
	private String binDir = null;
	private String bundleDir = null;
	private String defaultPlatformConfigFileName = null;
	private String defaultLayoutConfigFileName = null;
	private String cacheDir = null;

	/** Replies the name of the layout.
	 * 
	 * @return the name of the layout.
	 */
	public abstract String getName();
	
	/** Replies the default filename of the configuration file for the application platform.
	 * 
	 * @return the default filename of the configuration file for the application platform.
	 */
	public String getDefaultPlatformConfigFileName() {
		return this.defaultPlatformConfigFileName;
	}

	/** Set the default filename of the configuration file for the application platform.
	 * 
	 * @param defaultPlatformConfigFileName is the default filename.
	 */
	public void setDefaultPlatformConfigFileName(String defaultPlatformConfigFileName) {
		this.defaultPlatformConfigFileName = defaultPlatformConfigFileName;
	}

	/** Replies the default filename of the configuration file for the layout.
	 * 
	 * @return the default filename of the configuration file for the layout.
	 */
	public String getDefaultLayoutConfigFileName() {
		return this.defaultLayoutConfigFileName;
	}

	/** Set the default filename of the configuration file for the layout.
	 * 
	 * @param defaultLayoutConfigFileName is the default filename.
	 */
	public void setDefaultLayoutConfigFileName(String defaultLayoutConfigFileName) {
		this.defaultLayoutConfigFileName = defaultLayoutConfigFileName;
	}

	/** Replies the filename of the configuration directory.
	 * 
	 * @return the filename of the configuration directory.
	 */
	public String getConfigurationDir() {
		return this.configurationDir;
	}

	/** Set the filename of the configuration directory.
	 * 
	 * @param configurationDir is the directory to set
	 */
	public void setConfigurationDir(String configurationDir) {
		this.configurationDir = configurationDir;
	}

	/** Replies filename of the directory where the OSGi layout jar file is located.
	 * @return the filename of the directory where the OSGi layout jar file is located.
	 */
	public String getBinDir() {
		return this.binDir;
	}

	/** Set the filename of the directory where the OSGi layout jar file is located.
	 * @param binDir is the filename of the directory where the OSGi layout jar file is located.
	 */
	public void setBinDir(String binDir) {
		this.binDir = binDir;
	}

	/** Replies filename of the directory where the OSGi bundles are located.
	 * @return the filename of the directory where the OSGi bundles are located.
	 */
	public String getBundleDir() {
		return this.bundleDir;
	}

	/** Set filename of the directory where the OSGi bundles are located.
	 * @param bundleDir is the filename of the directory where the OSGi bundles are located.
	 */
	public void setBundleDir(String bundleDir) {
		this.bundleDir = bundleDir;
	}

	/** Replies the cache directory.
	 * 
	 * @return the cache directory.
	 */
	public String getCacheDir() {
		return this.cacheDir;
	}

	/** Set the cache directory.
	 * 
	 * @param cacheDir is the filename where the OSGi layout put its cache.
	 */
	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}

}
