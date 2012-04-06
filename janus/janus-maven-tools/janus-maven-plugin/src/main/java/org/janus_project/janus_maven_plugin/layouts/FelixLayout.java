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


/**
 * An apache Felix layout.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FelixLayout extends Layout {
	
	/** Name of the layout.
	 */
	public static final String NAME = "felix"; //$NON-NLS-1$

	/**
	 */
	public FelixLayout() {
		super();
		setConfigurationDir("conf"); //$NON-NLS-1$
		setBinDir("bin"); //$NON-NLS-1$
		setBundleDir("bundle"); //$NON-NLS-1$
		setCacheDir("felix-cache"); //$NON-NLS-1$
		setDefaultPlatformConfigFileName("system.properties"); //$NON-NLS-1$
		setDefaultLayoutConfigFileName("config.properties"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return NAME;
	}
	
}
