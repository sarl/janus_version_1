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

/** A message that embbeds a short value.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ShortMessage extends AbstractContentMessage<Short> {
	
	private static final long serialVersionUID = 1271265154917189296L;
	
	private final short content;
	
	/**
	 * @param o the content of the message.
	 */
	public ShortMessage(short o) {
		this.content = o;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Short getContent() {
		return Short.valueOf(this.content);
	}
	
	/** Replies the value embedded in this message.
	 * 
	 * @return the content of this message.
	 */
	public final short shortValue() {
		return this.content;
	}
	
	/** Replies the value embedded in this message.
	 * 
	 * @return the content of this message.
	 */
	public final int intValue() {
		return this.content;
	}

	/** Replies the value embedded in this message.
	 * 
	 * @return the content of this message.
	 */
	public final byte byteValue() {
		return (byte)this.content;
	}

	/** Replies the value embedded in this message.
	 * 
	 * @return the content of this message.
	 */
	public final long longValue() {
		return this.content;
	}
	
	/** Replies the value embedded in this message.
	 * 
	 * @return the content of this message.
	 */
	public final float floatValue() {
		return this.content;
	}	

	/** Replies the value embedded in this message.
	 * 
	 * @return the content of this message.
	 */
	public final double doubleValue() {
		return this.content;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append('=');
		buf.append(this.content);
		return buf.toString();
	}

}