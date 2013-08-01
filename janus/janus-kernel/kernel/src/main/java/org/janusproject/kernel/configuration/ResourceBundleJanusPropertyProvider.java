/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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

import java.util.Locale;
import java.util.MissingFormatArgumentException;
import java.util.ResourceBundle;

/** This class provides Janus configuration properties
 * stored inside a property file.
 * 
 * @author $Author: sgalland$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ResourceBundleJanusPropertyProvider implements JanusPropertyProvider {

	private final String resourcePath;
	private final ClassLoader classLoader;
	
	/**
	 * @param resourcePath is the path of the resource.
	 * @param classLoader is the class loader which permits to retreive the resource bundle.
	 */
	public ResourceBundleJanusPropertyProvider(String resourcePath, ClassLoader classLoader) {
		assert(resourcePath!=null);
		this.resourcePath = resourcePath;
		this.classLoader = classLoader;
	}

	/**
	 * @param resourcePath is the path of the resource.
	 */
	public ResourceBundleJanusPropertyProvider(String resourcePath) {
		this(resourcePath, null);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public String getProperty(String name) {
		assert(name!=null);
		ResourceBundle bundle;
		try {
			if (this.classLoader!=null)
				bundle = ResourceBundle.getBundle(this.resourcePath, Locale.getDefault(), this.classLoader);
			else
				bundle = ResourceBundle.getBundle(this.resourcePath);
			if (bundle!=null) {
				return bundle.getString(name);
			}
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable _) {
			//
		}
		return null;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public boolean containsProperty(String name) {
		assert(name!=null);
		ResourceBundle bundle;
		try {
			if (this.classLoader!=null)
				bundle = ResourceBundle.getBundle(this.resourcePath, Locale.getDefault(), this.classLoader);
			else
				bundle = ResourceBundle.getBundle(this.resourcePath);
			if (bundle!=null) {
				// Patch by Jeremie
				try {
					bundle.getObject(name);
				}
				catch(MissingFormatArgumentException _) {
					return false;
				}
				return true;
				// End of patch by Jeremie
			}
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable _) {
			//
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isReadOnlyProperty(String name) {
		return containsProperty(name);
	}

}