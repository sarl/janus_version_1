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

/** A message that embbeds a boolean value.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BooleanMessage extends AbstractContentMessage<Boolean> {
	
	private static final long serialVersionUID = 6265340059411364755L;
	
	private final boolean content;
	
	/**
	 * @param o the content of the message.
	 */
	public BooleanMessage(boolean o) {
		this.content = o;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getContent() {
		return Boolean.valueOf(this.content);
	}
	
	/** Replies the boolean value embedded in this message.
	 * 
	 * @return the content of this message.
	 */
	public boolean booleanValue() {
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