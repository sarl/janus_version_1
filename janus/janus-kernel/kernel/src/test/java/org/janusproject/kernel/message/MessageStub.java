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

import org.janusproject.kernel.address.Address;

/**
 * Stub for message interface.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MessageStub extends AbstractContentMessage<Object> {

	private static final long serialVersionUID = -1229004694837770242L;
	
	private final String name;

	/**
	 * @param date
	 */
	public MessageStub(float date) {
		this.creationDate = date;
		this.name = null;
		this.sender = null;
	}
	
	/**
	 * @param date
	 * @param name
	 */
	public MessageStub(float date, String name) {
		this.creationDate = date;
		this.name = name;
		this.sender = null;
	}

	/**
	 * @param date
	 * @param name
	 * @param emitter
	 */
	public MessageStub(float date, String name, Address emitter) {
		this.creationDate = date;
		this.name = name;
		this.sender = emitter;
	}

	@Override
	public String toString() {
		return this.name+":"+Long.toString(Float.valueOf(this.creationDate).longValue()); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getContent() {
		return Float.valueOf(this.creationDate).longValue();
	}
	
}
