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
package org.janusproject.kernel.network.utils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Resolves class by searching into the OSGi bundles available in the {@link BundleContext}.
 * It also provides a cache of class already found to avoid unnecessary searches.
 * 
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OSGiHelper {

	private Map<String, Class<?>> cache = new HashMap<String, Class<?>>();
	private BundleContext context = null;

	/**
	 * Creates a new {@link OSGiHelper}
	 * 
	 * @param context - the context used to search for the class.
	 */
	public OSGiHelper(BundleContext context) {
		this.context = context;
	}

	/**
	 * Find the specified class in bundles classpaths
	 * 
	 * @param clazz - the name of the desired class
	 * @return the desired class
	 * @throws ClassNotFoundException
	 */
	public Class<?> findClass(String clazz) throws ClassNotFoundException {
		synchronized (this.cache) {
			Class<?> orga = this.cache.get(clazz);
			if (orga == null) {
				String organizationClassPath = clazz.replace('.', '/').concat(".class"); //$NON-NLS-1$
				if (this.context != null) {
					URL classURL = this.context.getBundle().getResource(organizationClassPath);

					if (classURL != null) {
						Bundle[] bs = this.context.getBundles();
						URL entry = null;
						int i = 0;
						while (i < bs.length && orga == null) {// loading a bundle class
							entry = bs[i].getEntry(organizationClassPath);

							if (entry != null) {
								orga = bs[i].loadClass(clazz);
							}
							++i;
						}

						if (orga == null) {// loading a jvm class
							orga = Class.forName(clazz);
						}

					} else {
						orga = Class.forName(clazz);
					}
				} else {
					orga = Class.forName(clazz);
				}

			}
			if (orga != null) {
				this.cache.put(clazz, orga);
			} else {
				throw new ClassNotFoundException(clazz);
			}
			return orga;
		}
	}
}
