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
package org.janusproject.kernel.message;


/**
 * This class defines a message which is containing a single type of data.
 * 
 * @param <T> is the type of the data in the message.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractContentMessage<T>
extends Message {
	
	private static final long serialVersionUID = 4667370212509217901L;
	
	/**
	 * Create a message without embedded information.
	 */
	protected AbstractContentMessage() {
		super();
	}
	
	/** Replies the content of this message.
	 * 
	 * @return the content of this message.
	 */
	public abstract T getContent();

	/** Rpelies the content of this message.
	 * 
	 * @param <C> is the type of the content to reply.
	 * @param type is the type of the content to reply.
	 * @return the content of this message.
	 */
	public <C extends T> C getContent(Class<C> type) {
		Object c = getContent();
		if (c!=null && type.isInstance(c)) {
			return type.cast(c);
		}
		return null;
	}

	/**
	 * Replies if the message content is an instance of the given type.
	 *
	 * @param type
	 * @return <code>true</code> if the content instances of the given type,
	 * otherwise <code>false</code>
	 */
	public boolean containsType(Class<?> type) {
		assert(type!=null);
		Object obj = getContent();
		return obj==null || type.isInstance(obj);
	}
	
}
