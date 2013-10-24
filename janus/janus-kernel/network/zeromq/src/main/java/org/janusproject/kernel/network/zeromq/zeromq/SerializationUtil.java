/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012-2013 Janus Core Developers
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
package org.janusproject.kernel.network.zeromq.zeromq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.arakhne.afc.text.Base64Coder;

/** Utilities for the serialization of Java objects in Base64.
 * 
 * @author $Author: bfeld$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class SerializationUtil {

	/** Serizalize and encode the specified object into Base64. 
	 * 
	 * @param o is the object to seralize.
	 * @return the Base64 representation of the object, or <code>null</code>
	 * if the object is <code>null</code> or cannot be serialized.
	 */
	public static String encode(Object o) {
		if (o==null) return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			try {
				oos.writeObject(o);
			}
			finally {
				oos.close();
			}
		}
		catch(IOException e) {
			return null;
		}
		finally {
			try {
				baos.close();
			}
			catch (IOException e) {
				throw new IOError(e);
			}
		}
		return new String(Base64Coder.encode(baos.toByteArray()));
	}

	/** Unserialize and decode a Java object from the specified Base64 string.
	 * 
	 * @param from is the Base64 to parse.
	 * @return the unserialized object, or <code>null</code>.
	 */
	public static Object decode(String from) {
		if (from==null) return null;
		ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decode(from));
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			try {
				return ois.readObject();
			}
			catch (ClassNotFoundException e) {
				throw new IOException(e);
			}
			finally {
				ois.close();
			}
		}
		catch(IOException e) {
			return null;
		}
		finally {
			try {
				bais.close();
			}
			catch(IOException e) {
				throw new IOError(e);
			}
		}
	}
}
