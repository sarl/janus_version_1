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
package org.janusproject.kernel.network.jxse.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * An Object input reader able to resolve the Class using an {@link OSGiHelper}.
 * If no helper is provided it tries to resolve it using the super class 
 * method {@link ObjectInputStream#resolveClass(java.io.ObjectStreamClass) }
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OSGIHelperObjectInputStream extends ObjectInputStream {
	
	private OSGiHelper helper = null;

	/**
	 * @param is 
	 * @param helper 
	 * @throws IOException
	 */
	public OSGIHelperObjectInputStream(InputStream is, OSGiHelper helper)
			throws IOException {
		super(is);
		this.helper = helper;

	}

	/** {@inheritDoc}
	 */
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc)
			throws IOException, ClassNotFoundException {
		if(this.helper == null)
			return super.resolveClass(desc);
		
		return this.helper.findClass(desc.getName());
	}

}
