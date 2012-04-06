/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.probe;

/** A probe value was not found.
 * <p>
 * This class is strongly inspirated by the
 * <code>ProbeValueNotDefinedException</code>
 * class from the
 * <a href="http://www.arakhne.org/tinyMAS/">tinyMAS project</a>.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ProbeValueNotDefinedException extends ProbeException {

	private static final long serialVersionUID = -6000235162845689314L;

	/**
	 * @param valueName is the name of the attribute.
	 */
	public ProbeValueNotDefinedException(String valueName) {
		super(valueName);
	}
	
	/**
	 * @return the name of the attribute for which this exception was generated.
	 */
	public String getValueName() {
		return getMessage();
	}
	
}