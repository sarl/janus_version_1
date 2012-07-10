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
package org.janusproject.kernel.network.jxse.jxta.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;

/**
 * A simple and re-usable example of manipulating JXATA Messages. Included
 * in this tutorial are:
 * <ul>
 * <li>Adding and reading {@code String}, {@code int} and {@code long} with
 * Message elements
 * <li>Adding and reading Java {@code Object} with Message Elements.
 * <li>Adding and reading byte arrays with Message Elements.</li>
 * <li>Adding and reading JXTA Advertisements with Message Elements.</li>
 * <li>Compressing message element content with gzip.</li>
 * </ul>
 * <p>
 * This code was obtained in the JXTA Programming Guide.
 * 
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class MessageUtils {
	
	private final static MimeMediaType GZIP_MEDIA_TYPE = new MimeMediaType(
			"application/gzip").intern(); //$NON-NLS-1$

	/**
	 * Adds a String to a Message as a StringMessageElement
	 * 
	 * @param message
	 *            The message to add to
	 * @param nameSpace
	 *            The namespace of the element to add. a null value assumes
	 *            default namespace.
	 * @param elemName
	 *            Name of the Element.
	 * @param string
	 *            The string to add
	 */
	public static void addStringToMessage(Message message, String nameSpace,
			String elemName, String string) {
		message.addMessageElement(nameSpace, new StringMessageElement(elemName,
				string, null));
	}

	/**
	 * Adds an byte array to a message
	 * 
	 * @param message
	 *            The message to add to
	 * @param nameSpace
	 *            The namespace of the element to add. a null value assumes
	 *            default namespace.
	 * @param elemName
	 *            Name of the Element.
	 * @param data
	 *            the byte array
	 * @param compress
	 *            indicates whether to use GZIP compression
	 * @throws IOException
	 *             if an io error occurs
	 */
	public static void addByteArrayToMessage(Message message, String nameSpace,
			String elemName, byte[] data, boolean compress) throws IOException {
		byte[] buffer = data;
		MimeMediaType mimeType = MimeMediaType.AOS;
		if (compress) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(outStream);
			try {
				gos.write(data, 0, data.length);
				gos.finish();
				buffer = outStream.toByteArray();
			}
			finally {
				gos.close();
				outStream.close();
			}
			mimeType = GZIP_MEDIA_TYPE;
		}
		message.addMessageElement(nameSpace, new ByteArrayMessageElement(
				elemName, mimeType, buffer, null));
	}

	/**
	 * Adds an Object to message within the specified name space and with the
	 * specified element name
	 * 
	 * @param message
	 *            the message to add the object to
	 * @param nameSpace
	 *            the name space to add the object under
	 * @param elemName
	 *            the given element name
	 * @param object
	 *            the object
	 * @throws IOException
	 *             if an io error occurs
	 */
	public static void addObjectToMessage(Message message, String nameSpace,
			String elemName, Object object) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		try {
			oos.writeObject(object);
			addByteArrayToMessage(message, nameSpace, elemName, bos.toByteArray(),
					false);
		}
		finally {
			oos.close();
			bos.close();
		}
	}

	/**
	 * Returns a String from a message
	 * 
	 * @param message
	 *            The message to retrieve from
	 * @param nameSpace
	 *            The namespace of the element to get.
	 * @param elemName
	 *            Name of the Element.
	 * @return The string value or {@code null} if there was no element matching
	 *         the specified name.
	 */
	public static String getStringFromMessage(Message message,
			String nameSpace, String elemName) {
		MessageElement me = message.getMessageElement(nameSpace, elemName);
		if (null != me) {
			return me.toString();
		}
		return null;
	}

	/**
	 * Returns an InputStream for a byte array
	 * 
	 * @param message
	 *            The message to retrieve from
	 * @param nameSpace
	 *            The namespace of the element to get.
	 * @param elemName
	 *            Name of the Element.
	 * @return The {@code InputStream} or {@code null} if the message has no
	 *         such element, String elemName) throws IOException {
	 * @throws IOException
	 *             if an io error occurs
	 */
	public static InputStream getInputStreamFromMessage(Message message,
			String nameSpace, String elemName) throws IOException {
		InputStream result = null;
		MessageElement element = message.getMessageElement(nameSpace, elemName);
		if (null == element) {
			return null;
		}
		if (element.getMimeType().equals(GZIP_MEDIA_TYPE)) {
			result = new GZIPInputStream(element.getStream());
		} else if (element.getMimeType().equals(MimeMediaType.AOS)) {
			result = element.getStream();
		}
		return result;
	}

	/**
	 * Reads a single Java Object from a Message.
	 * 
	 * @param message
	 *            The message containing the object.
	 * @param nameSpace
	 *            The name space of the element containing the object.
	 * @param elemName
	 *            The name of the element containing the object.
	 * @return The Object or {@code null} if the Message contained no such
	 *         element.
	 * @throws IOException
	 *             if an io error occurs
	 * @throws ClassNotFoundException
	 *             if an object could not constructed from the message element
	 */
	public static Object getObjectFromMessage(Message message,
			String nameSpace, String elemName) throws IOException,
			ClassNotFoundException {
		InputStream is = getInputStreamFromMessage(message, nameSpace, elemName);
		if (is!=null) {
			try {
				ObjectInputStream ois = new ObjectInputStream(is);
				try {
					return ois.readObject();
				}
				finally {
					ois.close();
				}
			}
			finally {
				is.close();
			}
		}
		return null;
	}


}
