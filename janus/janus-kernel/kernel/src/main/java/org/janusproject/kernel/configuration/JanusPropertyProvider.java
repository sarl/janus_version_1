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

/** This interfaces may be implemented by a provider
 * of Janus configuration property.
 * <p>
 * The provider may retreive property values from any source
 * (property file, preferences, variable...).
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JanusPropertyProvider {

	/** Replies the value of the property.
	 * 
	 * @param name is the name of the property.
	 * @return the value of the property or <code>null</code> if undefined.
	 */
	public String getProperty(String name);
	
	/** Replies if the property is defined in this provider.
	 * 
	 * @param name is the name of the property.
	 * @return <code>true</code> if the property is defined,
	 * otherwise <code>false</code>
	 */
	public boolean containsProperty(String name);

	/** Replies if the property is read-only in this provider.
	 * 
	 * @param name is the name of the property.
	 * @return <code>true</code> if the property is read-only,
	 * otherwise <code>false</code>
	 */
	public boolean isReadOnlyProperty(String name);

}