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

import java.util.Properties;

/** This class provides Janus configuration properties
 * stored inside {@link Properties}.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PropertiesJanusPropertyProvider extends Properties implements JanusPropertyProvider {

	private static final long serialVersionUID = -3475473965569800710L;

	/**
     * Creates an empty property list with no default values.
	 */
	public PropertiesJanusPropertyProvider() {
		super();
	}
	
	/**
     * Creates an empty property list with the specified defaults.
     *
     * @param   defaults are the defaults.
	 */
	public PropertiesJanusPropertyProvider(Properties defaults) {
		super(defaults);
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean containsProperty(String name) {
		assert(name!=null);
		return containsKey(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnlyProperty(String name) {
		return false;
	}

}